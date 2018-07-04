package bminjection.db.MongoDB

import java.util.UUID

import bminjection.db.DBTrait
import bmutil.dao.{_data_connection, from}
import bmutil.logging.LogImpl
import com.mongodb.casbah
import com.mongodb.casbah.Imports._
import play.api.libs.json.JsValue

trait MongoDBImpl extends DBTrait {
    this:LogImpl=>
    override def insertObject(obj : DBObject, db_name : String, primary_key : String) : Unit = {
        val primary = obj.get(primary_key)//.map (x => x).getOrElse(throw new Exception("get primary key error"))
        val log_str=s"[${UUID.randomUUID()}] Operation: insert ${obj.toString} | db: $db_name | Condition: ${(primary_key -> primary).toString()}"
        (from db() in db_name where (primary_key -> primary) select(x => x)).toList match {
            case Nil =>
                this.DBRolling(s"#START# $log_str")
                _data_connection.getCollection(db_name) += obj
                this.DBRolling(s"#END# $log_str")
            case _ =>
                this.DBRolling(s"#ERROR# $log_str")
                throw new Exception("primary key error")
        }
    }

    override def updateObject(obj : DBObject, db_name : String, primary_key : String)  : Unit = {
        val primary = obj.get(primary_key) //.map (x => x).getOrElse(throw new Exception("get primary key error"))
        val log_str=s"[${UUID.randomUUID()}] Operation: update ${obj.toString} | db: $db_name | Condition: ${(primary_key -> primary).toString()}"
        (from db() in db_name where (primary_key -> primary) select(x =>x)).toList match {
            case head :: Nil =>
                this.DBRolling(s"#START# $log_str")
                _data_connection.getCollection(db_name).update(head, obj)
                this.DBRolling(s"#END# $log_str")
            case _ =>
                this.DBRolling(s"#ERROR# $log_str")
                throw new Exception("primary key error")
        }
    }

    override def queryObject(condition : DBObject, db_name : String)
                   (implicit t : DBObject => Map[String, JsValue]) : Option[Map[String, JsValue]] = {

        (from db() in db_name where condition).selectTop(1)("date")(x => t(x)).toList match {
            case Nil => None
            case head :: Nil => Some(head)
            case _ => throw new Exception("data duplicate")
        }
    }

    override def queryMultipleObject(condition : DBObject, db_name : String, sort : String = "date", skip : Int = 0, take : Int = 20)
                           (implicit t : DBObject => Map[String, JsValue]) : List[Map[String, JsValue]] = {
        (from db() in db_name where condition).selectSkipTop(skip)(take)(sort)(x => t(x)).toList
    }
    
    override def queryAllObject(db_name: String,skip: Int=0, take: Int=20)
                               (implicit t: DBObject => Map[String, JsValue]): List[Map[String, JsValue]] = {
        (from db() in db_name).selectSkipTopLoc(skip)(take)(x => t(x)).toList
    }
    override def loadAllData(db_name:String)
                            (implicit t: DBObject => Map[String, JsValue]): List[Map[String, JsValue]] = {
        (from db() in db_name).select(x => t(x)).toList
    }
    
 
    override def deleteObject(obj : DBObject, db_name: String, primary_key: String) : Unit = {
        val primary = obj.get(primary_key) //.map (x => x).getOrElse(throw new Exception("get primary key error"))
        val log_str=s"[${UUID.randomUUID()}] Operation: delete ${obj.toString} | db: $db_name | Condition: ${(primary_key -> primary).toString()}"
        (from db() in db_name where (primary_key -> primary) select(x =>x)).toList match {
            case head :: Nil =>
                this.DBRolling(s"#START# $log_str")
                _data_connection.getCollection(db_name) -= head
                this.DBRolling(s"#END# $log_str")
            case _ =>
                this.DBRolling(s"#ERROR# $log_str")
                throw new Exception("primary key error")
        }
    }

    override def querySum(condition : DBObject, db_name : String)
                         (sum : (Map[String, JsValue], Map[String, JsValue]) => Map[String, JsValue])
                         (acc: (DBObject) => Map[String, JsValue]) : Option[Map[String, JsValue]] = {

        val c = from db() in db_name where condition selectCursor

        var result : Map[String, JsValue] = Map.empty
        while (c.hasNext) {
            result = sum(result, acc(c.next()))
        }

        if (result.isEmpty) None
        else Some(result)
    }

    override def aggregate(condition : DBObject, db_name : String, group : DBObject)
                 (implicit t : DBObject => Map[String, JsValue]) : Option[Map[String, JsValue]] =
        Some(t((from db() in db_name where condition).aggregate(group)))
    
}