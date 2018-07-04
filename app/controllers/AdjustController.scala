package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import bminjection.db.DBTrait
import bminjection.token.AuthTokenTrait
import bmlogic.adjustdata.AdjustDataMessage.msg_AdjustNameDataCommand
import bmlogic.common.requestArgsQuery
import bmmessages.{CommonModules, MessageRoutes}
import bmpattern.LogMessage.msg_log
import bmpattern.ResultMessage.msg_CommonResultMessage
import play.api.mvc._
import play.api.libs.json.Json.toJson

/**
  * Created by jeorch on 17-6-13.
  */
class AdjustController @Inject () (as_inject : ActorSystem, dbt : DBTrait, att : AuthTokenTrait) extends Controller {
    implicit val as = as_inject

    def adjustNameData = Action (request => requestArgsQuery().requestArgsV2(request) { jv =>
        import bmpattern.LogMessage.common_log
        import bmpattern.ResultMessage.common_result
        MessageRoutes(msg_log(toJson(Map("method" -> toJson("adjust name command"))), jv)
            :: msg_AdjustNameDataCommand(jv) :: msg_CommonResultMessage()
            :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
    })
}
