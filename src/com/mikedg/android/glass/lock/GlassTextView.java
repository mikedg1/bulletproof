package com.mikedg.android.glass.lock;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class GlassTextView extends TextView {
    private Typeface sRobotoLight;

    public GlassTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public GlassTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GlassTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (sRobotoLight == null && !isInEditMode()) {
            sRobotoLight = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Light.ttf");
        }
        if (sRobotoLight != null) {
            this.setTypeface(sRobotoLight);
        }
    }
}
