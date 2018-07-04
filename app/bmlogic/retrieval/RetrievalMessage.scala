package bmlogic.retrieval

/**
  * Created by alfredyang on 01/06/2017.
  */
import play.api.libs.json.JsValue
import bmmessages.CommonMessage

abstract class msg_RetrievalCommand extends CommonMessage

object RetrievalMessage {
    case class msg_ConditionSearchCommand(data : JsValue) extends msg_RetrievalCommand

    case class msg_PushProduct(data : JsValue) extends msg_RetrievalCommand
    case class msg_UpdateProduct(data : JsValue) extends msg_RetrievalCommand
    case class msg_DeleteProduct(data : JsValue) extends msg_RetrievalCommand
}
