package bmutil.errorcode

import play.api.libs.json.Json
import play.api.libs.json.Json._
import play.api.libs.json.JsValue

object ErrorCode {
  	case class ErrorNode(name : String, code : Int, message : String)

  	private def xls : List[ErrorNode] = List(
  		new ErrorNode("input error", -1, "输入的参数有错误"),

		new ErrorNode("get primary key error", -101, "获取主健健值失败"),
		new ErrorNode("primary key error", -102, "主健重复创建或者主键出错"),
		new ErrorNode("data not exist", -103, "数据不存在"),
		new ErrorNode("data duplicate", -104, "搜索结果不唯一，用query multiple搜索"),

		new ErrorNode("product without time", -201, "销售数据没有时间"),
		new ErrorNode("product without province", -202, "销售数据没有省份数据"),
		new ErrorNode("product without sales value", -203, "销售数据没有销售金额数据"),
		new ErrorNode("product without sales units", -204, "销售数据没有销售熟料数据"),
		new ErrorNode("product without oral name", -205, "销售数据没有商品通用名"),
		new ErrorNode("product without manufacture", -206, "销售数据没有生产厂家数据"),
        new ErrorNode("product without specifications", -207, "销售数据没有规格数据"),
        new ErrorNode("product without product unit", -208, "销售数据没有最小产品数据"),
        new ErrorNode("product without manufacture type", -209, "销售数据没有生产厂家类型数据"),
        new ErrorNode("product without product type", -210, "销售数据没有剂型数据"),
        new ErrorNode("product without package", -211, "销售数据没有剂型数据"),
        new ErrorNode("product without sales id", -212, "销售数据没有缺少ID"),
		new ErrorNode("product without category", -213, "销售数据没有分类数据"),
		new ErrorNode("product without product name", -214, "销售数据没有商品名数据"),

		new ErrorNode("search condition parse error", -301, "搜索条件解析错误"),
		new ErrorNode("calc market size func error", -302, "计算市场销售额出错"),
		new ErrorNode("calc percentage without oral name or product name", -303, "计算市场份额必须提供通用名或者产品名"),
		new ErrorNode("calc market trend func error", -304, "计算市场增长了出错"),
		new ErrorNode("product size  func error", -305, "计算产品数量出错"),

		
		new ErrorNode("category authority filter error", -401, "治疗分类数据授权错误"),
		new ErrorNode("load category error", -402, "加载治疗分类数据出错"),
		new ErrorNode("category without level", -403, "没有治疗分类级别数据"),
		new ErrorNode("category without parent", -404, "没有治疗分类父级数据"),
		new ErrorNode("category without def", -405, "没有治疗分类def数据"),
		new ErrorNode("category without des", -406, "治疗分类def数据"),
	 
	 
		new ErrorNode("search condition parse error", -501, "搜索条件出错"),
	 
		new ErrorNode("GenericName db prase error", -601, "没有样本数据GenericName数据"),
		new ErrorNode("CompanyName db prase error", -602, "没有样本数据CompanyName数据"),
		new ErrorNode("Year db prase error", -603, "没有样本数据Year数据"),
		new ErrorNode("Quantity db prase error", -604, "没有样本数据Quantity数据"),
		new ErrorNode("SalesAmount db prase error", -605, "没有样本数据SalesAmount数据"),
		new ErrorNode("Formulation db prase error", -606, "没有样本数据Formulation数据"),
		new ErrorNode("Quarter db prase error", -607, "没有样本数据Quarter数据"),
		new ErrorNode("ROA db prase error", -608, "没有样本数据ROA数据"),
		new ErrorNode("TherapyMicro db prase error", -609, "没有样本数据TherapyMicro数据"),
		new ErrorNode("TherapyWide db prase error", -610, "没有样本数据TherapyWide数据"),
		new ErrorNode("City db prase error", -611, "没有样本数据City数据"),
	 
		new ErrorNode("user_name db prase error", -701, "没有用户名数据"),
		new ErrorNode("pwd db prase error", -702, "没有用户密码数据"),
		new ErrorNode("user_id db prase error", -703, "没有用户标志数据"),
		new ErrorNode("screen_name db prase error", -704, "没有用户名数据"),
		new ErrorNode("phoneNo db prase error", -705, "没有用户电话数据"),
		new ErrorNode("screen_photo db prase error", -706, "没有用户头像数据"),
		new ErrorNode("email db prase error", -707, "没有用户邮箱数据"),
		new ErrorNode("edge db prase error", -708, "没有省份数据"),
		new ErrorNode("manufacture_name db prase error", -709, "没有生产公司数据"),
		new ErrorNode("is_admin db prase error", -710, "没有管理员权限数据"),
		new ErrorNode("sample db prase error", -711, "没有样本权限数据"),
		new ErrorNode("scope db prase error", -712, "没有用户权限数据"),
		
		new ErrorNode("read user_name error", -801, "取用户名数据出错"),
		new ErrorNode("read date error", -802, "取日期数据出错"),
		new ErrorNode("read ip error", -803, "取Ip数据出错"),
		new ErrorNode("log login_end db prase error", -804, "日志数据出错"),
		new ErrorNode("log time_sum db prase error", -805, "数据出错"),
		new ErrorNode("log ip db prase error", -806, "数据出错"),

		new ErrorNode("no db connection", -901, "没找到数据库链接"),
		new ErrorNode("db prase error", -902, "数据库结构发现错误"),
		new ErrorNode("no encrypt impl", -903, "权限加密方式不清晰或者Token不存在"),
		new ErrorNode("token parse error", -904, "token数据解析出现错误"),
		new ErrorNode("token expired", -905, "token过期"),
		new ErrorNode("can't get log", -911, "无法得到log"),
		new ErrorNode("db aggregation error", -906, "数据Map Reduce操作发生错误"),

  		new ErrorNode("unknown error", -999, "unknown error")
  	)
  
  	def getErrorCodeByName(name : String) : Int = (xls.find(x => x.name == name)) match {
  			case Some(y) => y.code
  			case None => -9999
  		}
  	
   	def getErrorMessageByName(name : String) : String = (xls.find(x => x.name == name)) match {
  			case Some(y) => y.message
  			case None => "unknow error"
  		}
   	
   	def errorToJson(name : String) : JsValue =
  		Json.toJson(Map("status" -> toJson("error"), "error" -> 
  				toJson(Map("code" -> toJson(this.getErrorCodeByName(name)), "message" -> toJson(this.getErrorMessageByName(name))))))
}