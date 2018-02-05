package com.klsl.sp.pad.utility;

import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class ImageSketchView extends ImageView {
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		//setMeasuredDimension(200,400);
	}

	private Bitmap imageBitmap = null;
	private Paint mPaint = null;
	private Path mpath = null;
	private static final float TOUCH_TOLERANCE = 4.0f;
	private float m_curX = 0.0f;
	private float m_curY = 0.0f;
	private Boolean saveFlag = false;
	private String filePath = null;
	private String fileName = null;
	
	public ImageSketchView(Context context) {
		super(context);
		mpath = new Path();
		mPaint = new Paint();
		// 去锯齿
		mPaint.setAntiAlias(true);
		// 设置paint的颜色
		mPaint.setColor(Color.RED);
		// 设置paint的style为空心
		mPaint.setStyle(Paint.Style.STROKE);
		// 设置paint的宽度
		mPaint.setStrokeWidth(3);
		mPaint.setDither(true);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
	}
	
	public ImageSketchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mpath = new Path();
		mPaint = new Paint();
		// 去锯齿
		mPaint.setAntiAlias(true);
		// 设置paint的颜色
		mPaint.setColor(Color.RED);
		// 设置paint的style为空心
		mPaint.setStyle(Paint.Style.STROKE);
		// 设置paint的宽度
		mPaint.setStrokeWidth(3);
		mPaint.setDither(true);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
	}

	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.WHITE);
		if (imageBitmap != null && !imageBitmap.isRecycled()) {
			try {
				int imageX =0;//(int)(this.getWidth()-imageBitmap.getWidth())/2;
				int imageY =0;//(int)(this.getHeight()-imageBitmap.getHeight())/2;
				canvas.drawBitmap(imageBitmap, imageX, imageY, mPaint);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (null != canvas) {
			canvas.drawPath(mpath, mPaint);
			if (saveFlag) {
				SaveBitmap(imageBitmap, mpath, mPaint);
				saveFlag = false;
			}
		}
		super.onDraw(canvas);
	}

	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mpath.moveTo(x, y);
			m_curX = x;
			m_curY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			float dx = Math.abs(x - m_curX);
			float dy = Math.abs(y - m_curY);

			if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
				mpath.quadTo(m_curX, m_curY, (x + m_curX) / 2, (y + m_curY) / 2);
				m_curX = x;
				m_curY = y;
			}
			break;
		case MotionEvent.ACTION_UP:
			mpath.lineTo(x, y);
			break;
		}
		this.invalidate();
		return true;
	}

	public Bitmap getImageBitmap() {
		return imageBitmap;
	}

	public void setImageBitmap(Bitmap imageBitmap) {
		this.imageBitmap = imageBitmap;
	}
	
	public void setFilePath(String filepath) {
		this.filePath = filepath;
	}

	public void ResetMPath() {
		if (null != mpath) {
			mpath.rewind();
			this.invalidate();
		}
	}

	public void SaveImg(String folider,String fileName) {
		saveFlag = true;
		if("" != folider && null != folider){
			this.filePath += folider;
		}
		this.fileName = fileName;
		this.invalidate();
	}
	
	// 保存到本地
	public void SaveBitmap(Bitmap bmp, Path mpath, Paint paint) {
		Bitmap bitmap = null;
		if(null == bmp){
			bitmap = Bitmap.createBitmap(800, 600, Config.ARGB_8888);
			fileName = "test.jpg";
		}else{
			bitmap = bmp.copy(bmp.getConfig(), true);
		}
		Canvas canvas = new Canvas(bitmap);
		if(null == bmp){
			canvas.drawColor(Color.WHITE);
		}
		canvas.drawPath(mpath, paint);
		// 保存全部图层
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		// 存储路径
		File file = new File(filePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(
					file.getPath() + "/" + fileName);
			if (null != bitmap) {
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
						fileOutputStream);
			}
			fileOutputStream.close();
			this.imageBitmap.recycle();
			bitmap.recycle();
			bmp.recycle();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}