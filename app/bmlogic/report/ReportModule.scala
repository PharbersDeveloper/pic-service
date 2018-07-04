package bmlogic.report

import java.util.Calendar

import bminjection.db.DBTrait
import bmlogic.conditions.ConditionSearchFunc
import bmlogic.report.ReportMessage._
import bmlogic.report.ReportModule.oralNameConditionParse
import bmlogic.report.reportData.ReportData
import bmmessages.{CommonModules, MessageDefines}
import bmpattern.ModuleTrait
import bmutil.errorcode.ErrorCode
import com.mongodb.casbah.Imports._
import play.api.libs.json.JsValue
import play.api.libs.json.Json._

import scala.collection.JavaConverters._
import scala.collection.immutable.Map
import scala.collection.mutable.ArrayBuffer

/**
  * Created by qianpeng on 2017/6/20.
  */
object ReportModule extends ModuleTrait with ReportData with ConditionSearchFunc{
	
	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]])(implicit cm : CommonModules) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_InertParameterCommand(data) => insertparameter(data)
		case msg_ReportParameterSummary(data) => reportparametersummary(data)
		
		case msg_ReportGraph_One(data) => reportgraphone(data)(pr)
		case msg_ReportGraph_Tow(data) => reportgraphtow(data)(pr)
		case msg_ReportGraph_Thr(data) => reportgraphthr(data)(pr)
		case msg_ReportGraph_Four(data) => reportgraphfour(data)(pr)
		case msg_ReportGraph_Five(data) => reportgraphfive(data)(pr)
		case msg_ReportGraph_Six(data) => reportgraphsix(data)(pr)
		case msg_ReportGraph_Seven(data) => reportgraphseven(data)(pr)
		case msg_ReportGraph_Eight(data) => reportgrapheight(data)(pr)
		

		/************************************************************/
		case msg_ReportTable_Sales(data) => reporttablesales(data)(pr)
		case msg_ReportTable_ProductUnitCount(data) => reporttableproductunitcount(data)(pr)
		case msg_ReportTable_Calc(data) => reporttablescalc(data)(pr)
		case msg_ReportTable_One(data) => reportTableOne(data)(pr)
		
		


		case _ => ???
	}
	
	def insertparameter(data: JsValue)
	                   (implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val db=cm.modules.get.get("db").map(x=>x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
			val o: DBObject = data
			db.insertObject(o, "report", "reportid")
			(Some(Map("reportid" -> toJson(o.getAs[String]("reportid").getOrElse("")))), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def reportparametersummary(data: JsValue)
	                          (implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val db=cm.modules.get.get("db").map(x=>x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
			val reportid = (data \ "reportid").asOpt[String].getOrElse("")
			val result = db.queryObject(DBObject("reportid" -> reportid), "report")(x => reportparameter(x))
			(Some(Map("parameter" -> toJson(result))), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	
	def reportgraphone (data: JsValue)
	                   (pr: Option[Map[String, JsValue]])
	                   (implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		
		def resultdata(timecount: List[JsValue]): List[Option[Map[String, JsValue]]] = {
			val db=cm.modules.get.get("db").map(x=>x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
			val group = MongoDBObject("_id" -> MongoDBObject("ms" -> "reportgraphone"), "sales" -> MongoDBObject("$sum" -> "$sales"))
			timecount map { x =>
				val condition = (conditionParse(data, pr.get) :: oralNameConditionParse(data) :: dateConditionParse(x) :: Nil).filterNot(_ == None).map(_.get)
				db.aggregate($and(condition), "retrieval", group) { z =>
					Map("sales" -> toJson(aggregateSalesResult(z, "reportgraphone")),
						"start" -> toJson((x \ "condition" \ "date" \ "start").as[String]),
						"end" -> toJson((x \ "condition" \ "date" \ "end").as[String])
					)
				}
			}
		}
		
		try {
			var flag = 0D
			val lst = resultdata(dateCondition(timeList(1, data))).reverse.map { x =>
				if(flag == 0) {
					flag = x.get.get("sales").get.as[Double]
					x.get ++ Map("trend" -> toJson("null"))
				}else {
					x.get ++ Map("trend" -> toJson((x.get.get("sales").get.as[Double] - flag)/flag))
				}
			}
			(Some(Map("reportgraphone" -> toJson(lst))), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def reportgraphtow (data: JsValue)
	                   (pr: Option[Map[String, JsValue]])
	                   (implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val db=cm.modules.get.get("db").map(x=>x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
			val result = (conditionParse(data, pr.get) :: oralNameConditionParse(data) :: dateConditionParse(data) :: Nil).filterNot(_ == None).map(_.get)
			
			(Some(Map("reportid" -> toJson(0))), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def reportgraphthr (data: JsValue)
	                   (pr: Option[Map[String, JsValue]])
	                   (implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val db=cm.modules.get.get("db").map(x=>x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
			val result = (conditionParse(data, pr.get) :: oralNameConditionParse(data) :: dateConditionParse(data) :: Nil).filterNot(_ == None).map(_.get)
			
			(Some(Map("reportid" -> toJson(0))), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}

	def reportgraphfour (data: JsValue)
	                    (pr: Option[Map[String, JsValue]])
	                    (implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {

		def getByID(x : MongoDBObject, id : String) : Double = {
			val ok = x.getAs[Number]("ok").get.intValue
			if (ok == 0) throw new Exception("db aggregation error")
			else {
				val lst: MongoDBList = x.getAs[MongoDBList]("result").get
				val tmp = lst.toList.asInstanceOf[List[BasicDBObject]]
				if (tmp.isEmpty) throw new Exception("db aggregation find None")
				else {
					val res = tmp.find(x => x.getString("_id") == id)
					res.get.getDouble("sales") / 100
				}
			}
		}
		def resultdata(timecount: List[JsValue]): List[Option[Map[String, JsValue]]] = {
			val db=cm.modules.get.get("db").map(x=>x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
			timecount map { x =>
				val condition = (conditionParse(data, pr.get)::categoryConditionParse(data,pr.get) :: oralNameConditionParse(data) :: dateConditionParse(x) :: Nil).filterNot(_ == None).map(_.get)
				val group = MongoDBObject("_id" -> "$manufacture_type", "sales" -> MongoDBObject("$sum" -> "$sales"))
				var res=Map(""->toJson(0))
				try{
					res=db.aggregate($and(condition), "retrieval", group) { z =>
						val interNum = Some(getByID(z,"内资")).getOrElse(0D)
						val outerNum = Some(getByID(z,"合资")).getOrElse(1D)
						val per = outerNum / (interNum+outerNum) * 100
						Map("percent" -> toJson(per),
							"inter"->toJson(interNum),
							"outer"->toJson(outerNum),
							"start" -> toJson((x\ "condition" \ "date" \ "start").as[String]),
							"end" -> toJson((x \ "condition" \ "date" \ "end").as[String])
						)
					
				}.get
				}catch {
					case ex:Exception=>
						res=Map(
							"percent" -> toJson("暂无数据"),
							"inter"->toJson("暂无数据"),
							"outer"->toJson("暂无数据"),
							"start" -> toJson((x\ "condition" \ "date" \ "start").as[String]),
							"end" -> toJson((x \ "condition" \ "date" \ "end").as[String])
						)
				}
				Some(res)
			}
		}
		
		try {
			val lst=resultdata(dateCondition(timeList(1,data)))
			(Some(Map("reportgraphfour" -> toJson(lst)) ++ pr.get), None)
			
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def reportgraphfive (data: JsValue)
	                    (pr: Option[Map[String, JsValue]])
	                    (implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		
		def resultdata(timecount: List[JsValue]): List[Option[Map[String, JsValue]]] = {
			val db=cm.modules.get.get("db").map(x=>x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
			val group = MongoDBObject("_id" -> "$oral_name", "sales" -> MongoDBObject("$sum" -> "$sales"))
			timecount map{ x =>
				val condition = (conditionParse(data, pr.get) :: oralNameConditionParse(data) :: dateConditionParse(x) :: Nil).filterNot(_ == None).map(_.get)
				db.aggregate($and(condition), "retrieval", group) { z =>
					val r = aggregateResult(z)
					val sum = r.map(y => y._2).sum
					val scale = r.map ( y => Map("key" -> toJson(y._1), "value" -> toJson((y._2 / sum))))
					val sales = r.map ( y => Map("key" -> toJson(y._1), "value" -> toJson(y._2)))
					Map("sales" -> toJson(sales),
						"scale" -> toJson(scale),
						"start" -> toJson((x \ "condition" \ "date" \ "start").as[String]),
						"end" -> toJson((x \ "condition" \ "date" \ "end").as[String])
					)
				}
			}
		}
		
		def aggregateResult(x : MongoDBObject) : List[(String, Double)] = {
			val ok = x.getAs[Number]("ok").get.intValue
			if (ok == 0) throw new Exception("db aggregation error")
			else {
				val lst : MongoDBList = x.getAs[MongoDBList]("result").get
				lst.toList.asInstanceOf[List[BasicDBObject]].map( z => (z.getString("_id"), z.getDouble("sales") / 100))
			}
		}
		
		try {
			val result = resultdata(dateCondition(timeList(1, data)))
			(Some(Map("reportgraphfive" -> toJson(result)) ++ pr.get), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def reportgraphsix (data: JsValue)
	                   (pr: Option[Map[String, JsValue]])
	                   (implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		
		def resultdata(timecount: List[JsValue]): List[Option[Map[String, JsValue]]] = {
			val db=cm.modules.get.get("db").map(x=>x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
			val group = MongoDBObject("_id" -> MongoDBObject("product_name" -> "$product_name","manufacture" -> "$manufacture","product_type" -> "$product_type"), "sales" -> MongoDBObject("$sum" -> "$sales"))
			timecount map{ x =>
				val condition = (conditionParse(data, pr.get) :: oralNameConditionParse(data) :: dateConditionParse(x) :: Nil).filterNot(_ == None).map(_.get)
				db.aggregate($and(condition), "retrieval", group) { z =>
					val r = aggregateResult(z)
					val keyvalue = r.map (y =>Map(y._1 -> y._2))
					Map("keyvalue" -> toJson(keyvalue),
						"start" -> toJson((x \ "condition" \ "date" \ "start").as[String]),
						"end" -> toJson((x \ "condition" \ "date" \ "end").as[String]))
				}
			}
		}
		
		def aggregateResult(x : MongoDBObject) : List[(String, Double)] = {
			val ok = x.getAs[Number]("ok").get.intValue
			if (ok == 0) throw new Exception("db aggregation error")
			else {
				val lst : MongoDBList = x.getAs[MongoDBList]("result").get
				lst.toList.asInstanceOf[List[BasicDBObject]].map { z =>
					val key = z.getAs[BasicDBObject]("_id")
					(key.get.getString("product_name") + key.get.getString("manufacture") + key.get.getString("product_type"), z.getDouble("sales") / 100)
				}
			}
		}
		
		try {
			val result = resultdata(dateCondition(timeList(1, data)))
			(Some(Map("reportgraphsix" -> toJson(result)) ++ pr.get), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def reportgraphseven (data: JsValue)
	                     (pr: Option[Map[String, JsValue]])
	                     (implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		
		def resultdata(timecount: List[JsValue]): List[Option[Map[String, JsValue]]] = {
			val db=cm.modules.get.get("db").map(x=>x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
			val group = MongoDBObject("_id" -> MongoDBObject("ms" -> "oral_sum"), "sales" -> MongoDBObject("$sum" -> "$sales"))
			val lst = pr.get.get("reportgrapheight").get.as[List[Map[String, JsValue]]]
			timecount map{ x =>
				val topsalessum = lst.find(z => z.get("start").get.as[String] == (x \ "condition" \ "date" \ "start").as[String]).map(y => y.get("sales").get.as[List[Map[String, JsValue]]].map(y => y.get("sales").get.as[Double]).sum).getOrElse(throw new Exception())
				val condition = (conditionParse(data, pr.get) :: oralNameConditionParse(data) :: dateConditionParse(x) :: Nil).filterNot(_ == None).map(_.get)
				db.aggregate($and(condition), "retrieval", group) { z =>
					val sum = aggregateSalesResult(z, "oral_sum")
					val value = if(topsalessum == 0) toJson(0) else toJson(topsalessum / sum)
					Map("sales" -> value,
						"start" -> toJson((x \ "condition" \ "date" \ "start").as[String]),
						"end" -> toJson((x \ "condition" \ "date" \ "end").as[String]))
				}
			}
		}
		
		try {
			var flag = 0D
			val lst = resultdata(dateCondition(timeList(1, data))).reverse.map { x =>
				if(flag == 0) {
					flag = x.get.get("sales").get.as[Double]
					x.get ++ Map("trend" -> toJson("null"))
				}else {
					x.get ++ Map("trend" -> toJson((x.get.get("sales").get.as[Double] - flag)/flag))
				}
			}
			(Some(Map("reportgraphseven" -> toJson(lst)) ++ pr.get), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def reportgrapheight (data: JsValue)
	                     (pr: Option[Map[String, JsValue]])
	                     (implicit cm : CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		
		def resultdata(timecount: List[JsValue]): List[Option[Map[String, JsValue]]] = {
			val db=cm.modules.get.get("db").map(x=>x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
			val group = MongoDBObject("_id" -> MongoDBObject("product_name" -> "$product_name", "manufacture_type" -> "$manufacture_type", "manufacture" -> "$manufacture"), "sales" -> MongoDBObject("$sum" -> "$sales"))
			timecount map{ x =>
				val condition = (conditionParse(data, pr.get) :: oralNameConditionParse(data) :: dateConditionParse(x) :: Nil).filterNot(_ == None).map(_.get)
				db.aggregate($and(condition), "retrieval", group) { z =>
					val r = aggregateResult(z).sortBy(y => y._3).reverse
					val sum = r.map(_._3).sum
					val keyvalue = r.take(10).map (y => Map(y._1 -> toJson((y._3) / sum)))
					val interouter = r.take(10).map (y => Map(y._1 -> toJson((y._2))))
					val manufacture = r.take(10).map(y => Map("manufacture" -> y._4, "product_name" -> y._1))
					val sales = r.take(10).map (y => Map("product_name" -> toJson(y._1), "sales" -> toJson(y._3)))
					Map("keyvalue" -> toJson(keyvalue),
						"sales" -> toJson(sales),
						"interouter" -> toJson(interouter),
						"manufacture" -> toJson(manufacture),
						"start" -> toJson((x \ "condition" \ "date" \ "start").as[String]),
						"end" -> toJson((x \ "condition" \ "date" \ "end").as[String]))
				}
			}
		}
		
		def aggregateResult(x : MongoDBObject) : List[(String, String, Double, String)] = {
			val ok = x.getAs[Number]("ok").get.intValue
			if (ok == 0) throw new Exception("db aggregation error")
			else {
				val lst : MongoDBList = x.getAs[MongoDBList]("result").get
				lst.toList.asInstanceOf[List[BasicDBObject]].map { z =>
					val key = z.getAs[BasicDBObject]("_id")
					(key.get.getString("product_name"), key.get.getString("manufacture_type"), z.getDouble("sales") / 100, key.get.getString("manufacture"))
				}
			}
		}
		
		try {
			val result = resultdata(dateCondition(timeList(1, data)))
			(Some(Map("reportgrapheight" -> toJson(result)) ++ pr.get), None)
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	
	def reporttablesales (data: JsValue)
	                     (pr: Option[Map[String, JsValue]])
	                     (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		
		def resultdata(timecount: List[JsValue]): List[Option[Map[String, JsValue]]] = {
			val db = cm.modules.get.get("db").map(x=>x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
			val group = MongoDBObject("_id" -> MongoDBObject("manufacture" -> "$manufacture", "manufacture_type" -> "$manufacture_type"), "sales" -> MongoDBObject("$sum" -> "$sales"))
			timecount map { x =>
				val condition = (conditionParse(data, pr.get) :: oralNameConditionParse(data) :: dateConditionParse(x) :: Nil).filterNot(_ == None).map(_.get)
				db.aggregate($and(condition), "retrieval", group) { z =>
					val r = aggregateResult(z)
					Map("sales" -> toJson(0),
						"start" -> toJson((x \ "condition" \ "date" \ "start").as[String]),
						"end" -> toJson((x \ "condition" \ "date" \ "end").as[String])
					)
				}
			}
		}
		
		def aggregateResult(x : MongoDBObject) : List[(String, Double)] = {
			val ok = x.getAs[Number]("ok").get.intValue
			if (ok == 0) throw new Exception("db aggregation error")
			else {
				val lst : MongoDBList = x.getAs[MongoDBList]("result").get
//				lst.toList.asInstanceOf[List[BasicDBObject]].map( z => (z.getString("_id"), z.getDouble("sales") / 100))
			}
			Nil
		}
		
		try {
			resultdata(dateCondition(timeList(1, data)))
		} catch {
			case ex: Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
		
		(Some(Map("" -> toJson(0))), None)
	}
	
	def reporttableproductunitcount (data: JsValue)
	                                (pr: Option[Map[String, JsValue]])
	                                (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		
		(Some(Map("" -> toJson(0))), None)
	}
	
	def reporttablescalc (data: JsValue)
	                     (pr: Option[Map[String, JsValue]])
	                     (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		
		(Some(Map("" -> toJson(0))), None)
	}
	
	def reportTableOne (data: JsValue)
	                   (pr: Option[Map[String, JsValue]])
	                   (implicit cm: CommonModules): (Option[Map[String, JsValue]], Option[JsValue]) = {
		
		def getSales(t:JsValue,data:JsValue,pr:Option[Map[String,JsValue]]): (List[(String,Double,String)],Double) ={
			val db = cm.modules.get.get("db").map(x => x.asInstanceOf[DBTrait])
				.getOrElse(throw new Exception("no db connection"))
			val group = MongoDBObject( "_id" ->
				MongoDBObject("manufacture" -> "$manufacture","manufacture_type"->"$manufacture_type"),
				"sales" -> MongoDBObject("$sum" -> "$sales"))
			val condition = (conditionParse(data, pr.get) :: categoryConditionParse(data, pr.get) ::
				oralNameConditionParse(data) :: dateConditionParse(t) :: Nil).filterNot(_ == None).map(_.get)
			//查询得到当年前十市场份额的数据
			var marketShare: List[Map[String, JsValue]] = null
			val topTen: ArrayBuffer[(String, Double,String)] = new ArrayBuffer[(String, Double,String)]()
			var sum=0D
			val allRes = db.aggregate($and(condition), "retrieval", group) { x =>
				val tmp = getObj(x).map { y =>
					val e=y.get("_id").asInstanceOf[BasicDBObject]
					
					val manufacture= e.get("manufacture").asInstanceOf[String]
					val manufacture_type =e.get("manufacture_type").asInstanceOf[String]
					val saleQuantity = y.getDouble("sales")
					(manufacture,saleQuantity,manufacture_type)
					
				}
				sum = tmp.map(x => x._2.toDouble).reduce((a, b) => a + b)
				val res = tmp.sortBy(x => x._2.toDouble).reverse.take(10)
				val top = res.map { x =>
					topTen.append(x)
					Map(x._1 -> (x._2,x._3))
				}
				marketShare = res.map(x => Map(x._1 -> toJson(x._2.toDouble / sum)))
				Map("top10" -> toJson(marketShare))
			}
			(topTen.toList,sum)
		}
		def getPreSales(t:JsValue,data:JsValue,pr:Option[Map[String,JsValue]]): List[(String,Double)] ={
			val db = cm.modules.get.get("db").map(x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
			val per_year_start = (t \ "condition" \ "date" \ "start").get.as[String]
			val per_year_last = (t \ "condition" \ "date" \ "end").get.as[String]
			val per_start = updateMonth(per_year_start, -12)
			val per_end = updateMonth(per_year_last, -12)
			val time = toJson(Map("condition" -> toJson(Map("date" -> toJson(Map("start" -> toJson(per_start), "end" -> toJson(per_end)))))))
			val perCondition = (conditionParse(data, pr.get) :: categoryConditionParse(data, pr.get) :: oralNameConditionParse(data) :: dateConditionParse(time) :: Nil).filterNot(_ == None).map(_.get)
			val group = MongoDBObject("_id" -> "$manufacture", "sales" -> MongoDBObject("$sum" -> "$sales"))
			val preData: ArrayBuffer[(String, Double)] = new ArrayBuffer[(String, Double)]()
			val preRes = db.aggregate($and(perCondition), "retrieval", group) { x =>
				val tmp = getObj(x).map { y =>
					val manufacture = y.getString("_id")
					val saleQuantity = y.getDouble("sales")
					val kv = Map(manufacture -> saleQuantity)
					preData.append((manufacture, saleQuantity))
					kv
				}
				Map("pre_sale" -> toJson(tmp))
			}
			preData.toList
		}
		def getCompany(t:JsValue,data:JsValue,pr:Option[Map[String,JsValue]]):List[(String,Int)]={
			val db = cm.modules.get.get("db").map(x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
			val productGroup = MongoDBObject("_id" -> "$manufacture", "product_unit" -> MongoDBObject("$push" -> "$product_unit"))
			val productArr: ArrayBuffer[(String, Int)] = new ArrayBuffer()
			val condition = (conditionParse(data, pr.get) :: categoryConditionParse(data, pr.get) ::
				oralNameConditionParse(data) :: dateConditionParse(t) :: Nil).filterNot(_ == None).map(_.get)
			val proRes = db.aggregate($and(condition), "retrieval", productGroup) { x =>
				val tmp = getObj(x).map { y =>
					val manufacture = y.getString("_id")
					val product_unit = y.getAs[List[String]]("product_unit").get.distinct.size
					productArr.append((manufacture, product_unit))
					Map(manufacture -> product_unit)
				}
				Map("production" -> toJson(tmp))
			}
			productArr.toList
		}
		def resultdata(timecount: List[JsValue]): List[Option[Map[String, JsValue]]] = {
			val db = cm.modules.get.get("db").map(x => x.asInstanceOf[DBTrait]).getOrElse(throw new Exception("no db connection"))
			timecount map { x =>
				try {
					
					//得到市场份额
					val topTenSales=getSales(x,data,pr)
					val preData=getPreSales(x,data,pr)
					val marketShare=topTenSales._1.map{x=>
						val share=x._2/(topTenSales._2)
						Map(x._1->share)
					}
					//得到市场增长率
					val percentGrowth = topTenSales._1.map { x =>
						val now = x._2
						val past = preData.filter(y => y._1 == x._1).head._2
						val growth = (now - past) / past * 100
						Map(x._1 -> growth)
					}
					//得到产品数
					val productUnit=getCompany(x,data,pr)
					val productNum = topTenSales._1.map { x =>
						productUnit.filter(y => y._1 == x._1).map(z => Map(z._1 -> z._2)).head
					}
					//查询公司类型
//					val com_type = topTenSales._1.map { x =>
//						val tl = db.queryObject(DBObject("manufacture" -> x._1), "retrieval") { obj =>
//							Map(
//								"manufacture" -> toJson(obj.getAs[String]("manufacture").map(x => x).getOrElse(throw new Exception("product with manufacture"))),
//								"manufacture_type" -> toJson(obj.getAs[String]("manufacture_type").map(x => x).getOrElse(throw new Exception("product with manufacture type")))
//							)
//						}
//						val typ = tl.map { x =>
//							Map(x.get("manufacture").get.as[String] -> x.get("manufacture_type").get.as[String])
//						}
//						typ
//					}
					val com_type=topTenSales._1.map(x=>Map(x._1->x._3))
					Some(Map(
						"start" -> toJson((x \ "condition" \ "date" \ "start").as[String]),
						"end" -> toJson((x \ "condition" \ "date" \ "end").as[String]),
						"marketShare" -> toJson(marketShare),
						"percentGrowth" -> toJson(percentGrowth),
						"productNumber" -> toJson(productNum),
						"manufacture_type" -> toJson(com_type)
					))
				} catch {
					case ex: Exception =>
						Some(Map(
							"info" -> toJson("暂无数据"),
							"start" -> toJson((x \ "condition" \ "date" \ "start").as[String]),
							"end" -> toJson((x \ "condition" \ "date" \ "end").as[String])
						))
				}
			}
		}
		
		try {
			
			val lst = resultdata(dateCondition(timeList(1, data)))
			(Some(Map("ReportTableOne" -> toJson(lst)) ++ pr.get), None)
			
		} catch {
			case ex: Exception =>
				(None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	
	def reportparameter(obj: DBObject): Map[String, JsValue] = {
		val condition = obj.getAs[String]("condition").getOrElse("")
		Map("condition" -> parse(condition))
	}
	
	def aggregateSalesResult(x : MongoDBObject, id : String): Double = {
		val ok = x.getAs[Number]("ok").get.intValue
		if (ok == 0) throw new Exception("db aggregation error")
		else {
			val lst : MongoDBList = x.getAs[MongoDBList]("result").get
			val tmp = lst.toList.asInstanceOf[List[BasicDBObject]]
			if(tmp.isEmpty) 0D
			else
				tmp.find(y => y.getAs[BasicDBObject]("_id").get.getString("ms") == id).map { z =>
					z.getDouble("sales") / 100
				}.getOrElse(throw new Exception("db aggregation error"))
		}
	}
	
	def dateCondition(lst: List[String]): List[JsValue] = {
		val tmp = lst match {
			case Nil => println("Nil"); None
			case (head :: tail) => if(!tail.isEmpty) Some((head, tail.head)) else None
			case _ => ???
		}
		
		if(lst.tail.isEmpty || tmp.isEmpty) Nil
		else
		//				($and("date" $lt sdf.parse(tmp.get._1).getTime, "date" $gte sdf.parse(tmp.get._2).getTime))
			toJson(Map("condition" -> toJson(Map("date" -> toJson(Map("start" -> toJson(tmp.get._2), "end" -> toJson(tmp.get._1))))))) :: dateCondition(lst.tail)
	}
	
	def getObj(x : MongoDBObject) : List[BasicDBObject] = {
		val ok = x.getAs[Number]("ok").get.intValue
		if (ok == 0) throw new Exception("db aggregation error")
		else {
			val lst : MongoDBList = x.getAs[MongoDBList]("result").get
			val tmp = lst.toList.asInstanceOf[List[BasicDBObject]]
			if(tmp.isEmpty) throw new Exception("db aggregation find None")
			else tmp
		}
	}
	
	def updateMonth(date:String,month:Int):String  ={
		val c = Calendar.getInstance()
		c.setTime(sdf.parse(date))
		c.add(Calendar.MONTH,month)
		val per_time=c.getTime
		sdf.format(per_time)
	}
}
