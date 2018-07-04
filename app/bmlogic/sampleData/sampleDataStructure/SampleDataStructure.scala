package bmlogic.sampleData.sampleDataStructure

import java.util.UUID

import com.mongodb.DBObject
import com.mongodb.casbah.Imports._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

/**
  * Created by yym on 7/27/17.
  */
trait SampleDataStructure {
    
    
    implicit val m2d : JsValue => DBObject = { js =>
        val build = MongoDBObject.newBuilder
        build += "GenericName" -> (js \ "GenericName").asOpt[String].map (x => x).getOrElse(Unit)
        build += "CompanyName" -> (js \ "CompanyName").asOpt[String].map (x => x).getOrElse(Unit)
        build += "Year" -> (js \ "Year").asOpt[String].map (x => x).getOrElse(Unit)
        build += "SalesAmount" -> (js \ "SalesAmount").asOpt[Long].map (x => x).getOrElse(Unit)
        build += "Quantity" ->(js \ "Quantity").asOpt[Long].map (x => x).getOrElse(Unit)
        build += "Specification" -> (js \ "Specification").asOpt[String].map (x => x).getOrElse(Unit)
        build += "Formulation" -> (js \ "Formulation").asOpt[String].map (x => x).getOrElse(Unit)
        build += "Quarter" -> (js \ "Quarter").asOpt[Int].map (x => x).getOrElse(Unit)
        build += "SinglePackage" -> (js \ "SinglePackage").asOpt[Int].map (x => x).getOrElse(Unit)
        build += "ROA" -> (js \ "ROA").asOpt[String].map (x => x).getOrElse(Unit)
        build += "TherapyMicro" -> (js \ "TherapyMicro").asOpt[String].map (x => x).getOrElse(Unit)
        build += "TherapyWide" -> (js \ "TherapyWide").asOpt[String].map (x => x).getOrElse(Unit)
        build += "City" -> (js \ "City").asOpt[String].map (x => x).getOrElse(Unit)
        
        build.result()
    }
    
    
    implicit val d2m : DBObject => Map[String, JsValue] = { obj =>
        Map(
            "GenericName" -> toJson(obj.getAs[String]("GenericName").map(x => x).getOrElse(throw new Exception("GenericName db prase error"))),
            "CompanyName" -> toJson(obj.getAs[String]("CompanyName").map(x => x).getOrElse(throw new Exception("CompanyName db prase error"))),
            "Year" -> toJson(obj.getAs[String]("Year").map(x => x).getOrElse(throw new Exception("Year db prase error"))),
            "Quantity" -> toJson(obj.getAs[Long]("Quantity").map(x => x).getOrElse(throw new Exception("Quantity db prase error"))),
            "SalesAmount" -> toJson(obj.getAs[Long]("SalesAmount").map(x => x).getOrElse(throw new Exception("SalesAmount db prase error"))),
            "Specification" -> toJson(obj.getAs[String]("Specification").map(x => x).getOrElse(throw new Exception("Specification db prase error"))),
            "Formulation" -> toJson(obj.getAs[String]("Formulation").map(x => x).getOrElse(throw new Exception("Formulation db prase error"))),
            "Quarter" -> toJson(obj.getAs[Int]("Quarter").map(x => x).getOrElse(throw new Exception("Quarter db prase error"))),
            "SinglePackage" -> toJson(obj.getAs[Int]("SinglePackage").map(x => x).getOrElse(throw new Exception("SinglePackage db prase error"))),
            "ROA" -> toJson(obj.getAs[String]("ROA").map(x => x).getOrElse(throw new Exception("ROA db prase error"))),
            "TherapyMicro" -> toJson(obj.getAs[String]("TherapyMicro").map(x => x).getOrElse(throw new Exception("TherapyMicro db prase error"))),
            "TherapyWide" -> toJson(obj.getAs[String]("TherapyWide").map(x => x).getOrElse(throw new Exception("TherapyWide db prase error"))),
            "City" -> toJson(obj.getAs[String]("City").map(x => x).getOrElse(throw new Exception("City db prase error"))))
    }
    
}
