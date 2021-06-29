package com.fursys.chatbot.mapper;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.ibatis.annotations.Mapper;

import com.fursys.chatbot.vo.erp.ERPOrderCdtChangeAutoYn;
import com.fursys.chatbot.vo.erp.ERPOrderSearchDetailVo;
import com.fursys.chatbot.vo.erp.ERPOrderSearchVo;
import com.fursys.chatbot.vo.erp.ERPOrderTestVo;
import com.fursys.chatbot.vo.erp.ERPVendorTelNoVo;
import com.fursys.chatbot.vo.DataResult;

@Mapper
public interface ChatBotMapper {
	

	public ArrayList<ERPOrderTestVo> selectSelectCooperationList(HashMap<String,Object> params);
	
	public ArrayList<ERPOrderSearchVo> selectOrderSearchList(HashMap<String,Object> params);
	public ArrayList<ERPOrderSearchVo> selectAsSearchList(HashMap<String,Object> params);
	public ArrayList<ERPOrderSearchDetailVo> selectAsDetailSearchList(HashMap<String,Object> params);
	public ArrayList<ERPOrderSearchDetailVo> selectOrderDetailSearchList(HashMap<String,Object> params);
	public ArrayList<ERPOrderSearchDetailVo> selectOrderDetailSearchListIloom(HashMap<String,Object> params);
	
	public DataResult executePraFaChgreqdt(HashMap<String,Object> params);
	public DataResult selectComBrand(HashMap<String,Object> params);
	
	public ERPVendorTelNoVo selectVendorTelNo(HashMap<String,Object> params);
	public int insertApicallHist(HashMap<String,Object> params);
}
