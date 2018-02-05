package com.klsl.sp.pad.view;

import java.util.ArrayList;
import java.util.HashMap;

import com.klsl.sp.pad.dao.Dao;
import com.klsl.sp.pad.utility.SqliteHelper;
import com.klsl.sp.pad.utility.WebServiceUtil;
import com.klsl.sp.pad.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends Activity {
	ImageButton btnAnswerSubjects;
	ImageButton btnShopScoreSearch;
	
	private ProgressDialog progressDialog = null;
	
	private static final int ANSWER_SUBJECTS = 1000; 
	private static final int SCORE_SEARCH = 1001;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        progressDialog = new ProgressDialog(MainActivity.this);
		progressDialog.setTitle("请稍等...");
		progressDialog.setMessage("同步数据中...");
		progressDialog.setCancelable(false);
        
        SqliteHelper.openDatabase(this);
        
        btnAnswerSubjects = (ImageButton)findViewById(R.id.btnAnswerSubjects);
        btnShopScoreSearch = (ImageButton)findViewById(R.id.btnShopScoreSearch);
        btnAnswerSubjects.setOnClickListener(new ImageButton.OnClickListener(){
        	public void onClick(View view)
            {
        		//调用SearchShop页面
        		Intent intent = new Intent();/* new 一个Intent 对象，并 指定class */
		        intent.setClass(MainActivity.this, SearchShopActivity.class);
		        Bundle bundle = new Bundle();
		        bundle.putString("shopCode", "");
		        intent.putExtras(bundle);
		        startActivityForResult(intent,ANSWER_SUBJECTS);
            }
        });
        btnShopScoreSearch.setOnClickListener(new ImageButton.OnClickListener(){
        	public void onClick(View view)
            {
        		//调用ScoreSearch页面
        		Intent intent = new Intent();/* new 一个Intent 对象，并 指定class */
		        intent.setClass(MainActivity.this, SearchShopActivity.class);
		        Bundle bundle = new Bundle();
		        bundle.putString("shopCode", "");
		        intent.putExtras(bundle);
		        startActivityForResult(intent,SCORE_SEARCH);
            }
        });
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    switch (resultCode) {
		    case RESULT_OK:
			    Bundle bunde = data.getExtras();
			    String shopCode = bunde.getString("shopCode");
			    String shopName = bunde.getString("shopName");
        		
			    Intent intent = new Intent();/* new 一个Intent 对象，并 指定class */
			    
			    switch (requestCode) {
					case ANSWER_SUBJECTS:
						//intent.setClass(MainActivity.this, AnswerSubjectsActivity.class);
						intent.setClass(MainActivity.this, AnswerSubjectsActivity2.class);
						break;
					case SCORE_SEARCH:
						intent.setClass(MainActivity.this, ScoreSearchActivity.class);
						break;
				}
		        
		        Bundle bundle = new Bundle();
		        bundle.putString("shopCode", shopCode);
		        bundle.putString("shopName", shopName);
		        intent.putExtras(bundle);
		        startActivity(intent);
			    break;
		    default:
		    	break;
	    }
    }
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	SqliteHelper.closeDatabase();
    	super.onDestroy();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.menu_syncdata:
			SyncDataTask task1 = new SyncDataTask();
			task1.execute("syncdata");
			break;
		case R.id.menu_syncdataWS:
			SyncDataTask task2 = new SyncDataTask();
			task2.execute("syncdataWS");
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
    
    private class SyncDataTask extends AsyncTask<String, Integer, Boolean> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog.show();
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				Dao dao = new Dao();
				if(params.length > 0 && params[0].equals("syncdata"))
				{
					dao.SyncData();
	    		}else{
	    			dao.SyncDataForWebService();
	    		}
			} catch (Exception e) {
				return false;
			}
    		return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			if(progressDialog != null && progressDialog.isShowing()){
				progressDialog.dismiss();
			}
			if(result){
				Toast.makeText(MainActivity.this, "同步完毕", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(MainActivity.this, "同步失败", Toast.LENGTH_SHORT).show();
			}
			}
	
		
    }
}