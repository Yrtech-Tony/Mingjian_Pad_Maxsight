package com.xhx.dsat.pad.adapter;

import com.xhx.dsat.pad.R;
import com.xhx.dsat.pad.utility.ImageUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {

	private Context mContext;
	private String[] pictures = {};
	private String imagePath = "";

	public ImageAdapter(Context mContext, String imagePath, String[] pictures) {
		super();
		this.mContext = mContext;
		this.imagePath = imagePath;
		this.pictures = pictures;
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return pictures.length;
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@SuppressWarnings("deprecation")
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ImageView image = new ImageView(mContext);
		Bitmap bmp = ImageUtil.GetImage(imagePath + "/" + pictures[arg0]);
		
		if(bmp == null){
			bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.noimage);
		}
		
		image.setImageBitmap(bmp);
		// 设置样式 ：填充整个屏幕
		image.setLayoutParams(new Gallery.LayoutParams(
				Gallery.LayoutParams.FILL_PARENT,
				Gallery.LayoutParams.FILL_PARENT));
		// 设置缩放比例：保持原样
		image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		
		return image;
	}

}
