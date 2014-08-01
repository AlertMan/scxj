/***********************************************************************
 * Module:  TB_CONST.java
 * Author:  think
 * Purpose: Defines the Class TB_CONST
 ***********************************************************************/
package com.scxj.model;
import java.io.Serializable;
import java.util.*;

/** 常量表
 * 
 * @pdOid b09eecbe-8d2b-497a-b98a-3fa3b3fdd521 */
public class TB_CONST implements Serializable { 
	
	private String CONSTID;//	常量ID
	private String CONSTNAME;//	常量名
	private String CONSTTYPE;//	常量类型
	private String CONSTVALUE;//	常量值
	
	public TB_CONST() {
		super();
		CONSTID = "";
		CONSTNAME = "";
		CONSTTYPE = "";
		CONSTVALUE = "";
	}
	public String getCONSTID() {
		return CONSTID;
	}
	public void setCONSTID(String cONSTID) {
		CONSTID = cONSTID;
	}
	public String getCONSTNAME() {
		return CONSTNAME;
	}
	public void setCONSTNAME(String cONSTNAME) {
		CONSTNAME = cONSTNAME;
	}
	public String getCONSTTYPE() {
		return CONSTTYPE;
	}
	public void setCONSTTYPE(String cONSTTYPE) {
		CONSTTYPE = cONSTTYPE;
	}
	public String getCONSTVALUE() {
		return CONSTVALUE;
	}
	public void setCONSTVALUE(String cONSTVALUE) {
		CONSTVALUE = cONSTVALUE;
	}
	
	
}