package com.scxj.utils;

import com.iflytek.speech.SpeechUtility;
import com.scxj.MyApplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

/**
 * 封装Toast相关操作
 * @author think
 *
 */
public class AudioTipsUtils {
	
	/**
	 * �?��语音组件安装情况
	 * @param context
	 */
	public static  void isSpeeshServiceInstalled(final Context context){
		if (SpeechUtility.getUtility(context).queryAvailableEngines() == null
				|| SpeechUtility.getUtility(context).queryAvailableEngines().length <= 0) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(context);
			dialog.setMessage("程序检测到您尚未安装语音组件,安装语音组件可进行语音提示帮助，现在是否安装主意组件");
			dialog.setNegativeButton("取消", null);			
			dialog.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface, int i) {
//							String url = SpeechUtility.getUtility(getApplicationContext()).getComponentUrl();
							String assetsApk="SpeechService.apk";
							MyApplication.getInstance().processInstall(context,/*url*/null,assetsApk);
						}
					});
			dialog.show();			
		} 
	}

	
	public static void showMsg(Context context,String content){
		showMsg(context, content,1000);
	}
	
	public static void showMsg(Context context,String content,int millTime){
		showMsg(context, content,1000,true);
	}
	
	public static void showMsg(Context context,String content,int millTime,boolean isAudioPlay){
		if(isAudioPlay){
			MyApplication.getInstance().tipsVoicePlayer(content);
		}
		Toast.makeText(context, content, millTime).show();
	}
}
