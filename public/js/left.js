$('.timepk_year').datetimepicker({
		language : 'zh-CN',
		format: "yyyy",
		weekStart : 1,
		todayBtn : true,
		autoclose : true,
		todayHighlight : 1,
		startView : 4,
		minView : 4,
		forceParse : 0
});

$('.timepk_month').datetimepicker({
	language : 'zh-CN',
	format: "yyyy-mm",
	weekStart : 1,
	todayBtn : 1,
	autoclose : 1,
	todayHighlight : 1,
	startView : 3,
	minView : 3,
	forceParse : 0
});

function seachGrid(){
	isBtnDis(0);
	serach();
	var currentFilters = [];
	$.each($(".box-wrap select :selected"),function(i,v){
		if(v.value != null && v.value != ""){
			var field = $(this).parent().attr("name");
			var value = "";
			if(field == "atc1Name" || field == "atc2Name" || field == "atc3Name" || field == "genericnameName"){
				value = $(this).val().split(",")[0];
			}else{
				value = $(this).val();
			}
			currentFilters.push({
				field:field,
				operator:"eq",
				value:value
			});
		}
	});
	var timeType = $("#timeType").val();//时间类型 年 月 还是季度
	var time;//时间
	if(timeType == 1){
		time = $("#yearInputb").val();//年
		if(time != null && time != ""){
			currentFilters.push({
				field:"year",
				operator:"eq",
				value:time
			});
		}
	}
	applyFilter(currentFilters);
}

function applyFilter(currentFilters) {

    var gridData = $("#grid").data("kendoGrid");
    gridData.dataSource.filter({
        logic: "and",
        filters: currentFilters
    });
    if($("#province").select2("data")[0].id == 32){
    	gridData.hideColumn(2);
	}else{
		gridData.showColumn(2);
	}
}

//绑定字典内容到指定的Select控件
function BindSelect(ctrlName, url, type, textname, maps) {
    var selectObj = $('#'+ctrlName);
    //设置Select2的处理
    selectObj.select2({
		 language : 'zh-CN',
	     maximumInputLength:100,//限制最大字符，以防坑货
		 placeholder: textname,
		 allowClear: true,
		 escapeMarkup: function (m) { return m; }
	 }); 
  //绑定Ajax的内容
  //   $.post(url, maps, function (data) {
  //   	selectObj.empty();//清空下拉框
  //   	selectObj.append("<option value=''>" + textname + "</option>");
  //   	iskickout(data);
  //       $.each(data, function (i, v) {
  //       	var id = v.lid;
  //       	var name = "";
  //       	if(type == 0){
  //       		name = v.lname
  //       		if(ctrlName == "province"){//全国汇总-qy
  //       			$.each(list,function(j,k){
  //       				if(k == name+"-qy"){
  //       					selectObj.append("<option value='" + id + "'>" + name + "</option>");
  //       				}
  //       			});
  //       		}else{
  //       			selectObj.append("<option value='" + id + "'>" + name + "</option>");
  //       		}
  //       	}else if(type == 1){
  //       		var code = v.lname;
  //       		name = v.remarks;
  //       		selectObj.append("<option value='" + id + ","+code+"'>" + name + "</option>");
  //       	}else if(type == 2){
  //       		var code = v.parentCode;
  //       		name = v.lname
  //       		selectObj.append("<option value='" + id + ","+code+"'>" + name + "</option>");
  //       	}
  //       });
  //   });
}
var leftUrl = "../leftData/queryByIDALL";

/**
 * 加载Left.jsp左边的下拉数据
 */
