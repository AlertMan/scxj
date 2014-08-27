
/**
 * 
 */
package com.scxj.adapter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.scxj.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.sax.StartElementListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @author think
 *
 */
public class PointListAdapter extends ListAdapter {
	
	public PointListAdapter(Context context, List<? extends Serializable> datas) {
		super(context, datas);
	}

	 
	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {/*
		ViewHolder vh = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.standard_item, null);
			vh = new ViewHolder();
			vh.tv01 = (TextView)convertView.findViewById(R.id.xuhao);
			vh.tv02 = (TextView)convertView.findViewById(R.id.content);	
			vh.tv03 = (TextView)convertView.findViewById(R.id.standard);
			vh.tv04 = (TextView)convertView.findViewById(R.id.result);

			convertView.setTag(vh);// 绑定ViewHolder对象
		} else {
			vh = (ViewHolder) convertView.getTag();// 取出ViewHolder对象
		}
		final TB_TASK_STANDARD item = (TB_TASK_STANDARD)datas.get(position);
		item.setSEQ(position + 1 + "");
		vh.tv01.setText(position + 1 + "");
		vh.tv02.setText(item.getCONTENT());
		vh.tv03.setText(item.getNAME());
		
		vh.tv04.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub			
				MyApplication.getInstance().standard=item;	
				final EditText et01 = new EditText(mContext);
				new AlertDialog.Builder(mContext).setTitle("请输入异常信息").setIcon(
						android.R.drawable.ic_dialog_info).setView(
								et01).setPositiveButton("确定", new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										
										new StandardDao(mContext).updateStandardStatus("异常",item.getID());
										
										Date now = new Date();
										AssetStandardExpDao assetStandardDao = new AssetStandardExpDao(mContext);
										PonitAssetDao ponitAssetDao = new PonitAssetDao(mContext);
										
										TB_TASK_STANDARD_EXP standard_exp=new TB_TASK_STANDARD_EXP();
										String s=et01.getText().toString();
										standard_exp.setEXCEPTION(s);
										standard_exp.setCREATETIME(DateUtil.dateToString(now));
										standard_exp.setTASKSTANDARDID(item.getID());
										
										for(TB_TASK_POINT_ASSET asset : assets){
											standard_exp.setID(null);
											standard_exp.setTASKID(item.getTASKID());
											standard_exp.setASSETID(asset.getASSETID());
											standard_exp.setASSETNAME(asset.getASSETNAME());
											if(assetStandardDao.isExistByStandardAndAsset(standard_exp.getTASKID(),standard_exp.getTASKSTANDARDID(),standard_exp.getASSETID())){
												assetStandardDao.updateItem(standard_exp);
											}else{
												assetStandardDao.insertItem(standard_exp);
											}
											
											asset.setSTATUS("异常");
											ponitAssetDao.updateAssetStatus(asset);
										}
										
									Intent intent=new Intent(mContext, AssetDefectInfoActivity.class);
									intent.putExtra("standard", item);
									mContext.startActivity(intent);
									((Activity)mContext).finish();
									}
								})
								.setNegativeButton("取消", null).show();
			}
		});
		return convertView;
	*/
		return null;
	}

 
	static class ViewHolder{
		public TextView tv01;
		public TextView tv02;
		public TextView tv03;
		public TextView tv04;
		public Button bt;
	}

}

