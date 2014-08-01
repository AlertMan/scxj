package com.scxj.main;

import java.io.File;
import java.util.Date;
import java.util.List;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.ActionBar.Tab;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import cn.pisoft.base.db.DBHelper;

import com.scxj.MyApplication;
import com.scxj.R;
import com.scxj.dao.AttachDefectDao;
import com.scxj.dao.AttachTaskDao;
import com.scxj.fragment.BaseFragment;
import com.scxj.fragment.FragmentAssetMain;
import com.scxj.fragment.FragmentDataMain;
import com.scxj.fragment.FragmentPhotoMain;
import com.scxj.fragment.FragmentSettingMain;
import com.scxj.fragment.FragmentTaskMain;
import com.scxj.fragment.FragmentTrackMain;
import com.scxj.fragment.OnDataRefreshListener;
import com.scxj.model.TB_ATTACH;
import com.scxj.utils.AppUtil;
import com.scxj.utils.AudioTipsUtils;
import com.scxj.utils.DateUtil;
import com.scxj.utils.FileUtil;
import com.scxj.utils.ImageHelper;
import com.scxj.utils.StringUtils;

public class MainMenuActivity extends BaseActivity {
	Context mContext;
	private BaseFragment crtFrg = null;
	private FragmentManager mFragMgr = getSupportFragmentManager();// 主FM
	private FragmentTransaction ft;
	private boolean isMapShow = false;
	
	private OnDataRefreshListener onDataRefreshListener;
	
	
	private ActionBar.TabListener tl = new ActionBar.TabListener() {

		@Override
		public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
			// TODO Auto-generated method stub
			Log.d("ActionBar.TabListener->onTabReselected", tab.getText()
					.toString());
		}

