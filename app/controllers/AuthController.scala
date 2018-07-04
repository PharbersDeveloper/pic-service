package controllers

import bmmessages._
import play.api.libs.json.Json.toJson
import play.api.mvc._
import javax.inject._

import akka.actor.ActorSystem
import bminjection.db.DBTrait
import bminjection.token.AuthTokenTrait
import bmlogic.auth.AuthMessage._
import bmlogic.common.requestArgsQuery
import bmpattern.LogMessage.msg_log
import bmpattern.ResultMessage.msg_CommonResultMessage
import bmutil.logging.PharbersLog

class AuthController @Inject () (as_inject : ActorSystem, dbt : DBTrait, att : AuthTokenTrait, plog : PharbersLog) extends Controller {
    implicit val as = as_inject

    def authPushUser = Action (request => requestArgsQuery().requestArgsV2(request) { jv =>
            import bmpattern.LogMessage.common_log
            import bmpattern.ResultMessage.common_result
            MessageRoutes(msg_log(toJson(Map("method" -> toJson("push user"))), jv)
               :: msg_AuthPushUser(jv) :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "plog" -> plog))))
        })

    def authWithPassword = Action (request => requestArgsQuery().requestArgsV2(request) { jv =>
            import bmpattern.LogMessage.common_log
            import bmpattern.ResultMessage.common_result
			MessageRoutes(msg_log(toJson(Map("method" -> toJson("auth with password"))), jv)
                :: msg_AuthWithPassword(jv) :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att, "plog" -> plog))))
        })

    def authTokenCheckTest = Action (request => requestArgsQuery().requestArgsV2(request) { jv =>
            import bmpattern.LogMessage.common_log
            import bmpattern.ResultMessage.common_result
            MessageRoutes(msg_log(toJson(Map("method" -> toJson("auth check"))), jv)
                :: msg_AuthTokenParser(jv) :: msg_CheckAuthTokenTest(jv) :: msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
        })
  
    def authChangePwd = Action (Ok(""))
    def authUpdate = Action (Ok(""))
    def authLogout = Action (Ok(""))
}