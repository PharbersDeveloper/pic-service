package bmlogic.retrieval.RetrievalData

import com.mongodb.casbah.Imports._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

/**
  * Created by alfredyang on 07/06/2017.
  */
trait RetrievalData {
    implicit val m2d : JsValue => DBObject = { js =>
        val builder = MongoDBObject.newBuilder

        builder += "date" -> (js \ "date").asOpt[Long].map (x => x).getOrElse(throw new Exception("product without time"))
        builder += "province" -> (js \ "province").asOpt[String].map (x => x).getOrElse(throw new Exception("product without province"))
        builder += "sales" -> (js \ "sales").asOpt[Long].map (x => x).getOrElse(throw new Exception("product without sales value"))
        builder += "units" -> (js \ "units").asOpt[Long].map (x => x).getOrElse(throw new Exception("product without sales units"))
        builder += "oral_name" -> (js \ "oral_name").asOpt[String].map (x => x).getOrElse(throw new Exception("product without oral name"))
        builder += "manufacture" -> (js \ "manufacture").asOpt[String].map (x => x).getOrElse(throw new Exception("product without manufacture"))
        builder += "specifications" -> (js \ "specifications").asOpt[String].map (x => x).getOrElse(throw new Exception("product without specifications"))
        builder += "product_unit" -> (js \ "product_unit").asOpt[String].map (x => x).getOrElse(throw new Exception("product without product unit"))
        builder += "manufacture_type" -> (js \ "manufacture_type").asOpt[String].map (x => x).getOrElse(throw new Exception("product without manufacture type"))
        builder += "product_type" -> (js \ "product_type").asOpt[String].map (x => x).getOrElse(throw new Exception("product without product type"))
        builder += "package" -> (js \ "package").asOpt[String].map (x => x).getOrElse(throw new Exception("product without package"))
        builder += "category" -> (js \ "category").asOpt[String].map (x => x).getOrElse(throw new Exception("product without category"))

        builder.result
    }

    // for query
    implicit val d2m : DBObject => Map[String, JsValue] = { obj =>
        Map(
            "sales_id" -> toJson(obj.getAs[String]("sales_id").map (x => x).getOrElse(throw new Exception("product without sales id"))),
            "date" -> toJson(obj.getAs[Number]("date").map (x => x.longValue).getOrElse(throw new Exception("product without time"))),
            "province" -> toJson(obj.getAs[String]("province").map (x => x).getOrElse(throw new Exception("product without province"))),
            "sales" -> toJson(obj.getAs[Number]("sales").map (x => x.longValue).getOrElse(throw new Exception("product without sales value"))),
            "units" -> toJson(obj.getAs[Number]("units").map (x => x.longValue).getOrElse(throw new Exception("product without sales units"))),
            "oral_name" -> toJson(obj.getAs[String]("oral_name").map (x => x).getOrElse(throw new Exception("product without oral name"))),
            "manufacture" -> toJson(obj.getAs[String]("manufacture").map (x => x).getOrElse(throw new Exception("product with manufacture"))),
            "specifications" -> toJson(obj.getAs[String]("specifications").map (x => x).getOrElse(throw new Exception("product with specifications"))),
            "product_unit" -> toJson(obj.getAs[String]("product_unit").map (x => x).getOrElse(throw new Exception("product with product unit"))),
            "product_name" -> toJson(obj.getAs[String]("product_name").map (x => x).getOrElse(throw new Exception("product with product unit"))),
            "manufacture_type" -> toJson(obj.getAs[String]("manufacture_type").map (x => x).getOrElse(throw new Exception("product with manufacture type"))),
            "product_type" -> toJson(obj.getAs[String]("product_type").map (x => x).getOrElse(throw new Exception("product with product type"))),
            "package" -> toJson(obj.getAs[String]("package").map (x => x).getOrElse(throw new Exception("product with package"))),
            "category" -> toJson(obj.getAs[String]("category").map (x => x).getOrElse(throw new Exception("product with category")))
        )
    }
}
