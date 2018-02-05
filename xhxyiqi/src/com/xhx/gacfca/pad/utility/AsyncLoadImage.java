package com.xhx.gacfca.pad.utility;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

public class AsyncLoadImage {

	private HashMap<String, SoftReference<Bitmap>> imageCache;

	public AsyncLoadImage() {
		imageCache = new HashMap<String, SoftReference<Bitmap>>();
	}

	@SuppressLint({ "HandlerLeak", "HandlerLeak" })
	public Bitmap loadBitmap(final String imageUrl,
			final ImageCallback imageCallback) {
		if (imageCache.containsKey(imageUrl)) {
			SoftReference<Bitmap> softReference = imageCache.get(imageUrl);
			Bitmap bitmap = softReference.get();
			if (bitmap != null) {
				return bitmap;
			}
		}
		final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				imageCallback.imageLoaded((Bitmap) message.obj, imageUrl);
			}
		};
		new Thread() {
			@Override
			public void run() {
				Bitmap bitmap = loadImageFromUrl(imageUrl);
				imageCache.put(imageUrl, new SoftReference<Bitmap>(bitmap));
				Message message = handler.obtainMessage(0, bitmap);
				handler.sendMessage(message);
			}
		}.start();
		return null;
	}

	public static Bitmap loadImageFromUrl(String url) {
		return ImageUtil.GetImage(url, 240, 240);
	}
	
	public interface ImageCallback {
		public void imageLoaded(Bitmap bitmap, String imageUrl);

	}
}
