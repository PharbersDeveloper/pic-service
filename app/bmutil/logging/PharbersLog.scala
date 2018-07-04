package bmutil.logging

/**
  * Created by yym on 8/23/17.
  */

import java.util.logging.LogManager

import org.apache.log4j.Logger

import scala.xml.XML

trait PharbersLog{
//    var usersInfo : List[String] = Nil // read config
    var current :String = "" // read config
    def out2console(message : AnyRef, developer_name:String): Unit
    def out2file(message : AnyRef, user_name: String): Unit
    def DBRolling(message : AnyRef):Unit
}

trait LogImpl extends PharbersLog {
    current=readUser("conf/UsersLoggingInfo.xml")
 
    override def out2console(message: AnyRef, developer_name: String): Unit = {
        val consolelogger=Logger.getLogger("console")
        println(current)
        if(developer_name == current)
            
                consolelogger.debug("["+ current +"]" +message )
        
    }
    override def out2file(message: AnyRef, user_name: String): Unit = {
        val filelogger=Logger.getLogger("file")
        filelogger.info("["+ user_name +"]" +message )
        
    }
    override def DBRolling(message: AnyRef): Unit = {
        val DBRollinglogger=Logger.getLogger("rollingFile")
        DBRollinglogger.info(message)
    }
    def readUser(file:String) : String ={
        try{
            val xml=XML.loadFile(file)
            val current=(xml \"host").map(x => x.text).head
            current
        }catch {
            case ex :Exception=>
                "None"
        }
        
        
    }
//    def setProperties(name : String) : Properties ={
//        val p = new Properties()
//        p.put("log4j.rootLogger.", "DEBUG," + name + ",file,rollingFile")
//        p.put("log4j.logger.play", "INFO")
//        p.put("log4j.appender." + name, "org.apache.log4j.ConsoleAppender")
//        p.put("log4j.appender." + name + ".Target", "System.out")
//        p.put("log4j.appender." + name + ".layout", "org.apache.log4j.PatternLayout")
//        p.put("log4j.appender." + name + ".layout.ConversionPattern", "%d{ yyyy-MM-dd HH:mm:ss } %-5p %-20c ~ %m%n")
//        p
//    }
    
}
