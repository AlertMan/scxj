package com.scxj.fragment;

import java.io.Serializable;
import java.util.List;

import android.R.color;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.scxj.MyApplication;
import com.scxj.R;
import com.scxj.adapter.ListAdapter;
import com.scxj.dao.TaskDao;
import com.scxj.model.TB_TASK;
import com.scxj.model.TB_TOWER;
import com.scxj.utils.Const;
import com.scxj.utils.StringUtils;
import com.scxj.utils.TextUtil;
/*
 * 任务杆塔
 * */
public class FragmentTaskMainTowers extends BaseFragment {
	private TB_TASK task;
	TaskMainTowerAdapter adapter;
	private List<TB_TOWER> datas;
	private ListView lv;
	private  OnDataRefreshListener onPreDataRefresh;
	public void setOnPreDataRefresh(OnDataRefreshListener onPreDataRefresh) {
		this.onPreDataRefresh = onPreDataRefresh;
	}

	private  OnDataRefreshListener onDataRefreshListener = new OnDataRefreshListener() {
		
		@Override
		public void refreshDatas() {
			datas = new TaskDao(getActivity()).getTowersListByLineId(task.getTASKID(),task.getLine().getLINEID());
			if(adapter == null){
				adapter = new TaskMainTowerAdapter(getActivity(), datas);
				lv.setAdapter(adapter);
				
			}else{
				adapter.refreshDatas(datas);
				adapter.notifyDataSetChanged();
			}
			
			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					FragmentTaskMainTowerCheck bf = new FragmentTaskMainTowerCheck();
					Bundle b = new Bundle();
					b.putSerializable("tower", datas.get(arg2));
					b.putSerializable("task", task);
					bf.setArguments(b);
					bf.setOnDataRefreshListener(onDataRefreshListener);
					FragmentTransaction fTransaction = getFragmentManager().beginTransaction();
					fTransaction.replace(R.id.task_main_tower_layout, bf);
					fTransaction.addToBackStack(null);
					fTransaction.commit();
				}
			});
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) 
		{
            return null;
        }
		
	    View layout = inflater.inflate(R.layout.task_main_tower, container, false); 
		
		return layout;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		String userName = MyApplication.getInstance().loginUser.getUSERNAME();
		task = (TB_TASK) getArguments().getSerializable("item");
		Const.DB_NAME.setUserTaskDirectory(userName, task.getTASKID(), false, 0);
		lv= (ListView) getView().findViewById(R.id.task_main_tower_lv);
		onDataRefreshListener.refreshDatas();
		TextView tv01 = (TextView)getView().findViewById(R.id.taskName);
		TextView tv02 = (TextView)getView().findViewById(R.id.taskDemand);
		tv01.setText("任务名称：" + task.getTASKNAME());
		tv02.setText("任务要求：" + task.getTASKDEMAND());
		
	}

	
	@Override
	public void onDestroyView() {
		if(onPreDataRefresh != null){
			onPreDataRefresh.refreshDatas();
		}
		super.onDestroyView();
	}
	
	 class TaskMainTowerAdapter extends ListAdapter {
		 
		public TaskMainTowerAdapter(Context context, List<? extends Serializable> datas) {
			super(context, datas);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vh;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.task_main_tower_item, null);
				vh = new ViewHolder();
				vh.tv01 = (TextView) convertView.findViewById(R.id.task_main_tower_item1);
				vh.tv02 = (TextView) convertView.findViewById(R.id.task_main_tower_item2);
				vh.tv03 = (TextView) convertView.findViewById(R.id.task_main_tower_item3);
				vh.tv04 = (TextView) convertView.findViewById(R.id.task_main_tower_item4);
				int color = mContext.getResources().getColor(R.color.textcolor);
				vh.tv01.setTextColor(color);
				vh.tv02.setTextColor(color);
				vh.tv03.setTextColor(color);
				vh.tv04.setTextColor(color);
			
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			setListViewBackGround(convertView,position);
			
			final TB_TOWER item = (TB_TOWER) datas.get(position);
			vh.tv01.setText(item.getTOWERID());
			vh.tv02.setText(item.getTOWERNAME());
			//默认值：未巡视
			if(StringUtils.isNull(item.getSTATUS())){
				item.setSTATUS("未巡视");
			}
			vh.tv03.setText(item.getSTATUS()); 
			vh.tv04.setText(TextUtil.highlightKeywords(item.getCHECKDETAIL(), "\\d+",Color.RED));
			
			return convertView;
		}

	}
	 
	 static  class ViewHolder {
			public  TextView tv01;
			public  TextView tv02;
			public  TextView tv03;
			public  TextView tv04;
		}

}
