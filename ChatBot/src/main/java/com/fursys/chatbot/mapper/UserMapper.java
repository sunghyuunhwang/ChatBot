package com.fursys.chatbot.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.fursys.chatbot.vo.FursysUser;

@Mapper
public interface UserMapper {
	   
	public int updateCalPassWord(HashMap<String,Object> params);
	public int updateLoginPassWord(HashMap<String,Object> params);
	public FursysUser login(HashMap<String,Object> params);  
	public FursysUser getUserInfo(HashMap<String,Object> params);  
	public Map<String, Object> getUserVND_CD(HashMap<String,Object> params); 
	public Map<String, Object> getUserEtc(HashMap<String,Object> params); 
	public Map<String, Object> getCom_Stsec(HashMap<String,Object> params);
	
}
