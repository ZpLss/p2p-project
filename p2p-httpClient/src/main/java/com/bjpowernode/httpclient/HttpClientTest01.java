package com.bjpowernode.httpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * java原生httpclient
 * Description:使用java.net包获取远程url数据、调用远程接口
 * ClassName:HttpClientTest01
 * <p>Description:</p>
 * @author guoxin
 */
public class HttpClientTest01 {

	public static void main(String[] args) {
		//测试一：调用远程服务
		/*String httpUrl = "http://localhost:8083/outAPI/outapi/realName?apiId=10010&apiKey=edsii2j3b2ediR&realName=吴士忠&idCard=110110199909099999";
		String result = doGet(httpUrl);
		System.out.println(result);*/
		
		//测试二：调用baidu远程地址
		/*String httpUrl = "http://www.baidu.com";
		String result = doGet(httpUrl);
		System.out.println(result);*/
		
		//----------------------------------------------------
		
		//测试三：通过post调用远程服务，并且传送参数
		String httpUrl = "http://localhost:8083/outAPI/outapi/realName";
		String param = "apiId=10010&apiKey=edsii2j3b2ediR&realName=王重阳&idCard=999999999999999999";
		String result = doPost(httpUrl, param);
		System.out.println(result);
	}
	
	
	/**
	 * GET请求
	 * @param httpUrl
	 * @return
	 */
	public static String doGet(String httpUrl) {
		HttpURLConnection connection = null;
		InputStream is = null;
		BufferedReader br = null;
		String result = null;//返回结果字符串
		
		try {
			//创建远程url连接对象
			URL url = new URL(httpUrl);
			//通过远程url连接对象打开一个连接，强转成httpURLConnection类
			connection = (HttpURLConnection) url.openConnection();
			//设置连接方式：get
			connection.setRequestMethod("GET");
			//设置连接主机服务器的超时时间：15000毫秒
			connection.setConnectTimeout(15000);
			//设置读取远程返回的数据时间：60000毫秒
			connection.setReadTimeout(60000);
			
			//通过connection连接，获取输入流
			is = connection.getInputStream();
			//封装输入流is，并指定字符集
			br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
			
			//存放数据
			StringBuffer sbf = new StringBuffer();
			String temp = null;
			while ((temp = br.readLine()) != null) {
				sbf.append(temp);
				sbf.append("\r\n");//回车+换行
			}
			
			result = sbf.toString();
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
			
			connection.disconnect();//关闭远程连接
		}
		
		return result;
	}

	
	/**
	 * POST 请求
	 * @param httpUrl
	 * @param param
	 * @return
	 */
	public static String doPost(String httpUrl, String param) {
		HttpURLConnection connection = null;
		InputStream is = null;
		OutputStream os = null;
		BufferedReader br = null;
		String result = null;
		
		try {
			//创建远程url连接对象
			URL url = new URL(httpUrl);
			//通过远程url连接对象打开连接
			connection = (HttpURLConnection) url.openConnection();
			//设置连接请求方式
			connection.setRequestMethod("POST");
			//设置连接主机服务器超时时间：15000毫秒
			connection.setConnectTimeout(15000);
			//设置读取主机服务器返回数据超时时间：60000毫秒
			connection.setReadTimeout(60000);
			
			//默认值为：false，当向远程服务器传送数据/写数据时，需要设置为true
			connection.setDoOutput(true);
			//默认值为：true，当前向远程服务读取数据时，设置为true，该参数可有可无
			connection.setDoInput(true);
			
			//通过连接对象获取一个输出流
			os = connection.getOutputStream();
			//通过输出流对象将参数写出去/传输出去,它是通过字节数组写出的
			os.write(param.getBytes());//把需要传送的参数发送给远程url
			
			//通过连接对象获取一个输入流，向远程读取
			is = connection.getInputStream();
			//对输入流对象进行包装
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			
			StringBuffer sbf = new StringBuffer();
			String temp = null;
			//循环遍历一行一行读取数据
			while ((temp = br.readLine()) != null) {
				sbf.append(temp);
				sbf.append("\r\n");
			}
			
			result = sbf.toString();
			
			
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
			
			if(null != os) {
				try {
					os.close();
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
			
			//断开与远程地址url的连接
			connection.disconnect();
		}
		return result;
	}
	
}
