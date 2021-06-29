package com.fursys.chatbot.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fursys.chatbot.mapper.ChatBotMapper;
import com.fursys.chatbot.role.Role;
import com.fursys.chatbot.service.ApiCommonService;
import com.fursys.chatbot.utils.CommonObjectUtils;
import com.fursys.chatbot.utils.StringUtil;
import com.fursys.chatbot.vo.BaseResponse;
import com.fursys.chatbot.vo.DataResult;
import com.google.gson.Gson;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Service
public class ApiCommonServiceImpl  implements ApiCommonService {
	@Autowired ChatBotMapper cChatBotMapper;	
	
	@Autowired private PlatformTransactionManager txManager;
	Gson gson = new Gson();
	
	@Override
	public BaseResponse erp_callApiHistSave(HashMap<String, Object> param) {
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
		BaseResponse response = new BaseResponse();
		HashMap<String, Object> params;
		int res = 0;
		try {
			
	        HttpServletRequest req = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
			String ip = req.getHeader("X-FORWARDED-FOR");
			if (ip == null)
				ip = req.getRemoteAddr();
			
			String call_function = (String) param.get("call_function");
			String function_name = (String) param.get("function_name");
			
			params = new HashMap<String, Object>();
			params.put("call_function", call_function);
			params.put("function_name", function_name);
			params.put("client_ip", ip);
			
			res = cChatBotMapper.insertApicallHist(params);
			
			if (res < 1) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("insertApicallHist 오류 [" + res + "]");
				return response;
			}
			
		} catch (Exception e) {
			txManager.rollback(status);
			System.out.println(e.toString());
			response.setResultCode("5001");
			response.setResultMessage(e.toString());
			return response;
		}
		
		txManager.commit(status);
		//response.setResultCode("200");		
		return response;

	}

}
