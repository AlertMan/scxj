package com.scxj.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.R.color;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.scxj.MyApplication;
import com.scxj.R;
import com.scxj.adapter.ListAdapter;
import com.scxj.dao.TrackDefectDao;
import com.scxj.dao.TrackTaskDao;
import com.scxj.model.TB_TRACK;

/*
 * 轨迹查询
 * */
public class FragmentTrackMain extends BaseFragment implements OnClickListener {

	private List<TB_TRACK> trackList = new ArrayList<TB_TRACK>();
	private List<TB_TRACK> trackDefectList = new ArrayList<TB_TRACK>();
	private ListView listView = null;
	private TrackAdapter adapter;
	private boolean isTaskTrack = true;
	private Button inspection;
	private Button defect;
	private String userName;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}
		this.mFragmentManager = getChildFragmentManager();
		View layout = inflater.inflate(R.layout.track, container, false);

		return layout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		
		inspection = (Button) getView().findViewById(
				R.id.inspection_track_btn);
		defect = (Button) getView().findViewById(R.id.defect_track_btn);
		inspection.setSelected(true);
		inspection.setOnClickListener(this);
		defect.setOnClickListener(this);
		
		userName = MyApplication.getInstance().loginUser.getUSERNAME();
		listView = (ListView) getView().findViewById(R.id.track_listView);
		trackList = new TrackTaskDao(getActivity()).getAllTrackListByUserName(userName);
		adapter = new TrackAdapter(getActivity(), trackList);
		listView.setAdapter(adapter);
		isTaskTrack = true;
		trackDefectList = new TrackDefectDao(getActivity()).getAllTrackListByUserId(userName);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.inspection_track_btn:// 加载巡视轨迹
//			if (trackList.isEmpty()) {
				trackList = new TrackTaskDao(getActivity()).getAllTrackListByUserName(userName);
//			}
			inspection.setSelected(true);
			defect.setSelected(false);
			isTaskTrack = true;
			adapter.setDatas(trackList);
			adapter.notifyDataSetChanged();
			break;
		case R.id.defect_track_btn:// 加载消缺轨迹
//			if (trackDefectList.isEmpty()) {
				trackDefectList = new TrackDefectDao(getActivity())
						.getAllTrackListByUserId(userName);
//			}
			inspection.setSelected(false);
			defect.setSelected(true);
			isTaskTrack = false;
			adapter.setDatas(trackDefectList);
			adapter.notifyDataSetChanged();
			break;
		default:
			break;
		}

	}

	public class TrackAdapter extends ListAdapter {
		public TrackAdapter(Context context, List<? extends Serializable> datas) {
			super(context, datas);

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.track_main, null);
				holder = new ViewHolder();
				/** 得到各个控件的对象 */
				holder.tv01 = (TextView) convertView
						.findViewById(R.id.list_title1);
				holder.tv02 = (TextView) convertView
						.findViewById(R.id.list_title2);
				holder.tv03 = (TextView) convertView
						.findViewById(R.id.list_title3);
				holder.tv04 = (TextView) convertView
						.findViewById(R.id.list_title4);	
				holder.tv06 = (TextView) convertView
						.findViewById(R.id.list_title6);
				int color = mContext.getResources().getColor(R.color.textcolor);
				holder.tv01.setTextColor(color);
				holder.tv02.setTextColor(color);
				holder.tv03.setTextColor(color);
				holder.tv04.setTextColor(color);
				holder.im05 = (ImageView) convertView
						.findViewById(R.id.list_title5);
				holder.im05.setVisibility(View.VISIBLE);
				holder.tv06.setVisibility(View.GONE);
				convertView.setTag(holder);// 绑定ViewHolder对象
			} else {
				holder = (ViewHolder) convertView.getTag();// 取出ViewHolder对象
			}

			setListViewBackGround(convertView,position);
			final TB_TRACK item = (TB_TRACK) datas.get(position);
			holder.tv01.setText(position + 1 + "");
			holder.tv02.setText(item.getTASKNAME());
			holder.tv03.setText(item.getSTARTADDRESS());
			holder.tv04.setText(item.getENDADDRESS());

			holder.im05.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					FragmentTrackMapMain bf = new FragmentTrackMapMain();
					Bundle b = new Bundle();
					b.putBoolean("isTaskTrack", isTaskTrack);
					b.putString("TASKNAME", item.getTASKNAME());
					b.putString("TASKID", item.getTASKID());
					bf.setArguments(b);

					FragmentTransaction fTransaction = mFragmentManager
							.beginTransaction();
					fTransaction.replace(R.id.track_main_layout, bf);
					fTransaction.addToBackStack(null);
					fTransaction.commit();
				}
			});

			return convertView;
		}

		/** 存放控件 */
		public final class ViewHolder {
			public TextView tv01;
			public TextView tv02;
			public TextView tv03;
			public TextView tv04;
			public TextView tv06;
			public ImageView im05;
		}

	}
}