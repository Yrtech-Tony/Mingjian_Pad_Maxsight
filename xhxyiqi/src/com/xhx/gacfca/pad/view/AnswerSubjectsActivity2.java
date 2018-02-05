package com.xhx.gacfca.pad.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.xhx.gacfca.pad.R;
import com.xhx.gacfca.pad.adapter.InspectionImgAdapter2;
import com.xhx.gacfca.pad.adapter.MultiImageAdapter;
import com.xhx.gacfca.pad.dao.Dao;
import com.xhx.gacfca.pad.utility.NameValueObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class AnswerSubjectsActivity2 extends Activity {

	protected int my_requestCode = 1550;
	Spinner cboProjects;
	EditText txtSubjectCode;
	EditText txtSubjectTypes;
	Button btnAnswerStart;
	Button btnPrevious;
	Button btnNext;
	GridView gridViewImGridView;
	ListView listView_multiImage;
	
	TextView textScore;
	TextView textLostScoreDesc;
	TextView textImplementation;
	
	private EditText txtOrderNO;
	
	private Dao _dao;
	private String _subjectCode;
	private String _shopCode;
	private String _shopName;
	private int _orderNO;
	private String _subjectTypes;
	private InputMethodManager imm;
	private Toast _toast ;
	InspectionImgAdapter2 _sa;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		try{
			super.onCreate(savedInstanceState);
	        setContentView(R.layout.answer_subjects2);
	        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	        
			txtSubjectCode = (EditText)findViewById(R.id.txtSubjectCode);
			btnPrevious = (Button)findViewById(R.id.btnPrevious);
			btnNext = (Button)findViewById(R.id.btnNext);
			cboProjects = (Spinner)findViewById(R.id.cboProjects);
			btnAnswerStart = (Button)findViewById(R.id.btnAnswerStart);
			txtSubjectTypes = (EditText)findViewById(R.id.txtSubjectTypes);
			
			gridViewImGridView = (GridView)findViewById(R.id.gridview_multiImg);
			listView_multiImage = (ListView)findViewById(R.id.list_multiImage);
			
			txtOrderNO = (EditText)findViewById(R.id.edittxtOrderNo);
			
			textScore = (TextView)findViewById(R.id.textScore);
			textLostScoreDesc = (TextView)findViewById(R.id.textLostScoreDesc);
			textLostScoreDesc.setMovementMethod(ScrollingMovementMethod.getInstance()); 
			
			textImplementation = (TextView)findViewById(R.id.textImplementation);
			
			//txtShopName.addTextChangedListener(textWatcher);
	        imm= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	        _dao = new Dao();

	        _shopCode = this.getIntent().getExtras().getString("shopCode");
	        _shopName = this.getIntent().getExtras().getString("shopName");
	        
	        _subjectCode = this.getIntent().getExtras().getString("subjectCode");
	        String orderNO = this.getIntent().getExtras().getString("orderNO");
	        if(orderNO != null){
	        	_orderNO = Integer.parseInt(this.getIntent().getExtras().getString("orderNO"));
	        }
	        else{
	        	_orderNO = 0;
	        }
	        
	        
	        //初始化查询条件及Button
	        initCboProjects();
	        initTxtSubjectTypes();
	        initBtnAnswerStart();
	        initBtnPrevious();
	        initBtnNext();
	        
	        if(_subjectCode !=null && !_subjectCode.equals("") && _orderNO !=0){
	        	txtSubjectCode.setText(_subjectCode);
		        String projectCode = ((NameValueObject)cboProjects.getSelectedItem()).getValue();
				SearchInspectionStandardAndInspectionImg(projectCode,_shopCode,_subjectCode);
	        }
	        else{
	        	AnswerStart();
	        }
		}catch (Exception e) {
			showMsg(this, "异常", e.getMessage());
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		try {
			String projectCode = ((NameValueObject)cboProjects.getSelectedItem()).getValue();
			SearchInspectionStandardAndInspectionImg(projectCode, _shopCode, _subjectCode);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			System.out.println("down");
			if (AnswerSubjectsActivity2.this.getCurrentFocus() != null) {
				if (AnswerSubjectsActivity2.this.getCurrentFocus().getWindowToken() != null) {
					imm.hideSoftInputFromWindow(AnswerSubjectsActivity2.this.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}
		}
		return super.onTouchEvent(event);
	}
//	private TextWatcher textWatcher = new TextWatcher(){
//		public void afterTextChanged(Editable s) {
//			initTxtSubjectTypes();
//		}
//		public void beforeTextChanged(CharSequence s, int start, int count,
//				int after) {
//			
//		}
//		public void onTextChanged(CharSequence s, int start, int before,
//				int count) {
//			
//		}
//	};
	
	private void initBtnAnswerStart()
	{
		btnAnswerStart.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				if (AnswerSubjectsActivity2.this.getCurrentFocus() != null) {
					if (AnswerSubjectsActivity2.this.getCurrentFocus().getWindowToken() != null) {
						imm.hideSoftInputFromWindow(AnswerSubjectsActivity2.this.getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
					}
				}
				AnswerStart();
			}
		});
	}
	private void initLstInspectionStandard()
	{
		try{
			_sa = new InspectionImgAdapter2(this, getInspectionStandardList(), R.layout.listview_inspectionimg_new, new String[] { "txtInspectionStandard" }, new int[] {
							R.id.fileName},getInspectionStandardCheckOptionValues(),textScore,textLostScoreDesc,true,"","");
			listView_multiImage.setAdapter(_sa);
			listView_multiImage.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					//Toast.makeText(AnswerSubjectsActivity2.this, "onItemClick",Toast.LENGTH_SHORT).show();
				}
			});
			//listView_multiImage.setEnabled(false);
			
		}catch(Exception ex)
		{
			showMsg(this, "异常", ex.getMessage());
		}
	}
	private void initLstInspectionImg()
	{
		try{
			String projectCode = ((NameValueObject)cboProjects.getSelectedItem()).getValue();
			MultiImageAdapter sa = new MultiImageAdapter(
					AnswerSubjectsActivity2.this, getInspectionImgData(), R.layout.grid_view_item,
					new String[] { "txtFileName" }, new int[] {
							R.id.textViewFileName},projectCode,_shopCode,_shopName,_subjectCode,gridViewImGridView);
			
			gridViewImGridView.setAdapter(sa);
			//gridViewImGridView.setEnabled(false);
		}catch(Exception ex)
		{
			showMsg(this, "异常", ex.getMessage());
		}
	}
	private void initTxtSubjectTypes()
	{
		try{
			String projectCode = ((NameValueObject)cboProjects.getSelectedItem()).getValue();
        	Cursor cursor = _dao.SearchSubjectTypeCode(projectCode, _shopCode);
        	if(cursor.moveToFirst()){
        		_subjectTypes = cursor.getString(cursor.getColumnIndex("SubjectTypeCodeExam"));
        		txtSubjectTypes.setText(_subjectTypes+" 卷");
        	}
        	else{
        		_subjectTypes = "";
        		txtSubjectTypes.setText("无对应类型");
        	}
		}catch(Exception ex)
		{
			new AlertDialog.Builder(this).setTitle("异常").setMessage(ex.getMessage()) 
            .setPositiveButton("关闭", null).show();
		}
	}
	private void initCboProjects()
	{
		try{
	        ArrayAdapter<NameValueObject> adapter = new ArrayAdapter<NameValueObject>(this,android.R.layout.simple_spinner_item);
	        Cursor cursor= _dao.SearchAllProjects();
	        while(cursor.moveToNext())
	        {
	        	NameValueObject obj = new NameValueObject();
	        	String projectCode = cursor.getString(cursor.getColumnIndex("ProjectCode")); 
	        	String projectName = cursor.getString(cursor.getColumnIndex("ProjectName")); 
	        	obj.setDisplay(projectName);
	        	obj.setValue(projectCode);
	        	adapter.add(obj);
	        }
	        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        cboProjects.setAdapter(adapter);
	        cboProjects.setOnItemSelectedListener(new ItemClickSelectListener(cboProjects));
	        String projectCode = this.getIntent().getExtras().getString("projectCode");
	        if(projectCode!=null && !projectCode.equals("")){
	        	for (int i = 0; i < adapter.getCount(); i++) {
	        		NameValueObject object = adapter.getItem(i);
	        		if(object.getValue().equals(projectCode)){
	        			cboProjects.setSelection(i);
	        			break;
	        		}
				}
	        }
		}catch(Exception ex)
		{
			new AlertDialog.Builder(this).setTitle("异常").setMessage(ex.getMessage()) 
            .setPositiveButton("关闭", null).show();
		}
	}

	private void initBtnPrevious()
	{
		btnPrevious.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				AnswerPrevious();
			}
		});
	}
	private void initBtnNext()
	{
		btnNext.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				AnswerNext();
			}
		});
	}
	
	private void AnswerStart()//得分登记开始
	{
		try{
			String projectCode = ((NameValueObject)cboProjects.getSelectedItem()).getValue();
			String subjectTypeCode = _subjectTypes;
			Cursor cursor = _dao.SearchSubjectStart(projectCode, _shopCode, subjectTypeCode);
			if(cursor.getCount()>0){
				cursor.moveToLast();
				_subjectCode = cursor.getString(cursor.getColumnIndex("SubjectCode"));
				_orderNO = cursor.getInt(cursor.getColumnIndex("OrderNO"));
				txtSubjectCode.setText(_subjectCode);
				txtOrderNO.setText(String.valueOf(_orderNO));
				textImplementation.setText(cursor.getString(cursor.getColumnIndex("Implementation")));
				SearchInspectionStandardAndInspectionImg(projectCode, _shopCode, _subjectCode);
			}
		}catch(Exception ex)
		{
			showMsg(this,"异常",ex.getMessage());
		}
	}

	private void AnswerNext()//下一个得分登记开始
	{
		try{
			AnswerSave();
			String projectCode = ((NameValueObject)cboProjects.getSelectedItem()).getValue();
			String subjectTypeCode = _subjectTypes;
			Cursor cursor = _dao.SearchSubjectNext(projectCode, _orderNO, subjectTypeCode);
			if(cursor.getCount()>0){
				cursor.moveToLast();
				_subjectCode = cursor.getString(cursor.getColumnIndex("SubjectCode"));
				_orderNO = cursor.getInt(cursor.getColumnIndex("OrderNO"));
				txtSubjectCode.setText(_subjectCode);
				txtOrderNO.setText(String.valueOf(_orderNO));
				textImplementation.setText(cursor.getString(cursor.getColumnIndex("Implementation")));
				SearchInspectionStandardAndInspectionImg(projectCode, _shopCode, _subjectCode);
			}
		}catch(Exception ex)
		{
			showMsg(this,"异常",ex.getMessage());
		}
	}
	
	private void AnswerSave()//得分登记保存
	{
		try{
			String score = textScore.getText().toString();
			String remark = textLostScoreDesc.getText().toString();
			String projectCode = ((NameValueObject)cboProjects.getSelectedItem()).getValue();
			//保存Score
			_dao.SaveSubject(projectCode, _shopCode, _subjectCode, score, remark);
			_dao.SaveAnswerLog(projectCode, _shopCode, _subjectCode, score, remark);
			_dao.SaveAnswerDtl(projectCode, _shopCode, _subjectCode, _sa.getInspectionImgCheckOptionValues());
			//_dao.SaveAnswerDtl2(projectCode, shopCode, _subjectCode, inspectionImgadapter.getInspectionImgCheckOptionValues());
			
		}catch(Exception ex)
		{
			showMsg(this,"异常",ex.getMessage());
		}
	}
	
	private void AnswerPrevious()//前一个得分登记开始
	{
		try{
			String projectCode = ((NameValueObject)cboProjects.getSelectedItem()).getValue();
			String subjectTypeCode = _subjectTypes;
			Cursor cursor = _dao.SearchSubjectPrevious(projectCode, _orderNO, subjectTypeCode);
			if(cursor.getCount()>0)
			{
				cursor.moveToLast();
				_subjectCode = cursor.getString(cursor.getColumnIndex("SubjectCode"));
				_orderNO = cursor.getInt(cursor.getColumnIndex("OrderNO"));
				txtSubjectCode.setText(_subjectCode);
				txtOrderNO.setText(String.valueOf(_orderNO));
				textImplementation.setText(cursor.getString(cursor.getColumnIndex("Implementation")));
				SearchInspectionStandardAndInspectionImg(projectCode, _shopCode, _subjectCode);
			}
		}catch(Exception ex)
		{
			showMsg(this,"异常",ex.getMessage());
		}
	}
	
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if (resultCode == RESULT_OK) {
		    switch (requestCode) {
			    case RESULT_OK:
//				    Bundle bunde = data.getExtras();
//				    String shopCode = bunde.getString("shopCode");
//				    String shopName = bunde.getString("shopName");
//				    txtShopCode.setText(shopCode);
//				    txtShopName.setText(shopName);
				    break;
			    case 1200:
			    	
				    break;
			    default:
			    	break;
		    }
	    }
    }
	
	private ArrayList<Map<String, Object>> getInspectionStandardList()
	{
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        
        String projectCode = ((NameValueObject)cboProjects.getSelectedItem()).getValue();
        Cursor cursor = _dao.SearchInspectionStandardList(projectCode, _subjectCode);
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

	private ArrayList<Map<String, Object>> getInspectionImgData()
	{
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        
        String projectCode = ((NameValueObject)cboProjects.getSelectedItem()).getValue();
        Cursor cursor = _dao.SearchInspectionImgList(projectCode, _subjectCode);
        while(cursor.moveToNext())
        {
        	String fileName = cursor.getString(cursor.getColumnIndex("FileName"));
        	String seqNO = cursor.getString(cursor.getColumnIndex("SeqNO"));
        	Map<String, Object> map = new HashMap<String, Object>();
        	map.put("txtFileName", fileName);
        	map.put("SeqNO", seqNO);
            list.add(map);
        }
        
        return list;
	}
	
	private Map<String, String> getInspectionStandardCheckOptionValues(){
		Map<String, String> allValues = new HashMap<String, String>();
		String projectCode = ((NameValueObject)cboProjects.getSelectedItem()).getValue();
		Cursor cursor = _dao.SearchInspectionStandardCheckOptionValues(projectCode, _shopCode, _subjectCode);
		while(cursor.moveToNext())
        {
        	String seqNO = cursor.getString(cursor.getColumnIndex("SeqNO"));
        	String checkOptionCode = cursor.getString(cursor.getColumnIndex("CheckOptionCode"));
        	allValues.put(seqNO, checkOptionCode);
        }
		return allValues;
	}
	
	private void SearchInspectionStandardAndInspectionImg(String projectCode, String shopCode, String subjectCode){
		//Cursor cursor = _dao.SearchScoreAndRemark(projectCode, shopCode, subjectCode);
		//initRdogScore();
		Cursor cursor = _dao.SearchScoreAndRemark(projectCode, shopCode, subjectCode);
		if(cursor.getCount()>0){
			cursor.moveToLast();
			String score = cursor.getString(cursor.getColumnIndex("Score"));
			String remark = cursor.getString(cursor.getColumnIndex("Remark"));
			textScore.setText(score);
			textLostScoreDesc.setText(remark);
		}
		else{
			Cursor cursorScore = _dao.SearchFullScoreANDScoreInspection(projectCode,subjectCode);
        	if(cursorScore.moveToFirst()){
        		String fullScore = cursorScore.getString(cursorScore.getColumnIndex("FullScore"));
        		textScore.setText(fullScore);
        	}else{
        		textScore.setText("1");
        	}
			textLostScoreDesc.setText("");
		}
        initLstInspectionStandard();
        initLstInspectionImg();
	}
	
	private void showMsg(Context context, String title, String message)
	{
		new AlertDialog.Builder(context).setTitle(title).setMessage(message).setPositiveButton("关闭", null).show();
	}

	@SuppressWarnings("unused")
	private void showToast(Context context, String message){
		if(_toast==null){
			_toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
			_toast.setGravity(Gravity.CENTER, 0, 0);
		}
		else{
			_toast.setText(message);
		}
		_toast.show();
	}
	
	private class ItemClickSelectListener implements OnItemSelectedListener 
	{

		public ItemClickSelectListener(Spinner cboProjects) {
			
		}
		
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			//_mCheckOptionValues.put(_cboInspectionResult.getPrompt().toString(), "0"+(position+1));
			initTxtSubjectTypes();
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			
		}
		
	}
}
