package com.example.imoocnews;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;

public class ImageLoad {

	private ImageView mImageView;
	private String mUrl;
	private LruCache<String, Bitmap> mCache;
	
	private ListView mListView;
	private Set<NewsAsyncTask> mTask;
	

	public ImageLoad(ListView listView) {
		mListView = listView;
		mTask = new HashSet<ImageLoad.NewsAsyncTask>();
		// 获取最大可用内存，初始化mCache
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 4;
		mCache = new LruCache<String, Bitmap>(cacheSize) {
			protected int sizeOf(String key, Bitmap value) {
				// 在存入缓存是调用
				return value.getByteCount();
			}
		};
	}

	// 增加到缓存
	public void addBitmapToCache(String url, Bitmap bitmap) {

		if (mCache.get(url) == null) {
			mCache.put(url, bitmap);
		}
	}

	// 从缓存中获取bitmap
	public Bitmap getBitmap(String url) {
		return mCache.get(url);
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (mImageView.getTag().equals(mUrl)) {
				mImageView.setImageBitmap((Bitmap) msg.obj);
			}
			// mImageView.setImageBitmap((Bitmap) msg.obj);
		}

	};

	public void showImageByThread(ImageView imageView, final String url) {

		mImageView = imageView;
		mUrl = url;

		new Thread() {

			public void run() {
				super.run();
				Bitmap bitmap = getBitmapFromURL(url);
				Message message = Message.obtain();
				message.obj = bitmap;
				mHandler.sendMessage(message);
			}
		}.start();
	}

	public Bitmap getBitmapFromURL(String urlString) {
		Bitmap bitmap;
		InputStream is = null;
		try {
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			is = new BufferedInputStream(connection.getInputStream());
			bitmap = BitmapFactory.decodeStream(is);
			connection.disconnect();
			return bitmap;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public void showImageByAsyncTask(ImageView imageView, String url) {
		// 从缓存中取出图片，如果缓存中没有，则从网络中加载、
		Bitmap bitmap = getBitmap(url);
		if (bitmap == null) {
			imageView.setImageResource(R.drawable.ic_launcher);
		} else {
			imageView.setImageBitmap(bitmap);
		}
	}
//	用来加载从start到end的所有图片
	public void loadImages(int start, int end) {
		for (int i = start; i <end; i++) {
			String url = NewsAdapter.URLS[i];
//			从缓存中寻找对应的图片
			Bitmap bitmap = getBitmap(url);
//			如果缓存中没有，则去下载
			if (bitmap == null) {
				NewsAsyncTask task = new NewsAsyncTask(url);
				task.execute(url);
				mTask.add(task);
			} else {
				ImageView imageView = (ImageView) mListView.findViewWithTag(url);
				imageView.setImageBitmap(bitmap);
			}
		}
	}

	private class NewsAsyncTask extends AsyncTask<String, Void, Bitmap> {

		private String mUrl;
//		private ImageView mImageView;

		public NewsAsyncTask(String url) {
			mUrl = url;
//			mImageView = imageView;
		}

		@Override
		protected Bitmap doInBackground(String... arg0) {
			String url = arg0[0];
			Bitmap bitmap = getBitmapFromURL(url);
			// 从网络中获取图片后 存入到缓存中、
			if (bitmap != null) {
				addBitmapToCache(url, bitmap);
			}
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
//			if (mImageView.getTag().equals(mUrl)) {
//				mImageView.setImageBitmap(result);
//			}
			ImageView imageView = (ImageView) mListView.findViewWithTag(mUrl);
			if (imageView!=null&&result!=null){
				imageView.setImageBitmap(result);
			}
		}
	}

	public void cancelAllTasks() {
		if(mTask!=null){
			for(NewsAsyncTask task:mTask){
				task.cancel(false);
			}
		}
	}
}
