package com.scxj.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import cn.pisoft.base.db.DBFileImporter;
import cn.pisoft.base.db.DBHelper;

import com.scxj.MyApplication;
import com.scxj.R;
import com.scxj.adapter.BzListAdapter;
import com.scxj.adapter.UserListAdapter;
import com.scxj.dao.InitDefectTaskDao;
import com.scxj.dao.InitTaskDao;
import com.scxj.dao.TaskDao;
import com.scxj.dao.TaskDefectDao;
import com.scxj.dao.UserDao;
import com.scxj.model.TB_USER;
import com.scxj.service.GpsAlertActivity;
import com.scxj.utils.AppUtil;
import com.scxj.utils.AudioTipsUtils;
import com.scxj.utils.AutoBrightScreen;
import com.scxj.utils.Const;
import com.scxj.utils.FileUtil;
import com.scxj.utils.SharedPreferencesUtil;
import com.scxj.utils.ShortcutUtil;
import com.scxj.utils.StringUtils;
import com.scxj.utils.net.WSUtils;
import com.scxj.utils.usb.NetAdapter;
import com.scxj.utils.usb.USBNetUtils;

/**
 * 用户登陆模块
 * 
 * @author think 1、检查语音组件 2、确定上网模式 3、初始化基础数据库、应用基本参数初始化 4、检查与服务器连通性 登陆成功后检查项
 *         5、软件更新 6、开启预定位
 */
public class LoginActivity extends BaseActivity implements Callback {
	protected EditText userInput;
	protected EditText passwordInput;
	protected EditText BZ;
//	protected EditText JH;
	private ImageView settingIV;
	// 设置对话框
	private SetCustomDialog dialog;
	// PopupWindow对象
	private PopupWindow selectPopupWindow = null;
	public static final int MSG_SHOW_TOAST = 20;
	public static final int MSG_AUTO_LINK = 100;
	public boolean alreadyLogin;
	public boolean isValidation = false;

	public boolean mConnected = false;

	private boolean isWifiConnected = false;

	private UserDao userDao;

	private String updateUrl;
	private String retStr = "";
  
	private SharedPreferencesUtil spu;
	private TB_USER loginUser;
	
    private Handler handler =null;//选择机号用户线程
    private List <TB_USER> jhList = new ArrayList<TB_USER>();
//    private JhListAdapter jhListAdapter;
    
    private List<TB_USER> bzList = new ArrayList<TB_USER>();
    private BzListAdapter bzListAdapter;
    
    private List <TB_USER> userList = new ArrayList<TB_USER>();
    private UserListAdapter userListAdapter;
    
