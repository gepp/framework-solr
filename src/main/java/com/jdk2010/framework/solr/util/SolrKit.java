package com.jdk2010.framework.solr.util;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.jdk2010.framework.solr.constant.SolrContants;
import com.sun.tools.jdi.LinkedHashMap;

public class SolrKit {
    private StringBuffer queryString;

    private static final Logger logger = LoggerFactory.getLogger(SolrKit.class);

    private int rows;

    // ***********高亮参数****************
    private boolean isHighlight;
    private String highlightPre; // 前缀
    private String highlightPost; // 后缀
    private List<String> highlightFieldList; // 高亮字符串
    private int highlightFragsize; // 返回的字符个数
    // ***********高亮参数****************
    // 排序参数
    private List<Map<String, Object>> sortList; // 排序 key 是字段名称 值是asc 或者desc

    // ***********facet参数****************
    private boolean isFacet;
    private List<String> facetFieldList; // facet

    // ***********facet参数****************

    public List<String> getFacetFieldList() {
        return facetFieldList;
    }

    public void setFacetFieldList(List<String> facetFieldList) {
        this.facetFieldList = facetFieldList;
    }

    public boolean getHighlight() {
        return isHighlight;
    }

    public SolrKit setHighlight(boolean isHighlight) {
        this.isHighlight = isHighlight;
        return this;
    }

    public boolean getFacet() {
        return isFacet;
    }

    public SolrKit setFacet(boolean isFacet) {
        this.isFacet = isFacet;
        return this;
    }

    public SolrKit addFacetField(String fieldName) {
        if (StringUtils.isBlank(fieldName)) {
            throw new RuntimeException("facet字段不能为空！");
        }
        if (!facetFieldList.contains(fieldName)) {
            if (!fieldName.equals(SolrContants.DEFAULT_PK)) {
                facetFieldList.add(fieldName + SolrContants.STRING_SUFFIX);
            } else {
                facetFieldList.add(fieldName);
            }
        }
        return this;
    }

    public SolrKit addFacetField(String fieldName, Class clazz) {
        String newFieldName = SolrKit.getFieldNameByClass(fieldName, clazz);
        facetFieldList.add(newFieldName);
        return this;
    }

    public SolrKit addHighlightField(String fieldName) {
        if (StringUtils.isBlank(fieldName)) {
            throw new RuntimeException("搜索字段不能为空！");
        }
        if (!highlightFieldList.contains(fieldName)) {
            if (!fieldName.equals(SolrContants.DEFAULT_PK)) {
                highlightFieldList.add(fieldName + SolrContants.STRING_SUFFIX);
            } else {
                highlightFieldList.add(fieldName);
            }

        }
        return this;
    }

    public SolrKit addSortField(String fieldName, ORDER orderType) {
        if (StringUtils.isBlank(fieldName) || orderType == null) {
            throw new RuntimeException("排序字段或者排序类型不能为空！");
        }
        Map<String, Object> map = new HashMap<String, Object>();
        if (!fieldName.equals(SolrContants.DEFAULT_PK)) {
            map.put(SolrContants.SORT_MAP_KEY_NAME, fieldName + SolrContants.STRING_SUFFIX);
        } else {
            map.put(SolrContants.SORT_MAP_KEY_NAME, fieldName);
        }
        map.put(SolrContants.SORT_MAP_KEY_VALUE, orderType);
        map.put(fieldName + SolrContants.STRING_SUFFIX, orderType);
        if (!sortList.contains(map)) {
            sortList.add(map);
        }
        return this;
    }

    @SuppressWarnings("rawtypes")
    public SolrKit addHighlightField(String fieldName, Class clazz) {
        String newFieldName = SolrKit.getFieldNameByClass(fieldName, clazz);
        highlightFieldList.add(newFieldName);
        return this;
    }

    public String getHighlightPre() {
        return highlightPre;
    }

    public SolrKit setHighlightPre(String highlightPre) {
        this.highlightPre = highlightPre;
        return this;
    }

    public String getHighlightPost() {
        return highlightPost;
    }

    public SolrKit setHighlightPost(String highlightPost) {
        this.highlightPost = highlightPost;
        return this;
    }

    public List<String> getHighlightFieldList() {
        return highlightFieldList;
    }

    public SolrKit setHighlightFieldList(List<String> highlightFieldList) {
        this.highlightFieldList = highlightFieldList;
        return this;
    }

    public int getHighlightFragsize() {
        return highlightFragsize;
    }

