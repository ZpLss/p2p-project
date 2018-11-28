//验证手机号
function checkPhone() {
	//手机号不能为空
    //手机号格式
    //手机号不能重复

	//获取用户输入的手机号
	var phone = $.trim($("#phone").val());
	var flag = true;
	if("" == phone){
		showError("phone","请输入手机号");
		return false;
	}else if (!/^1[1-9]\d{9}$/.test(phone)){
		showError("phone","请输入正确的手机号码");
		return false;
	}else{
		//发送ajax请求验证
		$.ajax({
			url:"loan/checkPhone",
			type:"post",//一般向服务器获取数据用get,传输数据用post
			data:"phone="+phone,
            async:false,//关闭异步,开启同步
			success:function (data) {
				if (data.errorMessage == "ok"){
                    //ok 手机号在系统中不存在
					showSuccess("phone");
					flag = true;
				}else {
					//no 手机号已存在
					showError("phone",data.errorMessage);
					flag = false;
				}
            },
			error:function () {
				showError("phone","系统繁忙,请稍侯...");
            }
		})
	}
	if(!flag){
		return false;
	}
	return true;

}

//验证登陆密码
function checkLoginPassword() {
	//密码不能为空
	//密码符合格式
	//两次输入密码必须一致

	//获取用户输入的登陆密码
	var loginPassword = $.trim($("#loginPassword").val());
	var replayLoginPassword = $.trim($("#replayLoginPassword").val());

	if ("" == loginPassword){
		showError("loginPassword","请输入登陆密码");
		return false;
	}else if (!/^[0-9a-zA-Z]+$/.test(loginPassword)){
		showError("loginPassword","密码字符只可使用数字和大小写英文字母");
		return false;
	}else if (!/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*/.test(loginPassword)){
		showError("loginPassword","密码应同时包含英文和数字");
		return false;
	}else if (loginPassword.length < 6 || loginPassword.length > 16){
		showError("loginPassword","密码长度应为6~16位")
		return false;
	}else {
		showSuccess("loginPassword");
	}

	if("" == replayLoginPassword){
		showError("replayLoginPassword","请再次输入密码");
		return false;
	}else if (replayLoginPassword != loginPassword){
		showError("replayLoginPassword","两次输入的密码不一致")
		return false;
	}else {
		showSuccess("replayLoginPassword");
	}
	return true;
}

//验证确认登陆密码
function checkReplayLoginPassword() {
	//获取用户输入的登陆密码
	var loginPassword = $.trim($("#loginPassword").val());
	var replayLoginPassword = $.trim($("#replayLoginPassword").val());
	if (!checkLoginPassword()){
		hideError("replayLoginPassword")
	}

	if ("" == loginPassword){
		showError("loginPassword","请输入登陆密码");
		return false;
	}else if ("" == replayLoginPassword){
		showError("replayLoginPassword","请再次输入确认密码");
		return false;
	}else if (loginPassword != replayLoginPassword){
		showError("replayLoginPassword","两次输入的密码不一致");
		return false;
	}else {
		showSuccess("replayLoginPassword");
	}
	return true;

}

//验证图形验证码
function checkCaptcha() {
	//验证图形验证码不能为空
	//验证是否一致

	//获取图形验证码
	var captcha = $.trim($("#captcha").val());
	var flag = true;
	if ("" == captcha){
		showError("captcha","请输入图形验证码");
		return false;
	}else {
		//发送ajax请求
		$.ajax({
			url:'loan/checkCaptcha',
			type:'post',
			data:'captcha=' + captcha,
            async:false,
			success:function (data) {
				if (data.errorMessage == "ok" ){
					flag = true;
					showSuccess("captcha");
				}else {
					flag = false;
					showError("captcha",data.errorMessage);
				}
            },
			error:function () {
				flag = false;
				showError("captcha","系统繁忙,请稍后再试...");
            }
		})

	}
	if(!flag){
		return false;
	}
	return true;
}

//用户注册
function register() {
    /*alert(checkPhone());
    alert(checkLoginPassword());
    alert(checkReplayLoginPassword());
    alert(checkCaptcha());*/
    //获取用户输入的表单信息
    var phone = $.trim($("#phone").val());
    var loginPassword = $.trim($("#loginPassword").val());
    var replayLoginPassword = $.trim($("#replayLoginPassword").val());

    if (checkPhone() && checkLoginPassword() && checkReplayLoginPassword() && checkCaptcha()){
        $("#loginPassword").val($.md5(loginPassword));
        $("#replayLoginPassword").val($.md5(replayLoginPassword));

        $.ajax({
            url:"loan/register",
            type:"post",
            data:{
                "phone":phone,
                "loginPassword":$.md5(loginPassword),
                "replayLoginPassword":$.md5(replayLoginPassword)
            },
            success:function (data) {
                if (data.errorMessage == "ok"){
                    //注册成功
                    window.location.href="realName.jsp";
                }else {
                    //注册失败
                    showError("captcha",data.errorMessage);
                }
            },
            error:function () {
                showError("captcha","系统繁忙,请稍后再试...");
            }


        })


    }

}


//错误提示
function showError(id,msg) {
	$("#"+id+"Ok").hide();
	$("#"+id+"Err").html("<i></i><p>"+msg+"</p>");
	$("#"+id+"Err").show();
	$("#"+id).addClass("input-red");
}
//错误隐藏
function hideError(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id).removeClass("input-red");
}
//显示成功
function showSuccess(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id+"Ok").show();
	$("#"+id).removeClass("input-red");
}

//注册协议确认
$(function() {
	$("#agree").click(function(){
		var ischeck = document.getElementById("agree").checked;
		if (ischeck) {
			$("#btnRegist").attr("disabled", false);
			$("#btnRegist").removeClass("fail");
		} else {
			$("#btnRegist").attr("disabled","disabled");
			$("#btnRegist").addClass("fail");
		}
	});
});

//打开注册协议弹层
function alertBox(maskid,bosid){
	$("#"+maskid).show();
	$("#"+bosid).show();
}
//关闭注册协议弹层
function closeBox(maskid,bosid){
	$("#"+maskid).hide();
	$("#"+bosid).hide();
}