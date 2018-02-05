package com.xhx.yiqi.pad.utility;

import java.io.*;

import com.xhx.yiqi.pad.R;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

public class SqliteHelper {
	private static final int BUFFER_SIZE = 400000;
	public static final String DB_NAME_ReadOnly = "readonly.db"; //��������ݿ��ļ���
	public static final String DB_NAME_Writeable = "writeable.db"; //��������ݿ��ļ���
	public static final String PACKAGE_NAME = "com.xhx.pad";
	public static final String DB_PATH = Environment.getExternalStorageDirectory() + "/XHX_YIQI_Data";
			//"/data"+ Environment.getDataDirectory().getAbsolutePath() + "/"+ PACKAGE_NAME; //���ֻ��������ݿ��λ��
	 
	private static SQLiteDatabase _databaseReadOnly;
	private static SQLiteDatabase _databaseWriteable;
	private static Context _context;
	
	private static String _CurrentUserID = "sysadmin";
	 
	public static void openDatabase(Context context) {
		_context = context;
		_databaseReadOnly = openDatabase(DB_PATH + "/" + DB_NAME_ReadOnly, R.raw.xhx_pad_readonly);
		_databaseWriteable = openDatabase(DB_PATH + "/" + DB_NAME_Writeable, R.raw.xhx_pad_writeable);
	}
	
	public static void closeDatabase(){
		if(_databaseReadOnly.isOpen()){
			_databaseReadOnly.close();
		}
		if(_databaseWriteable.isOpen()){
			_databaseWriteable.close();
		}
	}
	
	public static void execSQL_OnReadOnlyDB_NOTSELECT(String sql, String[] args)
	{
		_databaseReadOnly.execSQL(sql, args);
	}
	public static Cursor execSQL_OnReadOnlyDB_SELECT(String sql, String[] args)
	{
		return _databaseReadOnly.rawQuery(sql, args); 
	}
	
	public static void execSQL_OnWriteableDB_NOTSELECT(String sql, String[] args)
	{
		_databaseWriteable.execSQL(sql, args);
	}
	public static Cursor execSQL_OnWriteableDB_SELECT(String sql, String[] args)
	{
		return _databaseWriteable.rawQuery(sql, args); 
	}
	
	private static SQLiteDatabase openDatabase(String dbfile, int dbId) {
		try {
			 if (!(new File(dbfile).exists())) //�ж����ݿ��ļ��Ƿ���ڣ�����������ִ�е��룬����ֱ�Ӵ����ݿ�
			 {
				 InputStream is = _context.getResources().openRawResource(dbId); //����������ݿ�
				 FileOutputStream fos = new FileOutputStream(dbfile);
				 byte[] buffer = new byte[BUFFER_SIZE];
				 int count = 0;
				 
				 while ((count = is.read(buffer)) > 0) {
					 fos.write(buffer, 0, count); 
				 }
				 fos.close();
				 is.close();
			} 
			SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile,null);
			return db; 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) { 
			e.printStackTrace(); 
		} 
		return null; 
	}

	public static void setCurrentUserID(String currentUserID){
		_CurrentUserID = currentUserID;
	}
	public static String getCurrentUserID(){
		return _CurrentUserID;
	}
	
	public static void beginTransaction_OnReadOnlyDB(){
		_databaseReadOnly.beginTransaction();
	}
	public static void setTransactionSuccessful_OnReadOnlyDB(){
		_databaseReadOnly.setTransactionSuccessful();
	}
	public static void endTransaction_OnReadOnlyDB(){
		_databaseReadOnly.endTransaction();
	}
	
}
