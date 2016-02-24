package com.jdk2010.framework.solr.client;

import java.util.List;
import java.util.Map;

import com.jdk2010.framework.solr.util.Page;
import com.jdk2010.framework.solr.util.SolrKit;

public interface SolrClient {

    /**
     * 新增或者修改实体
     * 
     * @param obj
     * @return
     */
    public boolean saveOrUpdateBean(Object bean);

    /**
     * 批量新增修改实体
     * 
     * @param list
     * @return
     */
    public boolean saveOrUpdateBean(List<?> list);

    public boolean saveOrUpdateMap(Map<String, Object> map);

    public boolean saveOrUpdateMap(List<Map<String, Object>> list);

    /**
     * 根据 filedName fieldValue 删除 doc
     * 
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public boolean deleteByKey(String fieldName, String fieldValue);

    /**
     * 根据 filedName fieldValue 批量删除 doc
     * 
     * @param fieldName
     * @param fieldValues
     * @return
     */
    public boolean deleteByKeys(String fieldName, List<String> fieldValues);

    /**
     * 根据 filedName 删除全部
     * 
     * @param fieldName
     * @return
     */
    public boolean deleteByKeyAll(String fieldName);

    /**
     * 根据条件删除
     * 
     * @param kit
     * @return
     */
    public boolean deleteByQuery(SolrKit kit);

    public boolean deleteAll();

    /**
     * 根据条件查询 返回class对象
     * 
     * @param kit
     * @param clazz
     * @return
     */
    public <T> T queryForObject(SolrKit kit, Class<T> clazz);

    /**
     * 根据条件查询 返回class对象
     * 
     * @param kit
     * @param clazz
     * @return
     */
    public <T> List<T> queryForObjectList(SolrKit kit, Class<T> clazz);

    /**
     * 根据条件查询 返回Map对象
     * 
     * @param kit
     * @return
     */
    public Map<String, Object> queryForObject(SolrKit kit);

    /**
     * 根据条件查询 返回List<Map>对象
     * 
     * @param kit
     * @return
     */
    public List<Map<String, Object>> queryForObjectList(SolrKit kit);

    public Page queryForPageList(SolrKit kit, Page page);

    public <T> Page queryForPageList(SolrKit kit, Page page, Class<T> clazz);

}
