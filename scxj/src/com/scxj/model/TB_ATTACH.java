package com.scxj.model;

import java.io.File;
import java.io.Serializable;

import com.scxj.fragment.OnDataRefreshListener;

/**
 * 附件表
 * @author think
 *
 */
public class TB_ATTACH implements Serializable {
	private String ATTACHID = "";//ID
	private String ATTACHNAME = "";//图片名称
	private String ATTACHLEN = "";
	private String CREATETIME = "";//拍摄时间
	private String CREATEADDRESS = "";//拍摄地点
	private String ATTACHCONTENT = "" ;//图片BASE64字符串
	private String ATTACHNOTE = "" ;
	private String PARENTID = "" ;
	private String RATAINFIELD = "" ;
	private String ASSETID = "";//设备ＩＤ
	private OnDataRefreshListener selfOnDataRefreshListener;
	
	
	public String getASSETID() {
		return ASSETID;
	}
	public void setASSETID(String aSSETID) {
		ASSETID = aSSETID;
	}
	private String UPLOADSTATUS = "";
	
	
	//主要是解决相机回调的文件流
	private File file;
	
	
	public TB_ATTACH() {
		super();
		ATTACHID = "";
		ATTACHNAME = "";
		ATTACHLEN = "";
		CREATETIME = "";
		CREATEADDRESS = "";
		ATTACHCONTENT = "";
		ATTACHNOTE = "";
		PARENTID = "";
		RATAINFIELD = "";
		UPLOADSTATUS = "未上传";
		ASSETID="";
	}
	public String getATTACHID() {
		return ATTACHID;
	}
	public void setATTACHID(String aTTACHID) {
		ATTACHID = aTTACHID;
	}
	public String getATTACHNAME() {
		return ATTACHNAME;
	}
	public void setATTACHNAME(String aTTACHNAME) {
		ATTACHNAME = aTTACHNAME;
	}
	public String getATTACHLEN() {
		return ATTACHLEN;
	}
	public void setATTACHLEN(String aTTACHLEN) {
		ATTACHLEN = aTTACHLEN;
	}
	public String getCREATETIME() {
		return CREATETIME;
	}
	public void setCREATETIME(String cREATETIME) {
		CREATETIME = cREATETIME;
	}
	public String getCREATEADDRESS() {
		return CREATEADDRESS;
	}
	public void setCREATEADDRESS(String cREATEADDRESS) {
		CREATEADDRESS = cREATEADDRESS;
	}
	public String getATTACHCONTENT() {
		return ATTACHCONTENT;
	}
	public void setATTACHCONTENT(String aTTACHCONTENT) {
		ATTACHCONTENT = aTTACHCONTENT;
	}
	public String getATTACHNOTE() {
		return ATTACHNOTE;
	}
	public void setATTACHNOTE(String aTTACHNOTE) {
		ATTACHNOTE = aTTACHNOTE;
	}
	public String getPARENTID() {
		return PARENTID;
	}
	public void setPARENTID(String pARENTID) {
		PARENTID = pARENTID;
	}
	public String getRATAINFIELD() {
		return RATAINFIELD;
	}
	public void setRATAINFIELD(String rATAINFIELD) {
		RATAINFIELD = rATAINFIELD;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public String getUPLOADSTATUS() {
		return UPLOADSTATUS;
	}
	public void setUPLOADSTATUS(String uPLOADSTATUS) {
		UPLOADSTATUS = uPLOADSTATUS;
	}
	public OnDataRefreshListener getSelfOnDataRefreshListener() {
		return selfOnDataRefreshListener;
	}
	public void setSelfOnDataRefreshListener(OnDataRefreshListener selfOnDataRefreshListener) {
		this.selfOnDataRefreshListener = selfOnDataRefreshListener;
	}
	
	
}
