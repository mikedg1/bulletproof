/*
Copyright 2013 Michael DiGiovanni glass@mikedg.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.mikedg.android.glass.lock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextSwitcher;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Iterator;

public class MainActivity extends Activity implements GestureDetector.OnGestureListener {
    public static final String PREF_LOCKED = "locked"; // Whether we should be locked or not

    private enum EntryCode {
        SINGLE_TAP, LONG_PRESS, FLING_LEFT, FLING_RIGHT
    }

    private static final EntryCode[] CODE = { EntryCode.FLING_LEFT, EntryCode.FLING_LEFT,
            EntryCode.SINGLE_TAP, EntryCode.SINGLE_TAP };

    private static final EntryCode[] GUEST_CODE = { EntryCode.SINGLE_TAP, EntryCode.SINGLE_TAP, EntryCode.SINGLE_TAP };

    private static final float THRESHOLD = 3000;

    private GestureDetector gestureDetector;

    CircularQueue<EntryCode> queue = new CircularQueue<EntryCode>(CODE.length);
    CircularQueue<EntryCode> guestQueue = new CircularQueue<EntryCode>(GUEST_CODE.length);

    private TextSwitcher textSwitcher;

    public static void startLockScreen(Context context) {
        Intent i = new Intent(context, MainActivity.class);
        // FIXME: I'm fairly sure that I butchered these flags
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textSwitcher = (TextSwitcher) findViewById(R.id.textSwitcher1);
        
        startLockService();

        gestureDetector = new GestureDetector(this, this);
        
        setGuestText();        
    }

    private void setGuestText() {

        TextView tv = (TextView)findViewById(R.id.forGuestMode_textView);
        StringBuilder guestCode = new StringBuilder();

        Iterator<EntryCode> it =Arrays.asList(GUEST_CODE).iterator();
        while (it.hasNext()) {
            guestCode.append(getEventString(it.next()));
            if (!it.hasNext()) {
              break;                  
            }
            guestCode.append(", ");
        }
        tv.setText(String.format(getString(R.string.FOR_GUEST_MODE_TEXT), guestCode.toString()));
    }

    private boolean isCorrectCode() {
        return (Arrays.equals(CODE, queue.toArray()));
    }

    private boolean isGuestCode() {
        return (Arrays.equals(GUEST_CODE, guestQueue.toArray()));
    }

    private void startLockService() {
        Intent i = new Intent(this, LockService.class);
        startService(i);
    }

    @Override
    public void onBackPressed() {
        // Ignore it
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }

    private void unlock() {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(PREF_LOCKED, false)
                .commit();
        finish();
    }

    @Override
    public boolean onDown(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d("Lock", "onFling: velx:" + velocityX + "   vely:" + velocityY);
        // Fling up seems doable but hard 04-30 07:59:35.940: D/Lock(12950): onFling: velx:483.72153
        // vely:-2940.9343
        // Fling down I can't get
        // Fling left 04-30 08:00:05.276: D/Lock(12950): onFling: velx:12000.0 vely:538.85223
        // fling right04-30 08:00:05.276: D/Lock(12950): onFling: velx:-12000.0 vely:538.85223
        // Basic check for left right flings and ignore the hard ones
        if (velocityX < -THRESHOLD) {
            addAndCheckForUnlock(EntryCode.FLING_RIGHT);
        } else if (velocityX > THRESHOLD) {
            addAndCheckForUnlock(EntryCode.FLING_LEFT);
        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.d("Lock", "onLongPress");
        addAndCheckForUnlock(EntryCode.LONG_PRESS);
    }

    private void addAndCheckForUnlock(EntryCode event) {
        String eventString = getEventString(event);
        
        textSwitcher.setText(eventString);
        
        queue.add(event);
        guestQueue.add(event);
        if (isCorrectCode()) {
            unlock();
        } else if (isGuestCode()) {
            guestUnloxcked(); 
        }
    }

    private String getEventString(EntryCode event) {
        
        switch(event) {
            case FLING_LEFT:
                return "Forward";
            case LONG_PRESS:
                return "Long Press";
            case FLING_RIGHT:
                return "Back";
            case SINGLE_TAP:
                return "Tap";
        }
        return "unknown";
    }

    private void guestUnloxcked() {
        Intent localIntent = new Intent(GlassHelper.ACTION_GUEST_MODE);
        localIntent.putExtra(GlassHelper.EXTRA_GUEST_MODE_TOGGLE_TIME,
                System.currentTimeMillis());
        localIntent.putExtra(GlassHelper.EXTRA_GUEST_MODE_ENABLED, true);
        sendStickyBroadcast(localIntent);

        finish();
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.d("Lock", "onScroll");
        //Happens many times, need to filter out things and determine direction
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.d("Lock", "onShowPress");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d("Lock", "onSingleTapUp");
        addAndCheckForUnlock(EntryCode.SINGLE_TAP);
        return true;
    }
}
