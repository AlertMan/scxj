package com.scxj.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.R.color;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.scxj.R;
import com.scxj.adapter.ListAdapter;
import com.scxj.dao.AssetLineDao;
import com.scxj.dao.TaskDao;
import com.scxj.model.TB_LINE;
import com.scxj.model.TB_TASK;
import com.scxj.model.TB_TOWER;
/*
 *台帐查看
 * */
public class FragmentAssetMain extends BaseFragment{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) 
		{
            return null;
        }
		this.mFragmentManager = getChildFragmentManager();
	    View layout = inflater.inflate(R.layout.asset, container, false); 
		
		return layout;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		ListView assetList=(ListView)getView().findViewById(R.id.asset_list);
		TextView assetItme4 = (TextView) getView().findViewById(R.id.asset_list_item4);
		assetItme4.setText("查看地图");
		List <TB_LINE> lineList=new ArrayList<TB_LINE>();
		lineList=new AssetLineDao(getActivity()).getAllList();
		if(lineList!=null && lineList.size()>0){
			assetList.setAdapter(new AssetAdapter(getActivity(), lineList));
		}
		
	}
	
	public class AssetAdapter extends ListAdapter {
		public AssetAdapter(Context context, List<? extends Serializable> datas ) {
			super(context, datas);
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.asset_list_title, null);
				holder = new ViewHolder();
				/** 得到各个控件的对象 */
				holder.assetItme1 = (TextView) convertView
						.findViewById(R.id.asset_list_item1);
				holder.assetItme2 = (TextView) convertView
						.findViewById(R.id.asset_list_item2);
				holder.assetItme3 = (TextView) convertView
						.findViewById(R.id.asset_list_item3);
				holder.assetItme4 = (TextView) convertView
						.findViewById(R.id.asset_list_item4);	
				holder.assetItme5 = (ImageView) convertView
				.findViewById(R.id.asset_list_item5);
				int color = mContext.getResources().getColor(R.color.textcolor);
				holder.assetItme1.setTextColor(color);
				holder.assetItme2.setTextColor(color);
				holder.assetItme3.setTextColor(color);
				holder.assetItme4.setVisibility(View.GONE);
				holder.assetItme5.setVisibility(View.VISIBLE);
			convertView.setTag(holder);// 绑定ViewHolder对象
			} else {
				holder = (ViewHolder) convertView.getTag();// 取出ViewHolder对象
			}

			setListViewBackGround(convertView,position);

			TB_LINE item = (TB_LINE) datas.get(position);
			holder.assetItme1.setText(position + 1 + "");
			holder.assetItme2.setText(item.getLINENAME());
			//查看详细
			holder.assetItme2.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					TB_LINE item = (TB_LINE) datas.get(position);
					Bundle b = new Bundle();
					b.putSerializable("item", item);
					FragmentLineInfo bf = new FragmentLineInfo();
					bf.setArguments(b);
					
					FragmentTransaction fTransaction = mFragmentManager
							.beginTransaction();
					fTransaction.replace(R.id.asset_layout, bf);
					fTransaction.addToBackStack(null);
					fTransaction.commit();
				}
			});
			
			TaskDao taskDao = new TaskDao(mContext);
		 	TB_TOWER startTower = taskDao.getTowerByTowerId(item.getSTARTTOWERID());
		 	TB_TOWER endTower = taskDao.getTowerByTowerId( item.getENDTOWERID());
			String tv = startTower.getTOWERNAME() + "#-" + endTower.getTOWERNAME() + "#";
			holder.assetItme3.setText(tv);
			
			holder.assetItme3.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 查看杆塔列表
					TB_LINE item = (TB_LINE) datas.get(position);
					Bundle b = new Bundle();
					b.putSerializable("item", item);
					FragmentTower bf = new FragmentTower();
					bf.setArguments(b);
					
					
					FragmentTransaction fTransaction = mFragmentManager
							.beginTransaction();
					fTransaction.replace(R.id.asset_layout, bf);
					fTransaction.addToBackStack(null);
					fTransaction.commit();
				}
			});
			//查看地图
			holder.assetItme5.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 查看地图
					TB_LINE item = (TB_LINE) datas.get(position);
					Bundle b = new Bundle();
					b.putSerializable("item", item);
					FragmentLineMapMain bf = new FragmentLineMapMain();
					bf.setArguments(b);
					FragmentTransaction fTransaction = mFragmentManager
							.beginTransaction();
					fTransaction.replace(R.id.asset_layout, bf);
					fTransaction.addToBackStack(null);
					fTransaction.commit();

				}
			});

			return convertView;
		}

		/** 存放控件 */
		public final class ViewHolder {
			public TextView assetItme1;
			public TextView assetItme2;
			public TextView assetItme3;
			public TextView assetItme4;
			public ImageView assetItme5;
		}

	}

}
