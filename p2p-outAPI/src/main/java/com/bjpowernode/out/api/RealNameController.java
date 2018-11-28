package com.bjpowernode.out.api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bjpowernode.out.constants.Constants;
import com.bjpowernode.out.rto.ReturnObject;

/**
 * 实名认证接口（模拟第三方公司的实名认证接口，在实际企业开发中，会购买第三方公司的接口，调用他们的接口完成实名认证）
 * 
 * @author 郭鑫
 *
 */
@Controller
public class RealNameController {
	
	public static Map<String, String> idAuthMap = new ConcurrentHashMap<String, String>();
	
	public static Map<String, String> apiMap = new ConcurrentHashMap<String, String>();
	
	static {
		//验证此用户姓名和身份证号码是否合法
		idAuthMap.put("111111111111111111", "张三丰");
		idAuthMap.put("222222222222222222", "张无忌");
		idAuthMap.put("333333333333333333", "张翠山");
		idAuthMap.put("444444444444444444", "乔峰");
		idAuthMap.put("555555555555555555", "郭靖");
		idAuthMap.put("666666666666666666", "杨过");
		idAuthMap.put("777777777777777777", "汪剑通");
		idAuthMap.put("888888888888888888", "周伯通");
		idAuthMap.put("999999999999999999", "王重阳");
		idAuthMap.put("110110199909099999", "吴士忠");
		
		//验证接口调用的apiId+apiKey是否合法
		apiMap.put("10010", "edsii2j3b2ediR");
		apiMap.put("10020", "4ehrdfnvjds4dI");
	}

	/**
	 * 实名认证接口
	 * 
	 * @param apiId
	 * @param apikey
	 * @param realName
	 * @param idCard
	 * @return
	 */
	@RequestMapping(value="/outapi/realName") 
	public @ResponseBody ReturnObject realName (
			@RequestParam(value="apiId", required=true) String apiId,
			@RequestParam(value="apiKey", required=true) String apiKey,
			@RequestParam(value="realName", required=true) String realName,
			@RequestParam(value="idCard", required=true) String idCard) {
		
		System.out.println("接收的参数：" + realName + "-->" + idCard);
		
		ReturnObject returnObject = new ReturnObject();
		
		//判断apiId+apiKey是否合法
		if (apiMap.containsKey(apiId) && apiKey.equals(apiMap.get(apiId))) {
			
			//判断姓名身份证号码是否合法
			if (idAuthMap.containsKey(idCard) && realName.equals(idAuthMap.get(idCard))) {
				//实名验证通过
				returnObject.setErrorCode(Constants.ZERO);
				returnObject.setErrorMessage("实名认证通过");
			} else {
				//实名认证不通过
				returnObject.setErrorCode(Constants.ONE);
				returnObject.setErrorMessage("未找到该用户");
			}
		} else {
			//实名认证apiId或apiKey不匹配
			returnObject.setErrorCode(Constants.ONE);
			returnObject.setErrorMessage("apiId或apiKey不匹配");
		}
		//返回给调用方实名认证成功或失败信息
		return returnObject;
	}
}
