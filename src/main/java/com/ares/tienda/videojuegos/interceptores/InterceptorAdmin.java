package com.ares.tienda.videojuegos.interceptores;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class InterceptorAdmin implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String adminPassword = request.getParameter("pass-login-admin");

        if ("123".equals(adminPassword)) {
            request.getSession().setAttribute("admin", "ok");
        }

        System.out.println("Admin interceptor: "
                + request.getRequestURI());

        if (request.getRequestURI().contains("/admin/")) {

            Object adminSession =
                    request.getSession().getAttribute("admin");

            if (!"ok".equals(adminSession)) {
                response.sendRedirect("../loginAdmin");
                return false;
            }
        }

        return true;
    }
}