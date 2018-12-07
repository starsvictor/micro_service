package com.zl.bf.security.browser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.zl.bf.security.browser.handler.CustomerAuthenticationFailureHandler;
import com.zl.bf.security.browser.handler.CustomerAuthenticationSuccessHandler;
import com.zl.bf.security.core.captcha.CaptchaSecurityConfig;
import com.zl.bf.security.core.properties.CustomerSecurityProperties;


@Configuration
@EnableWebSecurity
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(); 
	}
	
	
	@Autowired
	private CustomerSecurityProperties customerSecurityProperties;
	
	
	@Autowired
	private CustomerAuthenticationSuccessHandler customerAuthenticationSuccessHandler;
	
	@Autowired
	private CustomerAuthenticationFailureHandler customerAuthenticationFailureHandler;
	
	@Autowired
	private CaptchaSecurityConfig captchaSecurityConfig;
	
	@Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/fonts/**", "/**/*.ftl");
        web.ignoring().antMatchers("/css/**");
        web.ignoring().antMatchers("/images/**");
        web.ignoring().antMatchers("/code/**");
        web.ignoring().antMatchers("/authentication/**");
        web.ignoring().antMatchers("/js/**");
    }
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http
			// 验证码校验相关配置
			.apply(captchaSecurityConfig)
			.and()
			// .httpBasic()
			// 表单登陆相关配置
			.formLogin()
			.loginPage("/authentication/require")
			.loginProcessingUrl("/authentication/form")
			.successHandler(customerAuthenticationSuccessHandler)
			.failureHandler(customerAuthenticationFailureHandler)
			.and().logout().logoutUrl("/logout").logoutSuccessUrl("/authentication/require").permitAll()
			.and()
			.authorizeRequests()
			.antMatchers("/authentication/require",
					"/authentication/mobile",
					"/logout",
					"/code/image",
					"/code/mobile",
					"/fonts/**",
					customerSecurityProperties.getBrowser().getLoginPage()).permitAll()
		 	.anyRequest()
		 	.authenticated()
			.and()
			// csrf配置
			.csrf().disable()
			;
	}
}
