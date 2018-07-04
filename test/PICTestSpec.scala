
import java.io.{File, PrintWriter}
import java.util.Calendar

import play.core.server.Server
import play.api.routing.sird._
import play.api.mvc._
import play.api.libs.json._
import play.api.test._
import Utils._
import bmmessages.CommonModules

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAll
import play.api.libs.json.Json.toJson
import play.api.libs.ws.WSClient

/**
  * Created by alfredyang on 07/07/2017.
  */
class PICTestSpec extends Specification with BeforeAll{
    import scala.concurrent.ExecutionContext.Implicits.global
    
    val user_name = "alfred"
    val pwd = "12345"
    
    /**
      * 参数说明：（Token & dbChanged）
      * 测试时把token临时调成10天 在bmlogic.auth.AuthModule.authWithPassword修改reVal值
      * 如果dbChanged为 true 则重新写入新的condition组合，如果为 false 则不更新conditions的配置文件（JsonFile）
      */
    var token : String = null
    val dbChanged : Boolean = false
    
    /**
      * 在这里输入搜索的时间条件：
      * timeType 间隔方式：以年计=>"year";以月计=>"month"，gap：间隔长短（1表示1年或1月）
      */
    val endYear : Int = 2017
    val endMonth : Int = 2
    val timeType : String = "year"
    val gap : Int = 1
    
    val dateYear = testUtil.timeArrInstance(endYear,endMonth,"year",gap)
    val dateMonth = testUtil.timeArrInstance(endYear,endMonth,"month",gap)
    
    val marketUrl :String = "/data/calc/market"
    val trendUrl :String = "/data/calc/trend"
    val quantityUrl :String = "/data/calc/quantity"
    val percentageUrl :String = "/data/calc/percentage"
    val urlList : List[String] = List(marketUrl,trendUrl,quantityUrl)
    
    var atc_one :Map[String,List[String]]= null
    var atc_two :Map[String,List[String]]= null
    var atc_three :Map[String,List[String]]= null
    var oral :Map[String,List[String]]= null
    var product :Map[String,List[String]]= null
    var edge :Map[String,List[String]]= null
    var manufacture_name :Map[String,List[String]]= null
    
    val contains = "他汀类"
    
    val skip = 1
    val time_out = 120
    
    override def is = s2"""
        This is a PIC specification to check the 'conditionSearch' string

            The 'PIC ' conditionSearch functions should
                auth with password user_name:${user_name},pwd:${pwd}                                    $authToken
                testCase3_15 result must be "ok"!                                                       $testCase3_15
                testCase3_15_search with condition with 治疗1类 && 年 双条件混合查询                        $testCase3_15_search
                testCase3_16 result must be "ok"!                                                       $testCase3_16
                testCase3_16_search with condition with 治疗1类 && 月 双条件混合查询                        $testCase3_16_search
                testCase3_17 result must be "ok"!                                                       $testCase3_17
                testCase3_17_search with condition with 治疗2类 && 年 双条件混合查询                        $testCase3_17_search
                testCase3_18 result must be "ok"!                                                       $testCase3_18
                testCase3_18_search with condition with 治疗2类 && 月 双条件混合查询                        $testCase3_18_search
                testCase3_19 result must be "ok"!                                                       $testCase3_19
                testCase3_19_search with condition with 治疗3类 && 年 双条件混合查询                        $testCase3_19_search
                testCase3_20 result must be "ok"!                                                       $testCase3_20
                testCase3_20_search with condition with 治疗3类 && 月 双条件混合查询                        $testCase3_20_search
                testCase3_21 result must be "ok"!                                                       $testCase3_21
                testCase3_21_search with condition with 治疗3类 && 通用名 && 年 三条件混合查询               $testCase3_21_search
                testCase3_22 result must be "ok"!                                                       $testCase3_22
                testCase3_22_search with condition with 治疗3类 && 通用名 && 月 三条件混合查询               $testCase3_22_search
                testCase3_23 result must be "ok"!                                                       $testCase3_23
                testCase3_23_search with condition with 治疗3类 && 产品名 && 年 三条件混合查询               $testCase3_23_search
                testCase3_24 result must be "ok"!                                                       $testCase3_24
                testCase3_24_search with condition with 治疗3类 && 产品名 && 月 三条件混合查询               $testCase3_24_search
                testCase3_25 result must be "ok"!                                                               $testCase3_25
                testCase3_25_search with condition with 治疗3类 && 产品名 && 省份 && 年 四条件混合查询                $testCase3_25_search
                testCase3_26 result must be "ok"!                                                               $testCase3_26
                testCase3_26_search with condition with 治疗3类 && 产品名 && 省份 && 月 四条件混合查询                $testCase3_26_search
                testCase3_27 result must be "ok"!                                                               $testCase3_27
                testCase3_27_search with condition with 治疗3类 && 产品名 && 省份 && 生产商 && 年 五条件混合查询       $testCase3_27_search
                testCase3_28 result must be "ok"!                                                               $testCase3_28
                testCase3_28_search with condition with 治疗3类 && 产品名 && 省份 && 生产商 && 月 五条件混合查询       $testCase3_28_search
                                                                              """
    
