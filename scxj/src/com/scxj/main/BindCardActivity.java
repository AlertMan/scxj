package com.scxj.main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import cn.pisoft.base.db.DBHelper;

import com.cetc7.UHFReader.UHFReaderClass.OnEPCsListener;
import com.scxj.R;
import com.scxj.adapter.StandardListAdapter;
import com.scxj.dao.PonitDao;
import com.scxj.model.TB_POINT;

/**
 * 绑卡主界面
 * 
 */
public class BindCardActivity extends BaseActivity implements OnEPCsListener {
	private boolean mAllive;
	private boolean mForceDisconnect;
	private ListView siteListView;
	private List<TB_TASK_STANDARD> datas;
	private StandardListAdapter adapter;
	private ArrayList<HashMap<String, String>> mSiteList;
	public EditText siteEditText;
	private List<TB_POINT> sitesSrc;
	private BaseAdapter mAdapterSite;
	private TB_POINT currentSite;
	private PonitDao pointDao;
	public static boolean mExit = false;
	private int readCount = 0;
	public boolean mConnected = false;
	public static final int READ_SUCCESS = 1;
	public static final int REFRESH_SITE_LIST = 4;

	public static final int MSG_QUIT = 9999;
	public static final int MSG_AUTO_REFRESH_DEVICE = 1;
	public static final int MSG_ENABLE_LINK_CTRL = 10;
	public static final int MSG_DISABLE_LINK_CTRL = 11;
	public static final int MSG_ENABLE_DISCONNECT = 12;
	public static final int MSG_DISABLE_DISCONNECT = 13;
	public static final int MSG_SHOW_TOAST = 20;
	public static final int MSG_REFRESH_LIST_DEVICE = 21;
	public static final int MSG_REFRESH_LIST_TAG = 22;

	public static final int MSG_LINK_ON = 30;
	public static final int MSG_LINK_OFF = 31;

	public static final int MSG_SOUND_RX = 40;
	public static final int MSG_SOUND_RX_HALF = 41;

	public static final int MSG_AUTO_LINK = 100;
	
	private int selIndex = 0;
	private String currentSiteId = "";
	private String tagId = "";
	protected Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case READ_SUCCESS:
				hideProgressDialog();
				tagId = msg.obj.toString();
				
