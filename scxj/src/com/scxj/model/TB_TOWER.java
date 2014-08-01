package com.scxj.model;
import java.io.Serializable;
import java.util.*;

/** 杆塔详细信息表
 * 
 */
public class TB_TOWER implements Serializable{
	
	private String TOWERID;//	杆塔ID
	private String TOWERNAME;//	杆塔描述
	private String TOWERLAT;//	杆塔纬度
	private String TOWERLNG;//	杆塔经度
	private String TOWERTYPE;//	杆塔类型
	private String ORGID;//	所属供电局
	private String SEQ;//	在线路中的顺序号
	private String LINEID;//	所属线路ID
	private String PRETOWERID;//	前一个杆塔的ID
	private String NEXTTOWERID;//	下一个杆塔的ID
	private String TOWERMATERIAL;//	杆的材质
	private String TOWERMODEL;//	杆塔型号
	private String TOWERSYTLE;//	杆塔型式
	private String STATUS;//	杆塔状态是否有异常
	private String RATAINFIELD;//	预留字段
	private String TASKID;//任务ID
	
	private String CHECKDETAIL;//巡检详情
	
	public TB_TOWER() {
		super();
		TOWERID = "";
		TOWERNAME = "";
		TOWERLAT = "";
		TOWERLNG = "";
		TOWERTYPE = "";
		ORGID = "";
		SEQ = "";
		LINEID = "";
		PRETOWERID = "";
		NEXTTOWERID = "";
		TOWERMATERIAL = "";
		TOWERMODEL = "";
		TOWERSYTLE = "";
		STATUS = "";
		RATAINFIELD = "";
		TASKID  = "";
		CHECKDETAIL = null;
	}
	public String getTASKID() {
		return TASKID;
	}
	public void setTASKID(String tASKID) {
		TASKID = tASKID;
	}
	public String getTOWERID() {
		return TOWERID;
	}
	public void setTOWERID(String tOWERID) {
		TOWERID = tOWERID;
	}
	public String getTOWERNAME() {
		return TOWERNAME;
	}
	public void setTOWERNAME(String tOWERNAME) {
		TOWERNAME = tOWERNAME;
	}
	public String getTOWERLAT() {
		return TOWERLAT;
	}
	public void setTOWERLAT(String tOWERLAT) {
		TOWERLAT = tOWERLAT;
	}
	public String getTOWERLNG() {
		return TOWERLNG;
	}
	public void setTOWERLNG(String tOWERLNG) {
		TOWERLNG = tOWERLNG;
	}
	public String getTOWERTYPE() {
		return TOWERTYPE;
	}
	public void setTOWERTYPE(String tOWERTYPE) {
		TOWERTYPE = tOWERTYPE;
	}
	public String getORGID() {
		return ORGID;
	}
	public void setORGID(String oRGID) {
		ORGID = oRGID;
	}
	public String getSEQ() {
		return SEQ;
	}
	public void setSEQ(String sEQ) {
		SEQ = sEQ;
	}
	public String getLINEID() {
		return LINEID;
	}
	public void setLINEID(String lINEID) {
		LINEID = lINEID;
	}
	public String getPRETOWERID() {
		return PRETOWERID;
	}
	public void setPRETOWERID(String pRETOWERID) {
		PRETOWERID = pRETOWERID;
	}
	public String getNEXTTOWERID() {
		return NEXTTOWERID;
	}
	public void setNEXTTOWERID(String nEXTTOWERID) {
		NEXTTOWERID = nEXTTOWERID;
	}
	public String getTOWERMATERIAL() {
		return TOWERMATERIAL;
	}
	public void setTOWERMATERIAL(String tOWERMATERIAL) {
		TOWERMATERIAL = tOWERMATERIAL;
	}
	public String getTOWERMODEL() {
		return TOWERMODEL;
	}
	public void setTOWERMODEL(String tOWERMODEL) {
		TOWERMODEL = tOWERMODEL;
	}
	public String getTOWERSYTLE() {
		return TOWERSYTLE;
	}
	public void setTOWERSYTLE(String tOWERSYTLE) {
		TOWERSYTLE = tOWERSYTLE;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
	public String getRATAINFIELD() {
		return RATAINFIELD;
	}
	public void setRATAINFIELD(String rATAINFIELD) {
		RATAINFIELD = rATAINFIELD;
	}
	public String getCHECKDETAIL() {
		return CHECKDETAIL;
	}
	public void setCHECKDETAIL(String cHECKDETAIL) {
		CHECKDETAIL = cHECKDETAIL;
	}
	
	
	
	
	
}