package com.xhx.gacfca.pad.adapter;

import java.util.List;
import java.util.Map;

import com.xhx.gacfca.pad.R;
import com.xhx.gacfca.pad.view.AnswerSubjectsActivity;
import com.xhx.gacfca.pad.view.AnswerSubjectsActivity2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class SearchScoreSimpleAdapter extends SimpleAdapter {
    
	private int  selectItem = -1;
    public  void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }
    
	public SearchScoreSimpleAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = super.getView(position, convertView, parent);
		String[] hideValues = ((String) ((TextView)convertView.findViewById(R.id.hidenValue)).getText()).split("&;");
		if (position == selectItem) {
	        convertView.setBackgroundColor(Color.parseColor("#E6F0C8"));
	        //convertView.setBackgroundResource(R.drawable.filemanagermenubg_w);
		} 
		else {
			if(hideValues.length > 3 && hideValues[3].equals("Y")) {
				convertView.setBackgroundColor(Color.argb(250, 46, 139, 87));
			}else{
				int colorPos = position % 2;
				if(colorPos==1)  
					convertView.setBackgroundColor(Color.argb(250, 255, 255, 255));   
				else  
					convertView.setBackgroundColor(Color.argb(250, 224, 243, 250));
			}
		}
		addBtnListener(convertView, hideValues);
		return convertView;
	}
	
	private void addBtnListener(View convertView,String[] hideValues) {
		final Context context = convertView.getContext();
		Button btn = (Button)convertView.findViewById(R.id.btnDetail);
		final String projectCode = hideValues[0];
		final String shopCode = hideValues[1];
		final String shopName = hideValues[2];
		final String subjectCode = (String) ((TextView)convertView.findViewById(R.id.subjectCode)).getText();
		final String orderNO = (String) ((TextView)convertView.findViewById(R.id.orderNO)).getText();
		btn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//跳转到得分登记的地方
				Intent intent = new Intent();
		        intent.setClass(context, AnswerSubjectsActivity2.class);
		        intent.putExtra("projectCode", projectCode);
		        intent.putExtra("subjectCode", subjectCode);
		        intent.putExtra("orderNO", orderNO);
		        intent.putExtra("shopCode", shopCode);
		        intent.putExtra("shopName", shopName);
		        context.startActivity(intent);
			}
		});
	 }
}
