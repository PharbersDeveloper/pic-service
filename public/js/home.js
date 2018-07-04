var state = false;//用于记录搜索关键词的时候Ajax请求时候是否查询返回结束 false 为可以继续发送请求
var time = null;
/*var time2 = null;
var time3 = null;
var time4 = null;
var time5 = null;*/
$(function () {
	fanzhun3d();
	
	$("#search").keydown(function(e){
		var str = $(this).val();
		if(e.keyCode == 13){
			if(time){
				clearInterval(time);
			}
			if(state == false){
				if(str != ""){
					state = true;
					$("#loding").css("display","block");
					$.post("../queryByKeyWord/queryByKey",{"key":str},function(data){
						if(data != null && data != "" ){
							state = false;
							$("#loding").css("display","none");
							$.each(data,function(i,v){
								if(v  = "医院药品销售"){
									msg("ypxs");
									time = setInterval(function (){ msg("ypxs");}, 2000)
								}
							});
						}else{
							setTimeout(function(){$("#loding").css("display","none");},2000);
							$.dialog.tips('根据关键词并没有查到相关数据库！',3,'');
							state = false;
						}
					});
				}
			}
		}else{
			if(str != ""){
				clearInterval(time);
				setTimeout(function(){$("#loding").css("display","none");},2000);
				state = false;
			}
		}
	});
	/*setInterval(function(){
		$("#ypxs").fadeOut(1500).fadeIn(1000);
	},1000);*/
	 
	 $('[data-toggle="tooltip"]').tooltip();
});

/*function createIndex(){
	$.post("../queryByKeyWord/lucene",{},function(data){})
}*/

/**
 * 搜索后选中跳动效果
 */
function msg(id){
	var ys = $("#"+id)
	if(!($(ys).is(":animated"))){
	$(ys).animate({"top":"-3px",},100).animate({"top":"3px",},100)
	.animate({"top":"-3px",},100).animate({"top":"3px",},100)
	.animate({"top":"-1px",},100).animate({"top":"1px",},100)
	}
}


/**
 * 3D翻转特效 部分浏览器不支持CSS3和H5的可能会出现问题
 */
function fanzhun3d(){
	if ( $('html').hasClass('csstransforms3d') ) {
		$('.artGroup').removeClass('slide').addClass('flip');
		$('.artGroup.flip').on('mouseenter',
			function () {
				$(this).find('.artwork').addClass('theFlip');
			});
		$('.artGroup.flip').on('mouseleave',
			function () {
				$(this).find('.artwork').removeClass('theFlip');
			});
	} else {
		$('.artGroup').on('mouseenter',
			function () {
				$(this).find('.detail').stop().animate({bottom:0}, 500, 'easeOutCubic');
			});
		$('.artGroup').on('mouseleave',
			function () {
				$(this).find('.detail').stop().animate({bottom: ($(this).height() + -1) }, 500, 'easeOutCubic');
			});
	}
}