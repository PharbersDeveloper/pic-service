package bmlogic.userManage.UserData

import java.text.SimpleDateFormat
import java.util.Date

import bmlogic.auth.AuthScopes.AuthScope
import com.mongodb.casbah.Imports._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

/**
  * Created by jeorch on 17-6-29.
  */
trait UserData extends AuthScope{

    val condition : JsValue => DBObject = { js =>
        val build = MongoDBObject.newBuilder
        (js \ "user_name").asOpt[String].map (x => x).getOrElse(Unit)
        (js \ "pwd").asOpt[String].map (x => x).getOrElse(Unit)
        (js \ "user_id").asOpt[String].map (x => x).getOrElse(Unit)
        (js \ "screen_name").asOpt[String].map (x => x).getOrElse(Unit)
        (js \ "screen_photo").asOpt[String].map (x => x).getOrElse(Unit)
        (js \ "phoneNo").asOpt[String].map (x => x).getOrElse(Unit)
        (js \ "email").asOpt[String].map (x => x).getOrElse(Unit)

        val scope_builder = MongoDBObject.newBuilder
        scope_builder += "edge" -> pushEdgeScope(js)
        scope_builder += "category" -> pushProduceLevelScope(js)
        scope_builder += "manufacture_name" -> pushManufactureNameScope(js)
        scope_builder += "is_admin" -> (js \ "scope" \ "is_admin").asOpt[Int].map (x => x).getOrElse(0)
        scope_builder += "sample" ->(js \ "scope" \ "sample").asOpt[Int].map(x => x).getOrElse(0)
        
        build += "scope" -> scope_builder.result

        build += "status" -> (js \ "status").asOpt[Int].map (x => x).getOrElse("")

        build.result
    }

    implicit val m2d : JsValue => DBObject = { js =>
        val build = MongoDBObject.newBuilder
        val user_name = (js \ "user_name").asOpt[String].map (x => x).getOrElse(throw new Exception("input error"))
        val pwd = (js \ "pwd").asOpt[String].map (x => x).getOrElse(throw new Exception("input error"))
        build += "user_name" -> user_name
        build += "pwd" -> pwd

        //        build += "user_id" -> (js \ "user_id").asOpt[String].map (x => x).getOrElse("")
        build += "screen_name" -> (js \ "screen_name").asOpt[String].map (x => x).getOrElse("")
        build += "screen_photo" -> (js \ "screen_photo").asOpt[String].map (x => x).getOrElse("")
        build += "phoneNo" -> (js \ "phoneNo").asOpt[String].map (x => x).getOrElse("")
        build += "email" -> (js \ "email").asOpt[String].map (x => x).getOrElse("")

        val scope_builder = MongoDBObject.newBuilder
        scope_builder += "edge" -> pushEdgeScope(js)
        scope_builder += "category" -> pushProduceLevelScope(js)
        scope_builder += "manufacture_name" -> pushManufactureNameScope(js)
        scope_builder += "is_admin" -> (js \ "scope" \ "is_admin").asOpt[Int].map (x => x).getOrElse(0)
        scope_builder += "sample" ->(js \ "scope" \ "sample").asOpt[Int].map(x => x).getOrElse(0)

        build += "scope" -> scope_builder.result

        build += "status" -> (js \ "status").asOpt[Int].map (x => x).getOrElse("")

        build.result
    }

    // for query
    implicit val d2m : DBObject => Map[String, JsValue] = { obj =>
        // 需要添加Scrope，的解析
        val sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        Map(
            "user_id" -> toJson(obj.getAs[String]("user_id").map (x => x).getOrElse(throw new Exception("db prase error"))),
            "user_name" -> toJson(obj.getAs[String]("user_name").map (x => x).getOrElse(throw new Exception("db prase error"))),
            "pwd" -> toJson(obj.getAs[String]("pwd").map (x => x).getOrElse(throw new Exception("db prase error"))),
            "phoneNo" -> toJson(obj.getAs[String]("phoneNo").map (x => x).getOrElse(throw new Exception("db prase error"))),
            "email" -> toJson(obj.getAs[String]("email").map (x => x).getOrElse(throw new Exception("db prase error"))),
            "scope" -> toJson(Map("edge" -> queryEdgeScope(obj),
                "category" -> queryProductLevelScope(obj),
                "manufacture_name" -> queryManufactureNameScope(obj),
                "is_admin" -> queryIsAdminScope(obj),
                "sample" ->querySampleScope(obj))),
            "screen_name" -> toJson(obj.getAs[String]("screen_name").map (x => x).getOrElse(throw new Exception("db prase error"))),
            "screen_photo" -> toJson(obj.getAs[String]("screen_photo").map (x => x).getOrElse(throw new Exception("db prase error"))),
            "date" -> toJson(obj.getAs[Number]("date").map (x => sdf.format(new Date(x.longValue()))).getOrElse(throw new Exception("db prase error"))),
            "updateDate" -> toJson(obj.getAs[Number]("updateDate").map (x => sdf.format(new Date(x.longValue()))).getOrElse(throw new Exception("db prase error"))),
            "status" -> toJson(obj.getAs[Int]("status").map (x => x).getOrElse(throw new Exception("db prase error")))
        )
    }
}
