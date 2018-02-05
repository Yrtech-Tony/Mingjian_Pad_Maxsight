package com.xhx.dsat.pad.view;

import com.xhx.dsat.pad.R;
import com.xhx.dsat.pad.adapter.ThumbnailImageAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class ThumbnailActivity extends Activity {

	private GridView gridview = null;
	private String imagePath;
	private String[] pictures;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.thumbnail);
		Intent intent = getIntent();
		//图片所处的路径
		imagePath =  intent.getStringExtra("imagePath");
		//路径下包含的图片名字
		pictures = intent.getStringArrayExtra("pictures");
		
		gridview = (GridView) findViewById(R.id.gridview);
		ThumbnailImageAdapter adapter = new ThumbnailImageAdapter(ThumbnailActivity.this, imagePath, pictures,gridview);
		gridview.setAdapter(adapter);
		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				//Toast.makeText(ThumbnailActivity.this, "" + arg2,Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
		        intent.setClass(ThumbnailActivity.this, ShowMoreImageActivity.class);
				intent.putExtra("imagePath", imagePath);
				intent.putExtra("pictures", pictures);
				intent.putExtra("index", arg2);
				startActivity(intent);
			}
		});
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if( imagePath == null || pictures.length == 0){
        	Toast.makeText(ThumbnailActivity.this,"无任何照片！",Toast.LENGTH_SHORT).show();
        	ThumbnailActivity.this.finish();
        }
	}

}
