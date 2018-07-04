package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import bminjection.db.DBTrait
import bminjection.token.AuthTokenTrait
import bmlogic.aggregateCalc.AggregateCalcMessage._
import bmlogic.auth.AuthMessage._
import bmlogic.common.requestArgsQuery
import bmlogic.retrieval.RetrievalMessage._
import bmmessages.{CommonModules, MessageRoutes}
import bmpattern.LogMessage.msg_log
import bmpattern.ResultMessage.msg_CommonResultMessage
import play.api.libs.json.Json.toJson
import play.api.mvc.{Action, Controller}

/**
  * Created by alfredyang on 06/06/2017.
  */
class RetrievalController @Inject () (as_inject : ActorSystem, dbt : DBTrait, att : AuthTokenTrait) extends Controller {
    implicit val as = as_inject

    def conditionSearch = Action (request => requestArgsQuery().requestArgsV2(request) { jv =>
        import bmpattern.LogMessage.common_log
        import bmpattern.ResultMessage.lst_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("condition search"))), jv)
            :: msg_AuthTokenParser(jv) :: msg_CheckTokenExpire(jv) :: msg_CheckProductLevelScope(jv)
            :: msg_CheckEdgeScope(jv) :: msg_CheckManufactureNameScope(jv) :: msg_ConditionSearchCommand(jv)
            :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
    })

    def calcMarket = Action (request => requestArgsQuery().requestArgsV2(request) { jv =>
        import bmpattern.LogMessage.common_log
        import bmpattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("calc market size"))), jv)
            :: msg_AuthTokenParser(jv) :: msg_CheckTokenExpire(jv) :: msg_CheckProductLevelScope(jv)
            :: msg_CheckEdgeScope(jv) :: msg_CheckManufactureNameScope(jv) :: msg_CalcMarketSize(jv)
            :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
    })

    def calcTrend = Action (request => requestArgsQuery().requestArgsV2(request) { jv =>
        import bmpattern.LogMessage.common_log
        import bmpattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("calc trend"))), jv)
            :: msg_AuthTokenParser(jv) :: msg_CheckTokenExpire(jv)
            :: msg_CheckProductLevelScope(jv) :: msg_CheckEdgeScope(jv) :: msg_CheckManufactureNameScope(jv)
            :: msg_CalcTrend(jv) :: msg_CalcTrend_Mat(jv) :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
    })

    def calcPercentage = Action (request => requestArgsQuery().requestArgsV2(request) { jv =>
        import bmpattern.LogMessage.common_log
        import bmpattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("calc percentage"))), jv) :: msg_AuthTokenParser(jv)
            :: msg_CheckTokenExpire(jv) :: msg_CheckProductLevelScope(jv)
            :: msg_CheckEdgeScope(jv) :: msg_CheckManufactureNameScope(jv)
            :: msg_CalcPercentage(jv) :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
    })
    
    def calcQuantity = Action (request => requestArgsQuery().requestArgsV2(request) { jv =>
        import bmpattern.LogMessage.common_log
        import bmpattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("calc quantity"))), jv)
            :: msg_AuthTokenParser(jv) :: msg_CheckTokenExpire(jv) :: msg_CheckProductLevelScope(jv)
            :: msg_CheckEdgeScope(jv) :: msg_CheckManufactureNameScope(jv)
            :: msg_ProductQuantity(jv) :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
    })

    def calcUnits = Action(Ok(""))
    def dataReports = Action(Ok(""))
}
