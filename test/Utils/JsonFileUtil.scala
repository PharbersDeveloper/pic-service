package Utils

import java.io._

import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import play.api.libs.json._

import scala.collection.immutable.Map
import scala.collection.mutable.ArrayBuffer
import scala.io.Source

/**
  * Created by yym on 7/13/17.
  */

object JsonFileUtil {
    /**
      *
      * @param lsts 参考ConditionUtil 中函数返回值
      * @param conditionNum 字母+条件数=>生成Json文件名（eg:"condition2"）
      * @param flag 是否不覆盖文件： true=>不覆盖 ，false=>覆盖
      */
    def writeJson(lsts:List[(String,List[Map[String,JsValue]])],conditionNum:String,flag:Boolean): Unit ={
        val file  = new File("")
        val absolutePath = file.getAbsolutePath()
        val filePath:String=absolutePath+"//test//JsonFile//"+conditionNum+".txt"
        val out=new FileOutputStream(filePath,flag)
        val writer=new PrintWriter(out)
        val res=lsts.map {lst =>
            val t = lst._2.map { x =>
                val condition = toJson(x)
                toJson(Map("condition" -> condition))
            }
            (lst._1 , t)
        }
        var conMap=Map("pic"->toJson("test"))
        for(data<-res){
            conMap=conMap.+(data._1 -> toJson(data._2))
        }
        val conditions=toJson(Map("conditions" -> toJson(conMap)))
        writer.println(conditions)
        writer.close()
    }
    
    /**
      *
      * @param conditionNum Json文件名
      * @param test_case 测试名
      * @return
      */
    def readJson(conditionNum:String,test_case:String): List[Map[String,JsValue]] = {
        val file = new File("")
        val absolutePath = file.getAbsolutePath()
        val filePath = absolutePath + "//test//JsonFile//" + conditionNum + ".txt"
        val lines = Source.fromFile(filePath).getLines()
        val jsArr:ArrayBuffer[JsValue]=new ArrayBuffer[JsValue]
        while (lines.hasNext){
            val js=lines.next()
            jsArr.append(Json.parse(js))
        }

        val conArr=(jsArr.head \ "conditions" \ test_case).as[List[JsValue]]
        val res_map=conArr.map{x =>
            val v=(x \ "condition").get
            val k="condition"
            Map(k -> v)
        }
        res_map
    }
    
}
