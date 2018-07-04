/**
 * 目前后端返回错误，暂时分页的初始化先这么写
 * @param data
 * @constructor
 */
function Page(data) {
	var page = null;
	if(data != null) {
		page = data.page[0];
	} else {
		page = eval({"page":[{"TOTLE_PAGE":0,"SERIAL":0,"ROW_START":0,"PAGE_CURRE":0,"TOTLE_RECORD":0,"ROW_END":0}]}).page[0]
	}
	if(data.search_result.length==0){
        $(".dataTable").hide();
        $(".img").show();
		alert("没有搜索数据")
	}else {
        $('div[id="pageinfo"]').html("显示第 " + page.ROW_START + " 至 " + page.ROW_END + " 条记录，共 " + page.TOTLE_RECORD + " 条记录");
        var element = $('#pageview');
        options = {
            size: "small",
            bootstrapMajorVersion: 3,
            currentPage: page.PAGE_CURRE,
            numberOfPages: 5,
            totalPages: page.TOTLE_PAGE
        };
        element.bootstrapPaginator(options);
    }
}
