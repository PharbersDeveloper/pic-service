import javax.inject.Inject

import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import play.api.libs.ws.WSClient

import play.api.test._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by alfredyang on 07/07/2017.
  */
class PICClient(ws: WSClient, baseUrl: String)(implicit ec: ExecutionContext) {
    @Inject def this(ws: WSClient, ec: ExecutionContext) = this(ws, "http://127.0.0.1:8888")(ec)
    
    def authWithPasswordTest(name : String, pwd : String) : Future[String] = {
        ws.url(baseUrl + "/auth/password")
            .withHeaders("Accept" -> "application/json", "Content-Type" -> "application/json")
            .post(toJson(Map("user_name" -> name, "pwd" -> pwd)))
            .map { response =>
                (response.json \ "result").get.asOpt[Map[String,JsValue]].get.get("auth_token").get.asOpt[String].get
            }
    }
    
    def getCateTest() : Future[Map[String,JsValue]] = {
        ws.url(baseUrl + "/category")
            .withHeaders("Accept" -> "application/json", "Content-Type" -> "application/json")
            .post(toJson("test"))
            .map { response =>
                (response.json \ "result").get.asOpt[Map[String,JsValue]].get
            }
    }
    
    def conditionSearchResult(token : String, conditions : List[Map[String, JsValue]], skip : Int, contains : String) : List[Future[String]] = {
        
        val resultList = conditions.map { condition =>
            val test = ws.url(baseUrl + "/data/search")
                .withHeaders("Accept" -> "application/json", "Content-Type" -> "application/json")
                .post(toJson(Map("token" -> toJson(token), "condition" -> toJson(condition), "skip" -> toJson(skip))))
                .map { response =>
                    val result = new StringBuffer("ok")
                    val pages = (response.json \ "page").get.asOpt[List[Map[String, JsValue]]].get.head.get("TOTLE_PAGE").get.asOpt[Int].get
                    if (pages > 0) {
                        if ((response.json \ "search_result").asOpt[List[Map[String, JsValue]]].get.filterNot(x => x.get("html").get.asOpt[String].get.contains(contains)).length > 0) {
                            result.append(s"Error with condition=${condition} with contain=${contains}")
                        }
                        if (result == "ok") {
                            for (i <- 1 to pages) {
                                ws.url(baseUrl + "/data/search")
                                    .withHeaders("Accept" -> "application/json", "Content-Type" -> "application/json")
                                    .post(toJson(Map("token" -> toJson(token), "condition" -> toJson(condition), "skip" -> toJson(i))))
                                    .map { response_ =>
                                        if ((response_.json \ "search_result").asOpt[List[Map[String, JsValue]]].get.filterNot(x => x.get("html").get.asOpt[String].get.contains(contains)).length > 0) {
                                            result.append(s"Error with condition=${condition} with contain=${contains}")
                                        }
                                    }
                            }
                        }
                        result.toString
                    } else {
                        println(s"--> No Results! <-- No SearchResults! with with condition=${condition} with contain=${contains}!")
                        result.toString
                    }
                }
            test
        }
        resultList
        
    }
    
    def oneCondition(token : String, condition : Map[String, JsValue],url:String):Future[String]={
        ws.url(baseUrl+url)
            .withHeaders("Accept" -> "application/json","Content-Type" -> "application/json")
            .post(toJson(Map("token" -> toJson(token), "condition" -> toJson(condition))))
            //        .map(response=>(response.json \ "status").asOpt[String].get)
            .map(response=>testUtil.resultHandling(response,condition))
    }
    
    def getToken(name : String, pwd : String) : Future[(String,JsValue)] = {
        ws.url(baseUrl + "/auth/password")
            .withHeaders("Accept" -> "application/json", "Content-Type" -> "application/json")
            .post(toJson(Map("user_name" -> name, "pwd" -> pwd)))
            .map { response =>
                val res=(response.json \ "result" \ "auth_token").asOpt[String].get
                ("token",toJson(res))
            }
    }
    def getCategoryTestInfo(categoryTestInfo:String):Future[List[(String,JsValue)]]={
        ws.url(baseUrl+"/category")
            .withHeaders("Accept" -> "application/json", "Content-Type" -> "application/json")
            .post(toJson(Map("" -> "")))
            .map(response=>{
                (response.json \ "result" \ categoryTestInfo).asOpt[List[String]].get
                    .map{v=>
                        val l:List[String]=(v)::Nil
                        "category" -> toJson(l)
                    }
            }
            )
    }
}
