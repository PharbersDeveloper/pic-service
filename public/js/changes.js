/**
 * Created by yym on 7/19/17.
 */
var c = null

var searchValue = null
var searchTime = null
//---------------------------------------全局变量--------------------------------
var atc1=""
var atc2=""
var atc3=""
var oral_name=""
var product_name=""
var edge=""
var manufacture_type=""
var manufacture_name=""
var product_type=""
var specifications=""
var pac=""
var userName = "";
//----------------------------------------------------------------------------------
try {
    userName = $.cookie("user_name")
}catch(ex) {
    console.info(ex)
}
var searchCount = 0;
$('.timepk_year').datetimepicker({
    language: 'zh-CN', format: "yyyy", weekStart: 1,
    todayBtn: true, autoclose: true, todayHighlight: 1,
    startView: 4, minView: 4, forceParse: 0
});

$('.timepk_month').datetimepicker({
    language: 'zh-CN', format: "yyyy-mm", weekStart: 1,
    todayBtn: 1, autoclose: 1, todayHighlight: 1,
    startView: 3, minView: 3, forceParse: 0
});

//----------------------------------------左侧下拉列表------------------------------------------
$("document").ready(function () {
    $(".dataTable").hide()
    showSomeNav()
    showOtherNav()

});
// 左导航
var showSomeNav = function() {
    var token= $.cookie("token")
    var js={
        "token" : token
    }
    $.ajax({
        type: "POST",
        url: "/category",
        data: JSON.stringify(js),
        contentType: "application/json,charset=utf-8",
        success: function (data) {
            if (data.status == "ok") {
                leftInitial("atc1", "category", data.result.atc_one)
                leftInitial("atc2", "category", data.result.atc_tow)
                leftInitial("atc3", "category", data.result.atc_three)
                leftInitial("oral_name", "oral_name", data.result.oral_name)
                leftInitial("product_name", "product_name", data.result.product)
            }
        }
    })

}
function showOtherNav() {
    var token= $.cookie("token")
    var js={
        "token" : token
    }
    $.ajax({
        type: "POST",
        url: "/showConfig",
        data: JSON.stringify(js),
        contentType: "application/json,charset=utf-8",
        success: function (data) {
            if (data.status == "ok") {
                leftInitial("edge", "edge", data.result.info[0].province)
                leftInitial("manufacture_name", "manufacture_name", data.result.info[0].manufacture)
                leftInitial("manufacture_type", "manufacture_type",eval(["内资","合资"]))
                leftInitial("product_type", "product_type", data.result.info[0].product_type)
                leftInitial("specifications", "specifications", data.result.info[0].specifications)
                leftInitial("package", "package", data.result.info[0].package)
            }
        }
    })
}

function leftInitial(id,key,res){
    var buttonObj=$('#'+id+'_button')
    var ulObj=$('#'+id+'_ul')
    var inObj=$('#'+id+'_input')
    var divObj=$('#'+id+'_div')
    divObj.hide()
    // buttonObj.click(function(){
    //     console.log(buttonObj)
    //     if(divObj.css("display")=="none"){
    //         divObj.show()
    //         buttonObj.find("i").removeClass("glyphicon-menu-right")
    //         buttonObj.find("i").addClass("glyphicon-menu-down")
    //     }else{
    //         divObj.hide()
    //         buttonObj.find("i").removeClass("glyphicon-menu-down")
    //         buttonObj.find("i").addClass("glyphicon-menu-right")
    //     }
    //
    // });

    $.each(res, function (i, item) {
        ulObj.append("<li class='downLi' onclick='searchClick($(this).text(),&quot;"+id+"&quot;,&quot;"+key+"&quot;)'>" + item + "</li>")
    });
}
function  buttonClick(buttonObj,id) {
    var divObj=$('#'+id+'_div')
    if(divObj.css("display")=="none"){
        $(".subMenu").hide()
        $(".aI").find("i").removeClass("glyphicon-menu-down")
        $(".aI").find("i").addClass("glyphicon-menu-right")
        divObj.show()
        buttonObj.find("i").removeClass("glyphicon-menu-right")
        buttonObj.find("i").addClass("glyphicon-menu-down")
    }else{
        divObj.hide()
        buttonObj.find("i").removeClass("glyphicon-menu-down")
        buttonObj.find("i").addClass("glyphicon-menu-right")
    }
}
function inputClick(id) {
    var buttonObj=$('#'+id+'_button')
    var inputObj=$('#'+ id+'_input')
    var divObj=$('#'+id+'_div')
    var li=$("#"+id+"_ul").find("li")
    $(".subMenu").hide()
    $(".aI").find("i").removeClass("glyphicon-menu-down")
    $(".aI").find("i").addClass("glyphicon-menu-right")
    divObj.show()
    buttonObj.find("i").removeClass("glyphicon-menu-right")
    buttonObj.find("i").addClass("glyphicon-menu-down")
    //文本框输入
    inputObj.keyup(function () {
        divObj.css('display', 'block');

        if (inputObj.val().length <= 0) {
            li.css('display', 'block');
            return;
        }
        li.css('display', 'none');
        for (var i = 0; i < li.size(); i++) {
            var item=$(li[i])
            if (item.text().indexOf(inputObj.val())>-1) {
                item.css('display', 'block');
            }
        }
    });

}
function showCategoryNav(json,value){
    $.ajax({
        type: "POST",
        url: "/categoryLinkage",
        data: JSON.stringify(json),
        contentType: "application/json,charset=utf-8",
        success: function (data) {
            if (data.status == "ok") {
                reloadLeft("atc1",data.result.atc_one,"category")
                reloadLeft("atc2", data.result.atc_tow,"category")
                reloadLeft("atc3", data.result.atc_three,"category")
                reloadLeft("oral_name",  data.result.oral_name,"oral_name")
                reloadLeft("product_name", data.result.product,"product_name")
            }
        }
    })

}
function reloadLeft(id,result,key) {
    var ulObj=$('#'+id+'_ul')
    ulObj.find("li").remove()
    $.each(result, function (i, item) {
        ulObj.append("<li class='downLi' onclick='searchClick($(this).text(),&quot;"+id+"&quot;,&quot;"+key+"&quot;)'>" + item + "</li>")
    });
}

