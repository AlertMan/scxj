package com.scxj.adapter;

import java.io.Serializable;
import java.util.List;

import com.scxj.R;
import com.scxj.model.TB_TASK;
import com.scxj.model.TB_TASK_DEFECT;


import android.R.color;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageListAdapter extends ListAdapter{
    private boolean isVisible=false;
	private List list=null;
	public ImageListAdapter(Context context, List<? extends Serializable> datas,boolean isVisible) {
		super(context, datas);
		this.list=datas;
		this.isVisible=isVisible;
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.image_list_item, null);
			holder = new ViewHolder();
			/** 得到各个控件的对象 */
			holder.number = (TextView) convertView
					.findViewById(R.id.image_polling_row1);
			holder.task = (TextView) convertView
					.findViewById(R.id.image_polling_row2);
			holder.lineName = (TextView) convertView
					.findViewById(R.id.image_polling_row3);
			holder.tower = (TextView) convertView
					.findViewById(R.id.image_polling_row4);
			holder.pic = (ImageView) convertView
					.findViewById(R.id.image_polling_row5);

			convertView.setTag(holder);// 绑定ViewHolder对象
		} else {
			holder = (ViewHolder) convertView.getTag();// 取出ViewHolder对象
		}
		
		if (position%2==0) {
			convertView.setBackgroundResource(color.transparent);
		}else{
			convertView.setBackgroundResource(color.holo_blue_light);
		}

		if(isVisible){
			holder.task.setVisibility(View.VISIBLE);
			TB_TASK item = (TB_TASK) datas.get(position);
			holder.number.setText(item.getTASKID());
			holder.task.setText(item.getTASKNAME());
			holder.lineName.setText(item.getLine().getLINENAME());
			//杆塔名称
			String towerDesc = item.getLine().getSTARTTOWERID() + "#-" + item.getLine().getENDTOWERID()+"#";
			holder.tower.setText(towerDesc);//启始杆塔号与结束杆塔号
			//图片
			holder.pic.setBackgroundResource(R.drawable.bottom_bar_icon_me_normal);
			
		}else{
			holder.task.setVisibility(View.GONE);
			TB_TASK_DEFECT item = (TB_TASK_DEFECT) datas.get(position);
			holder.number.setText(item.getDEFECTTASKID());
			holder.task.setText(item.getDEFECTTASKNAME());
			holder.lineName.setText(item.getLINENAME());
			//杆塔名称
			String towerDesc = item.getTOWERNAME() + "#-#";
			holder.tower.setText(towerDesc);//启始杆塔号与结束杆塔号
			//图片
			holder.pic.setBackgroundResource(R.drawable.bottom_bar_icon_me_normal);
			
		}
	

		return convertView;
	}

	/** 存放控件 */
	public final class ViewHolder {
		public TextView number;
		public TextView task;
		public TextView lineName;
		public TextView tower;
		public ImageView pic;
	}

}

