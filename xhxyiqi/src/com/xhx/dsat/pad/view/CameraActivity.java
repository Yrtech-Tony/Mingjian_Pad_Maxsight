package com.xhx.dsat.pad.view;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.xhx.dsat.pad.R;
import com.xhx.dsat.pad.dao.Dao;
import com.xhx.dsat.pad.utility.ImageSketchView;
import com.xhx.dsat.pad.utility.ImageUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView.ScaleType;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

public class CameraActivity extends Activity {

	private static String imageFilePath = "";//图片存储的完整路径
	private static final int CAMERA_RESULT = 1000;
	private static final int GALLERY_RESULT = 1001;
	private static ImageSketchView sketchView = null;
	private static String defaultFilePath = null;//程序存储默认路径
	private static String defaultFileName = null;//图片名字
	
	private static final String TAG_LOG = "CameraActivity";
	
	private String _projectCode;
	private String _shopCode;
	private String _shopName;
	private String _subjectCode;
	private String _seqNO;
	private String _imagePath;
	private String _imageName;
	private Dao _dao;
	
	private RelativeLayout layoutImage;
	private ProgressBar progressBar;
	
	private String _flagShowReview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		_dao = new Dao();
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏  
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  
        WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
        
		layoutImage = new RelativeLayout(this);
		RelativeLayout.LayoutParams lpLayout = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		layoutImage.setLayoutParams(lpLayout);
		setContentView(layoutImage);
		
		progressBar = new ProgressBar(this);
		progressBar.setVisibility(View.GONE);
		RelativeLayout.LayoutParams lppro = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lppro.addRule(RelativeLayout.CENTER_HORIZONTAL);
		lppro.addRule(RelativeLayout.CENTER_VERTICAL);
		progressBar.setLayoutParams(lppro);
		layoutImage.addView(progressBar);
		
        //setContentView(R.layout.camera);
		Intent intent = getIntent();
		_projectCode = intent.getStringExtra("projectCode");
		_shopCode = intent.getStringExtra("shopCode");
		_shopName = intent.getStringExtra("shopName");
		_subjectCode = intent.getStringExtra("subjectCode");
		_seqNO = intent.getStringExtra("seqNO");
		_imagePath = intent.getStringExtra("imagePath");
		_imageName = getImageName();
		defaultFilePath = Environment.getExternalStorageDirectory() + "/XHX_DSAT_Data";
		
		_flagShowReview = intent.getStringExtra("flagShowReview");
		
