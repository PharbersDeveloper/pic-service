package bmutil

import com.typesafe.config.ConfigFactory

/**
  * Created by liwei on 2017/7/10.
  */
object databaseConfig {
    val dbc: IConfigFactory = CommonConfigFactory.getConfigFactory("database")

    val dbhost = dbc.getProperties("database.dbhost")
    val dbport = dbc.getProperties("database.dbport")
    val dbuser = dbc.getProperties("database.username")
    val dbpwd = dbc.getProperties("database.password")
    val db = dbc.getProperties("database.db")
}

object CommonConfigFactory {
    val configFactoryMap:Map[String,IConfigFactory] = Map(
        "database" -> new DBConfigFactory
    )

    def getConfigFactory(configFileName:String):IConfigFactory = {
        configFactoryMap(configFileName)
    }
}

trait IConfigFactory{
    val configFileName:String
    lazy val config = ConfigFactory.load(configFileName)

    def getProperties(configKey: String):String = {
        config.getString(configKey)
    }
}

class DBConfigFactory  extends IConfigFactory{
    override val configFileName = "database.conf"
}