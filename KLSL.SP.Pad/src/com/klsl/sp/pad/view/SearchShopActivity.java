package com.klsl.sp.pad.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.klsl.sp.pad.dao.Dao;
import com.klsl.sp.pad.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SearchShopActivity extends Activity {
	EditText txtShopCode;
	EditText txtShopName;
	Button btnSearch;
	ListView lstShop;
	
	private Intent intent;
	private Dao _dao;
	private InputMethodManager imm;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchshop);
        _dao = new Dao();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        imm= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        
        String shopCode = "";
        try{
	        intent = this.getIntent();
	        Bundle bunde = this.getIntent().getExtras();
			shopCode = bunde.getString("shopCode");
		}catch(Exception ex)
		{
			showMsg(this, "异常", ex.getMessage());
		}
        
        txtShopCode = (EditText)findViewById(R.id.txtShopCode);
        txtShopName = (EditText)findViewById(R.id.txtShopName);
        btnSearch = (Button)findViewById(R.id.btnSearch);
        lstShop= (ListView)findViewById(R.id.lstShop);
        
        if(shopCode!=null)
        {
        	txtShopCode.setText(shopCode);
        }
        
        initBtnShop();
        selectedRow();

        SimpleAdapter adapter = new SimpleAdapter(this,getShopData(),R.layout.listview_shop,
                new String[]{"txtShopCode","txtShopName"},
                new int[]{R.id.txtShopCode,R.id.txtShopName});
		lstShop.setAdapter(adapter);
    }
    
    private void initBtnShop()
    {
    	btnSearch.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				try{
					if (SearchShopActivity.this.getCurrentFocus() != null) {
						if (SearchShopActivity.this.getCurrentFocus().getWindowToken() != null) {
							imm.hideSoftInputFromWindow(SearchShopActivity.this.getCurrentFocus().getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);
						}
					}
					SimpleAdapter adapter = new SimpleAdapter(v.getContext(),getShopData(),R.layout.listview_shop,
			                new String[]{"txtShopCode","txtShopName"},
			                new int[]{R.id.txtShopCode,R.id.txtShopName});
					lstShop.setAdapter(adapter);
				}catch(Exception ex)
				{
					new AlertDialog.Builder(v.getContext()).setTitle("异常").setMessage(ex.getMessage()).setPositiveButton("关闭", null).show();
				}
			}
		});
    }
    
    private void selectedRow()
    {
    	lstShop.setOnItemClickListener(new OnItemClickListener() {
  	      	public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
  	      		@SuppressWarnings("unchecked")
				HashMap<String, String> shop =(HashMap<String, String>)lstShop.getItemAtPosition(myItemInt);
  	      		String shopCode = (String)shop.get("txtShopCode");
  	      		String shopName = (String)shop.get("txtShopName");
  	      		
	  	      	Bundle resultBundle = new Bundle();
				resultBundle.putString("shopCode", shopCode);
				resultBundle.putString("shopName", shopName);
				intent.putExtras(resultBundle);
				
				/* 返回result 回上一个activity */
				SearchShopActivity.this.setResult(RESULT_OK, intent);
				/* 结束这个activity */
				SearchShopActivity.this.finish();
  	        }                 
  	  	});
    }
    
    private ArrayList<Map<String, Object>> getShopData()
    {
    	ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    	try{
	    	String shopCode = txtShopCode.getText().toString();
	    	String shopName = txtShopName.getText().toString();
	        Cursor cursor = _dao.SearchShop(shopCode, shopName);
	        while(cursor.moveToNext())
	        {
	        	shopCode = cursor.getString(cursor.getColumnIndex("ShopCode"));
	        	shopName = cursor.getString(cursor.getColumnIndex("ShopName"));
	        	Map<String, Object> map = new HashMap<String, Object>();
	            map.put("txtShopCode", shopCode);
	            map.put("txtShopName", shopName);
	            list.add(map);
	        }
    	}catch(Exception ex)
		{
    		showMsg(this, "GetData异常", ex.getMessage());
		}
    	
    	return list;
    }
    
    private void showMsg(Context context, String title, String message)
	{
		new AlertDialog.Builder(context).setTitle(title).setMessage(message).setPositiveButton("关闭", null).show();
	}
}