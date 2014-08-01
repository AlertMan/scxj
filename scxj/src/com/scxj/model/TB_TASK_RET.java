package com.scxj.model;

import java.io.Serializable;

public class TB_TASK_RET implements Serializable,Cloneable{
	
	private String ID;
	private String TASKID;
	private String TASKNAME;
	private String LINEID;
	private String LINENAME;
	private String MEASURETYPE;
	private String STATUS;
	private String DEFECTLEVEL;
	private String DEFECTCONTENT;
	private String RESULT;
	private String TOWERID;
	private String TOWERNAME;
	private String VOTALLEVEL;
	
	private int selectIndex;
	private int imgCnt = 0;
	
	/**
	 * 初始化 必要的参数
	 * @param tASKID
	 * @param tASKNAME
	 * @param lINEID
	 * @param lINENAME
	 * @param mEASURETYPE
	 * @param sTATUS
	 * @param dEFECTLEVEL
	 * @param dEFECTCONTENT
	 * @param rESULT
	 * @param tOWERID
	 * @param tOWERNAME
	 * @param vOTALLEVEL
	 */
	 
	
	public int getSelectIndex() {
		return selectIndex;
	}
	public void setSelectIndex(int selectIndex) {
		this.selectIndex = selectIndex;
	}
	
	
	public TB_TASK_RET() {
		super();
		TASKID = "";
		TASKNAME = "";
		LINEID = "";
		LINENAME = "";
		MEASURETYPE = "";
		STATUS = "";
		DEFECTLEVEL = "";
		DEFECTCONTENT = "";
		RESULT = "";
		TOWERID = "";
		TOWERNAME = "";
		VOTALLEVEL = "";
	}
	public TB_TASK_RET(String iD, String tASKID, String tASKNAME,
			String lINEID, String lINENAME, String mEASURETYPE, String sTATUS,
			String dEFECTLEVEL, String dEFECTCONTENT, String rESULT,
			String tOWERID, String tOWERNAME, String vOTALLEVEL) {
		super();
		ID = iD;
		TASKID = tASKID;
		TASKNAME = tASKNAME;
		LINEID = lINEID;
		LINENAME = lINENAME;
		MEASURETYPE = mEASURETYPE;
		STATUS = sTATUS;
		DEFECTLEVEL = dEFECTLEVEL;
		DEFECTCONTENT = dEFECTCONTENT;
		RESULT = rESULT;
		TOWERID = tOWERID;
		TOWERNAME = tOWERNAME;
		VOTALLEVEL = vOTALLEVEL;
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
	public String getMEASURETYPE() {
		return MEASURETYPE;
	}
	public void setMEASURETYPE(String mEASURETYPE) {
		MEASURETYPE = mEASURETYPE;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
	public String getDEFECTLEVEL() {
		return DEFECTLEVEL;
	}
	public void setDEFECTLEVEL(String dEFECTLEVEL) {
		DEFECTLEVEL = dEFECTLEVEL;
	}
	public String getDEFECTCONTENT() {
		return DEFECTCONTENT;
	}
	public void setDEFECTCONTENT(String dEFECTCONTENT) {
		DEFECTCONTENT = dEFECTCONTENT;
	}
	public String getRESULT() {
		return RESULT;
	}
	public void setRESULT(String rESULT) {
		RESULT = rESULT;
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
	public String getVOTALLEVEL() {
		return VOTALLEVEL;
	}
	public void setVOTALLEVEL(String vOTALLEVEL) {
		VOTALLEVEL = vOTALLEVEL;
	}
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	
	
	
	@Override
	public boolean equals(Object o) {
		if(o == null){
			return false;
		}
		
		if(o instanceof TB_TASK_RET){
			TB_TASK_RET obj = (TB_TASK_RET)o;
			return getID().equals(obj.getID()) && getMEASURETYPE().equals(obj.getMEASURETYPE()) && getTASKID().equals(obj.getTASKID());
		}
		return super.equals(o);
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		TB_TASK_RET o = null;
        try{
            o = (TB_TASK_RET)super.clone();
        }catch(CloneNotSupportedException e){
            e.printStackTrace();
        }
        return o;
	}
	public int getImgCnt() {
		return imgCnt;
	}
	public void setImgCnt(int imgCnt) {
		this.imgCnt = imgCnt;
	}
	  
}
