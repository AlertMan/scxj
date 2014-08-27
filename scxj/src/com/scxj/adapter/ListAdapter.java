package com.scxj.adapter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import com.scxj.R;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;

/**
 * 列表数据适配器基类
 * 
 * 
 */
public abstract class ListAdapter extends BaseAdapter {

	protected LayoutInflater mInflater;
	protected List<? extends Serializable> datas;
	protected Context mContext;
	public HashMap<Integer, View> views = new HashMap<Integer, View>();
	protected boolean refresh = false;
	protected int selIndex = -1;
	
	public void setDatas(List<? extends Serializable> datas) {
		this.datas = datas;
	}

	public void setSelIndex(int selIndex) {
		this.selIndex = selIndex;
	}

	public ListAdapter(Context context, List<? extends Serializable> datas) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		this.datas = datas;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return datas.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return datas.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	public void refreshDatas(List<? extends Serializable> datas) {
		this.refresh = true;
		this.datas = datas;
	}

/*	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}*/

	/**
	 * 圆角风格
	 * @param position
	 * @param count
	 * @param view
	 */
	public void setIOSListItemBg(int position, int count, View view) {
		if (count > 1) {
			if (position == 0) {
				view.setBackgroundResource(R.drawable.item_bg_selector_head);
			} else if (position == count - 1) {
				view.setBackgroundResource(R.drawable.item_bg_selector_foot);
			} else {
				view.setBackgroundResource(R.drawable.item_bg_selector_middle);
			}
		} else {
			view.setBackgroundResource(R.drawable.item_bg_selector);
		}

	}
}
