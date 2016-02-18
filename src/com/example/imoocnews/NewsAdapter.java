package com.example.imoocnews;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsAdapter extends BaseAdapter {

	private List<NewsBean> mList;
	private LayoutInflater mInflater;
	
	public NewsAdapter(Context context, List<NewsBean> mList){
		this.mList = mList;
		mInflater = LayoutInflater.from(context);
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
		new ImageLoad().showImageByAsyncTask(viewHolder.tvIcon, url);
		viewHolder.tvTitle.setText(mList.get(arg0).newsTitle);
//		viewHolder.tvTitle.setText(mList.get(arg0).newsIconUrl);
		viewHolder.tvContent.setText(mList.get(arg0).newsContent);
		return convertView;
	}
	
	class ViewHolder {
		TextView tvTitle,tvContent;
		ImageView tvIcon;
	}

}