    private String updateFileName = "";
    private String indext=null;
    
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			hideProgressDialog();
			switch (msg.what) {
			case 1:// 连接失败
				AudioTipsUtils.showMsg(context, "网络连接失败!");
				break;
			case 7:// 探测软件更新
				updateSoft();
				break;
			case 8:// 下载APK
				requestSure();
				break;
			case 9:// 登陆失败
				if (msg.obj != null && !StringUtils.isNull(msg.obj.toString())) {
					AudioTipsUtils.showMsg(context, msg.obj.toString());
				}
				break;

			case 10:// 提示下载APK
				new AlertDialog.Builder(LoginActivity.this)
						.setTitle("系统更新")
						.setMessage("发现新版本，请更新！")
						.setCancelable(true)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										mHandler.obtainMessage(8)
												.sendToTarget();// 下载新版本文件
									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										dialog.cancel();
										mHandler.obtainMessage(19)
												.sendToTarget();// 更新数据
									}
								}).show();
				break;
			case 19:// 校验成功，同步数据
				startSyncDbFiles();
				break;
			case 29:// 成功登陆
				if (msg.obj != null && !StringUtils.isNull(msg.obj.toString())) {
					AudioTipsUtils.showMsg(context, msg.obj.toString());
				}
				redirectToMainActivity();
				break;
			default:
				break;
			}

		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//设置屏幕为最高亮度
	//	if(!AutoBrightScreen.isAutoBrightness(this)){
			AutoBrightScreen.SetLightness(this,235);
	//	}
		//开启预定位
		MyApplication.getInstance().startGpsLocation();
		// 探测语音组件安装
		AudioTipsUtils.isSpeeshServiceInstalled(context);
		MyApplication.getInstance().tipsVoicePlayer("");
		// 初始化数据库
		importDBFiles();
		// 选择网络方式wifi or usb
		if (NetAdapter.isWifiAvailable(context)) {
			new AlertDialog.Builder(context)
					.setTitle("提示")
					.setMessage("若使用USB网络共享工具，请确定Wifi上网服务关闭，两种方式选一种即可。")
					.setCancelable(true)
					.setPositiveButton("使用USB共享上网",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									WifiManager wm = (WifiManager) context
											.getSystemService(Context.WIFI_SERVICE);
									if (wm.isWifiEnabled()) {
										if (wm.setWifiEnabled(false)) {
											// 如果关不了，另想办法吧
											// 启动监听端口
											try {
												NetAdapter
														.startAdapter(LoginActivity.this);
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									}

								}
							})
					.setNeutralButton("使用Wifi上网",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									NetAdapter.stopAdapter();
									WifiManager wm = (WifiManager) context
											.getSystemService(Context.WIFI_SERVICE);
									if (wm.isWifiEnabled()) {
										wm.setWifiEnabled(true);
										isWifiConnected = true;
									}
								}
							}).show();
		} else {
			// 启动监听端口　// 如果是USB连接线启动监听端口
			try {
				NetAdapter.startAdapter(context);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		setContentView(R.layout.login);
		spu = new SharedPreferencesUtil(context);
		userDao = new UserDao(context);
		String serverIp = spu.readHostIp();
		String serverPort = spu.readHostPort();
		if (StringUtils.isNull(serverIp) || StringUtils.isNull(serverPort)) {
			showSettingDialog();
		} else {
			WSUtils.init(serverIp, serverPort);
		}

		// 显示界面
		showActivity();
		
		if(!AppUtil.isGpsOPen(context)){
			startActivity(new Intent(context,GpsAlertActivity.class));
		}
		
		// 创建桌面图标
		ShortcutUtil.createShortCut(LoginActivity.this,
				R.drawable.ico_launcher, R.string.app_name);
		
	}

	public void showActivity() {

		Button login = (Button) findViewById(R.id.btn_login);
//		JH = (EditText) findViewById(R.id.login_jh);
		BZ = (EditText) findViewById(R.id.login_bz);
		userInput = (EditText) findViewById(R.id.userName);
		passwordInput = (EditText) findViewById(R.id.password);
		settingIV = (ImageView) findViewById(R.id.img_setting);// 设置按钮
		loginUser = getLastedLoginUser();// 获取最后一次登陆人员的信息

//		JH.setText(loginUser.getGROUPNAME());
		BZ.setText(loginUser.getCLASSNAME());
		userInput.setText(loginUser.getREALNAME());
//		passwordInput.setText("123456");
		
		handler = new Handler(LoginActivity.this);
		
		/*ImageButton jhSelect=(ImageButton)findViewById(R.id.login_jh_select);
		jhSelect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				// 弹出下拉框时关闭输入法
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(JH.getWindowToken(), 0);
				JH.clearFocus();
				v.requestFocus();
				// 弹出一弹出框展示机号信息可供用户选择
				indext="JH";
				initJHDatas();
				if(jhList.isEmpty()){
					return;
				}
				initPopuWindow("JH");
				selectPopupWindow.showAsDropDown(JH, 0, -10);
				
			}
		});*/

		ImageButton bzSelect=(ImageButton)findViewById(R.id.login_bz_select);
		bzSelect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				indext="BZ";
				// 弹出下拉框时关闭输入法
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(BZ.getWindowToken(), 0);
				BZ.clearFocus();
				v.requestFocus();
				initBZDatasByJh("");
				if(bzList.isEmpty()){
					return;
				}
				initPopuWindow("BZ");
				selectPopupWindow.showAsDropDown(BZ, 0, -8);
			 
			}
		});
		
		ImageButton userSelect=(ImageButton)findViewById(R.id.login_user_select);
		userSelect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 弹出框以选中的班组信息查询人员
				indext="RY";
				// 弹出下拉框时关闭输入法
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
				userInput.clearFocus();
				v.requestFocus();
				initUserDatasByBz(loginUser.getCLASSNAME());
				if(userList.isEmpty()){
					return;
				}
				initPopuWindow("RY");
				selectPopupWindow.showAsDropDown(userInput, 0, -8);
			}
		});

		login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String password = passwordInput.getText().toString();
				final String userName = userInput.getText().toString().trim();
