package bmlogic.retrieval


import bminjection.db.DBTrait
import bmlogic.category.CategoryModule
import bmlogic.common.Page._
import bmlogic.common.sercurity.Sercurity
import bmlogic.conditions.ConditionSearchFunc
import bmlogic.retrieval.RetrievalData.RetrievalData
import bmlogic.retrieval.RetrievalMessage._
import bmmessages.{CommonModules, MessageDefines}
import bmpattern.ModuleTrait
import bmutil.MergeJs
import bmutil.errorcode.ErrorCode
import play.api.libs.json.{JsObject, JsValue}
import play.api.libs.json.Json.toJson

import scala.collection.immutable.Map
import com.mongodb.casbah.Imports._

/**
  * Created by alfredyang on 01/06/2017.
  */
object RetrievalModule extends ModuleTrait with RetrievalData with ConditionSearchFunc {
   
    
    def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_ConditionSearchCommand(data) => conditionSearch(data)(pr)

        case msg_PushProduct(data) => pushProduct(data)
        case msg_UpdateProduct(data) => updateProduct(data)
        case msg_DeleteProduct(data) => deleteProduct(data)

        case _ => ???
    }
    
    def searchatc(value: String)
                 (result: Option[List[Map[String, JsValue]]])
                 (implicit cm : CommonModules) : Option[List[Map[String, JsValue]]] = {
        val lst = CategoryModule.LoadCategory
        lst match {
            case None => None
            case Some(list) =>
                val v = list.find(x => x.get("des").get.as[String] == value)
                val vv = list.find(x => x.get("def").get.as[String] == v.get.get("parent").get.as[String]).get
                if(vv.get("level").get.as[Int] == 0) Some(vv :: result.get)
                else searchatc(vv.get("des").get.as[String])(Some(vv :: result.get))
            case _ => ???
        }
    }

    def conditionSearch(data : JsValue)
                       (pr : Option[Map[String, JsValue]])
                       (implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {
        import bmutil.alDateOpt._
        try {
            val mergeJs=MergeJs.dataMergeWithPr(data,pr)
            val db = cm.modules.get.get("db").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
            
            val condition = (conditionParse(data, pr.get) :: dateConditionParse(data) ::
                             oralNameConditionParse(data) :: productNameConditionParse(data) :: Nil).filterNot(_ == None).map(_.get)
            if (mergeJs.as[JsObject].value.filterKeys(x => x=="Warning").isEmpty){
                val count = if(!condition.isEmpty) {
                    val group = MongoDBObject("_id" -> MongoDBObject("ms" -> "count"), "count" -> MongoDBObject("$sum" -> 1))
                    db.aggregate($and(condition), "retrieval", group){ x =>
                        Map("count" -> toJson(aggregateSalesResult(x, "count")))
                    }
                    
                }else {
                    Some(Map("count" -> toJson(0)))
                }

                var index = 0
                val skip = (data \ "skip").asOpt[Int].getOrElse(1)
                val r = db.queryMultipleObject($and(condition), "retrieval", skip = skip, take = TAKE).map { x =>
                    val atc = searchatc(x.get("category").get.asOpt[String].getOrElse(throw new Exception("input error")))(Some(Nil)).map(x => x).getOrElse(throw new Exception)
//                    val html =
//                        s"""<tr>
//                           |     <td>$index</td>
//                           |     <td>${Timestamp2yyyyMM(x.get("date").get.as[Long])}</td>
//                           |     <td>${x.get("province").get.as[String]}</td>
//                           |     <td>${x.get("product_name").get.as[String]}</td>
//                           |     <td>${x.get("sales").get.as[Long]}</td>
//                           |     <td>${x.get("units").get.as[Long]}</td>
//                           |     <td>${x.get("oral_name").get.as[String]}</td>
//                           |     <td>${x.get("manufacture").get.as[String]}</td>
//                           |     <td>${x.get("specifications").get.as[String]}</td>
//                           |     <td>${atc.head.get("des").get.as[String]}</td>
//                           |     <td>${atc.tail.head.get("des").get.as[String]}</td>
//                           |     <td>${x.get("category").get.as[String]}</td>
//                           |     <td>${x.get("product_unit").get.as[String]}</td>
//                           |     <td>${x.get("manufacture_type").get.as[String]}</td>
//                           |     <td>${x.get("product_type").get.as[String]}</td>
//                           |     <td>${x.get("package").get.as[String]}</td>
//                           |  </tr>""".stripMargin
                    
                    index += 1
//                    Map("html" -> html)
                    Map("date" -> toJson(Timestamp2yyyyMM(x.get("date").get.as[Long])),
                        "province" -> x.get("province").get,
                        "product_name" -> x.get("product_name").get,
                        "sales" -> x.get("sales").get,
                        "units" -> x.get("units").get,
                        "oral_name" -> x.get("oral_name").get,
                        "manufacture" -> x.get("manufacture").get,
                        "specifications" -> x.get("specifications").get,
                        "act1" -> atc.head.get("des").get,
                        "act2" -> atc.tail.head.get("des").get,
                        "act3" -> x.get("category").get,
                        "product_unit" -> x.get("product_unit").get,
                        "manufacture_type" -> x.get("manufacture_type").get,
                        "product_type" -> x.get("product_type").get,
                        "package" -> x.get("package").get,
                        "index" -> toJson(index)
                    )
                }
                val result = Map("search_result" -> toJson(r), "page" -> toJson(Page(skip, count.get.get("count").get.as[Long])))
                (Some(result), None)
            }else {
                val emptyList : List[Map[String,String]] = List()
                val result = Map("search_result" -> toJson(emptyList), "page" -> toJson(""))
                (Some(result), None)
            }

        } catch {
            case ex : Exception =>
                (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }
  
//    //查询某商品数量
//    def productQuantity(data:JsValue)
//                       (pr : Option[Map[String, JsValue]])
//                       (implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue])={
//        try{
//            val db=cm.modules.get.get("db").map(x=>x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
//            val condition=(productNameConditionParse(data)::Nil).filterNot(_==None).map(_.get)
//            val productNum = db.queryMultipleObject($or(condition), "retrieval").size
//            val result=Map("productQuantity"->toJson(productNum))
//            (Some(result), None)
//        }catch {
//            case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
//        }
//
//    }

    def pushProduct(data : JsValue)
                   (implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {

        try {
            val db = cm.modules.get.get("db").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))

            val o : DBObject = data
            val product_unit = (data \ "product_unit").asOpt[String].map (x => x).getOrElse(throw new Exception("input error"))
            val date = (data \ "date").asOpt[String].map (x => x).getOrElse(throw new Exception("input error"))
            o += "sales_id" -> Sercurity.md5Hash(product_unit + date)

            db.insertObject(o, "retrieval", "sales_id")

            (Some(Map(
                "retrival" -> toJson(o - "sales_id")
            )), None)

        } catch {
            case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }

    def updateProduct(data : JsValue)
                     (implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {

        try {
            val db = cm.modules.get.get("db").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))

            val o : DBObject = data
            db.updateObject(o, "retrieval", "sales_id")

            (Some(Map(
                "retrival" -> toJson(o - "sales_id")
            )), None)

        } catch {
            case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }

    def deleteProduct(data : JsValue)
                     (implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {

        try {
            val db = cm.modules.get.get("db").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))

            val o : DBObject = data
            db.deleteObject(o, "retrieval", "sales_id")

            (Some(Map(
                "retrival" -> toJson(o - "sales_id")
            )), None)

        } catch {
            case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }
    
    def aggregateSalesResult(x : MongoDBObject, id : String) : Long = {
        val ok = x.getAs[Number]("ok").get.intValue
        if (ok == 0) throw new Exception("db aggregation error")
        else {
            val lst = x.getAs[MongoDBList]("result").get
            val tmp = lst.toList.asInstanceOf[List[BasicDBObject]]
            tmp.find ( z => z.getAs[BasicDBObject]("_id").get.getAs[String]("ms").map(a => a).getOrElse("") == id )
                .map ( y =>y.getAs[Number]("count").map(_.longValue()).getOrElse(throw new Exception("db aggregation error"))).getOrElse(0)
        }
    }
}
