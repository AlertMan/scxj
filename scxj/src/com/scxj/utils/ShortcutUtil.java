package com.scxj.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;

public class ShortcutUtil {

	public static void createShortCut(Activity act, int iconResId,
			int appnameResId) {
		if(IfaddShortCut(act,appnameResId)){
			// com.android.launcher.permission.INSTALL_SHORTCUT
			Intent shortcutintent = new Intent(
					"com.android.launcher.action.INSTALL_SHORTCUT");
			// 不允许重复创建
			shortcutintent.putExtra("duplicate", false);
			// 需要现实的名称
			shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
					act.getString(appnameResId));
			// 快捷图片
			Parcelable icon = Intent.ShortcutIconResource.fromContext(
					act.getApplicationContext(), iconResId);
			shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
			// 点击快捷图片，运行的程序主入口
			shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,
					new Intent(act.getApplicationContext(), act.getClass()));
			// 发送广播
			act.sendBroadcast(shortcutintent);
		}
	}

	private static boolean IfaddShortCut(Activity act, int appResId) {
		boolean isInstallShortcut = false;
		final ContentResolver cr = act.getContentResolver();
		final String AUTHORITY = "com.android.launcher.settings";
		final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
				+ "/favorites?notify=true");
		Cursor c = cr.query(CONTENT_URI,
				new String[] { "title", "iconResource" }, "title=?",
				new String[] { act.getString(appResId) }, null);// XXX表示应用名称。
		if (c != null && c.getCount() > 0) {
			isInstallShortcut = true;
		}
		return isInstallShortcut;
	}
}
