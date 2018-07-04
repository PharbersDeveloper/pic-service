package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import bminjection.db.DBTrait
import bminjection.token.AuthTokenTrait
import bmlogic.category.CategoryMessage.{msg_Category, msg_LinkageCategory}
import bmlogic.common.requestArgsQuery
import bmmessages.{CommonModules, MessageRoutes}
import bmpattern.LogMessage.msg_log
import bmpattern.ResultMessage.msg_CommonResultMessage
import play.api.libs.json.Json.toJson
import play.api.mvc.{Action, Controller}

/**
  * Created by qianpeng on 2017/6/15.
  */
class CategoryController @Inject () (as_inject : ActorSystem, dbt : DBTrait, att : AuthTokenTrait) extends Controller {
	implicit val as = as_inject
	def category = Action (request => requestArgsQuery().requestArgsV2(request) { jv =>
		import bmpattern.LogMessage.common_log
		import bmpattern.ResultMessage.common_result
		
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("show category"))), jv)
			::msg_Category(jv)::msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
	})
	
	def categoryLinkage = Action (request => requestArgsQuery().requestArgsV2(request) { jv =>
		import bmpattern.LogMessage.common_log
		import bmpattern.ResultMessage.common_result
		
		MessageRoutes(msg_log(toJson(Map("method" -> toJson("show categoryLinkage"))), jv)
			::msg_LinkageCategory(jv)::msg_CommonResultMessage() :: Nil, None)(CommonModules(Some(Map("db" -> dbt, "att" -> att))))
	})
	
}
