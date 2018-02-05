package com.xhx.yiqi.pad.adapter;

import java.util.ArrayList;
import java.util.Map;

import com.xhx.yiqi.pad.R;
import com.xhx.yiqi.pad.dao.Dao;
import com.xhx.yiqi.pad.utility.ListViewButton;
import com.xhx.yiqi.pad.view.CameraActivity;
import com.xhx.yiqi.pad.view.ShowMoreImageActivity;
import com.xhx.yiqi.pad.view.ThumbnailActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class InspectionStandardAdapter extends BaseAdapter {
	
	private ArrayList<Map<String, Object>> _mInspectionStandardData;
	private LayoutInflater _mInflater;
	private Map<String, String> _mCheckOptionValues;
	private String _projectCode;
	private String _shopCode;
	private String _shopName;
	private String _subjectCode;
	
	public InspectionStandardAdapter(Context context, 
									 ArrayList<Map<String, Object>> mInspectionStandardData, 
									 Map<String, String> mCheckOptionValues,
									 String projectCode,
									 String shopCode,
									 String shopName,
									 String subjectCode){
		_mInflater = LayoutInflater.from(context);
        _mInspectionStandardData = mInspectionStandardData;
        _mCheckOptionValues = mCheckOptionValues;
        _projectCode = projectCode;
        _shopCode = shopCode;
        _shopName = shopName;
        _subjectCode = subjectCode;
    }

	public int getCount() {
		return _mInspectionStandardData.size();
	}

	public Object getItem(int arg0) {
		return null;
	}

	public long getItemId(int arg0) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup viewGroup) {
		try{
			ViewHolder_InspectionStandard holder = null;
	        if (convertView == null) {
	            holder=new ViewHolder_InspectionStandard();  
	            convertView = _mInflater.inflate(R.layout.listview_inspectionstandard, null);
	            holder.txtInspectionStandard = (TextView)convertView.findViewById(R.id.txtInspectionStandard);
	            holder.cboInspectionResult = (Spinner)convertView.findViewById(R.id.cboInspectionResult);
	            holder.btnAddImg = (ListViewButton)convertView.findViewById(R.id.btnAddImg);
	            holder.btnSelectImg = (ListViewButton)convertView.findViewById(R.id.btnSelectImg);
	            holder.btnLookImg = (ListViewButton)convertView.findViewById(R.id.btnLookImg);
	            holder.rdogCheckOptions = (RadioGroup)convertView.findViewById(R.id.rdogCheckOptions);
	            holder.rdo01 = (RadioButton)convertView.findViewById(R.id.rdo01);
	            holder.rdo02 = (RadioButton)convertView.findViewById(R.id.rdo02);
	            holder.rdo03 = (RadioButton)convertView.findViewById(R.id.rdo03);
	            convertView.setTag(holder);
	        }else {
	            holder = (ViewHolder_InspectionStandard)convertView.getTag();
	        }
	        
	        holder.txtInspectionStandard.setText((String)_mInspectionStandardData.get(position).get("txtInspectionStandard"));
		        
	        String seqNO = (String)_mInspectionStandardData.get(position).get("inspectionStandardSeqNO");
	        holder.rdogCheckOptions.setTag(seqNO);
			if(_mCheckOptionValues.containsKey(seqNO)){
				String checkOptionCode = _mCheckOptionValues.get(seqNO);
		        int intValue = Integer.parseInt(checkOptionCode);
				switch (intValue) {
				case 1:
					holder.rdo01.setChecked(true);
					break;
				case 2:
					holder.rdo02.setChecked(true);
					break;
				case 3:
					holder.rdo03.setChecked(true);
					break;
				default:
					break;
				}
			}
			holder.rdogCheckOptions.setOnCheckedChangeListener(onRadioChanged);

	        holder.btnAddImg.setTag(seqNO);
	        holder.btnAddImg.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	try{
						Intent intent = new Intent();
						//调用相机
				        intent.setClass(v.getContext(), CameraActivity.class);
				        intent.putExtra("projectCode", _projectCode);
				        intent.putExtra("shopCode", _shopCode);
				        intent.putExtra("shopName", _shopName);
				        intent.putExtra("subjectCode", _subjectCode);
				        intent.putExtra("seqNO", (String)v.getTag());
				        intent.putExtra("type", "Camera");
				        intent.putExtra("imagePath", "失分说明图片");
				        v.getContext().startActivity(intent);
	            	}catch (Exception e) {
	        			showMsg(v.getContext(), "异常", e.getMessage());
	        		}
	            }
	        });
	        
	        holder.btnSelectImg.setTag(seqNO);
	        holder.btnSelectImg.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	try{
						Intent intent = new Intent();
						//调用相册
				        intent.setClass(v.getContext(), CameraActivity.class);
				        intent.putExtra("projectCode", _projectCode);
				        intent.putExtra("shopCode", _shopCode);
				        intent.putExtra("shopName", _shopName);
				        intent.putExtra("subjectCode", _subjectCode);
				        intent.putExtra("seqNO", (String)v.getTag());
				        intent.putExtra("type", "Gallery");
				        intent.putExtra("imagePath", "失分说明图片");
				        v.getContext().startActivity(intent);
	            	}catch (Exception e) {
	        			showMsg(v.getContext(), "异常", e.getMessage());
	        		}
	            }
	        });
	        
	        holder.btnLookImg.setTag(seqNO);
	        holder.btnLookImg.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	//写跳转到详细的地方
					Intent intent = new Intent();
					//调用查看图片
			        intent.setClass(v.getContext(), ThumbnailActivity.class);
			        intent.putExtra("projectCode", _projectCode);
			        intent.putExtra("shopCode", _shopCode);
			        intent.putExtra("subjectCode", _subjectCode);
			        intent.putExtra("seqNO", (String)v.getTag());
					String paths = Environment.getExternalStorageDirectory() + 
								   "/XHX_YIQI_Data"+
								   "/"+_projectCode+_shopName+
								   "/"+_subjectCode;
			        intent.putExtra("imagePath", paths);
					String[] pics = null;
					Dao dao = new Dao();
					Cursor cursor = dao.SearchInspectionStandardImageList(_projectCode, _shopCode, _subjectCode, (String)v.getTag());
					if(cursor.getCount()>0){
						cursor.moveToFirst();
						String picNames = cursor.getString(cursor.getColumnIndex("PicName"));
						if(null==picNames) pics=new String[]{};
						else pics = picNames.split(";");
				        intent.putExtra("pictures", pics);
				        v.getContext().startActivity(intent);
					}
					else{
						Toast.makeText(v.getContext(), "没有照片", Toast.LENGTH_LONG).show();
					}
	            }
	        });
	        
		}catch (Exception e) {
			showMsg(convertView.getContext(), "异常", e.getMessage());
		}
         
        return convertView;
	}
	
	public final class ViewHolder_InspectionStandard{
		public TextView txtInspectionStandard;
		public Spinner cboInspectionResult;
		public Button btnAddImg;
		public Button btnSelectImg;
		public Button btnLookImg;
		public RadioGroup rdogCheckOptions;
		public RadioButton rdo01;
		public RadioButton rdo02;
		public RadioButton rdo03;
	}

	public ArrayList<Map<String, Object>> getInspectionStandardList(){
		return _mInspectionStandardData;
	}
	
	public Map<String, String> getInspectionStandardCheckOptionValues(){
		return _mCheckOptionValues;
	}
	
	private RadioGroup.OnCheckedChangeListener onRadioChanged = new RadioGroup.OnCheckedChangeListener(){ 
	    public void onCheckedChanged(RadioGroup group, int checkedId){ 
	    	RadioButton radioButton = (RadioButton)group.findViewById(checkedId);
	    	String stringValue = radioButton.getText().toString();
	    	String value = stringValue.equals("是")?"01":stringValue.equals("否")?"02":"03";
	    	_mCheckOptionValues.put(group.getTag().toString(), value);
	    }
	};
	
	private void showMsg(Context context, String title, String message)
	{
		new AlertDialog.Builder(context).setTitle(title).setMessage(message).setPositiveButton("关闭", null).show();
	}
	
}


