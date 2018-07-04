var userName;
var userId;

$("#grid").kendoGrid({
	dataSource : {
		transport : {
			read : {
					type : "post",
					url : "../userInfo/queryUserAll",
					dataType : "json",
					contentType : "application/json"
			},
			update: {
	                url: "../userInfo/saveOrUpdate",
	                contentType : "application/json",
	                dataType: "json",
	                type : "post"
            },
            destroy: {
	                url: "../userInfo/deleteById",
                	dataType: "json",
                	contentType : "application/json",
	                type : "post"
            },
            create: {
	                url: "../auth/pushUser ",
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
                        // console.log("destroy->"+JSON.stringify(options)+"<-");
						var user=new Object();
                        user.user_id=options.user_id;
                        user.pwd="";
						return JSON.stringify(user);

                        // return "id:"+options.id;
                    }
			}
		},
		pageSize : 10,
		schema : {
			total : "totalRecord",
			model : {
				id : "user_id",
				fields : {
					user_name : {
						type : "string",
						validation: { required: true,validationMessage : "用户名不能为空并保证唯一。"  }
					},pwd : {
                        type : "string",
                        validation: { required: true,validationMessage : "密码不能为空！"  }
                    },status: {
						type : "number",
						validation: { required: true,validationMessage : "用户状态不能为空。"  }
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
					alert("用户名已存在，请尝试其他用户名！");
					return 0;
				}else{
					$("#grid").data('kendoGrid').dataSource.read();
				}
			}else if(d.type == "destroy"){
				if(d.response != ""){
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
        field: "user_id",
        title: "#",
        template: "<div style=\"text-align:center\"><input type='checkbox' id='batch' value='#: id #' /></div>",
        width: '5%'
    }/*,{
         field: "rowNumber",
         title: "序号",
         template : "<span class='row-number'></span>",
         editable : false
     }*/, {
         field: "user_name",
         title: "用户名",
         width: '15%'
     }, {
         field: "pwd",
         title: "密码",
     	 width: 1
     }, {
        field: "status",
        title: "用户状态",
        values : [{"value": 1, "text": "正常开通" },{"value": 0,"text": "暂不开通"}],
        width: '10%'
    }, {
         field: "date",
         title: "创建时间",
         format: "{0: yyyy-MM-dd HH:mm:ss}", //格式化时间  
         width: '20%'
     }, {
         field: "updateDate",
         title: "修改时间",
         format: "{0: yyyy-MM-dd HH:mm:ss}", //格式化时间
         width: '20%'
     },{
 	     command : [/*{ text: "View Details", click: showDetails }, */{ name: "edit", text: { edit: "编辑", cancel: "取消", update: "更新" } }, { name: "destroy",text: "删除" }, {  template: kendo.template($("#templateRole").html())}], 
         title : "操作",
		 width: '30%'
     }],
    editable: {
    	mode: "popup",
    	confirmation : "您确定要进行删除操作吗？",
    	window: {
    		title: "修改人员信息"
    	}
    },
    toolbar: [{ name: "create", text: "新增用户" }, {  template: kendo.template($("#template").html())}],
    edit: function(e) {
        if (!e.model.isNew()) {
        	e.container.find("input[name=user_name]")[0].disabled = true;
        }
        $("label[for='date']").remove();
    	$("div[data-container-for='date']").remove();
    	$("label[for='updateDate']").remove();
    	$("div[data-container-for='updateDate']").remove();
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


/**
 * 自定义编辑窗口 开始
 */
var wnd,detailsTemplate;
wnd = $("#details").kendoWindow({
    title: "Customer Details",
    modal: true,
    visible: false,
    resizable: false,
    width: 400,
    height: 300
}).data("kendoWindow");

detailsTemplate = kendo.template($("#editTemplate").html());

function showDetails(e) {
    e.preventDefault();
    var dataItem = this.dataItem($(e.currentTarget).closest("tr"));
    wnd.content(detailsTemplate(dataItem));
    wnd.center().open();
}
//自定义编辑窗口结束

function deleteByIds(){
	var check = $("tbody[role='rowgroup']").find("input[id='batch']:checked");
	if(check.length > 0){
		var array = new Array();
		$(check).each(function(){
			array.push($(this).val());
		});
		$.ajax({
		   type : "post",
		   url : "../userInfo/deleteByIds",
		   dataType:"json",
		   data : {"ids" : array},
		   success : function(msg){
			   alert("删除成功！");
			  $("#grid").data('kendoGrid').dataSource.read();
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

function userSetRoles(obj){
	var id = new Object();
	id.user_id = $(obj).parent().parent().find("input[id='batch']").val();
	userName = $(obj).parent().parent()[0].cells[1].innerText;
	$.ajax({
	   type : "post",
	   url : "../module/queryAuthTree",
	   dataType:"json",
	   contentType : "application/json",
	   data : JSON.stringify(id),
	   success : function(msg){
		   $("#treeView").kendoTreeView({
		        checkboxes: {
		            checkChildren: true
		        },
		        dataSource: eval("("+msg.result+")")
		    });

		   $("#treeView").kendoWindow({
		        width: "600px",
		        height : "600px",
		        title: "为用户【"+userName+"】分配权限",
		        visible: false,
		        actions: [
		            "Pin",
		            "Minimize",
		            "Maximize",
		            "Close"
		        ],
		        close: function (e) {
		        	$("#treeView").remove();
		        	$("#grid").after("<div id='treeView'></div>");
		        }
		    }).data("kendoWindow").center().open();

		   userId=$(obj).parent().parent().find("input[id='batch']").val();
		   var html = "<div style='position: absolute;bottom: 35px;right: 39%;text-align: center;'><input type='button' style='margin:20px;'value='确定' onclick='saveUserAuth("+"userId"+");'><input type='button' value='取消' onclick='cancelUserAuth();'></div>"
		   $("#treeView").after(html);
	   },
	   error : function(e){
		   alert("加载权限列表失败，请重试！"+userName);
	   }
	});
}

function saveUserAuth(userId){
	var checkedNodes = new Array();
    var treeView = $("#treeView").data("kendoTreeView"),
    checkedNodes = checkedNodeIds(treeView.dataSource.view(), checkedNodes);
	var message;
	if (checkedNodes.length > 0) {
	    checkedNodes.join(",");
	} else{
		checkedNodes = [""];
	}
	// console.log("checkedNodes="+checkedNodes);
	var save_user_auth= new Object();
    save_user_auth.user_id=userId;
    save_user_auth.scope=checkedNodes;
    // console.log(save_user_auth);
	$.ajax({
	   type : "post",
	   url : "../module/saveUserAuth",
	   dataType:"json",
	   contentType : "application/json",
	   data : JSON.stringify(save_user_auth),
	   success : function(data){

           $("#grid").data('kendoGrid').dataSource.read();
		   alert("用户【"+userName+"】的权限已分配成功！");
		   cancelUserAuth();
	   },
	   error : function(e){
		   alert("用户【"+userName+"】分配权限失败，请重试！");
	   }
	});
}

function checkedNodeIds(nodes, checkedNodes) {
    for (var i = 0; i < nodes.length; i++) {
        if (nodes[i].checked && nodes[i].hasChildren == false && nodes[i].text!="显示样本报告") {
            if(nodes[i].id < 30){
                checkedNodes.push("c-"+nodes[i].text);
            }else{
                checkedNodes.push("e-"+nodes[i].text);
            }
        }
        if (nodes[i].hasChildren) {
            checkedNodeIds(nodes[i].children.view(), checkedNodes);
        }
    }
    var beforeLen=nodes.length -1;
    if(nodes[beforeLen].text=="显示样本报告"){
        if(nodes[beforeLen].checked){
            checkedNodes.push("s-" + "1");
        }else {
            checkedNodes.push("s-" + "0");
        }
    }
    return checkedNodes;
}

function cancelUserAuth(){
	$("#treeView").data("kendoWindow").close();
	$("#treeView").remove();
	$("#grid").after("<div id='treeView'></div>");
}

$(function(){
	$(".user-manage").addClass("active");
})
