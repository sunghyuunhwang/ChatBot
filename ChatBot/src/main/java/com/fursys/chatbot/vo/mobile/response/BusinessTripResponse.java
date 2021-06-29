package com.fursys.chatbot.vo.mobile.response;

import java.util.ArrayList;

import com.fursys.chatbot.vo.BaseResponse;
import com.fursys.chatbot.vo.erp.ERPBusinessTrip;
import com.fursys.chatbot.vo.erp.ERPBusinessTripDetail;

import lombok.Getter;
import lombok.Setter;
public class BusinessTripResponse extends BaseResponse{
	@Getter @Setter private ERPBusinessTrip businesstrip;	
	@Getter @Setter private ArrayList<ERPBusinessTripDetail> list;

}
 