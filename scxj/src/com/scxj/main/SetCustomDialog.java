package com.scxj.main;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.scxj.R;
import com.scxj.utils.SharedPreferencesUtil;
import com.scxj.utils.StringUtils;

public class SetCustomDialog extends Dialog{
	
	public Button btn_set_cancel,btn_set_sure;
	public EditText edt_set_server_ip,edt_set_server_port;
	private Context mContext;

	public SetCustomDialog(Context context) {
		super(context);
		this.mContext = context;
		
	}
	
	public SetCustomDialog(Context context, int theme){
		super(context, theme);
		this.mContext = context;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set);
		
		btn_set_cancel = (Button)this.findViewById(R.id.btn_set_cancel);
		edt_set_server_ip = (EditText)this.findViewById(R.id.edt_set_server_ip);
		edt_set_server_port = (EditText)this.findViewById(R.id.edt_set_server_port);
		btn_set_sure = (Button)this.findViewById(R.id.btn_set_sure);
		SharedPreferencesUtil util = new SharedPreferencesUtil(mContext);
		String ipStr = util.readHostIp();
		if(StringUtils.isNull(ipStr)){
			ipStr= "192.168.0.53";
		}
		edt_set_server_ip.setText(ipStr);
		String portStr = util.readHostPort();
		if(StringUtils.isNull(portStr)){
			portStr= "8080";
		}
		edt_set_server_port.setText(portStr);
	}

}
