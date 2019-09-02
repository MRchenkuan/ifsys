package com.gigold.pay.ifsys.filter;

import com.gigold.pay.framework.service.AbstractService;
import com.gigold.pay.framework.util.common.StringUtil;
import com.gigold.pay.framework.web.constant.SessionConstant;
import com.gigold.pay.ifsys.bo.UserInfo;
import com.gigold.pay.ifsys.service.WeChatService;
import com.gigold.pay.ifsys.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Servlet Filter implementation class SessionFilter
 * 拦截所有的html请求
 */
public class WxAllowanceFilter extends AbstractService implements Filter {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String[] allowUrls;

	@Autowired
	WeChatService weChatService;
	/**
	 * @return the allowUrls
	 */
	public String[] getAllowUrls() {
		return allowUrls;
	}

	/**
	 * @param allowUrls
	 *            the allowUrls to set
	 */
	public void setAllowUrls(String[] allowUrls) {
		this.allowUrls = allowUrls;
	}

	/**
	 * Default constructor.
	 */
	public WxAllowanceFilter() {
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
	}



	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		String urls = fConfig.getInitParameter("allowUrlStr");
		this.allowUrls = urls.split(",");
	}

	/**
	 * 微信会话控制
	 * @param request
	 * @param response
	 * @param chain
	 * @throws IOException
	 * @throws ServletException
     */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		boolean flag = false;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		String requestUrl = httpRequest.getRequestURI().replace(httpRequest.getContextPath(), "");
		debug("===" + requestUrl);
		if (null != allowUrls && allowUrls.length >= 1)
			for (String url : allowUrls) {
				url = url.trim();
				if (requestUrl.contains(url)) {
					flag = true;
				}
			}
		if (flag) {
			// 若是允许地址,则通过
			chain.doFilter(request, response);
		} else {
			HttpSession session = httpRequest.getSession();
			if (session.getAttribute(Constant.LOGIN_KEY) == null) {
				try {
					// 没找到用户时
					String code = httpRequest.getParameter("code");
					System.out.println("code:" + code);
					if (StringUtil.isEmpty(code)) {
						httpResponse.sendRedirect(httpRequest.getContextPath() + "/wechat/err.html?msg=无权访问info=你无权访问该页面");
						return;
					}
					// 初始化 weChatService bean
					ServletContext sc = httpRequest.getSession().getServletContext();
					XmlWebApplicationContext cxt = (XmlWebApplicationContext) WebApplicationContextUtils.getWebApplicationContext(sc);
					if(cxt != null && cxt.getBean("weChatService") != null && weChatService == null)
						weChatService = (WeChatService) cxt.getBean("weChatService");
					// 若session中未查询到用户,则重新获取用户
					UserInfo user = weChatService.getUserInfo(code, session);
					System.out.println("user:" + user);
					// 若获取成功,则继续
					if (user != null && user.getId() > 0) {
						System.out.println("从微信获取用户信息成功" + user.getLoginName());
						session.setAttribute(Constant.LOGIN_KEY, user);
						// 新版本登录
						session.setAttribute(SessionConstant.GIGOLD_AUTH, Constant.SESSION_VALUE_GIGOLD_AUTH);
						session.setAttribute(SessionConstant.GIGOLD_USR_NO, user.getId());

						String userName = user.getUserName();
						chain.doFilter(request, response);
					} else {
						System.out.println("从微信获取用户信息失败");
						httpResponse.sendRedirect(httpRequest.getContextPath() + "/wechat/err.html?msg=获取不到用户信息&info=获取不到用户信息");
					}
				}catch (Exception e){
					e.printStackTrace();
					httpResponse.sendRedirect(httpRequest.getContextPath() + "/wechat/err.html?msg=你遇到了500&info=出现了一些问题");
				}
			} else {
				// 如果登录,则通过
				chain.doFilter(request, response);
			}
		}
	}

}
