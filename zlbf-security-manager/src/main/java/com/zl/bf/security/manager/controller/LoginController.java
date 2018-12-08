package com.zl.bf.security.manager.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;

@RestController
@RequestMapping("/authentication")
public class LoginController {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private ProviderSignInUtils providerSignInUtils;
	
    /**
     * 认证页面
     * 		在 SecurityConfig 类中的 configure(HttpSecurity http) 方法里面定义了自定义登录
     * 	 匹配到这里的 RestController, 跳转到自定义登录页面
     * @return ModelAndView
     */
	/*@RequestMapping("/require")
    public ModelAndView require() {
        return new ModelAndView("ftl/login");
    }*/
	
	
	@PostMapping("/regist")
	public void regist(User user, HttpServletRequest request){
		// 不管是注册用户还是绑定用户，都会拿到一个用户唯一标识
		String userId = user.getUsername();
		
		logger.info(request.getParameter("type"));
		
		providerSignInUtils.doPostSignUp(userId, new ServletWebRequest(request));
		
		// 浏览器注册用providerSignInUtils
		//providerSignInUtils.doPostSignUp(userId, new ServletWebRequest(request));
		// app注册用AppSignUpUtils
		//appSignUpUtils.doPostSignUp(new ServletWebRequest(request), userId);
	}
}
