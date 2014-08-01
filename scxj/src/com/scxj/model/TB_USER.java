/***********************************************************************
 * Module:  TB_INSPECTION.java
 * Author:  think
 * Purpose: Defines the Class TB_INSPECTION
 ***********************************************************************/
package com.scxj.model;
import java.io.Serializable;
import java.util.*;

public class TB_USER implements Serializable{
	private String USERID;//	用户ID
	private String USERNAME;//	登录名称
	private String SEX;//	性别
	private String AGE;//	年龄
	private String TEL;//	电话
	private String PASSWORD;//	密码
	private String ORGID;//	所属供电局ID
	private String ORGNAME;//	所属供电局名称
	private String GROUPNAME;//	机号
	private String CLASSNAME;//	班组
	private String REALNAME;//真实姓名
	
	public TB_USER() {
		super();
		USERID = "";
		USERNAME = "";
		SEX = "";
		AGE = "";
		TEL = "";
		PASSWORD = "";
		ORGID = "";
		ORGNAME = "";
		GROUPNAME = "";
		CLASSNAME = "";
		REALNAME  = "";
	}
	
	public TB_USER(String userId) {
		super();
		USERID = userId;
		}

	public String getUSERID() {
		return USERID;
	}

	public void setUSERID(String uSERID) {
		USERID = uSERID;
	}

	public String getUSERNAME() {
		return USERNAME;
	}

	public void setUSERNAME(String uSERNAME) {
		USERNAME = uSERNAME;
	}

	public String getSEX() {
		return SEX;
	}

	public void setSEX(String sEX) {
		SEX = sEX;
	}

	public String getAGE() {
		return AGE;
	}

	public void setAGE(String aGE) {
		AGE = aGE;
	}

	public String getTEL() {
		return TEL;
	}

	public void setTEL(String tEL) {
		TEL = tEL;
	}

	public String getPASSWORD() {
		return PASSWORD;
	}

	public void setPASSWORD(String pASSWORD) {
		PASSWORD = pASSWORD;
	}

	public String getORGID() {
		return ORGID;
	}

	public void setORGID(String oRGID) {
		ORGID = oRGID;
	}

	public String getORGNAME() {
		return ORGNAME;
	}

	public void setORGNAME(String oRGNAME) {
		ORGNAME = oRGNAME;
	}

	public String getGROUPNAME() {
		return GROUPNAME;
	}

	public void setGROUPNAME(String gROUPNAME) {
		GROUPNAME = gROUPNAME;
	}

	public String getCLASSNAME() {
		return CLASSNAME;
	}

	public void setCLASSNAME(String cLASSNAME) {
		CLASSNAME = cLASSNAME;
	}

	public String getREALNAME() {
		return REALNAME;
	}

	public void setREALNAME(String rEALNAME) {
		REALNAME = rEALNAME;
	}
	
	
	
	}