function searchClick(c,id,key){
    var token=$.cookie("token")
    var value=c
    var buttonObj=$('#'+id+'_button')
    var inObj=$('#'+id+'_input')
    var divObj=$('#'+id+'_div')
    inObj.val(value)
    divObj.hide()
    buttonObj.find("i").removeClass("glyphicon-menu-down")
    buttonObj.find("i").addClass("glyphicon-menu-right")
    var temp=new Object()
    temp[key]=value
    var v=JSON.stringify(temp)
    if(id=="atc1") {
        var json={
            "token":token,
            "level":"0",
            "des":value
        }

        showCategoryNav(json,value)
        atc1 = JSON.parse(v)
    }else if(id=="atc2"){
        var json={
            "token":token,
            "level":"1",
            "des":value
        }
        showCategoryNav(json,value)
        atc2=JSON.parse(v)
    }else if(id=="atc3"){
        var json={
            "token":token,
            "level":"2",
            "des":value
        }
        showCategoryNav(json,value)
        atc3=JSON.parse(v)
    }else if(id=="manufacture_name"){
        var js=JSON.parse(v)
        manufacture_name={
            "manufacture_name": new Array(js.manufacture_name)
        }
    }else if(id=="edge"){
        var js=JSON.parse(v)
        edge={
            "edge": new Array(js.edge)
        }
    }else if(id=="specifications"){
        var js=JSON.parse(v)
        specifications={
            "specifications": js.specifications
        }
    }else if(id=="oral_name"){

        var json={
            "token":token,
            "level":"3",
            "des":value
        }
        showCategoryNav(json,value)
        oral_name=JSON.parse(v)

    }else if(id=="product_name"){
        var json={
            "token":token,
            "level":"4",
            "des":value
        }
        showCategoryNav(json,value)
        product_name=JSON.parse(v)
    }else if(id=="manufacture_type"){
        manufacture_type=JSON.parse(v)

    }else if(id=="product_type"){
        product_type=JSON.parse(v)

    }else if(id=="package"){
        pac=JSON.parse(v)

    } else{
        window.console.error("no search")
    }

}

var getSearchValue = function () {
    var atc = [
        atc1,atc2,atc3
    ];
    var categoryChoose=null
    $.each(atc, function(i, v) {
        if(v.category!=undefined){
            categoryChoose = {
                "category" : v.category
            }
        }

    })
    return $.extend(categoryChoose, edge, oral_name, product_name, manufacture_type, manufacture_name, product_type, specifications, pac)
}

//-------------------------------------------------时间------------------------------------------------

$(document).ready(function () {
    $("#tabYear").click(function () {
        $("#timeType").val(1);
        $("#monthInputb").val("");
        $("#quarterInput").val("");
        $("#quarterSelect").prev().children().eq(0).children().eq(0).children().click();
    });

    $("#tabMonth").click(function () {
        $("#timeType").val(2);
        $("#yearInputb").val("");
        $("#quarterInput").val("");
        $("#quarterSelect").prev().children().eq(0).children().eq(0).children().click();
    });

    $("#tabQuarter").click(function () {
        $("#timeType").val(3);
        $("#monthInputb").val("");
        $("#yearInputb").val("");
    });

    $('#timeTab a').click(function (e) {
        e.preventDefault()
        $(this).tab('show')
    });
    $(".userName").text(userName);

});






//------------------------------------------------选项框控制-------------------------------
function showDig() {
    searchValue = getSearchValue()
    searchTime = getTime()
    c = $.extend(searchValue, searchTime)
    var arr = Object.keys(c)
    if(arr.length == 0){
        alert("请至少在左边栏选择一个条件进行搜索！")
    }else {
        $("#xssj").attr({"class":"screen-box","onclick":""})
        $("#guim").text("")
        $('#zengzl').text("");
        $("#fene").text("")
        $("#chanps").text("")
        showDataList()
        showDataGather()
    }
    searchValue = null
    searchTime = null
}
//----------------------------------------------------登录信息--------------------------------
$("#userInfo").click(function () {
    var token = $.cookie("token")
    var data = JSON.stringify({
        "token": token
    });

    ajaxData("/auth/checkAuthToken", data, "POST", function(){
        if (data.status == "ok") {
            $.cookie("screen_name", data.result.auth.screen_name);
            $.cookie("email", data.result.auth.email);
            $.cookie("phoneNo", data.result.phoneNo);
            $.cookie("screen_photo", data.result.screen_photo);
            window.open("/userInfo")
        } else {
            window.location = "/login"
        }
    }, function(e){console.error(e)})
})

