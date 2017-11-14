package com.example.demo;

import java.io.IOException;
import java.io.*;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class WechatHelper {
	private static String token = "wechat";
	
	public static boolean checkSignature(String signature, String timestamp, String nonce) {
		List<String> params = new ArrayList<String>();
		params.add(timestamp);
		params.add(nonce);
		params.add(token);
		
		Collections.sort(params);
		
		String str = String.join("", params);
		
		String hashed_str = DigestUtils.sha1Hex(str);  // sha1 加密 str
		
		if (hashed_str != null && hashed_str.equalsIgnoreCase(signature)) {  // 对比 hashed_str 和 signature
			return true;
		}
		else {
			return false;
		}
	}

	/**
     * 解析微信发来的请求（XML）
     *
     * @param request 封装了请求信息的HttpServletRequest对象
     * @return map 解析结果
     * @throws Exception
     */
    public static Map<String, String> parseXml(HttpServletRequest request) throws Exception {
        Map<String, String> map = new HashMap<String, String>();      // 将解析结果存储在HashMap中
        InputStream inputStream = request.getInputStream();          // 从request中取得输入流
        SAXReader reader = new SAXReader();          // 读取输入流
        reader.setEncoding("utf-8"); 
        Document document = reader.read(inputStream);
        document.setXMLEncoding("utf-8"); 
        Element root = document.getRootElement();         // 得到xml根元素
        List<Element> elementList = root.elements();         // 得到根元素的所有子节点

        // 遍历所有子节点
        for (Element e : elementList) {
            System.out.println(e.getName() + "|" + e.getText());
            map.put(e.getName(), e.getText());
        }

        // 释放资源
        inputStream.close();
        inputStream = null;
        return map;
    }
    
    
    /**
     * 构造回复消息
     * @param map 封装了解析结果的Map
     * @return responseMessage(响应消息)
     * @throws UnsupportedEncodingException 
     */
    public static String buildResponseMessage(Map map) throws UnsupportedEncodingException{
        String responseMessage = "success";
        //得到消息类型
        String msgType = map.get("MsgType").toString();
        System.out.println("MsgType:" + msgType);

        //处理文本消息
        responseMessage = handleTextMessage(map);
 
        //返回响应消息
        return responseMessage;
    }
    
    
    /**
     * 接收到文本消息后处理
     * @param map 封装了解析结果的Map
     * @return
     * @throws UnsupportedEncodingException 
     */
    private static String handleTextMessage(Map<String, String> map) throws UnsupportedEncodingException {
        //响应消息
        String responseMessage = "success";
        // 消息内容
        String content = map.get("Content");
        
        Date nowTime = new Date(System.currentTimeMillis());
    	SimpleDateFormat sdFormatter = new SimpleDateFormat("MM月dd日hh时mm分");
    	String retStrFormatNowDate = sdFormatter.format(nowTime);

        switch (content) {
            case "hello":
                String msgText = "你好，现在时间是：" + retStrFormatNowDate;
                responseMessage = buildTextMessage(map, msgText);
                break;
            case "Hello":
                String msgText1 = "你好！现在时间是：" + retStrFormatNowDate;
                responseMessage = buildTextMessage(map, msgText1);
                break;
            default:
            	String msgText2 = "移动互联第三组全体成员欢迎你！";
                responseMessage = buildTextMessage(map, msgText2);
                break;
        }
        //返回响应消息
        return responseMessage;
    }
    
    
    /**
     * 构造文本消息
     * @param map 封装了解析结果的Map
     * @param content 文本消息内容
     * @return 文本消息XML字符串
     * @throws UnsupportedEncodingException 
     */
    private static String buildTextMessage(Map<String, String> map, String content) throws UnsupportedEncodingException {
        //发送方帐号
        String fromUserName = map.get("FromUserName");
        // 开发者微信号
        String toUserName = map.get("ToUserName");
        /**
         * 文本消息XML数据格式
         * <xml>
         <ToUserName><![CDATA[toUser]]></ToUserName>
         <FromUserName><![CDATA[fromUser]]></FromUserName>
         <CreateTime>1348831860</CreateTime>
         <MsgType><![CDATA[text]]></MsgType>
         <Content><![CDATA[this is a test]]></Content>
         <MsgId>1234567890123456</MsgId>
         </xml>
         */
        String trans_content = new String(content.getBytes("UTF-8"), "ISO-8859-1");  // encoding 转换，否则会有编码问题，中文显示不了
        return String.format(
        		"<xml>" +
        				"<ToUserName><![CDATA[%s]]></ToUserName>" +
        				"<FromUserName><![CDATA[%s]]></FromUserName>" +
        				"<CreateTime>%s</CreateTime>" +
        				"<MsgType><![CDATA[text]]></MsgType>" +
        				"<Content><![CDATA[%s]]></Content>" +
        				"</xml>",
        				fromUserName, toUserName, getMessageCreateTime(), trans_content);
    }
    

    private static String getMessageCreateTime() {
        Date dt = new Date();
        DateFormat df = new SimpleDateFormat("yyyyMMddhhmm");
        String nowTime = df.format(dt);
        long dd = (long) 0;
        try {
            dd = df.parse(nowTime).getTime();
        } catch (Exception e) {

        }
        return String.valueOf(dd);
    }
}
