package com.mikhail.shop.cars.interceptores;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class InterceptorAdmin implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(InterceptorAdmin.class);

    @Value("${admin.password:changeme}")
    private String adminPassword;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String providedPassword = request.getParameter("pass-login-admin");

        if (adminPassword.equals(providedPassword)) {
            request.getSession().setAttribute("admin", "ok");
        }

        log.debug("Admin interceptor: {}", request.getRequestURI());

        if (request.getRequestURI().contains("/admin/")) {

            Object adminSession =
                    request.getSession().getAttribute("admin");

            if (!"ok".equals(adminSession)) {
                response.sendRedirect("/loginAdmin");
                return false;
            }
        }

        return true;
    }
}