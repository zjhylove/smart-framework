package com.zj.smart4j.framework.helper;

import com.zj.smart4j.framework.bean.FormParam;
import com.zj.smart4j.framework.bean.Param;
import com.zj.smart4j.framework.util.CodecUtil;
import com.zj.smart4j.framework.util.StreamUtil;
import com.zj.smart4j.framework.util.StringUtil;
import org.apache.commons.lang3.ArrayUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

/**
 * 请求助手
 */
public class RequestHelper {

    /**
     * 创建请求对象
     *
     * @param request
     * @return
     * @throws IOException
     */
    public static Param createParam(HttpServletRequest request) throws IOException {
        List<FormParam> formParams = new ArrayList<>();
        formParams.addAll(parseParameterNames(request));
        formParams.addAll(parseInputStream(request));
        return new Param(formParams);
    }

    private static Collection<? extends FormParam> parseParameterNames(HttpServletRequest request) {
        List<FormParam> formParams = new ArrayList<>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String filedName = parameterNames.nextElement();
            String[] fieldValues = request.getParameterValues(filedName);
            if (ArrayUtils.isNotEmpty(fieldValues)) {
                Object filedValue;
                if (fieldValues.length == 1) {
                    filedValue = fieldValues[0];
                } else {
                    StringBuilder sb = new StringBuilder("");
                    for (int i = 0; i < fieldValues.length; i++) {
                        sb.append(fieldValues[i]);
                        if (i != fieldValues.length - 1) {
                            sb.append(StringUtil.SEPARATOR);
                        }
                    }
                    filedValue = sb.toString();
                }
                formParams.add(new FormParam(filedName, filedValue));
            }
        }
        return formParams;
    }

    private static Collection<? extends FormParam> parseInputStream(HttpServletRequest request) throws IOException {
        List<FormParam> formParams = new ArrayList<>();
        String body = CodecUtil.decodeURL(StreamUtil.getString(request.getInputStream()));
        if (StringUtil.isNotEmpty(body)) {
            String[] kvs = StringUtil.split(body, "&");
            if (ArrayUtils.isNotEmpty(kvs)) {
                for (String kv : kvs) {
                    String[] array = StringUtil.split(kv, "=");
                    if (ArrayUtils.isNotEmpty(array) && array.length == 2) {
                        formParams.add(new FormParam(array[0], array[1]));
                    }
                }
            }
        }
        return formParams;
    }

}
