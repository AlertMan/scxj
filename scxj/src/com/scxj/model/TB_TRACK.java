package com.scxj.model;
import java.io.Serializable;
import java.util.*;

/** 轨迹表
 * 
 */
public class TB_TRACK implements Serializable {
	 private String TRACKID;
	 private String TASKID;
	 private String TASKNAME;
	 private String CREATETIME;
	 private String LNG;
	 private String LAT;
	 private String STARTADDRESS;
	 private String ENDADDRESS;
	 private String NOTE;
	 
	public TB_TRACK() {
		super();
		TRACKID = "";
		TASKID = "";
		TASKNAME = "";
		CREATETIME = "";
		LNG = "";
		LAT = "";
		STARTADDRESS = "";
		ENDADDRESS = "";
		NOTE = "";
	}
	public String getTRACKID() {
		return TRACKID;
	}
	public void setTRACKID(String tRACKID) {
		TRACKID = tRACKID;
	}
	public String getTASKID() {
		return TASKID;
	}
	public void setTASKID(String tASKID) {
		TASKID = tASKID;
	}
	public String getTASKNAME() {
		return TASKNAME;
	}
	public void setTASKNAME(String tASKNAME) {
		TASKNAME = tASKNAME;
	}
	public String getCREATETIME() {
		return CREATETIME;
	}
	public void setCREATETIME(String cREATETIME) {
		CREATETIME = cREATETIME;
	}
	public String getLNG() {
		return LNG;
	}
	public void setLNG(String lNG) {
		LNG = lNG;
	}
	public String getLAT() {
		return LAT;
	}
	public void setLAT(String lAT) {
		LAT = lAT;
	}
	public String getSTARTADDRESS() {
		return STARTADDRESS;
	}
	public void setSTARTADDRESS(String sTARTADDRESS) {
		STARTADDRESS = sTARTADDRESS;
	}
	public String getENDADDRESS() {
		return ENDADDRESS;
	}
	public void setENDADDRESS(String eNDADDRESS) {
		ENDADDRESS = eNDADDRESS;
	}
	public String getNOTE() {
		return NOTE;
	}
	public void setNOTE(String nOTE) {
		NOTE = nOTE;
	}
   
   

}