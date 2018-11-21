package com.zj.smart4j.framework.bean;

import com.zj.smart4j.framework.util.CastUtil;
import com.zj.smart4j.framework.util.CollectionUtil;
import com.zj.smart4j.framework.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请求参数对象
 */
public class Param {

    private List<FormParam> formParams;
    private List<FileParam> fileParams;

    public Param(List<FormParam> formParams) {
        this.formParams = formParams;
    }

    public Param(List<FormParam> formParams, List<FileParam> fileParams) {
        this.formParams = formParams;
        this.fileParams = fileParams;
    }

    /**
     * 获取请求参数映射
     *
     * @return
     */
    public Map<String, Object> getFieldMap() {
        Map<String, Object> filedMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(formParams)) {
            for (FormParam formParam : formParams) {
                String fieldName = formParam.getFiledName();
                Object filedValue = formParam.getFiledValue();
                if (filedMap.containsKey(fieldName)) {
                    filedValue = filedMap.get(fieldName) + StringUtil.SEPARATOR + filedValue;
                }
                filedMap.put(fieldName, filedValue);
            }
        }
        return filedMap;
    }

    /**
     * 获取上传文件映射
     *
     * @return
     */
    public Map<String, List<FileParam>> getFileMap() {
        Map<String, List<FileParam>> fileMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(fileParams)) {
            for (FileParam fileParam : fileParams) {
                String filedName = fileParam.getFiledName();
                List<FileParam> fileParamList;
                if (fileMap.containsKey(filedName)) {
                    fileParamList = fileMap.get(filedName);
                } else {
                    fileParamList = new ArrayList<>();
                }
                fileParamList.add(fileParam);
                fileMap.put(filedName, fileParamList);
            }
        }
        return fileMap;
    }

    /**
     * 获取所有上传文件
     *
     * @param filedName
     * @return
     */
    public List<FileParam> getFileParams(String filedName) {
        return getFileMap().get(filedName);
    }

    /**
     * 获取唯一的上传文件
     *
     * @param fileName
     * @return
     */
    public FileParam getFile(String fileName) {
        List<FileParam> fileParams = getFileParams(fileName);
        if (CollectionUtil.isNotEmpty(fileParams) && fileParams.size() == 1) {
            return fileParams.get(0);
        }
        return null;
    }

    /**
     * 验证参数是否为空
     *
     * @return
     */
    public boolean isEmpty() {
        return CollectionUtil.isEmpty(formParams) && CollectionUtil.isEmpty(fileParams);
    }

    /**
     * 根据参数名获取String型参数值
     *
     * @param name
     * @return
     */
    public String getString(String name) {
        return CastUtil.castString(getFieldMap().get(name));
    }

    /**
     * 根据参数名获取double 型参数值
     *
     * @param name
     * @return
     */
    public double getDouble(String name) {
        return CastUtil.castDouble(getFieldMap().get(name));
    }

    /**
     * 根据参数名获取long型参数值
     *
     * @param name
     * @return
     */
    public long getLong(String name) {
        return CastUtil.castLong(getFieldMap().get(name));
    }

    /**
     * 根据参数名获取int 型参数值
     *
     * @param name
     * @return
     */
    public int getInt(String name) {
        return CastUtil.castInt(getFieldMap().get(name));
    }

    /**
     * 根据参数名获取boolean型参数值
     *
     * @param name
     * @return
     */
    public boolean getBoolean(String name) {
        return CastUtil.castBoolean(getFieldMap().get(name));
    }

}
