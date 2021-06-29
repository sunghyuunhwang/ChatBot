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
	
	@ApiOperation(value = "headerTest", notes = "헤더테스트")
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
	
	@ApiOperation(value = "erp_selectOrderTest", notes = "api오더정보테스트")
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
        params.put("function_name", "챗봇 오더정보 조회 테스트");
        response = apiCommonService.erp_callApiHistSave(params);
		return gson.toJson(arList);        
        
	}	 
	
	@ApiOperation(value = "erp_searchOrderInfo", notes = "ERP_API_주문정보조회")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK !!"), @ApiResponse(code = 5001, message = "조회 실패 !!") })
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
	        params.put("function_name", "챗봇 오더정보 마스터 조회");
	        response = apiCommonService.erp_callApiHistSave(params);	    	
	    	
	        return gson.toJson(arList); 
  	
	    } else {
	        		
	        ArrayList<ERPOrderSearchVo> arList = cChatBotMapper.selectAsSearchList(params);
	        
	        params.put("call_function", "erp_searchOrderInfo");
	        params.put("function_name", "챗봇 오더정보 마스터 조회");
	        response = apiCommonService.erp_callApiHistSave(params);
	        
	        return gson.toJson(arList); 
	    }
	    
	    
	}
	
	@ApiOperation(value = "erp_searchOrderDetailInfo", notes = "ERP_API_주문상세정보조회")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK !!"), @ApiResponse(code = 5001, message = "조회 실패 !!") })
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
	        params.put("function_name", "챗봇 오더상세정보 조회");
	        response = apiCommonService.erp_callApiHistSave(params);	 
	        
	    	return gson.toJson(arList); 
	    	        	
	       
	    } else {
	        		
	        arList = cChatBotMapper.selectAsDetailSearchList(params);
	        
	        params.put("call_function", "erp_searchOrderDetailInfo");
	        params.put("function_name", "챗봇 오더상세정보 조회");
	        response = apiCommonService.erp_callApiHistSave(params);		
	        
	        return gson.toJson(arList); 
	    }
	}

	@ApiOperation(value = "erp_asArrivalDateChangeAutoCheck", notes = "ERP_API_주문변경Auto처리여부확인")
	@ApiResponses({ @ApiResponse(code = 200, message = "주문변경Auto처리여부확인 성공 !!"), @ApiResponse(code = 5001, message = "주문변경Auto처리여부확인 실패 !!") })
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
	        params.put("function_name", "챗봇 ERP_API_주문변경Auto처리여부확인");
	        response = apiCommonService.erp_callApiHistSave(params);
	        
		} catch (Exception e) {
			
			response.setResultCode("5001");
			response.setResultMessage(e.toString());
			return gson.toJson(item);
		}
		
		response.setResultCode("200");
		return gson.toJson(item);
	}	
	
	@ApiOperation(value = "erp_OrderArrivalDateChange", notes = "주문 도착일정 변경 Auto 처리")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK !!"), @ApiResponse(code = 5001, message = "주문 도착일정 변경 실패 Auto 처리 !!") })
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
// 1. 신규 수주마스터 정보 등록
			
			//	1.1 신규 수주번호 채번
			
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("key_no", key_no);
					
			dataResult = cChatBotChangeMapper.executeProGetNo(params);
			
			if (dataResult == null) { 
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("신규 수주번호 채번 오류");
				return gson.toJson(response);
			}
			
			String new_ormno = "";			
			new_ormno = dataResult.getData1() ;

			//  1.2 변경요청일자
			
			params.put("com_cmp", com_cmp);
			dataResult = cChatBotChangeMapper.selectToChgmasChgdt(params);
			
			if (dataResult == null) { 
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("변경요청일자 SELECT 오류");
				return gson.toJson(response);
			}
			
			String chg_orm_cdt = "";
			chg_orm_cdt = dataResult.getData1() ;
			
			//	1.3 신규 수주마스터 등록
			params.put("new_orm_no", new_ormno);
			params.put("orm_no", key_no);
			params.put("chg_orm_cdt", chg_orm_cdt);
			params.put("com_cmp", com_cmp);
			
        	res = cChatBotChangeMapper.insertNewOrdMaster(params);
        	
			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("수주마스터 등록 오류 [" + res + "]");
				return gson.toJson(response);
			}			
			
//	2. 신규 수주디테일 등록
			
        	res = cChatBotChangeMapper.insertNewOrdDetail(params);
        	
			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("수주디테일 등록 오류 [" + res + "]");
				return gson.toJson(response);
			}			
			
//	3. 수주품목별 현황테이블 삭제
			
        	res = cChatBotChangeMapper.deleteRecdRenD(params);
        	
			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("수주품목별 현황테이블 삭제 오류 [" + res + "]");
				return gson.toJson(response);
			}			
			
//	4. 수주품목별 현황테이블 등록			
			
        	res = cChatBotChangeMapper.insertRecdRenD(params);
        	
			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("수주품목별 현황테이블 삭제 오류 [" + res + "]");
				return gson.toJson(response);
			}			
			
//  5. 수주변경요청 마스터정보 수정 			
			
			// 1. 수주변경요청 순번 가져오기
			
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
				response.setResultMessage("수주변경요청 마스터정보 수정 	 오류 [" + res + "]");
				return gson.toJson(response);
			}			
			
//  6. 수주 마스터정보 수정 			
			
        	res = cChatBotChangeMapper.updateToOrdmasStat(params);
        	
			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("수주 마스터정보 수정 	 오류 [" + res + "]");
				return gson.toJson(response);
			}			
			
			
////  7. 수주변경요청 마스터정보 추가 등록		
//			
//        	res = cChatBotChangeMapper.insertToChgmasAdded(params);
//        	
//			if (res < 0) {
//				txManager.rollback(status);
//				response.setResultCode("5001");
//				response.setResultMessage("수주변경요청 마스터정보 추가 등록 오류 [" + res + "]");
//				return gson.toJson(response);
//			}			

