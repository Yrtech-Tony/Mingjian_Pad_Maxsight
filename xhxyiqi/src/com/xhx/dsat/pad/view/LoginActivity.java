package com.xhx.dsat.pad.view;

import com.xhx.dsat.pad.R;
import com.xhx.dsat.pad.dao.Dao;
import com.xhx.dsat.pad.utility.SqliteHelper;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity {
	
	private TextView txtErrorinfo;
	private Button btnLogin;
	private Dao _dao;
	private String _userPwd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		SqliteHelper.openDatabase(this);
		_dao = new Dao();
		txtErrorinfo = (TextView) findViewById(R.id.textErrorInfo);
		txtErrorinfo.setVisibility(View.GONE);
		btnLogin = (Button) findViewById(R.id.btnUserLogin);
		btnLogin.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String userName = ((EditText) findViewById(R.id.etUserName)).getText().toString();
				String userPwd = ((EditText) findViewById(R.id.etUserPwd)).getText().toString();
				if(CheckUserInfo(userName))
				{
					if(userPwd.equals(_userPwd))
					{
						txtErrorinfo.setVisibility(View.GONE);
						Intent intent = new Intent();
				        intent.setClass(LoginActivity.this, MainActivity.class);
				        startActivity(intent);
				        LoginActivity.this.finish();
					}else{
						txtErrorinfo.setText(R.string.UserPwdError);
						txtErrorinfo.setVisibility(View.VISIBLE);
					}
				}else{
					txtErrorinfo.setText(R.string.UserNameError);
					txtErrorinfo.setVisibility(View.VISIBLE);
				}
			}
		});
	}
	
	private boolean CheckUserInfo(String userName){
		Cursor cursor = _dao.SearchUserInfo(userName);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			String userID = cursor.getString(cursor.getColumnIndex("UserID"));
			_userPwd = cursor.getString(cursor.getColumnIndex("PSW"));
			SqliteHelper.setCurrentUserID(userID);
			return true;
		}
		return false;
	}
}