		@Override
		public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
			// TODO Auto-generated method stub
			Log.d("ActionBar.TabListener->onTabSelected", tab.getText()
					.toString());
			showMenuFrag(tab.getPosition());
		}

		@Override
		public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
			// TODO Auto-generated method stub
			Log.d("ActionBar.TabListener->onTabUnselected", tab.getText()
					.toString());
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		mContext = this;
		final ActionBar actionBar = getActionBar();
		// 设置返回键不显示
		actionBar.setDisplayHomeAsUpEnabled(false);
		// 设置Logo图表不显示
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle("任务主界面");
		// 默认开始显示tab导航
		actionBar
				.addTab(actionBar.newTab()
						.setText(getString(R.string.title_section1))
						.setTabListener(tl));
		actionBar
				.addTab(actionBar.newTab()
						.setText(getString(R.string.title_section2))
						.setTabListener(tl));
		actionBar
				.addTab(actionBar.newTab()
						.setText(getString(R.string.title_section3))
						.setTabListener(tl));
		actionBar
				.addTab(actionBar.newTab()
						.setText(getString(R.string.title_section4))
						.setTabListener(tl));
		actionBar
				.addTab(actionBar.newTab()
						.setText(getString(R.string.title_section5))
						.setTabListener(tl));
		actionBar
				.addTab(actionBar.newTab()
						.setText(getString(R.string.title_section6))
						.setTabListener(tl));
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setListNavigationCallbacks(ArrayAdapter.createFromResource(
				context, R.array.title_section,
				android.R.layout.simple_spinner_dropdown_item),
				new OnNavigationListener() {
					public boolean onNavigationItemSelected(int itemPosition,
							long itemId) {
						showMenuFrag(itemPosition);
						return true;
					}
				});
		// 平铺展示
		showTabsNav();
	}

	private void showMenuFrag(int position) {
		BaseFragment bf = (BaseFragment) mFragMgr.findFragmentByTag(position
				+ "");
		if(bf != null){
			if((position == 2 || position == 3) && MyApplication.MapShowIndex != -1 && MyApplication.MapShowIndex != position){//这个也只能解决一层，后面再看
				//	if(MyApplication.MapShowIndex != -1 && MyApplication.MapShowIndex != position ){
				BaseFragment mapBf = (BaseFragment) mFragMgr.findFragmentByTag(MyApplication.MapShowIndex+ "");
				mapBf.getmFragmentManager().popBackStack();
			}
			 
			if(MyApplication.MapShowIndex != -1){
				
				BaseFragment mMapBf = (BaseFragment) mFragMgr.findFragmentByTag(MyApplication.MapShowIndex+ "");
				FragmentManager childMFragMgr = mMapBf.getmFragmentManager();
				List<Fragment> list =  childMFragMgr.getFragments();
				int cnt = childMFragMgr.getBackStackEntryCount();
				BaseFragment mapBf = (BaseFragment) list.get(cnt-1);
				if(position == 2 || position == 3){
					mapBf.onResume();
				}else{
					mapBf.onPause();
				}
			}
		}
		
		ft = mFragMgr.beginTransaction();
		if (bf == null) {
			switch (position) {
			case 0:// 巡检消缺管理
				bf = new FragmentTaskMain();
				break;
			case 1:// 照片查看
				bf = new FragmentPhotoMain();
				break;
			case 2:// 台账查看
				bf = new FragmentAssetMain();
				break;
			case 3:// 轨迹查询
				bf = new FragmentTrackMain();
				break;
			case 4:// 上传与下载
				bf = new FragmentDataMain();
				break;
			default:// 系统设置
				bf = new FragmentSettingMain();
				break;
			}
//			ft.add(R.id.container, bf, position + "");
			ft.replace(R.id.container, bf);
			ft.setTransitionStyle(FragmentTransaction.TRANSIT_ENTER_MASK);
		}
		if (crtFrg != null) {
			ft.hide(crtFrg);
			ft.show(bf);
		}
		ft.commit();
		crtFrg = bf;
	}

	long curtTime = 0;
	@Override
	public void onBackPressed() {

		if (crtFrg.canBack()) {
			crtFrg.onBackPressed();
			return;
		}
		if(System.currentTimeMillis() - curtTime > 4000){
			AudioTipsUtils.showMsg(getApplicationContext(), "再按一次返回键退出！");
			curtTime = System.currentTimeMillis();
		} else {
			((MyApplication)getApplication()).onTerminate();
			finish();
		}
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private Context getActionBarThemedContextCompat() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return getActionBar().getThemedContext();
		} else {
			return this;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.menu_nav_tabs:
			item.setChecked(true);
			showTabsNav();
			return true;
		case R.id.menu_nav_drop_down:// 显示List_av
			item.setChecked(true);
			showDropDownNav();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void showTabsNav() {// 显示tab导航条
		ActionBar ab = getActionBar();
		if (ab.getNavigationMode() != ActionBar.NAVIGATION_MODE_TABS) {
			ab.setDisplayShowTitleEnabled(false);
			ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		}
	}

	private void showDropDownNav() {// 显示下拉List导航条
		ActionBar ab = getActionBar();
		if (ab.getNavigationMode() != ActionBar.NAVIGATION_MODE_LIST) {
			ab.setDisplayShowTitleEnabled(false);
			ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		
		String userName = MyApplication.getInstance().loginUser.getUSERNAME();
		if(resultCode == RESULT_OK){
			TB_ATTACH ath = ((MyApplication) getApplication()).attach;
			//目前没有压缩图片
			/* Bitmap bitmap = ImageHelper.createImage(ath.getATTACHCONTENT()); 
			  String imageName = DateUtil.dateToString(new Date(),
						"yyyyMMddHHmmss") + ".jpg";
			 String imageFilePath = DBHelper.DB_PATH + "/" + imageName;
			 File compressFile = new File(imageFilePath);  
	         ImageHelper.saveCompressBitmap(bitmap, compressFile);  
	         FileUtil.deleteFile(ath.getATTACHCONTENT());*/
	         
	         String imageFilePath = ath.getATTACHCONTENT();
	         
			if (requestCode == 9 ) {// 储存附件
				String taskId= ath.getATTACHID();
				ath.setATTACHLEN(ath.getFile().length() + "");
				ath.setATTACHCONTENT(imageFilePath);
				ath.setASSETID(AppUtil.getDeviceId(context));
				ath.setFile(null);// 清空一下
				ath.setATTACHID(null);
				ath.setCREATEADDRESS(MyApplication.getInstance().currentAddress);
				new AttachTaskDao(mContext).insertItem(userName,taskId,ath);
				ath.getSelfOnDataRefreshListener().refreshDatas();
			}else if (requestCode == 10) {// 储存附件
				String defectTaskId = ath.getATTACHID();
				ath.setATTACHLEN(ath.getFile().length() + "");
				ath.setATTACHCONTENT(imageFilePath);
				ath.setASSETID(AppUtil.getDeviceId(context));
				ath.setFile(null);// 清空一下
				ath.setATTACHID(null);
				ath.setCREATEADDRESS(MyApplication.getInstance().currentAddress);
				new AttachDefectDao(mContext).insertItem(userName,defectTaskId,ath);
			}
			
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public OnDataRefreshListener getOnDataRefreshListener() {
		return onDataRefreshListener;
	}

	public void setOnDataRefreshListener(OnDataRefreshListener onDataRefreshListener) {
		this.onDataRefreshListener = onDataRefreshListener;
	}

}
