package com.xhx.dsat.pad.utility;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

public class CustomGallery extends Gallery {

	public CustomGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		int keyCode;
        if (e2.getX() > e1.getX()) {        
            keyCode = KeyEvent.KEYCODE_DPAD_LEFT;  
        } else {  
            keyCode = KeyEvent.KEYCODE_DPAD_RIGHT;  
        }  
        onKeyDown(keyCode, null);
        return false;  
	}
	
}