    public SolrKit setHighlightFragsize(int highlightFragsize) {
        this.highlightFragsize = highlightFragsize;
        return this;
    }

    public int getRows() {
        return rows;
    }

    public SolrKit setRows(int rows) {
        this.rows = rows;
        return this;
    }

    public SolrKit() {
        this.queryString = new StringBuffer();
        this.isHighlight = false;
        this.isFacet=false;
        this.rows = 0;
        this.highlightPre = "<font color=\"red\">";
        this.highlightPost = "</font>";
        this.highlightFragsize = 120;
        this.highlightFieldList = new ArrayList<String>();
        this.sortList = new ArrayList<Map<String, Object>>();
    }

    public SolrKit(String highLightFieldName, boolean highlight) {
        this();
        this.isHighlight = true;
        this.highlightFieldList.add(highLightFieldName + SolrContants.STRING_SUFFIX);
    }

    public SolrKit(String highLightFieldName, Class clazz) {
        this();
        this.isHighlight = true;
        String newFieldName = SolrKit.getFieldNameByClass(highLightFieldName, clazz);
        this.highlightFieldList.add(newFieldName);
    }

    private String formatUTCString(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String s = sdf.format(d);
        return s;
    }

    public SolrKit andEquals(String fieldName, String val) {
        queryString.append(" && ").append(fieldName).append(":").append(val);
        return this;
    }

    public SolrKit orEquals(String fieldName, String val) {
        queryString.append(" || ").append(fieldName).append(":").append(val);
        return this;
    }

    public SolrKit andNotEquals(String fieldName, String val) {
        queryString.append(" && ").append("-").append(fieldName).append(":").append(val);
        return this;
    }

    public SolrKit orNotEquals(String fieldName, String val) {
        queryString.append(" || ").append("-").append(fieldName).append(":").append(val);
        return this;
    }

    // 大于
    public SolrKit andGreaterThan(String fieldName, String val) {
        queryString.append(" && ").append(fieldName).append(":[").append(val).append(" TO ").append("*]");
        return this;
    }

    public SolrKit orGreaterThan(String fieldName, String val) {
        queryString.append(" || ").append(fieldName).append(":[").append(val).append(" TO ").append("*]");
        return this;
    }

    public SolrKit andDateGreaterThan(String fieldName, Date val) {
        queryString.append(" && ").append(fieldName).append(":[").append(formatUTCString(val)).append(" TO ")
                .append("*]");
        return this;
    }

    public SolrKit orDateGreaterThan(String fieldName, Date val) {
        queryString.append(" || ").append(fieldName).append(":[").append(formatUTCString(val)).append(" TO ")
                .append("*]");
        return this;
    }

    public SolrKit andLessThan(String fieldName, String val) {
        queryString.append(" && ").append(fieldName).append(":[").append("*").append(" TO ").append(val).append("]");
        return this;
    }

    public SolrKit orLessThan(String fieldName, String val) {
        queryString.append(" && ").append(fieldName).append(":[").append("*").append(" TO ").append(val).append("]");
        return this;
    }

    public SolrKit andDateLessThan(String fieldName, Date val) {
        queryString.append(" && ").append(fieldName).append(":[").append("*").append(" TO ")
                .append(formatUTCString(val)).append("]");
        return this;
    }

    public SolrKit orDateLessThan(String fieldName, Date val) {
        queryString.append(" && ").append(fieldName).append(":[").append("*").append(" TO ")
                .append(formatUTCString(val)).append("]");
        return this;
    }

    public SolrKit andLike(String fieldName, String val, Class clazz) {
        String fieldNameNew = getFieldNameByClass(fieldName, clazz);
        val = getSolrStr(val);
        queryString.append(" && ").append(fieldNameNew).append(":(").append(val).append(")");
        return this;
    }

    public SolrKit orLike(String fieldName, String val) {
        val = getSolrStr(val);
        queryString.append(" || ").append(fieldName).append(":(").append(val).append(")");
        return this;
    }

    public SolrKit andNotLike(String fieldName, String val) {
        val = getSolrStr(val);
        queryString.append(" && ").append("-").append(fieldName).append(":(").append(val).append(")");
        return this;
    }

    public SolrKit orNotLike(String fieldName, String val) {
        val = getSolrStr(val);
        queryString.append(" || ").append("-").append(fieldName).append(":(").append(val).append(")");
        return this;
    }

    public SolrKit andIn(String fieldName, String[] vals) {
        queryString.append(" && ");
        in(fieldName, vals);
        return this;
    }

