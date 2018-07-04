var iphonezz = /^1[3|4|5|7|8][0-9]\d{8}$/;
var keysflag = false;

function click() {
	if (event.button == 2)      //单击的鼠标键为右键
	{
		alert("请尊重本网站版权！");
		return false;
	}
}

function ctrl_key() {
	if((window.event.ctrlKey) && (window.event.keyCode == 67)){
		alert("禁用CTRL+C 复制网页内容，请尊重本网站版权！");
		return false;
	}else if((window.event.ctrlKey) && (window.event.keyCode == 83)){
		alert("禁用CTRL+S 复制网页内容，请尊重本网站版权！");
		return false;
	}/*else if(event.keyCode == 123){
		alert("请尊重本网站版权！");
		return false;
	}*/
}

$(function () {
	var indextemp = 0; 
	$.ajaxSetup({
		dataFilter:function(response){ 
            if(response.indexOf("登录页面") > -1){
                //如果返回的文本包含"登陆页面"，就跳转到登陆页面  
            	if(indextemp == 0){
            		alert("您的会话时间已过期，为了保护账户安全，请重新登录！");
            	}
            	indextemp = 1;
            	window.top.location.reload(true);
                //一定要返回一个字符串不能不返回或者不给返回值，否则会进入success方法  
                return "";  
            }else{  
                //如果没有超时直接返回  
                return response;  
            }  
        }  
	});
	document.onmousedown = click;  //绑定禁用鼠标右键事件
	document.onkeydown = ctrl_key; //绑定禁用键盘事件
    if($('#header')[0]){
        var setMenuHeight = function (){
            var mainheight = $(window).outerHeight(true);
            var sHeight = mainheight-$('#header').outerHeight(true)-$("#footer").outerHeight(true);
            $('#sidebar').height(sHeight);
            var iWidth = $(window).width();
            $('#main').width(iWidth - $('#sidebar').outerWidth(true)).height(sHeight);
        };

        setMenuHeight();
        $(window).resize(function (){
            setMenuHeight();
        });
    }
});

function iskickout(obj){
	if(obj != null && obj.kitout != undefined && obj.kitout != null && obj.kitout == "yes"){
		kickout();
	}
}

function kickout(){
	new $.flavr({
	    animateEntrance : "fadeInLeft",
	    animateClosing  : "bounceOutDown",
	    content         : "您的账号在另一台设备上登录，您被挤下线，若不是您本人操作，请立即修改密码！",
	    buttons     : {
	    	danger  : { text: "关闭", style: "danger"}
	    },
	    onClose     : function(){
	    	window.top.location.reload( true );  
	    }
	});
}

/**
 * flavr的弹框
 * @param msg
 * @returns
 */
function dialog(Entrance,Closing,msg,type,btnmsg){
	new $.flavr({
	    animateEntrance : Entrance,
	    animateClosing  : Closing,
	    content         : msg,
	    buttons     : {
	    	type : { text: btnmsg, style: type}
	    },
	    closeEsc     : true
	});
}

/**
 * 自动关闭的弹窗
 * @returns
 */
function autoCloseDialog(Entrance,Closing,msg,type,btnmsg){
	new $.flavr({
	    animateEntrance : Entrance,
	    animateClosing  : Closing,
	    content         : msg,
	    autoclose   : true,
	    buttons     : {
	    	type : { text: btnmsg, style: type}
	    },
	    timeout     : 5000,
	    onClose     : function(){
	    	returnLogin();
	    }
	});
}

//参数1 ： ID  参数2 ： 图片ID  参数3 ： 可表示长度、是否为NULL用于验证非空、可表示是否是正则表达式   参数4 ： 自定义提示内容  参数5 ： 如果为正则表达式的时候 可为 定义的 变量、自己写的正则表达式
function onblurVerification(inputId,imgId,obj,str,str2){
	var element = document.getElementById(inputId).value;
	if(obj == null){
		if(element=="" || element==-1){
			document.getElementById(imgId).innerHTML = "<img  style=\"vertical-align:text-bottom;\" title=\""+str+"不能为空！\" src=\"../assets/images/drop-no.gif\"  /><strong style=\"color:red;font-size: 14px;\" >"+str+"不能为空！</strong>";
			flag = true;
		}else{
			document.getElementById(imgId).innerHTML = "<img src=\"../assets/images/drop-yes.gif\" style=\"vertical-align:text-bottom;\">";
			flag = false;
		}
	}else if(obj == "zzbds"){
		if(inputId == "mobile"){
			if(!str2.test(element)){
				document.getElementById(imgId).innerHTML = "<img src=\"../assets/images/drop-no.gif\" style=\"vertical-align:text-bottom;\"><strong style=\"color:red;font-size: 14px;\">"+str+"有误！</strong>";
				flag = true;
			}else{
				verifyMobile();
			}
		}else{
			if(!str2.test(element)){
				document.getElementById(imgId).innerHTML = "<img src=\"../assets/images/drop-no.gif\" style=\"vertical-align:text-bottom;\"><strong style=\"color:red;font-size: 14px;\">"+str+"有误！</strong>";
				flag = true;
			}else{
				document.getElementById(imgId).innerHTML = "<img src=\"../assets/images/drop-yes.gif\" style=\"vertical-align:text-bottom;\">";
				flag = false;
			}
		}
	}else{
		if(inputId == "password"){
			if(element.split("").length<6){
				document.getElementById(imgId).innerHTML = "<img src=\"../assets/images/drop-no.gif\" style=\"vertical-align:text-bottom;\"><strong style=\"color:red;font-size: 14px;\">"+str+"不能小于"+obj+"位！</strong>";
				flag = true;
			}else{
				document.getElementById(imgId).innerHTML = "<img src=\"../assets/images/drop-yes.gif\" style=\"vertical-align:text-bottom;\">";
				flag = false;
			}
		}
	}
}

