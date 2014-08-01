package com.scxj.broadcast;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.scxj.service.PreLocationService;

public class AutoStartBroadcast extends BroadcastReceiver {

	public void onReceive(Context context, Intent intent) {
		
		Log.w("AutoStartBroadcast", "开机启动GPS！");
		/* Intent in = new Intent("android.location.GPS_ENABLED_CHANGE");
         intent.putExtra("enabled", true);
         context.sendBroadcast(in);

        String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(!provider.contains("gps")){ //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider"); 
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3")); 
            context.sendBroadcast(poke);
        }*/
        //Service 持续定位
        
        context.startService(new Intent(context, PreLocationService.class));
	}
 
}
