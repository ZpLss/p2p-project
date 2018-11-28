package com.bjpowernode.httpclient;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * HttpClient3.1 是apache下操作远程 url的工具包
 * 虽然已不再更新，但实现工作中使用httpClient3.1的代码还是很多
 * 现仍然在大量使用
 * ClassName:HttpClient02
 * <p>Description:</p>
 * @author guoxin
 */
public class HttpClientTest02 {

	public static void main(String[] args) throws UnsupportedEncodingException {
		String url = "http://localhost:8083/outAPI/outapi/realName?apiId=1111111111&apiKey=22222222&realName=333333333&idCard=444444444";
		String result = doGet(url);
		System.out.println(result);
		
		/*String url = "http://192.168.100.2:9090/outAPI/outapi/realName";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("apiId", "1111111111");
		paramMap.put("apiKey", "22222222");
		paramMap.put("realName", "333333333");
		paramMap.put("idCard", "444444444");
		
		String result = doPost(url, paramMap);
		System.out.println(result);*/
	}
	
	/**
	 * GET 请求方法
	 * 注：如果需要传递参数，把参数拼接在url地址后面
	 * @param url
	 */
	public static String doGet(String url) {
		//输入流
		InputStream is = null;
		BufferedReader br = null;
		String result = null;
		
		//创建httpClient实例
		HttpClient httpClient = new HttpClient();
		
		//设置http连接主机服务超时时间：15000毫秒
		//先获取连接管理器对象，再获取参数对象,再进行参数的赋值
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(15000);
		
		//创建一个Get方法实例对象
		GetMethod getMethod = new GetMethod(url);
		//设置get请求超时为60000毫秒
		getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 60000);
		//设置请求重试机制，默认重试次数：3次，参数设置为true，重试机制可用，false相反
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, true));
		
			
		try {
			//执行Get方法
			int statusCode = httpClient.executeMethod(getMethod);
			//判断返回码
			if(statusCode != HttpStatus.SC_OK) {
				//如果状态码返回的不是ok,说明失败了,打印错误信息
				System.err.println("Method faild: " + getMethod.getStatusLine());
			}
			
			//通过getMethod实例，获取远程的一个输入流
			is = getMethod.getResponseBodyAsStream();
			//包装输入流
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			
			StringBuffer sbf = new StringBuffer();
			//读取封装的输入流
			String temp = null;
			while ((temp = br.readLine()) != null) {
				sbf.append(temp).append("\r\n");
			}
			
			result = sbf.toString();
			
		} catch (HttpException e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			//关闭资源
			if(null != br) {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(null != is) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			//释放连接
			getMethod.releaseConnection();
		}
			
		return result;
	}
	
	/**
	 * POST 请求方法
	 * @param url
	 * @param paramMap
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static String doPost(String url, Map<String,Object> paramMap) throws UnsupportedEncodingException {
		//获取输入流
		InputStream is = null;
		BufferedReader br = null;
		String result = null;
		
		//创建httpClient实例对象
		HttpClient httpClient = new HttpClient();
		//设置httpClient连接主机服务器超时时间：15000毫秒
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(15000);
		
		//创建post请求方法实例对象
		PostMethod postMethod = new PostMethod(url);
		//设置post请求超时时间
		postMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 60000);
		
		NameValuePair[] nvp = null;
		//判断参数map集合paramMap是否为空
		if(null != paramMap && paramMap.size() > 0) {//不为空
			//创建键值参数对象数组，大小为参数的个数
			nvp = new NameValuePair[paramMap.size()];
			//循环遍历参数集合map
			Set<Entry<String, Object>> entrySet = paramMap.entrySet();
			//获取迭代器
			Iterator<Entry<String, Object>> iterator = entrySet.iterator();
			
			int index = 0;
			while(iterator.hasNext()) {
				Entry<String, Object> mapEntry = iterator.next();
				//从mapEntry中获取key和value创建键值对象存放到数组中
				nvp[index] = new NameValuePair(mapEntry.getKey(), new String(mapEntry.getValue().toString().getBytes("UTF-8"),"UTF-8"));
				index++;
			}
		}
		
		//判断nvp数组是否为空
		if(null != nvp && nvp.length > 0) {
			//将参数存放到requestBody对象中
			postMethod.setRequestBody(nvp);
		}
			
		try {
			//执行POST方法
			int statusCode = httpClient.executeMethod(postMethod);
			//判断是否成功
			if(statusCode != HttpStatus.SC_OK) {
				System.err.println("Method faild: " + postMethod.getStatusLine());
			}
			
			//获取远程返回的数据
			is = postMethod.getResponseBodyAsStream();
			//封装输入流
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			
			StringBuffer sbf = new StringBuffer();
			String temp = null;
			while ((temp = br.readLine()) != null) {
				sbf.append(temp).append("\r\n");
			}
			
			result = sbf.toString();
			
		} catch (HttpException e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			
			//关闭资源
			if(null != br) {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(null != is) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			//释放连接
			postMethod.releaseConnection();
		}
		
		
		
		return result;
	}

}
