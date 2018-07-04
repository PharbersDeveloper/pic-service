package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import bminjection.db.DBTrait
import bminjection.token.AuthTokenTrait
import bmlogic.common.requestArgsQuery
import bmlogic.loginLog.LoginLogMessage.{msg_loginLog_query,msg_loginLog_import,msg_loginLog_save}
import bmmessages.{CommonModules, MessageRoutes}
import bmpattern.LogMessage.msg_log
import bmpattern.ResultMessage.msg_CommonResultMessage
import play.api.libs.json.Json.toJson
import play.api.mvc.{Action, Controller}

/**
  * Created by jeorch on 17-7-6.
  */
class AdminController @Inject ()(as_inject : ActorSystem, dbt : DBTrait, att : AuthTokenTrait) extends Controller {
    implicit val as = as_inject

    def adminWithPassword = Action(Ok(""))
    def adminChangeScope = Action(Ok(""))
    def adminUserLst = Action(Ok(""))

    def loginLogQuery = Action(request => requestArgsQuery().requestArgsV2(request) { jv =>
        import bmpattern.LogMessage.common_log
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("queryUsers"))), jv)
            :: msg_loginLog_query(jv) :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
    })

    def loginLogImport = Action (request => requestArgsQuery().requestArgsV2(request) { jv =>
        import bmpattern.LogMessage.common_log
        import bmpattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("auth with password"))), jv)
            :: msg_loginLog_import(jv) :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
    })

    def loginLogSave = Action (request => requestArgsQuery().requestArgsV2(request) { jv =>
        import bmpattern.LogMessage.common_log
        import bmpattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("auth with password"))), jv)
            :: msg_loginLog_save(jv) :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
    })
}