    override def beforeAll(): Unit = {
//        if (dbChanged){
//            val listC2 = List(
//                ("test3_15",ConditionUtil.one_condition_with_time("atc_one",dateYear)),
//                ("test3_16",ConditionUtil.one_condition_with_time("atc_one",dateMonth)),
//                ("test3_17",ConditionUtil.one_condition_with_time("atc_two",dateYear)),
//                ("test3_18",ConditionUtil.one_condition_with_time("atc_two",dateMonth)),
//                ("test3_19",ConditionUtil.one_condition_with_time("atc_three",dateYear)),
//                ("test3_20",ConditionUtil.one_condition_with_time("atc_three",dateMonth))
//            )
//            val listC3 = List(
//                ("test3_21",ConditionUtil.two_condition_with_time("atc_three","oral_name",dateYear)),
//                ("test3_22",ConditionUtil.two_condition_with_time("atc_three","oral_name",dateMonth)),
//                ("test3_23",ConditionUtil.two_condition_with_time("atc_three","product_name",dateYear)),
//                ("test3_24",ConditionUtil.two_condition_with_time("atc_three","product_name",dateMonth))
//            )
//            val listC4_testCase3_25 = List(
//                ("test3_25",ConditionUtil.three_condition_with_year("atc_three","product_name","province",dateYear))
//            )
//            val listC4_testCase3_26 = List(
//                ("test3_26",ConditionUtil.three_condition_with_month("atc_three","product_name","province",dateMonth))
//            )
//            val listC5_testCase3_27 = List(
//                ("test3_27",ConditionUtil.four_condition_with_year("atc_three","product_name","province","manufacture",dateYear))
//            )
//            val listC5_testCase3_28 = List(
//                ("test3_28",ConditionUtil.four_condition_with_month("atc_three","product_name","province","manufacture",dateMonth))
//            )
//            JsonFileUtil.writeJson(listC2,"c2",false)
//            JsonFileUtil.writeJson(listC3,"c3",false)
//            JsonFileUtil.writeJson(listC4_testCase3_25,"c4_testCase3_25",false)
//            JsonFileUtil.writeJson(listC4_testCase3_26,"c4_testCase3_26",false)
//            JsonFileUtil.writeJson(listC5_testCase3_27,"c5_testCase3_27",false)
//            JsonFileUtil.writeJson(listC5_testCase3_28,"c5_testCase3_28",false)
//        }
        println("beforeAll !")
    }
    
    def authToken =
        WsTestClient.withClient { client =>
            val result = Await.result(
                new PICClient(client, "http://127.0.0.1:8888").authWithPasswordTest(user_name, pwd), 30.seconds)
            token = result
            result must_!= ""
        }
    
