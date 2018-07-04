package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import bminjection.db.DBTrait
import bminjection.token.AuthTokenTrait
import play.api.mvc.{Action, Controller}

/**
  * Created by alfredyang on 06/06/2017.
  */
class AdminController_old @Inject () (as_inject : ActorSystem, dbt : DBTrait, att : AuthTokenTrait) extends Controller {
    implicit val as = as_inject

    def adminWithPassword = Action(Ok(""))
    def adminChangeScope = Action(Ok(""))
    def adminUserLst = Action(Ok(""))
}
