/**
 * Created by qianpeng on 2017/6/15.
 */

// var token =  $.cookie("token")

/***
 * 针对于左侧菜单栏的条选中返回Object
 * 只针对于这个项目
 * 后续需要改变成公用的
 */
// var getSearchValue = function () {
//     var atc = [
//         $("#ATC1"),
//         $("#ATC2"),
//         $("#ATC3")
//     ];
//
//     var category = null;
//     $.each(atc, function(i, v) {
//         if(v.val() != "") {
//             category = {
//                 "category" : v.val()
//             }
//         }
//     })
//
//     var oral = null;
//     if($("#genericnameinfo").val() != "") {
//         oral = {
//             "oral_name": $("#genericnameinfo").val()
//         }
//     }
//
//     var product = null;
//     if($("#product").val() != "") {
//         product = {
//             "product_name": $("#product").val()
//         }
//     }
//
//     var edge = null;
//     if($("#province").val() != "") {
//         edge = {
//             "edge" : new Array($("#province").val())
//         }
//     }
//
//     var manufacturetype = null;
//     if($("#manufacturetype").val() != "") {
//         manufacturetype = {
//             "manufacture_type": $("#manufacturetype").val()
//         }
//     }
//
//     var manufacture = null;
//     if($("#manufacture").val() != "") {
//         manufacture = {
//             "manufacture_name": new Array($("#manufacture").val())
//         }
//     }
//
//     var dosage = null;
//     if($("#dosage").val() != "") {
//         dosage = {
//             "product_type": $("#dosage").val()
//         }
//     }
//
//     var specification = null
//     if($("#specification").val()!= "") {
//         specification = {
//             "specifications": $("#specification").val() + " MG"
//         }
//     }
//
//     var package = null;
//     if($("#package").val() != "") {
//         package = {
//             "package": $("#package").val()
//         }
//     }
//     return $.extend(category, edge, oral, product, manufacturetype, manufacture, dosage, specification, package)
// }

/**
 * 公用ajax调用，初步版本
 */
var ajaxData = function(url, data, type, successfun, errorfun) {
    $.ajax({
        type: type,
        url: url,
        dataType: "json",
        cache: false,
        data: data,
        contentType: "application/json,charset=utf-8",
        success: function (data) {
            successfun(data)
        },
        error: function (e) {
            errorfun(e)
        }
    });
}
//--------------------------------------------------回主页---------------------------------------
function goHome() {
    window.location = "/data/report";
}