    def testCase3_15 =
        WsTestClient.withClient{client=>
            val conditions = JsonFileUtil.readJson("c2","test3_15")
            val listStr = urlList.map{ url =>
                println(s"&&3_15_url=>${url}&&")
                val res = twoConditionCombination(client,url,token,conditions,30)
                println(s"&&3_15_u_res=>${res}&&")
                res
            }
            val result = testUtil.listStrToStr(listStr,time_out)
            result must_== "ok"
        }
    def testCase3_15_search =
        WsTestClient.withClient { client =>
            val conditions = JsonFileUtil.readJson("c2","test3_15")
            val resList = new PICClient(client, "http://127.0.0.1:8888").conditionSearchResult(token, conditions, skip, contains)
            val result = testUtil.listFutureToString(resList,time_out)
            result must_== "ok"
        }
    def testCase3_16 =
        WsTestClient.withClient{client=>
            val conditions = JsonFileUtil.readJson("c2","test3_16")
            val listStr = urlList.map{ url =>
                println(s"&&3_16_url=>${url}&&")
                val res = twoConditionCombination(client,url,token,conditions,30)
                println(s"&&3_16_u_res=>${res}&&")
                res
            }
            val result = testUtil.listStrToStr(listStr,time_out)
            result must_== "ok"
        }
    def testCase3_16_search =
        WsTestClient.withClient { client =>
            val conditions = JsonFileUtil.readJson("c2","test3_16")
            val resList = new PICClient(client, "http://127.0.0.1:8888").conditionSearchResult(token, conditions, skip, contains)
            val result = testUtil.listFutureToString(resList,time_out)
            result must_== "ok"
        }
    
    
    def testCase3_17 =
        WsTestClient.withClient{client=>
            val conditions = JsonFileUtil.readJson("c2","test3_17")
            val listStr = urlList.map{ url =>
                println(s"&&3_17_url=>${url}&&")
                val res = twoConditionCombination(client,url,token,conditions,30)
                println(s"&&3_17_u_res=>${res}&&")
                res
            }
            val result = testUtil.listStrToStr(listStr,time_out)
            result must_== "ok"
        }
    def testCase3_17_search =
        WsTestClient.withClient { client =>
            val conditions = JsonFileUtil.readJson("c2","test3_17")
            val resList = new PICClient(client, "http://127.0.0.1:8888").conditionSearchResult(token, conditions, skip, contains)
            val result = testUtil.listFutureToString(resList,time_out)
            result must_== "ok"
        }
    def testCase3_18 =
        WsTestClient.withClient{client=>
            val conditions = JsonFileUtil.readJson("c2","test3_18")
            val listStr = urlList.map{ url =>
                println(s"&&3_18_url=>${url}&&")
                val res = twoConditionCombination(client,url,token,conditions,30)
                println(s"&&3_18_u_res=>${res}&&")
                res
            }
            val result = testUtil.listStrToStr(listStr,time_out)
            result must_== "ok"
        }
    def testCase3_18_search =
        WsTestClient.withClient { client =>
            val conditions = JsonFileUtil.readJson("c2","test3_18")
            val resList = new PICClient(client, "http://127.0.0.1:8888").conditionSearchResult(token, conditions, skip, contains)
            val result = testUtil.listFutureToString(resList,time_out)
            result must_== "ok"
        }
    
