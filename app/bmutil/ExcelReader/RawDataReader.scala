package bmutil.ExcelReader

import bmutil.ExcelReader.JavaBean.RawData
import com.pharbers.aqll.common.alFileHandler.alExcelOpt.scala.alExcelDataParser

/**
  * Created by yym on 7/27/17.
  */
object RawDataReader {
    def read(path: String) : List[RawData]={
        try{
            val en_rawData="app/bmutil/ExcelReader/XMLFile/en_RawData.xml"
            val ch_rawData="app/bmutil/ExcelReader/XMLFile/ch_RawData.xml"
            val data=new alExcelDataParser(new RawData,en_rawData,ch_rawData)
            data.prase("app/bmutil/ExcelReader/ExcelFile/sampleData.xlsx")("")
//            println(data.data.toList.asInstanceOf[List[RawData]].size)
            data.data.toList.asInstanceOf[List[RawData]]
        }catch {
            case ex:Exception =>
                List(new RawData())
        }
        
    }
}
