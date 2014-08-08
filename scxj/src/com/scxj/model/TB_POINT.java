
package com.scxj.model;

import java.io.Serializable;

/**
 * 类描述：巡视点信息
 * Peter
 * 下午4:36:14
 */
public class TB_POINT implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3762202736740431521L;
	
	private String POINTID;//巡视点ID
	private String POINTNAME;//巡视点名称
	private String POINTTYPE;//巡视点类型
	private String STATUS;//绑卡状态
	private String EPCCODE;//RFID EPC码
	public TB_POINT() {
		super();
		POINTID = "";
		POINTNAME = "";
		POINTTYPE = "";
		STATUS = "";
		EPCCODE = "";
	}
	public String getPOINTID() {
		return POINTID;
	}
	public void setPOINTID(String pOINTID) {
		POINTID = pOINTID;
	}
	public String getPOINTNAME() {
		return POINTNAME;
	}
	public void setPOINTNAME(String pOINTNAME) {
		POINTNAME = pOINTNAME;
	}
	public String getPOINTTYPE() {
		return POINTTYPE;
	}
	public void setPOINTTYPE(String pOINTTYPE) {
		POINTTYPE = pOINTTYPE;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
	public String getEPCCODE() {
		return EPCCODE;
	}
	public void setEPCCODE(String ePCCODE) {
		EPCCODE = ePCCODE;
	}
	
}
