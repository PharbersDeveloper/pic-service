package bmlogic.aggregateCalc

/**
  * Created by jeorch on 17-6-12.
  */
import play.api.libs.json.JsValue
import bmmessages.CommonMessage

abstract class msg_AggregateCommand extends CommonMessage

object AggregateCalcMessage {
    case class msg_CalcMarketSize(data : JsValue) extends msg_AggregateCommand // 市场规模
    case class msg_CalcPercentage(data : JsValue) extends msg_AggregateCommand // 市场份额
    case class msg_CalcTrend(data : JsValue) extends msg_AggregateCommand // 市场增长率 选中当前日期数据
    case class msg_CalcTrend_Mat(data: JsValue) extends msg_AggregateCommand // 市场增长率 选中当前日期的MAT数据
    case class msg_ProductQuantity(data : JsValue) extends msg_AggregateCommand//产品数量
}
