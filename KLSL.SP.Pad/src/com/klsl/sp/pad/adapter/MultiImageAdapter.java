package com.klsl.sp.pad.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.klsl.sp.pad.dao.Dao;
import com.klsl.sp.pad.utility.AsyncLoadImage;
import com.klsl.sp.pad.utility.ImageUtil;
import com.klsl.sp.pad.utility.AsyncLoadImage.ImageCallback;
import com.klsl.sp.pad.view.CameraActivity;
import com.klsl.sp.pad.view.ThumbnailActivity;
import com.klsl.sp.pad.R;

import android.R.string;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MultiImageAdapter extends SimpleAdapter {

	private String _projectCode;
	private String _shopCode;
	private String _shopName;
	private String _subjectCode;
	private ArrayList<Map<String, Object>> _mInspectionImgData;
	private AsyncLoadImage asyncLoadImage;
	GridView gridViewImGridView;

	public MultiImageAdapter(Context context,
			ArrayList<Map<String, Object>> mInspectionImgData, int resource,
			String[] from, int[] to, String projectCode, String shopCode,
			String shopName, String subjectCode, GridView gridView) {
		super(context, mInspectionImgData, resource, from, to);
		// TODO Auto-generated constructor stub
		_mInspectionImgData = mInspectionImgData;
		_projectCode = projectCode;
		_shopCode = shopCode;
		_shopName = shopName;
		_subjectCode = subjectCode;
		this.asyncLoadImage = new AsyncLoadImage();
		this.gridViewImGridView = gridView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = super.getView(position, convertView, parent);
		String seqNo = _mInspectionImgData.get(position).get("SeqNO")
				.toString();
		addBtnListener(convertView, seqNo);
		return convertView;
	}

	private void addBtnListener(View convertView, String seqNo) {

		TextView txtSearchImg = (TextView) convertView
				.findViewById(R.id.txtSearchImg);
		txtSearchImg.setTag(seqNo);
		txtSearchImg.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					Intent intent = new Intent();
					//调用查看图片
			        intent.setClass(v.getContext(), ThumbnailActivity.class);
			        intent.putExtra("projectCode", _projectCode);
			        intent.putExtra("shopCode", _shopCode);
			        intent.putExtra("subjectCode", _subjectCode);
			        intent.putExtra("seqNO", (String)v.getTag());
					String paths = Environment.getExternalStorageDirectory() + 
								   "/XHX_BMW_Data"+
								   "/"+_projectCode+_shopName+
								   "/"+_subjectCode;
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
				} catch (Exception e) {
					showMsg(v.getContext(), "异常", e.getMessage());
				}
			}
		});
		
		ImageView imageView1 = (ImageView) convertView
				.findViewById(R.id.imageView1);
		imageView1.setTag(seqNo);
		imageView1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					Intent intent = new Intent();
					// 调用相机
					intent.setClass(v.getContext(), CameraActivity.class);
					intent.putExtra("projectCode", _projectCode);
					intent.putExtra("shopCode", _shopCode);
					intent.putExtra("shopName", _shopName);
					intent.putExtra("subjectCode", _subjectCode);
					intent.putExtra("seqNO", v.getTag().toString());
					intent.putExtra("type", "Camera");
					intent.putExtra("imagePath", "标准图片");
					intent.putExtra("flagShowReview", "");
					v.getContext().startActivity(intent);
				} catch (Exception e) {
					showMsg(v.getContext(), "异常", e.getMessage());
				}
			}
		});

		ImageView imageView2 = (ImageView) convertView
				.findViewById(R.id.imageView2);
		imageView2.setTag(seqNo);
		imageView2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					Intent intent = new Intent();
					// 调用相机
					intent.setClass(v.getContext(), CameraActivity.class);
					intent.putExtra("projectCode", _projectCode);
					intent.putExtra("shopCode", _shopCode);
					intent.putExtra("shopName", _shopName);
					intent.putExtra("subjectCode", _subjectCode);
					intent.putExtra("seqNO", v.getTag().toString());
					intent.putExtra("type", "Camera");
					intent.putExtra("imagePath", "失分说明图片");
					intent.putExtra("flagShowReview", "show");
					v.getContext().startActivity(intent);
				} catch (Exception e) {
					showMsg(v.getContext(), "异常", e.getMessage());
				}
			}
		});

		ImageView imageView3 = (ImageView) convertView
				.findViewById(R.id.ImageView3);
		imageView3.setTag(seqNo);
		imageView3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					Intent intent = new Intent();
					// 调用相册
					intent.setClass(v.getContext(), CameraActivity.class);
					intent.putExtra("projectCode", _projectCode);
					intent.putExtra("shopCode", _shopCode);
					intent.putExtra("shopName", _shopName);
					intent.putExtra("subjectCode", _subjectCode);
					intent.putExtra("seqNO", v.getTag().toString());
					intent.putExtra("type", "Gallery");
					intent.putExtra("imagePath", "失分说明图片");
					intent.putExtra("flagShowReview", "show");
					v.getContext().startActivity(intent);
				} catch (Exception e) {
					showMsg(v.getContext(), "异常", e.getMessage());
				}
			}
		});
		
		ImageView imageView4 = (ImageView) convertView
				.findViewById(R.id.ImageView4);
		imageView4.setTag(seqNo);
		imageView4.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					Intent intent = new Intent();
					//调用查看图片
			        intent.setClass(v.getContext(), ThumbnailActivity.class);
			        intent.putExtra("projectCode", _projectCode);
			        intent.putExtra("shopCode", _shopCode);
			        intent.putExtra("subjectCode", _subjectCode);
			        intent.putExtra("seqNO", (String)v.getTag());
					String paths = Environment.getExternalStorageDirectory() + 
								   "/XHX_BMW_Data"+
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
				} catch (Exception e) {
					showMsg(v.getContext(), "异常", e.getMessage());
				}
			}
		});
		
		String imageUrl = "";
		String paths = Environment.getExternalStorageDirectory()
				+ "/XHX_BMW_Data" + "/" + _projectCode + _shopName + "/"
				+ _subjectCode;
		Dao dao = new Dao();
		Cursor cursor = dao.SearchInspectionImg(_projectCode, _subjectCode,
				seqNo);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			String picName = cursor
					.getString(cursor.getColumnIndex("FileName"));
			File file = new File(paths + "/" + picName + ".jpg");
			imageUrl = paths + "/" + picName + ".jpg";
			while (!file.exists() && cursor.moveToNext()) {
				picName = cursor.getString(cursor.getColumnIndex("FileName"));
				file = new File(paths + "/" + picName + ".jpg");
				imageUrl = paths + "/" + picName + ".jpg";
			}

			Bitmap bitmap = ImageUtil.GetImage(imageUrl, 240, 240);
			if (bitmap == null) {
				imageView1.setImageResource(R.drawable.noimage);
			} else {
				imageView1.setImageBitmap(bitmap);
			}
			// imageView1.setTag(imageUrl);
			// Bitmap cachedImage = asyncLoadImage.loadBitmap(imageUrl,
			// new ImageCallback() {
			// public void imageLoaded(Bitmap bitmap, String imageUrl) {
			// ImageView imageViewByTag = (ImageView)
			// gridViewImGridView.findViewWithTag(imageUrl);
			// if (imageViewByTag != null) {
			// if (bitmap == null) {
			// imageViewByTag.setImageResource(R.drawable.noimage);
			// } else {
			// imageViewByTag.setImageBitmap(bitmap);
			// }
			// }
			// }
			// });
			// if (cachedImage != null) {
			// imageView1.setImageBitmap(cachedImage);
			// }
		}

		// cursor = dao.SearchInspectionStandardImageList(_projectCode,
		// _shopCode,
		// _subjectCode, seqNo);
		// String[] pics = null;
		// if (cursor.getCount() > 0) {
		// cursor.moveToFirst();
		// cursor.moveToFirst();
		// String picNames = cursor
		// .getString(cursor.getColumnIndex("PicName"));
		// if (null == picNames)
		// pics = new String[] {};
		// else
		// pics = picNames.split(";");
		// if (pics.length > 0) {
		// imageUrl = paths + "/" + pics[pics.length - 1];
		//
		// Bitmap bitmap = ImageUtil.GetImage(imageUrl, 240, 240);
		// if (bitmap == null) {
		// imageView2.setImageResource(R.drawable.noimage);
		// } else {
		// imageView2.setImageBitmap(bitmap);
		// }
		// imageView2.setTag(imageUrl);
		// Bitmap cachedImage = asyncLoadImage.loadBitmap(imageUrl,
		// new ImageCallback() {
		// public void imageLoaded(Bitmap bitmap,
		// String imageUrl) {
		// ImageView imageViewByTag = (ImageView) gridViewImGridView
		// .findViewWithTag(imageUrl);
		// if (imageViewByTag != null) {
		// if (bitmap == null) {
		// imageViewByTag
		// .setImageResource(R.drawable.noimage);
		// } else {
		// imageViewByTag.setImageBitmap(bitmap);
		// }
		// }
		// }
		// });
		// if (cachedImage != null) {
		// imageView2.setImageBitmap(cachedImage);
		// }
		// }
		// }

	}

	private void showMsg(Context context, String title, String message) {
		new AlertDialog.Builder(context).setTitle(title).setMessage(message)
				.setPositiveButton("关闭", null).show();
	}

	public final class Holder {
		public ImageView imageView;
		public String SeqNo;
		public String ImgUrl;
	}
}
