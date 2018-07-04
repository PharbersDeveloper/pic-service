package bmlogic.adjustdata

import bminjection.db.DBTrait
import bmlogic.adjustdata.AdjustDataMessage.msg_AdjustNameDataCommand
import bmmessages.{CommonModules, MessageDefines}
import bmpattern.ModuleTrait
import bmutil.dao.{_data_connection, from}
import com.mongodb.casbah.Imports._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

/**
  * Created by jeorch on 17-6-13.
  */
object AdjustDataModule extends ModuleTrait{
    def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {

        case msg_AdjustNameDataCommand(data) => adjustNameData(data)

        case _ => ???
    }

    def adjustNameData(data : JsValue)(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {
        val db=cm.modules.get.get("db").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
        /**
          * 生产厂商， 生产规格， 包装， 剂型
          */
//         val ct = (from db() in "retrieval").selectCursor
        val ct=db.loadAllData("retrieval")(retrievalD2M)
        var product_type_lst : List[String] = Nil
        var manufacture_lst : List[String] = Nil
        var specifications_lst : List[String] = Nil
        var package_lst : List[String] = Nil
        ct.map { x =>
            manufacture_lst = (manufacture_lst :+ x.get("manufacture").get.as[String]).distinct
            product_type_lst = (product_type_lst :+ x.get("product_type").get.as[String]).distinct
            specifications_lst = (product_type_lst :+ x.get("specifications").get.as[String]).distinct
            package_lst = (product_type_lst :+ x.get("package").get.as[String]).distinct
            ""
        }
//
//        while (ct.hasNext) {
//            val iter = ct.next()
//            manufacture_lst = (manufacture_lst :+ iter.getAs[String]("manufacture").get).distinct
//            product_type_lst = (product_type_lst :+ iter.getAs[String]("product_type").get).distinct
//            specifications_lst = (specifications_lst :+ iter.getAs[String]("specifications").get).distinct
//            package_lst = (package_lst :+ iter.getAs[String]("package").get).distinct
//        }
//        val con = (from db() in "config" where ("index" -> "PIC")).selectTop(1)("index")(x => x).toList.head
        var con=db.queryObject(DBObject("index" -> "PIC"),"config")(categoryD2M).get
        con += "manufacture" -> toJson(manufacture_lst)
        con += "product_type" -> toJson(product_type_lst)
        con += "specifications" -> toJson(specifications_lst)
        con += "package" -> toJson(package_lst)
        db.updateObject(con,"config","_id")

//        _data_connection.getCollection("config").update(DBObject("index" -> "PIC"), con)

        (Some(Map("ok" -> toJson("ok"))), None)
    }
    val retrievalD2M : DBObject => Map[String, JsValue] = { obj =>
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
    val categoryD2M : DBObject => Map[String, JsValue] = { obj =>
        Map(
            "level"->toJson(obj.getAs[Int]("level").map(x=>x).getOrElse(throw  new Exception("category without level"))),
            "parent"->toJson(obj.getAs[String]("parent").map(x=>x).getOrElse(throw  new Exception("category without parent"))),
            "def"->toJson(obj.getAs[String]("def").map(x=>x).getOrElse(throw  new Exception("category without def"))),
            "des"->toJson(obj.getAs[String]("des").map(x=>x).getOrElse(throw  new Exception("category without des")))
        )
    }
    
}
