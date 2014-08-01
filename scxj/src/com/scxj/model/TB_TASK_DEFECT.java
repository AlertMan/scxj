/***********************************************************************
 * Module:  TB_DEFECT.java
 * Author:  think
 * Purpose: Defines the Class TB_DEFECT
 ***********************************************************************/
package com.scxj.model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.text.GetChars;

/** 设备缺陷信息
 * @pdOid 5c43d7c2-654e-4826-b4b8-410e065a7864 */
public class TB_TASK_DEFECT implements Serializable { 
	private String DEFECTTASKID;//	消缺任务编号	TEXT
	private String DEFECTTASKNAME;//	消缺任务名称	TEXT
	private String SIGNPERSON;//	制单人	TEXT
	private String FINDTIME;//	发现时间	TEXT
	private String CLASSNAME;//	班组	TEXT
	private String STARTCLASSNAME;//	发起班组	TEXT
	private String DEFECTTYPE;//	缺陷、隐患类别	TEXT
	private String FINISHTIME;//	处理期限	TEXT
	private String DEFECTDESC;//	缺陷、隐患描述	TEXT
	private String ACTIONCLASSNAME;//	执行班组名	TEXT
	private String LOGINUSERNAME;//	领单人	TEXT
	private String DOWNLOADTIME;//	领单时间	TEXT
	private String RETDESC;//	消除隐患、缺陷情况	TEXT
	private String LEADUSERNAME;//	削除隐患、缺陷负责人	TEXT
	private String SUCESSTIME;//	消除职中、缺陷时间	TEXT
	private String RETAINFIELD;//	预留字段时间	TEXT
	private String LINEID;//	所属线路ID	TEXT
	private String LINENAME;//	所属线路名称	TEXT
	private String TOWERID;//	所属村塔ID	TEXT
	private String TOWERNAME;//	所属杆塔名称	TEXT
	private String STATUS;//任务状态
	
	
	public String getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
	public TB_TASK_DEFECT() {
		super();
		DEFECTTASKID = "";
		DEFECTTASKNAME = "";
		SIGNPERSON = "";
		FINDTIME = "";
		CLASSNAME = "";
		STARTCLASSNAME = "";
		DEFECTTYPE = "";
		FINISHTIME = "";
		DEFECTDESC = "";
		ACTIONCLASSNAME = "";
		LOGINUSERNAME = "";
		DOWNLOADTIME = "";
		RETDESC = "";
		LEADUSERNAME = "";
		SUCESSTIME = "";
		RETAINFIELD = "";
		LINEID = "";
		LINENAME = "";
		TOWERID = "";
		TOWERNAME = "";
	}
	public String getDEFECTTASKID() {
		return DEFECTTASKID;
	}
	public void setDEFECTTASKID(String dEFECTTASKID) {
		DEFECTTASKID = dEFECTTASKID;
	}
	public String getDEFECTTASKNAME() {
		return DEFECTTASKNAME;
	}
	public void setDEFECTTASKNAME(String dEFECTTASKNAME) {
		DEFECTTASKNAME = dEFECTTASKNAME;
	}
	public String getSIGNPERSON() {
		return SIGNPERSON;
	}
	public void setSIGNPERSON(String sINGPERSON) {
		SIGNPERSON = sINGPERSON;
	}
	public String getFINDTIME() {
		return FINDTIME;
	}
	public void setFINDTIME(String fINDTIME) {
		FINDTIME = fINDTIME;
	}
	public String getCLASSNAME() {
		return CLASSNAME;
	}
	public void setCLASSNAME(String cLASSNAME) {
		CLASSNAME = cLASSNAME;
	}
	public String getSTARTCLASSNAME() {
		return STARTCLASSNAME;
	}
	public void setSTARTCLASSNAME(String sTARTCLASSNAME) {
		STARTCLASSNAME = sTARTCLASSNAME;
	}
	public String getDEFECTTYPE() {
		return DEFECTTYPE;
	}
	public void setDEFECTTYPE(String dEFECTTYPE) {
		DEFECTTYPE = dEFECTTYPE;
	}
	public String getFINISHTIME() {
		return FINISHTIME;
	}
	public void setFINISHTIME(String fINISHTIME) {
		FINISHTIME = fINISHTIME;
	}
	public String getDEFECTDESC() {
		return DEFECTDESC;
	}
	public void setDEFECTDESC(String dEFECTDESC) {
		DEFECTDESC = dEFECTDESC;
	}
	public String getACTIONCLASSNAME() {
		return ACTIONCLASSNAME;
	}
	public void setACTIONCLASSNAME(String aCTIONCLASSNAME) {
		ACTIONCLASSNAME = aCTIONCLASSNAME;
	}
	public String getLOGINUSERNAME() {
		return LOGINUSERNAME;
	}
	public void setLOGINUSERNAME(String lOGINUSERNAME) {
		LOGINUSERNAME = lOGINUSERNAME;
	}
	public String getDOWNLOADTIME() {
		return DOWNLOADTIME;
	}
	public void setDOWNLOADTIME(String dOWNLOADTIME) {
		DOWNLOADTIME = dOWNLOADTIME;
	}
	public String getRETDESC() {
		return RETDESC;
	}
	public void setRETDESC(String rETDESC) {
		RETDESC = rETDESC;
	}
	public String getLEADUSERNAME() {
		return LEADUSERNAME;
	}
	public void setLEADUSERNAME(String lEADUSERNAME) {
		LEADUSERNAME = lEADUSERNAME;
	}
	public String getSUCESSTIME() {
		return SUCESSTIME;
	}
	public void setSUCESSTIME(String sUCESSTIME) {
		SUCESSTIME = sUCESSTIME;
	}
	public String getRETAINFIELD() {
		return RETAINFIELD;
	}
	public void setRETAINFIELD(String rETAINFIELD) {
		RETAINFIELD = rETAINFIELD;
	}
	public String getLINEID() {
		return LINEID;
	}
	public void setLINEID(String lINEID) {
		LINEID = lINEID;
	}
	public String getLINENAME() {
		return LINENAME;
	}
	public void setLINENAME(String lINENAME) {
		LINENAME = lINENAME;
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
	
	
	@Override
	public boolean equals(Object o) {
		if(o == null){
			return false;
		}
		
		if(o instanceof TB_TASK_DEFECT){
			TB_TASK_DEFECT obj = (TB_TASK_DEFECT)o;
			return getDEFECTTASKID().equals(obj.getDEFECTTASKID());
		}
		return super.equals(o);
	}
	
}