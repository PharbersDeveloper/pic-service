package bmlogic.loginLog

/**
  * Created by jeorch on 17-6-12.
  */
import play.api.libs.json.JsValue
import bmmessages.CommonMessage

abstract class msg_LoginLogCommand extends CommonMessage

object LoginLogMessage {
    case class msg_loginLog_query(data : JsValue) extends msg_LoginLogCommand // 日志管理
    case class msg_loginLog_import(data : JsValue) extends msg_LoginLogCommand // 导入日志
    case class msg_loginLog_save(data : JsValue) extends msg_LoginLogCommand // 保存日志
}