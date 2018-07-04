package bmlogic.loginLog.LogData

import java.text.SimpleDateFormat
import java.util.Date

import com.mongodb.casbah.Imports._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

/**
  * Created by jeorch on 17-7-6.
  */
trait LogData {
    val condition : JsValue => DBObject = { js =>
        val build = MongoDBObject.newBuilder
        build += "user_name" -> (js \ "user_name").asOpt[String].map (x => x).getOrElse(throw new Exception("read user_name error"))
        build += "ip" -> (js \ "ip").asOpt[String].map (x => x).getOrElse(throw new Exception("read ip error"))
        (js \ "date").asOpt[String].map (x => x).getOrElse(Unit)
        (js \ "login_end").asOpt[String].map (x => x).getOrElse(Unit)
        (js \ "time_sum").asOpt[Int].map (x => x).getOrElse(Unit)

        build.result
    }

    implicit val m2d : JsValue => DBObject = { js =>
        val build = MongoDBObject.newBuilder
        build += "user_name" -> (js \ "user_name").asOpt[String].map (x => x).getOrElse(throw new Exception("read user_name error"))
        build += "log_id" -> (js \ "log_id").asOpt[String].map (x => x).getOrElse(throw new Exception("read user_name error"))
        build += "date" -> (js \ "date").asOpt[String].map (x => x).getOrElse(throw new Exception("read date error"))
//        build += "login_start" -> sdf.parse((js \ "login_start").get.asOpt[String].get).getTime.asInstanceOf[Number]
        build += "login_end" -> (js \ "login_end").asOpt[String].map (x => x).getOrElse(throw new Exception("read login_end error"))
        build += "time_sum" -> (js \ "time_sum").asOpt[String].map (x => x).getOrElse(throw new Exception("read time_sum error"))
        build += "ip" -> (js \ "ip").asOpt[String].map (x => x).getOrElse(throw new Exception("read ip error"))
        build.result
    }

    // for query
    implicit val d2m : DBObject => Map[String, JsValue] = { obj =>
        val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        Map(
            "user_name" -> toJson(obj.getAs[String]("user_name").map (x => x).getOrElse(throw new Exception("log user_name db prase error"))),
            "log_id" -> toJson(obj.getAs[String]("log_id").map (x => x).getOrElse(throw new Exception("log_id db prase error"))),
            "date" -> toJson(obj.getAs[Number]("date").map (x => sdf.format(new Date(x.longValue()))).getOrElse(throw new Exception("log date db prase error"))),
//            "login_end" -> toJson(obj.getAs[Number]("login_end").map (x => sdf.format(new Date(x.longValue()))).getOrElse(throw new E"login_start" -> toJson(obj.getAs[Number]("login_start").map (x => sdf.format(new Date(x.longValue()))).getOrElse(throw new Exception("db prase error"))),
//            "date" -> toJson(obj.getAs[Long]("date").map (x => x).getOrElse(throw new Exception("db prase error"))),
            "login_end" -> toJson(obj.getAs[Long]("login_end").map (x => sdf.format(new Date(x.longValue()))).getOrElse(throw new Exception("log login_end db prase error"))),
            "time_sum" -> toJson(obj.getAs[Int]("time_sum").map (x => int2String(x)).getOrElse(throw new Exception("log time_sum db prase error"))),
            "ip" -> toJson(obj.getAs[String]("ip").map (x => x).getOrElse(throw new Exception("log ip db prase error")))
        )
    }

    def int2String(i : Int): String = i match {

        case _ if i==0 => "不明确"
        case _ if i<60000 => "小于1分钟"
        case _ if i>1800000 => "大于30分钟"
        case _ => i/60000+"分钟"
    }

}
