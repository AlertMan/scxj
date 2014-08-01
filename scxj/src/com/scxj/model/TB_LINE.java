/***********************************************************************
 * Module:  TB_LINE.java
 * Author:  think
 * Purpose: Defines the Class TB_LINE
 ***********************************************************************/
package com.scxj.model;
import java.io.Serializable;
import java.util.*;

/** 线路详细信息
 * 
 * @pdOid 44fb6b14-00fb-4cb4-b5fe-ee4ef998d6ac */
public class TB_LINE implements Serializable{
	private String LINEID;//	线路ID
	private String LINENAME;//	线路名称
	private String TASKID;//	任务ID
	private String LINETYPE;//	线路所属类型
	private String LINEMODEL;//	线路型号
	private String ORGID;//	线路所属供电局
	private String STARTTOWERID;//	线路起点杆塔ID
	private String ENDTOWERID;//	线路终点杆塔ID
	private String LINELEN;//	线路长度描述
	private String LINESUBTYPE;//	线路子类型
	private String VOLTRANK;//	电压等级
	private String LEFTGRNDMODEL;//	左接地线型号
	private String RIGHTGRNDMODEL;//	右接地线型号
	private String RATAINFIELD;//	预留字段
	
	private TB_TOWER tower;//线路对应的杆塔
	
	public TB_LINE() {
		super();
		LINEID = "";
		LINENAME = "";
		TASKID = "";
		LINETYPE = "";
		LINEMODEL = "";
		ORGID = "";
		STARTTOWERID = "";
		ENDTOWERID = "";
		LINELEN = "";
		LINESUBTYPE = "";
		VOLTRANK = "";
		LEFTGRNDMODEL = "";
		RIGHTGRNDMODEL = "";
		RATAINFIELD = "";
	}
	public TB_TOWER getTower() {
		return tower;
	}
	public void setTower(TB_TOWER tower) {
		this.tower = tower;
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
	public String getTASKID() {
		return TASKID;
	}
	public void setTASKID(String tASKID) {
		TASKID = tASKID;
	}
	public String getLINETYPE() {
		return LINETYPE;
	}
	public void setLINETYPE(String lINETYPE) {
		LINETYPE = lINETYPE;
	}
	public String getLINEMODEL() {
		return LINEMODEL;
	}
	public void setLINEMODEL(String lINEMODEL) {
		LINEMODEL = lINEMODEL;
	}
	public String getORGID() {
		return ORGID;
	}
	public void setORGID(String oRGID) {
		ORGID = oRGID;
	}
	public String getSTARTTOWERID() {
		return STARTTOWERID;
	}
	public void setSTARTTOWERID(String sTARTTOWERID) {
		STARTTOWERID = sTARTTOWERID;
	}
	public String getENDTOWERID() {
		return ENDTOWERID;
	}
	public void setENDTOWERID(String eNDTOWERID) {
		ENDTOWERID = eNDTOWERID;
	}
	public String getLINELEN() {
		return LINELEN;
	}
	public void setLINELEN(String lINELEN) {
		LINELEN = lINELEN;
	}
	public String getLINESUBTYPE() {
		return LINESUBTYPE;
	}
	public void setLINESUBTYPE(String lINESUBTYPE) {
		LINESUBTYPE = lINESUBTYPE;
	}
	public String getVOLTRANK() {
		return VOLTRANK;
	}
	public void setVOLTRANK(String vOLTRANK) {
		VOLTRANK = vOLTRANK;
	}
	public String getLEFTGRNDMODEL() {
		return LEFTGRNDMODEL;
	}
	public void setLEFTGRNDMODEL(String lEFTGRNDMODEL) {
		LEFTGRNDMODEL = lEFTGRNDMODEL;
	}
	public String getRIGHTGRNDMODEL() {
		return RIGHTGRNDMODEL;
	}
	public void setRIGHTGRNDMODEL(String rIGHTGRNDMODEL) {
		RIGHTGRNDMODEL = rIGHTGRNDMODEL;
	}
	public String getRATAINFIELD() {
		return RATAINFIELD;
	}
	public void setRATAINFIELD(String rATAINFIELD) {
		RATAINFIELD = rATAINFIELD;
	}
	
	
	
	
	
	
   
}