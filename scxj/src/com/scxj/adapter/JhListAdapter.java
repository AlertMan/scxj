package com.scxj.adapter;

import java.io.Serializable;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scxj.R;
import com.scxj.model.TB_USER;

//自定义适配器Adapter
public class JhListAdapter extends ListAdapter {
	
	public JhListAdapter(Context context, List<? extends Serializable> datas) {
		super(context, datas);
	}
 
	private int[] colors = new int[] { 0xffffffff, 0xffececec };


	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			// 下拉项布局
			convertView = mInflater.inflate(
					R.layout.loginlist_item, null);
			holder.textView = (TextView) convertView
					.findViewById(R.id.login_list_item);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		TB_USER item = (TB_USER)datas.get(position);
		
		holder.textView.setText(item.getGROUPNAME());
		// ListView中隔行设置不同的颜色值
		int colorPos = position % colors.length;
		convertView.setBackgroundColor(colors[colorPos]);

		return convertView;
	}

}

class ViewHolder {
	TextView textView;
}