//시공 유  / 시공무 확인			
			
			dataResult = cChatBotChangeMapper.selectOrmCryn(params);
						
			String orm_cryn = "";
			String before_orm_cdt = "";
			
			orm_cryn = dataResult.getData1() ;
			before_orm_cdt = dataResult.getData2();
			
			if ("Y".equals(orm_cryn)) {
				
				//시공예약여부 확인하기
				String sigongreserve_yn = "";
				
				dataResult = cChatBotChangeMapper.selectSigongReserveYn(params);
				
				sigongreserve_yn = dataResult.getData1() ;
				
				if ("Y".equals(sigongreserve_yn)) {
					
					//시공예약된 key 값 가져오기 
					
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
					
					
					//신규 시공예약 key 채번하기 
					
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
						response.setResultMessage("insertTcSeqnoinf 등록 오류 [" + res + "]");
						return gson.toJson(response);
					}			
					
					String new_rem_seq = "";
					new_rem_seq = StringUtils.right(com_agsec, 1) + StringUtils.right("C18C", 1) + StringUtils.leftPad(String.valueOf(max_seq_no), 4, "0");
					
					//tc_resdtl 업데이트
					
					
					params.put("new_rem_seq", new_rem_seq);
					params.put("req_dt", req_dt);
					
		        	res = cChatBotChangeMapper.updateTcresdtl(params);
		        	
					if (res < 0) {
						txManager.rollback(status);
						response.setResultCode("5001");
						response.setResultMessage("updateTcresdetl 수정 	 오류 [" + res + "]");
						return gson.toJson(response);
					}			

					//tc_resmst 업데이트
					params.put("new_ormno", new_ormno);
		        	res = cChatBotChangeMapper.updateTcresmst(params);
		        	
					if (res < 0) {
						txManager.rollback(status);
						response.setResultCode("5001");
						response.setResultMessage("updateTcresmst 수정 	 오류 [" + res + "]");
						return gson.toJson(response);
					}			

					//tc_planmst 업데이트	
					
					if("XXXX".equals(plm_no)) {
						
						params.put("plm_no", plm_no);
						
			        	res = cChatBotChangeMapper.updateTcplanmst(params);
			        	
						if (res < 0) {
							txManager.rollback(status);
							response.setResultCode("5001");
							response.setResultMessage("updateTcplanmst 수정 오류 [" + res + "]");
							return gson.toJson(response);
						}			
	
					}
					
					//to_ordmas 업데이트
					
					params.put("new_ormno", new_ormno);
					params.put("chg_orm_cdt", chg_orm_cdt);
					
		        	res = cChatBotChangeMapper.updateToOrdmasSigongStat(params);
		        	
					if (res < 0) {
						txManager.rollback(status);
						response.setResultCode("5001");
						response.setResultMessage("updateToOrdmasSigongStat 수정 오류 [" + res + "]");
						return gson.toJson(response);
					}			
	
				}
				
			}
			
			//대리점건인 경우 알람 메시지 insert 처리
			
			dataResult = cChatBotChangeMapper.selectOnlineynCheck(params);
			
			String online_yn = "";
			String orm_nm = "";			
			String message= "";
			
			online_yn = dataResult.getData1();
			orm_nm = dataResult.getData2();
			
			message = "챗봇을 통해 납기변경처리 되었습니다. (주문번호 : " + key_no + ",  수주건명 : " + orm_nm + " )";
					
			if(online_yn.equals("N")) {
				
				params.put("message", message);
				
				res = cChatBotChangeMapper.insertAgtChangeAlarm(params);
				
				if (res < 0) {
					txManager.rollback(status);
					response.setResultCode("5001");
					response.setResultMessage("insertAgtChangeAlarm 대리점알림메세지 insert 오류 [" + res + "]");
					return gson.toJson(response);
				}					
			}

	        params.put("call_function", "erp_OrderArrivalDateChange");
	        params.put("function_name", "챗봇 주문 도착일정 변경 Auto 처리");
	        response = apiCommonService.erp_callApiHistSave(params);			
			
	    	JSONObject obj = new JSONObject();
	    	JSONArray jArray = new JSONArray(); //배열이 필요할때
	    	JSONArray jArray2 = new JSONArray(); //배열이 필요할때
	    	
	    	JSONObject api_token = new JSONObject();
	    	JSONObject sendList = new JSONObject();	    	 
	    	JSONObject sObject = new JSONObject();//배열 내에 들어갈 json
	    	JSONObject sObject2 = new JSONObject();//배열 내에 들어갈 json
	    	
	    	String templateCode = "";
	    	String senderkey = "";
	    	String title = "";
	    	String subject = "";
	    	String from_no = "";
	    	String message_type = "";
	    	String sales_managerhp = "";
	    	
	        //변경요청정보 기본 정보 가져오기	    	
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
	        
			title = "(CHATBOT)날짜변경AUTO처리안내";
			subject = "(CHATBOT)날짜변경AUTO처리안내";	      
    		
			String chg_content = "";
			chg_content = "날짜변경("+req_dt+")";
