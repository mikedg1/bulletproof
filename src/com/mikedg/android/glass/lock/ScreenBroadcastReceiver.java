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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

public class ScreenBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            //If it's locked bring the lock screen up
            if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(MainActivity.PREF_LOCKED, true)) {
                bringMainActivityToFront(context);
            }
        } else if (intent.getAction().equals("com.google.glass.action.DON_STATE")) {
            handleDonStateChanged(context, intent);
        } else if (intent.getAction().equals("com.google.glass.action.LONG_TAP")) {
            //Prevents long press from trigger Google Search... should only prevent if we are
            if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(MainActivity.PREF_LOCKED, true)) {
                //FIXME: if here, make sure we communicate a long press to the main app! maybe a localbroadcast?
                abortBroadcast();
            }
        } else if (intent.getAction().equals(GlassHelper.ACTION_GUEST_MODE)) {
            //If we are turning guest mode off then lock
            if (!intent.getBooleanExtra(GlassHelper.EXTRA_GUEST_MODE_ENABLED, false)) {
                //We just turned guest mode off
                PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(MainActivity.PREF_LOCKED, true)
                .commit();
                bringMainActivityToFront(context);
            } else {
                PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(MainActivity.PREF_LOCKED, false)
                .commit();
                //We just turned guest mode on
            }
            //Can I see who sent this? abort it?
        }
    }

    private void handleDonStateChanged(Context context, Intent intent) {
        if (!intent.getExtras().getBoolean("is_donned")) {
            ///If we take it off, then lock the device
            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(MainActivity.PREF_LOCKED, true).commit();
        }
    }

    private void bringMainActivityToFront(Context context) {
        MainActivity.startLockScreen(context);
    }
}
