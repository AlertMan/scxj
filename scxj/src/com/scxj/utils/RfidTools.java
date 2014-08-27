package com.scxj.utils;

import java.util.List;


import com.cetc7.UHFReader.UHFReaderClass;
import com.cetc7.UHFReader.UHFReaderClass.OnEPCsListener;

public class RfidTools {

	static UHFReaderClass UHFRFID = new UHFReaderClass();
//	private static UHFReaderClass UHFRFID = UHFReaderClass.GetUHFReader();
//	
//	private static RfidTools instance = null;
//
//	public static RfidTools getInstance() {
//		if (instance == null) {
//			instance = new RfidTools();
//			UHFRFID = new UHFReaderClass();
//		}
//		return instance;
//	}

	public boolean SetPower(boolean OnorOff) {
		if (UHFRFID.SetPower(OnorOff) == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 打开rfid链接
	 * 
	 * **/
	public static boolean openRfid() {
		closeRfid(); 
//		System.out.println("=UHFRFID.SetPower(true)==="+UHFRFID.SetPower(true));	
//		System.out.println("=UHFRFID.Connect()==="+UHFRFID.Connect());
		if (/*UHFRFID.SetPower(true) == 0 && */UHFRFID.Connect() == 0) {	
			return true;
		} else {
			return false;
		}

	}
	



	/***
	 * 断开rfid链接
	 * 
	 * **/
	public static boolean closeRfid() {

		if (UHFRFID.DisConnect() == 0 && UHFRFID.SetPower(false) == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 设置工作模式为主从模式 当模块工作在主从模式下时，调用一次ReadEPCs()函数， 模块读一次标 签并将EPC码返回结果
	 * 
	 * **/

	public static boolean setSuperiorModel() {
		if (UHFRFID.SetReaderMode(0) == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 设置工作模式为触发模式 当模块工作在触发模式下时，调用TirggerStart(OnEPCsListener
	 * listener)函数，模块将连续不断进行扫描标签，直至TirggerStop()被调用。 此期间，用户程序不断的通过listener
	 * 接收并处理EPC码。
	 * 
	 * */

	public static boolean setBurstModel() {
		if (UHFRFID.SetReaderMode(2) == 0) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean triggerStart(OnEPCsListener onEPCsListener){

		if(UHFRFID.TirggerStart(onEPCsListener)==0){
			return true;
		}else{
			return false;	
		}

	}
	
	public static boolean triggerStop(){

		if(	UHFRFID.TriggerStop()==0){
			return true;
		}else{
			return false;	
		}

	}

	/**
	 * 获取工作模式 0 为主从模式 ； 2 为触发模式
	 * */

	public int GetReaderMode() {
		return UHFRFID.GetReaderMode();
	}

	/**
	 * 设置功放衰减值 有效值范围为0--19。 0：功率不衰减（最大功率）； 1：功率衰减1dB； 2：功率衰减2dB；依此类推。
	 * **/
	public static int SetRFAttenuation(int RF) {
		if (UHFRFID.SetRFAttenuation(RF) < 0) {
			return RF;
		} else {
			return RF;
		}
	}

	/**
	 * 获取功放衰减值
	 * 
	 * */
	public int GetRFAttenuation() {
		if (UHFRFID.GetFrequency() == -1) {
			return UHFRFID.GetFrequency();
		} else {

			return UHFRFID.GetFrequency();
		}

	}

	/********
	 * 设置工作频率 0：跳频；1--49：各频率点；
	 * 
	 * ********/
	public static int SetFrequency(int Channel) {
		if (UHFRFID.SetFrequency(Channel) < 0) {
			return UHFRFID.SetFrequency(Channel);
		} else {
			return UHFRFID.SetFrequency(Channel);
		}
	}

	/**
	 * 获取工作频率
	 * 
	 * 
	 * **/
	public int GetFrequency() {
		if (UHFRFID.GetFrequency() == -1) {
			return UHFRFID.GetFrequency();
		} else {

			return UHFRFID.GetFrequency();
		}
	}

	/**
	 * 设置RF开关
	 * 
	 * ***/
	public static boolean SetRF(boolean OpenOrClose) {
		if (UHFRFID.SetRF(OpenOrClose) < 0) {
			return false;
		} else {
			return true;
		}

	}

	/**
	 * Q值设置
	 * **/
	public static boolean SetQValue(int iValue) {
		byte bData = (byte) iValue;
		if (UHFRFID.SetQValue(bData) == 0)
			return true;
		else
			return false;
	}

	/**
	 * 获取Q值
	 * **/
	public int GetQValue() {
		int iResult = -1;
		if (UHFRFID.GetQValue() == iResult) {
			return UHFRFID.GetQValue();
		} else {
			return UHFRFID.GetQValue();
		}
	}


	/**
	 * 主从模式下读标签
	 * ***/
	public static String ReadEPC(){
		List<String> epcs = ReadEPCs();
		if (epcs != null && epcs.size() > 0 && epcs.get(0) != null) {
			return epcs.get(0);
		}
		return null;
	}
	
	/**
	 * 主从模式下读标签
	 * ***/
	public static List<String> ReadEPCs(){
		return UHFRFID.ReadEPCs();
	}

}
