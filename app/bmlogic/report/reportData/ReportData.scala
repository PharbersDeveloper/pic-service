package bmlogic.report.reportData

import com.mongodb.casbah.Imports.{DBObject, MongoDBObject}
import play.api.libs.json.JsValue
import play.api.libs.json.Json._

/**
  * Created by qianpeng on 2017/6/21.
  */
trait ReportData {
	implicit val m2d : JsValue => DBObject = { js =>
		val build = MongoDBObject.newBuilder
		build += "reportid" -> (js \ "reportid").get.asOpt[String].map(x => x).getOrElse("")
		build += "token" -> (js \ "token").get.asOpt[String].map(x => x).getOrElse("")
		build += "condition" -> (js \ "condition").get.toString()
		build.result
	}
	
	implicit val d2m: DBObject => Map[String, JsValue] = { dbo =>
		
		Map("test" -> toJson(0))
		
	}
}