//	    	biztalkmessage = "(결제안내) 안녕하세요 "+orm_nm+" 고객님, 고객님의 결제하신 금액 " + "" +pay_amt3+ "원이 승인되었습니다.\r\n\r\n" +
//					 "1) 결제번호 : "+""+pay_no+"\r\n"+
//					 "2) 결제일자 : "+""+rem_dt+"";s	
			
			String pattern = "yyyy-MM-dd";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

			String date = simpleDateFormat.format(new Date());
			System.out.println(date);
		        
	        if(send_yn.equals("Y")) {
	        
	        	if(com_brd.equals("T60I01")) {
        		
	        		biztalkmessage = "안녕하세요.\r\n" +
	        					"귀 매장의 고객님이 일룸챗봇을 통해 아래와 같이 수주 변경을 진행하여 알림 드립니다.\r\n\r\n" +
	        					"수주변경 내용\r\n"+
	        					"수주번호 : "+key_no+"\r\n"+
	        					"수주건명 : "+org_orm_nm+"\r\n"+
	        					"요청내용 : "+chg_content+"\r\n"+
	        					"요청일자 : "+date+"\r\n\r\n"+
	        					"관련 내용 확인 부탁드립니다.\r\n"+
	        					"감사합니다.\r\n"
	        					;
	        					
	        		senderkey = "dbf8669a88dd7926fd653ff3ff9b23d331fbbb4c";
	        		from_no = "1577-5670";
	        		templateCode = "iloomchatbotorderchangeinform0";
	        		
	        	}else if(com_brd.equals("T60I02")) {
	        		
	        		biztalkmessage = "안녕하세요.\r\n" +
        					"귀 매장의 고객님이 데스커챗봇을 통해 아래와 같이 수주 변경을 진행하여 알림 드립니다.\r\n\r\n" +
        					"수주변경 내용\r\n"+
        					"수주번호 : "+key_no+"\r\n"+
        					"수주건명 : "+org_orm_nm+"\r\n"+
        					"요청내용 : "+chg_content+"\r\n"+
        					"요청일자 : "+date+"\r\n\r\n"+
        					"관련 내용 확인 부탁드립니다.\r\n"+
        					"감사합니다.\r\n"
        					;
		
	        		senderkey = "9917d09567d2ebf1acc89662d7f9ff10db1488d7";
	        		from_no = "1588-1662";
	        		templateCode = "deskerchatbotorderchginform0";
	        		
	        	}else if(com_brd.equals("T60I03")) {
	        		
	        		biztalkmessage = "안녕하세요.\r\n" +
        					"귀 매장의 고객님이 슬로우챗봇을 통해 아래와 같이 수주 변경을 진행하여 알림 드립니다.\r\n\r\n" +
        					"수주변경 내용\r\n"+
        					"수주번호 : "+key_no+"\r\n"+
        					"수주건명 : "+org_orm_nm+"\r\n"+
        					"요청내용 : "+chg_content+"\r\n"+
        					"요청일자 : "+date+"\r\n\r\n"+
        					"관련 내용 확인 부탁드립니다.\r\n"+
        					"감사합니다.\r\n"
        					;
	        		
	        		senderkey = "3ed320702f733d0b5a31e99a3ba931d9f2f9f960";
	        		from_no = "1899-8588";
	        		templateCode = "slouchatbotorderchangeinform0";	        		

	        	}else if(com_brd.equals("T60P01")) {
	        		
	        		biztalkmessage = "안녕하세요.\r\n" +
        					"귀 매장의 고객님이 시디즈챗봇을 통해 아래와 같이 수주 변경을 진행하여 알림 드립니다.\r\n\r\n" +
        					"수주변경 내용\r\n"+
        					"수주번호 : "+key_no+"\r\n"+
        					"수주건명 : "+org_orm_nm+"\r\n"+
        					"요청내용 : "+chg_content+"\r\n"+
        					"요청일자 : "+date+"\r\n\r\n"+
        					"관련 내용 확인 부탁드립니다.\r\n"+
        					"감사합니다.\r\n"
        					;
	        		
	        		senderkey = "6b94c758a1f689223024765ae6e2b0aede351955";
	        		from_no = "1577-5674";
	        		templateCode = "sidizchatbotorderchangeinform0";	  
	        		
	        	}else {
	        		
	        	}
	       	        	
		    	sendList.put("authKey", "D62D413F25CD43B3BD06636F2B3F570ABFB5008BD727901E341F041448D22C3A6593D58D45C68E60171F7FB2B2C345459361A08D20298BAE6A3A1B74196A95C3");
				
		       	 for (int i = 0; i < 1; i++)//배열
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
					response.setResultMessage("알림톡전송결과 오류  [" + kakao_res.getResultMessage() + "]");
					return gson.toJson(response);
				}
				
	 	        
	        	if(com_brd.equals("T60I01")) {
					
			       	 for (int i = 0; i < 1; i++)//배열
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
						response.setResultMessage("알림톡전송결과 오류  [" + kakao_res2.getResultMessage() + "]");
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
	
	@ApiOperation(value = "erp_asArrivalDateChange", notes = "AS 도착일정 변경")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK !!"), @ApiResponse(code = 5001, message = "AS 도착일정 변경 실패 !!") })
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
			
			//예약확정여부 확인
			
			dataResult = cChatBotChangeMapper.selectAsDateConfirmYn(params);
			String com_rmfg = "";
			
			com_rmfg = dataResult.getData1() ;
						
			if ("C142".equals(com_rmfg)) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("AS일정 확정된 건은 변경이 불가합니다.");
				return gson.toJson(response);
			}			
			
			//출고처리여부 확인
			
			dataResult = cChatBotChangeMapper.selectAsChulgoYn(params);
			
			String com_sprog = "";
			com_sprog = dataResult.getData1() ;
			
			if ("A17008".equals(com_sprog)) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("이미 출고 완료된 건은 변경이 불가합니다.");
				return gson.toJson(response);
			}			
			
			//최종변경처리
			
			params.put("rpt_no", rpt_no);
			params.put("rpt_seq", rpt_seq);
			
			dataResult = cChatBotChangeMapper.executePraFaChgreqdt(params);
			
			if (dataResult == null) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("AS일정 변경 오류");
				return gson.toJson(response);
			}

			//AUTO처리정보 이력 테이블 insert
			params.put("chgreq_hp", "");
			params.put("chgreq_dt", req_dt);
			params.put("chgreq_addr", "");
			params.put("reqorm_no", key_no);
			params.put("chgreq_std", "A96005");
			
			res = cChatBotChangeMapper.insertAsAutoChgReqInformation(params);
			
			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("insertAsAutoChgReqInformation 오류 [" + res + "]");
				return gson.toJson(response);
			}			
						
	        params.put("call_function", "erp_asArrivalDateChange");
	        params.put("function_name", "챗봇 AS 도착일정 변경");
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

	
	@ApiOperation(value = "erp_asHpChange", notes = "AS 고객 핸드폰 번호 변경")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK !!"), @ApiResponse(code = 5001, message = "AS AS 고객 핸드폰 번호 변경 실패 !!") })
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
			
        	//tc_resmst 변경
        	res = cChatBotChangeMapper.updateAsHpChange(params);
        	
			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("updateAsHpChange 오류 [" + res + "]");
				return gson.toJson(response);
			}			
			
			//AUTO처리정보 이력 테이블 insert
			params.put("chgreq_hp", ctm_hp);
			params.put("chgreq_dt", "");
			params.put("chgreq_addr", "");
			params.put("reqorm_no", key_no);
			params.put("chgreq_std", "A96007");
			
			res = cChatBotChangeMapper.insertAsAutoChgReqInformation(params);
			
			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("insertAsAutoChgReqInformation 오류 [" + res + "]");
				return gson.toJson(response);
			}

	        params.put("call_function", "erp_asHpChange");
	        params.put("function_name", "챗봇 AS 고객 핸드폰 번호 변경");
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

	@ApiOperation(value = "erp_OrderChangeDtRequest", notes = "주문 도착일정 변경")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK !!"), @ApiResponse(code = 5001, message = "주문 도착일정 변경 실패 !!") })
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
			
			
			//변경요청 확인 순서
			//1. 시공유건의 경우
			//   - 시공분배 확정여부체크
			//   - 시공확정여부 체크
			//   - 기요청건 처리 완료 여부 체크
			//2. 시공무건의 경우
			//   - 기요청건 처리 완료 여부 체크
			//   - 매출발행여부 체크
			
			//step1 - 시공유부 체크
			
			dataResult = cChatBotChangeMapper.selectSigongYn(params);
			
			String orm_cryn = "";
			orm_cryn = dataResult.getData1();
			
			if ("Y".equals(orm_cryn)) {  //시공건의 경우
				
				//- 시공분배 확정여부체크
				dataResult = cChatBotChangeMapper.selectSchDivYn(params);
				
				String sch_divyn = "";
				sch_divyn = dataResult.getData1();
				
				
				if ("Y".equals(sch_divyn)) {
					txManager.rollback(status);
					response.setResultCode("5001");
					response.setResultMessage("이미 일정이 확정된 건입니다. 납기 변경이 불가합니다.");
					return gson.toJson(response);
				}				
				
				//- 시공예약 확정여부체크
				dataResult = cChatBotChangeMapper.selectSigongConfirmYn(params);
				
				String com_rfg = "";
				com_rfg = dataResult.getData1();
				
				
				if ("C142".equals(com_rfg)) {
					txManager.rollback(status);
					response.setResultCode("5001");
					response.setResultMessage("이미 일정이 확정된 건입니다. 납기 변경이 불가합니다.");
					return gson.toJson(response);
				}					
				
			}else {
		
				//- 매출발행여부 체크
				dataResult = cChatBotChangeMapper.selectFinishYn(params);
				
				String finish_yn = "";
				finish_yn = dataResult.getData1();
				
				
				if ("Y".equals(finish_yn)) {
					txManager.rollback(status);
					response.setResultCode("5001");
					response.setResultMessage("이미 출고 완료된 건입니다.");
					return gson.toJson(response);
				}				
			}
			
			//기변경요청건 있는지 여부 체크

			dataResult = cChatBotChangeMapper.selectChgstatusYn(params);
			
			String req_status = "";
			req_status = dataResult.getData1();
			
			
			if ("Y".equals(req_status)) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("기존에 변경 요청한 건이 아직 처리 중입니다.");
				return gson.toJson(response);
			}			
			
			//최종변경처리 - 변경요청정보 insert
        	res = cChatBotChangeMapper.insertOrmCdtChangeReq(params);
        	
			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("insertOrmCdtChangeReq 오류 [" + res + "]");
				return gson.toJson(response);
			}			
			
	        params.put("call_function", "erp_OrderChangeDtRequest");
	        params.put("function_name", "챗봇 주문 도착일정 변경");
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

	
	
	
	@ApiOperation(value = "erp_orderHpChange", notes = "주문 핸드폰번호 변경")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK !!"), @ApiResponse(code = 5001, message = "주문 핸드폰번호 변경 실패 !!") })
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
			
        	//tc_resmst 변경
        	res = cChatBotChangeMapper.updateOrderHpChange(params);
        	
			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("updateOrderHpChange 오류 [" + res + "]");
				return gson.toJson(response);
			}			

			//대리점건인 경우 알람 메시지 insert 처리
			
			dataResult = cChatBotChangeMapper.selectOnlineynCheck(params);
			
			String online_yn = "";
			String orm_nm = "";			
			String message= "";
			
			online_yn = dataResult.getData1();
			orm_nm = dataResult.getData2();
			
			message = "챗봇을 통해 납품처 핸드폰 번호 변경 처리 되었습니다. (주문번호 : " + key_no + ",  수주건명 : " + orm_nm + " )";
					
			if(online_yn.equals("N")) {
				
				params.put("message", message);
				
				res = cChatBotChangeMapper.insertAgtChangeAlarm(params);
				
				if (res < 0) {
					txManager.rollback(status);
					response.setResultCode("5001");
					response.setResultMessage("insertAgtChangeAlarm 대리점알림메세지 insert 오류 [" + res + "]");
					return gson.toJson(response);
				}					
			}

	        params.put("call_function", "erp_orderHpChange");
	        params.put("function_name", "챗봇 주문 납품처 핸드폰번호 변경");
	        response = apiCommonService.erp_callApiHistSave(params);	

			
	    	JSONObject obj = new JSONObject();
	    	JSONArray jArray = new JSONArray(); //배열이 필요할때
	    	JSONArray jArray2 = new JSONArray(); //배열이 필요할때
	    	
	    	JSONObject api_token = new JSONObject();
	    	JSONObject sendList = new JSONObject();	    	 
	    	JSONObject sObject = new JSONObject();//배열 내에 들어갈 json
	    	JSONObject sObject2 = new JSONObject();//배열 내에 들어갈 json
	    	
	    	String templateCode = "";
	    	String senderkey = "";
	    	String title = "";
	    	String subject = "";
	    	String from_no = "";
	    	String message_type = "";
	    		        
	        //변경요청정보 기본 정보 가져오기	    	
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
	        
			title = "(CHATBOT)연락처변경AUTO처리안내";
			subject = "(CHATBOT)연락처변경AUTO처리안내";	      
    		
			String chg_content = "";
			chg_content = "연락처변경";
