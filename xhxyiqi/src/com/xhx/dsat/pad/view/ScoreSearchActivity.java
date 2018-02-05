package com.xhx.dsat.pad.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.xhx.dsat.pad.R;
import com.xhx.dsat.pad.adapter.SearchScoreSimpleAdapter;
import com.xhx.dsat.pad.dao.Dao;
import com.xhx.dsat.pad.utility.CustomDict;
import com.xhx.dsat.pad.utility.SqliteHelper;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ScoreSearchActivity extends Activity {
	private Spinner cboProject;
	private Dao _dao;
	private EditText txtShopCode;
	private EditText txtShopName;
	private Button btnShop;
	protected int my_requestCode = 1550;
	
	@TargetApi(15)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scoresearch);
		
		_dao = new Dao();
		cboProject = (Spinner) findViewById(R.id.cboProject);

		List<CustomDict> prodicts = getProjectList();
		ArrayAdapter<CustomDict> proadapter = new ArrayAdapter<CustomDict>(
				this, android.R.layout.simple_spinner_item, prodicts);
		proadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		cboProject.setAdapter(proadapter);
		
		initBtnShop();
		
		// 设置列头
		RelativeLayout mHead = (RelativeLayout) findViewById(R.id.head);
		mHead.setFocusable(true);
		mHead.setClickable(true);
		//mHead.setBackgroundColor(Color.parseColor("#1788D8"));

		// 设置粗体
		TextView scoringCriteriaT = (TextView) findViewById(R.id.scoringCriteriaTitle);
		TextView scoreElementsT = (TextView) findViewById(R.id.scoreElementsTitle);
		TextView listImageT = (TextView) findViewById(R.id.listImageTitle);
		TextView implementationT = (TextView) findViewById(R.id.implementationTitle);
		scoringCriteriaT.getPaint().setFakeBoldText(true);
		scoreElementsT.getPaint().setFakeBoldText(true);
		listImageT.getPaint().setFakeBoldText(true);
		implementationT.getPaint().setFakeBoldText(true);
		
		//设置查询按钮
		Button btnSearch = (Button) this.findViewById(R.id.btnSearch);
		btnSearch.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				searchScoreList(((CustomDict) cboProject.getSelectedItem())
						.getValue(), txtShopCode.getText().toString(),null);
			}
		});
		
		InitColSort();
		
		searchScoreList(((CustomDict) cboProject.getSelectedItem()).getValue(), txtShopCode.getText().toString(),null);

		if (VERSION.SDK_INT  >= 15) { 
			((TextView)findViewById(R.id.orderNOHead)).callOnClick();
		} 

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
	
	private void InitColSort() {
		final TextView subjectCodeHead = (TextView) findViewById(R.id.subjectCodeHead);
		final TextView orderNOHead = (TextView) findViewById(R.id.orderNOHead);
		final TextView checkPointHead = (TextView) findViewById(R.id.checkPointHead);
		final TextView ScoreHead = (TextView) findViewById(R.id.ScoreHead);

		subjectCodeHead.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				String text = (String) subjectCodeHead.getText();
				Boolean type = true;
				if(text.contains("▲")){
					type = false;
					text += "▼";
					text = text.replace("▲","");
					subjectCodeHead.setText(text);
					orderNOHead.setText(R.string.colOrderNO);
					checkPointHead.setText(R.string.colCheckPoint);
					ScoreHead.setText(R.string.colScore);
				}else{
					text += "▲";
					text = text.replace("▼","");
					subjectCodeHead.setText(text);
					orderNOHead.setText(R.string.colOrderNO);
					checkPointHead.setText(R.string.colCheckPoint);
					ScoreHead.setText(R.string.colScore);
				}
				SearchScoreListSortByCol("a.SubjectCode",type);
			}
		});
		orderNOHead.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				String text = (String) orderNOHead.getText();
				Boolean type = true;
				if(text.contains("▲")){
					type = false;
					text += "▼";
					text = text.replace("▲","");
					orderNOHead.setText(text);
					subjectCodeHead.setText(R.string.colSubjectCode);
					checkPointHead.setText(R.string.colCheckPoint);
					ScoreHead.setText(R.string.colScore);
				}else{
					text += "▲";
					text = text.replace("▼","");
					orderNOHead.setText(text);
					subjectCodeHead.setText(R.string.colSubjectCode);
					checkPointHead.setText(R.string.colCheckPoint);
					ScoreHead.setText(R.string.colScore);
				}
				SearchScoreListSortByCol("a.OrderNO",type);
			}
		});
		checkPointHead.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				String text = (String) checkPointHead.getText();
				Boolean type = true;
				if(text.contains("▲")){
					type = false;
					text += "▼";
					text = text.replace("▲","");
					checkPointHead.setText(text);
					subjectCodeHead.setText(R.string.colSubjectCode);
					orderNOHead.setText(R.string.colOrderNO);
					ScoreHead.setText(R.string.colScore);
				}else{
					text += "▲";
					text = text.replace("▼","");
					checkPointHead.setText(text);
					subjectCodeHead.setText(R.string.colSubjectCode);
					orderNOHead.setText(R.string.colOrderNO);
					ScoreHead.setText(R.string.colScore);
				}
				SearchScoreListSortByCol("a.CheckPoint",type);
			}
		});
		ScoreHead.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				String text = (String) ScoreHead.getText();
				Boolean type = true;
				if(text.contains("▲")){
					type = false;
					text += "▼";
					text = text.replace("▲","");
					ScoreHead.setText(text);
					subjectCodeHead.setText(R.string.colSubjectCode);
					checkPointHead.setText(R.string.colCheckPoint);
					orderNOHead.setText(R.string.colOrderNO);
				}else{
					text += "▲";
					text = text.replace("▼","");
					ScoreHead.setText(text);
					subjectCodeHead.setText(R.string.colSubjectCode);
					checkPointHead.setText(R.string.colCheckPoint);
					orderNOHead.setText(R.string.colOrderNO);
				}
				SearchScoreListSortByCol("b.Score",type);
			}
		});
	}
	
	private void SearchScoreListSortByCol(String col,Boolean type){
		Cursor cursor = _dao.SearchScoreListSortByCol(((CustomDict) cboProject.getSelectedItem()).getValue(), txtShopCode.getText().toString(), col, type ? "DESC" : "ASC");
		searchScoreList(((CustomDict) cboProject.getSelectedItem()).getValue(), txtShopCode.getText().toString(),cursor);
	}
	
	private void initBtnShop()
	{
		btnShop = (Button)findViewById(R.id.btnShop);
		txtShopCode = (EditText)findViewById(R.id.txtShopCode);
		txtShopName = (EditText)findViewById(R.id.txtShopName);
		
		txtShopCode.setText(this.getIntent().getExtras().getString("shopCode"));
        txtShopName.setText(this.getIntent().getExtras().getString("shopName"));
		
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
					        intent.setClass(ScoreSearchActivity.this, SearchShopActivity.class);
					        Bundle bundle = new Bundle();
					        bundle.putString("shopCode", shopCode);
					        intent.putExtras(bundle);
					        startActivityForResult(intent,my_requestCode);
						}
					}
				}catch(Exception ex)
				{
					showMsg(arg0.getContext(), "initBtnShop异常", ex.getMessage());
				}
			}
		});
	}
	
	private List<CustomDict> getProjectList() {
		List<CustomDict> dicts = new ArrayList<CustomDict>();
		try {
			Cursor cursor = _dao.SearchAllProjects();
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				do {
					dicts.add(new CustomDict(cursor.getString(cursor
							.getColumnIndex("ProjectCode")), cursor
							.getString(cursor.getColumnIndex("ProjectName"))));
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			showMsg(this, "getProjectList异常", e.getMessage());
		}
		return dicts;
	}

	private void searchScoreList(String projectCode, String shopCode,Cursor cursor) {
		try {
			if(cursor == null){
				cursor = _dao.SearchScoreList(projectCode,shopCode);
			}
			String hideValue = projectCode +"&;"+shopCode+"&;"+  txtShopName.getText();
			if (cursor.getCount() > 0) {
				
				final ListView listScoreInfo = (ListView) this.findViewById(R.id.listScoreInfo);
				
				List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

				cursor.moveToFirst();
				int i = 1;
				do {
					HashMap<String, String> row = new HashMap<String, String>();
					row.put("colNO", String.valueOf(i));
					row.put("SubjectCode",
							cursor.getString(cursor.getColumnIndex("SubjectCode")));
					row.put("OrderNO",
							cursor.getString(cursor.getColumnIndex("OrderNO")));
					row.put("CheckPoint",
							cursor.getString(cursor.getColumnIndex("CheckPoint")));
					if (cursor.getString(cursor.getColumnIndex("Score")) == null) {
						row.put("Score", "");
					} else {
						row.put("Score",
								cursor.getString(cursor.getColumnIndex("Score")));
					}
					String picChk = "N";
					if(cursor.getColumnIndex("PicChk") != -1)
					if(cursor.getString(cursor.getColumnIndex("PicChk")) != null) {
						picChk = cursor.getString(cursor.getColumnIndex("PicChk"));
					}
					row.put("HidenValue",hideValue + "&;" + picChk);
					data.add(row);
					i++;
				} while (cursor.moveToNext());

				final SearchScoreSimpleAdapter sa = new SearchScoreSimpleAdapter(ScoreSearchActivity.this, data,
						R.layout.scoresearchrow, new String[] { "colNO",
								"SubjectCode", "OrderNO", "CheckPoint", "Score","HidenValue" },
						new int[] { R.id.colNO, R.id.subjectCode, R.id.orderNO,
								R.id.checkPoint, R.id.Score,R.id.hidenValue });

				listScoreInfo.setAdapter(sa);
				
				//设置ListView中按钮的点击事件
				listScoreInfo.setOnItemClickListener(new OnItemClickListener() {
					@SuppressWarnings("unchecked")
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
							long arg3) {
						// TODO Auto-generated method stub
						//获得选中项的HashMap对象
						sa.setSelectItem(arg2);
						sa.notifyDataSetInvalidated();
						HashMap<String,String> map=(HashMap<String,String>)listScoreInfo.getItemAtPosition(arg2);        
						String subjectCode=map.get("SubjectCode"); 
						getDetail(((CustomDict) cboProject.getSelectedItem())
								.getValue(), subjectCode);
					}
				});
			}
		} catch (Exception e) {
			showMsg(this, "searchScoreList异常", e.getMessage());
		}
	}

	private void getDetail(String projectCode, String subjectCode) {
		try {
			Cursor cursor = _dao.SearchDetail(projectCode, subjectCode);
			
			TextView scoringCriteria = (TextView) findViewById(R.id.scoringCriteria);
			TextView scoreElements = (TextView) findViewById(R.id.scoreElements);
			TextView implementation = (TextView) findViewById(R.id.implementation);
			scoringCriteria.setMovementMethod(ScrollingMovementMethod.getInstance());
			scoreElements.setMovementMethod(ScrollingMovementMethod.getInstance());
			implementation.setMovementMethod(ScrollingMovementMethod.getInstance());
			
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				scoringCriteria.setText(cursor.getString(cursor
						.getColumnIndex("Desc")));
				scoreElements.setText(cursor.getString(cursor
						.getColumnIndex("InspectionDesc")));
				implementation.setText(cursor.getString(cursor
						.getColumnIndex("Implementation")));
				getImageList(projectCode, subjectCode);
			}
			else
			{
				showToast(this,"未查询到相关信息");
				scoringCriteria.setText("");
				scoreElements.setText("");
				implementation.setText("");
				ListView listview = (ListView) findViewById(R.id.listImage);
				SimpleAdapter sa = new SimpleAdapter(ScoreSearchActivity.this, new ArrayList<HashMap<String, String>>(),
						R.layout.listimagerow, new String[] { "FileName"},
						new int[] { R.id.fileName});
				listview.setAdapter(sa);
				listview.setEnabled(false);
			}
		} catch (Exception e) {
			showMsg(this, "getDetail异常", e.getMessage());
		}
	}

	private void getImageList(String projectCode, String subjectCode) {
		try {
			Cursor cursor = _dao.SearchImageList(projectCode, subjectCode);
			if (cursor.getCount() > 0) {
				ListView listview = (ListView) findViewById(R.id.listImage);
				List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
				
				cursor.moveToFirst();
				do {
					HashMap<String, String> row = new HashMap<String, String>();
					row.put("FileName", cursor.getString(cursor.getColumnIndex("FileName")));
					data.add(row);
				} while (cursor.moveToNext());

				SimpleAdapter sa = new SimpleAdapter(ScoreSearchActivity.this, data,
						R.layout.listimagerow, new String[] { "FileName"},
						new int[] { R.id.fileName});
				listview.setAdapter(sa);
				listview.setEnabled(false);
			}
		} catch (Exception e) {
			showMsg(this, "getImageList异常", e.getMessage());
		}
	}
	
	private void showMsg(Context context, String title, String message)
	{
		new AlertDialog.Builder(context).setTitle(title).setMessage(message).setPositiveButton("关闭", null).show();
	}
	
	private Toast _toast ;
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
}
