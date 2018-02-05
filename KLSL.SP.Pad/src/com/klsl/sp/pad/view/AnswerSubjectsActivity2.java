package com.klsl.sp.pad.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.klsl.sp.pad.adapter.InspectionImgAdapter2;
import com.klsl.sp.pad.adapter.MultiImageAdapter;
import com.klsl.sp.pad.dao.Dao;
import com.klsl.sp.pad.utility.NameValueObject;
import com.klsl.sp.pad.R;

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
import android.widget.CompoundButton.OnCheckedChangeListener;

public class AnswerSubjectsActivity2 extends Activity {

	protected int my_requestCode = 1550;
	protected String unRelate_Score = "9999";

	Spinner cboProjects;
	EditText txtSubjectCode;
	Spinner cboSuoPeiType;
	//EditText txtSubjectTypes;
	//private EditText txtOrderNO;
	Button btnAnswerStart;
	//Button btnPrevious;
	Button btnNext;
	GridView gridViewImGridView;
	ListView listView_multiImage;

	TextView textScore;
	CheckBox chkUnRelate;
	TextView textLostScoreDesc;
//	TextView textImplementation;

	private Dao _dao;
	private String _subjectCode;
	private String _shopCode;
	private String _shopName;
	private int _orderNO;
//	private String _subjectTypes;
	private InputMethodManager imm;
	private Toast _toast;
	private Boolean _unRelate;
	InspectionImgAdapter2 _sa;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.answer_subjects2);
			getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

			txtSubjectCode = (EditText) findViewById(R.id.txtSubjectCode);
			btnNext = (Button) findViewById(R.id.btnNext);
			cboProjects = (Spinner) findViewById(R.id.cboProjects);
			cboSuoPeiType = (Spinner) findViewById(R.id.cboSuoPeiType);
			btnAnswerStart = (Button) findViewById(R.id.btnAnswerStart);

			gridViewImGridView = (GridView) findViewById(R.id.gridview_multiImg);
			listView_multiImage = (ListView) findViewById(R.id.list_multiImage);

			textScore = (TextView) findViewById(R.id.textScore);
			chkUnRelate = (CheckBox) findViewById(R.id.chkUnRelate);
			textLostScoreDesc = (TextView) findViewById(R.id.textLostScoreDesc);
			textLostScoreDesc.setMovementMethod(ScrollingMovementMethod.getInstance());
