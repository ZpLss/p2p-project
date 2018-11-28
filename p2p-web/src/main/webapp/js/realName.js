//验证真实名字格式
function checkRealName() {
	//真实姓名不能为空
	//只能是汉字
	//获取用户的姓名
	var realName = $("#realName").val();
	//验证
	if("" == realName){
		showError("realName","请输入真实姓名");
		return false;
	}else if (!/[\u4E00-\u9FA5\uF900-\uFA2D]/.test(realName)){
		showError("realName","真实姓名只支持中文");
		return false;
	}else {
		showSuccess("realName");
	}
	return true;
}

//验证身份证号
function checkIdCard(){
	//获取用户的身份证号码
	var idCard = $("#idCard").val();
	var replayIdCard = $("#replayIdCard").val();

	//验证
	if("" == idCard){
		showError("idCard","请输入身份证号码");
		return false;
	}else if (!/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(idCard)){
		showError("idCard","请输入正确的身份证号码");
		return false;
	}else if (!(idCard.length == 15 || idCard.length == 18)){
		showError("idCard","请输入正确的身份证号码");
		return false;
	}else {
		showSuccess("idCard");
	}
	if ("" != idCard && "" != replayIdCard && replayIdCard == idCard){
        showSuccess("idCard");
	}else {
        showError("replayIdCard","请输入身份证号");
        return false;
	}

	return true;
	
}
//验证确认身份证
function checkIdCardEequ() {
	//获取用户的身份证好么
	var idCard = $("#idCard").val();
    var replayIdCard = $("#replayIdCard").val();
    //验证
	if ("" == replayIdCard){
		showError("replayIdCard","请输入身份证号码");
		return false;
	}else if ("" == idCard){
		showError("idCard","请输入身份证号码");
		return false;
	}else if ( idCard != replayIdCard){
		showError("replayIdCard","两次输入的身份证号码不一致");
		return false;
	}else {
		showSuccess("replayIdCard");
	}
	return true;
}

//图形验证码
function checkCaptcha() {
	//获取用户输入的验证码
	var captcha = $("#captcha").val();
	//验证
	var flag = true;
	if ("" == captcha){
		showError("captcha","请输入验证码");
		return false;
	}else {
		//判断是否一样
		//获取图片上的验证码
		$.ajax({
			url:"loan/checkCaptcha",
			type:"post",
			data:"captcha=" + captcha,
			async:false,
			success:function (data) {
				if (data.errorMessage == "ok"){
                    showSuccess("captcha");
                    flag = true;
				}else {
					showError("captcha",data.errorMessage);
					flag = false;
				}
            },
            error:function () {
                flag = false;
                showError("captcha","系统繁忙,请稍后再试...");
            }
		});
	}
	if (!flag){
		return false;
	}
	return true;
}

//认证
function verifyRealName() {
	if(checkRealName() && checkIdCard() && checkIdCardEequ() && checkCaptcha()){
		//获取表单内容
		var idCard = $("#idCard").val();
		var realName = $("#realName").val();
		var replayIdCard = $("#replayIdCard").val();

		//发送请求
		$.ajax({
			url:"loan/verifyRealName",
			data:{
				"idCard":idCard,
				"replayIdCard":replayIdCard,
				"realName":realName
			},
			type:"post",
			success:function (data) {
				if(data.errorMessage == "ok"){
					//实名认证成功
					window.location.href="index.jsp";
				}else {
					//认证失败
					showError("captcha",data.errorMessage);
				}
            },
			error:function () {
				//实名认证失败
				showError("captcha",data.errorMessage);
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

//成功
function showSuccess(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id+"Ok").show();
	$("#"+id).removeClass("input-red");
}


//实名认证提交
function realName () {
	
	var idCard = $.trim($("#idCard").val());
	var replayIdCard = $.trim($("#replayIdCard").val());//确认身份证号
	var realName = $.trim($("#realName").val());
	var captcha = $.trim($("#captcha").val());
	
	if(userRealName(0) && idCardEequ() && checkCaptcha() && idCardCheck()) {
		$.ajax({
			type:"POST",
			url:"loan/checkRealName",
			dataType: "json",
			async: false,
			data:"realName="+realName+"&idCard="+idCard+"&replayIdCard="+replayIdCard+"&captcha="+captcha,
			success: function(retMap) {
				if (retMap.errorMessage == "ok") {
					window.location.href = "loan/showMyCenter";
				} else {
					showError('captcha', retMap.errorMessage);
				}
			},
		    error:function() {
				 showError('captcha','网络错误');
				 rtn = false;
			}
		});
	}
}
//同意实名认证协议
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