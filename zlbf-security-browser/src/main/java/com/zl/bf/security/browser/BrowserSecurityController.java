package com.zl.bf.security.browser;

import java.io.IOException;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.ModelAndView;

import com.google.code.kaptcha.Producer;
import com.zl.bf.security.browser.support.SimpleResponse;
import com.zl.bf.security.browser.support.SocialUserInfo;
import com.zl.bf.security.core.properties.CustomerSecurityProperties;
import com.zl.bf.security.core.properties.constant.CustomerSecurityConstants;

@RestController
public class BrowserSecurityController {
	
	@Resource
    protected RedisTemplate<String, Object> redisTemplate;
	
	private final String REDIS_KEY = "zlbf:kaptcha";
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	// 重定向到身份验证url前， 会在Session塞一个SavedRequest对象（具体可查看源码ExceptionTranslationFilter.sendStartAuthentication方法）， 
	// 通过RequestCache.getRequest()可获取到
	private RequestCache requestCache = new HttpSessionRequestCache();
	
	// 工具类，方便拼装重定向url
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	
	@Autowired
	private CustomerSecurityProperties customerSecurityProperties;
	
	@Autowired
	private Producer producer;
	
	@Autowired
	private ProviderSignInUtils providerSignInUtils;
	
	/**
	 * 当没身份校验时的登陆调试跳转逻辑
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value = "/authentication/require", method = {RequestMethod.GET})
	@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
	public Object requireAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authenction) throws IOException {
		
		SavedRequest savedRequest = requestCache.getRequest(request, response);
		
		boolean auth = authenction != null && authenction.isAuthenticated();
		boolean hasBackUrl = savedRequest != null;
		
		if (auth) {
			return new SimpleResponse("你已登录成功, 请自行游览");
		} else {
			if (hasBackUrl) {
				String targetUrl = savedRequest.getRedirectUrl();
				if (StringUtils.endsWithIgnoreCase(targetUrl, ".html")) {
					// 如果是页面请求, 重定向回去
					redirectStrategy.sendRedirect(request, response, customerSecurityProperties.getBrowser().getLoginPage());
					return null;
				}
				
			}
			//return new SimpleResponse("访问的服务需要身份验证, 请引导用户到登陆页");
			return new ModelAndView("ftl/login");
		}
	}
	
	
//	@RequestMapping(value = "/code/image", method = {RequestMethod.GET})
//	public void captcha(HttpServletResponse response) throws ServletException, IOException {
//		response.setHeader("Cache-Control", "no-store, no-cache");
//		response.setContentType("image/jpeg");
//
//		// 生成文字验证码
//		String text = producer.createText();
//		// 生成图片验证码
//		BufferedImage image = producer.createImage(text);
//		// 保存到验证码到 Redis
//		redisTemplate.opsForValue().set(REDIS_KEY, text, 60, TimeUnit.SECONDS);	//向redis里存入数据和设置缓存时间 
//
//		ServletOutputStream out = response.getOutputStream();
//		ImageIO.write(image, "jpg", out);
//		IOUtils.closeQuietly(out);
//	}
	
	@GetMapping("/me")
	public Object getCurrentUser(@AuthenticationPrincipal UserDetails user) {
		return user;
	}
	
	
	
	/**
     * 主页
     *
     * @param userDetails
     * @param map
     * @return
     */
    @GetMapping(value = {"/index", "/"})
    public ModelAndView index(@AuthenticationPrincipal UserDetails userDetails, Map<String, String> map) {
        map.put("username", userDetails.getUsername());

        //
        return new ModelAndView("ftl/index", map);

    }

    @GetMapping(value = "/main")
    public ModelAndView main(@AuthenticationPrincipal UserDetails userDetails, Map<String, String> map) {
        map.put("username", userDetails.getUsername());

        //
        return new ModelAndView("ftl/main", map);

    }

    @GetMapping(value = "/hello")
    public ModelAndView hello(@AuthenticationPrincipal UserDetails userDetails, Map<String, String> map) {
        map.put("username", userDetails.getUsername());

        //
        return new ModelAndView("ftl/hello", map);

    }

    /**
     * 注册界面
     *
     * @param map
     * @return
     */
    @GetMapping(value = "/register")
    public ModelAndView register(Map<String, String> map) {
        return new ModelAndView("ftl/register", map);
    }

    /**
     * 注册界面
     *
     * @param map
     * @return
     */
    /*@GetMapping(value = "/socialRegister")
    public ModelAndView socialRegister(HttpServletRequest request, Map<String, Object> map) {
        SocialUserInfo userInfo = new SocialUserInfo();
        Connection<?> connection = providerSignInUtils.getConnectionFromSession(new ServletWebRequest(request));
        userInfo.setProviderId(connection.getKey().getProviderId());
        userInfo.setProviderUserId(connection.getKey().getProviderUserId());
        userInfo.setNickname(connection.getDisplayName());
        userInfo.setHeadImg(connection.getImageUrl());
        map.put("user", userInfo);
        return new ModelAndView("socialRegister", map);
    }*/

    /**
     * 手机号登录界面
     *
     * @param request
     * @param response
     * @param map
     * @return
     */
    @GetMapping(CustomerSecurityConstants.DEFAULT_SIGNIN_PROCESS_URL_MOBILEPAGE)
    public ModelAndView mobilePage(HttpServletRequest request, HttpServletResponse response, Map<String, Object> map) {

        return new ModelAndView("ftl/mobileLoginPage", map);
    }

    @GetMapping("/message/{msg}")
    public ModelAndView message(@PathVariable("msg") String msg, HttpServletRequest request, HttpServletResponse response, Map<String, Object> map) {
        String message="解绑成功！";
        if("bindingSuccess".equals(msg)){
            message="绑定成功！";
        }
        String page = "success";
        map.put("msg", message);
        map.put("url", "/index");
        return new ModelAndView(page, map);
    }
	
    /**
	 * 查看已绑定用户
	 * @param request
	 * @return
	 */
	@GetMapping("/social/user")
	public SocialUserInfo getSocialInfo(HttpServletRequest request) {
		SocialUserInfo userInfo = new SocialUserInfo();
		Connection<?> connection = providerSignInUtils.getConnectionFromSession(new ServletWebRequest(request));
		
		if (connection != null)  {
			userInfo.setProviderId(connection.getKey().getProviderId());
			userInfo.setProviderUserId(connection.getKey().getProviderUserId());
			userInfo.setNickName(connection.getDisplayName());
			userInfo.setHeaderImg(connection.getImageUrl());
		}
		
		return userInfo;
	}
}