    private SolrKit in(String fieldName, String[] vals) {
        List<String> list = Arrays.asList(vals);
        in(queryString, fieldName, list);
        return this;
    }

    public SolrKit orIn(String fieldName, List<String> vals) {
        queryString.append(" || ");
        in(queryString, fieldName, vals);
        return this;
    }

    private SolrKit in(StringBuffer queryString, String fieldName, List<String> vals) {
        queryString.append("(");
        inStr(queryString, fieldName, vals);
        queryString.append(")");
        return this;
    }

    private SolrKit inStr(StringBuffer queryString, String fieldName, List<String> vals) {
        int index = 0;
        for (String val : vals) {
            if (0 != index) {
                queryString.append(" || ");
            }
            queryString.append(fieldName).append(":").append(val);
            index++;
        }
        return this;
    }

    public SolrKit andNotIn(String fieldName, String[] vals) {
        List<String> list = Arrays.asList(vals);
        queryString.append("&&(");
        queryString.append("*:* NOT ");
        inStr(queryString, fieldName, list);
        queryString.append(")");
        return this;
    }

    public SolrKit andDateBetween(String fieldName, Date startDate, Date endDate) {
        queryString.append(" && ").append(fieldName).append(":[").append(formatUTCString(startDate)).append(" TO ")
                .append(formatUTCString(endDate)).append("]");
        return this;
    }

    public SolrKit orDateBetween(String fieldName, Date startDate, Date endDate) {
        queryString.append(" || ").append(fieldName).append(":[").append(formatUTCString(startDate)).append(" TO ")
                .append(formatUTCString(endDate)).append("]");
        return this;
    }

    public SolrKit andDateNotBetween(String fieldName, Date startDate, Date endDate) {
        queryString.append(" && ").append("-").append(fieldName).append(":[").append(formatUTCString(startDate))
                .append(" TO ").append(formatUTCString(endDate)).append("]");
        return this;
    }

    public SolrKit orDateNotBetween(String fieldName, Date startDate, Date endDate) {
        queryString.append(" && ").append("-").append(fieldName).append(":[").append(formatUTCString(startDate))
                .append(" TO ").append(formatUTCString(endDate)).append("]");
        return this;
    }

    public SolrKit andBetween(String fieldName, String start, String end) {
        queryString.append(" && ").append(fieldName).append(":[").append(start).append(" TO ").append(end).append("]");
        return this;
    }

    public SolrKit orBetween(String fieldName, String start, String end) {
        queryString.append(" || ").append(fieldName).append(":[").append(start).append(" TO ").append(end).append("]");
        return this;
    }

    public SolrKit andNotBetween(String fieldName, String start, String end) {
        queryString.append(" && ").append("-").append(fieldName).append(":[").append(start).append(" TO ").append(end)
                .append("]");
        return this;
    }

    public SolrKit orNotBetween(String fieldName, String start, String end) {
        queryString.append(" || ").append("-").append(fieldName).append(":[").append(start).append(" TO ").append(end)
                .append("]");
        return this;
    }

    public SolrKit andStartSub() {
        queryString.append(" && (");
        return this;
    }

    public SolrKit orStartSub() {
        queryString.append(" || (");
        return this;
    }

    public SolrKit endSub() {
        queryString.append(")");
        return this;
    }

