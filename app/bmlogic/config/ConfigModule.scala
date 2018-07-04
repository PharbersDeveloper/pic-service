package bmlogic.config

import bminjection.db.DBTrait
import bminjection.token.AuthTokenTrait
import bmlogic.config.ConfigData.ConfigData
import bmlogic.config.ConfigMessage.{msg_QueryInfoCommand, msg_queryAuthTree}
import bmmessages.{CommonModules, MessageDefines}
import bmpattern.ModuleTrait
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import bmlogic.auth.AuthModule.queryUserById

import scala.collection.immutable.Map
import com.mongodb.casbah.Imports._

/**
  * Created by alfredyang on 08/06/2017.
  */
object ConfigModule extends ModuleTrait with ConfigData{
    def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_QueryInfoCommand(data) => infoQuery(data)
        case msg_queryAuthTree(data) => query_auth_tree(data)

        case _ => ???
    }

    def infoQuery(data : JsValue)(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = {
        val db = cm.modules.get.get("db").map(x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
        val preRes = db.queryMultipleObject(DBObject("index" -> "PIC"), "config")
        val token = (data \ "token").get.asOpt[String].map(x => x).getOrElse(throw new Exception("can't find token"))
        val att = cm.modules.get.get("att").map(x => x.asInstanceOf[AuthTokenTrait]).getOrElse(throw new Exception("no encrypt impl"))
        val auth = att.decrypt2JsValue(token)
        val manufacture_name = (auth \ "scope" \ "manufacture_name").get.asOpt[List[String]].getOrElse(throw new Exception("prase error"))
        val edge = (auth \ "scope" \ "edge").get.asOpt[List[String]].getOrElse(throw new Exception("prase error"))
//        val res =if (!manufacture_name.isEmpty && edge.isEmpty) {
//            List( Map(("package" -> preRes.head.get("package").get),("specifications" -> preRes.head.get("specifications").get),
//                ("product_type" -> preRes.head.get("product_type").get),("province" ->toJson(edge)), ("manufacture" -> preRes.head.get("manufacture").get)))
//        }else if (!edge.isEmpty && manufacture_name.isEmpty) {
//           List( Map(("package" -> preRes.head.get("package").get),("specifications" -> preRes.head.get("specifications").get),
//                ("product_type" -> preRes.head.get("product_type").get),("province" -> preRes.head.get("province").get), ("manufacture" -> toJson(manufacture_name))))
//        }else if(!edge.isEmpty && !manufacture_name.isEmpty){
//            List( Map(("package" -> preRes.head.get("package").get),("specifications" -> preRes.head.get("specifications").get),
//                ("product_type" -> preRes.head.get("product_type").get),("province" ->toJson(edge)), ("manufacture" -> toJson(manufacture_name))))
//        }else{
//            preRes
//        }
        val condition_manufacture=if(manufacture_name.isEmpty) 0 else 1
        val condition_edge=if(edge.isEmpty) 0 else 1
        val result= List(condition_manufacture , condition_edge) match {
            case List(1,0) =>
                List( Map(("package" -> preRes.head.get("package").get),("specifications" -> preRes.head.get("specifications").get),
                    ("product_type" -> preRes.head.get("product_type").get),("province" ->toJson(edge)), ("manufacture" -> preRes.head.get("manufacture").get)))
            case List(0,1) =>
                List( Map(("package" -> preRes.head.get("package").get),("specifications" -> preRes.head.get("specifications").get),
                    ("product_type" -> preRes.head.get("product_type").get),("province" -> preRes.head.get("province").get), ("manufacture" -> toJson(manufacture_name))))
            case List(1,1) =>
                List( Map(("package" -> preRes.head.get("package").get),("specifications" -> preRes.head.get("specifications").get),
                    ("product_type" -> preRes.head.get("product_type").get),("province" ->toJson(edge)), ("manufacture" -> toJson(manufacture_name))))
            case _ =>
                preRes
        }

        (Some(Map(
            "info" -> toJson(result)
    
        )), None)
    }

    def query_auth_tree(data: JsValue)(implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
        val db = cm.modules.get.get("db").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))

        val user_scope = queryUserById(data)._1.get.get("scope").get
        val user_edge = user_scope.\("edge").get.asOpt[List[JsValue]].get
        val user_category = user_scope.\("category").get.asOpt[List[JsValue]].get
        val user_sample = user_scope.\("sample").get.asOpt[Int].get
//        val user_manufacture_name = user_scope.\("manufacture_name").get.asOpt[List[JsValue]].get
//        val user_is_admin = user_scope.\("is_admin").get

        val category = db.queryObject(DBObject(),"config").get.get("category").get.asOpt[List[JsValue]].get

        val province = db.queryObject(DBObject(),"config").get.get("province").get.asOpt[List[JsValue]].get

        val level = category.groupBy(x => (x \ "level").get)
        val atc_one = level.filter(x => x._1.as[Int]==0).map(x => x._2.map(x => (x \ "des").get)).head
        val atc_tow = level.filter(x => x._1.as[Int]==1).map(x => x._2.map(x => (x \ "des").get)).head
        val atc_three = level.filter(x => x._1.as[Int]==2).map(x => x._2.map(x => (x \ "des").get)).head

        var s_atc_one = "{id:\"1\",text:\"治疗类别I[搜索框]\",checked:\"true\",items:"+list2string(atc_one)+"},"
        var s_atc_two = "{id:\"2\",text:\"治疗类别II[搜索框]\",checked:\"true\",items:"+list2string(atc_tow)+"},"
        var s_atc_three = "{id:\"3\",text:\"治疗类别III[搜索框]\",checked:\"true\",items:"+list2string(atc_three)+"},"
        var s_province = "{id:\"4\",text:\"区域[搜索框]\",checked:\"true\",items:"+list2string(province)+"},"
//        val s_sample = "{id:\"5\",text:\"显示样本报告\",checked:\"true\"}"
        var s_sample = "{id:\"5\",text:\"显示样本报告\",checked:\"true\"}"
        
//        val u_c_it = user_category.iterator
//        val u_e_it = user_edge.iterator
//        var temp = ""

        if (user_category.length>0){
            s_atc_one = "{id:\"1\",text:\"治疗类别I[搜索框]\",items:"+list2string(atc_one)+"},"
            s_atc_two = "{id:\"2\",text:\"治疗类别II[搜索框]\",items:"+list2string(atc_tow)+"},"
            s_atc_three = "{id:\"3\",text:\"治疗类别III[搜索框]\",items:"+list2string(atc_three)+"},"
            user_category.foreach{x =>
                val temp=x.toString()
                s_atc_one=s_atc_one.replaceFirst(temp,temp+",checked:\"true\"")
                s_atc_two=s_atc_two.replaceFirst(temp,temp+",checked:\"true\"")
                s_atc_three=s_atc_three.replaceFirst(temp,temp+",checked:\"true\"")
                
            }
//            while (u_c_it.hasNext){
            //                temp = u_c_it.next().toString()
            //                s_atc_one=s_atc_one.replaceFirst(temp,temp+",checked:\"true\"")
            //                s_atc_two=s_atc_two.replaceFirst(temp,temp+",checked:\"true\"")
            //                s_atc_three=s_atc_three.replaceFirst(temp,temp+",checked:\"true\"")
            //            }
        }
        if (user_edge.length>0){
            s_province = "{id:\"4\",text:\"区域[搜索框]\",items:"+list2string(province)+"},"
            user_edge.map{x =>
                val temp=x.toString()
                s_province=s_province.replaceFirst(temp,temp+",checked:\"true\"")
                ""
            }
//            while (u_e_it.hasNext){
//                temp = u_e_it.next().toString()
//                s_province=s_province.replaceFirst(temp,temp+",checked:\"true\"")
//            }
        }
    
        val s_result = "["+s_atc_one+s_atc_two+s_atc_three+s_province+s_sample+"]"
    
        (Some(Map(
            "result" -> toJson(s_result)
        )), None)
    }

    def list2string(list: List[JsValue]): String ={
        var str = "["
        val len=list.length
        list.zipWithIndex.map{case(v,k) =>
            val count=1+k+len+ ""
            str += "{id:\""+count+"\",text:"+v+"},"
            ""
        }
        str = str.substring(0,str.length-1)+"]"
        str
//        val iterable = list.iterator
//        var i = list.length
//        var string = "["
//        while (iterable.hasNext){
//            i=i+1
//            val s = i.toString
//            string += "{id:\""+s+"\",text:"+iterable.next()+"},"
//        }
//        string = string.substring(0,string.length-1)+"]"
//        string
    }
    def LoadConfig(implicit cm : CommonModules ): Option[List[Map[String, JsValue]]] ={
        val db = cm.modules.get.get("db").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
        val config: Option[List[Map[String, JsValue]]] = Some(db.loadAllData("config"))
        if(config.isEmpty) throw new Exception("load config error")
        else config
    }
   
}