//	    	biztalkmessage = "(결제안내) 안녕하세요 "+orm_nm+" 고객님, 고객님의 결제하신 금액 " + "" +pay_amt3+ "원이 승인되었습니다.\r\n\r\n" +
//					 "1) 결제번호 : "+""+pay_no+"\r\n"+
//					 "2) 결제일자 : "+""+rem_dt+"";s	
			
			String pattern = "yyyy-MM-dd";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

			String date = simpleDateFormat.format(new Date());
			System.out.println(date);
		        
	        if(send_yn.equals("Y")) {
	        
	        	if(com_brd.equals("T60I01")) {
        		
	        		biztalkmessage = "안녕하세요.\r\n" +
	        					"귀 매장의 고객님이 일룸챗봇을 통해 아래와 같이 수주 변경을 진행하여 알림 드립니다.\r\n\r\n" +
	        					"수주변경 내용\r\n"+
	        					"수주번호 : "+key_no+"\r\n"+
	        					"수주건명 : "+org_orm_nm+"\r\n"+
	        					"요청내용 : "+chg_content+"\r\n"+
	        					"요청일자 : "+date+"\r\n\r\n"+
	        					"관련 내용 확인 부탁드립니다.\r\n"+
	        					"감사합니다.\r\n"
	        					;
	        					
	        		senderkey = "dbf8669a88dd7926fd653ff3ff9b23d331fbbb4c";
	        		from_no = "1577-5670";
	        		templateCode = "iloomchatbotorderchangeinform0";
	        		
	        	}else if(com_brd.equals("T60I02")) {
	        		
	        		biztalkmessage = "안녕하세요.\r\n" +
        					"귀 매장의 고객님이 데스커챗봇을 통해 아래와 같이 수주 변경을 진행하여 알림 드립니다.\r\n\r\n" +
        					"수주변경 내용\r\n"+
        					"수주번호 : "+key_no+"\r\n"+
        					"수주건명 : "+org_orm_nm+"\r\n"+
        					"요청내용 : "+chg_content+"\r\n"+
        					"요청일자 : "+date+"\r\n\r\n"+
        					"관련 내용 확인 부탁드립니다.\r\n"+
        					"감사합니다.\r\n"
        					;
		
	        		senderkey = "9917d09567d2ebf1acc89662d7f9ff10db1488d7";
	        		from_no = "1588-1662";
	        		templateCode = "deskerchatbotorderchginform0";
	        		
	        	}else if(com_brd.equals("T60I03")) {
	        		
	        		biztalkmessage = "안녕하세요.\r\n" +
        					"귀 매장의 고객님이 슬로우챗봇을 통해 아래와 같이 수주 변경을 진행하여 알림 드립니다.\r\n\r\n" +
        					"수주변경 내용\r\n"+
        					"수주번호 : "+key_no+"\r\n"+
        					"수주건명 : "+org_orm_nm+"\r\n"+
        					"요청내용 : "+chg_content+"\r\n"+
        					"요청일자 : "+date+"\r\n\r\n"+
        					"관련 내용 확인 부탁드립니다.\r\n"+
        					"감사합니다.\r\n"
        					;
	        		
	        		senderkey = "3ed320702f733d0b5a31e99a3ba931d9f2f9f960";
	        		from_no = "1899-8588";
	        		templateCode = "slouchatbotorderchangeinform0";	        		

	        	}else if(com_brd.equals("T60P01")) {
	        		
	        		biztalkmessage = "안녕하세요.\r\n" +
        					"귀 매장의 고객님이 시디즈챗봇을 통해 아래와 같이 수주 변경을 진행하여 알림 드립니다.\r\n\r\n" +
        					"수주변경 내용\r\n"+
        					"수주번호 : "+key_no+"\r\n"+
        					"수주건명 : "+org_orm_nm+"\r\n"+
        					"요청내용 : "+chg_content+"\r\n"+
        					"요청일자 : "+date+"\r\n\r\n"+
        					"관련 내용 확인 부탁드립니다.\r\n"+
        					"감사합니다.\r\n"
        					;
	        		
	        		senderkey = "6b94c758a1f689223024765ae6e2b0aede351955";
	        		from_no = "1577-5674";
	        		templateCode = "sidizchatbotorderchangeinform0";	  
	        		
	        	}else {
	        		
	        	}
	       	        	
		    	sendList.put("authKey", "D62D413F25CD43B3BD06636F2B3F570ABFB5008BD727901E341F041448D22C3A6593D58D45C68E60171F7FB2B2C345459361A08D20298BAE6A3A1B74196A95C3");
				
		       	 for (int i = 0; i < 1; i++)//배열
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
					response.setResultMessage("알림톡전송결과 오류  [" + kakao_res.getResultMessage() + "]");
					return gson.toJson(response);
				}
				
	        	
	        	if(com_brd.equals("T60I01")) {
					
			       	 for (int i = 0; i < 1; i++)//배열
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
						response.setResultMessage("알림톡전송결과 오류  [" + kakao_res2.getResultMessage() + "]");
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

	@ApiOperation(value = "erp_chgmindate", notes = "주문변경 최소 납기일자 조회")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK !!"), @ApiResponse(code = 5001, message = "주문변경 최소 납기일자 조회 실패 !!") })
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
			
			//1. 최소재고예약가능일 조회 
			//   - 수주번호로 우편번호 가져오기
			//   - executePraMindateZipChatbot 실행
			
			//시공 or 시공무 여부 확인
			//2-1. 시공인 경우 - 최소예약 시공가능일자 가져오기
			//	   - 
			//2-2. 시공 무인 경우 1번에서 최소재고예약가능일 return
			
			dataResult = cChatBotChangeMapper.selectSigongYn(params);
			
			String orm_cryn = "";
			orm_cryn = dataResult.getData1();		

			//최소재고예약가능일 조회
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
				response.setResultMessage("executePraMindateZipChatbot 오류");
				return gson.toJson(item);
			}
			
			String orm_cdt = "";			
			orm_cdt = item.getAvailable_date();	
			
			if("Y".equals(orm_cryn)) {
				
				String com_scd = "";
				String bdong_cd = "";
				String sti_cd = "";
				
				//com_scd 가져오기 
				dataResult = cChatBotChangeMapper.selectOrmComScd(params);
				
				com_scd = dataResult.getData1();	
				
				HashMap<String, Object> params3 = new HashMap<String, Object>();
				params3.put("key_no", key_no);			
				params3.put("com_scd", com_scd);					
				
				dataResult = cChatBotChangeMapper.selectOrmStiCd(params3);
		
				sti_cd = dataResult.getData1();	
				
				
				//bdong_cd 가져오기
				dataResult = cChatBotChangeMapper.selectOrmBdongCd(params3);
				
				bdong_cd = dataResult.getData1();	
				
				
				// sti_cd 가져오기
				
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
					response.setResultMessage("executePraDayInfChatbot 오류");
					return gson.toJson(item);
				}				
				
				orm_cdt = item.getAvailable_date();	
				
			}
			
	        params.put("call_function", "erp_chgmindate");
	        params.put("function_name", "챗봇 주문변경 최소 납기일 조회");
	        response = apiCommonService.erp_callApiHistSave(params);			
	        
		} catch (Exception e) {
			
			response.setResultCode("5001");
			response.setResultMessage(e.toString());
			return gson.toJson(item);
		}
		
		response.setResultCode("200");
		return gson.toJson(item);
	}	

	@ApiOperation(value = "erp_orderChangeDtAvailableYn", notes = "날짜변경 가능 여부 체크")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK !!"), @ApiResponse(code = 5001, message = "날짜변경 가능 여부 체크 실패 !!") })
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

		    //기존 확정납기일보다 더 납기일을 당기는 경우에는 무조건 available_yn이 무조건 "N"	        
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
		
	//			*시공 일정확인
	//			call PRC_DAYINF('C02I', 'T60I01', '1174010800', '20210330', 'C16YA', 'YA200', '1')
	//			* 재고예약가능일
	//			call pro_ren_preenposymd('I20210321000100','T01I','05373','20210330','T60I01','<OUT>'
	//			* 시디즈 확인하기
				
				//시공유건인 경우
				if("Y".equals(orm_cryn)) {
					dataResult = cChatBotChangeMapper.selectIloompreenposymd(params);
					final_date = dataResult.getData1();
					
					
					//시공가능일자 확인
					if (Integer.parseInt(final_date) <= Integer.parseInt(req_dt)) {
					
						dataResult = cChatBotChangeMapper.selectSigongReserveYn(params);
						sigongreserve_yn = dataResult.getData1();
						
						//이미 시공예약이 되어 있는 경우
						
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
								System.out.println("간으해");
								available_yn = "Y";
								item = available_yn;								
							} else {
								System.out.println("불가능해1");
								available_yn = "N";
								item = available_yn;								
							}
							
						//시공예약이 되어 있지 않은 경우 	
						}else {
							
							params.put("key_no", key_no);
							dataResult = cChatBotChangeMapper.selectGetComScd(params);
							
							new_com_scd = dataResult.getData1();
							
							//전담시공팀이 있는지 확인
							
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
								System.out.println("가능해2");
								//휴무일체크
								
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
								System.out.println("불가능해2");
								available_yn = "N";
								item = available_yn;								
							}						
		
						}
						
					} else {
						System.out.println("불가능해3");
						available_yn = "N";
						item = available_yn;
					}
					
					
					
				}
				//시공무건인경우
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
						System.out.println("불가능해4");
						available_yn = "N";
						item = available_yn;
					}				
				}

	        }
	        
	        params.put("call_function", "erp_orderChangeDtAvailableYn");
	        params.put("function_name", "챗봇 날짜변경 가능여부 체크");
	        response = apiCommonService.erp_callApiHistSave(params);				
			
		} catch (Exception e) {
			
			response.setResultCode("5001");
			response.setResultMessage(e.toString());
			return gson.toJson(item);
		}
		
		response.setResultCode("200");
		return gson.toJson(item);
	}	

	//황성현
	@ApiOperation(value = "erp_orderChangeAvailableDateList", notes = "주문변경가능한 날짜 리스트 조회")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK !!"), @ApiResponse(code = 5001, message = "주문변경가능한 날짜 리스트 조회 실패 !!") })
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
		
		JSONObject sObject = new JSONObject();//배열 내에 들어갈 json
		JSONArray jArray = new JSONArray(); //배열이 필요할때
		
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
			
			//시공유건인 경우
			if ("Y".equals(orm_cryn)) {
				
				params.put("key_no", key_no);
				params.put("com_cmp", com_cmp);
				params.put("orm_gpost", orm_gpost);
				params.put("req_dt", orm_adt);
				params.put("com_brand", com_brand);				
				
				dataResult = cChatBotChangeMapper.selectIloompreenposymd(params);
				final_date = dataResult.getData1();
				
				if (Integer.parseInt(final_date) <= Integer.parseInt(orm_adt)) {
								
					//휴무일체크
					
					params.put("req_dt", orm_adt);
					dataResult = cChatBotChangeMapper.selectWorkingYnCheck(params);
					
					working_yn = dataResult.getData1();
					
					if ("Y".equals(working_yn)) {
						
						
						//시공 Capacity 확인
						//1. 전담시공팀 여부 확인
						
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
			//시공무건인 경우
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

					//휴무일체크
					
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
        params.put("function_name", "챗봇 주문변경가능한 날짜 리스트 조회");
        response = apiCommonService.erp_callApiHistSave(params);			
        
	    return gson.toJson(jArray); 
	}

		
	
	@ApiOperation(value = "erp_selectbdongcode", notes = "법정동코드 가져오기")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK !!"), @ApiResponse(code = 5001, message = "법정동코드 가져오기 조회 실패 !!") })
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
				response.setResultMessage("selectBdongCd 오류");
				return gson.toJson(item);
			}			
			
	        params.put("call_function", "erp_selectbdongcode");
	        params.put("function_name", "챗봇 법정동 코드 가져오기");
	        response = apiCommonService.erp_callApiHistSave(params);	
	        
		} catch (Exception e) {
			
			response.setResultCode("5001");
			response.setResultMessage(e.toString());
			return gson.toJson(item);
		}

        
		response.setResultCode("200");
		return gson.toJson(item);
	}	
	
	@ApiOperation(value = "erp_sameOrderCheck", notes = "ERP_API_동일수주여부 체크 로직")
	@ApiResponses({ @ApiResponse(code = 200, message = "ERP_API_동일수주여부 체크 로직 확인 성공 !!"), @ApiResponse(code = 5001, message = "ERP_API_동일수주여부 체크 로직 확인 실패 !!") })
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
			
			//온라인 or 오프라인 여부 체크
		
			dataResult = cChatBotChangeMapper.selectOnlineYn(params);
			
			//data1 = 쇼핑몰주문번호
			//data2 = 법정동코드
			//data3 = 우편번호
			//data4 = 주문일시
			//data5 = 대리점코드
			//data6 = 사업소 유형
			
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
			//온라인 수주의 경우	
				
				item = cChatBotChangeMapper.selectOnlineSameOrderYn(params);
				
				sameorder_yn = dataResult.getData1();
				
			} else {
			// 온라인 수주가 아닌 경우	
				
				item = cChatBotChangeMapper.selectOfflineSameOrderYn(params);
				
				sameorder_yn = dataResult.getData1();
				
			}
			
	        params.put("call_function", "erp_sameOrderCheck");
	        params.put("function_name", "챗봇 ERP_API_동일수주여부 체크 로직");
	        response = apiCommonService.erp_callApiHistSave(params);
	        
		} catch (Exception e) {
			
			response.setResultCode("5001");
			response.setResultMessage(e.toString());
			return gson.toJson(dataResult);
		}
		
		response.setResultCode("200");
		return gson.toJson(item);
	}	

	@ApiOperation(value = "erp_OrderChangeAddressRequestProc", notes = "주문 주소변경요청 처리")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK !!"), @ApiResponse(code = 5001, message = "주문 주소변경요청 처리 실패 !!") })
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
			
			//주소변경 요청 처리 순서
			//1. to_chgmas insert
			
			//2-1. 시공무건인 경우
			//		1) 수주 주소 정보 수정
			//		2) TO_CHGMAS_ADDED insert
			
			//2-2. 시공유건인경우
			//		1) 수주 주소 정보 수정
			//		2) TO_CHGMAS_ADDED insert
			//		3) TC_RESMST 정보 수정
			//		4) TC_PLANMST 정보 수정
			
			//3. to_chgmas 상태값 update
			
			
			//1. to_chgmas insert
        	res = cChatBotChangeMapper.insertChgAddrReq(params);
        	
			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("insertChgAddrReq 오류 [" + res + "]");
				return gson.toJson(response);
			}			
			
			//2. 수주주소정보변경
        	res = cChatBotChangeMapper.updateToOrdmasAddr(params);
        	
			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("updateToOrdmasAddr 오류 [" + res + "]");
				return gson.toJson(response);
			}			
			
			//3. TO_CHGMAS_ADDED insert
			
			//	3.1  수주변경요청 순번 가져오기
			dataResult = cChatBotChangeMapper.selectToChgmasMaxSeqNo(params);
			
			String req_seq = "";
			req_seq = dataResult.getData1() ;
			
			params.put("req_seq", req_seq);					

