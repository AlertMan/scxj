package com.scxj.adapter;

import java.io.Serializable;
import java.util.List;

import android.R.color;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scxj.R;
import com.scxj.fragment.BaseFragment;
import com.scxj.fragment.FragmentTower;
import com.scxj.model.TB_LINE;
import com.scxj.model.TB_TOWER;

public class TowerAdapter extends ListAdapter {

	public TowerAdapter(Context context, List<? extends Serializable> datas) {
		super(context, datas);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.tower_list_title, null);
			holder = new ViewHolder();
			/** 得到各个控件的对象 */
			holder.assetItme1 = (TextView) convertView
					.findViewById(R.id.tower_list_item1);
			holder.assetItme2 = (TextView) convertView
					.findViewById(R.id.tower_list_item2);
			holder.assetItme3 = (TextView) convertView
					.findViewById(R.id.tower_list_item3);
			holder.assetItme4 = (TextView) convertView
					.findViewById(R.id.tower_list_item4);
			
			int color = mContext.getResources().getColor(R.color.textcolor);
			holder.assetItme1.setTextColor(color);
			holder.assetItme2.setTextColor(color);
			holder.assetItme3.setTextColor(color);
			holder.assetItme4.setTextColor(color);
			convertView.setTag(holder);// 绑定ViewHolder对象
		} else {
			holder = (ViewHolder) convertView.getTag();// 取出ViewHolder对象
		}

		if (position % 2 == 0) {
			convertView.setBackgroundResource(color.transparent);
		} else {
			convertView.setBackgroundResource(color.holo_blue_light);
		}

		TB_TOWER item = (TB_TOWER)getItem(position);
		holder.assetItme1.setText(position + 1 + "");
		holder.assetItme2.setText(item.getTOWERNAME());
		holder.assetItme3.setText(item.getTOWERMATERIAL());
		holder.assetItme4.setText(item.getTOWERTYPE());
		

		return convertView;
	}

	/** 存放控件 */
	public final class ViewHolder {
		public TextView assetItme1;
		public TextView assetItme2;
		public TextView assetItme3;
		public TextView assetItme4;
	}

}