				for(HashMap<String,String> o : mSiteList){
					if(o.get("rfidCode").equals(tagId) && !o.get("id").equals(currentSite.getPOINTID())){
						currentSiteId = o.get("id");
						new AlertDialog.Builder(BindCardActivity.this)
						.setTitle("提示")
						.setMessage(
								"["+o.get("name") + "]巡视点已使用标签[" + tagId
										+ "]，是否替换，是则巡视点["+o.get("name")+"]的标签将被清除！")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(
											DialogInterface dialog,
											int whichButton) {
										currentSite.setEPCCODE(tagId);
										pointDao.bindingCard(currentSite);
										TB_TASK_POINT point = pointDao.getPointById(currentSiteId);
										point.setEPCCODE("");
										pointDao.bindingCard(point);
										 
										handler.sendEmptyMessage(REFRESH_SITE_LIST);
										
									}
									
								})
						.setNegativeButton("取消",
								null).create().show();
						return;
					}
				}

				// 绑巡视点
				if (currentSite != null) {
					if (StringUtils.isNull(currentSite.getEPCCODE())) {
						 
						new AlertDialog.Builder(BindCardActivity.this)
						.setTitle("提示")
						.setMessage(
								"巡视点["+currentSite.getPOINTNAME() + "]将绑定标签[" + tagId
										+ "]")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(
											DialogInterface dialog,
											int whichButton) {
										currentSite.setEPCCODE(tagId);
										pointDao.bindingCard(currentSite);
										handler.sendEmptyMessage(REFRESH_SITE_LIST);
									}
								})
						.setNegativeButton("取消",
								null).create().show();
						
					} else {
						if(tagId.equals(currentSite.getEPCCODE())){
							Toast.makeText(context, "已绑卡相同标签卡！", Toast.LENGTH_SHORT)
							.show();
							return ;
						}
						new AlertDialog.Builder(BindCardActivity.this)
								.setTitle("提示")
								.setMessage(
										"是否使用已扫描到的标签" + tagId + "，原有巡视点["+currentSite.getPOINTNAME()+"]的标签["+currentSite.getEPCCODE()+"]将被清除！")
								.setPositiveButton("确定",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int whichButton) {
												currentSite.setEPCCODE(tagId);
												pointDao.bindingCard(currentSite);
												handler.sendEmptyMessage(REFRESH_SITE_LIST);
												
											}
										})
								.setNegativeButton("取消",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int whichButton) {
											}
										}).create().show();
					}
				}

				break;
			case REFRESH_SITE_LIST:
				if(null != msg.obj && !StringUtils.isNull(msg.obj.toString())){
					Toast.makeText(context, msg.obj.toString(), Toast.LENGTH_SHORT)
					.show();
				}
				reloadSiteData();
				hideProgressDialog();
				break;
			case MSG_SHOW_TOAST:
				Toast.makeText(context, msg.obj.toString(), Toast.LENGTH_SHORT)
						.show();
				break;

			case MSG_AUTO_LINK:
				if (mAllive == true && mForceDisconnect == false) {
					if (mConnected == false) {
						try {
							boolean ret = RfidTools.openRfid();
							setConnectionStatus(ret);
							if (mConnected) {
								boolean mod = RfidTools.setBurstModel();
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}

				if (mExit == false)
					sendEmptyMessageDelayed(MSG_AUTO_LINK, 5000);
				break;

			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.site_search_input);
		pointDao = new PonitDao(BindCardActivity.this);
		initPatrolSiteList();
		setNavTitleBar("巡视点绑卡");
	}
	
	
	private void reloadSiteData() {
		sitesSrc =  pointDao.getAllPoint();
		initSiteData(sitesSrc);
		mAdapterSite = new SimpleAdapter(this, mSiteList,
				android.R.layout.simple_list_item_2, new String[] { "name",
						"info", "rfidCode", "id" }, new int[] {
						android.R.id.text1, android.R.id.text2 });
		siteListView.setAdapter(mAdapterSite);
		siteListView.smoothScrollToPosition(selIndex);
		siteListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ListView listView = (ListView) parent;
				HashMap<String, String> item = (HashMap<String, String>) listView
						.getItemAtPosition(position);
				currentSite = pointDao.getPointById(item.get("id"));
				selIndex = position;
				if (mConnected) {
					cardProcess();
				} else {
					Toast.makeText(BindCardActivity.this, "请连接设备",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private void initPatrolSiteList() {
		siteListView = (ListView) this.findViewById(R.id.list_config_site);
		mSiteList = new ArrayList<HashMap<String, String>>();
		siteEditText = (EditText) findViewById(R.id.site_edit_search);
		siteEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				Log.d("TAG", "onTextChanged:" + s + "-" + "-" + start + "-"
						+ before + "-" + count);
				if (count > 0 || before > 0) {
					reloadSite();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				Log.d("TAG", "beforeTextChanged:" + s + "-" + start + "-"
						+ count + "-" + after);
			}

			@Override
			public void afterTextChanged(Editable s) {
				Log.d("TAG", "afterTextChanged");
			}
		});
		reloadSiteData();
	}

	// 刷卡处理
	private void cardProcess() {
		readCount = 0;
		RfidTools.triggerStart(this);
		handler.obtainMessage(MSG_SHOW_TOAST, "开始扫描").sendToTarget();
	}

	private void onSiteBindingSuccess(TB_TASK_POINT site) {
		for (HashMap<String, String> map : mSiteList) {
			String siteId = map.get("id");
			if (siteId != null && siteId.equalsIgnoreCase(site.getPOINTID())) {
				if (!"".equalsIgnoreCase(site.getEPCCODE())
						&& site.getEPCCODE() != null) {
					map.put("info", site.getSTATUS() + "--" + site.getEPCCODE());
				} else {
					map.put("info", site.getSTATUS());
				}
			}
			handler.sendEmptyMessage(REFRESH_SITE_LIST);
		}
	}

	private void showToastByOtherThread(String msg, int time) {
		handler.removeMessages(MSG_SHOW_TOAST);
		handler.sendMessage(handler.obtainMessage(MSG_SHOW_TOAST, time, 0, msg));
	}

	public void closeApp() {
		try {
			finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		finish();
	}

	public void postCloseApp() {
		handler.sendEmptyMessageDelayed(MSG_QUIT, 1000);
	}

	private void reloadSite() {

		String searchName = siteEditText.getText().toString().trim();
		mSiteList.clear();
		if (searchName != null && searchName.trim().length() == 0) {
			initSiteData(sitesSrc);
			mAdapterSite.notifyDataSetChanged();
			return;
		}

		if (sitesSrc != null && sitesSrc.size() > 0) {
			List<TB_TASK_POINT> tempUserList = new ArrayList<TB_TASK_POINT>();
			for (int i = 0; i < sitesSrc.size(); i++) {

				System.out.println(sitesSrc.get(i).getPOINTNAME() + "|||"
						+ searchName);
				if (sitesSrc.get(i).getPOINTNAME().contains(searchName)) {
					tempUserList.add(sitesSrc.get(i));
				}

			}
			initSiteData(tempUserList);
			mAdapterSite.notifyDataSetChanged();
			return;
		}

	}

	private void initSiteData(List<TB_TASK_POINT> siteData) {
		mSiteList.clear();
		for (TB_TASK_POINT site : siteData) {
			HashMap<String, String> item = new HashMap<String, String>();
			item.put("name", site.getPOINTNAME());
			item.put("info", site.getSTATUS()+"--"+site.getEPCCODE());
			item.put("rfidCode", site.getEPCCODE());
			item.put("id", site.getPOINTID());
			mSiteList.add(item);
		}
	}

	@Override
	public void onEPCsRecv(ArrayList<String> arg0) {
		// TODO Auto-generated method stub

		if (arg0 != null && arg0.size() > 0 && arg0.get(0) != null
				&& readCount == 0) {
			updateAccessDataRead(arg0.get(0).toLowerCase());
			RfidTools.triggerStop();
			readCount++;
		}
	}

	private void updateAccessDataRead(final String param) {
		if (param == null || param.length() <= 4)
			return;
		String strTagId = param.toLowerCase();
		handler.obtainMessage(READ_SUCCESS, strTagId).sendToTarget();
	}

	private void setConnectionStatus(boolean bConnected) {
		if (mExit == true)
			return;
		if (bConnected) {
			showToastByOtherThread("RFID连接成功", Toast.LENGTH_SHORT);
		} else {
			showToastByOtherThread("RFID连接失败", Toast.LENGTH_SHORT);
		}
		mConnected = bConnected;
	}

	protected void finalize() throws Throwable {
		mExit = true;
		mConnected = false;
		boolean off = RfidTools.closeRfid();
		String toast = "RFID断开成功";
		if (!off) {
			toast = "RFID断开失败";
		}
		showToastByOtherThread(toast, Toast.LENGTH_SHORT);
		handler.removeMessages(MSG_AUTO_LINK);
		super.finalize();
	}

	public void onStart() {
		super.onStart();
		mExit = false;
		mAllive = true;
		handler.removeMessages(MSG_AUTO_LINK);
		handler.sendEmptyMessage(MSG_AUTO_LINK);
	}

	public void uploadPointInfo(View view) {
		showProgressDialog("正在同步绑卡信息");
		final String userId = MyApplication.getInstance().loginUser.getUSERID();
		final String pointDbPath = DBHelper.DB_PATH + "/" + userId + "/TRNPOINT.DB";
		 File pointDbFile = new File(pointDbPath);
		if (!pointDbFile.exists()) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					boolean ret = NetAdapter.downloadPointDb(userId);
					if(ret){
						handler.obtainMessage(REFRESH_SITE_LIST, "绑卡信息下载成功!")
						.sendToTarget();
					}else{
						handler.obtainMessage(MSG_SHOW_TOAST, "绑卡信息下载失败!")
						.sendToTarget();
					}
				}
			}).start();

		} else {
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					byte[]  dbBytes = FileUtil.readFileByByte(pointDbPath);
					
					String ret = NetAdapter.updateBindingInfo(userId,Base64.encode(dbBytes));
					if (StringUtils.isNull(ret)) {
						handler.obtainMessage(MSG_SHOW_TOAST, "绑卡信息上传失败!").sendToTarget();
						return;
					}
					try {
						JSONObject jb = new JSONObject(ret);
						if ("0".equals(jb.getString("resultCode"))) {
							FileUtil.deleteFile(pointDbPath);
							boolean downloadRet = NetAdapter.downloadPointDb(userId);
							if(downloadRet){
								handler.obtainMessage(REFRESH_SITE_LIST, "绑卡信息同步成功!")
									.sendToTarget();
							}else{
								handler.obtainMessage(REFRESH_SITE_LIST, "绑卡信息上传成功，但下载失败!")
								.sendToTarget();
							}
						} else {
							handler.obtainMessage(MSG_SHOW_TOAST, jb.getString("resultDesc"))
									.sendToTarget();
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
			}).start();
		}

	}
}
