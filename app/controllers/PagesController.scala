package controllers


import bmlogic.common.requestArgsQuery
import play.api.mvc._

/**
  * Created by yym on 6/5/17.
  */
class PagesController extends Controller {

    //登陆跳转
    def goHome = Action {
        Ok(views.html.home())
    }
    
    //登陆
    def login = Action{
        Ok(views.html.login())
    }

    //管理員界面
    def admin = Action{
        Ok(views.html.admin())
    }
    //用户管理界面
    def userManage = Action{
        Ok(views.html.admin())
    }
    //登录日志界面
    def loginLog = Action{
        Ok(views.html.loginLog())
    }
    //显示样本数据界面
    def sampleData = Action{
        Ok(views.html.sampleData())
    }
    //个人中心
    def userInfo = Action{
        Ok(views.html.useInfo())
    }
    
    def contactus = Action {
        Ok(views.html.contactus())
    }

    def aboutus = Action {
        Ok(views.html.aboutus())
    }
    
    def comingsoon = Action {
        Ok(views.html.comingsonn())
    }
    
    //报告
    def report(reportid: String) = Action {
        Ok(views.html.reportbody(reportid))
    }
}
