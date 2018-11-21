package com.zj.smart4j.framework;

import com.zj.smart4j.framework.bean.Data;
import com.zj.smart4j.framework.bean.Handler;
import com.zj.smart4j.framework.bean.Param;
import com.zj.smart4j.framework.bean.View;
import com.zj.smart4j.framework.helper.*;
import com.zj.smart4j.framework.util.JsonUtil;
import com.zj.smart4j.framework.util.ReflectionUtil;
import com.zj.smart4j.framework.util.StringUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 请求转发器
 */
@WebServlet(urlPatterns = "/*", loadOnStartup = 0)
public class DispatcherServlet extends HttpServlet {

    @Override
    public void init(ServletConfig servletConfig){
        //初始化相关helper
        HelperLoader.init();
        //获取ServletContext 对象（用于注册Servlet）
        ServletContext servletContext = servletConfig.getServletContext();
        //注册处理JSP的Servlet
        ServletRegistration jspRegistration = servletContext.getServletRegistration("jsp");
        jspRegistration.addMapping(ConfigHelper.getAppJspPath() + "*");
        //注册处理静态之的默认Servlet
        ServletRegistration staticRegistration = servletContext.getServletRegistration("default");
        staticRegistration.addMapping(ConfigHelper.getAppAssetPath() + "*");
        //初始化servlet文件上传对象
        UploadHelper.init(servletContext);
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse rep) throws ServletException, IOException {
        try {
            //初始化ServletHelper
            ServletHelper.init(req, rep);
            //获取请求方法与请求路径
            String requestMethod = req.getMethod().toLowerCase();
            String requestPath = req.getPathInfo();
            if (requestPath.equals("/favicon.ico")) {
                return;
            }
            //获取action 处理器
            Handler handler = ControllerHelper.getHandler(requestMethod, requestPath);
            if (handler != null) {
                //获取controller 类及其bean实例
                Class<?> controllerClz = handler.getControllerClz();
                Object controllerBean = BeanHelper.getBean(controllerClz);
                //创建请求参数对象
                Param param;
                if (UploadHelper.isMultipart(req)) {
                    param = UploadHelper.createParam(req);
                } else {
                    param = RequestHelper.createParam(req);
                }
                //调用Action 方法
                Object result;
                Method actionMethod = handler.getActionMethod();
                if (param.isEmpty()) {
                    result = ReflectionUtil.invokeMethod(controllerBean, actionMethod);
                } else {
                    result = ReflectionUtil.invokeMethod(controllerBean, actionMethod, param);
                }
                //处理Action方法返回值
                if (result instanceof View) {
                    //返回Jsp页面
                    handleViewResult(req, rep, (View) result);
                } else if (result instanceof Data) {
                    //返回JSON数据
                    handleDataResult(rep, (Data) result);
                }
            }
        } finally {
            //销毁ServletHelper
            ServletHelper.destroy();
        }
    }

    private void handleViewResult(HttpServletRequest req, HttpServletResponse rep, View view) throws IOException, ServletException {
        String path = view.getPath();
        if (StringUtil.isNotEmpty(path)) {
            if (path.startsWith("/")) {
                rep.sendRedirect(req.getContextPath() + path);
            } else {
                Map<String, Object> model = view.getModel();
                for (Map.Entry<String, Object> entry : model.entrySet()) {
                    req.setAttribute(entry.getKey(), entry.getValue());
                }
                req.getRequestDispatcher(ConfigHelper.getAppJspPath() + path).forward(req, rep);
            }
        }
    }

    private void handleDataResult(HttpServletResponse rep, Data data) throws IOException {
        Object model = data.getModel();
        if (model != null) {
            rep.setContentType("application/json");
            rep.setCharacterEncoding("utf-8");
            PrintWriter writer = rep.getWriter();
            String json = JsonUtil.toJson(model);
            writer.write(json);
            writer.flush();
            writer.close();
        }
    }

}