//        	res = cChatBotChangeMapper.insertToChgmasAdded(params);
//        	
//			if (res < 0) {
//				txManager.rollback(status);
//				response.setResultCode("5001");
//				response.setResultMessage("수주변경요청 마스터정보 추가 등록 오류 [" + res + "]");
//				return gson.toJson(response);
//			}			
			
			//4. 시공유무에 따라 변경 처리 추가
			
			dataResult = cChatBotChangeMapper.selectOrmCryn(params);
			
			String orm_cryn = "";
			orm_cryn = dataResult.getData1() ;
			
			if ("Y".equals(orm_cryn)) {
				
				//4-1. TC_RESMST 주소 변경 처리
				
	        	res = cChatBotChangeMapper.updateTcResmstAddr(params);
	        	
				if (res < 0) {
					txManager.rollback(status);
					response.setResultCode("5001");
					response.setResultMessage("updateTcResmstAddr 오류 [" + res + "]");
					return gson.toJson(response);
				}			
							
				//4-2. TC_PLANMST 주소 변경처리
				//TC_PLANMST 여부 확인
				
				dataResult = cChatBotChangeMapper.selectTcplanmstYn(params);
				
				String tcplanmst_yn = "";
				tcplanmst_yn = dataResult.getData1() ;
				
				if("Y".equals(tcplanmst_yn)) {
					
		        	res = cChatBotChangeMapper.updateTcPlanmstAddr(params);
		        	
					if (res < 0) {
						txManager.rollback(status);
						response.setResultCode("5001");
						response.setResultMessage("updateTcPlanmstAddr 오류 [" + res + "]");
						return gson.toJson(response);
					}			
				
				}
			
			}
			
			//5. to_chgmas 상태값 update
			
        	res = cChatBotChangeMapper.updateToChgmasChgAddrStat(params);
        	
			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("updateToChgmasChgAddrStat 오류 [" + res + "]");
				return gson.toJson(response);
			}			

			//대리점건인 경우 알람 메시지 insert 처리
			
			dataResult = cChatBotChangeMapper.selectOnlineynCheck(params);
			
			String online_yn = "";
			String orm_nm = "";			
			String message= "";
			
			online_yn = dataResult.getData1();
			orm_nm = dataResult.getData2();
			
			message = "챗봇을 통해 주소변경처리 되었습니다. (주문번호 : " + key_no + ",  수주건명 : " + orm_nm + " )";
					
			if(online_yn.equals("N")) {
				
				params.put("message", message);
				
				res = cChatBotChangeMapper.insertAgtChangeAlarm(params);
				
				if (res < 0) {
					txManager.rollback(status);
					response.setResultCode("5001");
					response.setResultMessage("insertAgtChangeAlarm 대리점알림메세지 insert 오류 [" + res + "]");
					return gson.toJson(response);
				}					
			}

			
	        params.put("call_function", "erp_OrderChangeAddressRequestProc");
	        params.put("function_name", "챗봇 주문 주소변경요청 처리");
	        response = apiCommonService.erp_callApiHistSave(params);

			
	    	JSONObject obj = new JSONObject();
	    	JSONArray jArray = new JSONArray(); //배열이 필요할때
	    	JSONArray jArray2 = new JSONArray(); //배열이 필요할때
	    	
	    	JSONObject api_token = new JSONObject();
	    	JSONObject sendList = new JSONObject();	    	 
	    	JSONObject sObject = new JSONObject();//배열 내에 들어갈 json
	    	JSONObject sObject2 = new JSONObject();//배열 내에 들어갈 json
	    	
	    	String templateCode = "";
	    	String senderkey = "";
	    	String title = "";
	    	String subject = "";
	    	String from_no = "";
	    	String message_type = "";
	    		        
	        //변경요청정보 기본 정보 가져오기	    	
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
	        
			title = "(CHATBOT)주소변경 AUTO처리안내";
			subject = "(CHATBOT)주소변경 AUTO처리안내";	      
    		chg_addrinfo = chg_addr +" "+ chg_addr_dtl ;
    		
			String chg_content = "";
			chg_content = "주소변경("+chg_addrinfo+")";
