package Utils


import bmmessages.CommonModules
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

import scala.collection.immutable.Map

/**
  * Created by yym on 7/14/17.
  */
case class TestCondition(test_name:List[String],test_condition:List[List[String]],conditionNum:Int)
object TestCondition {
    /**
      *
      * @param condition 查询条件名称列表
      *                  atc_one ， atc_two，atc_three，oral_name，product_name，package，specifications，manufacture，product_type，province，manufacture_type
      * @param endYear 参照TimeUtil
      * @param endMonth
      * @param timeType
      * @param gap
      * @return
      */
    def condition(condition:List[String],endYear:Int=2016,endMonth:Int=12,timeType:String="year",gap:Int=1)(implicit cm : CommonModules): List[Map[String,JsValue]] ={
        val time=TimeUtil.timeArrInstance(endYear,endMonth,timeType,gap)
        var c:List[Map[String,JsValue]]=List(Map(""->toJson("")))
        val conditionNum=condition.size
        conditionNum match {
            case 1 =>
                c = ConditionUtil.one_condition_with_time(condition.head, time)
            case 2 =>
                c = ConditionUtil.two_condition_with_time(condition(0), condition(1), time)
            case 3 =>
                c = ConditionUtil.three_condition_with_year(condition(0), condition(1), condition(2), time)
    
        }
        c
    }
}
