package bmlogic.adjustdata

/**
  * Created by jeorch on 17-6-13.
  */

import play.api.libs.json.JsValue
import bmmessages.CommonMessage

abstract class msg_AdjustDataCommand extends CommonMessage

object AdjustDataMessage {
    case class msg_AdjustNameDataCommand(data : JsValue) extends msg_AdjustDataCommand
}
