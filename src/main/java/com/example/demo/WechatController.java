package com.example.demo;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WechatController {
	@RequestMapping("/wx")
	public String wx(
		@RequestParam("signature") String signature, 
		@RequestParam("timestamp")String timestamp, 
		@RequestParam("nonce")String nonce, 
		@RequestParam("echostr")String echostr) {
		
		if(WechatHelper.checkSignature(signature, timestamp, nonce)) {
			return echostr;
		}
		return "failed";
	}
	
    
	@ResponseBody
	@RequestMapping(value = "/wx", method = RequestMethod.POST)
	public void wechatServicePost(PrintWriter out, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        System.out.println("收到请求");
        String responseMessage = "success";
        try {
            // 解析微信发来的请求,将解析后的结果封装成Map返回
            Map<String,String> map = WechatHelper.parseXml(request);
            System.out.println("开始构造响应消息");
            responseMessage = WechatHelper.buildResponseMessage(map);
            System.out.println(responseMessage);
            if(responseMessage.equals("")){
                responseMessage ="未正确响应";
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("发生异常："+ e.getMessage());
            responseMessage ="未正确响应";
        }
        response.getWriter().println(responseMessage);
    }
}
