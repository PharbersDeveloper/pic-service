/**
 * 根据条件导出数据
 */
function reportExcelDataGrid(){
	dialog("lightSpeedIn","lightSpeedOut","该功能暂未对测试用户开放，敬请期待！",'info',"知道了");
	/*$(".excelReportLoad").attr("class","excelReportLoad excelDataLoad");
	var excelCurrentFilters = [];
	$.each($(".box-wrap select :selected"),function(i,v){
		if(v.value != null && v.value != ""){
			var field = $(this).parent().attr("name");
			var value = $(this).val();
			excelCurrentFilters.push({
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
			excelCurrentFilters.push({
				field:"year",
				operator:"eq",
				value:time
			});
		}
	}
	var Filter = {
		logic: "and",
        filters: excelCurrentFilters
	};
	
	$.ajax({
         type : 'POST', 
         url  : '../listData/checkData',
         contentType : "application/json;charset=UTF-8",
         data : JSON.stringify({filter:Filter}),
         success : function(data) {
             //location.href="../listData/download?name="+data
             download(data);
             $(".excelReportLoad").attr("class","excelReportLoad loadNone");
         },
         error:function(){
        	 $(".excelReportLoad").attr("class","excelReportLoad loadNone");
             alert("错误。。");   
         }
     });*/
//	location.href="../";
}

//下载
function download(name){
	var form=$("<form>");//定义一个form表单
	form.attr("style","display:none");
	form.attr("target","");
	form.attr("method","post");
	form.attr("action","../listData/download");
	var input1=$("<input>");
	input1.attr("type","hidden");
	input1.attr("name","name");
	input1.attr("value",name);
	$("body").append(form);//将表单放置在web中
	form.append(input1);
	form.submit();//表单提交 
}

var dataSource = new kendo.data.DataSource({
	transport : {
		read : {
			type : "post",
			url : "../listData/query",
			dataType : "json",
			contentType : "application/json"
		},
		parameterMap : function(options, operation) {
			if (operation == "read") {
				return JSON.stringify(options);
			}
		}
	},
	pageSize : 20,
	schema : {
		total : "totalRecord",
		model : {
			//id : "salesid",
			fields : {
				/* salesid : {type: "number"}, */
				atc1Name : {
					type : "string"
				},
				atc2Name : {
					type : "string"
				},
				atc3Name : {
					type : "string"
				},
				dosageformName : {
					type : "string"
				},
				genericnameName : {
					type : "string"
				},
				manuName : {
					type : "string"
				},
				manuTypeName : {
					type : "string"
				},
				minimumName : {
					type : "string"
				},
				packagingName : {
					type : "string"
				},
				productName : {
					type : "string"
				},
				provinceName : {
					type : "string"
				},
				salesDate : {
					type : "string"
				},
				salesValue : {
					type : "number"
				},
				unit : {
					type : "number"
				},
				specificationsName : {
					type : "string"
				}
			}
		},
		data : "results",
	// groups : "results"
	/*
	 * data: function (d) { //console.info(d); return
	 * d.results; }, total: function (d) {
	 * //console.info(d); return d.totalRecord; }
	 */
	},
	serverPaging : true,
	serverFiltering : true
// serverGrouping: true
});

var data = [
            { text: "年份汇总", value: "年份汇总" },
            { text: "月份汇总", value: "月份汇总" }/*,
            { text: "季度汇总", value: "季度汇总" }*/
        ];

function cityFilter(element) {
    element.kendoDropDownList({
        dataSource: data,
        dataTextField: "text",
        dataValueField: "value",
        valuePrimitive: true,
        optionLabel: "--请选择--"
    });
}

/**
 * 加载KendoUI的Grid组件
 */
