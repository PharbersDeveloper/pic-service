package bmlogic.sampleData

import java.util.UUID

import bminjection.db.DBTrait
import bmlogic.sampleData.SampleDataMessage.{msg_rawData2Sample, msg_readRawData}
import bmlogic.sampleData.sampleDataStructure.SampleDataStructure
import bmmessages.{CommonModules, MessageDefines}
import bmpattern.ModuleTrait
import bmutil.ExcelReader.JavaBean.RawData
import bmutil.ExcelReader.RawDataReader
import bmutil.MergeJs
import bmutil.errorcode.ErrorCode
import com.mongodb.casbah.Imports.MongoDBObject
import play.api.libs.json.{JsObject, JsValue}
import play.api.libs.json.Json.toJson

import scala.collection.immutable.Map
import com.mongodb.casbah.Imports._
/**
  * Created by yym on 7/27/17.
  */
object SampleDataModule extends ModuleTrait with SampleDataStructure{
    def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
        case msg_rawData2Sample(data) => rawData2DB(data)
        case msg_readRawData(data) => readRawData(data)(pr)
        case _ => ???
    }
    def rawData2DB(data:JsValue)(implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) ={
        try{
            val db=cm.modules.get.get("db").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
            val data=RawDataReader.read("")
            val objs=data.asInstanceOf[List[RawData]]
            objs.map{obj =>
                val build = MongoDBObject.newBuilder
                build += "rid" ->  UUID.randomUUID()
                build += "GenericName" -> obj.getGenericName
                build += "CompanyName" -> obj.getCompanyName
                build += "Year" -> obj.getYear
                build += "SalesAmount" -> obj.getSalesAmount
                build += "Quantity" -> obj.getQuantity
                build += "Specification" -> obj.getSpecification
                build += "Formulation" -> obj.getFormulation
                build += "Quarter" -> obj.getQuarter
                build += "SinglePackage" -> obj.getSinglePackage
                build += "ROA" -> obj.getROA
                build += "TherapyMicro" -> obj.getTherapyMicro
                build += "TherapyWide" -> obj.getTherapyWide
                build += "City" -> obj.getCity
                db.insertObject(build.result(),"raw_data", "rid")
                ""
            }
            (None,Some(toJson(Map("status" -> toJson("Insert OK")))))
        }catch {
            
            case ex: Exception =>
                (None, Some(ErrorCode.errorToJson(ex.getMessage)))
        }
        
    }
    
    def readRawData(data:JsValue)
                   (pr : Option[Map[String, JsValue]])
                   (implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue])={
        try{
            val mergeJs=MergeJs.dataMergeWithPr(data,pr)
            val db=cm.modules.get.get("db").map (x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
            val group=MongoDBObject("_id" -> "count", "counter" -> MongoDBObject("$sum" -> 1))
            val counter=db.aggregate(MongoDBObject(),"raw_data",group){x =>
                val result=x.getAs[List[BasicDBObject]]("result").get
                val status=x.getAs[Double]("ok").get
                val gr=result.head.getAs[Int]("counter")
                Map("ok" -> toJson(status),
                    "counter"->toJson(gr)
                )
                
            }.get.get("counter").get.as[Int]
            val take=10
            val pages=if(counter % take==0) counter/take else counter/take+1
            val nextIndex = (mergeJs \ "nextIndex").asOpt[Int].getOrElse(1)
            val skip = (nextIndex-1) * take
            val lst : List[Map[String, JsValue]]=db.queryAllObject( "raw_data", skip = skip, take = 10)
            if(mergeJs.as[JsObject].value.filterKeys(x => x=="Warning").isEmpty){
                (None,Some(toJson(Map(
                    "status" -> toJson("ok"),
                    "pages" -> toJson(pages),
                    "currIndex" -> toJson(nextIndex),
                    "result" -> toJson(lst)

                ))))
            }else{
                (None,Some(toJson(Map("status" -> toJson("no")))))
            }
        }catch {
            case ex :Exception =>
                (None , Some(ErrorCode.errorToJson(ex.getMessage)))
        }
        
    }
}
