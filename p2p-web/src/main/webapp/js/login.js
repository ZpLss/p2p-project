var referrer = "";//登录后返回页面


referrer = document.referrer;
if (!referrer) {
	try {
		if (window.opener) {                
			// IE下如果跨域则抛出权限异常，Safari和Chrome下window.opener.location没有任何属性              
			referrer = window.opener.location.href;
		}  
	} catch (e) {
	}
}


//按键盘Enter键即可登录
$(document).keyup(function(event){
	if(event.keyCode == 13){
		login();
	}
});

//页面加载完成后调用
$(function () {
	load();
})

//登陆页信息
function load() {
	$.ajax({
		url:"loan/loadStat",
		type:"get",
		success:function (jsonObject) {
			$("#historyAverageRate").html(jsonObject.historyAverageRate);
			$("#allUserCount").html(jsonObject.allUserCount);
			$("#allBidMoney").html(jsonObject.allBidMoney);

        }
	})
}



//验证手机号
function checkPhone() {
	//获取用户手机号

	var phone = $.trim($("#phone").val());

	if ("" == phone){
		$("#showId").html("");
		$("#showId").html("请输入手机号");
		return false;
	}else if (!/^1[1-9]\d{9}$/.test(phone)){
        $("#showId").html("");
        $("#showId").html("请输入正确的手机号码");
        return false;
	}else {
        $("#showId").html("");
	}
	return true;
}

//验证密码
function checkLoginPassword() {
	//获取输入框密码
	var loginPassword = $.trim($("#loginPassword").val());

	if ("" == loginPassword){
        $("#showId").html("");
        $("#showId").html("请输入密码");
        return false;
	}else {
        $("#showId").html("");
	}
	return true;
}

//验证图形验证码
function checkCaptcha() {
	//获取图形验证码
	var captcha = $.trim($("#captcha").val());
	var flag = true;
	if ("" == captcha){
        $("#showId").html("");
        $("#showId").html("请输入验证码");
        return false;
	}else {
		//验证验证码
		$.ajax({
			url:"loan/checkCaptcha",
			data:"captcha="+captcha,
			type:"post",
			async:false,
			success:function (data) {
				if (data.errorMessage == "ok"){
                    $("#showId").html("");
                    flag = true;
				}else {
                    $("#showId").html("");
                    $("#showId").html(data.errorMessage);
                    flag = false;
				}
            }
		})
	}
	if(!flag){
		return false;
	}
	return true;
}

//登陆
function login() {
	//alert(checkCaptcha()&& checkLoginPassword() && checkPhone());
	//获取用户输入的表单信息
	var phone = $.trim($("#phone").val());
	var loginPassword = $.trim($("#loginPassword").val());

	if (checkPhone() && checkLoginPassword() && checkCaptcha()){
		$("#loginPassword").val($.md5(loginPassword));
		$.ajax({
			url:"loan/login",
			data:{
				"phone":phone,
				"loginPassword":$.trim($("#loginPassword").val())
			},
			type:"post",
			success:function (data) {
				if (data.errorMessage == "ok"){
					//成功,跳转到用户中心
					if("" == referrer){
						window.location.href = "index";
					}else {
						window.location.href = referrer;
					}
				}else {
					//失败,显示错误信息
					$("#showId").html("");
					$("#showId").html(data.errorMessage);
				}
            },
			error:function () {
				$("#showId").html("");
				$("#showId").html("系统繁忙,请稍后再试...")
            }
		})
	}
}




