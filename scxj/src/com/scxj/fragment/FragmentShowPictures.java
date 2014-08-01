package com.scxj.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scxj.MyApplication;
import com.scxj.R;
import com.scxj.dao.AttachDefectDao;
import com.scxj.dao.AttachTaskDao;
import com.scxj.model.TB_ATTACH;
import com.scxj.model.TB_TASK_DEFECT;
import com.scxj.model.TB_TASK_RET;
import com.scxj.utils.CustomTouchListener;
import com.scxj.utils.FileUtil;

public class FragmentShowPictures extends BaseFragment {

	private TB_TASK_RET task;
	private TB_TASK_DEFECT taskDefect;
	private List<Button> btn = new ArrayList<Button>();
	private List<TB_ATTACH> taskAttachList = new ArrayList<TB_ATTACH>();
	private List<TB_ATTACH> defectAttachList = new ArrayList<TB_ATTACH>();
	private FileAdapter fileAdapter = null;
	private boolean isDelect = false;
	private TextView title;
	private boolean isTask = false;
	private String userName = "";
	private String taskId = "";
	private String taskDefectId = "";
	private  OnDataRefreshListener onDataRefreshListener = new OnDataRefreshListener() {
		
		@Override
		public void refreshDatas() {
			resumeStatus();
			fileAdapter.photoList.clear();
			taskAttachList.clear();
			defectAttachList.clear();
			if (task != null ) {
				// 加载数据 根据线路Id查询此条线路此杆塔下的图片
					taskAttachList = new AttachTaskDao(getActivity())
						.getAllList(userName,task);
					String con = "巡检任务："+task.getTASKNAME()+ ", 线路名称:"+task.getLINENAME()+", 杆塔名称:"+ task.getTOWERNAME() + ". 共有照片["+taskAttachList.size()+"]张";
					if(taskAttachList.size() > 0 ){
						con += ",最后拍照时间："+ taskAttachList.get(0).getCREATETIME();
					}
					title.setText(con);
					
				new AsyncLoadedImage().execute();
			} else if (taskDefect != null) {
				// 加载数据
				title.setText("消缺任务："+taskDefect.getLINENAME()+",杆塔名称:"+ taskDefect.getTOWERNAME());
				defectAttachList = new AttachDefectDao(getActivity())
						.getAllList(userName,taskDefect.getDEFECTTASKID());
				String con = "消缺任务："+taskDefect.getLINENAME()+",杆塔名称:"+ taskDefect.getTOWERNAME()+ ". 共有照片["+defectAttachList.size()+"]张";
				if(defectAttachList.size() > 0){
					con += ",最后拍照时间："+ defectAttachList.get(0).getCREATETIME();
				}
				title.setText(con);
				
				new AsyncLoadedImage().execute();
			} else {
				// 提示用户此条线路下没有图片
				Toast.makeText(getActivity(), "此线路下没有相关图片！", Toast.LENGTH_SHORT)
						.show();
			}
			
		}
	};
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		if (container == null) {
			return null;
		}

		View layout = inflater
				.inflate(R.layout.show_pictures, container, false);

		return layout;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		super.onCreateOptionsMenu(menu, inflater);
		menu.add("刷新").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add("删除").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// 点击使图片能删除
		if ("删除".equals(item.getTitle()) && !isDelect) {
			// 使图片右上角的删除按钮显示出来
			for (int i = 0; i < btn.size(); i++) {
				btn.get(i).setVisibility(View.VISIBLE);
			}
			isDelect = true;
			// AudioTipsUtils.showMsg(getActivity(), "保存成功");
		} else if("刷新".equals(item.getTitle())){
			onDataRefreshListener.refreshDatas();
			
		}else {
			for (int i = 0; i < btn.size(); i++) {
				btn.get(i).setVisibility(View.INVISIBLE);
			}
			isDelect = false;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);

		Bundle b = getArguments();
		isTask = b.getBoolean("isTask", false);
		userName = MyApplication.getInstance().loginUser.getUSERNAME();
		if(isTask){
			task = (TB_TASK_RET)b.getSerializable("taskRet");
			taskId = task.getTASKID();
			taskDefect = null;
		}else{
			task = null;
			taskDefect = (TB_TASK_DEFECT)b.getSerializable("taskDefect");
			taskDefectId = taskDefect.getDEFECTTASKID();
		}
		