//			textImplementation = (TextView) findViewById(R.id.textImplementation);
//			textImplementation.setMovementMethod(ScrollingMovementMethod.getInstance());

			imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			_dao = new Dao();

			_shopCode = this.getIntent().getExtras().getString("shopCode");
			_shopName = this.getIntent().getExtras().getString("shopName");

			_subjectCode = this.getIntent().getExtras().getString("subjectCode");
			String orderNO = this.getIntent().getExtras().getString("orderNO");
			if (orderNO != null) {
				_orderNO = Integer.parseInt(this.getIntent().getExtras()
						.getString("orderNO"));
			} else {
				_orderNO = 0;
			}

			// 初始化查询条件及Button
			initCboProjects();
			initCboSuoPeiType();
			initBtnAnswerStart();
			initBtnNext();
			initChkUnRelate();

			if (_subjectCode != null && !_subjectCode.equals("")
					&& _orderNO != 0) {
				txtSubjectCode.setText(_subjectCode);
				String projectCode = ((NameValueObject) cboProjects
						.getSelectedItem()).getValue();
				SearchInspectionStandardAndInspectionImg(projectCode,
						_shopCode, _subjectCode);
			} else {
				AnswerStart();
			}
		} catch (Exception e) {
			showMsg(this, "异常", e.getMessage());
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		try {
			String projectCode = ((NameValueObject) cboProjects
					.getSelectedItem()).getValue();
			SearchInspectionStandardAndInspectionImg(projectCode, _shopCode,
					_subjectCode);
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
				if (AnswerSubjectsActivity2.this.getCurrentFocus()
						.getWindowToken() != null) {
					imm.hideSoftInputFromWindow(AnswerSubjectsActivity2.this
							.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}
		}
		return super.onTouchEvent(event);
	}

	private void initBtnAnswerStart() {
		btnAnswerStart.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (AnswerSubjectsActivity2.this.getCurrentFocus() != null) {
					if (AnswerSubjectsActivity2.this.getCurrentFocus().getWindowToken() != null) {
						imm.hideSoftInputFromWindow(AnswerSubjectsActivity2.this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
					}
				}
				AnswerStart();
			}
		});
	}

	private void initLstInspectionStandard() {
		try {
			_sa = new InspectionImgAdapter2(this, getInspectionStandardList(),
					R.layout.listview_inspectionimg_new,
					new String[] { "txtInspectionStandard" },
					new int[] { R.id.fileName },
					getInspectionStandardCheckOptionValues(), textScore,
					textLostScoreDesc, true, "", "");
			listView_multiImage.setAdapter(_sa);
			listView_multiImage
					.setOnItemClickListener(new OnItemClickListener() {

						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
						}
					});
		} catch (Exception ex) {
			showMsg(this, "异常", ex.getMessage());
		}
	}

	private void initLstInspectionImg() {
		try {
			String projectCode = ((NameValueObject) cboProjects.getSelectedItem()).getValue();
			MultiImageAdapter sa = new MultiImageAdapter(
					AnswerSubjectsActivity2.this, getInspectionImgData(),
					R.layout.grid_view_item, new String[] { "txtFileName" },
					new int[] { R.id.textViewFileName }, projectCode,
					_shopCode, _shopName, _subjectCode, gridViewImGridView);

			gridViewImGridView.setAdapter(sa);
		} catch (Exception ex) {
			showMsg(this, "异常", ex.getMessage());
		}
	}

	private void initCboProjects() {
		try {
			ArrayAdapter<NameValueObject> adapter = new ArrayAdapter<NameValueObject>(
					this, android.R.layout.simple_spinner_item);
			Cursor cursor = _dao.SearchAllProjects();
			while (cursor.moveToNext()) {
				NameValueObject obj = new NameValueObject();
				String projectCode = cursor.getString(cursor
						.getColumnIndex("ProjectCode"));
				String projectName = cursor.getString(cursor
						.getColumnIndex("ProjectName"));
				obj.setDisplay(projectName);
				obj.setValue(projectCode);
				adapter.add(obj);
			}
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			cboProjects.setAdapter(adapter);
			cboProjects.setOnItemSelectedListener(new ItemClickSelectListener(cboProjects));
			String projectCode = this.getIntent().getExtras()
					.getString("projectCode");
			if (projectCode != null && !projectCode.equals("")) {
				for (int i = 0; i < adapter.getCount(); i++) {
					NameValueObject object = adapter.getItem(i);
					if (object.getValue().equals(projectCode)) {
						cboProjects.setSelection(i);
						break;
					}
				}
			}
		} catch (Exception ex) {
			new AlertDialog.Builder(this).setTitle("异常")
					.setMessage(ex.getMessage()).setPositiveButton("关闭", null)
					.show();
		}
	}

	private void initCboSuoPeiType(){
		try {
			ArrayAdapter<NameValueObject> adapter = new ArrayAdapter<NameValueObject>(
					this, android.R.layout.simple_spinner_item);
			Cursor cursor = _dao.SearchAllSuoPeiType();
			while (cursor.moveToNext()) {
				NameValueObject obj = new NameValueObject();
				String spTypeCode = cursor.getString(cursor.getColumnIndex("SPTypeCode"));
				String spTypeName = cursor.getString(cursor.getColumnIndex("SPTypeName"));
				obj.setDisplay(spTypeName);
				obj.setValue(spTypeCode);
				adapter.add(obj);
			}
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			cboSuoPeiType.setAdapter(adapter);
//			cboSuoPeiType.setOnItemSelectedListener(new ItemClickSelectListener(
//					cboSuoPeiType));
//			String projectCode = this.getIntent().getExtras()
//					.getString("projectCode");
//			if (projectCode != null && !projectCode.equals("")) {
//				for (int i = 0; i < adapter.getCount(); i++) {
//					NameValueObject object = adapter.getItem(i);
//					if (object.getValue().equals(projectCode)) {
//						cboProjects.setSelection(i);
//						break;
//					}
//				}
//			}
		} catch (Exception ex) {
			new AlertDialog.Builder(this).setTitle("异常")
					.setMessage(ex.getMessage()).setPositiveButton("关闭", null)
					.show();
		}
	}
	
	private void initChkUnRelate() {
		chkUnRelate.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				_unRelate = arg1;
				if(arg1) textScore.setText("");
			}
		});
	}

	private void initBtnNext() {
		btnNext.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				AnswerNext();
			}
		});
	}

	private void AnswerStart()// 得分登记开始
	{
		try {
			String projectCode = ((NameValueObject) cboProjects.getSelectedItem()).getValue();
			_subjectCode = txtSubjectCode.getText().toString();
			SearchInspectionStandardAndInspectionImg(projectCode, _shopCode, _subjectCode);
		} catch (Exception ex) {
			showMsg(this, "异常", ex.getMessage());
		}
	}

	private void AnswerNext()// 下一个得分登记开始
	{
		try {
			AnswerSave();
			String projectCode = ((NameValueObject) cboProjects.getSelectedItem()).getValue();
			SearchInspectionStandardAndInspectionImg(projectCode, _shopCode, _subjectCode);
		} catch (Exception ex) {
			showMsg(this, "异常", ex.getMessage());
		}
	}

	private void AnswerSave()// 得分登记保存
	{
		try {
			String score = textScore.getText().toString();
			if (_unRelate!=null && _unRelate)
				score = unRelate_Score;
			String remark = textLostScoreDesc.getText().toString();
			String projectCode = ((NameValueObject) cboProjects.getSelectedItem()).getValue();
			// 保存Score
			_dao.SaveSubject(projectCode, _shopCode, _subjectCode, score, remark);
			_dao.SaveAnswerLog(projectCode, _shopCode, _subjectCode, score, remark);
			_dao.SaveAnswerDtl(projectCode, _shopCode, _subjectCode, _sa.getInspectionImgCheckOptionValues());
			// _dao.SaveAnswerDtl2(projectCode, shopCode, _subjectCode,
			// inspectionImgadapter.getInspectionImgCheckOptionValues());

		} catch (Exception ex) {
			showMsg(this, "异常", ex.getMessage());
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case RESULT_OK:
				// Bundle bunde = data.getExtras();
				// String shopCode = bunde.getString("shopCode");
				// String shopName = bunde.getString("shopName");
				// txtShopCode.setText(shopCode);
				// txtShopName.setText(shopName);
				break;
			case 1200:

				break;
			default:
				break;
			}
		}
	}

	private ArrayList<Map<String, Object>> getInspectionStandardList() {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		String projectCode = ((NameValueObject) cboProjects.getSelectedItem()).getValue();
		Cursor cursor = _dao.SearchInspectionStandardList(projectCode);
		while (cursor.moveToNext()) {
			String inspectionStandardName = cursor.getString(cursor
					.getColumnIndex("InspectionStandardName"));
			String inspectionStandardSeqNO = cursor.getString(cursor
					.getColumnIndex("InspectionStandardSeqNO"));
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("txtInspectionStandard", inspectionStandardName);
			map.put("inspectionStandardSeqNO", inspectionStandardSeqNO);
			list.add(map);
		}

		return list;
	}

	private ArrayList<Map<String, Object>> getInspectionImgData() {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		String projectCode = ((NameValueObject) cboProjects.getSelectedItem()).getValue();
		Cursor cursor = _dao.SearchInspectionImgList(projectCode);
		while (cursor.moveToNext()) {
			String fileName = cursor.getString(cursor
					.getColumnIndex("FileName"));
			String seqNO = cursor.getString(cursor.getColumnIndex("SeqNO"));
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("txtFileName", fileName);
			map.put("SeqNO", seqNO);
			list.add(map);
		}

		return list;
	}

	private Map<String, String> getInspectionStandardCheckOptionValues() {
		Map<String, String> allValues = new HashMap<String, String>();
		String projectCode = ((NameValueObject) cboProjects.getSelectedItem())
				.getValue();
		Cursor cursor = _dao.SearchInspectionStandardCheckOptionValues(
				projectCode, _shopCode, _subjectCode);
		while (cursor.moveToNext()) {
			String seqNO = cursor.getString(cursor.getColumnIndex("SeqNO"));
			String checkOptionCode = cursor.getString(cursor
					.getColumnIndex("CheckOptionCode"));
			allValues.put(seqNO, checkOptionCode);
		}
		return allValues;
	}

	private void SearchInspectionStandardAndInspectionImg(String projectCode, String shopCode, String subjectCode) {
		Cursor cursor = _dao.SearchScoreAndRemark(projectCode, shopCode, subjectCode);
		if (cursor.getCount() > 0) {
			cursor.moveToLast();
			String score = cursor.getString(cursor.getColumnIndex("Score"));
			String remark = cursor.getString(cursor.getColumnIndex("Remark"));
			textScore.setText(score);
			textLostScoreDesc.setText(remark);
			if (score.equals(unRelate_Score) || score.equals("")) {
				textScore.setText("");
				chkUnRelate.setChecked(true);
			} else {
				chkUnRelate.setChecked(false);
			}
		} else {
			Cursor cursorScore = _dao.SearchFullScoreANDScoreInspection(
					projectCode, subjectCode);
			if (cursorScore.moveToFirst()) {
				String fullScore = cursorScore.getString(cursorScore
						.getColumnIndex("FullScore"));
				textScore.setText(fullScore);
			} else {
				textScore.setText("1");
			}
			chkUnRelate.setChecked(false);
			textLostScoreDesc.setText("");
		}
		initLstInspectionStandard();
		initLstInspectionImg();
	}

	private void showMsg(Context context, String title, String message) {
		new AlertDialog.Builder(context).setTitle(title).setMessage(message)
				.setPositiveButton("关闭", null).show();
	}

	@SuppressWarnings("unused")
	private void showToast(Context context, String message) {
		if (_toast == null) {
			_toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
			_toast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			_toast.setText(message);
		}
		_toast.show();
	}

	private class ItemClickSelectListener implements OnItemSelectedListener {

		public ItemClickSelectListener(Spinner cboProjects) {

		}

		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			
		}

		public void onNothingSelected(AdapterView<?> arg0) {

		}

	}
}
