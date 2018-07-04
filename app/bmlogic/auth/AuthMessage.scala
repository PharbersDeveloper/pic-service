package bmlogic.auth

import play.api.libs.json.JsValue
import bmmessages.CommonMessage

abstract class msg_AuthCommand extends CommonMessage

object AuthMessage {
	case class msg_AuthPushUser(data : JsValue) extends msg_AuthCommand
	case class msg_AuthWithPassword(data : JsValue) extends msg_AuthCommand
	case class msg_AuthTokenParser(data : JsValue) extends msg_AuthCommand
	case class msg_getAuthUserName(data : JsValue) extends msg_AuthCommand
	case class msg_CheckAuthTokenTest(data : JsValue) extends msg_AuthCommand

	case class msg_CheckAdministrator(data : JsValue) extends msg_AuthCommand
	case class msg_CheckTokenExpire(data : JsValue) extends msg_AuthCommand
	case class msg_CheckEdgeScope(data : JsValue) extends msg_AuthCommand
	case class msg_CheckProductLevelScope(data : JsValue) extends msg_AuthCommand
	case class msg_CheckManufactureNameScope(data : JsValue) extends msg_AuthCommand
	case class msg_CheckManufactureTypeScope(data : JsValue) extends msg_AuthCommand
	case class msg_CheckAuthSampleScope(data : JsValue) extends  msg_AuthCommand
}
