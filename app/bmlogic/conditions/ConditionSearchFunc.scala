package bmlogic.conditions

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import bmlogic.report.ReportModule.{getDateMatParse, sdf}
import bmutil.dao.from
import com.mongodb.casbah.Imports._
import play.api.libs.json.JsValue

/**
  * Created by alfredyang on 08/06/2017.
  */
trait ConditionSearchFunc {
    val sdf = new SimpleDateFormat("yyyyMM")

    def timeList(n: Int = 0, data: JsValue): List[String] = {
        val start = (data \ "condition" \ "date" \ "start").get.as[String]
        val end = (data \ "condition" \ "date" \ "end").get.as[String]
        end :: start :: (n-n+1 to n).map( x =>sdf.format(getDateMatParse(sdf.parse(start), x * -12))).toList
    }
    
    def oralNameConditionParse(js : JsValue) : Option[DBObject] = {
        val data = (js \ "condition").asOpt[JsValue].map (x => x)
            .getOrElse(throw new Exception("search condition parse error"))
        
        equalsConditions[String](data, "oral_name")
    }
    
    def productNameConditionParse(js : JsValue) : Option[DBObject] = {
        val data = (js \ "condition").asOpt[JsValue].map (x => x)
            .getOrElse(throw new Exception("search condition parse error"))
        equalsConditions[String](data, "product_name")
    }
    
    def parentNameConditionParse(js : JsValue) : Option[DBObject] = {
        val data = (js \ "condition").asOpt[JsValue].map (x => x)
            .getOrElse(throw new Exception("search condition parse error"))
        
        val oral_name_opt = (data \ "oral_name").asOpt[String]
        val product_name_opt = (data \ "product_name").asOpt[String]
        
        if (oral_name_opt.isEmpty && product_name_opt.isEmpty) None
        else {
            val oral_lst =
                oral_name_opt.map { x =>
                    (from db() in "category" where("des" -> x) select(x => x.getAs[String]("parent").get)).toList
                }.getOrElse(Nil)
            
            val product_lst =
                product_name_opt.map { x =>
                    (from db() in "category" where("def" -> x) select(x => x.getAs[String]("parent").get)).toList
                }.getOrElse(Nil)
            if(oral_lst.isEmpty&&product_lst.isEmpty) None
            else
                Some($or((oral_lst ++ product_lst).distinct.map (x => DBObject("category" -> x))))
        }
    }
    
    def categoryConditionParse(js : JsValue, pr : Map[String, JsValue]) : Option[DBObject] = {
        /**
          * 类型
          */
        val cat = pr.get("search_category_condition").map (x => x.asOpt[List[String]].get).getOrElse(Nil)
        val cat_condition : Option[DBObject] =
            if (cat.isEmpty) None
            else Some($or(cat map ("category" $eq _)))
        cat_condition
    }
    
    def dateConditionParse(js : JsValue, flag: Boolean = false, month: Int = -12) : Option[DBObject] = {
        val data = (js \ "condition").asOpt[JsValue].map (x => x).getOrElse(throw new Exception("search condition parse error"))
        
        val date_input = (data \ "date").asOpt[JsValue].map (x => Some(x)).getOrElse(None)
        
        date_input match {
            case Some(date) => {
                /**
                  * start : yyyyMM 形式的时间表达式
                  * end : yyyyMM 形式的时间表达式
                  */
                val start = (date \ "start").asOpt[String].map (x => x).
                    getOrElse(throw new Exception("search condition parse error"))
                val end = (date \ "end").asOpt[String].map (x => x).
                    getOrElse(throw new Exception("search condition parse error"))
                
                val start_date = sdf.parse(start)
                val end_date = sdf.parse(end)
                if(!flag) Some($and("date" $lt end_date.getTime, "date" $gte start_date.getTime))
                else Some($and("date" $lt start_date.getTime, "date" $gte getDateMatParse(start_date, month).getTime))
            }
            case None => None
        }
    }
   
    def getDateMatParse(date: Date, month: Int) = {
        val c = Calendar.getInstance()
        c.setTime(date)
        c.add(Calendar.MONTH, month)
        c.getTime
    }
    
    def conditionParse(js : JsValue, pr : Map[String, JsValue]) : Option[DBObject] = {
        
        val data = (js \ "condition").asOpt[JsValue].map (x => x).getOrElse(throw new Exception("search condition parse error"))
        
        /**
          * 生产厂商名
          */
        val mnc = pr.get("search_manufacture_name_condition").map (x => x.asOpt[List[String]].get).getOrElse(Nil)
        val manufacture_name_condition : Option[DBObject] =
            if (mnc.isEmpty) None
            else Some($or(mnc map ("manufacture" $eq _)))
        
        /**
          * 区域
          */
        val ec = pr.get("search_edge_condition").map (x => x.asOpt[List[String]].get).getOrElse(Nil)
        val edge_condition : Option[DBObject] =
            if (ec.isEmpty) None
            else Some($or(ec map ("province" $eq _)))
        
        /**
          * 类型
          */
        val cat = pr.get("search_category_condition").map (x => x.asOpt[List[String]].get).getOrElse(Nil)
        val cat_condition : Option[DBObject] =
            if (cat.isEmpty) None
            else Some($or(cat map ("category" $eq _)))
        /**
          * 通用名 * 产品名 * 生产厂商类型 * 剂型 * 规格 * 包装
          */
        val result =
            (cat_condition :: edge_condition :: manufacture_name_condition
                //                :: ("oral_name" :: "product_name" :: "manufacture_type"
                :: ("manufacture_type" :: "product_type" :: "specifications" :: "package" :: Nil)
                .map (equalsConditions[String](data, _))).filterNot(_ == None).map (_.get)
        if (result.isEmpty) None
        else Some($and(result))
    }
    
    def  equalsConditions[T <: String](data : JsValue, name : String) : Option[DBObject] =
        (data \ name).asOpt[String].map (x => Some(name $eq x)).getOrElse(None)
}