function leftData(){
	document.getElementById("tabYear").click();
	$("#timeType").val("1");
	$("#yearInputb").val("");
	$("#monthInputb").val("");
	$("#quarterInput").val("");
	var tempMaps = new StringBuffer();
	BindSelect("ATC1","../leftData/queryByATC1",1,"治疗I",{});
	BindSelect("ATC2","../leftData/queryByATC2",1,"治疗II",{});
	BindSelect("ATC3","../leftData/queryByATC3",1,"治疗III",{});
	BindSelect("genericnameinfo","../leftData/queryBygenericName",2,"通用名");
	BindSelect("product","../leftData/queryByProduct",0,"商品名",{});
	BindSelect("province","../leftData/queryByProvince",0,"区域",{});
	BindSelect("manufacturetype","../leftData/queryByManufactureType",0,"生产厂家类型",{});
	BindSelect("manufacture","../leftData/queryByManufacture",0,"生产厂家",{});
	BindSelect("dosage","../leftData/queryByDosage",0,"剂型",{});
	BindSelect("specification","../leftData/queryBySpecification",0,"规格",{});
	BindSelect("package","../leftData/queryByPackage",0,"包装",{});
	$("#ATC1").change(function(){
		isBtnDis(0);
		tempMaps.cleans();
		var act1obj = $("#ATC1").select2("data");
		var obj = act1obj[0].id.split(",")[0] != "" ?  act1obj[0].id.split(",")[0] : "";
		if(obj != undefined && obj != ""){
			//tempMaps.append("{\"code\":\""+act1obj[0].id.split(",")[1]+"\"}");
			tempMaps.append("{\"temp\":1,\"leftType\":1,\"leftValue\":"+obj+"}");
			BindSelect("ATC2",leftUrl,1,"治疗II",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
			tempMaps.append("{\"temp\":1,\"leftType\":2,\"leftValue\":"+obj+"}");
			BindSelect("ATC3",leftUrl,1,"治疗III",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
			tempMaps.append("{\"temp\":1,\"leftType\":3,\"leftValue\":"+obj+"}");
			BindSelect("genericnameinfo",leftUrl,2,"通用名",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
			tempMaps.append("{\"temp\":1,\"leftType\":4,\"leftValue\":"+obj+"}");
			BindSelect("product",leftUrl,0,"商品名",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
			tempMaps.append("{\"temp\":1,\"leftType\":5,\"leftValue\":"+obj+"}");
			BindSelect("manufacturetype",leftUrl,0,"生产厂家类型",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
			tempMaps.append("{\"temp\":1,\"leftType\":6,\"leftValue\":"+obj+"}");
			BindSelect("manufacture",leftUrl,0,"生产厂家",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
			tempMaps.append("{\"temp\":1,\"leftType\":7,\"leftValue\":"+obj+"}");
			BindSelect("dosage",leftUrl,0,"剂型",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
			tempMaps.append("{\"temp\":1,\"leftType\":8,\"leftValue\":"+obj+"}");
 			BindSelect("specification",leftUrl,0,"规格",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
			tempMaps.append("{\"temp\":1,\"leftType\":9,\"leftValue\":"+obj+"}");
			BindSelect("package",leftUrl,0,"包装",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
		}else{
			BindSelect("ATC2","../leftData/queryByATC2",1,"治疗II",{});
			BindSelect("ATC3","../leftData/queryByATC3",1,"治疗III",{});
			BindSelect("genericnameinfo","../leftData/queryBygenericName",2,"通用名");
			BindSelect("product","../leftData/queryByProduct",0,"商品名",{});
			BindSelect("province","../leftData/queryByProvince",0,"区域",{});
			BindSelect("manufacturetype","../leftData/queryByManufactureType",0,"生产厂家类型",{});
			BindSelect("manufacture","../leftData/queryByManufacture",0,"生产厂家",{});
			BindSelect("dosage","../leftData/queryByDosage",0,"剂型",{});
			BindSelect("specification","../leftData/queryBySpecification",0,"规格",{});
			BindSelect("package","../leftData/queryByPackage",0,"包装",{});
		}
	});
	
	$("#ATC2").change(function(){
		isBtnDis(0);
		tempMaps.cleans();
		var act2obj = $("#ATC2").select2("data");
		var obj = act2obj[0].id.split(",")[0] != "" ?  act2obj[0].id.split(",")[0] : "";
		if(obj != undefined && obj != ""){
			//tempMaps.append("{\"code\":\""+act2obj[0].id.split(",")[1]+"\"}");
			//BindSelect("ATC3","../leftData/queryByATC3",1,"治疗III",eval("("+tempMaps.toString()+")"));
			tempMaps.append("{\"temp\":2,\"leftType\":2,\"leftValue\":"+obj+"}");
			BindSelect("ATC3",leftUrl,1,"治疗III",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
			tempMaps.append("{\"temp\":2,\"leftType\":3,\"leftValue\":"+obj+"}");
			BindSelect("genericnameinfo",leftUrl,2,"通用名",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
			tempMaps.append("{\"temp\":2,\"leftType\":4,\"leftValue\":"+obj+"}");
			BindSelect("product",leftUrl,0,"商品名",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
			tempMaps.append("{\"temp\":2,\"leftType\":5,\"leftValue\":"+obj+"}");
			BindSelect("manufacturetype",leftUrl,0,"生产厂家类型",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
			tempMaps.append("{\"temp\":2,\"leftType\":6,\"leftValue\":"+obj+"}");
			BindSelect("manufacture",leftUrl,0,"生产厂家",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
			tempMaps.append("{\"temp\":2,\"leftType\":7,\"leftValue\":"+obj+"}");
			BindSelect("dosage",leftUrl,0,"剂型",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
			tempMaps.append("{\"temp\":2,\"leftType\":8,\"leftValue\":"+obj+"}");
			BindSelect("specification",leftUrl,0,"规格",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
			tempMaps.append("{\"temp\":2,\"leftType\":9,\"leftValue\":"+obj+"}");
			BindSelect("package",leftUrl,0,"包装",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
		}else{
			BindSelect("ATC3","../leftData/queryByATC3",1,"治疗III",{});
			BindSelect("genericnameinfo","../leftData/queryBygenericName",2,"通用名");
			BindSelect("product","../leftData/queryByProduct",0,"商品名",{});
			BindSelect("province","../leftData/queryByProvince",0,"区域",{});
			BindSelect("manufacturetype","../leftData/queryByManufactureType",0,"生产厂家类型",{});
			BindSelect("manufacture","../leftData/queryByManufacture",0,"生产厂家",{});
			BindSelect("dosage","../leftData/queryByDosage",0,"剂型",{});
			BindSelect("specification","../leftData/queryBySpecification",0,"规格",{});
			BindSelect("package","../leftData/queryByPackage",0,"包装",{});
		}
	});
	
	$("#ATC3").change(function(){
		isBtnDis(0);
		tempMaps.cleans();
		var act3obj = $("#ATC3").select2("data");
		var obj = act3obj[0].id.split(",")[0] != "" ?  act3obj[0].id.split(",")[0] : "";
		if(obj != undefined && obj != ""){
//			tempMaps.append("{\"code\":\""+act3obj[0].id.split(",")[1]+"\"}");
//			BindSelect("genericnameinfo","../leftData/queryBygenericName",2,"通用名",eval("("+tempMaps.toString()+")"));
			tempMaps.append("{\"temp\":3,\"leftType\":3,\"leftValue\":"+obj+"}");
			BindSelect("genericnameinfo",leftUrl,2,"通用名",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
			tempMaps.append("{\"temp\":3,\"leftType\":4,\"leftValue\":"+obj+"}");
			BindSelect("product",leftUrl,0,"商品名",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
			tempMaps.append("{\"temp\":3,\"leftType\":5,\"leftValue\":"+obj+"}");
			BindSelect("manufacturetype",leftUrl,0,"生产厂家类型",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
			tempMaps.append("{\"temp\":3,\"leftType\":6,\"leftValue\":"+obj+"}");
			BindSelect("manufacture",leftUrl,0,"生产厂家",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
			tempMaps.append("{\"temp\":3,\"leftType\":7,\"leftValue\":"+obj+"}");
			BindSelect("dosage",leftUrl,0,"剂型",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
			tempMaps.append("{\"temp\":3,\"leftType\":8,\"leftValue\":"+obj+"}");
			BindSelect("specification",leftUrl,0,"规格",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
			tempMaps.append("{\"temp\":3,\"leftType\":9,\"leftValue\":"+obj+"}");
			BindSelect("package",leftUrl,0,"包装",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
		}else{
			BindSelect("genericnameinfo","../leftData/queryBygenericName",2,"通用名");
			BindSelect("product","../leftData/queryByProduct",0,"商品名",{});
			BindSelect("province","../leftData/queryByProvince",0,"区域",{});
			BindSelect("manufacturetype","../leftData/queryByManufactureType",0,"生产厂家类型",{});
			BindSelect("manufacture","../leftData/queryByManufacture",0,"生产厂家",{});
			BindSelect("dosage","../leftData/queryByDosage",0,"剂型",{});
			BindSelect("specification","../leftData/queryBySpecification",0,"规格",{});
			BindSelect("package","../leftData/queryByPackage",0,"包装",{});
		}
	});
	
	$("#genericnameinfo").change(function(){
		isBtnDis(0);
		tempMaps.cleans();
		var genericobj = $("#genericnameinfo").select2("data")[0];
		var obj = genericobj.id.split(",")[0] != "" ?  genericobj.id.split(",")[0] : "";
		if(obj != undefined && obj != ""){
			tempMaps.append("{\"temp\":4,\"leftType\":4,\"leftValue\":"+obj+"}");
			BindSelect("product",leftUrl,0,"商品名",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
			tempMaps.append("{\"temp\":4,\"leftType\":5,\"leftValue\":"+obj+"}");
			BindSelect("manufacturetype",leftUrl,0,"生产厂家类型",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
			tempMaps.append("{\"temp\":4,\"leftType\":6,\"leftValue\":"+obj+"}");
			BindSelect("manufacture",leftUrl,0,"生产厂家",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
			tempMaps.append("{\"temp\":4,\"leftType\":7,\"leftValue\":"+obj+"}");
			BindSelect("dosage",leftUrl,0,"剂型",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
			tempMaps.append("{\"temp\":4,\"leftType\":8,\"leftValue\":"+obj+"}");
			BindSelect("specification",leftUrl,0,"规格",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
			tempMaps.append("{\"temp\":4,\"leftType\":9,\"leftValue\":"+obj+"}");
			BindSelect("package",leftUrl,0,"包装",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
		}else{
			BindSelect("product","../leftData/queryByProduct",0,"商品名",{});
			BindSelect("province","../leftData/queryByProvince",0,"区域",{});
			BindSelect("manufacturetype","../leftData/queryByManufactureType",0,"生产厂家类型",{});
			BindSelect("manufacture","../leftData/queryByManufacture",0,"生产厂家",{});
			BindSelect("dosage","../leftData/queryByDosage",0,"剂型",{});
			BindSelect("specification","../leftData/queryBySpecification",0,"规格",{});
			BindSelect("package","../leftData/queryByPackage",0,"包装",{});
		}
	});
	
	$("#product").change(function(){
		isBtnDis(0);
		tempMaps.cleans();
		var productobj = $("#product").select2("data")[0];
		var obj = productobj.id != "" ?  productobj.id : "";
		if(obj != undefined && obj != ""){
			tempMaps.append("{\"temp\":5,\"leftType\":5,\"leftValue\":\""+obj+"\"}");
			BindSelect("manufacturetype",leftUrl,0,"生产厂家类型",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
			tempMaps.append("{\"temp\":5,\"leftType\":6,\"leftValue\":\""+obj+"\"}");
			BindSelect("manufacture",leftUrl,0,"生产厂家",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
			tempMaps.append("{\"temp\":5,\"leftType\":7,\"leftValue\":\""+obj+"\"}");
			BindSelect("dosage",leftUrl,0,"剂型",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
			tempMaps.append("{\"temp\":5,\"leftType\":8,\"leftValue\":\""+obj+"\"}");
			BindSelect("specification",leftUrl,0,"规格",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
			tempMaps.append("{\"temp\":5,\"leftType\":9,\"leftValue\":\""+obj+"\"}");
			BindSelect("package",leftUrl,0,"包装",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
		}else{
			BindSelect("manufacturetype","../leftData/queryByManufactureType",0,"生产厂家类型",{});
			BindSelect("manufacture","../leftData/queryByManufacture",0,"生产厂家",{});
			BindSelect("dosage","../leftData/queryByDosage",0,"剂型",{});
			BindSelect("specification","../leftData/queryBySpecification",0,"规格",{});
			BindSelect("package","../leftData/queryByPackage",0,"包装",{});
		}
	});
	
	$("#manufacturetype").change(function(){
		isBtnDis(0);
		tempMaps.cleans();
		/*var manufacturetypeobj = $("#manufacturetype").select2("data")[0];
		var obj = manufacturetypeobj.id != "" ?  manufacturetypeobj.id : "";*/
		var productobj = $("#product").select2("data")[0];
		var obj = productobj.id != "" ?  productobj.id : "";
		if(obj != undefined && obj != ""){
			tempMaps.append("{\"temp\":5,\"leftType\":6,\"leftValue\":"+obj+"}");
			BindSelect("manufacture",leftUrl,0,"生产厂家",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
			tempMaps.append("{\"temp\":5,\"leftType\":7,\"leftValue\":"+obj+"}");
			BindSelect("dosage",leftUrl,0,"剂型",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
			tempMaps.append("{\"temp\":5,\"leftType\":8,\"leftValue\":"+obj+"}");
			BindSelect("specification",leftUrl,0,"规格",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
			tempMaps.append("{\"temp\":5,\"leftType\":9,\"leftValue\":"+obj+"}");
			BindSelect("package",leftUrl,0,"包装",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
		}else{
			BindSelect("manufacture","../leftData/queryByManufacture",0,"生产厂家",{});
			BindSelect("dosage","../leftData/queryByDosage",0,"剂型",{});
			BindSelect("specification","../leftData/queryBySpecification",0,"规格",{});
			BindSelect("package","../leftData/queryByPackage",0,"包装",{});
		}
	});
	
	$("#manufacture").change(function(){
		isBtnDis(0);
		tempMaps.cleans();
		/*var manufactureobj = $("#manufacture").select2("data")[0];
		var obj = manufactureobj.id != "" ?  manufactureobj.id : "";*/
		var productobj = $("#product").select2("data")[0];
		var obj = productobj.id != "" ?  productobj.id : "";
		if(obj != undefined && obj != ""){
			tempMaps.append("{\"temp\":5,\"leftType\":7,\"leftValue\":"+obj+"}");
			BindSelect("dosage",leftUrl,0,"剂型",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
			tempMaps.append("{\"temp\":5,\"leftType\":8,\"leftValue\":"+obj+"}");
			BindSelect("specification",leftUrl,0,"规格",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
			tempMaps.append("{\"temp\":5,\"leftType\":9,\"leftValue\":"+obj+"}");
			BindSelect("package",leftUrl,0,"包装",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
		}else{
			BindSelect("dosage","../leftData/queryByDosage",0,"剂型",{});
			BindSelect("specification","../leftData/queryBySpecification",0,"规格",{});
			BindSelect("package","../leftData/queryByPackage",0,"包装",{});
		}
	});
	
	$("#dosage").change(function(){
		isBtnDis(0);
		tempMaps.cleans();
		/*var dosageobj = $("#dosage").select2("data")[0];
		var obj = dosageobj.id != "" ?  dosageobj.id : "";*/
		var productobj = $("#product").select2("data")[0];
		var obj = productobj.id != "" ?  productobj.id : "";
		if(obj != undefined && obj != ""){
			tempMaps.append("{\"temp\":5,\"leftType\":8,\"leftValue\":"+obj+"}");
			BindSelect("specification",leftUrl,0,"规格",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
			tempMaps.append("{\"temp\":5,\"leftType\":9,\"leftValue\":"+obj+"}");
			BindSelect("package",leftUrl,0,"包装",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
		}else{
			BindSelect("specification","../leftData/queryBySpecification",0,"规格",{});
			BindSelect("package","../leftData/queryByPackage",0,"包装",{});
		}
	});
	
	$("#specification").change(function(){
		isBtnDis(0);
		tempMaps.cleans();
		/*var specificationobj = $("#specification").select2("data")[0];
		var obj = specificationobj.id != "" ?  specificationobj.id : "";*/
		var productobj = $("#product").select2("data")[0];
		var obj = productobj.id != "" ?  productobj.id : "";
		if(obj != undefined && obj != ""){
			tempMaps.append("{\"temp\":5,\"leftType\":9,\"leftValue\":"+obj+"}");
			BindSelect("package",leftUrl,0,"包装",eval("("+tempMaps.toString()+")"));
			tempMaps.cleans();
		}else{
			BindSelect("package","../leftData/queryByPackage",0,"包装",{});
		}
	});
	$("#package").change(function(){
		isBtnDis(0);
	});
	
}

/**
 * 根据以下条件时候禁用按钮
 * @returns
 */
function isBtnDis(type){
	if(type == 0){
		var product = $("#product").select2("data")[0].id;
		var manufacturetype = $("#manufacturetype").select2("data")[0].id;
		var manufacture = $("#manufacture").select2("data")[0].id;
		var dosage = $("#dosage").select2("data")[0].id;
		var specification = $("#specification").select2("data")[0].id;
		var packages = $("#package").select2("data")[0].id;
		if(product != ""){
			$("#xsbg").attr({"onclick":"","class":"screen-box"});
			$("#reqr").attr({"onclick":"","class":"screen-box"});
			$("#xsssbg").attr({"onclick":"","style":"background: #b3b3b3 none repeat scroll 0 0;"});
		}else if(manufacturetype != ""){
			$("#xsbg").attr({"onclick":"","class":"screen-box"});
			$("#reqr").attr({"onclick":"","class":"screen-box"});
			$("#xsssbg").attr({"onclick":"","style":"background: #b3b3b3 none repeat scroll 0 0;"});
		}else if(manufacture != ""){
			$("#xsbg").attr({"onclick":"","class":"screen-box"});
			$("#reqr").attr({"onclick":"","class":"screen-box"});
			$("#xsssbg").attr({"onclick":"","style":"background: #b3b3b3 none repeat scroll 0 0;"});
		}else if(dosage != ""){
			$("#xsbg").attr({"onclick":"","class":"screen-box"});
			$("#reqr").attr({"onclick":"","class":"screen-box"});
			$("#xsssbg").attr({"onclick":"","style":"background: #b3b3b3 none repeat scroll 0 0;"});
		}else if(specification != ""){
			$("#xsbg").attr({"onclick":"","class":"screen-box"});
			$("#reqr").attr({"onclick":"","class":"screen-box"});
			$("#xsssbg").attr({"onclick":"","style":"background: #b3b3b3 none repeat scroll 0 0;"});
		}else if(packages != ""){
			$("#xsbg").attr({"onclick":"","class":"screen-box"});
			$("#reqr").attr({"onclick":"","class":"screen-box"});
			$("#xsssbg").attr({"onclick":"","style":"background: #b3b3b3 none repeat scroll 0 0;"});
		}else{
			$("#xsbg").attr({"onclick":"showView(0)","class":"search-btn"});
			$("#reqr").attr({"onclick":"serachbtn()","class":"search-btn"});
			$("#xsssbg").attr({"onclick":"showView(0)","style":""});
		}
	}else{
		$("#xsbg").attr({"onclick":"showView(0)","class":"search-btn"});
		$("#reqr").attr({"onclick":"serachbtn()","class":"search-btn"});
		$("#xsssbg").attr({"onclick":"showView(0)","style":""});
	}
}

/**
 * 判断左边的Left是需要什么按钮
 */
function leftBtnType(){
	if(pageTypeIndex == "index1Page"){
		$("#screen_box").empty().append('<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6"><a class="search-btn" onclick="showView(0)">显示报告</a></div><div class="col-xs-6 col-sm-6 col-md-6 col-lg-6"><a class="search-btn" onclick="showView(1)">显示数据</a></div>');
	}/*else if(pageTypeIndex == "index2Page"){
		$("#screen_box").empty().append('<a onclick="resetbtn()" style="margin-left: 5px;">重置</a> <a class="search-btn" onclick="seachGrid()" style="margin-left: 5px;">确认</a>');
	}else if(pageTypeIndex == "reportPage"){
		$("#screen_box").empty().append('<a onclick="resetbtn()" style="margin-left: 5px;">重置</a> <a class="search-btn" onclick="serachbtn()" style="margin-left: 5px;">确认</a>');
	}*/
}

function showView(type){
	if($("#timeType").val() == 2){
		new $.flavr({
		    animateEntrance : "flipInX",
		    animateClosing  : "flipOutX",
		    content         : "按月查询暂未开通！",
		    buttons     : {
		    	info  : { text: "知道了", style: "info"}
		    }
		});
	}else if($("#timeType").val() == 3){
		new $.flavr({
		    animateEntrance : "flipInX",
		    animateClosing  : "flipOutX",
		    content         : "按季度查询暂未开通！",
		    buttons     : {
		    	info  : { text: "知道了", style: "info"}
		    }
		});
	}else{
		$("#show").empty();
		if(type == 0){
			pageTypeIndex="reportPage";
			$("#screen_box").empty().append('<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6"><a class="search-btn" onclick="resetbtn()" style="margin-left: 5px;">重置</a></div><div class="col-xs-6 col-sm-6 col-md-6 col-lg-6"><a id="reqr" class="search-btn" onclick="serachbtn()" style="margin-left: 5px;">确认</a></div>');
			$("#show").load('Report.jsp');
			setTimeout("serachbtn()",1500);
		}else if(type == 1){//显示数据
			pageTypeIndex = "dataGridPage";
			$("#screen_box").empty().append('<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6"><a class="search-btn" onclick="resetbtn()" style="margin-left: 5px;">重置</a></div><div class="col-xs-6 col-sm-6 col-md-6 col-lg-6"><a id="lbqr" class="search-btn" onclick="seachGrid()" style="margin-left: 5px;">确认</a></div>');
			$("#show").load('DataGrid.jsp');
			setTimeout("seachGrid()",200);
		}else if(type == 2){
			pageTypeIndex = "dataGridPage";
			serach();
		}
	}
	isBtnDis(1);
}


$(document).ready(function(){
	$("#tabYear").click(function(){
		$("#timeType").val(1);
		$("#monthInputb").val("");
		$("#quarterInput").val("");
		$("#quarterSelect").prev().children().eq(0).children().eq(0).children().click();
	});
	
	$("#tabMonth").click(function(){
		$("#timeType").val(2);
		$("#yearInputb").val("");
		$("#quarterInput").val("");
		$("#quarterSelect").prev().children().eq(0).children().eq(0).children().click();
	});
	
	$("#tabQuarter").click(function(){
		$("#timeType").val(3);
		$("#monthInputb").val("");
		$("#yearInputb").val("");
	});
	leftData();
});

function serachbtn(){
	isBtnDis(0);
	serach();
}

function resetbtn(){
	leftData();
}