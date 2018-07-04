/**
 * Created by yym on 6/13/17.
 */
var userName = $.cookie("user_name")
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


    $(".mCustomScrollbar").mCustomScrollbar({
        theme: "minimal-dark",
        scrollEasing: "easeOutCirc",
        scrollInertia: 400
    });

    showAtcInfo()
    showleft()
});



//这个还可以在简化，@杨艳梅 回来你做
function showleft() {
    $.ajax({
        type: "POST",
        url: "/showConfig",
        data: JSON.stringify({}),
        contentType: "application/json,charset=utf-8",
        success: function (data) {
            if (data.status == "ok") {
                showLeftInfo("province", "区域", data.result.info[0].province,"glyphicon glyphicon-globe")
                showLeftInfo("manufacture", "生产厂家", data.result.info[0].manufacture,"glyphicon glyphicon-wrench")
                showLeftInfo("manufacturetype", "生产厂商类型",eval(["内资","合资"]),"glyphicon glyphicon-tags")
                showLeftInfo("dosage", "剂型", data.result.info[0].product_type,"glyphicon glyphicon-book")
                showLeftInfo("specification", "规格", data.result.info[0].specifications,"glyphicon-info-sign")
                showLeftInfo("package", "包装", data.result.info[0].package,"glyphicon glyphicon-lock")
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
                showLeftInfo("ATC1", "治疗I", data.result.atc_one,"glyphicon-briefcase")
                showLeftInfo("ATC2", "治疗II", data.result.atc_tow,"glyphicon glyphicon-briefcase")
                showLeftInfo("ATC3", "治疗III", data.result.atc_three,"glyphicon glyphicon-briefcase")
                showLeftInfo("genericnameinfo", "通用名", data.result.oral,"glyphicon glyphicon-subscript")
                showLeftInfo("product", "商品名", data.result.product,"glyphicon glyphicon-th")
            }
        }
    })
}

/**
 * 暂时没用到
 * @param data
 * @param flag
 * @returns {Array}
 */
var eachResult = function (data, flag) {
    var array = new Array();
    $.each(data, function(i, v){
        if(flag == "des") {
            array.push(v.des);
        }else if(flag == "def") {
            array.push(v.def);
        }else return array;
    });
    return array;
}

var creatSelect = function(id, info, graph) {
    var formatState = function(state){
        var g = "glyphicon" +' '+ graph
        if (!state.id) { return state.text; }
        var $state = $(
            '<span><i class= '+g+' ></i>'   + state.text + '</span>'
        );
        return $state;
    }
    return $("#" + id).select2({
        language: 'zh-CN',
        maximumInputLength: 100,//限制最大字符，以防坑货
        placeholder: info,
        allowClear: true,
        templateResult: formatState,
        escapeMarkup: function (m) {
            return m;
        }
    });
}

//控制左
function showLeftInfo(btn, info, res, graph) {
    var selectObj = creatSelect(btn, info, graph);
    selectObj.empty();//清空下拉框
    selectObj.append("<option value=''>info</option>");
    $.each(res, function (i, item) {
        selectObj.append("<option value=" + item + ">" + item + "</option>")
    })
}

//选项框控制
function showDig() {
    $("#xssj").attr({"class":"screen-box","onclick":""})
    $("#guim").text("")
    $('#zengzl').text("");
    $("#fene").text("")
    $("#chanps").text("")
    showDataList()
    showDataGather()
}
