package bmlogic.aggregateCalc

import bminjection.db.DBTrait
import bmlogic.aggregateCalc.AggregateCalcMessage._
import bmlogic.conditions.ConditionSearchFunc
import bmmessages.{CommonModules, MessageDefines}
import bmpattern.ModuleTrait
import bmutil.MergeJs
import bmutil.errorcode.ErrorCode
import play.api.libs.json.{JsObject, JsValue}
import play.api.libs.json.Json.toJson
import com.mongodb.casbah.Imports._

/**
  * Created by jeorch on 17-6-12.
  */

object AggregateModule extends ModuleTrait with ConditionSearchFunc {
    
    def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_CalcPercentage(data) => calcPercentage(data)(pr)
        case msg_CalcTrend(data) => calcTrend(data)(pr)
        case msg_CalcTrend_Mat(data) => calcTrendMat(data)(pr)
        case msg_CalcMarketSize(data) => calcMarketSize2(data)(pr)
        case msg_ProductQuantity(data) => productSize(data)(pr)
    
        case _ => ???
    }
    /**
      * 市场规模：客户输入的市场的最新月份，累计一年的销售额-------------------------
      */
    def calcMarketSize2(data : JsValue)
                       (pr : Option[Map[String, JsValue]])
                       (implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {
        
        try {
            val mergeJs=MergeJs.dataMergeWithPr(data,pr)
            val db = cm.modules.get.get("db").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
            
            val condition = (conditionParse(data, pr.get) :: dateConditionParse(data) :: oralNameConditionParse(data) :: productNameConditionParse(data) :: Nil).filterNot(_ == None).map(_.get)
            if (mergeJs.as[JsObject].value.filterKeys(x => x=="Warning").isEmpty){
                val group = MongoDBObject("_id" -> MongoDBObject("ms" -> "market size"), "sales" -> MongoDBObject("$sum" -> "$sales"))
                val result = db.aggregate($and(condition), "retrieval", group){ x =>
                    Map("sales" -> toJson(aggregateSalesResult(x, "market size")))
                }
                if (result.isEmpty) throw new Exception("calc market size func error")
                else (Some(Map("calc" -> toJson(result))), None)
            }else {
                (Some(Map("calc" -> toJson(0))), None)
            }
            
        } catch {
            case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }
    /**
      * 还可以更简单，你们谁来优化这个？？？
      */
    def calcTrend(data : JsValue)
                 (pr : Option[Map[String, JsValue]])
                 (implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            val mergeJs=MergeJs.dataMergeWithPr(data,pr)
            val db = cm.modules.get.get("db").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
            if (mergeJs.as[JsObject].value.filterKeys(x => x=="Warning").isEmpty){
                val condition = (conditionParse(data, pr.get) :: dateConditionParse(data) ::
                    oralNameConditionParse(data) :: productNameConditionParse(data) :: Nil).
                    filterNot(_ == None).map(_.get)
                
                val group = MongoDBObject("_id" -> MongoDBObject("ms" -> "market trend"), "sales" -> MongoDBObject("$sum" -> "$sales"))
                
                
                val result = db.aggregate($and(condition), "retrieval", group){ x =>
                    Map("sales" -> toJson(aggregateSalesResult(x, "market trend")))
                }
                
                if (result.isEmpty) throw new Exception("calc market trend func error")
                else (Some(Map("trend" -> toJson(result))), None)
            }else{
                (Some(Map("trend" -> toJson(0)) ++ pr.get), None)
            }
            
            
        } catch {
            case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }
    
    def calcPercentage(data : JsValue)
                      (pr : Option[Map[String, JsValue]])
                      (implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            val mergeJs=MergeJs.dataMergeWithPr(data,pr)
            val db = cm.modules.get.get("db").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
            
            if (mergeJs.as[JsObject].value.filterKeys(x => x=="Warning").isEmpty){
                val oral_name_condition = oralNameConditionParse(data)
                val product_name_condition = productNameConditionParse(data)

                val parent_name_conditon =
                    if (oral_name_condition.isEmpty && product_name_condition.isEmpty) throw new Exception("calc percentage without oral name or product name")
                    else parentNameConditionParse(data)

                val basic_conditions = (conditionParse(data, pr.get) :: dateConditionParse(data) :: Nil)

                val group = MongoDBObject("_id" -> MongoDBObject("ms" -> "market size"), "sales" -> MongoDBObject("$sum" -> "$sales"))

                val ori_con = (oral_name_condition :: product_name_condition :: basic_conditions).
                    filterNot(_ == None).map (_.get)

                val par_con = (parent_name_conditon :: basic_conditions).filterNot(_ == None).map (_.get)

                val ori_result = db.aggregate($and(ori_con), "retrieval", group){ x =>
                    Map("sales" -> toJson(aggregateSalesResult(x, "market size")))
                }

                val par_result = db.aggregate($and(par_con), "retrieval", group){ x =>
                    Map("sales" -> toJson(aggregateSalesResult(x, "market size")))
                }

                val percentage =
                    if (ori_result.isEmpty || par_result.isEmpty) throw new Exception("")
                    else (ori_result.get.get("sales").get.asOpt[Long].get.floatValue()) /
                        (par_result.get.get("sales").get.asOpt[Long].get.floatValue())
                (Some(Map(
                    "percentage" -> toJson(percentage)
                )), None)
            }else{
                (Some(Map(
                    "percentage" -> toJson(0)
                )), None)
            }

        } catch {
            case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }
    
    /**
      *
      * 计算产品数量------------------------------------------------------
      */
    def productSize(data: JsValue)
                   (pr : Option[Map[String, JsValue]])
                   (implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
        
        def aggregateResult(x : MongoDBObject) : Long = {
            val ok = x.getAs[Number]("ok").get.intValue
            if (ok == 0) throw new Exception("db aggregation error")
            else {
                val lst : BasicDBList = x.getAs[BasicDBList]("result").get
                lst.toList.asInstanceOf[List[BasicDBObject]].size.toLong
            }
        }
        
        try {
            val mergeJs=MergeJs.dataMergeWithPr(data,pr)
            val db = cm.modules.get.get("db").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
            
            if (mergeJs.as[JsObject].value.filterKeys(x => x=="Warning").isEmpty){
                val group = MongoDBObject("_id" -> MongoDBObject("product_name" -> "$product_name", "manufacture" -> "$manufacture", "product_type" -> "$product_type"))
                val condition = (conditionParse(data, pr.get) :: dateConditionParse(data) :: oralNameConditionParse(data) :: productNameConditionParse(data) :: Nil).filterNot(_ == None).map(_.get)
                val size = db.aggregate($and(condition), "retrieval", group) { x =>
                    Map("size" -> toJson(aggregateResult(x)))
                }
                if(size.isEmpty) throw new Exception("product size  func error")
                else (size, None)
            }else{
                (Some(Map("size" -> toJson(0))), None)
            }
            
            
        } catch {
            case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
        
    }

    def calcTrendMat(data : JsValue)
                    (pr : Option[Map[String, JsValue]])
                    (implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {
        try {
            val mergeJs=MergeJs.dataMergeWithPr(data,pr)
            val db = cm.modules.get.get("db").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))

            if (mergeJs.as[JsObject].value.filterKeys(x => x=="Warning").isEmpty){
                val condition = (conditionParse(data, pr.get) :: dateConditionParse(data, true) ::
                    oralNameConditionParse(data) :: productNameConditionParse(data) :: Nil).
                    filterNot(_ == None).map(_.get)

                val group = MongoDBObject("_id" -> MongoDBObject("ms" -> "market trend"), "sales" -> MongoDBObject("$sum" -> "$sales"))

                val result = db.aggregate($and(condition), "retrieval", group){ x =>
                    Map("sales" -> toJson(aggregateSalesResult(x, "market trend")))
                }

                val sales = (pr.get.get("trend").get \ "sales").asOpt[Double].getOrElse(0D)
                val matSales = result.get.get("sales").get.asOpt[Double].getOrElse(0D)
                val r = (sales - matSales) / matSales * 100

                if (result.isEmpty) throw new Exception("calc market trend func error")
                else (Some(Map("trend" -> toJson(r))), None)
            }else{
                (Some(Map("trend" -> toJson(0)) ++ pr.get), None)
            }


        } catch {
            case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }
    
    /**
      * 这个速度太慢，利用MongoDB的 Advance Map Reduce 操作 Aggregate 查询
      * 详情见 calcMarketSize2
      */
    def calcMarketSize(data : JsValue)
                      (pr : Option[Map[String, JsValue]])
                      (implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {
        
        try {
            val db = cm.modules.get.get("db").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
            
            val condition = (conditionParse(data, pr.get) :: dateConditionParse(data) ::
                oralNameConditionParse(data) :: productNameConditionParse(data) :: Nil).
                filterNot(_ == None).map(_.get)
            val result = db.querySum($and(condition), "retrieval"){(s, a) =>
                val os = s.get("sales").map (x => x.asOpt[Long].get).getOrElse(0.toLong)
                val as = a.get("sales").map (x => x.asOpt[Long].get).getOrElse(0.toLong)
                Map("sales" -> toJson(os + as))
            } { o =>
                Map(
                    "sales" -> toJson(o.getAs[Number]("sales")
                        .map (x => x.longValue)
                        .getOrElse(throw new Exception("product without sales value")))
                )
            }
            
            if (result.isEmpty) throw new Exception("calc market size func error")
            else (result, None)
            
        } catch {
            case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
    }
    

    
    
 
    
    def aggregateSalesResult(x : MongoDBObject, id : String) : Long = {
        val ok = x.getAs[Number]("ok").get.intValue
        if (ok == 0) throw new Exception("db aggregation error")
        else {
            val lst : BasicDBList = x.getAs[BasicDBList]("result").get
            val tmp = lst.toList.asInstanceOf[List[BasicDBObject]]
            tmp.find(y => y.getAs[BasicDBObject]("_id").get.getString("ms") == id).map { z =>
                z.getLong("sales") / 100
            }.getOrElse(throw new Exception("db aggregation error"))
        }
    }
}