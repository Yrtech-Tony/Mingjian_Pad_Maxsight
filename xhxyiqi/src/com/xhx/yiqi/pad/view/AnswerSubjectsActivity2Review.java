package com.xhx.yiqi.pad.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.xhx.yiqi.pad.R;
import com.xhx.yiqi.pad.adapter.InspectionImgAdapter2;
import com.xhx.yiqi.pad.dao.Dao;
import com.xhx.yiqi.pad.utility.ImageUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

public class AnswerSubjectsActivity2Review extends Activity {
	protected int my_requestCode = 1550;
	Button btnNext;
	ListView listView_multiImage;
	
	TextView textScore;
	TextView textLostScoreDesc;
	
	private Dao _dao;
	private InputMethodManager imm;
	
	private String _projectCode;
	private String _shopCode;
	private String _subjectCode;
	private String _imageUrl;
	Context context;
	InspectionImgAdapter2 _sa;
	
	private String _fullScore;
	private String _scoreInspection;
	
	private int _orderNO;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		try{
			super.onCreate(savedInstanceState);
	        setContentView(R.layout.answer_subjects2_review);
	        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	        
			listView_multiImage = (ListView)findViewById(R.id.list_multiImage);
			
			//setContentView(R.layout.camera);
			Intent intent = getIntent();
			_projectCode = intent.getStringExtra("projectCode");
			_shopCode = intent.getStringExtra("shopCode");
			_subjectCode = intent.getStringExtra("subjectCode");
			_imageUrl = intent.getStringExtra("imagePath");
			
			ImageView imageView1 = (ImageView) findViewById(R.id.imageViewReview);
			Bitmap bitmap = ImageUtil.GetImage(_imageUrl);//ImageUtil.GetImage(_imageUrl, 240, 240)
			if (bitmap == null) {
				imageView1.setImageResource(R.drawable.noimage);
			} else {
				imageView1.setImageBitmap(bitmap);
			}
			
			String orderNO = this.getIntent().getExtras().getString("orderNO");
	        if(orderNO != null){
	        	_orderNO = Integer.parseInt(this.getIntent().getExtras().getString("orderNO"));
	        }
	        else{
	        	_orderNO = 0;
	        }
	        
			textScore = (TextView)findViewById(R.id.textScore);
			textLostScoreDesc = (TextView)findViewById(R.id.textLostScoreDesc);
			textLostScoreDesc.setMovementMethod(ScrollingMovementMethod.getInstance()); 
			
	        imm= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	        _dao = new Dao();
	        
	        btnNext = (Button)findViewById(R.id.btnNext);
	        
	        btnNext.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					AnswerSave();
					//setResult(1200);
					AnswerSubjectsActivity2Review.this.finish();
				}
			});
	        
	        initLstInspectionStandard();
	        
		}catch (Exception e) {
			showMsg(this, "异常", e.getMessage());
		}
	}
	
	private void initLstInspectionStandard()
	{
		try{
			
			Cursor cursor = _dao.SearchFullScoreANDScoreInspection(_projectCode,_subjectCode);
        	if(cursor.moveToFirst()){
        		_fullScore = cursor.getString(cursor.getColumnIndex("FullScore"));
        		_scoreInspection = cursor.getString(cursor.getColumnIndex("ScoreInspection"));
        	}else{
        		_fullScore = "0";
        		_scoreInspection = "0";
        	}
        	textScore.setText(_fullScore);
			_sa = new InspectionImgAdapter2(this, getInspectionStandardList(), R.layout.listview_inspectionimg_new, new String[] { "txtInspectionStandard" }, new int[] {
							R.id.fileName},getInspectionStandardCheckOptionValues(),textScore,textLostScoreDesc,false,_fullScore,_scoreInspection);
			listView_multiImage.setAdapter(_sa);
			listView_multiImage.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					//Toast.makeText(AnswerSubjectsActivity2Review.this, "onItemClick",Toast.LENGTH_SHORT).show();
				}
			});
		}catch(Exception ex)
		{
			showMsg(this, "异常", ex.getMessage());
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			System.out.println("down");
			if (AnswerSubjectsActivity2Review.this.getCurrentFocus() != null) {
				if (AnswerSubjectsActivity2Review.this.getCurrentFocus().getWindowToken() != null) {
					imm.hideSoftInputFromWindow(AnswerSubjectsActivity2Review.this.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}
		}
		return super.onTouchEvent(event);
	}
	
	private ArrayList<Map<String, Object>> getInspectionStandardList()
	{
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        
        Cursor cursor = _dao.SearchInspectionStandardList(_projectCode, _subjectCode);
        while(cursor.moveToNext())
        {
        	String inspectionStandardName = cursor.getString(cursor.getColumnIndex("InspectionStandardName"));
        	String inspectionStandardSeqNO = cursor.getString(cursor.getColumnIndex("InspectionStandardSeqNO"));
        	Map<String, Object> map = new HashMap<String, Object>();
            map.put("txtInspectionStandard", inspectionStandardName);
            map.put("inspectionStandardSeqNO", inspectionStandardSeqNO);
            list.add(map);
        }
        
        return list;
	}
	
	private Map<String, String> getInspectionStandardCheckOptionValues(){
		Map<String, String> allValues = new HashMap<String, String>();
		Cursor cursor = _dao.SearchInspectionStandardCheckOptionValues(_projectCode, _shopCode, _subjectCode);
		while(cursor.moveToNext())
        {
        	String seqNO = cursor.getString(cursor.getColumnIndex("SeqNO"));
        	String checkOptionCode = cursor.getString(cursor.getColumnIndex("CheckOptionCode"));
        	allValues.put(seqNO, checkOptionCode);
        }
		return allValues;
	}
	
	private void AnswerSave()//得分登记保存
	{
		try{
			String score = textScore.getText().toString();
			String remark = textLostScoreDesc.getText().toString();
			//保存Score
			_dao.SaveSubject(_projectCode, _shopCode, _subjectCode, score, remark);
			_dao.SaveAnswerLog(_projectCode, _shopCode, _subjectCode, score, remark);
			_dao.SaveAnswerDtl(_projectCode, _shopCode, _subjectCode, _sa.getInspectionImgCheckOptionValues());
			//_dao.SaveAnswerDtl2(projectCode, shopCode, _subjectCode, inspectionImgadapter.getInspectionImgCheckOptionValues());
			
		}catch(Exception ex)
		{
			showMsg(this,"异常",ex.getMessage());
		}
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    switch (resultCode) {
		    case RESULT_OK:
			    break;
		    default:
		    	break;
	    }
    }
	
	private void showMsg(Context context, String title, String message)
	{
		new AlertDialog.Builder(context).setTitle(title).setMessage(message).setPositiveButton("关闭", null).show();
	}

	
}
