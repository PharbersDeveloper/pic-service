package bmlogic.loginLog

import java.text.SimpleDateFormat
import java.util.Date

import bminjection.db.DBTrait
import bmlogic.common.sercurity.Sercurity
import bmlogic.loginLog.LogData.LogData
import bmlogic.loginLog.LoginLogMessage.{msg_loginLog_import, msg_loginLog_query, msg_loginLog_save}
import bmmessages.{CommonModules, MessageDefines}
import bmpattern.ModuleTrait
import bmutil.errorcode.ErrorCode
import com.mongodb.casbah.Imports._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

import scala.collection.immutable.Map

/**
  * Created by jeorch on 17-6-27.
  */
object LoginLogModule extends ModuleTrait with LogData {

    def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]])(implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_loginLog_query(data) => query_log_func(data)
        case msg_loginLog_import(data) => import_log_func(data)
        case msg_loginLog_save(data) => save_log_func(data)

        case _ => ???
    }

    def query_log_func(data: JsValue)(implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            val db = cm.modules.get.get("db").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
            val logList=db.queryMultipleObject(DBObject(),"login_logs").map(x=>x)
            if (logList.isEmpty) throw new Exception("unknown error")
            else {
                val record=logList.length
                val pages=record/10+1
                (Some(Map(
                    "serialNum" -> toJson(0),
                    "pageIndex" -> toJson(1),
                    "pageSize" -> toJson(10),
                    "results" -> toJson(logList),
                    "totalPage" -> toJson(pages),
                    "totalRecord" -> toJson(record)
                )), None)
            }

        } catch {
            case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }

    def import_log_func(data: JsValue)(implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            val db = cm.modules.get.get("db").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
            val user_name = (data \ "user_name").asOpt[String].map (x => x).getOrElse(throw new Exception("input error"))
            val ip = (data \ "ip").asOpt[String].map (x => x).getOrElse(throw new Exception("input error"))
            val o : DBObject = condition(data)
            val new_date = new Date().getTime.asInstanceOf[Number]

            val i : Int = 0
            o += "log_id" -> Sercurity.md5Hash(user_name + ip + Sercurity.getTimeSpanWithMillSeconds)
            o += "date" -> new_date
            o += "login_end" -> new_date
            o += "time_sum" -> i.asInstanceOf[Number]
            db.insertObject(o,"login_logs","log_id")
            (Some(Map(
                "log_id" -> toJson(o.get("log_id").toString)
            )),None)

        } catch {
            case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }

    def save_log_func(data: JsValue)(implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val db = cm.modules.get.get("db").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
            val log_id = (data \ "log_id").asOpt[String].map (x => x).getOrElse(throw new Exception("input error"))
            val log = db.queryObject(DBObject("log_id" -> log_id),"login_logs")
            val o = m2d(toJson(log.get))

            val date = sdf.parse((toJson(log.get) \ "date").get.asOpt[String].get).getTime

            val new_date = new Date().getTime

            val i : Int = (new_date - date).asInstanceOf[Int]
            o += "date" ->  date.asInstanceOf[Number]
            o += "login_end" -> new_date.asInstanceOf[Number]
            o += "time_sum" -> i.asInstanceOf[Number]
            db.updateObject(o,"login_logs","log_id")
            (Some(Map(
                "login_end" -> toJson(new Date())
            )),None)

        } catch {
            case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }

}
