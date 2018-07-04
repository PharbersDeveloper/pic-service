/**
 * Created by qianpeng on 2017/6/15.
 */
var searchCount = 0;
var hasResult = false

/**
 * ***************************************************************
 * 重置按钮
 */
var reset=function () {
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
    $(".leftMenu").find("li").remove("li")
    $("#yearInputb").val("")
    $("#monthInputb").val("")
    $("#guim").text("")
    $('#zengzl').text("")
    $("#fene").text("")
    $("#chanps").text("")
    $("#atc1_input").val("")
    $("#atc2_input").val("")
    $("#atc3_input").val("")
    $("#oral_name_input").val("")
    $("#product_name_input").val("")
    $("#package_input").val("")
    $("#specifications_input").val("")
    $("#manufacture_name_input").val("")
    $("#product_type_input").val("")
    $("#edge_input").val("")
    $("#manufacture_type_input").val("")
    showSomeNav()
    showOtherNav()
    $(".dataTable").hide();
    $(".img").show();

}
/**
 * 数据列表
 */
var showDataList = function() {
    $(".img").hide();
    $(".dataTable").show();
    pageResult(1)
}

var pageResult = function(skip) {
    searchCount++
    $("#tbody").empty();
    c = $.extend(getSearchValue(), getTime())
    var data = JSON.stringify({
        "token": $.cookie("token"),
        "condition":{
            "category": c.category,
            "oral_name": c.oral_name,
            "product_name": c.product_name,
            "edge": c.edge,
            "date": c.date,
            "product_type": c.product_type,
            "manufacture_name": c.manufacture_name,
            "manufacture_type": c.manufacture_type,
            "specifications": c.specifications,
            "package": c.package
        },
        "skip": skip
    });
    ajaxData("/data/search", data, "POST", function(r){
        if (r.status == "ok") {
            hasResult = true;
            $("#tbody").empty();
            $("#pageview").show()
            $.each(r.search_result, function(i, v){
                $("#tbody").append("<tr>" +
                        "<th>" +v.index+ "</th>"+
                        "<th>" +v.date+ "</th>"+
                        "<th>" +v.province+ "</th>"+
                        "<th>" +v.product_name+ "</th>"+
                        "<th>" +v.sales+ "</th>"+
                        "<th>" +v.units+ "</th>"+
                        "<th>" +v.oral_name+ "</th>"+
                        "<th>" +v.manufacture+ "</th>"+
                        "<th>" +v.specifications+ "</th>"+
                        "<th>" +v.act1+ "</th>"+
                        "<th>" +v.act2+ "</th>"+
                        "<th>" +v.act3+ "</th>"+
                        "<th>" +v.product_unit+ "</th>"+
                        "<th>" +v.manufacture_type+ "</th>"+
                        "<th>" +v.product_type+ "</th>"+
                        "<th>" +v.package+ "</th>"+
                    "</tr>")
                // $("#tbody").append(v.html)
            })
            Page(r)
            if(searchCount == 5){
                $("#xssj").attr({"class":"search-btn","onclick":"showDig()"})
            }
        }else{
            $("#xssj").attr({"class":"search-btn","onclick":"showDig()"})
            $("#pageview").hide();
            Page(null)
        }
        searchCount = 0
    }, function(e){console.error(e)})
}

var getTime = function() {
    var timeType = $("#timeType").val();
    var start = null;
    var end = null;
    if(timeType == 1 && $("#yearInputb").val() != ""){
        var year = $("#yearInputb").val()
        start = (parseInt(year) - 1) + "01"
        end = year + "01"
    }else if(timeType == 2 && $("#monthInputb").val() != ""){
        var yearMonth = $("#monthInputb").val()
        var year = yearMonth.split("-")[0]
        var month = yearMonth.split("-")[1]
        start = (parseInt(year) - 1) + month
        end = year + month
    }

    var date = null;
    if(start != null) {
        date = {
            "date": {
                "start": start,
                "end": end
            }
        }
    }
    return date;
}

/**
 * ************************************************************************
 * 显示主页的四个小汇总
 */
