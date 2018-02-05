package com.klsl.sp.pad.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import com.klsl.sp.pad.dao.Dao;
import com.klsl.sp.pad.utility.ListViewButton;
import com.klsl.sp.pad.utility.NameValueObject;
import com.klsl.sp.pad.view.CameraActivity;
import com.klsl.sp.pad.view.ShowMoreImageActivity;
import com.klsl.sp.pad.view.ThumbnailActivity;
import com.klsl.sp.pad.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class InspectionImgAdapter extends BaseAdapter {

	private LayoutInflater _mInflater;
	private ArrayList<Map<String, Object>> _mInspectionImgData;
	private Map<String, String> _mCheckOptionValues;
	private String _projectCode;
	private String _shopCode;
	private String _shopName;
	private String _subjectCode;
	
	public InspectionImgAdapter(Context context, 
			                    ArrayList<Map<String, Object>> mInspectionImgData, 
			                    Map<String, String> mCheckOptionValues,
								String projectCode,
								String shopCode,
								String shopName,
								String subjectCode){
		_mInflater = LayoutInflater.from(context);
        _mInspectionImgData = mInspectionImgData;
        _mCheckOptionValues = mCheckOptionValues;
        _projectCode = projectCode;
        _shopCode = shopCode;
        _shopName = shopName;
        _subjectCode = subjectCode;
    }

	public int getCount() {
		// TODO Auto-generated method stub
		return _mInspectionImgData.size();
	}
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder_InspectionImg holder = null;
        if (convertView == null) {
            holder=new ViewHolder_InspectionImg();  
            convertView = _mInflater.inflate(R.layout.listview_inspectionimg, null);
            holder.txtFileName = (TextView)convertView.findViewById(R.id.txtFileName);
            holder.cboCheckOptionCode = (Spinner)convertView.findViewById(R.id.cboCheckOptionCode);
            holder.btnPhoto = (ListViewButton)convertView.findViewById(R.id.btnPhoto);
            holder.btnSelectImg = (ListViewButton)convertView.findViewById(R.id.btnSelectImg);
            holder.btnLookImg = (ListViewButton)convertView.findViewById(R.id.btnLookImg);
            holder.rdogCheckOptions = (RadioGroup)convertView.findViewById(R.id.rdogCheckOptions);
            holder.rdo01 = (RadioButton)convertView.findViewById(R.id.rdo01);
            holder.rdo02 = (RadioButton)convertView.findViewById(R.id.rdo02);
            holder.rdo03 = (RadioButton)convertView.findViewById(R.id.rdo03);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder_InspectionImg)convertView.getTag();
        }
        
        holder.txtFileName.setText((String)_mInspectionImgData.get(position).get("txtFileName"));
        
    	String seqNO = (String)_mInspectionImgData.get(position).get("SeqNO");
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
        
		holder.btnPhoto.setTag(seqNO);
        holder.btnPhoto.setOnClickListener(new View.OnClickListener() {
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
			        intent.putExtra("imagePath", "标准图片");
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
			        intent.putExtra("imagePath", "标准图片");
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
							   "/KLSL_SP_Data"+
							   "/"+_projectCode+_shopName+
							   "/"+_subjectCode;
				String[] pics = null;
				Dao dao = new Dao();
				Cursor cursor = dao.SearchInspectionImg(_projectCode, _subjectCode, (String)v.getTag());
				if(cursor.moveToFirst()){
					String picName = cursor.getString(cursor.getColumnIndex("FileName"));
			        File file = new File(paths+"/"+picName+".jpg");
			        if(file.exists()){
			        	intent.putExtra("pictures", new String[]{picName+".jpg"});
			        }else{
			        	intent.putExtra("pictures", new String[]{});
			        }
					intent.putExtra("imagePath", paths);
			        
			        v.getContext().startActivity(intent);
				}
				else{
					Toast.makeText(v.getContext(), "没有照片", Toast.LENGTH_LONG).show();
				}
            }
        });
        
        return convertView;
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
		
	public ArrayList<Map<String, Object>> getInspectionImgList(){
		return _mInspectionImgData;
	}
	
	public Map<String, String> getInspectionImgCheckOptionValues(){
		return _mCheckOptionValues;
	}
	
	public final class ViewHolder_InspectionImg{
		public TextView txtFileName;
		public Spinner cboCheckOptionCode;
		public Button btnPhoto;
		public Button btnSelectImg;
		public Button btnLookImg;
		public RadioGroup rdogCheckOptions;
		public RadioButton rdo01;
		public RadioButton rdo02;
		public RadioButton rdo03;
	}
}
