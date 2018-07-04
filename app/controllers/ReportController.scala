package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import bminjection.db.DBTrait
import bminjection.token.AuthTokenTrait
import bmlogic.auth.AuthMessage._
import bmlogic.common.requestArgsQuery
import bmlogic.report.ReportMessage._
import bmmessages.{CommonModules, MessageRoutes}
import bmpattern.LogMessage.msg_log
import bmpattern.ResultMessage.msg_CommonResultMessage
import play.api.libs.json.Json.toJson
import play.api.mvc.{Action, Controller}

/**
  * Created by qianpeng on 2017/6/20.
  */
class ReportController @Inject () (as_inject : ActorSystem, dbt : DBTrait, att : AuthTokenTrait) extends Controller {
	implicit val as = as_inject
	
	def report = Action (request => requestArgsQuery().requestArgsV2(request) { jv =>
		import bmpattern.LogMessage.common_log
		import bmpattern.ResultMessage.lst_result
		
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("report"))), jv)
			:: msg_AuthTokenParser(jv) :: msg_CheckTokenExpire(jv) :: msg_CheckProductLevelScope(jv)
			:: msg_CheckEdgeScope(jv) :: msg_CheckManufactureNameScope(jv)
			:: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
	})
	
	/**
	  * 查询参数入库
	  */
	def reportparameter = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
		import bmpattern.LogMessage.common_log
		import bmpattern.ResultMessage.lst_result
		
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("reportparameter"))), jv)
			:: msg_AuthTokenParser(jv) :: msg_CheckTokenExpire(jv) :: msg_InertParameterCommand(jv)
			:: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
	})
	
	
	/**
	  * 查询报告参数
	  */
	def queryreportparameter = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
		import bmpattern.LogMessage.common_log
		import bmpattern.ResultMessage.lst_result
		
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("queryreportparameter"))), jv)
			:: msg_AuthTokenParser(jv) :: msg_CheckTokenExpire(jv) :: msg_ReportParameterSummary(jv)
			:: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
	})
	
	/**
	  * 计算报告部分的数据
	  */
	def reportcalcsummary = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
		import bmpattern.LogMessage.common_log
		import bmpattern.ResultMessage.lst_result
		
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("reportparameter"))), jv)
			:: msg_AuthTokenParser(jv) :: msg_CheckTokenExpire(jv) :: msg_CheckProductLevelScope(jv)
			:: msg_ReportGraph_One(jv)
			:: msg_ReportGraph_Four(jv) :: msg_ReportGraph_Five(jv)
//			:: msg_ReportTable_Sales(jv) :: msg_ReportTable_ProductUnitCount(jv) :: msg_ReportTable_Calc(jv)
//			:: msg_ReportTable_One(jv)
			:: msg_ReportGraph_Six(jv) :: msg_ReportGraph_Eight(jv) :: msg_ReportGraph_Seven(jv)
			:: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
	})
	

	
}