		//sketchView = (ImageSketchView) findViewById(R.id.imageView1);
		if(intent.getStringExtra("type").equals("Camera")){
			callCamera();
		}
		else if(intent.getStringExtra("type").equals("Gallery")){
			callGallery();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		Log.i(TAG_LOG, "resultCode:"+resultCode);
		
		// 如果拍照成功
		if (resultCode == RESULT_OK) {
			// 取得屏幕的显示大小
			//Display currentDisplay = getWindowManager().getDefaultDisplay();
			//Bitmap bmp = null;
			
			Log.i(TAG_LOG, "requestCode:"+requestCode);
			Log.i(TAG_LOG, "imageFilePath:"+imageFilePath);
			
			switch (requestCode) {
			case CAMERA_RESULT:
				LoadImageTask task1 = new LoadImageTask(this);
				task1.execute(imageFilePath);
				// bmp = ImageUtil.GetImageAdaptive(imageFilePath,currentDisplay);
				break;
			case GALLERY_RESULT:
				Uri selectedImage = data.getData();
				String[] filePathColumns = { MediaStore.Images.Media.DATA };
				Cursor cursor = this.getContentResolver().query(selectedImage,
						filePathColumns, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumns[0]);
				String picturePath = cursor.getString(columnIndex);
				cursor.close();
				LoadImageTask task2 = new LoadImageTask(this);
				task2.execute(picturePath);
				// bmp = ImageUtil.GetImageAdaptive(picturePath,currentDisplay);
				break;
			case 1200:
				setResult(1200);
				CameraActivity.this.finish();
				break;
			}
		} else if (resultCode == RESULT_CANCELED) {
			CameraActivity.this.finish();
		}
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.camera_menu, menu);
        return true;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.menu_save:
			SaveImg();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK){
			new AlertDialog.Builder(this)
			.setTitle("退出照相编辑")
			.setMessage("是否要保存更改？")
			.setNegativeButton("否", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					CameraActivity.this.finish();
				}
			})
			.setPositiveButton("是",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					SaveImg();
				}
			}) .show();            
			return true; 
		}
		return super.onKeyDown(keyCode, event);
	}

	private void callCamera(){
		new Thread(
				new Runnable(){
					public void run(){
						
						 if (!ImageUtil.ExistSDcard()) {
				                Toast.makeText(CameraActivity.this, "SD卡不可用", Toast.LENGTH_LONG).show();
				                return;
				            }
						 
						//用时间作为图片名字
						//Date today=new Date();
						//SimpleDateFormat formatdate=new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
						defaultFileName = _imageName + ".jpg";//_subjectCode+"_"+formatdate.format(today) + ".jpg";
						File file = new File(defaultFilePath);
						if (!file.exists()) {
							file.mkdirs();
						}
						defaultFilePath+=("/"+_projectCode+_shopName);
						file = new File(defaultFilePath);
						if (!file.exists()) {
							file.mkdirs();
						}
						defaultFilePath+=("/"+_subjectCode);
						file = new File(defaultFilePath);
						if (!file.exists()) {
							file.mkdirs();
						}
						imageFilePath = defaultFilePath + "/" + defaultFileName;
						File imageFile = new File(imageFilePath);
						Uri imageFileUri = Uri.fromFile(imageFile);
						Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
						intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageFileUri);
						startActivityForResult(intent, CAMERA_RESULT);
						Log.i(TAG_LOG, "callCamera_imageFilePath:"+imageFilePath);
					}
				}
			).start();
	}
	
	private void callGallery(){
		new Thread(
				new Runnable(){
					public void run(){
						
						 if (!ImageUtil.ExistSDcard()) {
				                Toast.makeText(CameraActivity.this, "SD卡不可用", Toast.LENGTH_LONG).show();
				                return;
				        }
						//用时间作为图片名字
						//Date today=new Date();
						//SimpleDateFormat formatdate=new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
						defaultFileName = _imageName + ".jpg";//_subjectCode+"_"+formatdate.format(today) + ".jpg";
						File file = new File(defaultFilePath);
						if (!file.exists()) {
							file.mkdirs();
						}
						defaultFilePath+=("/"+_projectCode+_shopName);
						file = new File(defaultFilePath);
						if (!file.exists()) {
							file.mkdirs();
						}
						defaultFilePath+=("/"+_subjectCode);
						file = new File(defaultFilePath);
						if (!file.exists()) {
							file.mkdirs();
						}
						imageFilePath = defaultFilePath + "/" + defaultFileName;
						
						Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
						intent.setType("image/*");
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivityForResult(intent, GALLERY_RESULT);
					}
				}
			).start();
	}
	
	private void SaveImg() {
		sketchView.setFilePath(defaultFilePath);
		sketchView.SaveImg("",defaultFileName);
		if(_imagePath.equals("失分说明图片")){
			_dao.SaveInspectionStandardImg(_projectCode, _shopCode, _subjectCode, _seqNO, defaultFileName);
		}
		Toast.makeText(CameraActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
		
		if(_flagShowReview.equals("show")){
			Intent intent = new Intent();
			// 调用相册
			intent.setClass(CameraActivity.this, AnswerSubjectsActivity2Review.class);
			intent.putExtra("projectCode", _projectCode);
			intent.putExtra("shopCode", _shopCode);
			intent.putExtra("shopName", _shopName);
			intent.putExtra("subjectCode", _subjectCode);
			intent.putExtra("imagePath", imageFilePath);
			startActivity(intent);
			CameraActivity.this.finish();
		}else{
			CameraActivity.this.finish();
		}
	}
	
	private String getImageName(){
		String imageName = "未命名";
		if(_imagePath.equals("失分说明图片")){
			Cursor cursor = _dao.SearchInspectionStandard(_projectCode, _subjectCode, _seqNO);
			if(cursor.moveToNext()){
				imageName = cursor.getString(cursor.getColumnIndex("InspectionStandardName"));
			}

			cursor = _dao.SearchInspectionStandardImageList(_projectCode, _shopCode, _subjectCode, _seqNO);
			String picName = "";
			if(cursor.moveToNext()){
				picName = cursor.getString(cursor.getColumnIndex("PicName"));
				String[] pics = null;
				if(null == picName)  pics=new String[]{};
				else pics = picName.split(";");
				imageName += pics.length + 1;
			}
			else{
				imageName += 1;
			}
			cursor.close();
			cursor = null;
		}
		else{
			Cursor cursor = _dao.SearchInspectionImg(_projectCode, _subjectCode, _seqNO);
			if(cursor.moveToNext()){
				imageName = cursor.getString(cursor.getColumnIndex("FileName"));
			}
			cursor.close();
			cursor = null;
		}
		return imageName;
	}
	
 private class LoadImageTask extends AsyncTask<String, Integer, Bitmap> {
		
		//private ProgressDialog progressDialog = null;
		
		public LoadImageTask(Context context) {
//			progressDialog = new ProgressDialog(context);
//			progressDialog.setTitle("请稍等...");
//			progressDialog.setMessage("读取图片中...");
//			progressDialog.setCancelable(false);
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			//progressDialog.show();
			if(progressBar != null && !progressBar.isShown()){
				progressBar.setVisibility(View.VISIBLE);
			}
		}
		
		@Override
		protected Bitmap doInBackground(String... params) {
			// TODO Auto-generated method stub
			Bitmap data = null;
			try {
				Display currentDisplay = getWindowManager().getDefaultDisplay();
				data = ImageUtil.GetImageAdaptive(params[0],currentDisplay);
				Log.i(TAG_LOG, "LoadImageTask_imageFilePath:"+params[0]);
			} catch (Exception e) {
				return null;
			}
			return data;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(progressBar != null && progressBar.isShown()){
				progressBar.setVisibility(View.GONE);
			}
			
//			if(progressDialog != null && progressDialog.isShowing()){
//				progressDialog.dismiss();
//			}
			
			if (result != null) {
				sketchView = new ImageSketchView(layoutImage.getContext());
				//layoutImage.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
				RelativeLayout.LayoutParams lpImage = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lpImage.height = result.getHeight();
				lpImage.width = result.getWidth();
				lpImage.addRule(RelativeLayout.CENTER_IN_PARENT);
				sketchView.setLayoutParams(lpImage);
				sketchView.setScaleType(ScaleType.CENTER_INSIDE);
				sketchView.setImageBitmap(result.copy(result.getConfig(), true));
				layoutImage.addView(sketchView);
				
				Button btnSave = new Button(layoutImage.getContext());
				btnSave.setText("保存");
				btnSave.setBackgroundResource(R.drawable.button_selector);
				btnSave.setTextColor(R.drawable.button_font_selector);
				RelativeLayout.LayoutParams lpBtnSave = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lpBtnSave.width = 100;
				lpBtnSave.rightMargin = 10;
				lpBtnSave.bottomMargin = 10;
				lpBtnSave.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				lpBtnSave.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				btnSave.setLayoutParams(lpBtnSave);
				btnSave.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						// TODO Auto-generated method stub
						SaveImg();
					}
				});
				layoutImage.addView(btnSave);
				Log.i(TAG_LOG, "onPostExecute");
			}else{
				Toast.makeText(CameraActivity.this, "读取图片失败", Toast.LENGTH_SHORT).show();
				CameraActivity.this.finish();
			}
			}
		}
}
