package com.scxj.utils;

import java.io.File;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.scxj.R;

public class AppUpgradeService extends Service{

    public static final int APP_VERSION_LATEST = 0;
    public static final int APP_VERSION_OLDER = 1;

    public static final int mNotificationId = 100;
    private String mDownloadUrl = null;
    private String localVersion = null;
    private String appName = null;
    private NotificationManager mNotificationManager = null;
    private Notification mNotification = null;
    private PendingIntent mPendingIntent = null;

    private File destDir = null;
    private File destFile = null;

    private static final int DOWNLOAD_FAIL = -1;
    private static final int DOWNLOAD_SUCCESS = 0;
    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case DOWNLOAD_SUCCESS:
                Toast.makeText(getApplicationContext(), "下载成功", Toast.LENGTH_LONG).show();
                install(destFile);
                break;
            case DOWNLOAD_FAIL:
                Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_LONG).show();
                mNotificationManager.cancel(mNotificationId);
                break;
            default:
                break;
            }
        }

    };
    private DownloadUtils.DownloadListener downloadListener = new DownloadUtils.DownloadListener() {
        @Override
        public void downloading(int progress) {
            mNotification.contentView.setProgressBar(R.id.app_upgrade_progressbar, 100, progress, false);
            mNotification.contentView.setTextViewText(R.id.app_upgrade_progresstext, progress + "%");
            mNotificationManager.notify(mNotificationId, mNotification);
        }

        @Override
        public void downloaded() {
            mNotification.contentView.setViewVisibility(R.id.app_upgrade_progressblock, View.GONE);
            mNotification.defaults = Notification.DEFAULT_SOUND;
            mNotification.contentIntent = mPendingIntent;
            mNotification.contentView.setTextViewText(R.id.app_upgrade_progresstext, "下载完成");
            mNotificationManager.notify(mNotificationId, mNotification);
            if (destFile.exists() && destFile.isFile() && checkApkFile(destFile.getPath())) {
                Message msg = mHandler.obtainMessage();
                msg.what = DOWNLOAD_SUCCESS;
                mHandler.sendMessage(msg);
            }
            mNotificationManager.cancel(mNotificationId);
        }
    };
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mDownloadUrl = intent.getStringExtra("downloadUrl");
        localVersion = intent.getStringExtra("localVersion");
        appName = intent.getStringExtra("appName");
        Log.d("AppUpgradeService->mDownloadUrl", mDownloadUrl);

        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
            destDir = new File(Environment.getExternalStorageDirectory().getPath());
            if (destDir.exists()) {
                File destFile = new File(destDir.getPath() + "/" + appName);
                if (destFile.exists() && destFile.isFile() && checkApkFile(destFile.getPath())) {
                    install(destFile);
                    stopSelf();
                    return super.onStartCommand(intent, flags, startId);
                }
            }
        }else {
            return super.onStartCommand(intent, flags, startId);
        }

        mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mNotification = new Notification();

        mNotification.contentView = new RemoteViews(getApplication().getPackageName(), R.layout.app_upgrade_notification);

        Intent completingIntent = new Intent();
        completingIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        completingIntent.setClass(getApplication().getApplicationContext(), AppUpgradeService.class);

        mPendingIntent = PendingIntent.getActivity(AppUpgradeService.this, R.string.app_name, completingIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mNotification.icon = R.drawable.ic_launcher;
        mNotification.tickerText = "下载";
        mNotification.contentIntent = mPendingIntent;
        mNotification.contentView.setProgressBar(R.id.app_upgrade_progressbar, 100, 0, false);
        mNotification.contentView.setTextViewText(R.id.app_upgrade_progresstext, "0%");
        mNotificationManager.cancel(mNotificationId);
        mNotificationManager.notify(mNotificationId, mNotification);
        new AppUpgradeThread().start();
        return super.onStartCommand(intent, flags, startId);
    }

    class AppUpgradeThread extends Thread{

        @Override
        public void run() {
            if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
                if (destDir == null) {
                    destDir = new File(Environment.getExternalStorageDirectory().getPath());
                }
                if (destDir.exists() || destDir.mkdirs()) {
                    destFile = new File(destDir.getPath() + "/" + appName);
                    if (destFile.exists() && destFile.isFile() && checkApkFile(destFile.getPath())) {
                        install(destFile);
                    } else {
                        try {
                            DownloadUtils.download(mDownloadUrl, destFile, false, downloadListener);
                        } catch (Exception e) {
                            Message msg = mHandler.obtainMessage();
                            msg.what = DOWNLOAD_FAIL;
                            mHandler.sendMessage(msg);
                            e.printStackTrace();
                        }
                    }
                }
            }
            stopSelf();
        }
    }

    /**
     * �?���?��APK文件，加�?��版本验证
     * @param apkFilePath
     * @return
     */
    public boolean checkApkFile(String apkFilePath) {
        boolean result = false;
        try{
            PackageManager pManager = getPackageManager();
            PackageInfo pInfo = pManager.getPackageArchiveInfo(apkFilePath, PackageManager.GET_ACTIVITIES);
            String version = pInfo.versionName;
            String pakageName = pInfo.packageName;
            
            PackageInfo localPInfo =  pManager.getPackageInfo(getPackageName(), 0);
            String localPakageName = localPInfo.packageName;
            if(pInfo != null && version != null && localPakageName != null && pakageName != null && pakageName.equals(localPakageName) && version.compareTo(localVersion) > 0){
                result =  true;
            }
        } catch(Exception e) {
            result =  false;
            e.printStackTrace();
        }
        return result;
    }

    public void install(File apkFile){
        Uri uri = Uri.fromFile(apkFile);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        startActivity(intent);
    }
}