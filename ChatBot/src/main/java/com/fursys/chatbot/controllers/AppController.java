package com.fursys.chatbot.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fursys.chatbot.service.ApiCommonService;
import com.fursys.chatbot.service.impl.ApiCommonServiceImpl;

import com.fursys.chatbot.utils.RestService;
import com.fursys.chatbot.utils.RestService.RestServiceCallBack;
import com.fursys.chatbot.vo.erp.ERPAvailableChgAddrStat;
import com.fursys.chatbot.vo.erp.ERPBdongCdVo;
import com.fursys.chatbot.vo.erp.ERPOrderCdtChangeAutoYn;
import com.fursys.chatbot.vo.erp.ERPOrderChangeAvailableDt;
import com.fursys.chatbot.vo.erp.ERPOrderFinalAvailableDate;
import com.fursys.chatbot.vo.erp.ERPOrderSearchDetailVo;
import com.fursys.chatbot.vo.erp.ERPOrderSearchVo;
import com.fursys.chatbot.vo.erp.ERPOrderTestVo;
import com.fursys.chatbot.vo.erp.ERPOrmAvailableYn;
import com.fursys.chatbot.vo.erp.ERPSameOrderYn;
import com.fursys.chatbot.vo.erp.ERPVendorTelNoVo;
import com.fursys.chatbot.vo.mobile.response.UserInfoResponse;
import com.fursys.chatbot.vo.DataResult;
import com.fursys.chatbot.vo.BaseResponse;

import com.google.gson.Gson;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import com.fursys.chatbot.mapper.ChatBotChangeMapper;
import com.fursys.chatbot.mapper.ChatBotMapper;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RestController
@RequestMapping("/v1/api")
public class AppController {
	
	@Autowired ChatBotMapper cChatBotMapper;	
	@Autowired ChatBotChangeMapper cChatBotChangeMapper;
	
	@Autowired ApiCommonService apiCommonService;	
	@Autowired private PlatformTransactionManager txManager;
	private ChatBotChangeMapper chatBotChangeMapper;
	
	Gson gson = new Gson();
	
	@ApiOperation(value = "exam", notes = "exam.")
    @ApiImplicitParams({ 
    	@ApiImplicitParam (name = "name", value="value", required = true),
    	@ApiImplicitParam (name = "name2", value="value2", required = true),
    	@ApiImplicitParam (name = "name3", value="value3", required = true)
    	})
	@ApiResponses({
        @ApiResponse(code = 200, message = "OK !!"),
        @ApiResponse(code = 500, message = "Internal Server Error !!"),
        @ApiResponse(code = 404, message = "Not Found !!")
	})
	@GetMapping("/demoapi") 
	public String demoapi() { 
		
		return "";
	}
	
	@ApiOperation(value = "headerTest", notes = "???????????????")
	@ApiResponses({
        @ApiResponse(code = 200, message = "OK !!"), @ApiResponse(code = 5001, message = "headerTest Fail !!")
	})
	@GetMapping("/headerTest") 
	@RequestMapping(value="/headerTest",method=RequestMethod.GET)
	public String userInfo(
			@RequestHeader(value="CertKey", required=false) String CertKey ,
			
			@RequestParam(name="id", required=false) String id) { 
		
		//UserInfoResponse userInfoResponse = new UserInfoResponse();
		
		System.out.println("id: " + id);
        System.out.println("CertKey: " + CertKey);
        
        String res = "";
        if ("58BEA9A133900619879E8FFF1ECB0F336A5672CF6B68EB56B0D229FF4C6A7D52".equals(CertKey)) {
        	res = "Your CertiKey is Valid.";
        } else {
        	res = "Your CertiKey is inValid!!!";
        }        
		
        BaseResponse response = new BaseResponse();
        HashMap<String,Object> params = new HashMap<String, Object>();
        params.put("call_function", "userInfo");
        params.put("function_name", "header test");
        
        response = apiCommonService.erp_callApiHistSave(params);
        
		return res;
	}	
	
	@ApiOperation(value = "erp_selectOrderTest", notes = "api?????????????????????")
	@RequestMapping(value="/erp_selectOrderTest",method=RequestMethod.GET)
	public String erp_selectOrderTest(
			@RequestHeader(value="CertKey", required=false) String CertKey ,
			@RequestParam(name="orm_nm", required=true) String orm_nm
		) { 
		
		HashMap<String,Object> params = new HashMap<String, Object>();
        params.put("orm_nm",orm_nm);

        String res = "";
        if ("58BEA9A133900619879E8FFF1ECB0F336A5672CF6B68EB56B0D229FF4C6A7D52".equals(CertKey)) {
        	res = "Your CertiKey is Valid.";
        } else {
        	res = "Your CertiKey is inValid!!!";
        }
        
        ArrayList<ERPOrderTestVo> arList = cChatBotMapper.selectSelectCooperationList(params);

        BaseResponse response = new BaseResponse();
        params.put("call_function", "erp_selectOrderTest");
        params.put("function_name", "?????? ???????????? ?????? ?????????");
        response = apiCommonService.erp_callApiHistSave(params);
		return gson.toJson(arList);        
        
	}	 
	
	@ApiOperation(value = "erp_searchOrderInfo", notes = "ERP_API_??????????????????")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK !!"), @ApiResponse(code = 5001, message = "?????? ?????? !!") })
	@RequestMapping(value="/erp_searchOrderInfo",method=RequestMethod.POST)
	public String erp_searchOrderInfo(
			@RequestHeader(value="CertKey", required=false) String CertKey ,
			@RequestParam(name="order_type", required=true) String order_type,
			@RequestParam(name="orm_hdphone", required=true) String orm_hdphone,
			@RequestParam(name="com_brand", required=true) String com_brand,
			@RequestParam(name="fdt", required=true) String fdt,
			@RequestParam(name="tdt", required=true) String tdt,
			@RequestParam(name="ctm_ci", required=true) String ctm_ci

		) { 

        String res = "";
        if ("58BEA9A133900619879E8FFF1ECB0F336A5672CF6B68EB56B0D229FF4C6A7D52".equals(CertKey)) {
        	res = "Your CertiKey is Valid.";
        } else {
        	return gson.toJson("Your CertiKey is inValid."); 
        }
        
		String vnd_cd = "";
		String com_agsec = "";
		if("T60F01".equals(com_brand) ) {
			vnd_cd = "T01F";
			com_agsec = "C02F";
		} else if ("T60I01".equals(com_brand) ) {
			vnd_cd = "T01I";
			com_agsec = "C02I";
		} else if ("T60I02".equals(com_brand) ) {
			vnd_cd = "T01I";
			com_agsec = "C02I";
		} else if ("T60I03".equals(com_brand) ){
			vnd_cd = "T01I";
			com_agsec = "C02I";
		} else if ("T60P01".equals(com_brand) ) {
			vnd_cd = "T01P";
			com_agsec = "C02P";
		} else if ("T60P02".equals(com_brand) ){
			vnd_cd = "T01P";
			com_agsec = "C02P";
		} else {
			
		}
		
		
		HashMap<String,Object> params = new HashMap<String, Object>();
		params.put("orm_hdphone",orm_hdphone);
		params.put("com_brand",com_brand);	
		params.put("vnd_cd",vnd_cd);		
		params.put("com_agsec",com_agsec);	
		params.put("ctm_ci",ctm_ci);	
		params.put("fdt",fdt);
		params.put("tdt",tdt);		
		    
	    BaseResponse response = new BaseResponse();    
	    
	    if ( "ORDER".equals(order_type)) {
	        	
	        	
	    	ArrayList<ERPOrderSearchVo> arList = cChatBotMapper.selectOrderSearchList(params);	        
	    	
	        
	        params.put("call_function", "erp_searchOrderInfo");
	        params.put("function_name", "?????? ???????????? ????????? ??????");
	        response = apiCommonService.erp_callApiHistSave(params);	    	
	    	
	        return gson.toJson(arList); 
  	
	    } else {
	        		
	        ArrayList<ERPOrderSearchVo> arList = cChatBotMapper.selectAsSearchList(params);
	        
	        params.put("call_function", "erp_searchOrderInfo");
	        params.put("function_name", "?????? ???????????? ????????? ??????");
	        response = apiCommonService.erp_callApiHistSave(params);
	        
	        return gson.toJson(arList); 
	    }
	    
	    
	}
	
	@ApiOperation(value = "erp_searchOrderDetailInfo", notes = "ERP_API_????????????????????????")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK !!"), @ApiResponse(code = 5001, message = "?????? ?????? !!") })
	@RequestMapping(value="/erp_searchOrderDetailInfo",method=RequestMethod.POST)
	public String erp_searchOrderDetailInfo(
			@RequestHeader(value="CertKey", required=false) String CertKey ,
			@RequestParam(name="order_type", required=true) String order_type,
			@RequestParam(name="key_no", required=true) String key_no
		) { 
		
        String res = "";
        if ("58BEA9A133900619879E8FFF1ECB0F336A5672CF6B68EB56B0D229FF4C6A7D52".equals(CertKey)) {
        	res = "Your CertiKey is Valid.";
        } else {
        	return gson.toJson("Your CertiKey is inValid."); 
        } 
        
		ArrayList<ERPOrderSearchDetailVo> arList = null;
		
		HashMap<String,Object> params = new HashMap<String, Object>();
		params.put("key_no",key_no);
		
		DataResult dataResult = new DataResult();
		
	    BaseResponse response = new BaseResponse();
	    
	    if ( "ORDER".equals(order_type)) {

			dataResult = cChatBotMapper.selectComBrand(params);
			String com_brand = "";
			
			com_brand = dataResult.getData1() ;

	    	if ("T60I01".equals(com_brand)) {
	    		
	    		arList = cChatBotMapper.selectOrderDetailSearchListIloom(params);
	    		 
	    	} else if ("T60I02".equals(com_brand)) {
	    		
	    		arList = cChatBotMapper.selectOrderDetailSearchList(params);	
	    		 
	    	} else if ("T60I03".equals(com_brand)) {
	    		
	    		arList = cChatBotMapper.selectOrderDetailSearchList(params);	
	    		 
	    	} else if ("T60P01".equals(com_brand)) {

	    		arList = cChatBotMapper.selectOrderDetailSearchList(params);	
	    		
	    	} else if ("T60P02".equals(com_brand)) {

	    		arList = cChatBotMapper.selectOrderDetailSearchList(params);	
	    		
	    	} else if ("T60F01".equals(com_brand)) {

	    		arList = cChatBotMapper.selectOrderDetailSearchList(params);	
	    	}
	    	
	       
	        params.put("call_function", "erp_searchOrderDetailInfo");
	        params.put("function_name", "?????? ?????????????????? ??????");
	        response = apiCommonService.erp_callApiHistSave(params);	 
	        
	    	return gson.toJson(arList); 
	    	        	
	       
	    } else {
	        		
	        arList = cChatBotMapper.selectAsDetailSearchList(params);
	        
	        params.put("call_function", "erp_searchOrderDetailInfo");
	        params.put("function_name", "?????? ?????????????????? ??????");
	        response = apiCommonService.erp_callApiHistSave(params);		
	        
	        return gson.toJson(arList); 
	    }
	}

	@ApiOperation(value = "erp_asArrivalDateChangeAutoCheck", notes = "ERP_API_????????????Auto??????????????????")
	@ApiResponses({ @ApiResponse(code = 200, message = "????????????Auto?????????????????? ?????? !!"), @ApiResponse(code = 5001, message = "????????????Auto?????????????????? ?????? !!") })
	@GetMapping("/erp_asArrivalDateChangeAutoCheck")
	@RequestMapping(value = "/erp_asArrivalDateChangeAutoCheck", method = RequestMethod.GET)
	public String erp_asArrivalDateChangeAutoCheck(
			@RequestHeader(value="CertKey", required=false) String CertKey ,
			@RequestParam(name = "com_cmp", required = false) String com_cmp,
			@RequestParam(name = "key_no", required = false) String key_no
			) {
		       
		BaseResponse response = new BaseResponse();
		DataResult dataResult = new DataResult();
		
		ERPOrderCdtChangeAutoYn item = null;
				
		try {

			
	        String res = "";
	        if ("58BEA9A133900619879E8FFF1ECB0F336A5672CF6B68EB56B0D229FF4C6A7D52".equals(CertKey)) {
	        	res = "Your CertiKey is Valid.";
	        } else {
	        	return gson.toJson("Your CertiKey is inValid."); 
	        } 
	        
			HashMap<String,Object> params = new HashMap<String, Object>();
			params.put("key_no",key_no);
			params.put("com_cmp", com_cmp);
			
			dataResult = cChatBotChangeMapper.selectToChgmasMaxSeqNo(params);
			
			String req_seq = "";
			
			req_seq = dataResult.getData1() ;				
			
			params.put("key_no",key_no);
			params.put("com_cmp", com_cmp);
			params.put("req_seq", req_seq);
			
			item = cChatBotChangeMapper.executeProRenAutoBlock(params);
			 
			String block_yn = "";
			
			block_yn = dataResult.getData1() ;
			
	        params.put("call_function", "erp_asArrivalDateChangeAutoCheck");
	        params.put("function_name", "?????? ERP_API_????????????Auto??????????????????");
	        response = apiCommonService.erp_callApiHistSave(params);
	        
		} catch (Exception e) {
			
			response.setResultCode("5001");
			response.setResultMessage(e.toString());
			return gson.toJson(item);
		}
		
		response.setResultCode("200");
		return gson.toJson(item);
	}	
	
	@ApiOperation(value = "erp_OrderArrivalDateChange", notes = "?????? ???????????? ?????? Auto ??????")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK !!"), @ApiResponse(code = 5001, message = "?????? ???????????? ?????? ?????? Auto ?????? !!") })
	@GetMapping("/erp_OrderArrivalDateChange")
	@RequestMapping(value = "/erp_OrderArrivalDateChange", method = RequestMethod.GET)
	public String erp_OrderArrivalDateChange(
			@RequestHeader(value="CertKey", required=false) String CertKey ,
			@RequestParam(name = "key_no", required = false) String key_no,
			@RequestParam(name = "com_cmp", required = false) String com_cmp,
			@RequestParam(name = "com_agsec", required = false) String com_agsec,
			@RequestParam(name = "req_dt", required = false) String req_dt
			) {
		       
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
		int res = 0;
		BaseResponse response = new BaseResponse();
		DataResult dataResult = new DataResult();
		
		try {
			String res2 = "";
	        if ("58BEA9A133900619879E8FFF1ECB0F336A5672CF6B68EB56B0D229FF4C6A7D52".equals(CertKey)) {
	        	res2 = "Your CertiKey is Valid.";
	        } else {
	        	return gson.toJson("Your CertiKey is inValid."); 
	        } 						
// 1. ?????? ??????????????? ?????? ??????
			
			//	1.1 ?????? ???????????? ??????
			
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("key_no", key_no);
					
			dataResult = cChatBotChangeMapper.executeProGetNo(params);
			
			if (dataResult == null) { 
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("?????? ???????????? ?????? ??????");
				return gson.toJson(response);
			}
			
			String new_ormno = "";			
			new_ormno = dataResult.getData1() ;

			//  1.2 ??????????????????
			
			params.put("com_cmp", com_cmp);
			dataResult = cChatBotChangeMapper.selectToChgmasChgdt(params);
			
			if (dataResult == null) { 
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("?????????????????? SELECT ??????");
				return gson.toJson(response);
			}
			
			String chg_orm_cdt = "";
			chg_orm_cdt = dataResult.getData1() ;
			
			//	1.3 ?????? ??????????????? ??????
			params.put("new_orm_no", new_ormno);
			params.put("orm_no", key_no);
			params.put("chg_orm_cdt", chg_orm_cdt);
			params.put("com_cmp", com_cmp);
			
        	res = cChatBotChangeMapper.insertNewOrdMaster(params);
        	
			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("??????????????? ?????? ?????? [" + res + "]");
				return gson.toJson(response);
			}			
			
//	2. ?????? ??????????????? ??????
			
        	res = cChatBotChangeMapper.insertNewOrdDetail(params);
        	
			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("??????????????? ?????? ?????? [" + res + "]");
				return gson.toJson(response);
			}			
			
//	3. ??????????????? ??????????????? ??????
			
        	res = cChatBotChangeMapper.deleteRecdRenD(params);
        	
			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("??????????????? ??????????????? ?????? ?????? [" + res + "]");
				return gson.toJson(response);
			}			
			
//	4. ??????????????? ??????????????? ??????			
			
        	res = cChatBotChangeMapper.insertRecdRenD(params);
        	
			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("??????????????? ??????????????? ?????? ?????? [" + res + "]");
				return gson.toJson(response);
			}			
			
//  5. ?????????????????? ??????????????? ?????? 			
			
			// 1. ?????????????????? ?????? ????????????
			
			params.put("key_no",key_no);
			params.put("com_cmp", com_cmp);
			
			dataResult = cChatBotChangeMapper.selectToChgmasMaxSeqNo(params);
			
			String req_seq = "";
			req_seq = dataResult.getData1() ;
			
			
			params.put("req_seq", req_seq);
			
        	res = cChatBotChangeMapper.updateToOrdChgmasStat(params);
        	
			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("?????????????????? ??????????????? ?????? 	 ?????? [" + res + "]");
				return gson.toJson(response);
			}			
			
