package com.xcj.android.touchgallery.asyncdownload;

import java.io.File;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.xcj.android.net.download.image.ImageCacheMgr;

public class AsyncImageCacheMgr extends ImageCacheMgr {

	private static final String SDCARD_IMAGE_PATH = "/GalleryCache";
	private static final String GALLERY_DIR= "/gallery";
	private static AsyncImageCacheMgr mInstance;
	private Context mContext;
	
	public static AsyncImageCacheMgr getInstance(Context context){
		if(mInstance == null){
			mInstance = new AsyncImageCacheMgr(context);
		}
		return mInstance;
	}
	
	private AsyncImageCacheMgr(Context context) {
		super(context);
		this.mContext = context;
	}
	
	
	public Bitmap getGalleryBitmap(String url) {
		return getImage(url, GALLERY_DIR);
	}


	public void downloadGalleryBitmap(String url, ImageCallBack callback) {
		doDownloadImage(url, GALLERY_DIR, callback);
	}

	public Bitmap getImage(String url, String folder) {
		if (url.startsWith("local://")) {  //程序图片
			int index = url.indexOf(".");
			String filename = url.substring("local://".length(), index);
			Resources res = mContext.getResources();
			int id = res.getIdentifier(filename, "drawable",mContext.getPackageName());
			if (id != 0) {
				return BitmapFactory.decodeResource(res, id);
			} else {
				return null;
			}
		} else if(url.startsWith("/mnt")
				|| url.startsWith("/sdcard")
				|| url.startsWith("/data/data")){  //本地图片
			File file = new File(url);
			if (file.exists()) {
				return BitmapFactory.decodeFile(file.getPath());
			}
			return null;
		}else {			//网络图片
			return super.getImage(url, folder);
		}
	}

	@Override
	public int getMaxCacheSize() {
		return 3;  //返回0不需要缓存
	}

	@Override
	public String getSdcardImagePath() {
		return SDCARD_IMAGE_PATH;
	}

}