    def testCase3_19 =
        WsTestClient.withClient{client=>
            val conditions = JsonFileUtil.readJson("c2","test3_19")
            val listStr = urlList.map{ url =>
                println(s"&&3_19_url=>${url}&&")
                val res = twoConditionCombination(client,url,token,conditions,60)
                println(s"&&3_19_u_res=>${res}&&")
                res
            }
            val result = testUtil.listStrToStr(listStr,time_out)
            result must_== "ok"
        }
    def testCase3_19_search =
        WsTestClient.withClient { client =>
            val conditions = JsonFileUtil.readJson("c2","test3_19")
            val resList = new PICClient(client, "http://127.0.0.1:8888").conditionSearchResult(token, conditions, skip, contains)
            val result = testUtil.listFutureToString(resList,time_out)
            result must_== "ok"
        }
    def testCase3_20 =
        WsTestClient.withClient{client=>
            val conditions = JsonFileUtil.readJson("c2","test3_20")
            val listStr = urlList.map{ url =>
                println(s"&&3_20_url=>${url}&&")
                val res = twoConditionCombination(client,url,token,conditions,60)
                println(s"&&3_20_u_res=>${res}&&")
                res
            }
            val result = testUtil.listStrToStr(listStr,time_out)
            result must_== "ok"
        }
    def testCase3_20_search =
        WsTestClient.withClient { client =>
            val conditions = JsonFileUtil.readJson("c2","test3_20")
            val resList = new PICClient(client, "http://127.0.0.1:8888").conditionSearchResult(token, conditions, skip, contains)
            val result = testUtil.listFutureToString(resList,time_out)
            result must_== "ok"
        }
    
    def testCase3_21 =
        WsTestClient.withClient{client=>
            val conditions = JsonFileUtil.readJson("c3","test3_21")
            val listStr = urlList.map{ url =>
                println(s"&&3_21_url=>${url}&&")
                val res = twoConditionCombination(client,url,token,conditions,60)
                println(s"&&3_21_u_res=>${res}&&")
                res
            }
            val result = testUtil.listStrToStr(listStr,time_out)
            result must_== "ok"
        }
    def testCase3_21_search =
        WsTestClient.withClient { client =>
            val conditions = JsonFileUtil.readJson("c3","test3_21")
            val resList = new PICClient(client, "http://127.0.0.1:8888").conditionSearchResult(token, conditions, skip, contains)
            val result = testUtil.listFutureToString(resList,time_out)
            result must_== "ok"
        }
    
    def testCase3_22 =
        WsTestClient.withClient{client=>
            val conditions = JsonFileUtil.readJson("c3","test3_22")
            val listStr = urlList.map{ url =>
                println(s"&&3_22_url=>${url}&&")
                val res = twoConditionCombination(client,url,token,conditions,60)
                println(s"&&3_22_u_res=>${res}&&")
                res
            }
            val result = testUtil.listStrToStr(listStr,time_out)
            result must_== "ok"
        }
    
    def testCase3_22_search =
        WsTestClient.withClient { client =>
            val conditions = JsonFileUtil.readJson("c3","test3_22")
            
            val resList = new PICClient(client, "http://127.0.0.1:8888").conditionSearchResult(token, conditions, skip, contains)
            
            val result = testUtil.listFutureToString(resList,time_out)
            
            result must_== "ok"
        }
    
    def testCase3_23 =
        WsTestClient.withClient{client=>
            val conditions = JsonFileUtil.readJson("c3","test3_23")
            val listStr = urlList.map{ url =>
                println(s"&&3_23_url=>${url}&&")
                val res = twoConditionCombination(client,url,token,conditions,60)
                println(s"&&3_23_u_res=>${res}&&")
                res
            }
            val result = testUtil.listStrToStr(listStr,time_out)
            result must_== "ok"
        }
    
    def testCase3_23_search =
        WsTestClient.withClient { client =>
            val conditions = JsonFileUtil.readJson("c3","test3_23")
            
            val resList = new PICClient(client, "http://127.0.0.1:8888").conditionSearchResult(token, conditions, skip, contains)
            
            val result = testUtil.listFutureToString(resList,time_out)
            
            result must_== "ok"
        }
    
    def testCase3_24 =
        WsTestClient.withClient{client=>
            val conditions = JsonFileUtil.readJson("c3","test3_24")
            val listStr = urlList.map{ url =>
                println(s"&&3_24_url=>${url}&&")
                val res = twoConditionCombination(client,url,token,conditions,60)
                println(s"&&3_24_u_res=>${res}&&")
                res
            }
            val result = testUtil.listStrToStr(listStr,time_out)
            result must_== "ok"
        }
    
