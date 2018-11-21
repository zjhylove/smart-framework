package com.zj.smart4j.framework.helper;

import com.zj.smart4j.framework.bean.FileParam;
import com.zj.smart4j.framework.bean.FormParam;
import com.zj.smart4j.framework.bean.Param;
import com.zj.smart4j.framework.util.CollectionUtil;
import com.zj.smart4j.framework.util.FileUtil;
import com.zj.smart4j.framework.util.StreamUtil;
import com.zj.smart4j.framework.util.StringUtil;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 文件上传助手
 */
public class UploadHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadHelper.class);

    private static ServletFileUpload servletFileUpload;

    /**
     * 初始化
     *
     * @param servletContext
     */
    public static void init(ServletContext servletContext) {
        File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
        servletFileUpload = new ServletFileUpload(new DiskFileItemFactory(DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD, repository));
        int uploadLimit = ConfigHelper.getAppUploadLimit();
        if (uploadLimit != 0) {
            servletFileUpload.setFileSizeMax(uploadLimit * 1024 * 1024);
        }
    }

    /**
     * 判断请求是否为multipart 类型
     *
     * @param request
     * @return
     */
    public static boolean isMultipart(HttpServletRequest request) {
        return ServletFileUpload.isMultipartContent(request);
    }

    /**
     * 创建请求对象
     *
     * @param request
     * @return
     * @throws IOException
     */
    public static Param createParam(HttpServletRequest request) throws IOException {
        List<FormParam> formParams = new ArrayList<>();
        List<FileParam> fileParams = new ArrayList<>();
        try {
            Map<String, List<FileItem>> fileItemsMap = servletFileUpload.parseParameterMap(request);
            if (CollectionUtil.isNotEmpty(fileItemsMap)) {
                for (Map.Entry<String, List<FileItem>> fileItems : fileItemsMap.entrySet()) {
                    String filedName = fileItems.getKey();
                    List<FileItem> fileItemsValue = fileItems.getValue();
                    if (CollectionUtil.isNotEmpty(fileItemsValue)) {
                        for (FileItem fileItem : fileItemsValue) {
                            if (fileItem.isFormField()) {
                                String filedValue = fileItem.getString("UTF-8");
                                formParams.add(new FormParam(filedName, filedValue));
                            } else {
                                String fileName = FileUtil.getRealFileName(new String(fileItem.getName().getBytes(), StandardCharsets.UTF_8));
                                if (StringUtil.isNotEmpty(fileName)) {
                                    long fileSize = fileItem.getSize();
                                    String contentType = fileItem.getContentType();
                                    InputStream inputStream = fileItem.getInputStream();
                                    fileParams.add(new FileParam(filedName, fileName, fileSize, contentType, inputStream));
                                }
                            }
                        }
                    }
                }
            }
        } catch (FileUploadException e) {
            LOGGER.error("create param failure ", e);
            throw new RuntimeException(e);
        }
        return new Param(formParams, fileParams);
    }

    /**
     * 上传文件
     *
     * @param basePath
     * @param fileParam
     */
    public static void uploadFile(String basePath, FileParam fileParam) {
        try {
            if (fileParam != null) {
                String filePath = basePath + fileParam.getFileName();
                FileUtil.createFile(filePath);
                InputStream inputStream = new BufferedInputStream(fileParam.getInputStream());
                OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(filePath));
                StreamUtil.copyStream(inputStream, outputStream);
            }
        } catch (Exception e) {
            LOGGER.error("upload file failure", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 批量上传文件
     *
     * @param basePath
     * @param fileParams
     */
    public static void uploadFile(String basePath, List<FileParam> fileParams) {
        try {
            if (CollectionUtil.isNotEmpty(fileParams)) {
                for (FileParam fileParam : fileParams) {
                    uploadFile(basePath, fileParam);
                }
            }
        } catch (Exception e) {
            LOGGER.error("upload file failure", e);
            throw new RuntimeException(e);
        }
    }
}
