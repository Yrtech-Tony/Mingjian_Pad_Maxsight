package com.xhx.dsat.pad.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.http.util.ByteArrayBuffer;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Environment;
import android.util.Log;
import android.view.Display;

public class ImageUtil {

	/**
	 * ���SD���Ƿ����
	 * 
	 * @return
	 */
	public static Boolean ExistSDcard() {
		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
			return false;
		}
		return true;
	}

	/**
	 * ��ͼƬ������С����
	 * 
	 * @param imageFilePath
	 * @param display
	 *            ���ųߴ�
	 * @return
	 */
	@SuppressLint("FloatMath")
	public static Bitmap GetImage(String imageFilePath, int dw,int dh) {
		
		File file = new File(imageFilePath);
		if(!file.exists()){
			return null;
		}
		
		// Point size = new Point();
		// currentDisplay.getSize(size);
		//int dw = display.getWidth();// size.x;
		//int dh = display.getHeight();// size.y;
		
		// ���ĳ�����Ƭ��������
		BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
		bmpFactoryOptions.inJustDecodeBounds = true;
		Bitmap bmp = BitmapFactory.decodeFile(imageFilePath, bmpFactoryOptions);

		int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight
				/ (float) dh);
		int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth
				/ (float) dw);

		if (heightRatio > 1 && widthRatio > 1) {
			if (heightRatio > widthRatio) {
				bmpFactoryOptions.inSampleSize = heightRatio;
			} else {
				bmpFactoryOptions.inSampleSize = widthRatio;
			}
		}
		bmpFactoryOptions.inJustDecodeBounds = false;
		bmp = BitmapFactory.decodeFile(imageFilePath, bmpFactoryOptions);
		return bmp;
	}

	
	/**��ͼƬ����ԭʼ�ȱ�������
	 * @param imageFilePath ͼƬ·��
	 * @param display
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Bitmap GetImageAdaptive(String imageFilePath, Display display) {
		
		File file = new File(imageFilePath);
		if(!file.exists()){
			return null;
		}
		
		BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
		bmpFactoryOptions.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath, bmpFactoryOptions);
		if (bmpFactoryOptions.outHeight > 6000 || bmpFactoryOptions.outWidth > 6000) {
			bmpFactoryOptions.inSampleSize = 4;
		}else if (bmpFactoryOptions.outHeight > 3000 || bmpFactoryOptions.outWidth > 3000) {
			bmpFactoryOptions.inSampleSize = 2;
		}
		bmpFactoryOptions.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(imageFilePath, bmpFactoryOptions);
		//Bitmap bitmap =  getBitMapByPath(imageFilePath);
		int scalX = display.getWidth();
		int scalY = display.getHeight();
		Matrix matrix = new Matrix();
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float sw = scalX / (float) width;
		float sh = scalY / (float) height;
		float scale = 0;
		if(sw > sh){
			scale = sh;
		}else{
			scale = sw;
		}
		matrix.postScale(scale, scale);// ��ȡ���ű���
		// �������ű�����ȡ�µ�λͼ
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,matrix, true);
		
		if(newbmp != null){
			//���ʱ��
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);
			String time = dateFormat.format(new Date());
	        Canvas canvas = new Canvas(newbmp);
	        Paint paint = new Paint();
	        //Typeface font = Typeface.create("����", Typeface.BOLD);
	        //paint.setTypeface(font);
	        paint.setColor(Color.argb(255, 204, 174, 51));
	        paint.setTextSize(18);
	        canvas.drawBitmap(newbmp, 0, 0, paint);
	        canvas.drawText(time, newbmp.getWidth() - paint.measureText(time) - 10, newbmp.getHeight() - 10, paint);
	        canvas.save(Canvas.ALL_SAVE_FLAG);
	        canvas.restore();
		}
		
		if(!bitmap.isRecycled())
		{
			bitmap.recycle();
		}
		return newbmp;
	}
	
	/**��ͼƬ����ԭʼ�ȱ�������
	 * @param bitmap λͼ
	 * @param display
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Bitmap GetImageAdaptive(Bitmap bitmap, Display display) {
		int scalX = display.getWidth();
		int scalY = display.getHeight();
		Matrix matrix = new Matrix();
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float sw = scalX / (float) width;
		float sh = scalY / (float) height;
		float scale = 0;
		if(sw > sh){
			scale = sh;
		}else{
			scale = sw;
		}
		matrix.postScale(scale, scale);// ��ȡ���ű���
		// �������ű�����ȡ�µ�λͼ
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,matrix, true);
		return newbmp;
	}
	
	/**
	 * ȡ��ԭͼƬ�����������Ŵ���
	 * 
	 * @param imageFilePath
	 * @return
	 */
	public static Bitmap GetImage(String imageFilePath) {
		File file = new File(imageFilePath);
		if(!file.exists()){
			return null;
		}
		BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
		bmpFactoryOptions.inJustDecodeBounds = false;
		Bitmap bmp = BitmapFactory.decodeFile(imageFilePath, bmpFactoryOptions);
		return bmp;
	}

	/**
	 * ����ͼƬ
	 * 
	 * @param filePath
	 * @param fileName
	 * @param bitmap
	 */
	public void SaveBitmap(String filePath, String fileName, Bitmap bitmap) {
		// �洢·��
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	private static Bitmap getBitMapByPath(String path) {
		Bitmap bmp = null;
		File myFile = new File(path);
		if (myFile.exists()) {
			try {
				FileInputStream inputStream = new FileInputStream(myFile);
				ByteArrayBuffer baf = new ByteArrayBuffer(50);
				int current = 0;
				while ((current = inputStream.read()) != -1) {
					baf.append((byte) current);
				}
				inputStream.close();
				bmp = BitmapFactory.decodeByteArray(baf.toByteArray(), 0,
						baf.toByteArray().length);
				// ByteArrayOutputStream baos = new ByteArrayOutputStream();
				// byte[] b = new byte[1024];
				// int len = 0;
				// while ((len = inputStream.read(b, 0, 1024)) != -1) {
				// baos.write(b, 0, len);
				// baos.flush();
				// }
				// byte[] bytes = baos.toByteArray();
				// inputStream.close();
				// bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
			} catch (Exception e) {
				// TODO: handle exception
				Log.e("getBitMapByPath", e.getMessage());
			}
		}
		return bmp;
	}
}
