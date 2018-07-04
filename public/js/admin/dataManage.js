$(function(){
	$(".data-manage").addClass("active");
	$("#index").click(function(){
		$("#loads").attr("class","load");
		$.post("../medicalForDataInfo/add",{},function(data){
			if(data == 1){
				alert("创建成功");
				$("#loads").attr("class","load noload");
			}else{
				alert("创建失败");
				$("#loads").attr("class","load noload");
			}
		});
	});
	
	$("#query").click(function(){
		var strWord = $("#str").val();
		$.post("../medicalForDataInfo/searchKeyWords",{"strWord":strWord},function(data){
			console.info(data);
		});
	});
})
