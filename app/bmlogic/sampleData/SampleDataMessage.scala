package bmlogic.sampleData

import bmmessages.CommonMessage
import play.api.libs.json.JsValue

/**
  * Created by yym on 7/27/17.
  */
abstract class msg_SampleDataCommand extends CommonMessage
object SampleDataMessage {
    case class msg_rawData2Sample(data :JsValue) extends msg_SampleDataCommand
    case class msg_readRawData(data:JsValue) extends msg_SampleDataCommand
}
