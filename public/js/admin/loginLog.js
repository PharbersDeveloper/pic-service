$(function(){
	$(".login-manage").addClass("active");
	
	log();
});

function log(){
	/*$('#grid').DataTable({
      "language": {"url": "//cdn.datatables.net/plug-ins/1.10.12/i18n/Chinese.json"},
      "paging": true,
      "lengthChange": true,
      "aLengthMenu": [[10, 25, 50], [10, 25, 50]],
      "searching": true,
      "ordering": false,
      "info": false,
      "autoWidth": true
	});*/
	$("#grid").kendoGrid({
		dataSource : {
			transport : {
				read : {
						type : "post",
						url : "../loginLog/query",
						dataType : "json",
						contentType : "application/json"
				},
				parameterMap : function(options, operation) {
						if (operation == "read") {
							return JSON.stringify(options);
						}
				}
			},
			pageSize : 10,
			schema : {
				total : "totalRecord",
				model : {
					fields : {
						user_name : {
							type : "string"
						},/*date: {
							type : "date"
						},login_end: {
							type : "date"
						},time_sum:{
							type : "number"
						},*/ip:{
							type : "string"
						}
					}
				},
				data : "results"
			},
			serverPaging : true,
			serverFiltering: true
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
		selectable : "multiple cell",
		sortable : false,
		reorderable : false,
		columnMenu : false,
		filterable : false,
		noRecords : {
			template : "暂无数据！"
		},
		columns: [{
	         field: "user_name",
	         title: "用户名",
	         width: '20%'
	     },{
	         field: "date",
	         title: "登入时间",
	         format: "{0: yyyy-MM-dd HH:mm:ss}", //格式化时间  
	         width: '20%'
	     },{
	         field: "login_end",
	         title: "登出时间",
	         format: "{0: yyyy-MM-dd HH:mm:ss}", //格式化时间
	         width: '20%'
	     },{
	         field: "time_sum",
	         title: "停留时长",
	         width: '20%'
	     },{
	         field: "ip",
	         title: "登录IP",
	     	 width: '20%'
	     }],
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
}