		title = (TextView) getView().findViewById(
				R.id.show_pictures_title);
		GridView gridView = (GridView) getView().findViewById(
				R.id.show_pictures_gridView);
		userName = MyApplication.getInstance().loginUser.getUSERNAME();
		if (isTask) {
			// 加载数据 根据线路Id查询此条线路下的图片
			title.setText("巡检任务"+task.getLINENAME()+"  "+ task.getTOWERNAME());
			taskAttachList = new AttachTaskDao(getActivity())
					.getAllList(userName,task);

		} else {
			// 加载数据
			title.setText("消缺任务"+taskDefect.getLINENAME()+"  "+ taskDefect.getTOWERNAME());
			defectAttachList = new AttachDefectDao(getActivity())
					.getAllList(userName,taskDefect.getDEFECTTASKID());

		}
		
		/**
		 * 异步加载
		 */
		fileAdapter = new FileAdapter(getActivity());
		onDataRefreshListener.refreshDatas();
		gridView.setAdapter(fileAdapter);
		gridView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// Log.i(this.getClass().getName(), "onScroll");
				resumeStatus();
				// 只要一动就把状态恢复到开始状态即无法删除按钮显示
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// Log.i(this.getClass().getName(), "onScrollStateChanged");
				resumeStatus();
			}
		});
		
	}
	

	/**
	 * 在滑动与删除后执行状态恢复
	 */
	public void resumeStatus() {
		for (int i = 0; i < btn.size(); i++) {
			btn.get(i).setVisibility(View.INVISIBLE);
		}
	}

	/*
	 * Adapter
	 */
	class FileAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private Context mContext;
		private List<LoadedImage> photoList = new ArrayList<LoadedImage>();

		public FileAdapter(Context context) {
			mContext = context;
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public void addPhoto(LoadedImage photo) {
			photoList.add(photo);
		}

		@Override
		public int getCount() {
			return photoList.size();
		}

		@Override
		public Object getItem(int position) {
			return photoList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			LinearLayout linearLayout = (LinearLayout) inflater.inflate(
					R.layout.show_pictures_grid_item, null);

			ImageView imageView = (ImageView) linearLayout
					.findViewById(R.id.photo_scanImage);
			TextView item1 = (TextView) linearLayout
					.findViewById(R.id.show_pictures_grid_item1);
			TextView item7 = (TextView) linearLayout
					.findViewById(R.id.show_pictures_grid_item7);
			
			LoadedImage tmpImg = (LoadedImage)getItem(position);
			item1.setText("照片名称："+tmpImg.getPicName());
			item7.setText("拍照时间："+tmpImg.getCreateTime());
			Button photo_scan_deleteButton = (Button) linearLayout
					.findViewById(R.id.photo_scan_deleteButton);
			photo_scan_deleteButton
					.setOnTouchListener(new CustomTouchListener() {

						@Override
						public void eventAction(View arg0) {

							new AlertDialog.Builder(mContext)
									.setIcon(R.drawable.dialog)
									.setTitle("请确认是否删除此图片,删除后将不可恢复！")
									.setPositiveButton(
											"删除",
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													// 删除图片 删除数据库中的数据
													TB_ATTACH item = (TB_ATTACH) taskAttachList
															.get(position);
													if(isTask){
														new AttachTaskDao(getActivity()).delItem(userName,taskId,item.getATTACHID());
													}else{
														new AttachDefectDao(getActivity()).delItem(userName,taskDefectId,item.getATTACHID());
													}
													FileUtil.deleteFile(item
															.getATTACHCONTENT());
													taskAttachList.remove(item);
													onDataRefreshListener.refreshDatas();
												}
											})
									.setNegativeButton(
											"取消",
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													dialog.cancel();
													dialog.dismiss();
												}
											}).create().show();
						}
					});

			photo_scan_deleteButton.setVisibility(View.INVISIBLE);
			btn.add(photo_scan_deleteButton);
			imageView.setScaleType(ImageView.ScaleType.CENTER);
			imageView.setImageBitmap(photoList.get(position).getBitmap());

			imageView.setOnTouchListener(new CustomTouchListener() {

				@Override
				public void eventAction(View arg0) {
					// 传递数据过去显示图片及相关信息
					FragmentPictureDetails bf = new FragmentPictureDetails();
					TB_ATTACH item = null;
					Bundle b = new Bundle();
					if (task != null) {
						// item = (TB_ATTACH) taskAttachList.get(position);
						 b.putSerializable("items", (Serializable) taskAttachList);
						 b.putString("taskId", taskId);
						 b.putBoolean("isTask", true);
					} else if (taskDefect != null) {
//						 item = (TB_ATTACH) defectAttachList
//								.get(position);
						 b.putSerializable("items", (Serializable) defectAttachList);
						 b.putString("taskDefectId", taskDefectId);
						 b.putBoolean("isTask", false);
					}  
					
					bf.setArguments(b);
					bf.setOnDataRefreshListener(onDataRefreshListener);
					FragmentTransaction fTransaction = getFragmentManager()
							.beginTransaction();
					fTransaction.replace(R.id.show_pictures_layout, bf);
					fTransaction.addToBackStack(null);
					fTransaction.commit();

				}
			});
			convertView = linearLayout;

			return convertView;
		}
	}
	
	private class AsyncLoadedImage extends
			AsyncTask<Object, LoadedImage, Object> {
		@Override
		protected Object doInBackground(Object... params) {
			boolean nothing = true;
			if (taskAttachList != null && !taskAttachList.isEmpty()) {
				addImages(taskAttachList);
				nothing = false;
			} 
			if (defectAttachList != null && !defectAttachList.isEmpty()) {
				addImages(defectAttachList);
				nothing = false;
			}  
			
			return nothing;
		}

		private void addImages(List gList) {
			Bitmap bitmap;
			Bitmap newBitmap;
			try {
				for (int i = 0; gList != null && i < gList.size(); i++) {
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 4;
					TB_ATTACH item = (TB_ATTACH) gList.get(i);
					try {
						bitmap = BitmapFactory.decodeFile(item.getATTACHCONTENT(),
								options);
					} catch (Exception e) {
						e.printStackTrace();
						bitmap = BitmapFactory.decodeResource(getResources(),
								R.drawable.pic_has_del);
					}
					if (bitmap == null) {// 出现异常
						bitmap = BitmapFactory.decodeResource(getResources(),
								R.drawable.pic_has_del);
					}

					newBitmap = ThumbnailUtils.extractThumbnail(bitmap, 170,
							120);
					bitmap.recycle();
					if (newBitmap != null) {
						publishProgress(new LoadedImage(newBitmap,
								item.getATTACHNAME(), item.getATTACHID(),item.getCREATEADDRESS(),item.getCREATETIME()));
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onProgressUpdate(LoadedImage... value) {
			addImage(value);
		}

		@Override
		protected void onPostExecute(Object result) {
			// setProgressBarIndeterminateVisibility(true);
			boolean ret = (Boolean)result;
			if(ret) {
				// 提示用户此条线路下没有图片
				Toast.makeText(getActivity(), "此线路下没有相关图片！", Toast.LENGTH_SHORT)
						.show();
			}
			
			
		}
	}

	private class LoadedImage {
		String picId;
		String picName;
		String address;
		String createTime;
		public String getAddress() {
			return address;
		}

		public String getCreateTime() {
			return createTime;
		}

		Bitmap mBitmap;

		LoadedImage(Bitmap bitmap, String picPath, String picId,String address, String createTime) {
			this.mBitmap = bitmap;
			this.picName = picPath;
			this.picId = picId;
			this.createTime = createTime;
		}

		public Bitmap getBitmap() {
			return mBitmap;
		}


		public String getPicName() {
			return picName;
		}

		public String getPicId() {
			return picId;
		}

	}

	private void addImage(LoadedImage... value) {
		for (LoadedImage image : value) {
			fileAdapter.addPhoto(image);
			fileAdapter.notifyDataSetChanged();
		}
	}

}
