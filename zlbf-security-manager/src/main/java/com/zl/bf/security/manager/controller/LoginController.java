package com.zl.bf.security.manager.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/authentication")
public class LoginController {
	
    /**
     * 认证页面
     * 		在 SecurityConfig 类中的 configure(HttpSecurity http) 方法里面定义了自定义登录
     * 	 匹配到这里的 RestController, 跳转到自定义登录页面
     * @return ModelAndView
     */
	@RequestMapping("/require")
    public ModelAndView require() {
        return new ModelAndView("ftl/login");
    }

}
