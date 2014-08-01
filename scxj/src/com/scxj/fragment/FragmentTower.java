package com.scxj.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.scxj.R;
import com.scxj.adapter.TowerAdapter;
import com.scxj.dao.AssetTowerDao;
import com.scxj.model.TB_LINE;
import com.scxj.model.TB_TOWER;

public class FragmentTower extends BaseFragment implements OnItemClickListener {

	private TB_LINE line;
	private List <TB_TOWER> towers;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) 
		{
            return null;
        }
		View layout = inflater.inflate(R.layout.tower, container, false); 
		return layout;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		line = (TB_LINE)getArguments().getSerializable("item");
		
		ListView assetList=(ListView)getView().findViewById(R.id.tower_list);
		assetList.setOnItemClickListener(this);
		
		towers=new ArrayList<TB_TOWER>();
		towers=new AssetTowerDao(getActivity()).getAllList(line.getLINEID());
		if(towers!=null && towers.size()>0){
			assetList.setAdapter(new TowerAdapter(getActivity(), towers));	
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		TB_TOWER item = (TB_TOWER) towers.get(arg2);
		Bundle b = new Bundle();
		b.putSerializable("item", item);
		FragmentTowerInfo bf = new FragmentTowerInfo();
		bf.setArguments(b);
		
		FragmentTransaction fTransaction = getFragmentManager()
				.beginTransaction();
		fTransaction.replace(R.id.tower_layout, bf);
		fTransaction.addToBackStack(null);
		fTransaction.commit();
		
	}

}