//	    	biztalkmessage = "(결제안내) 안녕하세요 "+orm_nm+" 고객님, 고객님의 결제하신 금액 " + "" +pay_amt3+ "원이 승인되었습니다.\r\n\r\n" +
//					 "1) 결제번호 : "+""+pay_no+"\r\n"+
//					 "2) 결제일자 : "+""+rem_dt+"";s	
			
			String pattern = "yyyy-MM-dd";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

			String date = simpleDateFormat.format(new Date());
			System.out.println(date);
		        
	        if(send_yn.equals("Y")) {
	        
	        	if(com_brd.equals("T60I01")) {
        		
	        		biztalkmessage = "안녕하세요.\r\n" +
	        					"귀 매장의 고객님이 일룸챗봇을 통해 아래와 같이 수주 변경을 진행하여 알림 드립니다.\r\n\r\n" +
	        					"수주변경 내용\r\n"+
	        					"수주번호 : "+key_no+"\r\n"+
	        					"수주건명 : "+org_orm_nm+"\r\n"+
	        					"요청내용 : "+chg_content+"\r\n"+
	        					"요청일자 : "+date+"\r\n\r\n"+
	        					"관련 내용 확인 부탁드립니다.\r\n"+
	        					"감사합니다.\r\n"
	        					;
	        					
	        		senderkey = "dbf8669a88dd7926fd653ff3ff9b23d331fbbb4c";
	        		from_no = "1577-5670";
	        		templateCode = "iloomchatbotorderchangeinform0";
	        		
	        	}else if(com_brd.equals("T60I02")) {
	        		
	        		biztalkmessage = "안녕하세요.\r\n" +
        					"귀 매장의 고객님이 데스커챗봇을 통해 아래와 같이 수주 변경을 진행하여 알림 드립니다.\r\n\r\n" +
        					"수주변경 내용\r\n"+
        					"수주번호 : "+key_no+"\r\n"+
        					"수주건명 : "+org_orm_nm+"\r\n"+
        					"요청내용 : "+chg_content+"\r\n"+
        					"요청일자 : "+date+"\r\n\r\n"+
        					"관련 내용 확인 부탁드립니다.\r\n"+
        					"감사합니다.\r\n"
        					;
		
	        		senderkey = "9917d09567d2ebf1acc89662d7f9ff10db1488d7";
	        		from_no = "1588-1662";
	        		templateCode = "deskerchatbotorderchginform0";
	        		
	        	}else if(com_brd.equals("T60I03")) {
	        		
	        		biztalkmessage = "안녕하세요.\r\n" +
        					"귀 매장의 고객님이 슬로우챗봇을 통해 아래와 같이 수주 변경을 진행하여 알림 드립니다.\r\n\r\n" +
        					"수주변경 내용\r\n"+
        					"수주번호 : "+key_no+"\r\n"+
        					"수주건명 : "+org_orm_nm+"\r\n"+
        					"요청내용 : "+chg_content+"\r\n"+
        					"요청일자 : "+date+"\r\n\r\n"+
        					"관련 내용 확인 부탁드립니다.\r\n"+
        					"감사합니다.\r\n"
        					;
	        		
	        		senderkey = "3ed320702f733d0b5a31e99a3ba931d9f2f9f960";
	        		from_no = "1899-8588";
	        		templateCode = "slouchatbotorderchangeinform0";	        		

	        	}else if(com_brd.equals("T60P01")) {
	        		
	        		biztalkmessage = "안녕하세요.\r\n" +
        					"귀 매장의 고객님이 시디즈챗봇을 통해 아래와 같이 수주 변경을 진행하여 알림 드립니다.\r\n\r\n" +
        					"수주변경 내용\r\n"+
        					"수주번호 : "+key_no+"\r\n"+
        					"수주건명 : "+org_orm_nm+"\r\n"+
        					"요청내용 : "+chg_content+"\r\n"+
        					"요청일자 : "+date+"\r\n\r\n"+
        					"관련 내용 확인 부탁드립니다.\r\n"+
        					"감사합니다.\r\n"
        					;
	        		
	        		senderkey = "6b94c758a1f689223024765ae6e2b0aede351955";
	        		from_no = "1577-5674";
	        		templateCode = "sidizchatbotorderchangeinform0";	  
	        		
	        	}else {
	        		
	        	}
	       	        	
		    	sendList.put("authKey", "D62D413F25CD43B3BD06636F2B3F570ABFB5008BD727901E341F041448D22C3A6593D58D45C68E60171F7FB2B2C345459361A08D20298BAE6A3A1B74196A95C3");
				
		       	 for (int i = 0; i < 1; i++)//배열
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
					response.setResultMessage("알림톡전송결과 오류  [" + kakao_res.getResultMessage() + "]");
					return gson.toJson(response);
				}
				
	        	if(com_brd.equals("T60I01")) {
					
			       	 for (int i = 0; i < 1; i++)//배열
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
						response.setResultMessage("알림톡전송결과 오류  [" + kakao_res2.getResultMessage() + "]");
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
	
	@ApiOperation(value = "erp_selectVndTel", notes = "대리점 전화번호 조회")
	@ApiResponses({ @ApiResponse(code = 200, message = "대리점 전화번호 조회 성공 !!"), @ApiResponse(code = 5001, message = "대리점 전화번호 조회 실패 !!") })
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
			
			//온라인 or 오프라인 여부 체크
		
			dataResult = cChatBotChangeMapper.selectVndTelNo(params);

	        params.put("call_function", "erp_selectVndTel");
	        params.put("function_name", "챗봇 대리점 전화번호 조회");
	        response = apiCommonService.erp_callApiHistSave(params);
	        
		} catch (Exception e) {
			
			response.setResultCode("5001");
			response.setResultMessage(e.toString());
			return gson.toJson(dataResult);
		}
		
		response.setResultCode("200");
		return gson.toJson(dataResult);
	}	

	@ApiOperation(value = "erp_AsChangeAddressRequestProc", notes = "AS 주소변경요청 처리")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK !!"), @ApiResponse(code = 5001, message = "AS 주소변경요청 처리 실패 !!") })
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
				response.setResultMessage("updateAsTarptreqChgAddr 오류 [" + res + "]");
				return gson.toJson(response);
			}	
			
			res = cChatBotChangeMapper.updateAsTcresmstChgAddr(params);

			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("updateAsTcresmstChgAddr 오류 [" + res + "]");
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
					response.setResultMessage("updateAsTaplanmstChgAddr 오류 [" + res + "]");
					return gson.toJson(response);
				}	
			
			}
			
			//AUTO처리정보 이력 테이블 insert
			params.put("chgreq_hp", "");
			params.put("chgreq_dt", "");
			params.put("chgreq_addr", chg_addr+ " " + chg_addr_dtl);
			params.put("reqorm_no", key_no);
			params.put("chgreq_std", "A96006");
			
			res = cChatBotChangeMapper.insertAsAutoChgReqInformation(params);
			
			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("insertAsAutoChgReqInformation 오류 [" + res + "]");
				return gson.toJson(response);
			}			
			
	        params.put("call_function", "erp_AsChangeAddressRequestProc");
	        params.put("function_name", "챗봇 AS 주소변경 처리");
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

	
	@ApiOperation(value = "erp_asCdtChangeAvailableYn", notes = "AS 날짜변경 가능여부 체크")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK !!"), @ApiResponse(code = 5001, message = "AS 날짜변경 가능여부 체크 실패 !!") })
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
			
            //날짜변경 땡길시 AS 불가능하도록 수정
			
			
			item = cChatBotChangeMapper.selectAsCdtChangeYn(params);
			//selectBdongCd
			
			if ( Integer.parseInt(req_dt) <= Integer.parseInt(rpt_enddt)) {
				item =  cChatBotChangeMapper.selectAsCdtChangeYn2(params);
			}
			
			if (item == null) {
//				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("selectAsCdtChangeYn 오류");
				return gson.toJson(item);
			}			
			
	        params.put("call_function", "erp_asCdtChangeAvailableYn");
	        params.put("function_name", "챗봇 AS 날짜변경 가능여부 체크");
	        response = apiCommonService.erp_callApiHistSave(params);	
	        
		} catch (Exception e) {
			
			response.setResultCode("5001");
			response.setResultMessage(e.toString());
			return gson.toJson(item);
		}
		
		response.setResultCode("200");
		return gson.toJson(item);
	}	
	
	@ApiOperation(value = "erp_asCdtAvailableList", notes = "AS 납기 변경 가능일자 리스트 조회")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK !!"), @ApiResponse(code = 5001, message = "AS 납기 변경 가능일자 리스트 조회 실패 !!") })
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
        params.put("function_name", "챗봇 AS 납기 변경 가능일자 리스트 조회");
        response = apiCommonService.erp_callApiHistSave(params);	
        		
		//		ArrayList<ERPOrderSearchVo> arList = cChatBotMapper.selectOrderSearchList(params);
		return gson.toJson(arList); 
		

	}
	
	@ApiOperation(value = "erp_orderPreviousChangeYn", notes = "기 주문변경건 체크")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK !!"), @ApiResponse(code = 5001, message = "기 주문변경건 체크 실패 !!") })
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
	        params.put("function_name", "챗봇 기 주문변경건 체크");
	        response = apiCommonService.erp_callApiHistSave(params);			
	        
		} catch (Exception e) {
			
			response.setResultCode("5001");
			response.setResultMessage(e.toString());
			return gson.toJson(item);
		}
		
		response.setResultCode("200");
		return gson.toJson(item);
	}	
	
	@ApiOperation(value = "erp_vendorTelInfo", notes = "대리점 전화번호 확인")
	@ApiResponses({ @ApiResponse(code = 200, message = "대리점 전화번호 확인 성공 !!"), @ApiResponse(code = 5001, message = "대리점 전화번호 확인 실패 !!") })
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
	        params.put("function_name", "챗봇 대리점 전화번호 확인");
	        response = apiCommonService.erp_callApiHistSave(params);	
	        
		} catch (Exception e) {
			
			response.setResultCode("5001");
			response.setResultMessage(e.toString());
			return gson.toJson(item);
		}
		
		response.setResultCode("200");
		return gson.toJson(item);
	}	


	@ApiOperation(value = "erp_orderCancelRequest", notes = "주문취소 요청")
	@ApiResponses({ @ApiResponse(code = 200, message = "주문취소 요청 성공 !!"), @ApiResponse(code = 5001, message = "주문취소 요청 처리 실패 !!") })
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
			
			//기존 수주 정보 불러오기
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
			
			//요청순번 불러오기 
			
			dataResult = cChatBotChangeMapper.selectChgReqSeq(params);
			
			String req_seq = "";
			req_seq = dataResult.getData1();
			
			params.put("req_seq", req_seq);
			
			res = cChatBotChangeMapper.insertOrderCancelMasterInsert(params);
			
			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("insertOrderCancelMasterInsert 오류 [" + res + "]");
				return gson.toJson(response);
			}	
			
			res = cChatBotChangeMapper.insertOrderCancelDetailInsert(params);
			
			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("insertOrderCancelDetailInsert 오류 [" + res + "]");
				return gson.toJson(response);
			}	
			
	        params.put("call_function", "erp_orderCancelRequest");
	        params.put("function_name", "챗봇 주문취소 요청");
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
	
	
	@ApiOperation(value = "erp_orderCancelRequestProc", notes = "주문 취소 요청 처리")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK !!"), @ApiResponse(code = 5001, message = "주문 주문 취소 요청 처리 실패 !!") })
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
			
			//수주마스터 취소
			
        	res = cChatBotChangeMapper.updateToOrdmasCancelStatus(params);
        	
			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("updateToOrdmasCancelStatus 오류 [" + res + "]");
				return gson.toJson(response);
			}			
			
			
			//시공건의 경우 시공 정보 삭제 (tc_resmst, tc_resdtl, tc_planmst, tc_plandtl)

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

				//tc_resdtl 삭제
				
				res = cChatBotChangeMapper.deleteTcresdtl(params);
				
				if (res < 0) {
					txManager.rollback(status);
					response.setResultCode("5001");
					response.setResultMessage("deleteTcres 오류 [" + res + "]");
					return gson.toJson(response);
				}	
				
				//tc_resmst 삭제
				res = cChatBotChangeMapper.deleteTcresmst(params);
				
				if (res < 0) {
					txManager.rollback(status);
					response.setResultCode("5001");
					response.setResultMessage("deleteTcresmst 오류 [" + res + "]");
					return gson.toJson(response);
				}				
								

				if("XXXX".equals(plm_no)) {
									
					
				} else {
					
					params.put("plm_no", plm_no);
					//tc_plandtl 삭제
					res = cChatBotChangeMapper.deleteTcplandtl(params);
					
					if (res < 0) {
						txManager.rollback(status);
						response.setResultCode("5001");
						response.setResultMessage("deleteTcplandtl 오류 [" + res + "]");
						return gson.toJson(response);
					}	
							
					//tc_planmst 삭제
					
					res = cChatBotChangeMapper.deleteTcplanmst(params);
					
					if (res < 0) {
						txManager.rollback(status);
						response.setResultCode("5001");
						response.setResultMessage("deleteTcplanmst 오류 [" + res + "]");
						return gson.toJson(response);
					}						
					
				}
					
			}
			
			//수주변경요청 취소 정보 삭제
			
        	res = cChatBotChangeMapper.updateToChgmasCancelStatus(params);
        	
			if (res < 0) {
				txManager.rollback(status);
				response.setResultCode("5001");
				response.setResultMessage("updateToChgmasCancelStatus 오류 [" + res + "]");
				return gson.toJson(response);
			}			
			
	        params.put("call_function", "erp_orderCancelRequestProc");
	        params.put("function_name", "챗봇 주문취소 요청 처리");
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

	@ApiOperation(value = "erp_orderasChangeRequest", notes = "주문/AS 변경 요청(확인 후 처리가능)")
	@ApiResponses({ @ApiResponse(code = 200, message = "주문/AS 변경 요청 성공 !!"), @ApiResponse(code = 5001, message = "주문/AS 변경 요청 실패 !!") })
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
				response.setResultMessage("insertOrderAsChgReqInformation 오류 [" + res + "]");
				return gson.toJson(response);
			}	
			
	        params.put("call_function", "erp_orderasChangeRequest");
	        params.put("function_name", "챗봇 주문/AS 변경 요청(확인 후 처리가능)");
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

	
	@ApiOperation(value = "erp_chgaddrAvailableCheck", notes = "주소변경가능여부체크")
	@ApiResponses({ @ApiResponse(code = 200, message = "주소변경가능여부체크 성공 !!"), @ApiResponse(code = 5001, message = "주소변경가능여부체크 실패 !!") })
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
				
				//1. 시공인 경우 
				//		센터가 변경되는 경우
				HashMap<String,Object> params = new HashMap<String, Object>();
				params.put("key_no",key_no);
				
				dataResult = cChatBotChangeMapper.selectOrgOrderInfo(params);
				
				//기존 수주 정보
				String orm_cryn = "";
				String org_comscd = "";
				
				orm_cryn = dataResult.getData1();
				org_comscd = dataResult.getData2();
				
				//운송비가 추가되는 경우
				String str_jejubdongcd = "";
				String org_jejubdongcd = "";
				String jeju_checkyn = "N";
				
				
				
				String str_ulleungbdongcd = "";
				String org_ulleungbdongcd = "";
				String ulleung_checkyn = "N";
				
				//시공인경우
				if(orm_cryn.equals("Y")) {
					
					//변경 센터정보
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
			        params.put("function_name", "챗봇 주소변경가능여부체크");
			        response = apiCommonService.erp_callApiHistSave(params);			
			        					
				//택배인경우	
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
		        params.put("function_name", "챗봇 주소변경가능여부체크");
		        response = apiCommonService.erp_callApiHistSave(params);			
		        
			//AS인경우	
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
		        params.put("function_name", "챗봇 주소변경가능여부체크");
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
	
	
	@ApiOperation(value = "erp_addRequestAvailableCheck", notes = "기변경요청건확인(배송중,배송준비중)")
	@ApiResponses({ @ApiResponse(code = 200, message = "기변경요청건확인(배송중,배송준비중) 성공 !!"), @ApiResponse(code = 5001, message = "기변경요청건확인(배송중,배송준비중) 실패 !!") })
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
	        params.put("function_name", "챗봇 기변경요청건확인(배송중,배송준비중)");
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
                System.out.println("발송 성공");
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
        	
            System.out.println("발송 성공");
            res.setResultCode("200");
            res.setResultMessage("");
        	
        	return res;
        }
    	
    	return res;
    }	
	
	
	@ApiOperation(value = "erp_arschatbotinformkakaosend", notes = "챗봇 알림메시지 유입 알림톡 발송")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK !!"), @ApiResponse(code = 5001, message = "챗봇 알림메시지 유입 알림톡 발송 실패 !!") })
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
	    	JSONArray jArray = new JSONArray(); //배열이 필요할때
	    	 
	    	JSONObject api_token = new JSONObject();
	    	JSONObject sendList = new JSONObject();	    	 
	    	JSONObject sObject = new JSONObject();//배열 내에 들어갈 json
	    	
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
	        
			title = "(CHATBOT)챗봇시작안내멘트";
			subject = "(CHATBOT)챗봇시작안내멘트";	      
    		
			String chg_content = "";
			chg_content = "연락처변경("+ctm_hp+")";
