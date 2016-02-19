package com.example.imoocnews;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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

	public ImageLoad() {
		// ��ȡ�������ڴ棬��ʼ��mCache
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 4;
		mCache = new LruCache<String, Bitmap>(cacheSize) {
			protected int sizeOf(String key, Bitmap value) {
				// �ڴ��뻺���ǵ���
				return value.getByteCount();
			}
		};
	}

	// ���ӵ�����
	public void addBitmapToCache(String url, Bitmap bitmap) {

		if (mCache.get(url) == null) {
			mCache.put(url, bitmap);
		}
	}

	// �ӻ����л�ȡbitmap
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
// �ӻ�����ȡ��ͼƬ�����������û�У���������м��ء�
		if (mCache.get(url) == null) {
			new NewsAsyncTask(imageView, url).execute(url);
		}else {
			imageView.setImageBitmap(mCache.get(url));
		}
	}

	class NewsAsyncTask extends AsyncTask<String, Void, Bitmap> {

		private String mUrl;
		private ImageView mImageView;

		public NewsAsyncTask(ImageView imageView, String url) {
			mUrl = url;
			mImageView = imageView;
		}

		@Override
		protected Bitmap doInBackground(String... arg0) {
			String url = arg0[0];
			Bitmap bitmap = getBitmapFromURL(url);
//			�������л�ȡͼƬ�� ���뵽�����С�
			if (bitmap != null) {
				addBitmapToCache(url, bitmap);
			}
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (mImageView.getTag().equals(mUrl)) {
				mImageView.setImageBitmap(result);
			}
		}
	}
}