    public String getSolrStr(String inputStr) {
        String solrStr = "";
        Analyzer analyzer = new IKAnalyzer(true);
        TokenStream ts = null;
        try {
            ts = analyzer.tokenStream("field", new StringReader(inputStr));
            // 获取词元文本属性
            CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
            ts.reset();
            // 迭代获取分词结果
            while (ts.incrementToken()) {
                solrStr = solrStr + " " + term.toString();
            }
            // 关闭TokenStream（关闭StringReader）
            ts.end(); // Perform end-of-stream operations, e.g. set the final offset.
        } catch (Exception e) {

        } finally {
            // 释放TokenStream的所有资源
            if (ts != null) {
                try {
                    ts.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return solrStr;
    }

    public SolrQuery handleQuery() {
        SolrQuery params = new SolrQuery();
        String qryFinalStr = queryString.toString();
        if (qryFinalStr.startsWith(" && ")) {
            qryFinalStr = qryFinalStr.replaceFirst(" && ", "");
        } else if (qryFinalStr.startsWith(" || ")) {
            qryFinalStr = qryFinalStr.replaceFirst(" || ", "");
        }
        // 子查询开头的关联符号
        if (-1 != qryFinalStr.indexOf("( && ")) {
            qryFinalStr = qryFinalStr.replaceAll("\\( \\&\\& ", "(");
        }

        if (-1 != qryFinalStr.indexOf("( || ")) {
            qryFinalStr = qryFinalStr.replaceAll("\\( \\|\\| ", "(");
        }

        if (StringUtils.isBlank(qryFinalStr)) {
            qryFinalStr = "*:*";
        }

        params.set("q", qryFinalStr);

        if (getHighlight()) {
            params.setHighlight(true); // 设置高亮显示
            // 设置高亮显示字段
            for (String fieldName : getHighlightFieldList()) {
                params.addHighlightField(fieldName);
            }
            // 设置前缀
            params.setHighlightSimplePre(getHighlightPre());
            // 设置后缀
            params.setHighlightSimplePost(getHighlightPost());
            // 返回的字符个数
            params.setHighlightFragsize(120);
        }
        if(getFacet()){
            params.setFacet(true);      // 设置使用facet
            params.setFacetMinCount(1); // 设置facet最少的统计数量
            for (String fieldName : getFacetFieldList()) {
                params.addFacetField(fieldName);
            }
        }
        
        if (getRows() != 0) {
            params.setRows(getRows());
        }

        if (getSortList() != null && getSortList().size() > 0) {
            for (Map<String, Object> map : getSortList()) {
                params.addSort(map.get(SolrContants.SORT_MAP_KEY_NAME).toString(),
                        (ORDER) map.get(SolrContants.SORT_MAP_KEY_VALUE));
            }
        }

        return params;
    }

    /**
     * 实体类与SolrInputDocument转换
     * 
     * @param entity
     * @return
     */
    public static SolrInputDocument transformBean2SolrDocument(Object entity) {
        SolrInputDocument doc = new SolrInputDocument();
        List<String> fdNames = ReflactKit.getAllSolrFields(entity.getClass());
        for (String fieldName : fdNames) {
            Object returnObj = ReflactKit.getPropertieValue(fieldName, entity);
            if (returnObj != null && !"".equals(returnObj)) {
                String fieldNameNew = getFieldNameByClass(fieldName, returnObj.getClass());
                doc.addField(fieldNameNew, returnObj);
            }
        }
        return doc;
    }

    /**
     * map与SolrInputDocument转换
     * 
     * @param entity
     * @return
     */
    public static SolrInputDocument transformMap2SolrDocument(Map<String, Object> map) {
        SolrInputDocument doc = new SolrInputDocument();
        for (String key : map.keySet()) {
            Object returnObj = map.get(key);
            if (returnObj != null && !"".equals(returnObj)) {
                String fieldNameNew = getFieldNameByClass(key, returnObj.getClass());
                doc.addField(fieldNameNew, returnObj);
            }
        }
        return doc;
    }

    /**
     * 实体 list 类与SolrInputDocument list转换
     * 
     * @param entity
     * @return
     */
    public static List<SolrInputDocument> transformBeanList2SolrDocumentList(List<?> list) {
        List<SolrInputDocument> inputDocumentList = new ArrayList<SolrInputDocument>();
        SolrInputDocument doc = null;
        for (Object obj : list) {
            doc = transformBean2SolrDocument(obj);
            if (!doc.isEmpty())
                inputDocumentList.add(doc);
        }
        return inputDocumentList;
    }

    /**
     * map list 类与SolrInputDocument list转换
     * 
     * @param entity
     * @return
     */
    public static List<SolrInputDocument> transformMapList2SolrDocumentList(List<Map<String, Object>> list) {
        List<SolrInputDocument> inputDocumentList = new ArrayList<SolrInputDocument>();
        SolrInputDocument doc = null;
        for (Map<String, Object> obj : list) {
            doc = transformMap2SolrDocument(obj);
            if (!doc.isEmpty())
                inputDocumentList.add(doc);
        }
        return inputDocumentList;
    }

    /**
     * document 转换成实体
     * 
     * @param doc
     * @param clzz
     * @return
     */
    public static <T> T solrDocument2Bean(SolrDocument doc, Class<T> clazz, SolrKit kit) {
        if (doc != null) {
            Object obj = null;
            try {
                obj = clazz.newInstance();
                Object docValue = null;
                for (String key : doc.keySet()) {
                    docValue = doc.get(key);
                    key = getOriginalBeanPropertyName(key, docValue.getClass());
                    if (ReflactKit.getAllSolrFields(clazz).contains(key)) { // 剔除没有用solrField注解的field
                        ReflactKit.setPropertieValue(key, obj, docValue);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return (T) obj;
        } else {
            return null;
        }
    }

    /**
     * document 转换成map
     * 
     * @param doc
     * @param clzz
     * @return
     */
    public static Map<String, Object> solrDocument2Map(SolrDocument doc, SolrKit kit) {
        if (doc != null) {
            Map<String, Object> map = new HashMap<String, Object>();
            try {
                Object docValue = null;
                for (String key : doc.keySet()) {
                    docValue = doc.get(key);
                    key = getOriginalBeanPropertyName(key, docValue.getClass());
                    map.put(key, docValue);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return map;
        } else {
            return null;
        }
    }

    /**
     * document 转换成实体
     * 
     * @param doc
     * @param clzz
     * @return
     */
    public static <T> List<T> solrDocumentList2Bean(QueryResponse response, Class<T> clazz, SolrKit kit) {
        SolrDocumentList list = response.getResults();
        List<T> returnList = new ArrayList<T>();
        // 这边进行是否高亮判断，替换
        Map<String, Map<String, List<String>>> highlightMap = response.getHighlighting();
        if (kit.getHighlight()) {
            if (highlightMap != null) {
                for (SolrDocument doc : list) {
                    Map<String, List<String>> docMap = highlightMap.get(doc.getFieldValue(SolrContants.DEFAULT_PK)
                            .toString());
                    if (docMap != null) {
                        for (String fieldName : kit.getHighlightFieldList()) {
                            if (docMap.get(fieldName) != null && docMap.get(fieldName).size() != 0) {
                                doc.setField(fieldName, docMap.get(fieldName).get(0));
                            }
                        }
                    }
                }
            }
        }
        if (list != null) {
            Object obj = null;
            for (SolrDocument doc : list) {
                obj = solrDocument2Bean(doc, clazz, kit);
                returnList.add((T) obj);
            }
            return returnList;
        } else {
            return null;
        }
    }

    /**
     * documentList 转换成List<Map>
     * 
     * @param doc
     * @param clzz
     * @return
     */
    public static List<Map<String, Object>> solrDocumentList2MapList(QueryResponse response, SolrKit kit) {

        SolrDocumentList list = response.getResults();
        // 这边进行是否高亮判断，替换
        Map<String, Map<String, List<String>>> highlightMap = response.getHighlighting();

        if (kit.getHighlight()) {
            if (highlightMap != null) {
                for (SolrDocument doc : list) {
                    Map<String, List<String>> docMap = highlightMap.get(doc.getFieldValue(SolrContants.DEFAULT_PK)
                            .toString());
                    if (docMap != null) {
                        for (String fieldName : kit.getHighlightFieldList()) {
                            if (docMap.get(fieldName) != null && docMap.get(fieldName).size() != 0) {
                                doc.setField(fieldName, docMap.get(fieldName).get(0));
                            }
                        }
                    }
                }
            }
        }

        List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
        if (list != null) {
            Map<String, Object> map = null;
            for (SolrDocument doc : list) {
                map = solrDocument2Map(doc, kit);
                returnList.add(map);
            }
            return returnList;
        } else {
            return null;
        }
    }

    /**
     * 根据class类型获取后缀
     * 
     * @param clazz
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static String getFieldNameByClass(String fieldName, Class clazz) {
        String suffix = "";
        if (!fieldName.equalsIgnoreCase("id")) {
            if (clazz.isAssignableFrom(Integer.class) || clazz.isAssignableFrom(int.class)) {
                suffix = SolrContants.INT_SUFFIX;
            } else if (clazz.isAssignableFrom(Long.class) || clazz.isAssignableFrom(long.class)) {
                suffix = SolrContants.LONG_SUFFIX;
            } else if (clazz.isAssignableFrom(Boolean.class) || clazz.isAssignableFrom(boolean.class)) {
                suffix = SolrContants.BOOLEAN_SUFFIX;
            } else if (clazz.isAssignableFrom(Double.class) || clazz.isAssignableFrom(double.class)) {
                suffix = SolrContants.DOUBLE_SUFFIX;
            } else if (clazz.isAssignableFrom(Float.class) || clazz.isAssignableFrom(float.class)) {
                suffix = SolrContants.FLOAT_SUFFIX;
            } else if (clazz.isAssignableFrom(String.class)) {
                suffix = SolrContants.STRING_SUFFIX;
            } else if (clazz.isAssignableFrom(Short.class) || clazz.isAssignableFrom(short.class)) {
                suffix = SolrContants.STRING_SUFFIX;
            } else if (clazz.isAssignableFrom(Byte.class) || clazz.isAssignableFrom(byte.class)) {
                suffix = SolrContants.STRING_SUFFIX;
            } else if (clazz.isAssignableFrom(Character.class) || clazz.isAssignableFrom(char.class)) {
                suffix = SolrContants.STRING_SUFFIX;
            } else if (clazz.isAssignableFrom(Date.class)) {
                suffix = SolrContants.DATE_SUFFIX;
            } else {
                logger.debug("未查找到类:" + clazz.getClass() + "的相关后缀，默认使用:" + SolrContants.STRING_SUFFIX);
                suffix = SolrContants.STRING_SUFFIX;
            }
        }
        return fieldName + suffix;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static String getOriginalBeanPropertyName(String key, Class clazz) {
        String originalBeanPropertyName = key;
        int keyLength = key.length();
        if (!key.equalsIgnoreCase("id") && !key.equalsIgnoreCase("_version_")) {
            if (clazz.isAssignableFrom(Integer.class) || clazz.isAssignableFrom(int.class)) {
                originalBeanPropertyName = originalBeanPropertyName.substring(0,
                        keyLength - SolrContants.INT_SUFFIX.length());
            } else if (clazz.isAssignableFrom(Long.class) || clazz.isAssignableFrom(long.class)) {
                originalBeanPropertyName = originalBeanPropertyName.substring(0,
                        keyLength - SolrContants.LONG_SUFFIX.length());
            } else if (clazz.isAssignableFrom(Boolean.class) || clazz.isAssignableFrom(boolean.class)) {
                originalBeanPropertyName = originalBeanPropertyName.substring(0, keyLength
                        - SolrContants.BOOLEAN_SUFFIX.length());
            } else if (clazz.isAssignableFrom(Double.class) || clazz.isAssignableFrom(double.class)) {
                originalBeanPropertyName = originalBeanPropertyName.substring(0,
                        keyLength - SolrContants.DOUBLE_SUFFIX.length());
            } else if (clazz.isAssignableFrom(Float.class) || clazz.isAssignableFrom(float.class)) {
                originalBeanPropertyName = originalBeanPropertyName.substring(0,
                        keyLength - SolrContants.FLOAT_SUFFIX.length());
            } else if (clazz.isAssignableFrom(String.class)) {
                originalBeanPropertyName = originalBeanPropertyName.substring(0,
                        keyLength - SolrContants.STRING_SUFFIX.length());
            } else if (clazz.isAssignableFrom(Short.class) || clazz.isAssignableFrom(short.class)) {
                originalBeanPropertyName = originalBeanPropertyName.substring(0,
                        keyLength - SolrContants.STRING_SUFFIX.length());
            } else if (clazz.isAssignableFrom(Byte.class) || clazz.isAssignableFrom(byte.class)) {
                originalBeanPropertyName = originalBeanPropertyName.substring(0,
                        keyLength - SolrContants.STRING_SUFFIX.length());
            } else if (clazz.isAssignableFrom(Character.class) || clazz.isAssignableFrom(char.class)) {
                originalBeanPropertyName = originalBeanPropertyName.substring(0,
                        keyLength - SolrContants.STRING_SUFFIX.length());
            } else if (clazz.isAssignableFrom(Date.class)) {
                originalBeanPropertyName = originalBeanPropertyName.substring(0,
                        keyLength - SolrContants.DATE_SUFFIX.length());
            } else {
                logger.debug("未查找到类:" + clazz.getClass() + "的相关后缀，默认使用:" + SolrContants.STRING_SUFFIX);
                originalBeanPropertyName = originalBeanPropertyName.substring(0,
                        keyLength - SolrContants.DATE_SUFFIX.length());
            }
        }
        return originalBeanPropertyName;
    }

    @Override
    public String toString() {
        return queryString.toString();
    }

    public List<Map<String, Object>> getSortList() {
        return sortList;
    }

    public void setSortList(List<Map<String, Object>> sortList) {
        this.sortList = sortList;
    }

    public static void main(String[] args) {
        Map<String, Object> map = new LinkedHashMap();
        Double d = 99.99d;
        map.put("price", 99.99d);
        for (String key : map.keySet()) {
            Object obj = map.get(key);
            System.out.println(obj.getClass());
        }

    }

}
