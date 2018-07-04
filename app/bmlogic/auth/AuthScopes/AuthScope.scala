package bmlogic.auth.AuthScopes

import bmutil.dao.from
import com.mongodb.casbah.Imports._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

/**
  * Created by alfredyang on 02/06/2017.
  */
trait AuthScope {
    def pushEdgeScope(data : JsValue) : MongoDBList = {
        val result = MongoDBList.newBuilder
        (data \ "scope" \ "edge").asOpt[List[String]].map (x => x).getOrElse(Nil).foreach(result += _)
        result.result
    }

    def pushProduceLevelScope(data : JsValue) : DBObject = {
        val result = MongoDBList.newBuilder
        (data \ "scope" \ "category").asOpt[List[String]].map (x => x).getOrElse(Nil).foreach(result += _)
        result.result
    }

    def pushManufactureNameScope(data : JsValue) : MongoDBList = {
        val result = MongoDBList.newBuilder
        (data \ "scope" \ "manufacture_name").asOpt[List[String]].map (x => x).getOrElse(Nil).foreach(result += _)
        result.result
    }

    def queryEdgeScope(obj : MongoDBObject) : JsValue = {
        val scope = obj.getAs[MongoDBObject]("scope").map (x => x).getOrElse(throw new Exception("scope db prase error"))
        val lst = scope.getAs[List[String]]("edge").map (x => x).getOrElse(throw new Exception("edge db prase error"))
        toJson(lst)
    }

    def queryProductLevelScope(obj : MongoDBObject) : JsValue = {
        val scope = obj.getAs[MongoDBObject]("scope").map (x => x).getOrElse(throw new Exception("scope db prase error"))
        val lst = scope.getAs[List[String]]("category").map (x => x).getOrElse(throw new Exception("category db prase error"))
        toJson(lst)
    }

    def queryManufactureNameScope(obj : MongoDBObject) : JsValue = {
        val scope = obj.getAs[MongoDBObject]("scope").map (x => x).getOrElse(throw new Exception("scope db prase error"))
        val lst = scope.getAs[List[String]]("manufacture_name").map (x => x).getOrElse(throw new Exception("manufacture_name db prase error"))
        toJson(lst)
    }

    def queryIsAdminScope(obj : MongoDBObject) : JsValue = {
        val scope = obj.getAs[MongoDBObject]("scope").map (x => x).getOrElse(throw new Exception("scope db prase error"))
        val is_admin = scope.getAs[Number]("is_admin").map (x => x).getOrElse(throw new Exception("is_admin db prase error"))
        toJson(is_admin.intValue)
    }
    
    def querySampleScope(obj:MongoDBObject) : JsValue={
        val scope = obj.getAs[MongoDBObject]("scope").map(x => x).getOrElse(throw new Exception("scope db  prase error"))
        val sample=scope.getAs[Int]("sample").map(x => x).getOrElse(throw  new Exception("sample db prase error"))
        toJson(sample)
    }

    def productLevel2Category(lst : List[String]) : List[String] = {
        val cat = (from db() in "config" where ("index" -> "PIC")
                    select (x =>
                        x.getAs[BasicDBList]("category").get.toList.asInstanceOf[List[BasicDBObject]]
                    )).toList.head

        val f = cat.filter(x => lst.contains(x.getString("des")))

        val one = (f.filter(x => x.getInt("level") == 0)).map (x => one2three(x.getString("des"), cat)).flatten
        val tow = (f.filter(x => x.getInt("level") == 1)).map (x => tow2three(x.getString("des"), cat)).flatten
        val three = f.filter(x => x.getInt("level") == 2)

        three.map (x => x.getString("des")) ::: one ::: tow
    }

    def one2three(s : String, cat : List[BasicDBObject]) : List[String] = {
        (cat.find(x => x.getString("des") == s && x.getInt("level") == 0).map { x =>
            cat.filter(p => p.getString("parent") == x.getString("def"))
        }.getOrElse(Nil)).map (x => x.getString("des")).map (tow2three(_, cat)).flatten
    }

    def tow2three(s : String, cat : List[BasicDBObject]) : List[String] =
        (cat.find(x => x.getString("des") == s && x.getInt("level") == 1).map { x =>
            cat.filter(p => p.getString("parent") == x.getString("def"))
        }.getOrElse(Nil)).map (x => x.getString("des"))
}
