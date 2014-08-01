package com.scxj.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.scxj.R;
import com.scxj.adapter.TowerAdapter;
import com.scxj.dao.AssetTowerDao;
import com.scxj.model.TB_LINE;
import com.scxj.model.TB_TOWER;

public class FragmentLineInfo extends BaseFragment {

	private TB_LINE line;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) 
		{
            return null;
        }
		View layout = inflater.inflate(R.layout.lineinfo, container, false); 
		return layout;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		line = (TB_LINE)getArguments().getSerializable("item");
		
		
		TextView tv01 = (TextView)getView().findViewById(R.id.lineid);
		TextView tv02 = (TextView)getView().findViewById(R.id.linename);
		TextView tv03 = (TextView)getView().findViewById(R.id.orgid);
		TextView tv04 = (TextView)getView().findViewById(R.id.vollevel);
		
		
		tv01.setText(line.getLINEID());
		tv02.setText(line.getLINENAME());
		tv03.setText(line.getORGID());
		tv04.setText(line.getVOLTRANK());
		
	}

 
}