(function ($) {
	
})(jQuery);

function jQuery_isTagName(e, whitelists) {
	e = $.event.fix(e);
      var target = e.target || e.srcElement;
      if (whitelists && $.inArray(target.tagName.toString().toUpperCase(), whitelists) == -1) {
        return false;
      }
      return true;
}

/**
 * Echarts
 */
var itemStyle = {
	    normal:{
	        borderColor: 'rgba(0, 0, 0, 0.2)'
	    },
	    emphasis:{
	        shadowOffsetX: 0,
	        shadowOffsetY: 0,
	        shadowBlur: 20,
	        borderWidth: 0,
	        shadowColor: 'rgba(0, 0, 0, 0.5)'
	    }
}
var itemStyleHight = {
	normal: {
		color: 'rgba(194,75,81, 0.9)'//rgba(0, 0, 0, 0.5) #002060
	}
}
var itemStyleMiddle = {
	normal: {
		color: 'rgba(223,138,104, 0.9)'//rgba(0, 0, 0, 0.5) #0070C0
	}
}
var itemStyleEnd = {
	normal: {
		color: 'rgba(236,202,149, 0.9)'//rgba(0, 0, 0, 0.5) #919ACA
	}
}

window.onload = function(){
	 var time;
		$("#btnShow,#content").mouseover(function(){
			clearTimeout(time);
			$(this).attr({"aria-expanded":true});
			$("#btnGroup").attr({"class":$("#btnGroup").attr("class")+" open"})
		}).mouseout(function(){
			time = setTimeout(function(){
				$(this).attr({"aria-expanded":false});
				$("#btnGroup").attr({"class":"btn-group btn-group-lg"})
			},150);
		});
}


/**
 * 警告弹框
 * @param content 警告内容
 */
function alertDialog(content){
	$.dialog({ 
		icon: 'alert.gif',
		title:'警告',
	    content: content, 
	    width: "230px",
		height: "100px",
	    top: "50%", 
		max: false, 
	    min: false,
	    drag: false, //禁止拖动
	    resize: false, //禁止拖动
	    lock: "true", 
	    ok: function(){}
	});	
}

/**
 * 数组去重复
 * @param arr
 * @returns {Array}
 */
function uniqueArray(arr) {
    var result = [], hash = {};
    for (var i = 0, elem; (elem = arr[i]) != null; i++) {
        if (!hash[elem]) {
            result.push(elem);
            hash[elem] = true;
        }
    }
    return result;
//http://www.cnblogs.com/sosoft/
}

/**
 * 实现Map<String,Object>
 */
function commonMap() {
	var struct = function(key, value) {
		this.key = key;
		this.value = value;
	};
	// 添加map键值对 
	var put = function(key, value) {
		for ( var i = 0; i < this.arr.length; i++) {
			if (this.arr[i].key === key) {
				this.arr[i].value = value;
				return;
			}
		}
		;
		this.arr[this.arr.length] = new struct(key, value);
	};
	// 根据key获取value 
	var get = function(key) {
		for ( var i = 0; i < this.arr.length; i++) {
			if (this.arr[i].key === key) {
				return this.arr[i].value;
			}
		}
		return null;
	};
	// 根据key删除 
	var remove = function(key) {
		var v;
		for ( var i = 0; i < this.arr.length; i++) {
			v = this.arr.pop();
			if (v.key === key) {
				continue;
			}
			this.arr.unshift(v);
		}
	};
	// 获取map键值对个数 
	var size = function() {
		return this.arr.length;
	};
	// 判断map是否为空 
	var isEmpty = function() {
		return this.arr.length <= 0;
	};
	this.arr = new Array();
	this.get = get;
	this.put = put;
	this.remove = remove;
	this.size = size;
	this.isEmpty = isEmpty;
}

//创建一个StringBuffer类 ，此类有两个方法：一个是append方法一个是toString方法
function StringBuffer() {
  this.__strings__ = [];
};
StringBuffer.prototype.append = function(str) {
	this.__strings__.push(str);
};
StringBuffer.prototype.toString = function() {
	return this.__strings__.join('');
};
StringBuffer.prototype.cleans = function() {
	this.__strings__ = [];
};

/**
 * 冒泡排序 （普通） 从小到大
 * @param array
 * @returns
 */
function maopao(array){
	 var temp;
	 for(var i=0;i<array.length;i++){ //比较多少趟，从第一趟开始
	     for(var j=0;j<array.length-i-1;j++){ //每一趟比较多少次数
	         if(array[j]>array[j+1]){
	             temp=array[j];
	             array[j]=array[j+1];
	             array[j+1]=temp;
	         }
	     }
	}
	return array;
}