$("#grid").kendoGrid({
	dataSource : dataSource,
	pageable : true,
	pageable : {
		refresh : true,
		pageSizes : true,
		buttonCount : 5,
		pageSizes : [ 10, 15, 20, 25, 30, 35, 40, 45, 50, 200 ],
		messages : {
			display : "显示{0}-{1}条，共{2}条",
			empty : "没有数据",
			page : "页",
			of : "/ {0}",
			itemsPerPage : "条/页",
			first : "第一页",
			previous : "前一页",
			next : "下一页",
			last : "最后一页",
			refresh : "刷新"
		}
	},
	height : 590,
	selectable : "multiple cell",
	sortable : false,
	resizable : true,
	scrollable : true,
	reorderable : true,
	filterable: true,
	columnMenu : true,
	filterable : {
		extra : false,// 是否双过滤条件，默认为true
		messages : {
			info : "筛选器: ",// 顶端提示信息
			and : "并且",
			filter : "筛选",// 过滤按钮
			clear : "清除",// 清空按钮
			isTrue : "是",
			isFalse : "否",
			navigatable : false,
			reorderable : true,
			mode : "menu"
		},
		operators : {
			string : {
				eq : "等于",
				neq : "不等于",
				contains : "包含……"
			},
			number : {
				eq : "等于",
				neq: "不等于",
				gte : "大于等于",
				gt : "大于",
				lte : "小于等于",
				lt : "小于"
			}
		}
	},
	noRecords : {
		template : "暂无数据！"
	},
	columns : [/*
				 * { field: "salesid", title: "主键", width: 80,
				 * hidden:true, filterable:false },
				 */
			{
				field : "rowNumber",
				title : "序号",
				template : "<span class='row-number'></span>",
				width : 80,
				filterable : false,
				sortable : false
			},
			{
				field : "salesDate",
				title : "时间",
				filterable: {
					operators : {
						string : {
							eq : "等于",
						},
					},
					ui: cityFilter
				},
				width : 80
			/*
			 * attributes:{ style:"background-color: red;" }
			 */
			},
			{
				field : "provinceName",
				title : "省份",
				filterable : false,
				width : 80
			},
			{
				field : "productName",
				title : "商品名",
				filterable : false,
				width : 120
			},
			{
				field : "salesValue",
				title : "销售额(元)",
				filterable : false,
				width : 150,
				template : function(dataItem) {
					if (dataItem.salesValue == 0) {
						return "<div style='color: red;font-weight: bold;'>"
								+ dataItem.salesValue
								+ "</div>";
					} else {
						return "<div>" + accounting.formatNumber(dataItem.salesValue)
								+ "</div>";
					}
				}
			},
			{
				field : "unit",
				title : "销量（盒）",
				filterable : false,
				width : 150,
				template : function(dataItem) {
					if (dataItem.unit == 0) {
						return "<div style='color: red;font-weight: bold;'>"
								+ dataItem.unit + "</div>";
					} else {
						return "<div>" + accounting.formatNumber(dataItem.unit)
								+ "</div>";
					}
				}
			},
			{
				field : "genericnameName",
				title : "通用名",
				filterable : false,
				width : 140
			},
			{
				field : "manuName",
				title : "厂家",
				filterable : false,
				width : 400
			},
			{
				field : "specificationsName",
				title : "规格",
				filterable : false,
				width : 90
			},
			{
				field : "atc1Name",
				title : "治疗Ⅰ类",
				filterable : false,
				width : 150
			}, {
				field : "atc2Name",
				title : "治疗Ⅱ类",
				filterable : false,
				width : 100
			}, {
				field : "atc3Name",
				title : "治疗III类",
				filterable : false,
				width : 370
			}, {
				field : "minimumName",
				title : "最小产品单位",
				filterable : false,
				width : 600
			}, {
				field : "manuTypeName",
				title : "厂家类型",
				filterable : false,
				width : 100
			}, {
				field : "dosageformName",
				title : "剂型",
				filterable : false,
				width : 200
			}, {
				field : "packagingName",
				title : "包装",
				filterable : false,
				width : 80
			} ],
	dataBound : function() {
		var rows = this.items();
		var page = this.pager.page() - 1;
		var pagesize = this.pager.pageSize();
		$(rows).each(function() {
			var index = $(this).index() + 1 + page * pagesize;
			var rowLabel = $(this).find(".row-number");
			$(rowLabel).html(index);
		});
	}
});