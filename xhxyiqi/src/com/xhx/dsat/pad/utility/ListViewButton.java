package com.xhx.dsat.pad.utility;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Button;

public class ListViewButton extends Button {
	private static final boolean INTERCEPT_PRESS_TRUE_STATE = Build.VERSION.SDK_INT < 16;
    public ListViewButton(Context context) {
        super(context);
    }
    public ListViewButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public ListViewButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    public void setPressed(boolean pressed) {
        if (INTERCEPT_PRESS_TRUE_STATE && pressed) {
            return;
        }
        super.setPressed(pressed);
    }

}