var showDataGather = function(){
    c = $.extend(getSearchValue(), getTime())
    var data = JSON.stringify({
        "token": $.cookie("token"),
        "condition":{
            "category": c.category,
            "oral_name": c.oral_name,
            "product_name": c.product_name,
            "date": c.date
        }
    });
    console.log(data)
    if(c.date==undefined&&(c.oral_name!=undefined||c.product_name!=undefined)){
        calcMarket(data);
        productSize(data);
        $('#zengzl').text("");
        $('#fene').text("");
    }else {
        calcMarket(data);
        calcTrend(data);
        calcPercentage(data);
        productSize(data);
    }

}

/**
 * 市场规模
 */
var calcMarket = function(data) {
    searchCount++
    ajaxData("/data/calc/market", data, "POST", function(r){
        if (r.status == "ok") {
            var market = r.result.calc.sales;
            $("#guim").text(toThousands(market))

            if(searchCount == 5){
                $("#xssj").attr({"class":"search-btn","onclick":"showDig()"})
            }
        }else{
            $("#xssj").attr({"class":"search-btn","onclick":"showDig()"})
        }
        searchCount = 0
    }, function (e) {console.error(e)})
}

/**
 * 市场增长率
 */
var calcTrend = function(data) {
    searchCount++
    ajaxData("/data/calc/trend", data, "POST", function (r) {
        if (r.status == "ok") {
            var trend = parseFloat(r.result.trend);
            var treNum=(Math.floor(trend*100)/100)+ "%"
            $('#zengzl').text(treNum);
            if(searchCount == 5){
                $("#xssj").attr({"class":"search-btn","onclick":"showDig()"})
            }
        }else {
            $("#xssj").attr({"class":"search-btn","onclick":"showDig()"})
        }
        searchCount = 0
    },function(e){console.error(e)})
}

/**
 * 市场份额
 */
var calcPercentage = function(data) {
    searchCount++
    ajaxData("/data/calc/percentage", data, "POST", function (r) {
        if (r.status == "ok") {
            var percentage = parseFloat(r.result.percentage);
            var percentNum=(Math.floor(percentage*10000)/100) +"%"
            $('#fene').text(percentNum);
            if(searchCount == 5){
                $("#xssj").attr({"class":"search-btn","onclick":"showDig()"})
            }
        }else {
            $("#xssj").attr({"class":"search-btn","onclick":"showDig()"})
        }
        searchCount = 0
    }, function (e) {console.error(e)})
}

/**
 * 产品数量
 */
var productSize = function(data) {
    searchCount++
    ajaxData("/data/calc/quantity", data, "POST", function(r){
        if (r.status == "ok") {
            $('#chanps').text(r.result.size);
            if(searchCount == 5){
                $("#xssj").attr({"class":"search-btn","onclick":"showDig()"})
            }
        }else {
            $("#xssj").attr({"class":"search-btn","onclick":"showDig()"})
        }
        searchCount = 0
    }, function(e){console.error(e)});
}

var showData = function() {

    var c = $.extend(getSearchValue(), getTime())
    var token = {"token": $.cookie("token")}
    var condition = {
        "condition": {
            "category": c.category,
            "oral_name": c.oral_name,
            "product_name": c.product_name,
            "date": c.date
        }
    }
    var reportid = ""
    if(!$.isEmptyObject(JSON.parse(JSON.stringify(condition)).condition)) {
        reportid = md5(JSON.stringify(token)+JSON.stringify(condition))
        var data = JSON.stringify({
            "reportid": reportid,
            "token" : token.token,
            "condition" : condition.condition
        })
        ajaxData("/data/reportparameter", data, "POST", function(r){}, function(e){console.error(e)})
    }
    return reportid
}


var report = function() {
    c = $.extend(getSearchValue(), getTime())
    var result = showData()
    if(!hasResult){
        alert("没有搜索数据！不能生成报告！")
    } else if(result != ""&&c.date!=undefined){
        var w = window.open("")
        w.window.location = "/report"+result
    }else {
        alert("请在选择时间后至少选择一个治疗类别或通用名或商品名进行搜索！")
    }
    // var w = window.open("")
    // w.window.location = "/report"+"aa"
}

var goSampleData = function () {
    var w = window.open("")
    w.window.location = "/sampleData/show"
}
