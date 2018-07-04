$("#gridModule").kendoGrid({
	dataSource : {
		transport : {
			read : {
					type : "post",
					url : "../module/queryModuleAll",
					dataType : "json",
					contentType : "application/json"
			},
			update: {
	                url: "../module/saveOrUpdate",
	                contentType : "application/json",
	                dataType: "json",
	                type : "post"
            },
            destroy: {
	                url: '../module/deleteById',
	                type : "post"
            },
            create: {
	                url: "../module/saveOrUpdate",
	                dataType: "json",
	                contentType : "application/json",
	                type : "post"
            },
			parameterMap : function(options, operation) {
					if (operation == "read") {
						return JSON.stringify(options);
					}else if(operation == "update"){
						return JSON.stringify(options);
					}else if(operation == "create"){
						return JSON.stringify(options);
					}else if(operation == "destroy"){
						console.log(options);
						return "id="+options.mid;
					}
			}
		},
		pageSize : 10,
		schema : {
			total : "totalRecord",
			model : {
				id : "mid",
				fields : {
					mname : {
						type : "string",
						validation: { required: true,validationMessage : "模块名不能为空并保证唯一。"  }
					},mtype: {
						type : "string",
						validation: { required: true,validationMessage : "模块类型不能为空。"  }
					},indexId : {
						type : "number",
						validation: { required: true, min: 0 ,validationMessage : "排序编号不能为空。" }
					},did : {
						type : "string",
						validation: { required: true,validationMessage : "所属数据库不能为空。"  } ,
					},openStatus : {
						type : "string",
					}
				}
			},
			data : "results"
		},
		serverPaging : true,
		serverFiltering: true,
		requestEnd : function(d){
			if(d.type == "create" || d.type == "update"){
				if(d.response == 6){
					alert("模块名已经存在");
				}else{
					$("#gridModule").data('kendoGrid').dataSource.read();
				}
			}else if(d.type == "destroy"){
				if(d.response > 0){
					alert("删除成功！");
				}else{
					alert("删除失败！");
				}
			}
		}
	},
	pageable : true,
	pageable : {
		refresh : true,
		pageSizes : true,
		buttonCount : 5,
		pageSizes : [ 5, 10, 20 ],
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
	height : 460,
	filterable : {
		mode : "row"
	},
	selectable : "multiple cell",
	sortable : false,
	reorderable : false,
	columnMenu : false,
	filterable : false,
	noRecords : {
		template : "暂无数据！"
	},
	columns: [{
        field: "mid",
        title: "批量操作",
        template: "<input type='checkbox' id='batch' value='#: mid #' />",
        width: 100
    }/*,{
         field: "rowNumber",
         title: "序号",
         template : "<span class='row-number'></span>",
         editable : false
     }*/, {
         field: "mname",
         title: "模块名"
     }, {
         field: "mtype",
         title: "模块类型",
         values : [{"value": 0, "text": "搜索框" },{"value": 1,"text": "按钮"}]
     }, {
         field: "did",
         title: "所属数据库",
         values : [{"value": 1, "text": "医院药品销售" },{"value": 2,"text": "中国社区医院基础信息库"}]
     },{
         field: "openStatus",
         title: "是否启用此模块",
         values : [{"value": 1, "text": "启用此功能" },{"value": 2,"text": "暂不使用此功能"}]
     }, {
         field: "indexId",
         title: "排序编号",
         width:75
     },  {
         field: "remark",
         title: "备注",
         width:75
     },{
 	     command : [{ name: "edit", text: { edit: "编辑", cancel: "取消", update: "更新" } }, { name: "destroy",text: "删除" }], 
         title : "操作"
     }],
    editable: {
    	mode: "popup",
    	confirmation : "您确定要进行删除操作吗？",
    	window: {
    		title: "模块管理"
    	}
    },
    toolbar: [{ name: "create", text: "新增模块" }, {  template: kendo.template($("#template").html())}],
    edit: function(e) {
        if (!e.model.isNew()) {
        	e.container.find("input[name=mname]")[0].disabled = true;
        }
     }/*,
	dataBound : function() {
		var rows = this.items();
		var page = this.pager.page() - 1;
		var pagesize = this.pager.pageSize();
		$(rows).each(function() {
			var index = $(this).index() + 1 + page * pagesize;
			var rowLabel = $(this).find(".row-number");
			$(rowLabel).html(index);
		});
	}*/
});

function deleteByIds(){
	var check = $("tbody[role='rowgroup']").find("input[id='batch']:checked");
	if(check.length > 0){
		var array = new Array();
		$(check).each(function(){
			array.push($(this).val());
		});
		$.ajax({
		   type : "post",
		   url : "../module/deleteByIds",
		   dataType:"json",
		   data : {"ids" : array},
		   success : function(msg){
			   alert("删除成功！");
			  $("#gridModule").data('kendoGrid').dataSource.read();
		   },
		   error : function(e){
			   alert("批量删除失败，请重试！");
		   }
		});
	}else{
		alert("请选择要删除的数据！");
		return false;
	}
}

$(function(){
	$(".module-manage").addClass("active");
})