//  6. ?????? ??????????????? ?????? 			
			
        	res = cChatBotChangeMapper.updateToOrdmasStat(params);
        	
			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("?????? ??????????????? ?????? 	 ?????? [" + res + "]");
				return gson.toJson(response);
			}			
			
			
////  7. ?????????????????? ??????????????? ?????? ??????		
//			
//        	res = cChatBotChangeMapper.insertToChgmasAdded(params);
//        	
//			if (res < 0) {
//				txManager.rollback(status);
//				response.setResultCode("5001");
//				response.setResultMessage("?????????????????? ??????????????? ?????? ?????? ?????? [" + res + "]");
//				return gson.toJson(response);
//			}			

//?????? ???  / ????????? ??????			
			
			dataResult = cChatBotChangeMapper.selectOrmCryn(params);
						
			String orm_cryn = "";
			String before_orm_cdt = "";
			
			orm_cryn = dataResult.getData1() ;
			before_orm_cdt = dataResult.getData2();
			
			if ("Y".equals(orm_cryn)) {
				
				//?????????????????? ????????????
				String sigongreserve_yn = "";
				
				dataResult = cChatBotChangeMapper.selectSigongReserveYn(params);
				
				sigongreserve_yn = dataResult.getData1() ;
				
				if ("Y".equals(sigongreserve_yn)) {
					
					//??????????????? key ??? ???????????? 
					
					String rem_dt = "";
					String rem_seq = "";
					String plm_no = "";
					params.put("rem_dt", before_orm_cdt);
					
					dataResult = cChatBotChangeMapper.selectRemdtRemseq(params);
					
					rem_dt = dataResult.getData1() ;
					rem_seq = dataResult.getData2() ;
					plm_no = dataResult.getData3();
					
					params.put("rem_dt", rem_dt);
					params.put("rem_seq", rem_seq);
					
					
					//?????? ???????????? key ???????????? 
					
					int max_seq_no = 0;
					
					
					params.put("com_agsec", com_agsec);
					params.put("req_dt", req_dt);
					dataResult = cChatBotChangeMapper.selectTcMaxSeqNo(params);
					
					max_seq_no = dataResult.getValue1() ;
					
					
					params.put("max_seq_no", max_seq_no);
					params.put("com_agsec", com_agsec);
					
					
		        	res = cChatBotChangeMapper.insertTcSeqnoinf(params);
		        	
					if (res < 0) {
						txManager.rollback(status);
						response.setResultCode("5001");
						response.setResultMessage("insertTcSeqnoinf ?????? ?????? [" + res + "]");
						return gson.toJson(response);
					}			
					
					String new_rem_seq = "";
					new_rem_seq = StringUtils.right(com_agsec, 1) + StringUtils.right("C18C", 1) + StringUtils.leftPad(String.valueOf(max_seq_no), 4, "0");
					
					//tc_resdtl ????????????
					
					
					params.put("new_rem_seq", new_rem_seq);
					params.put("req_dt", req_dt);
					
		        	res = cChatBotChangeMapper.updateTcresdtl(params);
		        	
					if (res < 0) {
						txManager.rollback(status);
						response.setResultCode("5001");
						response.setResultMessage("updateTcresdetl ?????? 	 ?????? [" + res + "]");
						return gson.toJson(response);
					}			

					//tc_resmst ????????????
					params.put("new_ormno", new_ormno);
		        	res = cChatBotChangeMapper.updateTcresmst(params);
		        	
					if (res < 0) {
						txManager.rollback(status);
						response.setResultCode("5001");
						response.setResultMessage("updateTcresmst ?????? 	 ?????? [" + res + "]");
						return gson.toJson(response);
					}			

					//tc_planmst ????????????	
					
					if("XXXX".equals(plm_no)) {
						
						params.put("plm_no", plm_no);
						
			        	res = cChatBotChangeMapper.updateTcplanmst(params);
			        	
						if (res < 0) {
							txManager.rollback(status);
							response.setResultCode("5001");
							response.setResultMessage("updateTcplanmst ?????? ?????? [" + res + "]");
							return gson.toJson(response);
						}			
	
					}
					
					//to_ordmas ????????????
					
					params.put("new_ormno", new_ormno);
					params.put("chg_orm_cdt", chg_orm_cdt);
					
		        	res = cChatBotChangeMapper.updateToOrdmasSigongStat(params);
		        	
					if (res < 0) {
						txManager.rollback(status);
						response.setResultCode("5001");
						response.setResultMessage("updateToOrdmasSigongStat ?????? ?????? [" + res + "]");
						return gson.toJson(response);
					}			
	
				}
				
			}
			
			//??????????????? ?????? ?????? ????????? insert ??????
			
			dataResult = cChatBotChangeMapper.selectOnlineynCheck(params);
			
			String online_yn = "";
			String orm_nm = "";			
			String message= "";
			
			online_yn = dataResult.getData1();
			orm_nm = dataResult.getData2();
			
			message = "????????? ?????? ?????????????????? ???????????????. (???????????? : " + key_no + ",  ???????????? : " + orm_nm + " )";
					
			if(online_yn.equals("N")) {
				
				params.put("message", message);
				
				res = cChatBotChangeMapper.insertAgtChangeAlarm(params);
				
				if (res < 0) {
					txManager.rollback(status);
					response.setResultCode("5001");
					response.setResultMessage("insertAgtChangeAlarm ???????????????????????? insert ?????? [" + res + "]");
					return gson.toJson(response);
				}					
			}

	        params.put("call_function", "erp_OrderArrivalDateChange");
	        params.put("function_name", "?????? ?????? ???????????? ?????? Auto ??????");
	        response = apiCommonService.erp_callApiHistSave(params);			
			
	    	JSONObject obj = new JSONObject();
	    	JSONArray jArray = new JSONArray(); //????????? ????????????
	    	JSONArray jArray2 = new JSONArray(); //????????? ????????????
	    	
	    	JSONObject api_token = new JSONObject();
	    	JSONObject sendList = new JSONObject();	    	 
	    	JSONObject sObject = new JSONObject();//?????? ?????? ????????? json
	    	JSONObject sObject2 = new JSONObject();//?????? ?????? ????????? json
	    	
	    	String templateCode = "";
	    	String senderkey = "";
	    	String title = "";
	    	String subject = "";
	    	String from_no = "";
	    	String message_type = "";
	    	String sales_managerhp = "";
	    	
	        //?????????????????? ?????? ?????? ????????????	    	
	    	params.put("key_no", key_no);
	    	dataResult = cChatBotChangeMapper.selectChginformData(params);
	        
	        String com_brd = "";
	        String com_tono = "";
	        String org_orm_nm = "";
	        String send_yn = "";
	        String biztalkmessage = "";
	        
	        com_brd = dataResult.getData1();
	        org_orm_nm = dataResult.getData2();
	        com_tono = dataResult.getData3();
	        send_yn = dataResult.getData4();
	        sales_managerhp = dataResult.getData5();
	        
			title = "(CHATBOT)????????????AUTO????????????";
			subject = "(CHATBOT)????????????AUTO????????????";	      
    		
			String chg_content = "";
			chg_content = "????????????("+req_dt+")";
