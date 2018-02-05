package com.klsl.sp.pad.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ApplicationContext {
	private static SharedPreferences sharedPrefs;
	
	public static void initContext(Context context) {
		if(null != context){
		    ApplicationContext.sharedPrefs = context.getSharedPreferences("KLSL_SP_preferences", Context.MODE_PRIVATE);
		}
	}
	
	public static void setKeyValue(String key, String value){
		Editor editor = sharedPrefs.edit();
		editor.putString(key, value);
		editor.commit();
	}
	public static String getValueByKey(String key){
		return sharedPrefs.getString(key, "");
	}
}
