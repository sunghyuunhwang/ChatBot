package com.fursys.chatbot.mapper;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.ibatis.annotations.Mapper;

import com.fursys.chatbot.vo.erp.ERPBdongCdVo;
import com.fursys.chatbot.vo.erp.ERPOrderCdtChangeAutoYn;
import com.fursys.chatbot.vo.erp.ERPOrderChangeAvailableDt;
import com.fursys.chatbot.vo.erp.ERPOrderFinalAvailableDate;
import com.fursys.chatbot.vo.erp.ERPOrderSearchVo;
import com.fursys.chatbot.vo.erp.ERPOrmAvailableYn;
import com.fursys.chatbot.vo.erp.ERPSameOrderYn;
import com.fursys.chatbot.vo.DataResult;

@Mapper
public interface ChatBotChangeMapper {
	
	public DataResult executePraFaChgreqdt(HashMap<String,Object> params);
	public int updateAsHpChange(HashMap<String,Object> params);
	public DataResult selectAsDateConfirmYn(HashMap<String,Object> params);
	public DataResult selectAsChulgoYn(HashMap<String,Object> params);
	public int updateOrderHpChange(HashMap<String,Object> params);
	public DataResult selectSigongYn(HashMap<String,Object> params);
	public DataResult selectSchDivYn(HashMap<String,Object> params);
	public DataResult selectSigongConfirmYn(HashMap<String,Object> params);
	public DataResult selectFinishYn(HashMap<String,Object> params);
	public DataResult selectChgstatusYn(HashMap<String,Object> params);
	public int insertOrmCdtChangeReq(HashMap<String,Object> params);
	public ERPOrderFinalAvailableDate executePraMindateZipChatbot(HashMap<String,Object> params);
	public DataResult selectOrmZipCd(HashMap<String,Object> params);
	public DataResult selectOrmVndCd(HashMap<String,Object> params);
	public DataResult selectOrmComScd(HashMap<String,Object> params);
	public DataResult selectOrmBdongCd(HashMap<String,Object> params);
	public DataResult selectOrmStiCd(HashMap<String,Object> params);
	public ERPOrderFinalAvailableDate executePraDayInfChatbot(HashMap<String,Object> params);
	public ERPBdongCdVo selectBdongCd(HashMap<String,Object> params);
	public DataResult selectToChgmasMaxSeqNo(HashMap<String,Object> params);
	public ERPOrderCdtChangeAutoYn executeProRenAutoBlock(HashMap<String,Object> params);
	

	public DataResult executeProGetNo(HashMap<String,Object> params);
	public DataResult selectToChgmasChgdt(HashMap<String,Object> params);
	public int insertNewOrdMaster(HashMap<String,Object> params);
	public int insertNewOrdDetail(HashMap<String,Object> params);
	public int deleteRecdRenD(HashMap<String,Object> params);
	public int insertRecdRenD(HashMap<String,Object> params);
	public int updateToOrdChgmasStat(HashMap<String,Object> params);
	public int updateToOrdmasStat(HashMap<String,Object> params);
	public int insertToChgmasAdded(HashMap<String,Object> params);
	public DataResult selectOrmCryn(HashMap<String,Object> params);
	public DataResult selectOrmGpost(HashMap<String,Object> params);
	public DataResult selectSigongReserveYn(HashMap<String,Object> params);
	public DataResult selectRemdtRemseq(HashMap<String,Object> params);
	public DataResult selectTcMaxSeqNo(HashMap<String,Object> params);
	public int insertTcSeqnoinf(HashMap<String,Object> params);
	public int updateTcresdtl(HashMap<String,Object> params);
	public int updateTcresmst(HashMap<String,Object> params);
	public int updateTcplanmst(HashMap<String,Object> params);
	public int updateToOrdmasSigongStat(HashMap<String,Object> params);
	public DataResult selectOnlineYn(HashMap<String,Object> params);
	public ERPSameOrderYn selectOnlineSameOrderYn(HashMap<String,Object> params);
	public ERPSameOrderYn selectOfflineSameOrderYn(HashMap<String,Object> params);
	
	public int insertChgAddrReq(HashMap<String,Object> params);
	public int updateToOrdmasAddr(HashMap<String,Object> params);
	public int updateTcResmstAddr(HashMap<String,Object> params);
	public int updateTcPlanmstAddr(HashMap<String,Object> params);
	public DataResult selectTcplanmstYn(HashMap<String,Object> params);
	public int updateToChgmasChgAddrStat(HashMap<String,Object> params);
	public DataResult selectVndTelNo(HashMap<String,Object> params);
	
	public int updateAsTarptreqChgAddr(HashMap<String,Object> params);
	public int updateAsTcresmstChgAddr(HashMap<String,Object> params);
	public int updateAsTaplanmstChgAddr(HashMap<String,Object> params);
	public DataResult selectTaplanmstYn(HashMap<String,Object> params);
	
	public DataResult selectIloompreenposymd(HashMap<String,Object> params);
	public DataResult selectSigongReserveInfo(HashMap<String,Object> params);
	public DataResult selectProcDayinf(HashMap<String,Object> params);
	public DataResult selectGetComScd(HashMap<String,Object> params);
	public DataResult selectJundamYn(HashMap<String,Object> params);
	public DataResult selectGetNormalStiCd(HashMap<String,Object> params);
	public DataResult selectJundamStiCd(HashMap<String,Object> params);
	public DataResult selectPrcDayinfChatbot(HashMap<String,Object> params);
	
	public DataResult selectOrmAdtCheck(HashMap<String,Object> params);
	public DataResult selectWorkingYnCheck(HashMap<String,Object> params);
	
	public ERPOrmAvailableYn selectAsCdtChangeYn(HashMap<String,Object> params);
	public ERPOrmAvailableYn selectAsCdtChangeYn2(HashMap<String,Object> params);
	public DataResult selectAsInfo(HashMap<String,Object> params);
	public ArrayList<ERPOrderChangeAvailableDt> selectAsAvailableCdtList(HashMap<String,Object> params);
	
	public DataResult selectOriginOrderInfoSelect(HashMap<String,Object> params);
	public int insertOrderCancelMasterInsert(HashMap<String,Object> params);
	public int insertOrderCancelDetailInsert(HashMap<String,Object> params);
	public DataResult selectChgReqSeq(HashMap<String,Object> params);
	
	public int updateToOrdmasCancelStatus(HashMap<String,Object> params);
	public DataResult selectSigongReserveData(HashMap<String,Object> params);
	public int deleteTcresmst(HashMap<String,Object> params);
	public int deleteTcresdtl(HashMap<String,Object> params);
	public int deleteTcplandtl(HashMap<String,Object> params);
	public int deleteTcplanmst(HashMap<String,Object> params);
	public int updateToChgmasCancelStatus(HashMap<String,Object> params);
	
	public int insertOrderAsChgReqInformation(HashMap<String,Object> params);
	
	public DataResult selectOrgOrderInfo(HashMap<String,Object> params);
	public DataResult selectChgOrderInfo(HashMap<String,Object> params);
	
	public DataResult selectOrgAsInfo(HashMap<String,Object> params);
	public DataResult selectPreviousChgreqYn(HashMap<String,Object> params);
	
	public int insertAsAutoChgReqInformation(HashMap<String,Object> params);
	
	public DataResult selectOnlineynCheck(HashMap<String,Object> params);
	public int insertAgtChangeAlarm(HashMap<String,Object> params);
	
	public DataResult selectChginformData(HashMap<String,Object> params);
}