    def testCase3_24_search =
        WsTestClient.withClient { client =>
            val conditions = JsonFileUtil.readJson("c3","test3_24")
            
            val resList = new PICClient(client, "http://127.0.0.1:8888").conditionSearchResult(token, conditions, skip, contains)
            
            val result = testUtil.listFutureToString(resList,time_out)
            
            result must_== "ok"
        }
    
    def testCase3_25 =
        WsTestClient.withClient{client=>
            val conditions = JsonFileUtil.readJson("c4_testCase3_25","test3_25")
            val listStr = urlList.map{ url =>
                val res = twoConditionCombination(client,url,token,conditions,60)
                res
            }
            val result = testUtil.listStrToStr(listStr,time_out)
            result must_== "ok"
        }
    
    def testCase3_25_search =
        WsTestClient.withClient { client =>
            val conditions = JsonFileUtil.readJson("c4_testCase3_25","test3_25")
            
            val resList = new PICClient(client, "http://127.0.0.1:8888").conditionSearchResult(token, conditions, skip, contains)
            
            val result = testUtil.listFutureToString(resList,time_out)
            
            result must_== "ok"
        }
    
    def testCase3_26 =
        WsTestClient.withClient{client=>
            val conditions = JsonFileUtil.readJson("c4_testCase3_26","test3_26")
            val listStr = urlList.map{ url =>
                val res = twoConditionCombination(client,url,token,conditions,60)
                res
            }
            val result = testUtil.listStrToStr(listStr,time_out)
            result must_== "ok"
        }
    
    def testCase3_26_search =
        WsTestClient.withClient { client =>
            val conditions = JsonFileUtil.readJson("c4_testCase3_26","test3_26")
            val resList = new PICClient(client, "http://127.0.0.1:8888").conditionSearchResult(token, conditions, skip, contains)
            
            val result = testUtil.listFutureToString(resList,time_out)
            
            result must_== "ok"
        }
    
    def testCase3_27 =
        WsTestClient.withClient{client=>
            val conditions = JsonFileUtil.readJson("c5_testCase3_27","test3_27")
            val listStr = urlList.map{ url =>
                val res = twoConditionCombination(client,url,token,conditions,60)
                res
            }
            val result = testUtil.listStrToStr(listStr,time_out)
            result must_== "ok"
        }
    
    def testCase3_27_search =
        WsTestClient.withClient { client =>
            val conditions = JsonFileUtil.readJson("c5_testCase3_27","test3_27")
            
            val resList = new PICClient(client, "http://127.0.0.1:8888").conditionSearchResult(token, conditions, skip, contains)
            
            val result = testUtil.listFutureToString(resList,time_out)
            
            result must_== "ok"
        }
    
    def testCase3_28 =
        WsTestClient.withClient{client=>
            val conditions = JsonFileUtil.readJson("c5_testCase3_28","test3_28")
            val listStr = urlList.map{ url =>
                val res = twoConditionCombination(client,url,token,conditions,60)
                res
            }
            val result = testUtil.listStrToStr(listStr,time_out)
            result must_== "ok"
        }
    
    def testCase3_28_search =
        WsTestClient.withClient { client =>
            val conditions = JsonFileUtil.readJson("c5_testCase3_28","test3_28")
            
            val resList = new PICClient(client, "http://127.0.0.1:8888").conditionSearchResult(token, conditions, skip, contains)
            
            val result = testUtil.listFutureToString(resList,time_out)
            
            result must_== "ok"
        }
    
    def twoConditionCombination(client:WSClient,conditionSearchUrl:String,token:String,lists:List[Map[String,JsValue]] ,time_out:Int): String ={
        val resArr=lists.map{condition=>
            val res=Await.result(
                new PICClient(client, "http://127.0.0.1:8888").oneCondition(token,condition,conditionSearchUrl), 60.seconds)
            res
        }.toArray
        testUtil.finalResult(resArr)
    }
    
}
