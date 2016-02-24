package com.jdk2010.framework.solr.util;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.ctc.wstx.util.ExceptionUtil;
import com.jdk2010.framework.solr.annotation.SolrField;

public class ReflactKit {
    // 缓存所有的字段
    private static Map<String, Set<String>> allFieldmap = new ConcurrentHashMap<String, Set<String>>();
    // 缓存 所有的数据库字段
    private static Map<String, List<String>> allSolrFieldmap = new ConcurrentHashMap<String, List<String>>();

    public static List<String> getAllSolrFields(Class clazz) {
        if (clazz == null) {
            return null;
        }
        String className = clazz.getName();
        boolean iskey = allSolrFieldmap.containsKey(className);
        if (iskey) {
            return allSolrFieldmap.get(className);
        }

        Set<String> allNames = getAllFieldNames(clazz);
        if (allNames == null || allNames.isEmpty()) {
            return null;
        }
        List<String> dbList = new ArrayList<String>();
        for (String fdName : allNames) {
            boolean isSolrField = isAnnotationField(clazz, fdName, SolrField.class);
            if (isSolrField == true) {
                dbList.add(fdName);
            }
        }
        allSolrFieldmap.put(className, dbList);
        return dbList;
    }

    /**
     * 获取字段名前面配置的注解
     * @param clazz
     * @param fdName
     * @param annotationClass
     * @return
     */
    public static boolean isAnnotationField(Class clazz, String fdName, Class annotationClass) {
        PropertyDescriptor pd = null;
        try {
            if (clazz == null || fdName == null || annotationClass == null)
                return false;
            pd = new PropertyDescriptor(fdName, clazz);
            Field field = clazz.getDeclaredField(fdName);
            Annotation anno = field.getAnnotation(annotationClass);
            if (anno != null) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Set<String> getAllFieldNames(Class clazz) {
        if (clazz == null) {
            return null;
        }
        String className = clazz.getName();
        boolean iskey = allFieldmap.containsKey(className);
        if (iskey) {
            return allFieldmap.get(className);
        }
        Set<String> allSet = new HashSet<String>();
        allSet = recursionFiled(clazz, allSet);
        allFieldmap.put(className, allSet);
        return allSet;
    }

    /**
     * 递归查询父类的所有属性,set 去掉重复的属性
     * 
     * @param clazz
     * @param fdNameSet
     * @return
     * @throws Exception
     */
    private static Set<String> recursionFiled(Class clazz, Set<String> fdNameSet) {
        Field[] fds = clazz.getDeclaredFields();
        String name = "";
        for (int i = 0; i < fds.length; i++) {
            Field fd = fds[i];
            name = (fd.getName());
            fdNameSet.add(name);
        }
        return fdNameSet;
    }
    
    public static Object getPropertieValue(String p, Object o) {
        Object _obj = null;
        for (Class<?> clazz = o.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                PropertyDescriptor pd = new PropertyDescriptor(p, clazz);
                Method getMethod = pd.getReadMethod();// 获得get方法
                if (getMethod != null) {
                    _obj = getMethod.invoke(o);
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("getPropertieValue"+e.getMessage());
            }

        }

        return _obj;
    }

    public static void setPropertieValue(String p, Object o, Object value) throws Exception {
        for (Class<?> clazz = o.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                PropertyDescriptor pd = new PropertyDescriptor(p, clazz);
                Method setMethod = pd.getWriteMethod();// 获得set方法
                if (setMethod != null) {
                    setMethod.invoke(o, value);
                    break;
                }
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }
    }
    
}
