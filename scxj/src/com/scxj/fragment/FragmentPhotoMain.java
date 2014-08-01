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
import com.scxj.dao.RetDefectDao;
import com.scxj.dao.RetTaskDao;
import com.scxj.dao.TaskDefectDao;
import com.scxj.model.TB_TASK_DEFECT;
import com.scxj.model.TB_TASK_RET;

/*
 * 巡视与消缺任务照片查看
 * */
public class FragmentPhotoMain extends BaseFragment implements OnClickListener {
	private List<TB_TASK_RET> taskList = new ArrayList<TB_TASK_RET>();
	private List<TB_TASK_DEFECT> taskDefectList = new ArrayList<TB_TASK_DEFECT>();
	private ListView listView = null;
	private PhotoMainAdapter adapter;
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
		View layout = inflater.inflate(R.layout.image, container, false);

		return layout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		listView = (ListView) getView().findViewById(R.id.image_list);
		userName = MyApplication.getInstance().loginUser.getUSERNAME();
		// 给LIST填充数据
		taskList = new RetTaskDao(getActivity()).getAllList(userName);
		adapter = new PhotoMainAdapter(getActivity(), taskList, true);
		listView.setAdapter(adapter);
		
		inspection = (Button) getView()
				.findViewById(R.id.inspection_btn);
		inspection.setOnClickListener(this);
		defect = (Button) getView().findViewById(R.id.defect_btn);
		defect.setOnClickListener(this);
		inspection.setSelected(true);

		taskDefectList = new RetDefectDao(getActivity()).getAllList(userName);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.inspection_btn:
			getView().findViewById(R.id.image_list_title2).setVisibility(View.VISIBLE);
			// 添加不同的数据源
			taskList = new RetTaskDao(getActivity()).getAllList(userName);
			inspection.setSelected(true);
			defect.setSelected(false);
			adapter.setDatas(taskList);
			adapter.setVisible(true);
			adapter.notifyDataSetChanged();

			break;

		case R.id.defect_btn:
			getView().findViewById(R.id.image_list_title2).setVisibility(View.GONE);
			// 添加不同的数据源
			taskDefectList = new RetDefectDao(getActivity()).getAllList(userName);
			inspection.setSelected(false);
			defect.setSelected(true);
			adapter.setDatas(taskDefectList);
			adapter.setVisible(false);
			adapter.notifyDataSetChanged();
			break;

		default:
			break;
		}

	}

	public class PhotoMainAdapter extends ListAdapter {
		private boolean isVisible = false;

		public void setVisible(boolean isVisible) {
			this.isVisible = isVisible;
		}

		public PhotoMainAdapter(Context context,
				List<? extends Serializable> datas, boolean isVisible) {
			super(context, datas);
			this.isVisible = isVisible;

		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.image_list_item, null);
				holder = new ViewHolder();
				/** 得到各个控件的对象 */
				holder.tv01 = (TextView) convertView
						.findViewById(R.id.image_polling_row1);
				holder.tv02 = (TextView) convertView
						.findViewById(R.id.image_polling_row2);
				holder.tv03 = (TextView) convertView
						.findViewById(R.id.image_polling_row3);
				holder.tv04 = (TextView) convertView
						.findViewById(R.id.image_polling_row4);
				holder.im05 = (ImageView) convertView
						.findViewById(R.id.image_polling_row5);

				convertView.setTag(holder);// 绑定ViewHolder对象
			} else {
				holder = (ViewHolder) convertView.getTag();// 取出ViewHolder对象
			}

			setListViewBackGround(convertView,position);

			if (isVisible) {
				holder.tv02.setVisibility(View.VISIBLE);
				TB_TASK_RET item = (TB_TASK_RET) datas.get(position);
				holder.tv01.setText(position + 1 + "");
				holder.tv02.setText(item.getTASKNAME());
				holder.tv03.setText(item.getLINENAME());
				holder.tv04.setText(item.getTOWERNAME());// 启始杆塔号与结束杆塔号
				 

			} else {
				holder.tv02.setVisibility(View.GONE);
				TB_TASK_DEFECT item = (TB_TASK_DEFECT) datas.get(position);
				holder.tv01.setText(position + 1 + "");
				holder.tv03.setText(item.getLINENAME());
				holder.tv04.setText(item.getTOWERNAME());// 启始杆塔号与结束杆塔号

			}
			
			holder.im05.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					FragmentShowPictures bf;
					Bundle b = new Bundle();
					
					if (isVisible) {
						bf = new FragmentShowPictures();
						b.putSerializable("taskRet", datas.get(position));
						b.putSerializable("isTask", true);
					} else {
						bf = new FragmentShowPictures();
						b.putSerializable("taskDefect", datas.get(position));
						b.putSerializable("isTask", false);
					}
					
					bf.setArguments(b);
					
					
					FragmentTransaction fTransaction = mFragmentManager.beginTransaction();
					fTransaction.replace(R.id.image_layout, bf);
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
			public ImageView im05;
		}

	}
}
