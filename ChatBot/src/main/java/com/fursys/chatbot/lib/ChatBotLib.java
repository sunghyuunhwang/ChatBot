package com.fursys.chatbot.lib;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.fursys.chatbot.mapper.ChatBotMapper;
import com.fursys.chatbot.vo.BaseResponse;
import com.fursys.chatbot.vo.DataResult;

public class ChatBotLib {
    
    public static String makeTmsMsg(BaseResponse response) {
    	return "[코드:" + response.getResultCode() + "],메세지[" + response.getResultMessage() + "]"; 
    }

}
