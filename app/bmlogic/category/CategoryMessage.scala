package bmlogic.category

import bmmessages.CommonMessage
import play.api.libs.json.JsValue

/**
  * Created by yym on 6/15/17.
  */

abstract class msg_CategoryCommand extends CommonMessage
   
object CategoryMessage{
    case class msg_FirstChildCategoryCommand(data : JsValue) extends msg_CategoryCommand
    case class msg_SecondChildCategoryCommand(data : JsValue) extends msg_CategoryCommand
    case class msg_ThirdChildCategoryCommand(data : JsValue) extends msg_CategoryCommand
    case class msg_Category(data: JsValue) extends msg_CategoryCommand
    case class msg_LinkageCategory(data:JsValue) extends msg_CategoryCommand
}

