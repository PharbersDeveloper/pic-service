import java.io.{File, PrintWriter}
import java.text.SimpleDateFormat
import java.util.Calendar

import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.Await
import scala.concurrent.duration._

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import scala.collection.mutable.ArrayBuffer

/**
  * Created by yym on 7/10/17.
  */
object testUtil {
    /**
      *
      * @param endYear 截止年
      * @param endMonth 截止月
      * @param timeType 间隔方式：以年计=>"year";以月计=>"month"
      */
    def timeGenerator(endYear:Int=2016,endMonth:Int=12,timeType:String="year"): (String,JsValue)={
    
        var end :String=""
        var start:String=""
        if(timeType=="month") {
            val sdf = new SimpleDateFormat("yyyyMM")
            val cal = Calendar.getInstance()
            cal.setTime(sdf.parse(endYear+""+endMonth))
            end = sdf.format(cal.getTime)
            cal.add(Calendar.MONTH,-1)
            start = sdf.format(cal.getTime)
        }else {
            val sdf = new SimpleDateFormat("yyyyMM")
            val cal = Calendar.getInstance()
            cal.setTime(sdf.parse(endYear + ""+endMonth))
            end = sdf.format(cal.getTime)
            cal.add(Calendar.YEAR, -1)
            start = sdf.format(cal.getTime)
        }
        ("date",toJson(Map("start"->start, "end"->end)))
        
    }

    def timeArrInstance(endYear:Int,endMonth:Int,timeType:String,gap:Int): List[(String,JsValue)] ={
        val arr: ArrayBuffer[(String,JsValue)] = new ArrayBuffer[(String,JsValue)]()
        if(timeType=="month"){
            for(i<-0 to gap*12) {
                val sdf = new SimpleDateFormat("yyyyMM")
                val cal = Calendar.getInstance()
                cal.setTime(sdf.parse(endYear + "" + endMonth))
                cal.add(Calendar.MONTH, -i)
                val res = timeGenerator(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), timeType)
                arr.append(res)
            }

        }else{
            for(i<-0 to gap){
                val sdf = new SimpleDateFormat("yyyyMM")
                val cal = Calendar.getInstance()
                cal.setTime(sdf.parse(endYear+""+endMonth))
                cal.add(Calendar.YEAR,-i)
                val res=timeGenerator(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),timeType)
                arr.append(res)
            }
        }
        arr.toList
    }

    def twoCondition(time:List[(String,JsValue)],another:List[(String,JsValue)]):List[((String,JsValue),(String,JsValue))]={
        val arr:ArrayBuffer[((String,JsValue),(String,JsValue))]=new ArrayBuffer[((String,JsValue),(String,JsValue))]
        time.map{v1=>
            another.map{v2=>
                val res=(v1,v2)
                arr.append(res)
                res
            }  
        }
        arr.toList
    }

    def resultHandling(response:WSResponse,condition:Map[String,JsValue]): String ={
        var info = "Error!"
        val res = (response.json \ "status").asOpt[String].get
        if(res=="ok"){
            info=res
        }else{
            val empty = (response.json \ "error").get.asOpt[Map[String,JsValue]].get.get("code").get.asOpt[Int].get
            if (empty== -906){
                println(s"--> No result Error! <-- With condition=${condition}")
                info="ok"
            }else{
                info = info+"Condition:"+condition.head._2.asOpt[String].get+"\t"+condition.tail.head._2.asOpt[Map[String,String]].get.tail.head._2+"-"+
                    condition.tail.head._2.asOpt[Map[String,String]].get.head._2+"\tError msg:"+(response.json \ "error" \ "message")+"\n"
            }
        }
        info
    }
    def finalResult(resArr:Array[String]): String ={
        var info="ok"
        resArr.foreach{r=>
            if(r!="ok"){
                info = info.+(r)
            }
        }
        info
    }
    
    def getConditions(lists: List[Map[String,List[String]]],date : List[(String,JsValue)]) : List[Map[String,JsValue]] = {
        var conditions = listToMatrixJsMap(lists)
        var conditions_final : List[Map[String,JsValue]] = conditions.map(x => x.+(date.head._1 -> date.head._2))
        var conditions_temp : List[Map[String,JsValue]] = Nil
        for (d <- date.tail){
            conditions_temp = conditions
            conditions = conditions.map(x => x.+(d._1 -> d._2))
            conditions_final = conditions_final:::conditions
            conditions = conditions_temp
        }
        conditions_final
    }

    def listToMatrixJsMap (lists: List[Map[String,List[String]]]) : List[Map[String,JsValue]] = {
        val head = lists.head
        val tail = lists.tail

        val head_key = head.keys.head
        var result : List[Map[String,JsValue]] = head.get(head_key).map(x => x.map(y => Map(head_key -> toJson(y)))).get
        var result_temp : List[Map[String,JsValue]] = Nil
        var result_final : List[Map[String,JsValue]] = Nil

        for (x <- tail){
            val key : String = x.keys.head

            for (y <- x.get(key).get){
                result_temp = result
                result = result.map(z => z.+(key -> toJson(y)))
                result_final = result_final ::: result
                result = result_temp
            }
            result = result_final
            result_final = Nil
        }
        result
    }

    def listFutureToString(lists : List[Future[String]],time_out : Int)(implicit ec: ExecutionContext) : String = {
        val sucResList = lists.map{ x =>
            val sucRes = Await.ready(x, time_out.seconds)
            sucRes
        }

        val resultTemp = new StringBuffer()
        resultTemp.append("ok")
        for (res <- sucResList){
            res foreach { r =>
                if(r != "ok"){
                    resultTemp.append(r)
                }
            }
        }
        val result = Await.result(Future{resultTemp.toString}, time_out.seconds)
        /*val writer = new PrintWriter(new File("/home/jeorch/桌面/test.txt" ))
        writer.write(result)
        writer.close()*/
        result
    }

    def listStrToStr(listStr:List[String],time_out : Int)(implicit ec: ExecutionContext) : String = {
        val resultTemp = new StringBuffer()
        resultTemp.append("ok")
        for (res <- listStr){
            res foreach { r =>
                if(r != "ok"){
                    resultTemp.append(r)
                }
            }
        }
        var result = Await.result(Future{resultTemp.toString}, time_out.seconds)
        if (result.replaceAll("ok","")==""){
            result = "ok"
        }
        result
    }
}
