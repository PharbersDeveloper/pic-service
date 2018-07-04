package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import bminjection.db.DBTrait
import bminjection.token.AuthTokenTrait
import bmlogic.auth.AuthMessage.{msg_AuthTokenParser, msg_CheckAuthSampleScope}
import bmlogic.common.requestArgsQuery
import bmlogic.sampleData.SampleDataMessage.{msg_rawData2Sample, msg_readRawData}
import bmmessages.{CommonModules, MessageRoutes}
import bmpattern.LogMessage.msg_log
import bmpattern.ResultMessage.msg_CommonResultMessage
import play.api.libs.json.Json.toJson
import play.api.mvc.{Action, Controller}

/**
  * Created by yym on 7/27/17.
  */
class DBDataController @Inject ()(as_inject : ActorSystem, dbt : DBTrait, att : AuthTokenTrait) extends Controller{
    implicit val as = as_inject
    
    def InsertRawData = Action (request => requestArgsQuery().requestArgsV2(request) { jv =>
        import bmpattern.LogMessage.common_log
        import bmpattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("Insert raw data"))), jv)
            ::msg_rawData2Sample(jv)::msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
    })
    
    def ReadRawData = Action (request => requestArgsQuery().requestArgsV2(request) { jv =>
        import bmpattern.LogMessage.common_log
        import bmpattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("read raw data"))), jv)
            ::msg_AuthTokenParser(jv)::msg_CheckAuthSampleScope(jv)::msg_readRawData(jv)::msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
    })
    
}
