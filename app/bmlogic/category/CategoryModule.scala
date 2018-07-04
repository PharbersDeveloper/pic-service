package bmlogic.category

import bminjection.db.DBTrait
import bmlogic.category.categoryData.CategoryData
import bmlogic.category.CategoryMessage._
import bmmessages.{CommonModules, MessageDefines}
import bmpattern.ModuleTrait
import bmutil.errorcode.ErrorCode
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import bminjection.token.AuthTokenTrait

import scala.collection.immutable.Map

/**
  * Created by yym on 6/15/17.
  */
object CategoryModule extends ModuleTrait with CategoryData {
    
    
    def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_Category(data) => Category(data)
        case msg_LinkageCategory(data)=>categoryLinkage(data)
        case _ => ???
    }

    //根据用户权限 过滤可查询字段
    def categoryAuthFilter(data : JsValue)(implicit cm : CommonModules):List[Map[String, JsValue]]={
        val token = (data \ "token").get.asOpt[String].map(x => x).getOrElse(throw new Exception("can't find token"))
        val att = cm.modules.get.get("att").map (x => x.asInstanceOf[AuthTokenTrait]).getOrElse(throw new Exception("no encrypt impl"))
        val auth=att.decrypt2JsValue(token)
        val categorys = (auth \ "scope" \ "category").get.asOpt[List[String]].getOrElse(throw  new Exception("prase error"))
//        val manufacture_name = (auth \ "scope" \ "manufacture_name").get.asOpt[List[String]].getOrElse(throw  new Exception("prase error"))
//        val edge = (auth \ "scope" \ "edge").get.asOpt[List[String]].getOrElse(throw  new Exception("prase error"))
        val c = LoadCategory
        var cateRes=c.getOrElse(throw new Exception("have on data"))
        if(categorys.length != 0){
            cateRes=categorys.map{x =>
                val atc_three=x
                val atc_two=findParent(atc_three)
                val atc_one=findParent(atc_two)
                val one=c.getOrElse(throw new Exception("have on data")).filter(y => y.get("des").get.as[String] == atc_one)
                val two=c.getOrElse(throw new Exception("have on data")).filter(y => y.get("des").get.as[String] == atc_two)
                val three=c.getOrElse(throw new Exception("have on data")).filter(y => y.get("des").get.as[String] == atc_three)
                val children_three=findOtherChildren(three)
                val list=(one::two::three::children_three::Nil).flatten
                list
            }.flatten
        }
        if(cateRes.isEmpty) throw new Exception("category authority filter error")
        else cateRes
    }
 
    def Category(data: JsValue)(implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
        try{
            val fil=Option(categoryAuthFilter(data))
            val result =fil match {
                case None => None
                case Some(ca) =>
                    val atc_one = ca.filter(x => x.get("level").get.as[Int] == 0).map(x => x.get("des").get.as[String]).distinct
                    val atc_tow = ca.filter(x => x.get("level").get.as[Int] == 1).map(x => x.get("des").get.as[String]).distinct
                    val atc_three = ca.filter(x => x.get("level").get.as[Int] == 2).map(x => x.get("des").get.as[String]).distinct
                    val oral = ca.filter(x => x.get("level").get.as[Int] == 3).map(x => x.get("des").get.as[String]).distinct
                    val product = ca.filter(x => x.get("level").get.as[Int] == 3).map(x => x.get("def").get.as[String]).distinct
                    Some(Map("atc_one" -> toJson(atc_one),
                        "atc_tow" -> toJson(atc_tow),
                        "atc_three" -> toJson(atc_three),
                        "oral_name" -> toJson(oral),
                        "product" -> toJson(product)))
                case _ => ???
                
            }
            (result, None)
        }catch {
            case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
        
    }

    //医疗目录联动显示
    def categoryLinkage(data:JsValue)(implicit cm : CommonModules):(Option[Map[String,JsValue]],Option[JsValue])={
        try{
            val category=categoryAuthFilter(data)
            val level=(data \ "level").as[String]
            val des=(data \ "des").get.as[String]
            val result=level match {
                 case "0" =>
                    val atc_one=des
                    val children_one=findChildren(category
                        .filter(x => x.get("des").get.as[String]==des).distinct)
                    val atc_two=children_one.map(x => x.get("des").get.as[String])
                    val children_two=findChildren(children_one)
                    val atc_three=children_two.map(x => x.get("des").get.as[String])
                    val children_three=findOtherChildren(children_two)
                    val oral_name=children_three.map(x => x.get("des").get.as[String]).distinct
                    val product = children_three.map(x => x.get("def").get.as[String]).distinct
                    Some(Map("atc_one" -> toJson(List(atc_one)),
                        "atc_tow" -> toJson(atc_two),
                        "atc_three" -> toJson(atc_three),
                        "oral_name" -> toJson(oral_name),
                        "product" -> toJson(product)))
                 case "1" =>
                     val atc_two=des
                     val atc_one=findParent(atc_two)
                     val children_two=findChildren(category.filter(x => x.get("des").get.as[String] == des).distinct)
                     val atc_three=children_two.map(x => x.get("des").get.as[String])
                     val children_three=findOtherChildren(children_two)
                     val oral_name=children_three.map(x => x.get("des").get.as[String]).distinct
                     val product = children_three.map(x => x.get("def").get.as[String]).distinct
                     Some(Map("atc_one" -> toJson(List(atc_one)),
                         "atc_tow" -> toJson(List(atc_two)),
                         "atc_three" -> toJson(atc_three),
                         "oral_name" -> toJson(oral_name),
                         "product" -> toJson(product)))
                 case "2" =>
                     val atc_three=des
                     val atc_two=findParent(atc_three)
                     val atc_one=findParent(atc_two)
                     val children_three=findOtherChildren(category.filter(x => x.get("des").get.as[String] == des).distinct)
                     val oral_name=children_three.map(x => x.get("des").get.as[String]).distinct
                     val product = children_three.map(x => x.get("def").get.as[String]).distinct
                     Some(Map("atc_one" -> toJson(List(atc_one)),
                         "atc_tow" -> toJson(List(atc_two)),
                         "atc_three" -> toJson(List(atc_three)),
                         "oral_name" -> toJson(oral_name),
                         "product" -> toJson(product)))
                 case "3" =>
                     val oral_name=des
                     val three=category.filter(x => x.get("des").get.as[String]==des).distinct
                     val product=three.map(x=>x.get("def").get.as[String])
                     val atc_three=findOtherParent(oral_name)
                     val atc_two=findParent(atc_three)
                     val atc_one=findParent(atc_two)
                     Some(Map("atc_one" -> toJson(List(atc_one)),
                         "atc_tow" -> toJson(List(atc_two)),
                         "atc_three" -> toJson(List(atc_three)),
                         "oral_name" -> toJson(List(oral_name)),
                         "product" -> toJson(product)))
                 case "4" =>
                     val product=des
                     val three=category.filter(x => x.get("def").get.as[String]==des).distinct
                     val oral_name=three.head.get("des").get.as[String]
                     val atc_three=findOtherParent(oral_name)
                     val atc_two=findParent(atc_three)
                     val atc_one=findParent(atc_two)
                     Some(Map("atc_one" -> toJson(List(atc_one)),
                         "atc_tow" -> toJson(List(atc_two)),
                         "atc_three" -> toJson(List(atc_three)),
                         "oral_name" -> toJson(List(oral_name)),
                         "product" -> toJson(List(product))))
                 case _ => ???
          
            }
            (result, None)
        } catch {
            case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
        
    }
    def findChildren(parents:List[Map[String,JsValue]])(implicit cm : CommonModules ): (List[Map[String,JsValue]]) ={
        val category= LoadCategory.get
        val findKeys=parents.map(x => x.get("def").get.as[String])
        val lsts=findKeys.map{x =>
            category.filter(y => y.get("parent").get.as[String]==x).distinct
        }
        lsts.flatten
    }
    def findOtherChildren(parents:List[Map[String,JsValue]])(implicit cm : CommonModules ): (List[Map[String,JsValue]]) ={
        val category= LoadCategory.get
        val findKeys=parents.map(x => x.get("des").get.as[String])
        val lsts=findKeys.map{x =>
            category.filter(y => y.get("parent").get.as[String]==x).distinct
        }
        lsts.flatten
    }
    def findParent(child:String)(implicit cm : CommonModules ):String={
        val category= LoadCategory.get
        val parentKey=category.filter(x => x.get("des").get.as[String] == child).distinct.head
        .get("parent").get.as[String]
        val parent=category.filter(x => x.get("def").get.as[String] == parentKey).distinct.head
        .get("des").get.as[String]
        parent
        
    }
    def findOtherParent(child:String)(implicit cm : CommonModules ):String={
        val category= LoadCategory.get
        val parentKey=category.filter(x => x.get("des").get.as[String] == child).distinct.head
            .get("parent").get.as[String]
        parentKey
    
    }
    def LoadCategory(implicit cm : CommonModules ): Option[List[Map[String, JsValue]]] ={
            val db = cm.modules.get.get("db").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
            val category: Option[List[Map[String, JsValue]]] = Some(db.loadAllData("category"))
            if(category.isEmpty) throw new Exception("load category error")
            else category
    }
    

}
