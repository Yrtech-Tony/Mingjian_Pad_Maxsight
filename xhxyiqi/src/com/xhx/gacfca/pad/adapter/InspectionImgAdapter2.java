package com.xhx.gacfca.pad.adapter;

import java.util.ArrayList;
import java.util.Map;

import com.xhx.gacfca.pad.R;
import com.xhx.gacfca.pad.view.AnswerSubjectsActivity2;

import android.R.bool;
import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SimpleAdapter;

public class InspectionImgAdapter2 extends SimpleAdapter {

	Context context;
	private ArrayList<Map<String, Object>> _mInspectionImgData;
	private Map<String, String> _mCheckOptionValues;
	TextView textScore;
	TextView textLostScoreDesc;
	private boolean _readOnly;
	public Map<String, String> getInspectionImgCheckOptionValues(){
		return _mCheckOptionValues;
	}
	
	private String _fullScore;
	private String _scoreInspection;
	
	public InspectionImgAdapter2(Context context,
			ArrayList<Map<String, Object>> mInspectionImgData, int resource,
			String[] from, int[] to,
            Map<String, String> mCheckOptionValues,TextView textScore,TextView textLostScoreDesc,boolean readOnly,String fullScore,String scoreInspection) {
		super(context, mInspectionImgData, resource, from, to);
		// TODO Auto-generated constructor stub
		_mInspectionImgData = mInspectionImgData;
		_mCheckOptionValues = mCheckOptionValues;
		this.context = context;
		this.textScore = textScore;
		this.textLostScoreDesc = textLostScoreDesc;
		this._readOnly = readOnly;
		this._fullScore = fullScore;
		this._scoreInspection = scoreInspection;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = super.getView(position, convertView, parent);
		String seqNo = _mInspectionImgData.get(position).get("inspectionStandardSeqNO")
				.toString();
		CheckBox chk = (CheckBox)convertView.findViewById(R.id.checkBoxfileName);
		chk.setTag(seqNo);
		if(_mCheckOptionValues.containsKey(seqNo)){
			String checkOptionCode = _mCheckOptionValues.get(seqNo);
	        int intValue = 0;
	        if (checkOptionCode != null) {
	        	intValue = Integer.parseInt(checkOptionCode);
			}
			if(intValue == 1){
				chk.setChecked(true);
			}else{
				chk.setChecked(false);
			}
		}
		else
		{
			chk.setChecked(false);
		}
		if(_readOnly){
			chk.setEnabled(!_readOnly);
		}
		chk.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				
				String seqNO = buttonView.getTag().toString();
				if(isChecked){
					_mCheckOptionValues.put(seqNO, "01");
		    	}else{
		    		_mCheckOptionValues.put(seqNO, "02");
		    	}
				String oldScoreDesc = "";
				for (int i = 0; i < _mInspectionImgData.size(); i++) {
					String itemStringseqNo = _mInspectionImgData.get(i).get("inspectionStandardSeqNO")
							.toString();
					String checkOptionCode = _mCheckOptionValues.get(itemStringseqNo);
			        int intValue = 0;
			        if (checkOptionCode != null) {
			        	intValue = Integer.parseInt(checkOptionCode);
					}
					if(intValue == 1){
						oldScoreDesc +=_mInspectionImgData.get(i).get("txtInspectionStandard").toString()+";";
					}
				}
				
				textLostScoreDesc.setText(oldScoreDesc);
				
				if(_mCheckOptionValues.containsValue("01")){
					textScore.setText("0");
				}else{
					if(_scoreInspection !=null && _scoreInspection.equals("01")){
						textScore.setText("1");
					}else if(_scoreInspection !=null && _scoreInspection.equals("02")){
						textScore.setText("2");
					}else{
						textScore.setText(_fullScore);
					}
				}
			}
		});
		return convertView;
	}

	@SuppressWarnings("unused")
	private void showMsg(Context context, String title, String message) {
		new AlertDialog.Builder(context).setTitle(title).setMessage(message)
				.setPositiveButton("¹Ø±Õ", null).show();
	}
}
