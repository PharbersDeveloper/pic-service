package bmlogic.category.categoryData

import com.mongodb.casbah.Imports._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

/**
  * Created by yym on 6/15/17.
  */
trait CategoryData {
 
    implicit val d2m : DBObject => Map[String, JsValue] = { obj =>
        Map(
            "level"->toJson(obj.getAs[Int]("level").map(x=>x).getOrElse(throw  new Exception("category without level"))),
            "parent"->toJson(obj.getAs[String]("parent").map(x=>x).getOrElse(throw  new Exception("category without level"))),
            "def"->toJson(obj.getAs[String]("def").map(x=>x).getOrElse(throw  new Exception("category without def"))),
            "des"->toJson(obj.getAs[String]("des").map(x=>x).getOrElse(throw  new Exception("category without des")))
        )
    }
}