//	    	biztalkmessage = "(????????????) ??????????????? "+orm_nm+" ?????????, ???????????? ???????????? ?????? " + "" +pay_amt3+ "?????? ?????????????????????.\r\n\r\n" +
//					 "1) ???????????? : "+""+pay_no+"\r\n"+
//					 "2) ???????????? : "+""+rem_dt+"";s	
			
			String pattern = "yyyy-MM-dd";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

			String date = simpleDateFormat.format(new Date());
			System.out.println(date);
		        
	        if(send_yn.equals("Y")) {
	        
	        	if(com_brd.equals("T60I01")) {
        		
	        		biztalkmessage = "???????????????.\r\n" +
	        					"??? ????????? ???????????? ??????????????? ?????? ????????? ?????? ?????? ????????? ???????????? ?????? ????????????.\r\n\r\n" +
	        					"???????????? ??????\r\n"+
	        					"???????????? : "+key_no+"\r\n"+
	        					"???????????? : "+org_orm_nm+"\r\n"+
	        					"???????????? : "+chg_content+"\r\n"+
	        					"???????????? : "+date+"\r\n\r\n"+
	        					"?????? ?????? ?????? ??????????????????.\r\n"+
	        					"???????????????.\r\n"
	        					;
	        					
	        		senderkey = "dbf8669a88dd7926fd653ff3ff9b23d331fbbb4c";
	        		from_no = "1577-5670";
	        		templateCode = "iloomchatbotorderchangeinform0";
	        		
	        	}else if(com_brd.equals("T60I02")) {
	        		
	        		biztalkmessage = "???????????????.\r\n" +
        					"??? ????????? ???????????? ?????????????????? ?????? ????????? ?????? ?????? ????????? ???????????? ?????? ????????????.\r\n\r\n" +
        					"???????????? ??????\r\n"+
        					"???????????? : "+key_no+"\r\n"+
        					"???????????? : "+org_orm_nm+"\r\n"+
        					"???????????? : "+chg_content+"\r\n"+
        					"???????????? : "+date+"\r\n\r\n"+
        					"?????? ?????? ?????? ??????????????????.\r\n"+
        					"???????????????.\r\n"
        					;
		
	        		senderkey = "9917d09567d2ebf1acc89662d7f9ff10db1488d7";
	        		from_no = "1588-1662";
	        		templateCode = "deskerchatbotorderchginform0";
	        		
	        	}else if(com_brd.equals("T60I03")) {
	        		
	        		biztalkmessage = "???????????????.\r\n" +
        					"??? ????????? ???????????? ?????????????????? ?????? ????????? ?????? ?????? ????????? ???????????? ?????? ????????????.\r\n\r\n" +
        					"???????????? ??????\r\n"+
        					"???????????? : "+key_no+"\r\n"+
        					"???????????? : "+org_orm_nm+"\r\n"+
        					"???????????? : "+chg_content+"\r\n"+
        					"???????????? : "+date+"\r\n\r\n"+
        					"?????? ?????? ?????? ??????????????????.\r\n"+
        					"???????????????.\r\n"
        					;
	        		
	        		senderkey = "3ed320702f733d0b5a31e99a3ba931d9f2f9f960";
	        		from_no = "1899-8588";
	        		templateCode = "slouchatbotorderchangeinform0";	        		

	        	}else if(com_brd.equals("T60P01")) {
	        		
	        		biztalkmessage = "???????????????.\r\n" +
        					"??? ????????? ???????????? ?????????????????? ?????? ????????? ?????? ?????? ????????? ???????????? ?????? ????????????.\r\n\r\n" +
        					"???????????? ??????\r\n"+
        					"???????????? : "+key_no+"\r\n"+
        					"???????????? : "+org_orm_nm+"\r\n"+
        					"???????????? : "+chg_content+"\r\n"+
        					"???????????? : "+date+"\r\n\r\n"+
        					"?????? ?????? ?????? ??????????????????.\r\n"+
        					"???????????????.\r\n"
        					;
	        		
	        		senderkey = "6b94c758a1f689223024765ae6e2b0aede351955";
	        		from_no = "1577-5674";
	        		templateCode = "sidizchatbotorderchangeinform0";	  
	        		
	        	}else {
	        		
	        	}
	       	        	
		    	sendList.put("authKey", "D62D413F25CD43B3BD06636F2B3F570ABFB5008BD727901E341F041448D22C3A6593D58D45C68E60171F7FB2B2C345459361A08D20298BAE6A3A1B74196A95C3");
				
		       	 for (int i = 0; i < 1; i++)//??????
		       	 {
			        	 
			        	 
			        	 sObject.put("sendDiv", "BIZTALK" );
			        	 sObject.put("title", title);
			        	 sObject.put("subject", subject );		  
			        	 sObject.put("message","");
			        	 sObject.put("fromNm", org_orm_nm );
			        	 sObject.put("toNm", org_orm_nm );
			        	 sObject.put("fromNo", from_no ); 
			        	 sObject.put("toNo", com_tono);
//			        	 sObject.put("toNo", "010-3652-9837");
			        	 sObject.put("companyCd", "T01B" );		        	 
			        	 sObject.put("fstUsr", "CHATBOT" );
			        	 sObject.put("systemNm", "CHATBOT" );
			        	 sObject.put("sendType", "SMTP" );
			        	 sObject.put("reserveDiv","I");
			        	 sObject.put("reserveDt", "" );
			        	 sObject.put("keyNo", key_no);
			        	 sObject.put("msgType", "TI4N" );		        	 
			        	 sObject.put("senderKey", senderkey);
			        	 sObject.put("templateCode", templateCode );
			        	 sObject.put("bizTalkMessage", biztalkmessage );
			        	 sObject.put("comBrd", com_brd );
			        	 
			        	 jArray.add(sObject);
		       	 }        	 
	        		        			
	        	sendList.put("list" ,jArray);  

	        	
	        	BaseResponse kakao_res = RestCall("https://msg-api.fursys.com/v1/api/message/SendMsg",sendList);	
	        	if (!"200".equals(kakao_res.getResultCode())) {
					response.setResultCode("5001");
					response.setResultMessage("????????????????????? ??????  [" + kakao_res.getResultMessage() + "]");
					return gson.toJson(response);
				}
				
	 	        
	        	if(com_brd.equals("T60I01")) {
					
			       	 for (int i = 0; i < 1; i++)//??????
			       	 {
				        	 
				        	 
				        	 sObject2.put("sendDiv", "BIZTALK" );
				        	 sObject2.put("title", title);
				        	 sObject2.put("subject", subject );		  
				        	 sObject2.put("message","");
				        	 sObject2.put("fromNm", org_orm_nm );
				        	 sObject2.put("toNm", org_orm_nm );
				        	 sObject2.put("fromNo", from_no ); 
				        	 sObject2.put("toNo", sales_managerhp);
//				        	 sObject.put("toNo", "010-3652-9837");
				        	 sObject2.put("companyCd", "T01B" );		        	 
				        	 sObject2.put("fstUsr", "CHATBOT" );
				        	 sObject2.put("systemNm", "CHATBOT" );
				        	 sObject2.put("sendType", "SMTP" );
				        	 sObject2.put("reserveDiv","I");
				        	 sObject2.put("reserveDt", "" );
				        	 sObject2.put("keyNo", key_no);
				        	 sObject2.put("msgType", "TI4N" );		        	 
				        	 sObject2.put("senderKey", senderkey);
				        	 sObject2.put("templateCode", templateCode );
				        	 sObject2.put("bizTalkMessage", biztalkmessage );
				        	 sObject2.put("comBrd", com_brd );
				        	 
				        	 jArray2.add(sObject2);
			       	 }        	 
		        		        			
		        	sendList.put("list" ,jArray2);  
		        	 
		        	BaseResponse kakao_res2 = RestCall("https://msg-api.fursys.com/v1/api/message/SendMsg",sendList);	
		        	
		        	if (!"200".equals(kakao_res2.getResultCode())) {
						response.setResultCode("5001");
						response.setResultMessage("????????????????????? ??????  [" + kakao_res2.getResultMessage() + "]");
						return gson.toJson(response);
					}
	        		
	        	}

	        }
	        
		} catch (Exception e) {
			txManager.rollback(status);
			System.out.println(e.toString());			
			response.setResultCode("5001");
			response.setResultMessage(e.toString());
			return gson.toJson(response);
		}
		
		txManager.commit(status);
		response.setResultCode("200");
		System.out.println(response.toString());	
		return gson.toJson(response);
	}
	
	@ApiOperation(value = "erp_asArrivalDateChange", notes = "AS ???????????? ??????")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK !!"), @ApiResponse(code = 5001, message = "AS ???????????? ?????? ?????? !!") })
	@GetMapping("/erp_asArrivalDateChange")
	@RequestMapping(value = "/erp_asArrivalDateChange", method = RequestMethod.GET)
	public String erp_asArrivalDateChange(
			@RequestHeader(value="CertKey", required=false) String CertKey ,
			@RequestParam(name = "key_no", required = false) String key_no,
			@RequestParam(name = "rpt_no", required = false) String rpt_no,
			@RequestParam(name = "rpt_seq", required = false) String rpt_seq,
			@RequestParam(name = "com_agsec", required = false) String com_agsec,
			@RequestParam(name = "req_dt", required = false) String req_dt
			) {
		       
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
		int res = 0;
		BaseResponse response = new BaseResponse();
		DataResult dataResult = new DataResult();
		
		try {
			
			String res2 = "";
	        if ("58BEA9A133900619879E8FFF1ECB0F336A5672CF6B68EB56B0D229FF4C6A7D52".equals(CertKey)) {
	        	res2 = "Your CertiKey is Valid.";
	        } else {
	        	return gson.toJson("Your CertiKey is inValid."); 
	        } 				
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("key_no", key_no);
			params.put("com_agsec", com_agsec);
			params.put("req_dt", req_dt);
			
			//?????????????????? ??????
			
			dataResult = cChatBotChangeMapper.selectAsDateConfirmYn(params);
			String com_rmfg = "";
			
			com_rmfg = dataResult.getData1() ;
						
			if ("C142".equals(com_rmfg)) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("AS?????? ????????? ?????? ????????? ???????????????.");
				return gson.toJson(response);
			}			
			
			//?????????????????? ??????
			
			dataResult = cChatBotChangeMapper.selectAsChulgoYn(params);
			
			String com_sprog = "";
			com_sprog = dataResult.getData1() ;
			
			if ("A17008".equals(com_sprog)) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("?????? ?????? ????????? ?????? ????????? ???????????????.");
				return gson.toJson(response);
			}			
			
			//??????????????????
			
			params.put("rpt_no", rpt_no);
			params.put("rpt_seq", rpt_seq);
			
			dataResult = cChatBotChangeMapper.executePraFaChgreqdt(params);
			
			if (dataResult == null) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("AS?????? ?????? ??????");
				return gson.toJson(response);
			}

			//AUTO???????????? ?????? ????????? insert
			params.put("chgreq_hp", "");
			params.put("chgreq_dt", req_dt);
			params.put("chgreq_addr", "");
			params.put("reqorm_no", key_no);
			params.put("chgreq_std", "A96005");
			
			res = cChatBotChangeMapper.insertAsAutoChgReqInformation(params);
			
			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("insertAsAutoChgReqInformation ?????? [" + res + "]");
				return gson.toJson(response);
			}			
						
	        params.put("call_function", "erp_asArrivalDateChange");
	        params.put("function_name", "?????? AS ???????????? ??????");
	        response = apiCommonService.erp_callApiHistSave(params);
		
		} catch (Exception e) {
			txManager.rollback(status);
			System.out.println(e.toString());			
			response.setResultCode("5001");
			response.setResultMessage(e.toString());
			return gson.toJson(response);
		}
		
		txManager.commit(status);
		response.setResultCode("200");
		System.out.println(response.toString());	
		return gson.toJson(response);
	}

	
	@ApiOperation(value = "erp_asHpChange", notes = "AS ?????? ????????? ?????? ??????")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK !!"), @ApiResponse(code = 5001, message = "AS AS ?????? ????????? ?????? ?????? ?????? !!") })
	@GetMapping("/erp_asHpChange")
	@RequestMapping(value = "/erp_asHpChange", method = RequestMethod.POST)
	public String erp_asHpChange(
			@RequestHeader(value="CertKey", required=false) String CertKey ,
			@RequestParam(name = "key_no", required = false) String key_no,
			@RequestParam(name = "ctm_hp", required = false) String ctm_hp
			) {
		       
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
		int res = 0;
		BaseResponse response = new BaseResponse();
		DataResult dataResult = new DataResult();
		
		try {
			
			String res2 = "";
	        if ("58BEA9A133900619879E8FFF1ECB0F336A5672CF6B68EB56B0D229FF4C6A7D52".equals(CertKey)) {
	        	res2 = "Your CertiKey is Valid.";
	        } else {
	        	return gson.toJson("Your CertiKey is inValid."); 
	        } 	
	        
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("key_no", key_no);
			params.put("ctm_hp", ctm_hp);
			
        	//tc_resmst ??????
        	res = cChatBotChangeMapper.updateAsHpChange(params);
        	
			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("updateAsHpChange ?????? [" + res + "]");
				return gson.toJson(response);
			}			
			
			//AUTO???????????? ?????? ????????? insert
			params.put("chgreq_hp", ctm_hp);
			params.put("chgreq_dt", "");
			params.put("chgreq_addr", "");
			params.put("reqorm_no", key_no);
			params.put("chgreq_std", "A96007");
			
			res = cChatBotChangeMapper.insertAsAutoChgReqInformation(params);
			
			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("insertAsAutoChgReqInformation ?????? [" + res + "]");
				return gson.toJson(response);
			}

	        params.put("call_function", "erp_asHpChange");
	        params.put("function_name", "?????? AS ?????? ????????? ?????? ??????");
	        response = apiCommonService.erp_callApiHistSave(params);			
			
		} catch (Exception e) {
			txManager.rollback(status);
			System.out.println(e.toString());			
			response.setResultCode("5001");
			response.setResultMessage(e.toString());
			return gson.toJson(response);
		}
		
		txManager.commit(status);
		response.setResultCode("200");
		System.out.println(response.toString());	
		return gson.toJson(response);
	}

	@ApiOperation(value = "erp_OrderChangeDtRequest", notes = "?????? ???????????? ??????")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK !!"), @ApiResponse(code = 5001, message = "?????? ???????????? ?????? ?????? !!") })
	@GetMapping("/erp_OrderChangeDtRequest")
	@RequestMapping(value = "/erp_OrderChangeDtRequest", method = RequestMethod.GET)
	public String erp_orderArrivalDateChange(
			@RequestHeader(value="CertKey", required=false) String CertKey ,
			@RequestParam(name = "key_no", required = false) String key_no,
			@RequestParam(name = "com_agsec", required = false) String com_agsec,
			@RequestParam(name = "req_dt", required = false) String req_dt
			) {
		       
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
		int res = 0;
		BaseResponse response = new BaseResponse();
		DataResult dataResult = new DataResult();
		
		try {
			
			String res2 = "";
	        if ("58BEA9A133900619879E8FFF1ECB0F336A5672CF6B68EB56B0D229FF4C6A7D52".equals(CertKey)) {
	        	res2 = "Your CertiKey is Valid.";
	        } else {
	        	return gson.toJson("Your CertiKey is inValid."); 
	        } 	
	        
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("key_no", key_no);
			params.put("com_agsec", com_agsec);
			params.put("req_dt", req_dt);
			
			
			//???????????? ?????? ??????
			//1. ??????????????? ??????
			//   - ???????????? ??????????????????
			//   - ?????????????????? ??????
			//   - ???????????? ?????? ?????? ?????? ??????
			//2. ??????????????? ??????
			//   - ???????????? ?????? ?????? ?????? ??????
			//   - ?????????????????? ??????
			
			//step1 - ???????????? ??????
			
			dataResult = cChatBotChangeMapper.selectSigongYn(params);
			
			String orm_cryn = "";
			orm_cryn = dataResult.getData1();
			
			if ("Y".equals(orm_cryn)) {  //???????????? ??????
				
				//- ???????????? ??????????????????
				dataResult = cChatBotChangeMapper.selectSchDivYn(params);
				
				String sch_divyn = "";
				sch_divyn = dataResult.getData1();
				
				
				if ("Y".equals(sch_divyn)) {
					txManager.rollback(status);
					response.setResultCode("5001");
					response.setResultMessage("?????? ????????? ????????? ????????????. ?????? ????????? ???????????????.");
					return gson.toJson(response);
				}				
				
				//- ???????????? ??????????????????
				dataResult = cChatBotChangeMapper.selectSigongConfirmYn(params);
				
				String com_rfg = "";
				com_rfg = dataResult.getData1();
				
				
				if ("C142".equals(com_rfg)) {
					txManager.rollback(status);
					response.setResultCode("5001");
					response.setResultMessage("?????? ????????? ????????? ????????????. ?????? ????????? ???????????????.");
					return gson.toJson(response);
				}					
				
			}else {
		
				//- ?????????????????? ??????
				dataResult = cChatBotChangeMapper.selectFinishYn(params);
				
				String finish_yn = "";
				finish_yn = dataResult.getData1();
				
				
				if ("Y".equals(finish_yn)) {
					txManager.rollback(status);
					response.setResultCode("5001");
					response.setResultMessage("?????? ?????? ????????? ????????????.");
					return gson.toJson(response);
				}				
			}
			
			//?????????????????? ????????? ?????? ??????

			dataResult = cChatBotChangeMapper.selectChgstatusYn(params);
			
			String req_status = "";
			req_status = dataResult.getData1();
			
			
			if ("Y".equals(req_status)) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("????????? ?????? ????????? ?????? ?????? ?????? ????????????.");
				return gson.toJson(response);
			}			
			
			//?????????????????? - ?????????????????? insert
        	res = cChatBotChangeMapper.insertOrmCdtChangeReq(params);
        	
			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("insertOrmCdtChangeReq ?????? [" + res + "]");
				return gson.toJson(response);
			}			
			
	        params.put("call_function", "erp_OrderChangeDtRequest");
	        params.put("function_name", "?????? ?????? ???????????? ??????");
	        response = apiCommonService.erp_callApiHistSave(params);			
			
		} catch (Exception e) {
			txManager.rollback(status);
			System.out.println(e.toString());			
			response.setResultCode("5001");
			response.setResultMessage(e.toString());
			return gson.toJson(response);
		}
		
		txManager.commit(status);
		response.setResultCode("200");
		System.out.println(response.toString());	
		return gson.toJson(response);
	}

	
	
	
	@ApiOperation(value = "erp_orderHpChange", notes = "?????? ??????????????? ??????")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK !!"), @ApiResponse(code = 5001, message = "?????? ??????????????? ?????? ?????? !!") })
	@GetMapping("/erp_orderHpChange")
	@RequestMapping(value = "/erp_orderHpChange", method = RequestMethod.POST)
	public String erp_orderHpChange(
			@RequestHeader(value="CertKey", required=false) String CertKey ,
			@RequestParam(name = "key_no", required = false) String key_no,
			@RequestParam(name = "ctm_hp", required = false) String ctm_hp
			) {
		       
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
		int res = 0;
		BaseResponse response = new BaseResponse();
		DataResult dataResult = new DataResult();
		
		try {
			
			String res2 = "";
	        if ("58BEA9A133900619879E8FFF1ECB0F336A5672CF6B68EB56B0D229FF4C6A7D52".equals(CertKey)) {
	        	res2 = "Your CertiKey is Valid.";
	        } else {
	        	return gson.toJson("Your CertiKey is inValid."); 
	        } 	
	        
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("key_no", key_no);
			params.put("ctm_hp", ctm_hp);
			
        	//tc_resmst ??????
        	res = cChatBotChangeMapper.updateOrderHpChange(params);
        	
			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("updateOrderHpChange ?????? [" + res + "]");
				return gson.toJson(response);
			}			

			//??????????????? ?????? ?????? ????????? insert ??????
			
			dataResult = cChatBotChangeMapper.selectOnlineynCheck(params);
			
			String online_yn = "";
			String orm_nm = "";			
			String message= "";
			
			online_yn = dataResult.getData1();
			orm_nm = dataResult.getData2();
			
			message = "????????? ?????? ????????? ????????? ?????? ?????? ?????? ???????????????. (???????????? : " + key_no + ",  ???????????? : " + orm_nm + " )";
					
			if(online_yn.equals("N")) {
				
				params.put("message", message);
				
				res = cChatBotChangeMapper.insertAgtChangeAlarm(params);
				
				if (res < 0) {
					txManager.rollback(status);
					response.setResultCode("5001");
					response.setResultMessage("insertAgtChangeAlarm ???????????????????????? insert ?????? [" + res + "]");
					return gson.toJson(response);
				}					
			}

	        params.put("call_function", "erp_orderHpChange");
	        params.put("function_name", "?????? ?????? ????????? ??????????????? ??????");
	        response = apiCommonService.erp_callApiHistSave(params);	

			
	    	JSONObject obj = new JSONObject();
	    	JSONArray jArray = new JSONArray(); //????????? ????????????
	    	JSONArray jArray2 = new JSONArray(); //????????? ????????????
	    	
	    	JSONObject api_token = new JSONObject();
	    	JSONObject sendList = new JSONObject();	    	 
	    	JSONObject sObject = new JSONObject();//?????? ?????? ????????? json
	    	JSONObject sObject2 = new JSONObject();//?????? ?????? ????????? json
	    	
	    	String templateCode = "";
	    	String senderkey = "";
	    	String title = "";
	    	String subject = "";
	    	String from_no = "";
	    	String message_type = "";
	    		        
	        //?????????????????? ?????? ?????? ????????????	    	
	    	params.put("key_no", key_no);
	    	dataResult = cChatBotChangeMapper.selectChginformData(params);
	        
	        String com_brd = "";
	        String com_tono = "";
	        String org_orm_nm = "";
	        String send_yn = "";
	        String biztalkmessage = "";
	        String sales_managerhp = "";
	        
	        com_brd = dataResult.getData1();
	        org_orm_nm = dataResult.getData2();
	        com_tono = dataResult.getData3();
	        send_yn = dataResult.getData4();
	        sales_managerhp = dataResult.getData5();
	        
			title = "(CHATBOT)???????????????AUTO????????????";
			subject = "(CHATBOT)???????????????AUTO????????????";	      
    		
			String chg_content = "";
			chg_content = "???????????????";
//	    	biztalkmessage = "(????????????) ??????????????? "+orm_nm+" ?????????, ???????????? ???????????? ?????? " + "" +pay_amt3+ "?????? ?????????????????????.\r\n\r\n" +
//					 "1) ???????????? : "+""+pay_no+"\r\n"+
//					 "2) ???????????? : "+""+rem_dt+"";s	
			
			String pattern = "yyyy-MM-dd";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

			String date = simpleDateFormat.format(new Date());
			System.out.println(date);
		        
	        if(send_yn.equals("Y")) {
	        
	        	if(com_brd.equals("T60I01")) {
        		
	        		biztalkmessage = "???????????????.\r\n" +
	        					"??? ????????? ???????????? ??????????????? ?????? ????????? ?????? ?????? ????????? ???????????? ?????? ????????????.\r\n\r\n" +
	        					"???????????? ??????\r\n"+
	        					"???????????? : "+key_no+"\r\n"+
	        					"???????????? : "+org_orm_nm+"\r\n"+
	        					"???????????? : "+chg_content+"\r\n"+
	        					"???????????? : "+date+"\r\n\r\n"+
	        					"?????? ?????? ?????? ??????????????????.\r\n"+
	        					"???????????????.\r\n"
	        					;
	        					
	        		senderkey = "dbf8669a88dd7926fd653ff3ff9b23d331fbbb4c";
	        		from_no = "1577-5670";
	        		templateCode = "iloomchatbotorderchangeinform0";
	        		
	        	}else if(com_brd.equals("T60I02")) {
	        		
	        		biztalkmessage = "???????????????.\r\n" +
        					"??? ????????? ???????????? ?????????????????? ?????? ????????? ?????? ?????? ????????? ???????????? ?????? ????????????.\r\n\r\n" +
        					"???????????? ??????\r\n"+
        					"???????????? : "+key_no+"\r\n"+
        					"???????????? : "+org_orm_nm+"\r\n"+
        					"???????????? : "+chg_content+"\r\n"+
        					"???????????? : "+date+"\r\n\r\n"+
        					"?????? ?????? ?????? ??????????????????.\r\n"+
        					"???????????????.\r\n"
        					;
		
	        		senderkey = "9917d09567d2ebf1acc89662d7f9ff10db1488d7";
	        		from_no = "1588-1662";
	        		templateCode = "deskerchatbotorderchginform0";
	        		
	        	}else if(com_brd.equals("T60I03")) {
	        		
	        		biztalkmessage = "???????????????.\r\n" +
        					"??? ????????? ???????????? ?????????????????? ?????? ????????? ?????? ?????? ????????? ???????????? ?????? ????????????.\r\n\r\n" +
        					"???????????? ??????\r\n"+
        					"???????????? : "+key_no+"\r\n"+
        					"???????????? : "+org_orm_nm+"\r\n"+
        					"???????????? : "+chg_content+"\r\n"+
        					"???????????? : "+date+"\r\n\r\n"+
        					"?????? ?????? ?????? ??????????????????.\r\n"+
        					"???????????????.\r\n"
        					;
	        		
	        		senderkey = "3ed320702f733d0b5a31e99a3ba931d9f2f9f960";
	        		from_no = "1899-8588";
	        		templateCode = "slouchatbotorderchangeinform0";	        		

	        	}else if(com_brd.equals("T60P01")) {
	        		
	        		biztalkmessage = "???????????????.\r\n" +
        					"??? ????????? ???????????? ?????????????????? ?????? ????????? ?????? ?????? ????????? ???????????? ?????? ????????????.\r\n\r\n" +
        					"???????????? ??????\r\n"+
        					"???????????? : "+key_no+"\r\n"+
        					"???????????? : "+org_orm_nm+"\r\n"+
        					"???????????? : "+chg_content+"\r\n"+
        					"???????????? : "+date+"\r\n\r\n"+
        					"?????? ?????? ?????? ??????????????????.\r\n"+
        					"???????????????.\r\n"
        					;
	        		
	        		senderkey = "6b94c758a1f689223024765ae6e2b0aede351955";
	        		from_no = "1577-5674";
	        		templateCode = "sidizchatbotorderchangeinform0";	  
	        		
	        	}else {
	        		
	        	}
	       	        	
		    	sendList.put("authKey", "D62D413F25CD43B3BD06636F2B3F570ABFB5008BD727901E341F041448D22C3A6593D58D45C68E60171F7FB2B2C345459361A08D20298BAE6A3A1B74196A95C3");
				
		       	 for (int i = 0; i < 1; i++)//??????
		       	 {
			        	 
			        	 
			        	 sObject.put("sendDiv", "BIZTALK" );
			        	 sObject.put("title", title);
			        	 sObject.put("subject", subject );		  
			        	 sObject.put("message","");
			        	 sObject.put("fromNm", org_orm_nm );
			        	 sObject.put("toNm", org_orm_nm );
			        	 sObject.put("fromNo", from_no ); 
			        	 sObject.put("toNo", com_tono);
			        	 sObject.put("companyCd", "T01B" );		        	 
			        	 sObject.put("fstUsr", "CHATBOT" );
			        	 sObject.put("systemNm", "CHATBOT" );
			        	 sObject.put("sendType", "SMTP" );
			        	 sObject.put("reserveDiv","I");
			        	 sObject.put("reserveDt", "" );
			        	 sObject.put("keyNo", key_no);
			        	 sObject.put("msgType", "TI4N" );		        	 
			        	 sObject.put("senderKey", senderkey);
			        	 sObject.put("templateCode", templateCode );
			        	 sObject.put("bizTalkMessage", biztalkmessage );
			        	 sObject.put("comBrd", com_brd );
			        	 
			        	 jArray.add(sObject);
		       	 }        	 
	        		        			
	        	sendList.put("list" ,jArray);  
	        	
	        	
	        	BaseResponse kakao_res = RestCall("https://msg-api.fursys.com/v1/api/message/SendMsg",sendList);	
	        	if (!"200".equals(kakao_res.getResultCode())) {
					response.setResultCode("5001");
					response.setResultMessage("????????????????????? ??????  [" + kakao_res.getResultMessage() + "]");
					return gson.toJson(response);
				}
				
	        	
	        	if(com_brd.equals("T60I01")) {
					
			       	 for (int i = 0; i < 1; i++)//??????
			       	 {
				        	 
				        	 
				        	 sObject2.put("sendDiv", "BIZTALK" );
				        	 sObject2.put("title", title);
				        	 sObject2.put("subject", subject );		  
				        	 sObject2.put("message","");
				        	 sObject2.put("fromNm", org_orm_nm );
				        	 sObject2.put("toNm", org_orm_nm );
				        	 sObject2.put("fromNo", from_no ); 
				        	 sObject2.put("toNo", sales_managerhp);
//				        	 sObject2.put("toNo", "010-3652-9837");
				        	 sObject2.put("companyCd", "T01B" );		        	 
				        	 sObject2.put("fstUsr", "CHATBOT" );
				        	 sObject2.put("systemNm", "CHATBOT" );
				        	 sObject2.put("sendType", "SMTP" );
				        	 sObject2.put("reserveDiv","I");
				        	 sObject2.put("reserveDt", "" );
				        	 sObject2.put("keyNo", key_no);
				        	 sObject2.put("msgType", "TI4N" );		        	 
				        	 sObject2.put("senderKey", senderkey);
				        	 sObject2.put("templateCode", templateCode );
				        	 sObject2.put("bizTalkMessage", biztalkmessage );
				        	 sObject2.put("comBrd", com_brd );
				        	 
				        	 jArray2.add(sObject2);
			       	 }        	 
		        		        			
		        	sendList.put("list" ,jArray2);  
		        	 
		        	BaseResponse kakao_res2 = RestCall("https://msg-api.fursys.com/v1/api/message/SendMsg",sendList);	
		        	
		        	if (!"200".equals(kakao_res2.getResultCode())) {
						response.setResultCode("5001");
						response.setResultMessage("????????????????????? ??????  [" + kakao_res2.getResultMessage() + "]");
						return gson.toJson(response);
					}
	        	}
	        		
	        }	        
	        
		} catch (Exception e) {
			txManager.rollback(status);
			System.out.println(e.toString());			
			response.setResultCode("5001");
			response.setResultMessage(e.toString());
			return gson.toJson(response);
		}
		
		txManager.commit(status);
		response.setResultCode("200");
		System.out.println(response.toString());	
		return gson.toJson(response);
	}	

	@ApiOperation(value = "erp_chgmindate", notes = "???????????? ?????? ???????????? ??????")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK !!"), @ApiResponse(code = 5001, message = "???????????? ?????? ???????????? ?????? ?????? !!") })
	@GetMapping("/erp_chgmindate")
	@RequestMapping(value = "/erp_chgmindate", method = RequestMethod.GET)
	public String erp_chgmindate(
			@RequestHeader(value="CertKey", required=false) String CertKey ,
			@RequestParam(value="com_brand", required=false) String com_brand ,
			@RequestParam(name = "key_no", required = false) String key_no
			) {
		       
		int res = 0;
		BaseResponse response = new BaseResponse();
		DataResult dataResult = new DataResult();
		ERPOrderFinalAvailableDate item = null;
		
		try {
			
			String res2 = "";
	        if ("58BEA9A133900619879E8FFF1ECB0F336A5672CF6B68EB56B0D229FF4C6A7D52".equals(CertKey)) {
	        	res2 = "Your CertiKey is Valid.";
	        } else {
	        	return gson.toJson("Your CertiKey is inValid."); 
	        } 	
	        
			String com_agsec = "";
			if("T60F01".equals(com_brand) ) {
				com_agsec = "C02F";
			} else if ("T60I01".equals(com_brand) ) {
				com_agsec = "C02I";
			} else if ("T60I02".equals(com_brand) ) {
				com_agsec = "C02I";
			} else if ("T60I03".equals(com_brand) ){
				com_agsec = "C02I";
			} else if ("T60P01".equals(com_brand) ) {
				com_agsec = "C02P";
			} else if ("T60P02".equals(com_brand) ){
				com_agsec = "C02P";
			} else {
				
			}
				
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("key_no", key_no);
			
			//1. ??????????????????????????? ?????? 
			//   - ??????????????? ???????????? ????????????
			//   - executePraMindateZipChatbot ??????
			
			//?????? or ????????? ?????? ??????
			//2-1. ????????? ?????? - ???????????? ?????????????????? ????????????
			//	   - 
			//2-2. ?????? ?????? ?????? 1????????? ??????????????????????????? return
			
			dataResult = cChatBotChangeMapper.selectSigongYn(params);
			
			String orm_cryn = "";
			orm_cryn = dataResult.getData1();		

			//??????????????????????????? ??????
			dataResult = cChatBotChangeMapper.selectOrmZipCd(params);
			
			String orm_gpost = "";
			orm_gpost = dataResult.getData1();	
			
			dataResult = cChatBotChangeMapper.selectOrmVndCd(params);
			
			String vnd_cd = "";
			vnd_cd = dataResult.getData1();	
			
			HashMap<String, Object> params2 = new HashMap<String, Object>();
			params2.put("vnd_cd", vnd_cd);			
			params2.put("orm_gpost", orm_gpost);	
			
			
			item = cChatBotChangeMapper.executePraMindateZipChatbot(params2);
					
			if (item == null) {
//				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("executePraMindateZipChatbot ??????");
				return gson.toJson(item);
			}
			
			String orm_cdt = "";			
			orm_cdt = item.getAvailable_date();	
			
			if("Y".equals(orm_cryn)) {
				
				String com_scd = "";
				String bdong_cd = "";
				String sti_cd = "";
				
				//com_scd ???????????? 
				dataResult = cChatBotChangeMapper.selectOrmComScd(params);
				
				com_scd = dataResult.getData1();	
				
				HashMap<String, Object> params3 = new HashMap<String, Object>();
				params3.put("key_no", key_no);			
				params3.put("com_scd", com_scd);					
				
				dataResult = cChatBotChangeMapper.selectOrmStiCd(params3);
		
				sti_cd = dataResult.getData1();	
				
				
				//bdong_cd ????????????
				dataResult = cChatBotChangeMapper.selectOrmBdongCd(params3);
				
				bdong_cd = dataResult.getData1();	
				
				
				// sti_cd ????????????
				
				HashMap<String, Object> params4 = new HashMap<String, Object>();
				params4.put("com_agsec", com_agsec);
				params4.put("com_brand", com_brand);
				params4.put("bdong_cd", bdong_cd);
				params4.put("available_date", orm_cdt);			
				params4.put("com_scd", com_scd);					
				params4.put("sti_cd", sti_cd);	

				item = cChatBotChangeMapper.executePraDayInfChatbot(params4);
				
				if (item == null) {
//					txManager.rollback(status);
					response.setResultCode("5001");
					response.setResultMessage("executePraDayInfChatbot ??????");
					return gson.toJson(item);
				}				
				
				orm_cdt = item.getAvailable_date();	
				
			}
			
	        params.put("call_function", "erp_chgmindate");
	        params.put("function_name", "?????? ???????????? ?????? ????????? ??????");
	        response = apiCommonService.erp_callApiHistSave(params);			
	        
		} catch (Exception e) {
			
			response.setResultCode("5001");
			response.setResultMessage(e.toString());
			return gson.toJson(item);
		}
		
		response.setResultCode("200");
		return gson.toJson(item);
	}	

	@ApiOperation(value = "erp_orderChangeDtAvailableYn", notes = "???????????? ?????? ?????? ??????")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK !!"), @ApiResponse(code = 5001, message = "???????????? ?????? ?????? ?????? ?????? !!") })
	@GetMapping("/erp_orderChangeDtAvailableYn")
	@RequestMapping(value = "/erp_orderChangeDtAvailableYn", method = RequestMethod.GET)
	public String erp_orderChangeDtAvailableYn(
			@RequestHeader(value="CertKey", required=false) String CertKey ,
			@RequestParam(value="key_no", required=false) String key_no,
			@RequestParam(value="com_cmp", required=false) String com_cmp,
			@RequestParam(value="com_brand", required=false) String com_brand,
			@RequestParam(value="com_agsec", required=false) String com_agsec,
			@RequestParam(value="req_dt", required=false) String req_dt
			) {
		       
//		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
		int res = 0;
		BaseResponse response = new BaseResponse();
		DataResult dataResult = new DataResult();
		String item = null;
		
		try {
			
			String res2 = "";
	        if ("58BEA9A133900619879E8FFF1ECB0F336A5672CF6B68EB56B0D229FF4C6A7D52".equals(CertKey)) {
	        	res2 = "Your CertiKey is Valid.";
	        } else {
	        	return gson.toJson("Your CertiKey is inValid."); 
	        } 	


	        
	        
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("key_no", key_no);	
			
			dataResult = cChatBotChangeMapper.selectOrmCryn(params);
			
			String orm_cryn = "";
			String orm_gpost = "";
			String final_date = "";
			String available_yn = "";
			String sigongreserve_yn = "";
			String com_scd = "";
			String sti_cd = "";
			String final_sigong_date = "";
			String bdong_cd = "";
			String orm_bdong_cd = "";
			String new_com_scd = "";
			String jundam_yn = "";
			String new_sti_cd = "";
			String new_bdong_cd = "";
			
			
			orm_cryn = dataResult.getData1();

		    //?????? ????????????????????? ??? ???????????? ????????? ???????????? ????????? available_yn??? ????????? "N"	        
	        String org_orm_cdt = "";			
			
	        org_orm_cdt = dataResult.getData2(); 
	        
	        if(Integer.parseInt(req_dt) <= Integer.parseInt(org_orm_cdt)) {
	        	available_yn = "N";
	        	item = available_yn ;
	        	
	        } else {
	
				dataResult = cChatBotChangeMapper.selectOrmGpost(params);
				
				orm_gpost = dataResult.getData1();
				orm_bdong_cd = dataResult.getData2();
				new_bdong_cd = orm_bdong_cd ;
				
				params.put("com_cmp", com_cmp);
				params.put("orm_gpost", orm_gpost);
				params.put("req_dt", req_dt);
				params.put("com_brand", com_brand);
		
	//			*?????? ????????????
	//			call PRC_DAYINF('C02I', 'T60I01', '1174010800', '20210330', 'C16YA', 'YA200', '1')
	//			* ?????????????????????
	//			call pro_ren_preenposymd('I20210321000100','T01I','05373','20210330','T60I01','<OUT>'
	//			* ????????? ????????????
				
				//??????????????? ??????
				if("Y".equals(orm_cryn)) {
					dataResult = cChatBotChangeMapper.selectIloompreenposymd(params);
					final_date = dataResult.getData1();
					
					
					//?????????????????? ??????
					if (Integer.parseInt(final_date) <= Integer.parseInt(req_dt)) {
					
						dataResult = cChatBotChangeMapper.selectSigongReserveYn(params);
						sigongreserve_yn = dataResult.getData1();
						
						//?????? ??????????????? ?????? ?????? ??????
						
						if("Y".equals(sigongreserve_yn)) {
							
							dataResult = cChatBotChangeMapper.selectSigongReserveInfo(params);
							com_scd = dataResult.getData1();
							sti_cd = dataResult.getData2();
							bdong_cd = dataResult.getData3();
							
							params.put("com_agsec", com_agsec);
							params.put("com_brand", com_brand);
							params.put("bdong_cd", bdong_cd);
							params.put("req_dt", req_dt);
							params.put("com_scd", com_scd);
							params.put("sti_cd", sti_cd);
							
							dataResult = cChatBotChangeMapper.selectProcDayinf(params);
							final_sigong_date = dataResult.getData1();
							
							if("Y".equals(final_sigong_date)) {
								System.out.println("?????????");
								available_yn = "Y";
								item = available_yn;								
							} else {
								System.out.println("????????????1");
								available_yn = "N";
								item = available_yn;								
							}
							
						//??????????????? ?????? ?????? ?????? ?????? 	
						}else {
							
							params.put("key_no", key_no);
							dataResult = cChatBotChangeMapper.selectGetComScd(params);
							
							new_com_scd = dataResult.getData1();
							
							//?????????????????? ????????? ??????
							
							params.put("key_no", key_no);
							params.put("new_com_scd", new_com_scd);
							
							dataResult = cChatBotChangeMapper.selectJundamYn(params);
							
							jundam_yn = dataResult.getData1();						
							
							if("Y".equals(jundam_yn)) {
								
								params.put("key_no", key_no);
								params.put("new_com_scd", new_com_scd);	
								
								dataResult = cChatBotChangeMapper.selectJundamStiCd(params);
								
								new_sti_cd = dataResult.getData1();
								
							}else {
								
								params.put("com_agsec", com_agsec);
								params.put("com_brand", com_brand);								
								params.put("new_bdong_cd", new_bdong_cd);			
								params.put("new_com_scd", new_com_scd);
				
								dataResult = cChatBotChangeMapper.selectGetNormalStiCd(params);
								
								new_sti_cd = dataResult.getData1();
								
							}
							
							params.put("com_agsec", com_agsec);
							params.put("com_brand", com_brand);
							params.put("new_bdong_cd", new_bdong_cd);
							params.put("req_dt", req_dt);
							params.put("com_scd", new_com_scd);
							params.put("sti_cd", new_sti_cd);
							
							dataResult = cChatBotChangeMapper.selectProcDayinf(params);
							
							final_sigong_date = dataResult.getData1();
							
							if("Y".equals(final_sigong_date)) {
								System.out.println("?????????2");
								//???????????????
								
								params.put("req_dt", req_dt);
								dataResult = cChatBotChangeMapper.selectWorkingYnCheck(params);
								
								String working_yn = "";
								
								working_yn = dataResult.getData1();
								
								if ("Y".equals(working_yn)) {
									
									available_yn = "Y";
									item = available_yn;										
								
								} else {
									
									available_yn = "N";
									item = available_yn;								
								}
									
							} else {
								System.out.println("????????????2");
								available_yn = "N";
								item = available_yn;								
							}						
		
						}
						
					} else {
						System.out.println("????????????3");
						available_yn = "N";
						item = available_yn;
					}
					
					
					
				}
				//?????????????????????
				else {
					
					dataResult = cChatBotChangeMapper.selectIloompreenposymd(params);
					final_date = dataResult.getData1();
					
					if (Integer.parseInt(final_date) <= Integer.parseInt(req_dt)) {
						
						params.put("orm_adt", req_dt);
						dataResult = cChatBotChangeMapper.selectWorkingYnCheck(params);
						
						String working_yn = "";
						
						working_yn = dataResult.getData1();
						
						if ("Y".equals(working_yn)) {
							
							available_yn = "Y";
							item = available_yn;										
						
						} else {
							
							available_yn = "N";
							item = available_yn;								
						}
						
					} else {
						System.out.println("????????????4");
						available_yn = "N";
						item = available_yn;
					}				
				}

	        }
	        
	        params.put("call_function", "erp_orderChangeDtAvailableYn");
	        params.put("function_name", "?????? ???????????? ???????????? ??????");
	        response = apiCommonService.erp_callApiHistSave(params);				
			
		} catch (Exception e) {
			
			response.setResultCode("5001");
			response.setResultMessage(e.toString());
			return gson.toJson(item);
		}
		
		response.setResultCode("200");
		return gson.toJson(item);
	}	

	//?????????
	@ApiOperation(value = "erp_orderChangeAvailableDateList", notes = "????????????????????? ?????? ????????? ??????")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK !!"), @ApiResponse(code = 5001, message = "????????????????????? ?????? ????????? ?????? ?????? !!") })
	@GetMapping("/erp_orderChangeAvailableDateList")
	@RequestMapping(value = "/erp_orderChangeAvailableDateList", method = RequestMethod.GET)
	public String erp_orderChangeAvailableDateList(
			@RequestHeader(value="CertKey", required=false) String CertKey ,
			@RequestParam(name="key_no", required=false) String key_no,
			@RequestParam(name="req_dt", required=false) String req_dt,
			@RequestParam(name="com_cmp", required=false) String com_cmp,
			@RequestParam(name="com_brand", required=false) String com_brand,
			@RequestParam(name="com_agsec", required=false) String com_agsec
		) { 

		
		String res2 = "";
        if ("58BEA9A133900619879E8FFF1ECB0F336A5672CF6B68EB56B0D229FF4C6A7D52".equals(CertKey)) {
        	res2 = "Your CertiKey is Valid.";
        } else {
        	return gson.toJson("Your CertiKey is inValid."); 
        } 	
        
		DataResult dataResult = new DataResult();
		
		JSONObject sObject = new JSONObject();//?????? ?????? ????????? json
		JSONArray jArray = new JSONArray(); //????????? ????????????
		
		ArrayList<ERPOrderChangeAvailableDt> arList = null;
		
		int i = 0;
		String orm_adt = "";
		String orm_cryn = "";
		String final_date = "";
		String orm_gpost = "";
		String orm_bdong_cd = "";
		String working_yn = "";
		int ord_check = 0;
		String new_com_scd = "";
		String jundam_yn = "";
		String new_sti_cd = "";
		String final_sigong_date = "";
		String org_orm_cdt = "";
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("key_no", key_no);
		
		
		dataResult = cChatBotChangeMapper.selectSigongYn(params);
		orm_cryn = dataResult.getData1();
		org_orm_cdt = dataResult.getData2();
		
		dataResult = cChatBotChangeMapper.selectOrmGpost(params);
		
		orm_gpost = dataResult.getData1();
		orm_bdong_cd = dataResult.getData2();
		
		for (i = 0; i<10;i++) {
			
			sObject = new JSONObject();
			
			params.put("i", i);			
			params.put("req_dt", req_dt);
			
			dataResult = cChatBotChangeMapper.selectOrmAdtCheck(params);
			
			orm_adt = dataResult.getData1();
			
			//??????????????? ??????
			if ("Y".equals(orm_cryn)) {
				
				params.put("key_no", key_no);
				params.put("com_cmp", com_cmp);
				params.put("orm_gpost", orm_gpost);
				params.put("req_dt", orm_adt);
				params.put("com_brand", com_brand);				
				
				dataResult = cChatBotChangeMapper.selectIloompreenposymd(params);
				final_date = dataResult.getData1();
				
				if (Integer.parseInt(final_date) <= Integer.parseInt(orm_adt)) {
								
					//???????????????
					
					params.put("req_dt", orm_adt);
					dataResult = cChatBotChangeMapper.selectWorkingYnCheck(params);
					
					working_yn = dataResult.getData1();
					
					if ("Y".equals(working_yn)) {
						
						
						//?????? Capacity ??????
						//1. ??????????????? ?????? ??????
						
						params.put("key_no", key_no);
						dataResult = cChatBotChangeMapper.selectGetComScd(params);
						
						new_com_scd = dataResult.getData1();						
						
						params.put("key_no", key_no);
						params.put("new_com_scd", new_com_scd);
						
						dataResult = cChatBotChangeMapper.selectJundamYn(params);
						
						jundam_yn = dataResult.getData1();	

						if("Y".equals(jundam_yn)) {
							
							params.put("key_no", key_no);
							params.put("new_com_scd", new_com_scd);	
							dataResult = cChatBotChangeMapper.selectJundamStiCd(params);
														
							new_sti_cd = dataResult.getData1();
							
						}else {
							
							params.put("com_agsec", com_agsec);
							params.put("com_brand", com_brand);								
							params.put("new_bdong_cd", orm_bdong_cd);			
							params.put("new_com_scd", new_com_scd);
						
							dataResult = cChatBotChangeMapper.selectGetNormalStiCd(params);
							
							new_sti_cd = dataResult.getData1();
							
						}

						params.put("com_agsec", com_agsec);
						params.put("com_brand", com_brand);
						params.put("bdong_cd", orm_bdong_cd);
						params.put("req_dt", orm_adt);
						params.put("com_scd", new_com_scd);
						params.put("sti_cd", new_sti_cd);
						
						dataResult = cChatBotChangeMapper.selectProcDayinf(params);
						
						final_sigong_date = dataResult.getData1();
						System.out.println("======================================"+final_sigong_date);
						
						if("Y".equals(final_sigong_date)) {
							
							if (ord_check <= 4) {
								
								if(org_orm_cdt.equals(orm_adt)) {
									
								}else {
									
									sObject.put("orm_cdt", orm_adt);
									ord_check = ord_check + 1;									
								}
								

							}							
						}
					}					
				} 							
			}
			//??????????????? ??????
			else {
				
				params.put("key_no", key_no);
				params.put("com_cmp", com_cmp);
				params.put("orm_gpost", orm_gpost);
				params.put("req_dt", req_dt);
				params.put("com_brand", com_brand);
				
				dataResult = cChatBotChangeMapper.selectIloompreenposymd(params);
				final_date = dataResult.getData1();
				
				System.out.println("final_date    " + final_date);
				System.out.println("orm_adt    " + orm_adt);
				
				
				if (Integer.parseInt(final_date) <= Integer.parseInt(orm_adt)) {

					//???????????????
					
					params.put("req_dt", orm_adt);
					dataResult = cChatBotChangeMapper.selectWorkingYnCheck(params);
					
					working_yn = dataResult.getData1();
					
					if ("Y".equals(working_yn)) {
						
						if (ord_check <= 4) {
							if(org_orm_cdt.equals(orm_adt)) {
								
							} else {
								sObject.put("orm_cdt", orm_adt);
								ord_check = ord_check + 1;								
							}

						}								
					}					
				} 						
			}	
			
			jArray.add(sObject);			
		}
		
        BaseResponse response = new BaseResponse();		
        
        params.put("call_function", "erp_orderChangeAvailableDateList");
        params.put("function_name", "?????? ????????????????????? ?????? ????????? ??????");
        response = apiCommonService.erp_callApiHistSave(params);			
        
	    return gson.toJson(jArray); 
	}

		
	
	@ApiOperation(value = "erp_selectbdongcode", notes = "??????????????? ????????????")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK !!"), @ApiResponse(code = 5001, message = "??????????????? ???????????? ?????? ?????? !!") })
	@GetMapping("/erp_selectbdongcode")
	@RequestMapping(value = "/erp_selectbdongcode", method = RequestMethod.GET)
	public String erp_selectbdongcode(
			@RequestHeader(value="CertKey", required=false) String CertKey ,
			@RequestParam(value="zip_cd", required=false) String zip_cd 
			) {
		       
//		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
		int res = 0;
		BaseResponse response = new BaseResponse();
		DataResult dataResult = new DataResult();
		ERPBdongCdVo item = null;
		
		try {
			
			String res2 = "";
	        if ("58BEA9A133900619879E8FFF1ECB0F336A5672CF6B68EB56B0D229FF4C6A7D52".equals(CertKey)) {
	        	res2 = "Your CertiKey is Valid.";
	        } else {
	        	return gson.toJson("Your CertiKey is inValid."); 
	        } 	
	        
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("zip_cd", zip_cd);			
			
			item = cChatBotChangeMapper.selectBdongCd(params);
			
			if (item == null) {
//				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("selectBdongCd ??????");
				return gson.toJson(item);
			}			
			
	        params.put("call_function", "erp_selectbdongcode");
	        params.put("function_name", "?????? ????????? ?????? ????????????");
	        response = apiCommonService.erp_callApiHistSave(params);	
	        
		} catch (Exception e) {
			
			response.setResultCode("5001");
			response.setResultMessage(e.toString());
			return gson.toJson(item);
		}

        
		response.setResultCode("200");
		return gson.toJson(item);
	}	
	
	@ApiOperation(value = "erp_sameOrderCheck", notes = "ERP_API_?????????????????? ?????? ??????")
	@ApiResponses({ @ApiResponse(code = 200, message = "ERP_API_?????????????????? ?????? ?????? ?????? ?????? !!"), @ApiResponse(code = 5001, message = "ERP_API_?????????????????? ?????? ?????? ?????? ?????? !!") })
	@GetMapping("/erp_sameOrderCheck")
	@RequestMapping(value = "/erp_sameOrderCheck", method = RequestMethod.GET)
	public String erp_sameOrderCheck(
			@RequestHeader(value="CertKey", required=false) String CertKey ,
			@RequestParam(name = "key_no", required = false) String key_no
			) {
		       
		BaseResponse response = new BaseResponse();
		DataResult dataResult = new DataResult();
		
		ERPSameOrderYn item = null;
				
		try {
			
			String res2 = "";
	        if ("58BEA9A133900619879E8FFF1ECB0F336A5672CF6B68EB56B0D229FF4C6A7D52".equals(CertKey)) {
	        	res2 = "Your CertiKey is Valid.";
	        } else {
	        	return gson.toJson("Your CertiKey is inValid."); 
	        } 	
	        
			HashMap<String,Object> params = new HashMap<String, Object>();
			params.put("key_no",key_no);
			
			String sameorder_yn = "";
			
			//????????? or ???????????? ?????? ??????
		
			dataResult = cChatBotChangeMapper.selectOnlineYn(params);
			
			//data1 = ?????????????????????
			//data2 = ???????????????
			//data3 = ????????????
			//data4 = ????????????
			//data5 = ???????????????
			//data6 = ????????? ??????
			
			String online_yn = "";
			String shp_no = "";
			String bdong = "";
			String zip_cd = "";
			String orm_orddt = "";
			String agt_cd = "";
			String orm_purcst = "";
			
			online_yn = dataResult.getData6();
			shp_no = dataResult.getData1();
			bdong = dataResult.getData2();
			zip_cd = dataResult.getData3();
			orm_orddt = dataResult.getData4();
			agt_cd = dataResult.getData5();
			orm_purcst = dataResult.getData7();
			
			params.put("shp_no", shp_no);
			params.put("bdong", bdong);
			params.put("zip_cd", zip_cd);
			params.put("orm_orddt", orm_orddt);
			params.put("agt_cd", agt_cd);
			params.put("orm_purcst", orm_purcst);
			
			if("4".equals(online_yn)) {
			//????????? ????????? ??????	
				
				item = cChatBotChangeMapper.selectOnlineSameOrderYn(params);
				
				sameorder_yn = dataResult.getData1();
				
			} else {
			// ????????? ????????? ?????? ??????	
				
				item = cChatBotChangeMapper.selectOfflineSameOrderYn(params);
				
				sameorder_yn = dataResult.getData1();
				
			}
			
	        params.put("call_function", "erp_sameOrderCheck");
	        params.put("function_name", "?????? ERP_API_?????????????????? ?????? ??????");
	        response = apiCommonService.erp_callApiHistSave(params);
	        
		} catch (Exception e) {
			
			response.setResultCode("5001");
			response.setResultMessage(e.toString());
			return gson.toJson(dataResult);
		}
		
		response.setResultCode("200");
		return gson.toJson(item);
	}	

	@ApiOperation(value = "erp_OrderChangeAddressRequestProc", notes = "?????? ?????????????????? ??????")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK !!"), @ApiResponse(code = 5001, message = "?????? ?????????????????? ?????? ?????? !!") })
	@GetMapping("/erp_OrderChangeAddressRequestProc")
	@RequestMapping(value = "/erp_OrderChangeAddressRequestProc", method = RequestMethod.POST)
	public String erp_OrderChangeAddressRequestProc(
			@RequestHeader(value="CertKey", required=false) String CertKey ,
			@RequestParam(name = "key_no", required = false) String key_no,
			@RequestParam(name = "chg_orm_gpost", required = false) String chg_orm_gpost,
			@RequestParam(name = "chg_bdongcd", required = false) String chg_bdongcd,
			@RequestParam(name = "chg_addr", required = false) String chg_addr,
			@RequestParam(name = "chg_addr_dtl", required = false) String chg_addr_dtl
			) {
		       
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
		int res = 0;
		BaseResponse response = new BaseResponse();
		DataResult dataResult = new DataResult();
		
		try {
			
			String res2 = "";
	        if ("58BEA9A133900619879E8FFF1ECB0F336A5672CF6B68EB56B0D229FF4C6A7D52".equals(CertKey)) {
	        	res2 = "Your CertiKey is Valid.";
	        } else {
	        	return gson.toJson("Your CertiKey is inValid."); 
	        } 	
	        
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("key_no", key_no);
			params.put("orm_gpost", chg_orm_gpost);
			params.put("chg_bdongcd", chg_bdongcd);
			params.put("chg_addr", chg_addr);
			params.put("chg_addr_dtl", chg_addr_dtl);
			
			//???????????? ?????? ?????? ??????
			//1. to_chgmas insert
			
			//2-1. ??????????????? ??????
			//		1) ?????? ?????? ?????? ??????
			//		2) TO_CHGMAS_ADDED insert
			
			//2-2. ?????????????????????
			//		1) ?????? ?????? ?????? ??????
			//		2) TO_CHGMAS_ADDED insert
			//		3) TC_RESMST ?????? ??????
			//		4) TC_PLANMST ?????? ??????
			
			//3. to_chgmas ????????? update
			
			
			//1. to_chgmas insert
        	res = cChatBotChangeMapper.insertChgAddrReq(params);
        	
			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("insertChgAddrReq ?????? [" + res + "]");
				return gson.toJson(response);
			}			
			
			//2. ????????????????????????
        	res = cChatBotChangeMapper.updateToOrdmasAddr(params);
        	
			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("updateToOrdmasAddr ?????? [" + res + "]");
				return gson.toJson(response);
			}			
			
			//3. TO_CHGMAS_ADDED insert
			
			//	3.1  ?????????????????? ?????? ????????????
			dataResult = cChatBotChangeMapper.selectToChgmasMaxSeqNo(params);
			
			String req_seq = "";
			req_seq = dataResult.getData1() ;
			
			params.put("req_seq", req_seq);					

