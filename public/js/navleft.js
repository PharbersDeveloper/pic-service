/**
 * Created by yym on 7/3/17.
 */
var c = null

var searchValue = null
var searchTime = null

/**
 * 重置按钮
 */
var resetSearch = function () {

    atc1=""
    atc2=""
    atc3=""
    oral_name=""
    product_name=""
    edge=""
    manufacture_type=""
    manufacture_name=""
    product_type=""
    specifications=""
    pac=""

    $(".selectInfo").remove("div")
    $("#yearInputb").val("")
    $("#monthInputb").val("")
    $("#guim").text("X")
    $('#zengzl').text("X");
    $("#fene").text("X")
    $("#chanps").text("X")
    var arr = Object.keys(getSearchValue())
    arr.forEach(function (x) {
        console.log(x)
    })
}

var userName = "";
try {
    userName = $.cookie("user_name")
}catch(ex) {
    console.info(ex)
}

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
$("document").ready(function () {
    $(".dataTable").hide()
    showleft()
    showAtcInfo()
    $('#leftMenu').scrollspy({ target: '#leftMenu' })

});
var arr=new Array()


function showleft() {
    $.ajax({
        type: "POST",
        url: "/showConfig",
        data: JSON.stringify({}),
        contentType: "application/json,charset=utf-8",
        success: function (data) {
            if (data.status == "ok") {
                leftInitial("province", "edge", data.result.info[0].province,edge)
                leftInitial("manufacture", "manufacture_name", data.result.info[0].manufacture,manufacture_name)
                leftInitial("manufacturetype", "manufacture_type",eval(["内资","合资"]),manufacture_type)
                leftInitial("dosage", "product_type", data.result.info[0].product_type,product_type)
                leftInitial("specification", "specifications", data.result.info[0].specifications,specifications)
                leftInitial("package", "package", data.result.info[0].package,pac)
            }
        }
    })
}



var showAtcInfo = function() {
    $.ajax({
        type: "POST",
        url: "/category",
        data: JSON.stringify({}),
        contentType: "application/json,charset=utf-8",
        success: function (data) {
            if (data.status == "ok") {
                leftInitial("atc1", "category", data.result.atc_one,atc1)
                leftInitial("atc2", "category", data.result.atc_tow,atc2)
                leftInitial("atc3", "category", data.result.atc_three,atc3)
                leftInitial("genericnameinfo", "oral_name", data.result.oral_name,oral_name)
                leftInitial("product", "product_name", data.result.product,product_name)
            }
        }
    })

}



function leftInitial(id,key,res,v){
    var li='#'+id
    var pli=$(li)
    var ulid='#'+id+'_ul'
    var ulObj=$(ulid)
    $.each(res, function (i, item) {
        ulObj.append("<li class='downLi' onclick='searchClick($(this).text(),&quot;"+id+"&quot;,&quot;"+key+"&quot;)'>" + item + "</li>")
    });
    ulObj.hide()
    pli.click(function(){
        ulObj.toggle()
    });
}


function searchClick(c,id,key){
    var ili='#'+id;
    var pli=$(ili);
    var value=c
    if(pli.find("div").length==0){
        pli.append("<div class='selectInfo'><i class='fa fa-check-circle' aria-hidden='true'></i>&nbsp;"+c+"<div>");

    }else{
        (pli.find("div")).html("<i class='fa fa-check-circle' aria-hidden='true'></i>&nbsp;"+c)

    }
    var temp=new Object()
    temp[key]=value
    var v=JSON.stringify(temp)
    if(id=="atc1") {
        atc1 = JSON.parse(v)
    }else if(id=="atc2"){
        atc2=JSON.parse(v)
    }else if(id=="atc3"){
        atc3=JSON.parse(v)
    }else if(id=="manufacture"){
        var js=JSON.parse(v)
        manufacture_name={
            "manufacture_name": new Array(js.manufacture_name)
        }
    }else if(id=="province"){
        var js=JSON.parse(v)
        edge={
            "edge": new Array(js.edge)
        }
    }else if(id=="specification"){
        var js=JSON.parse(v)
        specifications={
            "specifications": js.specifications
        }
    }else if(id=="genericnameinfo"){
        oral_name=JSON.parse(v)

    }else if(id=="product"){
        product_name=JSON.parse(v)

    }else if(id=="manufacturetype"){
        manufacture_type=JSON.parse(v)

    }else if(id=="dosage"){
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
    $("#jxname").text(userName.substring(userName.length - 1));

});






//选项框控制
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