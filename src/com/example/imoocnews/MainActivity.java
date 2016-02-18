package com.example.imoocnews;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

public class MainActivity extends Activity {

	private ListView listView;
	private String URL = "http://www.imooc.com/api/teacher?type=4&num=30";
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.lv_main);
        new NewsAsyncTask().execute(URL);
    }
	
	class NewsAsyncTask extends AsyncTask<String, Void, List<NewsBean>>{

		@Override
		protected List<NewsBean> doInBackground(String... params) {
			return getJasonData(params[0]);
		}
		
		@Override
		protected void onPostExecute(List<NewsBean> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			NewsAdapter adapter = new NewsAdapter(MainActivity.this, result);
			listView.setAdapter(adapter);
		}
		
	}
/*
 * 解析JSON数据
 */
	public List<NewsBean> getJasonData(String url) {
		List<NewsBean> newsBeanList = new ArrayList<NewsBean>();
		try {
			String jsonString = readStream(new URL(url).openStream());
			Log.d("abc", jsonString);
			JSONObject jsonObject;
			NewsBean newsBean;
			try {
				jsonObject = new JSONObject(jsonString);
				JSONArray jsonArray = jsonObject.getJSONArray("data");
				for (int i = 0; i<jsonArray.length(); i++){
					jsonObject = jsonArray.getJSONObject(i);
					newsBean = new NewsBean();
					newsBean.newsIconUrl = jsonObject.getString("picSmall");
					newsBean.newsTitle = jsonObject.getString("name");
					newsBean.newsContent = jsonObject.getString("description");
					newsBeanList.add(newsBean);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		

			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return newsBeanList;
	}
	/*
	 *读取输入流：
	 */
	public String readStream(InputStream is){
		String result = "";
		try {
			InputStreamReader isr = new InputStreamReader(is,"utf-8");
			BufferedReader br = new BufferedReader(isr);
			String line = "";
			while ((line = br.readLine())!=null){
				result+=line;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
}
//http://www.imooc.com/api/teacher?type=4&num=30