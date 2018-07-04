package bmlogic.config.ConfigData

import com.mongodb.casbah.Imports._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import collection.JavaConversions._

/**
  * Created by yym on 6/12/17.
  */
trait ConfigData {
    
    def convertJsValue(lst: List[BasicDBObject]): JsValue = {
        
//        toJson(lst.map (x => x.iterator.toList.map(z => Map(z._1 -> z._2))).flatten)
        
        toJson(lst.map { x =>
            Map("level" -> toJson(x.getAs[Number]("level").map(x => x.intValue()).getOrElse(throw new Exception("level error"))),
                "parent" -> toJson(x.getAs[String]("parent").map(x => x).getOrElse(throw new Exception("parent error"))),
                "def" -> toJson(x.getAs[String]("def").map(x => x).getOrElse(throw new Exception("def error"))),
                "des" -> toJson(x.getAs[String]("des").map(x => x).getOrElse(throw new Exception("des error")))
            )

        })
    }

    
    
   
    
    implicit val d2m : DBObject => Map[String, JsValue] = { obj =>
        
        Map(
            "index"->toJson(obj.getAs[String]("index").map(x=>x).getOrElse(throw new Exception("index error"))),
            "province"->toJson(obj.getAs[List[String]]("province").map(x=>x).getOrElse(throw new Exception("province error"))),
            "category"-> convertJsValue(obj.getAs[List[BasicDBObject]]("category").map(x => x).get),
            "manufacture"->toJson(obj.getAs[List[String]]("manufacture").get),
            "product_type"->toJson(obj.getAs[List[String]]("product_type").get),
            "specifications"->toJson(obj.getAs[List[String]]("specifications").get),
            "package"->toJson(obj.getAs[List[String]]("package").get)

        )
    }
}
