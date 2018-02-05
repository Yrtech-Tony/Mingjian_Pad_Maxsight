package com.klsl.sp.pad.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.klsl.sp.pad.adapter.InspectionImgAdapter;
import com.klsl.sp.pad.adapter.InspectionStandardAdapter;
import com.klsl.sp.pad.dao.Dao;
import com.klsl.sp.pad.utility.NameValueObject;
import com.klsl.sp.pad.utility.SqliteHelper;
import com.klsl.sp.pad.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;

public class AnswerSubjectsActivity extends Activity {
	protected int my_requestCode = 1550;
	TextView lblBefore;
	TextView lblCurrent;
	TextView lblAfter;
	Spinner cboProjects;
	EditText txtShopCode;
	EditText txtShopName;
	Button btnShop;
	EditText txtSubjectTypes;
	ListView lstInspectionStandard;
	ListView lstInspectionImg;
	EditText txtScore;
	EditText txtLastScore;
	EditText txtRemark;
	Button btnAnswerStart;
	Button btnScoreSearch;
	Button btnPrevious;
	Button btnNext;
	RadioGroup rdogScore;
	private Dao _dao;
	private String _subjectCode;
	private int _orderNO;
	private String _subjectTypes;
	private InputMethodManager imm;
	InspectionStandardAdapter inspectionStandardAdapter;
	InspectionImgAdapter inspectionImgadapter;
	private Toast _toast ;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		try{
			super.onCreate(savedInstanceState);
	        setContentView(R.layout.answersubjects);
	        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	        
	        lblBefore = (TextView)findViewById(R.id.textViewBefore);
	        lblCurrent = (TextView)findViewById(R.id.textViewCurrent);
	        lblAfter = (TextView)findViewById(R.id.textViewAfter);
	        btnShop = (Button)findViewById(R.id.btnShop);
			txtShopCode = (EditText)findViewById(R.id.txtShopCode);
			txtShopName = (EditText)findViewById(R.id.txtShopName);
			btnPrevious = (Button)findViewById(R.id.btnPrevious);
			txtScore = (EditText)findViewById(R.id.txtCurrentScore);
			txtLastScore = (EditText)findViewById(R.id.txtLastScore);
			txtRemark = (EditText)findViewById(R.id.txtRemark);
			btnScoreSearch = (Button)findViewById(R.id.btnScoreSearch);
			btnNext = (Button)findViewById(R.id.btnNext);
			cboProjects = (Spinner)findViewById(R.id.cboProjects);
			txtSubjectTypes = (EditText)findViewById(R.id.txtSubjectTypes);
			lstInspectionImg = (ListView)findViewById(R.id.lstInspectionImg);
			lstInspectionStandard = (ListView)findViewById(R.id.lstInspectionStandard);
			btnAnswerStart = (Button)findViewById(R.id.btnAnswerStart);
			rdogScore = (RadioGroup)findViewById(R.id.rdogScore);
			
			txtShopName.addTextChangedListener(textWatcher);
	        imm= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	        _dao = new Dao();

	        //初始化查询条件及Button
	        initCboProjects();
	        initBtnShop();
	        //initTxtSubjectTypes();
	        initBtnAnswerStart();
	        initBtnScoreSearch();
	        initBtnPrevious();
	        initBtnNext();
	        txtShopCode.setText(this.getIntent().getExtras().getString("shopCode"));
	        txtShopName.setText(this.getIntent().getExtras().getString("shopName"));
	        
	        _subjectCode = this.getIntent().getExtras().getString("subjectCode");
	        String orderNO = this.getIntent().getExtras().getString("orderNO");
	        if(orderNO != null){
	        	_orderNO = Integer.parseInt(this.getIntent().getExtras().getString("orderNO"));
	        }
	        else{
	        	_orderNO = 0;
	        }
	        if(_subjectCode !=null && !_subjectCode.equals("") && _orderNO !=0){
		        showToast(this, _subjectCode); setSubjectTitle(_subjectCode);
		        String projectCode = ((NameValueObject)cboProjects.getSelectedItem()).getValue();
				String shopCode = txtShopCode.getText().toString();
				SearchInspectionStandardAndInspectionImg(projectCode,shopCode,_subjectCode);
	        }
	        else{
	        	AnswerStart();
	        }
		}catch (Exception e) {
			showMsg(this, "异常", e.getMessage());
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			System.out.println("down");
			if (AnswerSubjectsActivity.this.getCurrentFocus() != null) {
				if (AnswerSubjectsActivity.this.getCurrentFocus().getWindowToken() != null) {
					imm.hideSoftInputFromWindow(AnswerSubjectsActivity.this.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}
		}
		return super.onTouchEvent(event);
	}
	private TextWatcher textWatcher = new TextWatcher(){
		public void afterTextChanged(Editable s) {
			initTxtSubjectTypes();
		}
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			
		}
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			
		}
	};
	
	private void initBtnAnswerStart()
	{
		btnAnswerStart.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				if (AnswerSubjectsActivity.this.getCurrentFocus() != null) {
					if (AnswerSubjectsActivity.this.getCurrentFocus().getWindowToken() != null) {
						imm.hideSoftInputFromWindow(AnswerSubjectsActivity.this.getCurrentFocus().getWindowToken(),
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
			String projectCode = ((NameValueObject)cboProjects.getSelectedItem()).getValue();
			String shopCode = txtShopCode.getText().toString();
			String shopName = txtShopName.getText().toString();
			inspectionStandardAdapter = new InspectionStandardAdapter(this, getInspectionStandardList(), getInspectionStandardCheckOptionValues(),projectCode,shopCode,shopName,_subjectCode);
			lstInspectionStandard.setAdapter(inspectionStandardAdapter);
			setListViewHeightBasedOnChildren(lstInspectionStandard);
			lstInspectionStandard.setEnabled(false);
		}catch(Exception ex)
		{
			showMsg(this, "异常", ex.getMessage());
		}
	}
	private void initLstInspectionImg()
	{
		try{
			String projectCode = ((NameValueObject)cboProjects.getSelectedItem()).getValue();
			String shopCode = txtShopCode.getText().toString();
			String shopName = txtShopName.getText().toString();
			inspectionImgadapter = new InspectionImgAdapter(this,getInspectionImgData(),getInspectionImgCheckOptionValues(),projectCode,shopCode,shopName,_subjectCode);
			lstInspectionImg.setAdapter(inspectionImgadapter);
			setListViewHeightBasedOnChildren(lstInspectionImg);
			lstInspectionImg.setEnabled(false);
		}catch(Exception ex)
		{
			showMsg(this, "异常", ex.getMessage());
		}
	}
	private void initTxtSubjectTypes()
	{
		try{
			String projectCode = ((NameValueObject)cboProjects.getSelectedItem()).getValue();
			String shopCode = txtShopCode.getText().toString();
        	Cursor cursor = _dao.SearchSubjectTypeCode(projectCode, shopCode);
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
	private void initBtnShop()
	{
		btnShop.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View arg0) {
				try{
					String shopCode = txtShopCode.getText().toString();
					Cursor cursor= SqliteHelper.execSQL_OnReadOnlyDB_SELECT("SELECT ShopCode, ShopName FROM Shop WHERE UseChk=1 AND ShopCode LIKE ?", new String[]{"%"+shopCode+"%"});
					if(cursor.getCount()>0)
					{
						if(cursor.getCount()==1)
						{
							cursor.moveToFirst();
							txtShopName.setText(cursor.getString(cursor.getColumnIndex("ShopName")));
						}
						else
						{
							Intent intent = new Intent();/* new 一个Intent 对象，并 指定class */
					        intent.setClass(AnswerSubjectsActivity.this, SearchShopActivity.class);
					        Bundle bundle = new Bundle();
					        bundle.putString("shopCode", shopCode);
					        intent.putExtras(bundle);
					        startActivityForResult(intent,my_requestCode);
						}
					}
				}catch(Exception ex)
				{
					showMsg(arg0.getContext(), "异常", ex.getMessage());
				}
			}
		});
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
				AnswerSave();
				AnswerNext();
			}
		});
	}
	private void initRdogScore(){
//		String projectCode = ((NameValueObject)cboProjects.getSelectedItem()).getValue();
//		Cursor cursor = _dao.SearchScoreSet(projectCode, _subjectCode);
//		while(cursor.moveToNext()){
//			String score = cursor.getString(cursor.getColumnIndex("Score"));
//			RadioButton radioButton = new RadioButton(this);
//			radioButton.setText(score);
//			rdogScore.addView(radioButton);
//		}
		
		rdogScore.removeAllViews();
		
		RadioButton radioButton = new RadioButton(this);
		radioButton.setText("0");
		rdogScore.addView(radioButton);
		
		radioButton = new RadioButton(this);
		radioButton.setText("0.5");
		rdogScore.addView(radioButton);
		
		radioButton = new RadioButton(this);
		radioButton.setText("1");
		rdogScore.addView(radioButton);
		
		radioButton = new RadioButton(this);
		radioButton.setText("1.5");
		rdogScore.addView(radioButton);
		
		radioButton = new RadioButton(this);
		radioButton.setText("2");
		rdogScore.addView(radioButton);
		
		rdogScore.setOnCheckedChangeListener(onRadioChanged);
	}
	private void initBtnScoreSearch(){
		btnScoreSearch.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
			    Intent intent = new Intent();
			    intent.setClass(AnswerSubjectsActivity.this, ScoreSearchActivity.class);
			    Bundle bundle = new Bundle();
			    String shopCode = txtShopCode.getText().toString();
			    String shopName = txtShopName.getText().toString();
		        bundle.putString("shopCode", shopCode);
		        bundle.putString("shopName", shopName);
		        intent.putExtras(bundle);
		        startActivity(intent);
			}
		});
	}
	
	private void AnswerStart()//得分登记开始
	{
		try{
			String projectCode = ((NameValueObject)cboProjects.getSelectedItem()).getValue();
			String shopCode = txtShopCode.getText().toString();
			String subjectTypeCode = _subjectTypes;
			Cursor cursor = _dao.SearchSubjectStart(projectCode, shopCode, subjectTypeCode);
			if(cursor.getCount()>0){
				cursor.moveToLast();
				_subjectCode = cursor.getString(cursor.getColumnIndex("SubjectCode"));
				_orderNO = cursor.getInt(cursor.getColumnIndex("OrderNO"));
				showToast(this, _subjectCode); setSubjectTitle(_subjectCode);
				SearchInspectionStandardAndInspectionImg(projectCode, shopCode, _subjectCode);
			}
		}catch(Exception ex)
		{
			showMsg(this,"异常",ex.getMessage());
		}
	}
	private void AnswerSave()//得分登记保存
	{
		try{
			String projectCode = ((NameValueObject)cboProjects.getSelectedItem()).getValue();
			String shopCode = txtShopCode.getText().toString();
			String score = txtScore.getText().toString();
			String remark = txtRemark.getText().toString();
			//保存Score
			_dao.SaveSubject(projectCode, shopCode, _subjectCode, score, remark);
			_dao.SaveAnswerLog(projectCode, shopCode, _subjectCode, score, remark);
			_dao.SaveAnswerDtl(projectCode, shopCode, _subjectCode, inspectionStandardAdapter.getInspectionStandardCheckOptionValues());
			_dao.SaveAnswerDtl2(projectCode, shopCode, _subjectCode, inspectionImgadapter.getInspectionImgCheckOptionValues());
			
		}catch(Exception ex)
		{
			showMsg(this,"异常",ex.getMessage());
		}
	}
	private void AnswerNext()//下一个得分登记开始
	{
		try{
			String projectCode = ((NameValueObject)cboProjects.getSelectedItem()).getValue();
			String subjectTypeCode = _subjectTypes;
			Cursor cursor = _dao.SearchSubjectNext(projectCode, _orderNO, subjectTypeCode);
			if(cursor.getCount()>0){
				cursor.moveToLast();
				_subjectCode = cursor.getString(cursor.getColumnIndex("SubjectCode"));
				_orderNO = cursor.getInt(cursor.getColumnIndex("OrderNO"));
				showToast(this, _subjectCode); setSubjectTitle(_subjectCode);
				String shopCode = txtShopCode.getText().toString();
				SearchInspectionStandardAndInspectionImg(projectCode, shopCode, _subjectCode);
			}
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
				showToast(this, _subjectCode); setSubjectTitle(_subjectCode);
				String shopCode = txtShopCode.getText().toString();
				SearchInspectionStandardAndInspectionImg(projectCode, shopCode, _subjectCode);
			}
		}catch(Exception ex)
		{
			showMsg(this,"异常",ex.getMessage());
		}
	}
	
	private static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + 1;
		listView.setLayoutParams(params);
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    switch (resultCode) {
		    case RESULT_OK:
			    Bundle bunde = data.getExtras();
			    String shopCode = bunde.getString("shopCode");
			    String shopName = bunde.getString("shopName");
			    txtShopCode.setText(shopCode);
			    txtShopName.setText(shopName);
			    break;
		    default:
		    	break;
	    }
    }
	
	private ArrayList<Map<String, Object>> getInspectionStandardList()
	{
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        
        String projectCode = ((NameValueObject)cboProjects.getSelectedItem()).getValue();
        Cursor cursor = _dao.SearchInspectionStandardList(projectCode);
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
		String projectCode = ((NameValueObject)cboProjects.getSelectedItem()).getValue();
		String shopCode = txtShopCode.getText().toString();
		Cursor cursor = _dao.SearchInspectionStandardCheckOptionValues(projectCode, shopCode, _subjectCode);
		while(cursor.moveToNext())
        {
        	String seqNO = cursor.getString(cursor.getColumnIndex("SeqNO"));
        	String checkOptionCode = cursor.getString(cursor.getColumnIndex("CheckOptionCode"));
        	allValues.put(seqNO, checkOptionCode);
        }
		return allValues;
	}
	
	private ArrayList<Map<String, Object>> getInspectionImgData()
	{
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        
        String projectCode = ((NameValueObject)cboProjects.getSelectedItem()).getValue();
        Cursor cursor = _dao.SearchInspectionImgList(projectCode);
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

	private Map<String, String> getInspectionImgCheckOptionValues(){
		Map<String, String> allValues = new HashMap<String, String>();
		String projectCode = ((NameValueObject)cboProjects.getSelectedItem()).getValue();
		String shopCode = txtShopCode.getText().toString();
		Cursor cursor = _dao.SerachInspectionImgCheckOptionValues(projectCode, shopCode, _subjectCode);
		while(cursor.moveToNext())
        {
        	String seqNO = cursor.getString(cursor.getColumnIndex("SeqNO"));
        	String checkOptionCode = cursor.getString(cursor.getColumnIndex("CheckOptionCode"));
        	allValues.put(seqNO, checkOptionCode);
        }
		return allValues;
	}
	
	private void SearchInspectionStandardAndInspectionImg(String projectCode, String shopCode, String subjectCode){
		Cursor cursor = _dao.SearchScoreAndRemark(projectCode, shopCode, subjectCode);
		if(cursor.getCount()>0){
			cursor.moveToLast();
			String score = cursor.getString(cursor.getColumnIndex("Score"));
			String remark = cursor.getString(cursor.getColumnIndex("Remark"));
			txtScore.setText(score);
			txtRemark.setText(remark);
		}
		else{
			txtScore.setText("");
		}
		
		cursor = _dao.SearchLastScore(projectCode, shopCode, subjectCode);
		if(cursor.getCount()>0){
			cursor.moveToLast();
			String score = cursor.getString(cursor.getColumnIndex("Score"));
			txtLastScore.setText(score);
		}
		else{
			txtLastScore.setText("");
		}
		
		initRdogScore();
        initLstInspectionStandard();
        initLstInspectionImg();
	}
	
	private void showMsg(Context context, String title, String message)
	{
		new AlertDialog.Builder(context).setTitle(title).setMessage(message).setPositiveButton("关闭", null).show();
	}

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
	
	private void setSubjectTitle(String currentSubjectCode)
	{
		String projectCode = ((NameValueObject)cboProjects.getSelectedItem()).getValue();
		String subjectTypeCode = _subjectTypes;
		
		Cursor cursor = _dao.SearchSubjectCurrent(projectCode, currentSubjectCode);
		if(cursor.getCount()>0){
			cursor.moveToLast();
			String subjectCode = cursor.getString(cursor.getColumnIndex("SubjectCode"));
			String checkPoint = cursor.getString(cursor.getColumnIndex("CheckPoint"));
			lblCurrent.setText(subjectCode+"\r\n"+checkPoint);
		}
		
		cursor = _dao.SearchSubjectNext(projectCode, _orderNO, subjectTypeCode);
		if(cursor.getCount()>0){
			cursor.moveToLast();
			String subjectCode = cursor.getString(cursor.getColumnIndex("SubjectCode"));
			String checkPoint = cursor.getString(cursor.getColumnIndex("CheckPoint"));
			lblAfter.setText(subjectCode+"\r\n"+checkPoint);
		}
		
		cursor = _dao.SearchSubjectPrevious(projectCode, _orderNO, subjectTypeCode);
		if(cursor.getCount()>0)
		{
			cursor.moveToLast();
			String subjectCode = cursor.getString(cursor.getColumnIndex("SubjectCode"));
			String checkPoint = cursor.getString(cursor.getColumnIndex("CheckPoint"));
			lblBefore.setText(subjectCode+"\r\n"+checkPoint);
		}
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
	
	private RadioGroup.OnCheckedChangeListener onRadioChanged = new RadioGroup.OnCheckedChangeListener(){ 
	    public void onCheckedChanged(RadioGroup group, int checkedId){ 
	    	RadioButton radioButton = (RadioButton)findViewById(checkedId);
	    	txtScore.setText(radioButton.getText());       
	    }
	}; 
}
