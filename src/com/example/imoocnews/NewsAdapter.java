package com.example.imoocnews;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class NewsAdapter extends BaseAdapter implements OnScrollListener{

	private List<NewsBean> mList;
	private LayoutInflater mInflater;
	private ImageLoad mImageLoad;
	private int mStart,mEnd;
	public static String[] URLS;
	private boolean mFirstIn;
	
	public NewsAdapter(Context context, List<NewsBean> mList,ListView listView){
		this.mList = mList;
		mFirstIn = true;
		mInflater = LayoutInflater.from(context);
		mImageLoad = new ImageLoad(listView);
		URLS = new String[mList.size()];
		for (int i=0;i<mList.size();i++){
			URLS[i] = mList.get(i).newsIconUrl;
		}
		listView.setOnScrollListener(this);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		ViewHolder viewHolder = new ViewHolder();
		if (convertView == null){
			convertView = mInflater.inflate(R.layout.item_layout, null);
			viewHolder.tvIcon = (ImageView) convertView.findViewById(R.id.image);
			viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.title);
			viewHolder.tvContent = (TextView) convertView.findViewById(R.id.content);
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.tvIcon.setImageResource(R.drawable.ic_launcher);
		String url = mList.get(arg0).newsIconUrl;
		viewHolder.tvIcon.setTag(url);
//		new ImageLoad().showImageByThread(viewHolder.tvIcon, url);
		mImageLoad.showImageByAsyncTask(viewHolder.tvIcon, url);
		viewHolder.tvTitle.setText(mList.get(arg0).newsTitle);
//		viewHolder.tvTitle.setText(mList.get(arg0).newsIconUrl);
		viewHolder.tvContent.setText(mList.get(arg0).newsContent);
		return convertView;
	}
	
	class ViewHolder {
		TextView tvTitle,tvContent;
		ImageView tvIcon;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisiableItem, int visiableItemCount, int totalItem) {
		mStart = firstVisiableItem;
		mEnd = mStart + visiableItemCount;
//		首次加载调用
		if(mFirstIn&&visiableItemCount>0){
			mImageLoad.loadImages(mStart, mEnd);
			mFirstIn = false;
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if(scrollState == SCROLL_STATE_IDLE){
//			加载可见性
			mImageLoad.loadImages(mStart, mEnd);
		} else {
//			停止所有的ansyncTask;
			mImageLoad.cancelAllTasks();
		}
	}

}