//        	res = cChatBotChangeMapper.insertToChgmasAdded(params);
//        	
//			if (res < 0) {
//				txManager.rollback(status);
//				response.setResultCode("5001");
//				response.setResultMessage("?????????????????? ??????????????? ?????? ?????? ?????? [" + res + "]");
//				return gson.toJson(response);
//			}			
			
			//4. ??????????????? ?????? ?????? ?????? ??????
			
			dataResult = cChatBotChangeMapper.selectOrmCryn(params);
			
			String orm_cryn = "";
			orm_cryn = dataResult.getData1() ;
			
			if ("Y".equals(orm_cryn)) {
				
				//4-1. TC_RESMST ?????? ?????? ??????
				
	        	res = cChatBotChangeMapper.updateTcResmstAddr(params);
	        	
				if (res < 0) {
					txManager.rollback(status);
					response.setResultCode("5001");
					response.setResultMessage("updateTcResmstAddr ?????? [" + res + "]");
					return gson.toJson(response);
				}			
							
				//4-2. TC_PLANMST ?????? ????????????
				//TC_PLANMST ?????? ??????
				
				dataResult = cChatBotChangeMapper.selectTcplanmstYn(params);
				
				String tcplanmst_yn = "";
				tcplanmst_yn = dataResult.getData1() ;
				
				if("Y".equals(tcplanmst_yn)) {
					
		        	res = cChatBotChangeMapper.updateTcPlanmstAddr(params);
		        	
					if (res < 0) {
						txManager.rollback(status);
						response.setResultCode("5001");
						response.setResultMessage("updateTcPlanmstAddr ?????? [" + res + "]");
						return gson.toJson(response);
					}			
				
				}
			
			}
			
			//5. to_chgmas ????????? update
			
        	res = cChatBotChangeMapper.updateToChgmasChgAddrStat(params);
        	
			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("updateToChgmasChgAddrStat ?????? [" + res + "]");
				return gson.toJson(response);
			}			

			//??????????????? ?????? ?????? ????????? insert ??????
			
			dataResult = cChatBotChangeMapper.selectOnlineynCheck(params);
			
			String online_yn = "";
			String orm_nm = "";			
			String message= "";
			
			online_yn = dataResult.getData1();
			orm_nm = dataResult.getData2();
			
			message = "????????? ?????? ?????????????????? ???????????????. (???????????? : " + key_no + ",  ???????????? : " + orm_nm + " )";
					
			if(online_yn.equals("N")) {
				
				params.put("message", message);
				
				res = cChatBotChangeMapper.insertAgtChangeAlarm(params);
				
				if (res < 0) {
					txManager.rollback(status);
					response.setResultCode("5001");
					response.setResultMessage("insertAgtChangeAlarm ???????????????????????? insert ?????? [" + res + "]");
					return gson.toJson(response);
				}					
			}

			
	        params.put("call_function", "erp_OrderChangeAddressRequestProc");
	        params.put("function_name", "?????? ?????? ?????????????????? ??????");
	        response = apiCommonService.erp_callApiHistSave(params);

			
	    	JSONObject obj = new JSONObject();
	    	JSONArray jArray = new JSONArray(); //????????? ????????????
	    	JSONArray jArray2 = new JSONArray(); //????????? ????????????
	    	
	    	JSONObject api_token = new JSONObject();
	    	JSONObject sendList = new JSONObject();	    	 
	    	JSONObject sObject = new JSONObject();//?????? ?????? ????????? json
	    	JSONObject sObject2 = new JSONObject();//?????? ?????? ????????? json
	    	
	    	String templateCode = "";
	    	String senderkey = "";
	    	String title = "";
	    	String subject = "";
	    	String from_no = "";
	    	String message_type = "";
	    		        
	        //?????????????????? ?????? ?????? ????????????	    	
	    	params.put("key_no", key_no);
	    	dataResult = cChatBotChangeMapper.selectChginformData(params);
	        
	        String com_brd = "";
	        String com_tono = "";
	        String org_orm_nm = "";
	        String send_yn = "";
	        String biztalkmessage = "";
	        String chg_addrinfo = "";
	        String sales_managerhp  = "";
	        
	        com_brd = dataResult.getData1();
	        org_orm_nm = dataResult.getData2();
	        com_tono = dataResult.getData3();
	        send_yn = dataResult.getData4();
	        sales_managerhp = dataResult.getData5();
	        
			title = "(CHATBOT)???????????? AUTO????????????";
			subject = "(CHATBOT)???????????? AUTO????????????";	      
    		chg_addrinfo = chg_addr +" "+ chg_addr_dtl ;
    		
			String chg_content = "";
			chg_content = "????????????("+chg_addrinfo+")";
