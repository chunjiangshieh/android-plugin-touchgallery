/*
 Copyright (c) 2012 Roman Truba

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial
 portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.xcj.android.touchgallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import com.xcj.android.net.download.image.ImageCacheMgr.ImageCallBack;
import com.xcj.android.touchgallery.asyncdownload.AsyncImageCacheMgr;

public class UrlTouchImageView extends RelativeLayout {
	
	private static final String TAG = UrlTouchImageView.class.getSimpleName();
	
    protected ProgressBar mProgressBar;
    protected TouchImageView mImageView;
    protected TextView mTextView;

    protected Context mContext;
    //Async Download Image Manager
    protected AsyncImageCacheMgr mAsyncImageCacheMgr;

    public UrlTouchImageView(Context ctx)
    {
        super(ctx);
        mContext = ctx;
        init();

    }
    public UrlTouchImageView(Context ctx, AttributeSet attrs)
    {
        super(ctx, attrs);
        mContext = ctx;
        init();
    }
    public TouchImageView getImageView() { return mImageView; }

    protected void init() {
        mImageView = new TouchImageView(mContext);
        LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        mImageView.setLayoutParams(params);
        this.addView(mImageView);
        mImageView.setVisibility(GONE);

        mProgressBar = new ProgressBar(mContext, null, android.R.attr.progressBarStyleLargeInverse);
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
//        params.setMargins(30, 0, 30, 0);
        mProgressBar.setLayoutParams(params);
        mProgressBar.setIndeterminate(true);
        this.addView(mProgressBar);
        
        mTextView = new TextView(mContext);
        params = new LayoutParams(LayoutParams.WRAP_CONTENT,
        		LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mTextView.setTextSize(20.0f);
        mTextView.setLayoutParams(params);
        
        this.addView(mTextView);
        
        mAsyncImageCacheMgr = AsyncImageCacheMgr.getInstance(mContext);
    }
    
    /**
     * 设置缩略图
     * @param filePath
     */
    public void setUrlAndThumbnail(String imageUrl,String filePath){
    	Bitmap bitmap = mAsyncImageCacheMgr.getGalleryBitmap(imageUrl);
    	if(bitmap != null){
    		 mImageView.setImageBitmap(bitmap);
             mImageView.setVisibility(VISIBLE);
             mProgressBar.setVisibility(GONE);
             mTextView.setVisibility(GONE);
    	}else{
    		if(filePath != null && !filePath.equals("")){
    			Bitmap thumbBitmap = BitmapFactory.decodeFile(filePath);
            	if(thumbBitmap != null){
            		mImageView.setVisibility(View.VISIBLE);
            		mImageView.setImageBitmap(thumbBitmap);  
            	}
    		}
    	
    		mAsyncImageCacheMgr.downloadGalleryBitmap(imageUrl,
    				new AsyncImageCallBack(mImageView, 
    						mProgressBar,
    						mTextView,
    						imageUrl));
    	}
    	
    	
    }

    public void setUrl(String imageUrl){
    	Bitmap bitmap = mAsyncImageCacheMgr.getGalleryBitmap(imageUrl);
    	if(bitmap != null){
    		 mImageView.setImageBitmap(bitmap);
             mImageView.setVisibility(VISIBLE);
             mProgressBar.setVisibility(GONE);
             mTextView.setVisibility(GONE);
    	}else{
    		mAsyncImageCacheMgr.downloadGalleryBitmap(imageUrl,
    				new AsyncImageCallBack(mImageView, 
    						mProgressBar,
    						mTextView,
    						imageUrl));
    	}
//        new ImageLoadTask().execute(imageUrl);
    }
    
    class AsyncImageCallBack implements ImageCallBack{
    	private ImageView mGalleryIV;
    	private ProgressBar mDownloadPB;
    	private TextView mProgressTV;
    	
    	public AsyncImageCallBack(ImageView imageView,
    			ProgressBar progressBar,
    			TextView textView,
    			String url){
    		this.mGalleryIV = imageView;
    		this.mDownloadPB = progressBar;
    		this.mProgressTV = textView;
    		mGalleryIV.setTag(url);
    		mDownloadPB.setTag(url);
    		mProgressTV.setTag(url);
    	}
    	
		@Override
		public void onGetImage(Bitmap bitmap, String url) {
//			Log.d(TAG, "onGetImage url:"+url);
			if(mGalleryIV.getTag().equals(url)&&mDownloadPB.getTag().equals(url)){
				if(bitmap != null){
					mGalleryIV.setImageBitmap(bitmap);
				}else{
					mGalleryIV.setImageResource(R.drawable.ic_launcher);
				}
				mGalleryIV.setVisibility(VISIBLE);
				mDownloadPB.setVisibility(GONE);
				mProgressTV.setVisibility(GONE);
			}
		}

		@Override
		public void onGetError(String url) {
			if(mGalleryIV.getTag().equals(url)&&mDownloadPB.getTag().equals(url)){
//				Log.d(TAG, "onGetError url:"+url);
				mGalleryIV.setImageResource(R.drawable.ic_launcher);
				mGalleryIV.setVisibility(VISIBLE);
				mDownloadPB.setVisibility(GONE);
				mProgressTV.setVisibility(GONE);
			}
		}

		@Override
		public void onUpdateProgress(String url,int progress) {
			if(mProgressTV.getTag().equals(url)&&mDownloadPB.getTag().equals(url)){
//				Log.d(TAG, "onUpdateProgress progress:"+progress);
				mDownloadPB.setProgress(progress);
				mProgressTV.setText(progress+"%");
			}
			
		}
    	
    }
    
    //No caching load
    public class ImageLoadTask extends AsyncTask<String, Integer, Bitmap>
    {
        @Override
        protected Bitmap doInBackground(String... strings) {
            String url = strings[0];
            Bitmap bm = null;
            try {
                URL aURL = new URL(url);
                URLConnection conn = aURL.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bm;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mImageView.setImageBitmap(bitmap);
            mImageView.setVisibility(VISIBLE);
            mProgressBar.setVisibility(GONE);
        }
    }
}
