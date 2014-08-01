package com.scxj.model;

import java.io.Serializable;

public class TB_TASK implements Serializable {
	private String TASKID;//	任务ID
	private String TASKNAME;//	任务描述（名称）
	private String TASKTYPE;//	任务类型(日常巡视、特殊巡视)
	private String BEGINDATE;//	计划开始时间
	private String ENDDATE;//	计划结束时间
	private String STATUS;//	任务状态
	private String PLANPERSON;//	计划执行人
	private String CREATETIME;//	任务下达时间任务下达时间（任务下载的服务器时间，用于校准终端时间）
	private String TASKDEMAND;//	任务要求
	private String ACTSTARTDATE;//	任务实际开始时间
	private String ACTENDDATE;//	任务实际结束时间
	private String TEMP;//	温度
	private String WEATHER;//	天气
	private String STANDARD;//	工作标准
	private String RATAINFIELD;//	预留字段
	private String WORKDATE;//	任务周期描述
	
	
	private TB_LINE line;//巡视任务对应的线路信息
	
	public TB_TASK() {
		super();
		TASKID = "";
		TASKNAME = "";
		TASKTYPE = "";
		BEGINDATE = "";
		ENDDATE = "";
		STATUS = "";
		PLANPERSON = "";
		CREATETIME = "";
		TASKDEMAND = "";
		ACTSTARTDATE = "";
		ACTENDDATE = "";
		TEMP = "";
		WEATHER = "";
		STANDARD = "";
		RATAINFIELD = "";
		WORKDATE = "";
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
	public String getTASKTYPE() {
		return TASKTYPE;
	}
	public void setTASKTYPE(String tASKTYPE) {
		TASKTYPE = tASKTYPE;
	}
	public String getBEGINDATE() {
		return BEGINDATE;
	}
	public void setBEGINDATE(String bEGINDATE) {
		BEGINDATE = bEGINDATE;
	}
	public String getENDDATE() {
		return ENDDATE;
	}
	public void setENDDATE(String eNDDATE) {
		ENDDATE = eNDDATE;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
	public String getPLANPERSON() {
		return PLANPERSON;
	}
	public void setPLANPERSON(String pLANPERSON) {
		PLANPERSON = pLANPERSON;
	}
	public String getCREATETIME() {
		return CREATETIME;
	}
	public void setCREATETIME(String cREATETIME) {
		CREATETIME = cREATETIME;
	}
	public String getTASKDEMAND() {
		return TASKDEMAND;
	}
	public void setTASKDEMAND(String tASKDEMAND) {
		TASKDEMAND = tASKDEMAND;
	}
	public String getACTSTARTDATE() {
		return ACTSTARTDATE;
	}
	public void setACTSTARTDATE(String aCTSTARTDATE) {
		ACTSTARTDATE = aCTSTARTDATE;
	}
	public String getACTENDDATE() {
		return ACTENDDATE;
	}
	public void setACTENDDATE(String aCTENDDATE) {
		ACTENDDATE = aCTENDDATE;
	}
	public String getTEMP() {
		return TEMP;
	}
	public void setTEMP(String tEMP) {
		TEMP = tEMP;
	}
	public String getWEATHER() {
		return WEATHER;
	}
	public void setWEATHER(String wEATHER) {
		WEATHER = wEATHER;
	}
	public String getSTANDARD() {
		return STANDARD;
	}
	public void setSTANDARD(String sTANDARD) {
		STANDARD = sTANDARD;
	}
	public String getRATAINFIELD() {
		return RATAINFIELD;
	}
	public void setRATAINFIELD(String rATAINFIELD) {
		RATAINFIELD = rATAINFIELD;
	}
	public String getWORKDATE() {
		return WORKDATE;
	}
	public void setWORKDATE(String wORKDATE) {
		WORKDATE = wORKDATE;
	}
	public TB_LINE getLine() {
		return line;
	}
	public void setLine(TB_LINE line) {
		this.line = line;
	}
	
	
	@Override
	public boolean equals(Object o) {
		if(o == null){
			return false;
		}
		
		if(o instanceof TB_TASK){
			TB_TASK obj = (TB_TASK)o;
			return TASKID.equals(obj.getTASKID());
		}
		return false;
	}
	
	
}
