package com.fursys.chatbot.vo.erp;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ERPOrderSearchVo {
	
	private String orm_nm = "";
	private String key_no = "";
	private String orm_orddt = "";
	private String orm_cdt = "";
	private String orm_gaddr = "";
	private String order_type = "";
	private String agt_nm = "";
}