//	    	biztalkmessage = "(결제안내) 안녕하세요 "+orm_nm+" 고객님, 고객님의 결제하신 금액 " + "" +pay_amt3+ "원이 승인되었습니다.\r\n\r\n" +
//					 "1) 결제번호 : "+""+pay_no+"\r\n"+
//					 "2) 결제일자 : "+""+rem_dt+"";s	
			
		    Date today = new Date();
		        
		    SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");
	        
	        	if(com_brd.equals("T60I01")) {
        		
	        		biztalkmessage = "안녕하세요. 일룸 챗봇입니다.\r\n\r\n" +
	        					"시작하시려면 \"처음으로\" 라고 입력해 주세요."
	        					;
	        					
	        		senderkey = "dbf8669a88dd7926fd653ff3ff9b23d331fbbb4c";
	        		from_no = "1577-5670";
	        		templateCode = "iloomArsCallChatbotRequest";
	        		
	        	}else if(com_brd.equals("T60I02")) {
	        		
	        		biztalkmessage = "안녕하세요. 데스커 챗봇입니다.\r\n\r\n" +
        					"시작하시려면 \"처음으로\" 라고 입력해 주세요."
        					;
		
	        		senderkey = "9917d09567d2ebf1acc89662d7f9ff10db1488d7";
	        		from_no = "1588-1662";
	        		templateCode = "deskerArsCallChatbotRequest";
	        		
	        	}else if(com_brd.equals("T60I03")) {
	        		
	        		biztalkmessage = "안녕하세요. 슬로우 챗봇입니다.\r\n\r\n" +
        					"시작하시려면 \"처음으로\" 라고 입력해 주세요."
        					;
	        		
	        		senderkey = "3ed320702f733d0b5a31e99a3ba931d9f2f9f960";
	        		from_no = "1899-8588";
	        		templateCode = "slouArsCallChatbotRequest";	        		

	        	}else if(com_brd.equals("T60P01")) {
	        		
	        		biztalkmessage = "안녕하세요. 시디즈 챗봇입니다.\r\n\r\n" +
        					"시작하시려면 \"처음으로\" 라고 입력해 주세요."
        					;
	        		
	        		senderkey = "6b94c758a1f689223024765ae6e2b0aede351955";
	        		from_no = "1577-5674";
	        		templateCode = "sidizArsCallChatbotRequest";	  
	        		
	        	}else {
	        		
	        	}
	       	        	
		    	sendList.put("authKey", "D62D413F25CD43B3BD06636F2B3F570ABFB5008BD727901E341F041448D22C3A6593D58D45C68E60171F7FB2B2C345459361A08D20298BAE6A3A1B74196A95C3");
				
		       	 for (int i = 0; i < 1; i++)//배열
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
					response.setResultMessage("알림톡전송결과 오류  [" + kakao_res.getResultMessage() + "]");
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
	