//	    	biztalkmessage = "(????????????) ??????????????? "+orm_nm+" ?????????, ???????????? ???????????? ?????? " + "" +pay_amt3+ "?????? ?????????????????????.\r\n\r\n" +
//					 "1) ???????????? : "+""+pay_no+"\r\n"+
//					 "2) ???????????? : "+""+rem_dt+"";s	
			
			String pattern = "yyyy-MM-dd";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

			String date = simpleDateFormat.format(new Date());
			System.out.println(date);
		        
	        if(send_yn.equals("Y")) {
	        
	        	if(com_brd.equals("T60I01")) {
        		
	        		biztalkmessage = "???????????????.\r\n" +
	        					"??? ????????? ???????????? ??????????????? ?????? ????????? ?????? ?????? ????????? ???????????? ?????? ????????????.\r\n\r\n" +
	        					"???????????? ??????\r\n"+
	        					"???????????? : "+key_no+"\r\n"+
	        					"???????????? : "+org_orm_nm+"\r\n"+
	        					"???????????? : "+chg_content+"\r\n"+
	        					"???????????? : "+date+"\r\n\r\n"+
	        					"?????? ?????? ?????? ??????????????????.\r\n"+
	        					"???????????????.\r\n"
	        					;
	        					
	        		senderkey = "dbf8669a88dd7926fd653ff3ff9b23d331fbbb4c";
	        		from_no = "1577-5670";
	        		templateCode = "iloomchatbotorderchangeinform0";
	        		
	        	}else if(com_brd.equals("T60I02")) {
	        		
	        		biztalkmessage = "???????????????.\r\n" +
        					"??? ????????? ???????????? ?????????????????? ?????? ????????? ?????? ?????? ????????? ???????????? ?????? ????????????.\r\n\r\n" +
        					"???????????? ??????\r\n"+
        					"???????????? : "+key_no+"\r\n"+
        					"???????????? : "+org_orm_nm+"\r\n"+
        					"???????????? : "+chg_content+"\r\n"+
        					"???????????? : "+date+"\r\n\r\n"+
        					"?????? ?????? ?????? ??????????????????.\r\n"+
        					"???????????????.\r\n"
        					;
		
	        		senderkey = "9917d09567d2ebf1acc89662d7f9ff10db1488d7";
	        		from_no = "1588-1662";
	        		templateCode = "deskerchatbotorderchginform0";
	        		
	        	}else if(com_brd.equals("T60I03")) {
	        		
	        		biztalkmessage = "???????????????.\r\n" +
        					"??? ????????? ???????????? ?????????????????? ?????? ????????? ?????? ?????? ????????? ???????????? ?????? ????????????.\r\n\r\n" +
        					"???????????? ??????\r\n"+
        					"???????????? : "+key_no+"\r\n"+
        					"???????????? : "+org_orm_nm+"\r\n"+
        					"???????????? : "+chg_content+"\r\n"+
        					"???????????? : "+date+"\r\n\r\n"+
        					"?????? ?????? ?????? ??????????????????.\r\n"+
        					"???????????????.\r\n"
        					;
	        		
	        		senderkey = "3ed320702f733d0b5a31e99a3ba931d9f2f9f960";
	        		from_no = "1899-8588";
	        		templateCode = "slouchatbotorderchangeinform0";	        		

	        	}else if(com_brd.equals("T60P01")) {
	        		
	        		biztalkmessage = "???????????????.\r\n" +
        					"??? ????????? ???????????? ?????????????????? ?????? ????????? ?????? ?????? ????????? ???????????? ?????? ????????????.\r\n\r\n" +
        					"???????????? ??????\r\n"+
        					"???????????? : "+key_no+"\r\n"+
        					"???????????? : "+org_orm_nm+"\r\n"+
        					"???????????? : "+chg_content+"\r\n"+
        					"???????????? : "+date+"\r\n\r\n"+
        					"?????? ?????? ?????? ??????????????????.\r\n"+
        					"???????????????.\r\n"
        					;
	        		
	        		senderkey = "6b94c758a1f689223024765ae6e2b0aede351955";
	        		from_no = "1577-5674";
	        		templateCode = "sidizchatbotorderchangeinform0";	  
	        		
	        	}else {
	        		
	        	}
	       	        	
		    	sendList.put("authKey", "D62D413F25CD43B3BD06636F2B3F570ABFB5008BD727901E341F041448D22C3A6593D58D45C68E60171F7FB2B2C345459361A08D20298BAE6A3A1B74196A95C3");
				
		       	 for (int i = 0; i < 1; i++)//??????
		       	 {
			        	 
			        	 
			        	 sObject.put("sendDiv", "BIZTALK" );
			        	 sObject.put("title", title);
			        	 sObject.put("subject", subject );		  
			        	 sObject.put("message","");
			        	 sObject.put("fromNm", org_orm_nm );
			        	 sObject.put("toNm", org_orm_nm );
			        	 sObject.put("fromNo", from_no ); 
			        	 sObject.put("toNo", com_tono);
//			        	 sObject.put("toNo", "010-3652-9837");
			        	 sObject.put("companyCd", "T01B" );		        	 
			        	 sObject.put("fstUsr", "CHATBOT" );
			        	 sObject.put("systemNm", "CHATBOT" );
			        	 sObject.put("sendType", "SMTP" );
			        	 sObject.put("reserveDiv","I");
			        	 sObject.put("reserveDt", "" );
			        	 sObject.put("keyNo", key_no);
			        	 sObject.put("msgType", "TI4N" );		        	 
			        	 sObject.put("senderKey", senderkey);
			        	 sObject.put("templateCode", templateCode );
			        	 sObject.put("bizTalkMessage", biztalkmessage );
			        	 sObject.put("comBrd", com_brd );
			        	 
			        	 jArray.add(sObject);
		       	 }        	 
	        		        			
	        	sendList.put("list" ,jArray);  
	        	
	        	
	        	BaseResponse kakao_res = RestCall("https://msg-api.fursys.com/v1/api/message/SendMsg",sendList);	
	        	if (!"200".equals(kakao_res.getResultCode())) {
					response.setResultCode("5001");
					response.setResultMessage("????????????????????? ??????  [" + kakao_res.getResultMessage() + "]");
					return gson.toJson(response);
				}
				
	        	if(com_brd.equals("T60I01")) {
					
			       	 for (int i = 0; i < 1; i++)//??????
			       	 {
				        	 
				        	 
				        	 sObject2.put("sendDiv", "BIZTALK" );
				        	 sObject2.put("title", title);
				        	 sObject2.put("subject", subject );		  
				        	 sObject2.put("message","");
				        	 sObject2.put("fromNm", org_orm_nm );
				        	 sObject2.put("toNm", org_orm_nm );
				        	 sObject2.put("fromNo", from_no ); 
				        	 sObject2.put("toNo", sales_managerhp);
//				        	 sObject2.put("toNo", "010-3652-9837");
				        	 sObject2.put("companyCd", "T01B" );		        	 
				        	 sObject2.put("fstUsr", "CHATBOT" );
				        	 sObject2.put("systemNm", "CHATBOT" );
				        	 sObject2.put("sendType", "SMTP" );
				        	 sObject2.put("reserveDiv","I");
				        	 sObject2.put("reserveDt", "" );
				        	 sObject2.put("keyNo", key_no);
				        	 sObject2.put("msgType", "TI4N" );		        	 
				        	 sObject2.put("senderKey", senderkey);
				        	 sObject2.put("templateCode", templateCode );
				        	 sObject2.put("bizTalkMessage", biztalkmessage );
				        	 sObject2.put("comBrd", com_brd );
				        	 
				        	 jArray2.add(sObject2);
			       	 }        	 
		        		        			
		        	sendList.put("list" ,jArray2);  
		        	 
		        	BaseResponse kakao_res2 = RestCall("https://msg-api.fursys.com/v1/api/message/SendMsg",sendList);	
		        	
		        	if (!"200".equals(kakao_res2.getResultCode())) {
						response.setResultCode("5001");
						response.setResultMessage("????????????????????? ??????  [" + kakao_res2.getResultMessage() + "]");
						return gson.toJson(response);
					}
	        	}
	 	        	
	        }	        
  
		} catch (Exception e) {
			txManager.rollback(status);
			System.out.println(e.toString());			
			response.setResultCode("5001");
			response.setResultMessage(e.toString());
			return gson.toJson(response);
		}
		
		txManager.commit(status);
		response.setResultCode("200");
		System.out.println(response.toString());	
		return gson.toJson(response);
	}
	
	@ApiOperation(value = "erp_selectVndTel", notes = "????????? ???????????? ??????")
	@ApiResponses({ @ApiResponse(code = 200, message = "????????? ???????????? ?????? ?????? !!"), @ApiResponse(code = 5001, message = "????????? ???????????? ?????? ?????? !!") })
	@GetMapping("/erp_selectVndTel")
	@RequestMapping(value = "/erp_selectVndTel", method = RequestMethod.GET)
	public String erp_selectVndTel(
			@RequestHeader(value="CertKey", required=false) String CertKey ,
			@RequestParam(name = "key_no", required = false) String key_no
			) {
		       
		BaseResponse response = new BaseResponse();
		DataResult dataResult = new DataResult();
		
		ERPSameOrderYn item = null;
				
		try {
			
			String res2 = "";
	        if ("58BEA9A133900619879E8FFF1ECB0F336A5672CF6B68EB56B0D229FF4C6A7D52".equals(CertKey)) {
	        	res2 = "Your CertiKey is Valid.";
	        } else {
	        	return gson.toJson("Your CertiKey is inValid."); 
	        } 	
	        
			HashMap<String,Object> params = new HashMap<String, Object>();
			params.put("key_no",key_no);
			
			//????????? or ???????????? ?????? ??????
		
			dataResult = cChatBotChangeMapper.selectVndTelNo(params);

	        params.put("call_function", "erp_selectVndTel");
	        params.put("function_name", "?????? ????????? ???????????? ??????");
	        response = apiCommonService.erp_callApiHistSave(params);
	        
		} catch (Exception e) {
			
			response.setResultCode("5001");
			response.setResultMessage(e.toString());
			return gson.toJson(dataResult);
		}
		
		response.setResultCode("200");
		return gson.toJson(dataResult);
	}	

	@ApiOperation(value = "erp_AsChangeAddressRequestProc", notes = "AS ?????????????????? ??????")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK !!"), @ApiResponse(code = 5001, message = "AS ?????????????????? ?????? ?????? !!") })
	@GetMapping("/erp_AsChangeAddressRequestProc")
	@RequestMapping(value = "/erp_AsChangeAddressRequestProc", method = RequestMethod.POST)
	public String erp_AsChangeAddressRequestProc(
			@RequestHeader(value="CertKey", required=false) String CertKey ,
			@RequestParam(name = "key_no", required = false) String key_no,
			@RequestParam(name = "chg_orm_gpost", required = false) String chg_orm_gpost,
			@RequestParam(name = "chg_bdongcd", required = false) String chg_bdongcd,
			@RequestParam(name = "chg_addr", required = false) String chg_addr,
			@RequestParam(name = "chg_addr_dtl", required = false) String chg_addr_dtl
			) {
		       
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
		int res = 0;
		BaseResponse response = new BaseResponse();
		DataResult dataResult = new DataResult();
		
		try {
			
			String res2 = "";
	        if ("58BEA9A133900619879E8FFF1ECB0F336A5672CF6B68EB56B0D229FF4C6A7D52".equals(CertKey)) {
	        	res2 = "Your CertiKey is Valid.";
	        } else {
	        	return gson.toJson("Your CertiKey is inValid."); 
	        } 	
	        
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("key_no", key_no);
			params.put("chg_orm_gpost", chg_orm_gpost);
			params.put("chg_bdongcd", chg_bdongcd);
			params.put("chg_addr", chg_addr);
			params.put("chg_addr_dtl", chg_addr_dtl);
			
			
			res = cChatBotChangeMapper.updateAsTarptreqChgAddr(params);
			
			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("updateAsTarptreqChgAddr ?????? [" + res + "]");
				return gson.toJson(response);
			}	
			
			res = cChatBotChangeMapper.updateAsTcresmstChgAddr(params);

			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("updateAsTcresmstChgAddr ?????? [" + res + "]");
				return gson.toJson(response);
			}	
						
			dataResult = cChatBotChangeMapper.selectTaplanmstYn(params);
			
			String taplanmst_yn = "";
			taplanmst_yn = dataResult.getData1() ;
			
			if("Y".equals(taplanmst_yn)) {
				
				res = cChatBotChangeMapper.updateAsTaplanmstChgAddr(params);

				if (res < 0) {
					txManager.rollback(status);
					response.setResultCode("5001");
					response.setResultMessage("updateAsTaplanmstChgAddr ?????? [" + res + "]");
					return gson.toJson(response);
				}	
			
			}
			
			//AUTO???????????? ?????? ????????? insert
			params.put("chgreq_hp", "");
			params.put("chgreq_dt", "");
			params.put("chgreq_addr", chg_addr+ " " + chg_addr_dtl);
			params.put("reqorm_no", key_no);
			params.put("chgreq_std", "A96006");
			
			res = cChatBotChangeMapper.insertAsAutoChgReqInformation(params);
			
			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("insertAsAutoChgReqInformation ?????? [" + res + "]");
				return gson.toJson(response);
			}			
			
	        params.put("call_function", "erp_AsChangeAddressRequestProc");
	        params.put("function_name", "?????? AS ???????????? ??????");
	        response = apiCommonService.erp_callApiHistSave(params);			
			
			
	
		} catch (Exception e) {
			txManager.rollback(status);
			System.out.println(e.toString());			
			response.setResultCode("5001");
			response.setResultMessage(e.toString());
			return gson.toJson(response);
		}
		
		txManager.commit(status);
		response.setResultCode("200");
		System.out.println(response.toString());	
		return gson.toJson(response);
	}

	
	@ApiOperation(value = "erp_asCdtChangeAvailableYn", notes = "AS ???????????? ???????????? ??????")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK !!"), @ApiResponse(code = 5001, message = "AS ???????????? ???????????? ?????? ?????? !!") })
	@GetMapping("/erp_asCdtChangeAvailableYn")
	@RequestMapping(value = "/erp_asCdtChangeAvailableYn", method = RequestMethod.GET)
	public String erp_asCdtChangeAvailableYn(
			@RequestHeader(value="CertKey", required=false) String CertKey ,
			@RequestParam(value="key_no", required=false) String key_no ,
			@RequestParam(value="req_dt", required=false) String req_dt ,
			@RequestParam(value="com_agsec", required=false) String com_agsec ,
			@RequestParam(value="com_brand", required=false) String com_brand 
			) {
		       
//		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
		int res = 0;
		BaseResponse response = new BaseResponse();
		DataResult dataResult = new DataResult();
		ERPOrmAvailableYn item = null;
		
		try {

			String res2 = "";
	        if ("58BEA9A133900619879E8FFF1ECB0F336A5672CF6B68EB56B0D229FF4C6A7D52".equals(CertKey)) {
	        	res2 = "Your CertiKey is Valid.";
	        } else {
	        	return gson.toJson("Your CertiKey is inValid."); 
	        } 	
	        
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("req_dt", req_dt);			
			params.put("com_agsec", com_agsec);	
			params.put("com_brand", com_brand);
			params.put("key_no", key_no);
			
			dataResult = cChatBotChangeMapper.selectAsInfo(params);
			
			String sti_cd = "";
			String com_scd = "";
			String rpt_enddt = "";
			
			sti_cd = dataResult.getData1();
			com_scd = dataResult.getData2();
			rpt_enddt = dataResult.getData3();
			
			params.put("sti_cd", sti_cd);
			params.put("com_scd", com_scd);			
			
            //???????????? ????????? AS ?????????????????? ??????
			
			
			item = cChatBotChangeMapper.selectAsCdtChangeYn(params);
			//selectBdongCd
			
			if ( Integer.parseInt(req_dt) <= Integer.parseInt(rpt_enddt)) {
				item =  cChatBotChangeMapper.selectAsCdtChangeYn2(params);
			}
			
			if (item == null) {
//				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("selectAsCdtChangeYn ??????");
				return gson.toJson(item);
			}			
			
	        params.put("call_function", "erp_asCdtChangeAvailableYn");
	        params.put("function_name", "?????? AS ???????????? ???????????? ??????");
	        response = apiCommonService.erp_callApiHistSave(params);	
	        
		} catch (Exception e) {
			
			response.setResultCode("5001");
			response.setResultMessage(e.toString());
			return gson.toJson(item);
		}
		
		response.setResultCode("200");
		return gson.toJson(item);
	}	
	
	@ApiOperation(value = "erp_asCdtAvailableList", notes = "AS ?????? ?????? ???????????? ????????? ??????")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK !!"), @ApiResponse(code = 5001, message = "AS ?????? ?????? ???????????? ????????? ?????? ?????? !!") })
	@RequestMapping(value="/erp_asCdtAvailableList",method=RequestMethod.GET)
	public String erp_asCdtAvailableList(
			@RequestHeader(value="CertKey", required=false) String CertKey ,
			@RequestParam(value="key_no", required=false) String key_no ,
			@RequestParam(value="req_dt", required=false) String req_dt ,
			@RequestParam(value="com_agsec", required=false) String com_agsec ,
			@RequestParam(value="com_brand", required=false) String com_brand 
		) { 
		
		DataResult dataResult = new DataResult();
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("req_dt", req_dt);			
		params.put("com_agsec", com_agsec);	
		params.put("com_brand", com_brand);
		params.put("key_no", key_no);
		
		dataResult = cChatBotChangeMapper.selectAsInfo(params);
		
		String sti_cd = "";
		String com_scd = "";
		
		sti_cd = dataResult.getData1();
		com_scd = dataResult.getData2();
		
		params.put("sti_cd", sti_cd);
		params.put("com_scd", com_scd);			

		ArrayList<ERPOrderChangeAvailableDt> arList = cChatBotChangeMapper.selectAsAvailableCdtList(params);
		
        BaseResponse response = new BaseResponse();		
        params.put("call_function", "erp_asCdtAvailableList");
        params.put("function_name", "?????? AS ?????? ?????? ???????????? ????????? ??????");
        response = apiCommonService.erp_callApiHistSave(params);	
        		
		//		ArrayList<ERPOrderSearchVo> arList = cChatBotMapper.selectOrderSearchList(params);
		return gson.toJson(arList); 
		

	}
	
	@ApiOperation(value = "erp_orderPreviousChangeYn", notes = "??? ??????????????? ??????")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK !!"), @ApiResponse(code = 5001, message = "??? ??????????????? ?????? ?????? !!") })
	@GetMapping("/erp_orderPreviousChangeYn")
	@RequestMapping(value = "/erp_orderPreviousChangeYn", method = RequestMethod.GET)
	public String erp_orderPreviousChangeYn(
			@RequestHeader(value="CertKey", required=false) String CertKey ,
			@RequestParam(value="key_no", required=false) String key_no
			) {
		       
//		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
		int res = 0;
		BaseResponse response = new BaseResponse();
		DataResult dataResult = new DataResult();
		String item = null;
		
		try {
			
			String res2 = "";
	        if ("58BEA9A133900619879E8FFF1ECB0F336A5672CF6B68EB56B0D229FF4C6A7D52".equals(CertKey)) {
	        	res2 = "Your CertiKey is Valid.";
	        } else {
	        	return gson.toJson("Your CertiKey is inValid."); 
	        } 	
	        
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("key_no", key_no);	
			
			dataResult = cChatBotChangeMapper.selectChgstatusYn(params);

			String req_status = "";
			req_status = dataResult.getData1();
			item = req_status;
			
	        params.put("call_function", "erp_orderPreviousChangeYn");
	        params.put("function_name", "?????? ??? ??????????????? ??????");
	        response = apiCommonService.erp_callApiHistSave(params);			
	        
		} catch (Exception e) {
			
			response.setResultCode("5001");
			response.setResultMessage(e.toString());
			return gson.toJson(item);
		}
		
		response.setResultCode("200");
		return gson.toJson(item);
	}	
	
	@ApiOperation(value = "erp_vendorTelInfo", notes = "????????? ???????????? ??????")
	@ApiResponses({ @ApiResponse(code = 200, message = "????????? ???????????? ?????? ?????? !!"), @ApiResponse(code = 5001, message = "????????? ???????????? ?????? ?????? !!") })
	@GetMapping("/erp_vendorTelInfo")
	@RequestMapping(value = "/erp_vendorTelInfo", method = RequestMethod.GET)
	public String erp_vendorTelInfo(
			@RequestHeader(value="CertKey", required=false) String CertKey ,
			@RequestParam(name = "agt_cd", required = false) String agt_cd
			) {
		       
		BaseResponse response = new BaseResponse();
		DataResult dataResult = new DataResult();
		
		ERPVendorTelNoVo item = null;	
		try {
			
			String res2 = "";
	        if ("58BEA9A133900619879E8FFF1ECB0F336A5672CF6B68EB56B0D229FF4C6A7D52".equals(CertKey)) {
	        	res2 = "Your CertiKey is Valid.";
	        } else {
	        	return gson.toJson("Your CertiKey is inValid."); 
	        } 	
	        
			//ERPVendorTelNoVo
			HashMap<String,Object> params = new HashMap<String, Object>();
			params.put("agt_cd",agt_cd);
			
//			item = cChatBotChangeMapper.executeProRenAutoBlock(params);
			item = cChatBotMapper.selectVendorTelNo(params); 
			
	        params.put("call_function", "erp_vendorTelInfo");
	        params.put("function_name", "?????? ????????? ???????????? ??????");
	        response = apiCommonService.erp_callApiHistSave(params);	
	        
		} catch (Exception e) {
			
			response.setResultCode("5001");
			response.setResultMessage(e.toString());
			return gson.toJson(item);
		}
		
		response.setResultCode("200");
		return gson.toJson(item);
	}	


	@ApiOperation(value = "erp_orderCancelRequest", notes = "???????????? ??????")
	@ApiResponses({ @ApiResponse(code = 200, message = "???????????? ?????? ?????? !!"), @ApiResponse(code = 5001, message = "???????????? ?????? ?????? ?????? !!") })
	@GetMapping("/erp_orderCancelRequest")
	@RequestMapping(value = "/erp_orderCancelRequest", method = RequestMethod.GET)
	public String erp_orderCancelRequest(
			@RequestHeader(value="CertKey", required=false) String CertKey ,
			@RequestParam(name = "key_no", required = false) String key_no
			) {
		       
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
		int res = 0;
		BaseResponse response = new BaseResponse();
		DataResult dataResult = new DataResult();
		
		try {
			
			String res2 = "";
	        if ("58BEA9A133900619879E8FFF1ECB0F336A5672CF6B68EB56B0D229FF4C6A7D52".equals(CertKey)) {
	        	res2 = "Your CertiKey is Valid.";
	        } else {
	        	return gson.toJson("Your CertiKey is inValid."); 
	        } 	
	        
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("key_no", key_no);
			
			//?????? ?????? ?????? ????????????
			dataResult = cChatBotChangeMapper.selectOriginOrderInfoSelect(params);
			
			String vnd_cd = "";
			String cmp_cd = "";
			String agt_cd = "";
			String orm_cdt = "";
			String orm_nm = "";
			String orm_gpost = "";
			String orm_gaddr = "";
			String bdong = "";
			
			vnd_cd = dataResult.getData1() ;
			cmp_cd = dataResult.getData2() ;
			agt_cd = dataResult.getData3() ;
			orm_cdt = dataResult.getData4() ;
			orm_nm = dataResult.getData5() ;
			orm_gpost = dataResult.getData6() ;
			orm_gaddr = dataResult.getData7() ;
			bdong = dataResult.getData8();
			
			params.put("vnd_cd", vnd_cd);
			params.put("cmp_cd", cmp_cd);
			params.put("agt_cd", agt_cd);
			params.put("orm_cdt", orm_cdt);
			params.put("orm_nm", orm_nm);
			params.put("orm_gpost", orm_gpost);
			params.put("orm_gaddr", orm_gaddr);
			params.put("bdong", bdong);
			
			//???????????? ???????????? 
			
			dataResult = cChatBotChangeMapper.selectChgReqSeq(params);
			
			String req_seq = "";
			req_seq = dataResult.getData1();
			
			params.put("req_seq", req_seq);
			
			res = cChatBotChangeMapper.insertOrderCancelMasterInsert(params);
			
			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("insertOrderCancelMasterInsert ?????? [" + res + "]");
				return gson.toJson(response);
			}	
			
			res = cChatBotChangeMapper.insertOrderCancelDetailInsert(params);
			
			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("insertOrderCancelDetailInsert ?????? [" + res + "]");
				return gson.toJson(response);
			}	
			
	        params.put("call_function", "erp_orderCancelRequest");
	        params.put("function_name", "?????? ???????????? ??????");
	        response = apiCommonService.erp_callApiHistSave(params);	
	        
		} catch (Exception e) {
			txManager.rollback(status);
			System.out.println(e.toString());			
			response.setResultCode("5001");
			response.setResultMessage(e.toString());
			return gson.toJson(response);
		}
		
		txManager.commit(status);
		response.setResultCode("200");
		System.out.println(response.toString());	
		return gson.toJson(response);
	}	
	
	
	@ApiOperation(value = "erp_orderCancelRequestProc", notes = "?????? ?????? ?????? ??????")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK !!"), @ApiResponse(code = 5001, message = "?????? ?????? ?????? ?????? ?????? ?????? !!") })
	@GetMapping("/erp_orderCancelRequestProc")
	@RequestMapping(value = "/erp_orderCancelRequestProc", method = RequestMethod.GET)
	public String erp_orderCancelRequestProc(
			@RequestHeader(value="CertKey", required=false) String CertKey ,
			@RequestParam(name = "key_no", required = false) String key_no
			) {
		       
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
		int res = 0;
		BaseResponse response = new BaseResponse();
		DataResult dataResult = new DataResult();
		
		try {
			
			String res2 = "";
	        if ("58BEA9A133900619879E8FFF1ECB0F336A5672CF6B68EB56B0D229FF4C6A7D52".equals(CertKey)) {
	        	res2 = "Your CertiKey is Valid.";
	        } else {
	        	return gson.toJson("Your CertiKey is inValid."); 
	        } 	
	        
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("key_no", key_no);
			
			//??????????????? ??????
			
        	res = cChatBotChangeMapper.updateToOrdmasCancelStatus(params);
        	
			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("updateToOrdmasCancelStatus ?????? [" + res + "]");
				return gson.toJson(response);
			}			
			
			
			//???????????? ?????? ?????? ?????? ?????? (tc_resmst, tc_resdtl, tc_planmst, tc_plandtl)

			dataResult = cChatBotChangeMapper.selectSigongReserveYn(params);
			
			String orm_cryn = "";
			
			orm_cryn = dataResult.getData1() ;
			
			if ("Y".equals(orm_cryn)) {			
			
				dataResult = cChatBotChangeMapper.selectSigongReserveData(params);
				
				String rem_dt = "";
				String rem_seq = "";
				String plm_no = "";
				String com_rfg = "";
				
				
				rem_dt  = dataResult.getData1() ;
				rem_seq = dataResult.getData2() ;
				plm_no  = dataResult.getData3() ;
				com_rfg = dataResult.getData4() ;
				
				
				params.put("rem_dt", rem_dt);
				params.put("rem_seq", rem_seq);

				//tc_resdtl ??????
				
				res = cChatBotChangeMapper.deleteTcresdtl(params);
				
				if (res < 0) {
					txManager.rollback(status);
					response.setResultCode("5001");
					response.setResultMessage("deleteTcres ?????? [" + res + "]");
					return gson.toJson(response);
				}	
				
				//tc_resmst ??????
				res = cChatBotChangeMapper.deleteTcresmst(params);
				
				if (res < 0) {
					txManager.rollback(status);
					response.setResultCode("5001");
					response.setResultMessage("deleteTcresmst ?????? [" + res + "]");
					return gson.toJson(response);
				}				
								

				if("XXXX".equals(plm_no)) {
									
					
				} else {
					
					params.put("plm_no", plm_no);
					//tc_plandtl ??????
					res = cChatBotChangeMapper.deleteTcplandtl(params);
					
					if (res < 0) {
						txManager.rollback(status);
						response.setResultCode("5001");
						response.setResultMessage("deleteTcplandtl ?????? [" + res + "]");
						return gson.toJson(response);
					}	
							
					//tc_planmst ??????
					
					res = cChatBotChangeMapper.deleteTcplanmst(params);
					
					if (res < 0) {
						txManager.rollback(status);
						response.setResultCode("5001");
						response.setResultMessage("deleteTcplanmst ?????? [" + res + "]");
						return gson.toJson(response);
					}						
					
				}
					
			}
			
			//?????????????????? ?????? ?????? ??????
			
        	res = cChatBotChangeMapper.updateToChgmasCancelStatus(params);
        	
			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("updateToChgmasCancelStatus ?????? [" + res + "]");
				return gson.toJson(response);
			}			
			
	        params.put("call_function", "erp_orderCancelRequestProc");
	        params.put("function_name", "?????? ???????????? ?????? ??????");
	        response = apiCommonService.erp_callApiHistSave(params);				

			
		} catch (Exception e) {
			txManager.rollback(status);
			System.out.println(e.toString());			
			response.setResultCode("5001");
			response.setResultMessage(e.toString());
			return gson.toJson(response);
		}
		
		txManager.commit(status);
		response.setResultCode("200");
		System.out.println(response.toString());	
		return gson.toJson(response);
	}	

	@ApiOperation(value = "erp_orderasChangeRequest", notes = "??????/AS ?????? ??????(?????? ??? ????????????)")
	@ApiResponses({ @ApiResponse(code = 200, message = "??????/AS ?????? ?????? ?????? !!"), @ApiResponse(code = 5001, message = "??????/AS ?????? ?????? ?????? !!") })
	@GetMapping("/erp_orderasChangeRequest")
	@RequestMapping(value = "/erp_orderasChangeRequest", method = RequestMethod.POST)
	public String erp_orderasChangeRequest(
			@RequestHeader(value="CertKey", required=false) String CertKey ,
			@RequestParam(name = "vnd_cd", required = false) String vnd_cd,
			@RequestParam(name = "com_brand", required = false) String com_brand,
			@RequestParam(name = "order_type", required = false) String order_type,
			@RequestParam(name = "chgreq_std", required = false) String chgreq_std,
			@RequestParam(name = "chgreq_dt", required = false) String chgreq_dt,
			@RequestParam(name = "chgreq_addr", required = false) String chgreq_addr,
			@RequestParam(name = "chgreq_hp", required = false) String chgreq_hp,
			@RequestParam(name = "reqorm_no", required = false) String reqorm_no
			) {
		       
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
		int res = 0;
		BaseResponse response = new BaseResponse();
		
		try {
			
			String res2 = "";
	        if ("58BEA9A133900619879E8FFF1ECB0F336A5672CF6B68EB56B0D229FF4C6A7D52".equals(CertKey)) {
	        	res2 = "Your CertiKey is Valid.";
	        } else {
	        	return gson.toJson("Your CertiKey is inValid."); 
	        } 	
	        
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("vnd_cd", vnd_cd);
			params.put("com_brand", com_brand);
			params.put("order_type", order_type);
			params.put("chgreq_std", chgreq_std);
			params.put("chgreq_dt", chgreq_dt);
			params.put("chgreq_addr", chgreq_addr);
			params.put("chgreq_hp", chgreq_hp);
			params.put("reqorm_no", reqorm_no);
			
			res = cChatBotChangeMapper.insertOrderAsChgReqInformation(params);
			
			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("insertOrderAsChgReqInformation ?????? [" + res + "]");
				return gson.toJson(response);
			}	
			
	        params.put("call_function", "erp_orderasChangeRequest");
	        params.put("function_name", "?????? ??????/AS ?????? ??????(?????? ??? ????????????)");
	        response = apiCommonService.erp_callApiHistSave(params);		
	        
		} catch (Exception e) {
			txManager.rollback(status);
			System.out.println(e.toString());			
			response.setResultCode("5001");
			response.setResultMessage(e.toString());
			return gson.toJson(response);
		}
		
		txManager.commit(status);
		response.setResultCode("200");
		System.out.println(response.toString());	
		return gson.toJson(response);
	}	

	
	@ApiOperation(value = "erp_chgaddrAvailableCheck", notes = "??????????????????????????????")
	@ApiResponses({ @ApiResponse(code = 200, message = "?????????????????????????????? ?????? !!"), @ApiResponse(code = 5001, message = "?????????????????????????????? ?????? !!") })
	@GetMapping("/erp_chgaddrAvailableCheck")
	@RequestMapping(value = "/erp_chgaddrAvailableCheck", method = RequestMethod.GET)
	public String erp_chgaddrAvailableCheck(
			@RequestHeader(value="CertKey", required=false) String CertKey ,
			@RequestParam(name = "order_type", required = false) String order_type,
			@RequestParam(name = "org_bdongcd", required = false) String org_bdongcd,
			@RequestParam(name = "chg_bdongcd", required = false) String chg_bdongcd,
			@RequestParam(name = "com_brand", required = false) String com_brand,
			@RequestParam(name = "key_no", required = false) String key_no
			) {
		       
		BaseResponse response = new BaseResponse();
		DataResult dataResult = new DataResult();
		
		ERPSameOrderYn item = null;
		ERPAvailableChgAddrStat item2 = null;
		String final_chgaddryn = "";
		
		try {
			
			String res2 = "";
	        if ("58BEA9A133900619879E8FFF1ECB0F336A5672CF6B68EB56B0D229FF4C6A7D52".equals(CertKey)) {
	        	res2 = "Your CertiKey is Valid.";
	        } else {
	        	return gson.toJson("Your CertiKey is inValid."); 
	        } 	
	        
			String chgcomscd_yn = "";
			
			
			if(order_type.equals("ORDER")) {
				
				//1. ????????? ?????? 
				//		????????? ???????????? ??????
				HashMap<String,Object> params = new HashMap<String, Object>();
				params.put("key_no",key_no);
				
				dataResult = cChatBotChangeMapper.selectOrgOrderInfo(params);
				
				//?????? ?????? ??????
				String orm_cryn = "";
				String org_comscd = "";
				
				orm_cryn = dataResult.getData1();
				org_comscd = dataResult.getData2();
				
				//???????????? ???????????? ??????
				String str_jejubdongcd = "";
				String org_jejubdongcd = "";
				String jeju_checkyn = "N";
				
				
				
				String str_ulleungbdongcd = "";
				String org_ulleungbdongcd = "";
				String ulleung_checkyn = "N";
				
				//???????????????
				if(orm_cryn.equals("Y")) {
					
					//?????? ????????????
					params.put("chg_bdongcd",chg_bdongcd);
					dataResult = cChatBotChangeMapper.selectChgOrderInfo(params);
					
					String chg_comscd = "";
					
					chg_comscd = dataResult.getData1();
					
					if(org_comscd.equals(chg_comscd)) {
						chgcomscd_yn = "N";
					}else {
						chgcomscd_yn = "Y";
					}
					
					if(com_brand.equals("T60I01") || com_brand.equals("T60I02") || com_brand.equals("T60I03")) {
						
						str_jejubdongcd = chg_bdongcd.substring(0,2);
						org_jejubdongcd = org_bdongcd.substring(0,2);
						
						if(str_jejubdongcd.equals(org_jejubdongcd)) {
							
							jeju_checkyn = "N";
							
						} else {
							
							if(str_jejubdongcd.equals("50")) {
								jeju_checkyn = "Y";
							}						
						}
						
						if(com_brand.equals("T60I03")) {
							
							str_ulleungbdongcd = chg_bdongcd.substring(0,4);
							org_ulleungbdongcd = org_bdongcd.substring(0,4);
							
							if(str_ulleungbdongcd.equals(org_ulleungbdongcd) ) {
								ulleung_checkyn = "N";
										
							} else {
								
								ulleung_checkyn = "Y";
							}

						}					
					}

					if(chgcomscd_yn.equals("N") && jeju_checkyn.equals("N")) {						
						final_chgaddryn = "Y";						
					} else {
						final_chgaddryn = "N";
					}				
					
					System.out.println("1111111");
			        params.put("call_function", "erp_chgaddrAvailableCheck");
			        params.put("function_name", "?????? ??????????????????????????????");
			        response = apiCommonService.erp_callApiHistSave(params);			
			        					
				//???????????????	
				} 
				else {
					
	
					if(com_brand.equals("T60I01") || com_brand.equals("T60I02") || com_brand.equals("T60I03")) {
						
						str_jejubdongcd = chg_bdongcd.substring(0,2);
						org_jejubdongcd = org_bdongcd.substring(0,2);
						
						if(str_jejubdongcd.equals(org_jejubdongcd)) {
							
							jeju_checkyn = "N";
							
						} else {
							
							if(str_jejubdongcd.equals("50")) {
								jeju_checkyn = "Y";
							}						
						}
						
						if(com_brand.equals("T60I03")) {
							
							str_ulleungbdongcd = chg_bdongcd.substring(0,4);
							org_ulleungbdongcd = org_bdongcd.substring(0,4);
							
							if(str_ulleungbdongcd.equals(org_ulleungbdongcd)) {
								ulleung_checkyn = "N";
										
							} else {
								
								ulleung_checkyn = "Y";
							}

						}					
					}

					if(jeju_checkyn.equals("N")) {						
						final_chgaddryn = "Y";						
					} else {
						final_chgaddryn = "N";
					}				

				}
				System.out.println("2222222");
		        params.put("call_function", "erp_chgaddrAvailableCheck");
		        params.put("function_name", "?????? ??????????????????????????????");
		        response = apiCommonService.erp_callApiHistSave(params);			
		        
			//AS?????????	
			}
			
			else {
				
				HashMap<String,Object> params = new HashMap<String, Object>();
				params.put("key_no",key_no);
				
				dataResult = cChatBotChangeMapper.selectOrgAsInfo(params);
				
				String as_org_comscd = "";
				
				as_org_comscd = dataResult.getData1();
				
				params.put("chg_bdongcd",chg_bdongcd);
				dataResult = cChatBotChangeMapper.selectChgOrderInfo(params);
				
				String as_chg_comscd = "";
				
				as_chg_comscd = dataResult.getData1();
				
				if(as_org_comscd.equals(as_chg_comscd)) {
					final_chgaddryn = "Y";
				}else {
					final_chgaddryn = "N";
				}
				
				System.out.println("33333333");
		        params.put("call_function", "erp_chgaddrAvailableCheck");
		        params.put("function_name", "?????? ??????????????????????????????");
		        response = apiCommonService.erp_callApiHistSave(params);			
		        
			}
			
	
	        
		} catch (Exception e) {
			
			response.setResultCode("5001");
			response.setResultMessage(e.toString());
			return gson.toJson(final_chgaddryn);
		}
		
		response.setResultCode("200");
		return gson.toJson(final_chgaddryn);
	}	
	
	
	@ApiOperation(value = "erp_addRequestAvailableCheck", notes = "????????????????????????(?????????,???????????????)")
	@ApiResponses({ @ApiResponse(code = 200, message = "????????????????????????(?????????,???????????????) ?????? !!"), @ApiResponse(code = 5001, message = "????????????????????????(?????????,???????????????) ?????? !!") })
	@GetMapping("/erp_addRequestAvailableCheck")
	@RequestMapping(value = "/erp_addRequestAvailableCheck", method = RequestMethod.GET)
	public String erp_addRequestAvailableCheck(
			@RequestHeader(value="CertKey", required=false) String CertKey ,
			@RequestParam(name = "chgreq_std", required = false) String chgreq_std,
			@RequestParam(name = "order_type", required = false) String order_type,
			@RequestParam(name = "vnd_cd", required = false) String vnd_cd,
			@RequestParam(name = "com_brand", required = false) String com_brand,
			@RequestParam(name = "key_no", required = false) String key_no
			) {
		       
		BaseResponse response = new BaseResponse();
		DataResult dataResult = new DataResult();
		
		String addreq_yn = "";
		try {

			String res2 = "";
	        if ("58BEA9A133900619879E8FFF1ECB0F336A5672CF6B68EB56B0D229FF4C6A7D52".equals(CertKey)) {
	        	res2 = "Your CertiKey is Valid.";
	        } else {
	        	return gson.toJson("Your CertiKey is inValid."); 
	        } 	
	        
			HashMap<String,Object> params = new HashMap<String, Object>();
			params.put("chgreq_std",chgreq_std);
			params.put("order_type",order_type);
			params.put("vnd_cd",vnd_cd);
			params.put("com_brand",com_brand);
			params.put("key_no",key_no);
			
			dataResult = cChatBotChangeMapper.selectPreviousChgreqYn(params);
				
			addreq_yn = dataResult.getData1();
			
	        params.put("call_function", "erp_addRequestAvailableCheck");
	        params.put("function_name", "?????? ????????????????????????(?????????,???????????????)");
	        response = apiCommonService.erp_callApiHistSave(params);			
	        			

		} catch (Exception e) {
			
			response.setResultCode("5001");
			response.setResultMessage(e.toString());
			return gson.toJson(addreq_yn);
		}
		
		response.setResultCode("200");
		return gson.toJson(addreq_yn);
	}	
		
	
    private BaseResponse RestCall(String paramUrl,JSONObject jsonObject){
    	BaseResponse res = new BaseResponse();
    	try {
            URL url = new URL(paramUrl);
           
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
           // conn.setRequestProperty("X-Auth-Token", API_KEY);            
            conn.setRequestProperty("X-Data-Type", "application/json");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream(),"UTF-8");
            osw.write(jsonObject.toString());
            osw.flush();
            osw.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
            if (conn.getResponseCode() != 200) {
                System.out.println("Failed: HTTP error code : " + conn.getResponseCode());
            	//throw new RuntimeException("Failed: HTTP error code : " + conn.getResponseCode());
            	res.setResultCode("5001");
            	res.setResultMessage("Failed: HTTP error code : " + conn.getResponseCode());
            } else {
                System.out.println("?????? ??????");
                res.setResultCode("200");
            	res.setResultMessage("");
            }
            
            String line = null;
            while((line = br.readLine()) != null){
                System.out.println(line);
            }            
            br.close();            
            conn.disconnect();
            
        } catch (IOException e) {        	
//            System.out.println("RestCall Fail : " + e.getMessage());
//            res.setResultCode("5001");
//        	res.setResultMessage("RestCall Fail : " + e.getMessage());
        	
            System.out.println("?????? ??????");
            res.setResultCode("200");
            res.setResultMessage("");
        	
        	return res;
        }
    	
    	return res;
    }	
	
	
	@ApiOperation(value = "erp_arschatbotinformkakaosend", notes = "?????? ??????????????? ?????? ????????? ??????")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK !!"), @ApiResponse(code = 5001, message = "?????? ??????????????? ?????? ????????? ?????? ?????? !!") })
	@GetMapping("/erp_arschatbotinformkakaosend")
	@RequestMapping(value = "/erp_arschatbotinformkakaosend", method = RequestMethod.GET)
	public String erp_arschatbotinformkakaosend(
			@RequestHeader(value="CertKey", required=false) String CertKey ,
			@RequestParam(name = "com_brd", required = false) String com_brd,
			@RequestParam(name = "ctm_hp", required = false) String ctm_hp
			) {
		       
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
		int res = 0;
		BaseResponse response = new BaseResponse();
		DataResult dataResult = new DataResult();
		
		try {

			String res2 = "";
//	        if ("58BEA9A133900619879E8FFF1ECB0F336A5672CF6B68EB56B0D229FF4C6A7D52".equals(CertKey)) {
//	        	res2 = "Your CertiKey is Valid.";
//	        } else {
//	        	return gson.toJson("Your CertiKey is inValid."); 
//	        }
	        
	    	JSONObject obj = new JSONObject();
	    	JSONArray jArray = new JSONArray(); //????????? ????????????
	    	 
	    	JSONObject api_token = new JSONObject();
	    	JSONObject sendList = new JSONObject();	    	 
	    	JSONObject sObject = new JSONObject();//?????? ?????? ????????? json
	    	
	    	String templateCode = "";
	    	String senderkey = "";
	    	String title = "";
	    	String subject = "";
	    	String from_no = "";
	    	String message_type = "";
	        
	        String com_tono = "";
	        String org_orm_nm = "";
	        String send_yn = "";
	        String biztalkmessage = "";
	        
			title = "(CHATBOT)????????????????????????";
			subject = "(CHATBOT)????????????????????????";	      
    		
			String chg_content = "";
			chg_content = "???????????????("+ctm_hp+")";
//	    	biztalkmessage = "(????????????) ??????????????? "+orm_nm+" ?????????, ???????????? ???????????? ?????? " + "" +pay_amt3+ "?????? ?????????????????????.\r\n\r\n" +
//					 "1) ???????????? : "+""+pay_no+"\r\n"+
//					 "2) ???????????? : "+""+rem_dt+"";s	
			
		    Date today = new Date();
		        
		    SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");
	        
	        	if(com_brd.equals("T60I01")) {
        		
	        		biztalkmessage = "???????????????. ?????? ???????????????.\r\n\r\n" +
	        					"?????????????????? \"????????????\" ?????? ????????? ?????????."
	        					;
	        					
	        		senderkey = "dbf8669a88dd7926fd653ff3ff9b23d331fbbb4c";
	        		from_no = "1577-5670";
	        		templateCode = "iloomArsCallChatbotRequest";
	        		
	        	}else if(com_brd.equals("T60I02")) {
	        		
	        		biztalkmessage = "???????????????. ????????? ???????????????.\r\n\r\n" +
        					"?????????????????? \"????????????\" ?????? ????????? ?????????."
        					;
		
	        		senderkey = "9917d09567d2ebf1acc89662d7f9ff10db1488d7";
	        		from_no = "1588-1662";
	        		templateCode = "deskerArsCallChatbotRequest";
	        		
	        	}else if(com_brd.equals("T60I03")) {
	        		
	        		biztalkmessage = "???????????????. ????????? ???????????????.\r\n\r\n" +
        					"?????????????????? \"????????????\" ?????? ????????? ?????????."
        					;
	        		
	        		senderkey = "3ed320702f733d0b5a31e99a3ba931d9f2f9f960";
	        		from_no = "1899-8588";
	        		templateCode = "slouArsCallChatbotRequest";	        		

	        	}else if(com_brd.equals("T60P01")) {
	        		
	        		biztalkmessage = "???????????????. ????????? ???????????????.\r\n\r\n" +
        					"?????????????????? \"????????????\" ?????? ????????? ?????????."
        					;
	        		
	        		senderkey = "6b94c758a1f689223024765ae6e2b0aede351955";
	        		from_no = "1577-5674";
	        		templateCode = "sidizArsCallChatbotRequest";	  
	        		
	        	}else {
	        		
	        	}
	       	        	
		    	sendList.put("authKey", "D62D413F25CD43B3BD06636F2B3F570ABFB5008BD727901E341F041448D22C3A6593D58D45C68E60171F7FB2B2C345459361A08D20298BAE6A3A1B74196A95C3");
				
		       	 for (int i = 0; i < 1; i++)//??????
		       	 {
			        	 
			        	 
			        	 sObject.put("sendDiv", "BIZTALK" );
			        	 sObject.put("title", title);
			        	 sObject.put("subject", subject );		  
			        	 sObject.put("message","");
			        	 sObject.put("fromNm", "" );
			        	 sObject.put("toNm", "" );
			        	 sObject.put("fromNo", from_no ); 
			        	 sObject.put("toNo", ctm_hp);
			        	 sObject.put("companyCd", "T01B" );		        	 
			        	 sObject.put("fstUsr", "CHATBOT" );
			        	 sObject.put("systemNm", "CHATBOT" );
			        	 sObject.put("sendType", "SMTP" );
			        	 sObject.put("reserveDiv","I");
			        	 sObject.put("reserveDt", "" );
			        	 sObject.put("keyNo", "");
			        	 sObject.put("msgType", "TI4N" );		        	 
			        	 sObject.put("senderKey", senderkey);
			        	 sObject.put("templateCode", templateCode );
			        	 sObject.put("bizTalkMessage", biztalkmessage );
			        	 sObject.put("comBrd", com_brd );
			        	 
			        	 jArray.add(sObject);
		       	 }        	 
	        		        			
	        	sendList.put("list" ,jArray);  
	        	 
	        	BaseResponse kakao_res = RestCall("https://msg-api.fursys.com/v1/api/message/SendMsg",sendList);	
	        	if (!"200".equals(kakao_res.getResultCode())) {
					response.setResultCode("5001");
					response.setResultMessage("????????????????????? ??????  [" + kakao_res.getResultMessage() + "]");
					return gson.toJson(response);
				}
    
	        
		} catch (Exception e) {
			txManager.rollback(status);
			System.out.println(e.toString());			
			response.setResultCode("5001");
			response.setResultMessage(e.toString());
			return gson.toJson(response);
		}
		
		txManager.commit(status);
		response.setResultCode("200");
		System.out.println(response.toString());	
		return gson.toJson(response);
	}	
	
}
	
