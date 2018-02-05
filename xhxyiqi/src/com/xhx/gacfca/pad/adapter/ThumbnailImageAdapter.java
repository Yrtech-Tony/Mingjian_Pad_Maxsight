package com.xhx.gacfca.pad.adapter;

import com.xhx.gacfca.pad.R;
import com.xhx.gacfca.pad.utility.AsyncLoadImage;
import com.xhx.gacfca.pad.utility.AsyncLoadImage.ImageCallback;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ThumbnailImageAdapter extends BaseAdapter {

	private Context mContext;
	private String[] pictures = {};
	private String imagePath = "";
	private AsyncLoadImage asyncLoadImage;
	private GridView gridView;

	public ThumbnailImageAdapter(Context mContext, String imagePath,
			String[] pictures, GridView gridView) {
		super();
		this.mContext = mContext;
		this.imagePath = imagePath;
		this.pictures = pictures;
		this.asyncLoadImage = new AsyncLoadImage();
		this.gridView = gridView;
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

	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ImageView imageView = (ImageView) arg1;
		if (imageView == null) {
			imageView = new ImageView(mContext);
			imageView.setLayoutParams(new GridView.LayoutParams(240, 240));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		}

		// Bitmap bmp = ImageUtil.GetImage(imagePath + "/" +
		// pictures[arg0],240,240);
		// if(bmp == null){
		// bmp = BitmapFactory.decodeResource(mContext.getResources(),
		// R.drawable.noimage);
		// }
		// imageView.setImageBitmap(bmp);

		String imageUrl = imagePath + "/" + pictures[arg0];
		imageView.setTag(imageUrl);
		Bitmap cachedImage = asyncLoadImage.loadBitmap(imageUrl,
				new ImageCallback() {
					public void imageLoaded(Bitmap bitmap, String imageUrl) {
						ImageView imageViewByTag = (ImageView) gridView.findViewWithTag(imageUrl);
						if (imageViewByTag != null) {
							if (bitmap == null) {
								imageViewByTag.setImageResource(R.drawable.noimage);
							} else {
								imageViewByTag.setImageBitmap(bitmap);
							}
						}
					}
				});
		if (cachedImage != null) {
			imageView.setImageBitmap(cachedImage);
		}
		return imageView;
	}

}
