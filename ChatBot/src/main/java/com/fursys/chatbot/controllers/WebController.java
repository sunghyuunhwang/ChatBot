package com.fursys.chatbot.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fursys.chatbot.config.ServerInfo;
import com.fursys.chatbot.mapper.UserMapper;
import com.fursys.chatbot.utils.RestService;
import com.fursys.chatbot.utils.RestService.RestServiceCallBack;
import com.fursys.chatbot.vo.BaseResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Controller
@RequestMapping("/v1/areamaster")
public class WebController {
	@Autowired UserMapper userMapper;
	
	@GetMapping("/greeting")
	public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);		
		return "greeting";   // html name
	}

	@GetMapping("/test")
	public String test(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);		
		return "test";   // html name
	}	
	
	@GetMapping("/sign")
	public String sign(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);		
		return "sign";   // html name
	}	
	
	@GetMapping("/areamina")
	public String areamina(@AuthenticationPrincipal User user, @RequestParam(name="name", required=false, defaultValue="World") String name, Model model, HttpServletRequest request) {
		
		System.out.println(user.getUsername());
		System.out.println("========================2222222===============");
        model.addAttribute("name", name);	
        
        HashMap<String, Object> params = new HashMap<String, Object>();
    	params = new HashMap<String, Object>();
    	params.put("vnd_cd", user.getUsername());
    	Map<String, Object> etc = userMapper.getUserEtc(params);

    	model.addAttribute("com_scd", etc.get("COM_SCD").toString());
    	model.addAttribute("sti_cd", etc.get("STI_CD").toString());
    	model.addAttribute("k_sti_cd", etc.get("K_STI_CD").toString());
        
    	System.out.println(etc.get("COM_SCD").toString());
    	System.out.println(etc.get("STI_CD").toString());
    	System.out.println(etc.get("K_STI_CD").toString());
    	
		return "areamina";   // html name
	}	
	
	@GetMapping("/scheduling")
	public String scheduling(@AuthenticationPrincipal User user, @RequestParam(name="name", required=false, defaultValue="World") String name, Model model, HttpServletRequest request) {
		
		System.out.println(user.getUsername());
		System.out.println("========================2222222===============");
        model.addAttribute("name", name);	
        
        HashMap<String, Object> params = new HashMap<String, Object>();
    	params = new HashMap<String, Object>();
    	params.put("vnd_cd", user.getUsername());
    	Map<String, Object> etc = userMapper.getUserEtc(params);

    	model.addAttribute("com_scd", etc.get("COM_SCD").toString());
    	model.addAttribute("sti_cd", etc.get("STI_CD").toString());
    	model.addAttribute("k_sti_cd", etc.get("K_STI_CD").toString());
        
    	System.out.println(etc.get("COM_SCD").toString());
    	System.out.println(etc.get("STI_CD").toString());
    	System.out.println(etc.get("K_STI_CD").toString());
    	
		return "scheduling";   // html name
	}	
	
}
