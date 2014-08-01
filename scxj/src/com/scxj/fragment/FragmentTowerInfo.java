package com.scxj.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scxj.R;
import com.scxj.model.TB_TOWER;

public class FragmentTowerInfo extends BaseFragment {

	private TB_TOWER tower;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) 
		{
            return null;
        }
		View layout = inflater.inflate(R.layout.towerinfo, container, false); 
		return layout;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		tower = (TB_TOWER)getArguments().getSerializable("item");
		
		
		TextView tv01 = (TextView)getView().findViewById(R.id.towerid);
		TextView tv02 = (TextView)getView().findViewById(R.id.towername);
		TextView tv03 = (TextView)getView().findViewById(R.id.orgid);
		TextView tv04 = (TextView)getView().findViewById(R.id.matril);
		
		
		tv01.setText(tower.getTOWERID());
		tv02.setText(tower.getTOWERNAME());
		tv03.setText(tower.getORGID());
		tv04.setText(tower.getTOWERMATERIAL());
		
	}

 
}