//				final String jh = JH.getText().toString().trim();
				final String bz = BZ.getText().toString().trim();
	 
				
				/*if (StringUtils.isNull(jh)) {
					AudioTipsUtils.showMsg(context, "注意,机号不能为空!");
					return;
				}*/

				if (StringUtils.isNull(bz)) {
					AudioTipsUtils.showMsg(context, "注意,班组不能为空!");
					return;
				}

				if (StringUtils.isNull(userName)) {
					AudioTipsUtils.showMsg(context, "注意,登录用户名不能为空!第一次登陆时请输入用户名登陆！");
					return;
				}

				if (StringUtils.isNull(password)) {
					AudioTipsUtils.showMsg(context, "注意,登陆密码不能为空!");
					return;
				}

				loginUser = userDao.getUSER(userName, password);
				String errStr = "";

				if (loginUser == null) {
					errStr = "离线本地登陆失败！";
				}

				if (loginUser != null
						&& !loginUser.getPASSWORD().equals(password)) {
					errStr = "本地账号正确，密码输入错误！";
				}
				/*if (loginUser != null && !loginUser.getGROUPNAME().equals(jh)) {
					errStr = "本地账号正确，机号输入错误！";
				}*/

				if (loginUser != null && !loginUser.getCLASSNAME().equals(bz)) {
					errStr = "本地账号正确，班组输入错误！";
				}

				if (loginUser != null
						&& loginUser.getPASSWORD().equals(password)
						&& loginUser.getCLASSNAME().equals(bz)) {

					mHandler.obtainMessage(7).sendToTarget();
				} else {
					loginUser = new TB_USER();
					if(StringUtils.isNull(loginUser.getUSERNAME())){
						loginUser.setUSERNAME(userName);
					}
					loginUser.setPASSWORD(password);
//					loginUser.setGROUPNAME(jh);
					loginUser.setCLASSNAME(bz);

					showProgressDialog(errStr + "，正在网络验证用户信息...");
					// mHandler.obtainMessage(9,
					// "本地用户名或者密码错误，正在网络验证用户信息...").sendToTarget();
					new Thread(new Runnable() {

						@Override
						public void run() {

							String retStr = NetAdapter.checkUser(
									loginUser.getGROUPNAME(),
									loginUser.getCLASSNAME(),
									loginUser.getUSERNAME(),
									loginUser.getPASSWORD());
							try {
								if (StringUtils.isNull(retStr)) {// 网络连接失败
									mHandler.obtainMessage(9, "本地密码错误，网络验证用户信息，网络连接失败，请检查网络连接！")
											.sendToTarget();
								} else {
									JSONObject jb = new JSONObject(retStr);
									if ("0".equals(jb.getString("resultCode"))) {// 校验成功，更新软件
										mHandler.obtainMessage(7)
												.sendToTarget();
									} else {// 服务端报错
										mHandler.obtainMessage(9,
												jb.getString("resultDesc"))
												.sendToTarget();
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
								mHandler.obtainMessage(9, "接口返回失败！")
										.sendToTarget();
							}
						}
					}).start();
				}
			}
		});

		settingIV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showSettingDialog();
			}
		});

	}

	/**
	 * 初始化PopupWindow
	 */
	private void initPopuWindow(String str) {
		
		// PopupWindow浮动下拉框布局
		View loginwindow = this.getLayoutInflater().inflate(R.layout.loginlist,
				null);
		// 下拉选项的ListView
		ListView listView = (ListView) loginwindow
				.findViewById(R.id.login_list);
		// 自定义Adapter
		/*if(str.equals("JH")){
			selectPopupWindow = new PopupWindow(loginwindow,
					JH.getWidth(),
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);
			jhListAdapter = new JhListAdapter(context, jhList);
			// 去掉item之间的分割线
			listView.setDivider(null);
			listView.setAdapter(jhListAdapter);
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					setEditTextValues(jhList.get(arg2));
					selectPopupWindow.dismiss();
					selectPopupWindow = null;
				}
			});
		}else*/ if(str.equals("BZ")){
			selectPopupWindow = new PopupWindow(loginwindow,
					BZ.getWidth(),
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);
			bzListAdapter = new BzListAdapter(context, bzList);
			listView.setDivider(null);
			listView.setAdapter(bzListAdapter);
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					setEditTextValues(bzList.get(arg2));
					selectPopupWindow.dismiss();
					selectPopupWindow = null;
				}
			});
			
		}else if(str.equals("RY")){
			selectPopupWindow = new PopupWindow(loginwindow,
					userInput.getWidth(),
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);
			
			userListAdapter = new UserListAdapter(context, userList);
			listView.setDivider(null);
			listView.setAdapter(userListAdapter);
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					setEditTextValues(userList.get(arg2));
					selectPopupWindow.dismiss();
					selectPopupWindow = null;
				}
			});
		}
		selectPopupWindow.setOutsideTouchable(true);
		selectPopupWindow.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				setEditTextValues(loginUser);
			}
		});
		selectPopupWindow.setBackgroundDrawable(new BitmapDrawable());
	}
	
	private void setEditTextValues(TB_USER user){
//		JH.setText(user.getGROUPNAME());
		BZ.setText(user.getCLASSNAME());
		userInput.setText(user.getREALNAME());
		loginUser = user;
	}

	/**
	 * 初始化填充Adapter所用List数据
	 */
	
	/*private void initJHDatas() {
		jhList.clear(); 
		jhList = (ArrayList<TB_USER>) userDao.getJHList();
		if(!jhList.isEmpty()){
			setEditTextValues(jhList.get(0));
		}
	}
	*/
	private void initBZDatasByJh(String jh) {
		bzList.clear(); 
		bzList = (ArrayList<TB_USER>) userDao.getBZListByJh(jh);
		if (!bzList.isEmpty()) {
			setEditTextValues(bzList.get(0));
		}
	}
	
	private void initUserDatasByBz(String bz) {
		userList.clear(); 
		userList = (ArrayList<TB_USER>) userDao.getUserListByBz(bz);
		if (!userList.isEmpty()) {
			setEditTextValues(userList.get(0));
		}
	}
	
	/**
	 * PopupWindow消失
	 */
	public void dismiss() {
		selectPopupWindow.dismiss();
	}
	
	// 跳转到登录成功的界面
	private void redirectToMainActivity() {
		MyApplication.getInstance().threadFlag = true;
		TB_USER user = userDao.getUSER(loginUser.getUSERNAME(),
				loginUser.getPASSWORD());
		if (user == null) {
			AudioTipsUtils.showMsg(context, "下载的用户信息数据，没有登陆用户数据！");
			return;
		}
		Const.DB_NAME.setUserDirectory(user.getUSERNAME(), false);// 注入数据
		MyApplication.getInstance().loginUser = user;
		saveUserToLocal(user);

		Intent intent = new Intent();
		intent.setClass(LoginActivity.this, MainMenuActivity.class);
		MyApplication.getInstance().tipsVoicePlayerStop();// 结束语音
		startActivity(intent);
		finish();
	}

	private void updateSoft() {
		if (USBNetUtils.isOnUSBNet() || isWifiConnected) {// 如果有连接才显示
			showProgressDialog("正在检测软件版本……");
			// 软件更新服务
			new Thread(new Runnable() {
				@Override
				public void run() {
					boolean isUpdate = compareVersion();
					if (isUpdate) {
						mHandler.obtainMessage(10).sendToTarget();// 提示更新
					} else {
						mHandler.obtainMessage(19).sendToTarget();// 更新数据
					}
				}
			}).start();

		} else {
			mHandler.obtainMessage(29).sendToTarget();
		}
	}

	private void startSyncDbFiles() {
		showProgressDialog("正在检测任务数据是否为最新，请稍等...");
		// MyApplication.getInstance().tipsVoicePlayer("正在检测任务数据是否为最新，请稍等...");
		new Thread(new Runnable() {
			@Override
			public void run() {

				try {
						syncDBFiles();
				} catch (Exception e) {
					e.printStackTrace();
				}
				mHandler.obtainMessage(29, retStr).sendToTarget();
			}
		}).start();
	}

	/**
	 * 同步数据 数据同步的策略为保证移动端没有文件过滤空文件。服务端在没有闭环的情况下，是任意下载。
	 * 
	 */
	protected void syncDBFiles() {

		boolean patrolTask = false;
		try {
			// 用户部门数据,如果有其它用户登陆需要取一次用户信息
			TB_USER user = userDao.getUSER(loginUser.getUSERNAME(),
					loginUser.getPASSWORD());
			if (user != null
					&& user.getPASSWORD().equals(loginUser.getPASSWORD())) {
				Log.w(this.getClass().getName(), "文件已存在，暂时不下载用户信息库!");
			} else {
				patrolTask = NetAdapter.downLoadPatrolUserDB(loginUser
						.getUSERNAME());
				if (patrolTask) {
					retStr += "\n用户信息数据更新成功！";
				} else {
					retStr += "\n用户信息数据更新失败！";
				}
			}

			// 任务数据
			String taskPath = DBHelper.DB_PATH + "/" + loginUser.getUSERNAME()
					+ "/TRNTASK.DB";
			File taskFile = new File(taskPath);

			TaskDao taskDao = new TaskDao(context);

			if (taskFile.exists()
					&& !taskDao.isEmptyTask("/" + loginUser.getUSERNAME()
							+ "/TRNTASK.DB")) {
				Log.w(this.getClass().getName(), "文件已存在，暂时不下载任务库!");
				patrolTask = NetAdapter.downLoadPatrolTaskDBADD(loginUser
						.getUSERNAME());
				
				if (patrolTask) {
					String bakPath = "/" + loginUser.getUSERNAME() + "/TRNTASKBAK.DB";
					InitTaskDao initTaskDao  = new InitTaskDao(context,bakPath);
					initTaskDao.initTargetDbData();
					retStr += "\n巡视任务数据更新成功！";
				} else {
					retStr += "\n巡视任务数据更新失败！";
				}
			} else {
				patrolTask = NetAdapter.downLoadPatrolTaskDB(loginUser
						.getUSERNAME());
				if (patrolTask) {
					retStr += "\n巡视任务数据更新成功！";
				} else {
					retStr += "\n巡视任务数据更新失败！";
				}
			}
			// 消缺数据
			String defectPath = DBHelper.DB_PATH + "/"
					+ loginUser.getUSERNAME() + "/TRNTASKDEFECT.DB";
			File defectFile = new File(defectPath);

			TaskDefectDao taskDefectDao = new TaskDefectDao(context);

			if (defectFile.exists()
					&& !taskDefectDao.isEmptyTask("/" + loginUser.getUSERNAME()
							+ "/TRNTASKDEFECT.DB")) {
				patrolTask = NetAdapter.downLoadPatrolDefectDBADD(loginUser
						.getUSERNAME());
				if (patrolTask) {
					
					String bakPath = "/" + loginUser.getUSERNAME() + "/TRNTASKDEFECTBAK.DB";
					InitDefectTaskDao initTaskDao  = new InitDefectTaskDao(context,bakPath);
					initTaskDao.initTargetDbData();
					retStr += "\n巡视消缺任务数据更新成功！";
				} else {
					retStr += "\n巡视消缺任务数据更新失败！";
				}
			} else {
				patrolTask = NetAdapter.downLoadPatrolDefectDB(loginUser
						.getUSERNAME());
				if (patrolTask) {
					retStr += "\n巡视消缺任务数据更新成功！";
				} else {
					retStr += "\n巡视消缺任务数据更新失败！";
				}
			}

			// 台账数据
			String assetPath = DBHelper.DB_PATH + "/" + loginUser.getUSERNAME()
					+ "/TRNASSET.DB";
			File assetFile = new File(assetPath);
			if (assetFile.exists()) {
				Log.w(this.getClass().getName(), "文件已存在，暂时不下载台帐库!");
			} else {
				patrolTask = NetAdapter.downLoadPatrolAssetDB(loginUser
						.getUSERNAME());
				if (patrolTask) {
					retStr += "\n台账数据更新成功！";
				} else {
					retStr += "\n台账数据更新失败！";
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 弹出设置按钮对话框
	 */
	private void showSettingDialog() {
		dialog = new SetCustomDialog(LoginActivity.this, R.style.my_dailog);
		dialog.show();
		// 取消按钮
		dialog.btn_set_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dialog != null) {
					dialog.cancel();
					dialog = null;
				}
			}
		});
		// 确定按钮
		dialog.btn_set_sure.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String serverIp = dialog.edt_set_server_ip.getText().toString();
				String serverPort = dialog.edt_set_server_port.getText()
						.toString();

				if ("".equals(serverIp.trim())) {
					AudioTipsUtils.showMsg(context, "注意,服务器IP不能为空!");
					return;
				} else if ("".equals(serverPort.trim())) {
					AudioTipsUtils.showMsg(context, "注意,服务端口不能为空!");
					return;
				}
				SharedPreferencesUtil spUtil = new SharedPreferencesUtil(
						context);
				serverIp = serverIp.replaceAll(" ", "");
				spUtil.saveHostIp(serverIp);
				spUtil.saveHostPort(serverPort);
				WSUtils.init(serverIp, serverPort);
				AudioTipsUtils.showMsg(context, "数据修改成功!");
				if (dialog != null) {
					dialog.cancel();
					dialog = null;
				}
			}
		});
	}

	protected void update() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(DBHelper.DB_PATH,updateFileName)),
				"application/vnd.android.package-archive");
		startActivity(intent);
	}

	protected void requestSure() {
		showProgressDialog("正在下载新版本");
		new Thread() {
			public void run() {
				boolean isOk = false;
				String url = updateUrl;
				updateFileName = url.substring(url.lastIndexOf("/") + 1,
						url.length());
				if (USBNetUtils.isOnUSBNet()) {
					try {
						isOk = USBNetUtils.downloadFileAndSave(updateUrl,
								updateFileName, DBHelper.DB_PATH);

					} catch (Exception e) {
						isOk = false;
						e.printStackTrace();
					}
				} else {
					String fileNamePath = DBHelper.DB_PATH + "/" + updateFileName;
					try {
						isOk = FileUtil.downFile(updateUrl, fileNamePath);
					} catch (Exception e) {
						isOk = false;
						e.printStackTrace();
					}
				}
				if (isOk) {
					mHandler.post(new Runnable() {
						public void run() {
							hideProgressDialog();
							update();
						}
					});
				} else {// 下载失败
					mHandler.obtainMessage(9, "应用程序安装文件下载失败").sendToTarget();
				}

			}
		}.start();
	}

	/**
	 * 记录用户到本地
	 */
	private void saveUserToLocal(TB_USER user) {
		SharedPreferences preferences = getSharedPreferences("lastLoginUser",
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("userName", user.getUSERNAME());
		editor.putString("realName", user.getREALNAME());
		editor.putString("bz", user.getCLASSNAME());
		editor.putString("jh", user.getGROUPNAME());
		editor.commit();
	}

	private TB_USER getLastedLoginUser() {
		SharedPreferences preferences = getSharedPreferences("lastLoginUser",
				Context.MODE_PRIVATE);
		TB_USER user = new TB_USER();
		user.setUSERNAME(preferences.getString("userName", ""));
		user.setREALNAME(preferences.getString("realName", ""));
		user.setCLASSNAME(preferences.getString("bz", ""));
		user.setGROUPNAME(preferences.getString("jh", ""));
		return user;
	}

	/**
	 * 判断是否已经安装过，安装过先清除数据
	 */
	private void importDBFiles() {
		SharedPreferences sp = getSharedPreferences("isInstalled", MODE_PRIVATE);
		boolean isInstalled = sp.getBoolean("isInstalled", false);
		if(!isInstalled){
			FileUtil.deleteFile(DBHelper.DB_PATH);
			Editor editor = sp.edit();
			editor.putBoolean("isInstalled", true);
			editor.commit();
		}
		
		DBFileImporter dbImporter = new DBFileImporter(context);
		for (int i = 0; i < Const.dbFileNames.length; i++) {
			if (!new File(DBHelper.DB_PATH, Const.dbFileNames[i]).exists()) {
				dbImporter.importDB(Const.dbFileNames[i]);
			}
		}
	}

	/**
	 * １、获取版本号 　２、申请更新升级接口 ３、若升级，下载、安装
	 */
	protected boolean compareVersion() {
		String versionName = MyApplication.getInstance().getVersion();
		String appName = getResources().getString(R.string.app_name);
		String updateInfo = NetAdapter.updateSoft(versionName, appName);
		if (StringUtils.isNull(updateInfo)) {
			return false;
		} else {
			try {
				JSONObject jb = new JSONObject(updateInfo);
				if ("0".equals(jb.getString("resultCode"))) {
					updateUrl = jb.getString("resultDesc");
					return true;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return false;
		}

	}

	@Override
	protected void onDestroy() {
		hideProgressDialog();
		super.onDestroy();
	}

	@Override
	protected void onRestart() {
		alreadyLogin = false;
		super.onRestart();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			new AlertDialog.Builder(this)
					.setTitle("提示")
					.setMessage("是否确定退出应用？")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									finish();
									((MyApplication)getApplication()).onTerminate();
								}

							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// 右键事件
									dialog.cancel();
								}
							}).show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
