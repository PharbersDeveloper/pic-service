package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import bminjection.db.DBTrait
import bminjection.token.AuthTokenTrait
import bmlogic.common.requestArgsQuery
import bmlogic.config.ConfigMessage.{msg_QueryInfoCommand}
import bmmessages.{CommonModules, MessageRoutes}
import bmpattern.LogMessage.msg_log
import bmpattern.ResultMessage.msg_CommonResultMessage
import play.api.libs.json.Json.toJson
import play.api.mvc.{Action, Controller}

/**
  * Created by yym on 6/12/17.
  */
class ConfigController @Inject () (as_inject : ActorSystem, dbt : DBTrait, att : AuthTokenTrait) extends Controller{
    implicit val as = as_inject
    //首页侧栏展示
    def showConfig = Action (request => requestArgsQuery().requestArgsV2(request) { jv =>
        import bmpattern.LogMessage.common_log
        import bmpattern.ResultMessage.common_result
        
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("show config"))), jv)
            ::msg_QueryInfoCommand(jv)::msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
    })
}
