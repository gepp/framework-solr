package com.jdk2010.framework.solr.client.support;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;

import com.jdk2010.framework.solr.client.SolrClient;
import com.jdk2010.framework.solr.util.Page;
import com.jdk2010.framework.solr.util.SolrKit;
import com.jdk2010.framework.test.Product;

public class DefaultSolrClient implements SolrClient, InitializingBean {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private String serverUrl;

    private SolrServer server;

    private String defaultCoreName;

    public String getDefaultCoreName() {
        return defaultCoreName;
    }

    public void setDefaultCoreName(String defaultCoreName) {
        this.defaultCoreName = defaultCoreName;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    @Override
    public boolean saveOrUpdateBean(Object bean) {
        boolean status = true;
        try {
            SolrInputDocument doc = SolrKit.transformBean2SolrDocument(bean);
            logger.debug("doc:"+doc);
            for (String key : doc.keySet()) {
                SolrInputField field = doc.get(key);
                System.out.println(field.getValue().getClass());
            }
            if (!doc.isEmpty()) {
                UpdateResponse response = server.add(doc);
                server.commit();
                if (response.getStatus() == 0) {
                    status = false;
                }
            } else {
                status = false;
            }
        } catch (Exception e) {
            status = false;
            throwException(e);
        }
        return status;
    }

    @Override
    public boolean saveOrUpdateBean(List<?> list) {
        boolean status = true;
        try {
            List<SolrInputDocument> docList = SolrKit.transformBeanList2SolrDocumentList(list);
            UpdateResponse response = server.add(docList);
            server.commit();
            if (response.getStatus() == 0) {
                status = false;
            }
        } catch (Exception e) {
            status = false;
            throwException(e);
        }
        return status;
    }

    @Override
    public boolean saveOrUpdateMap(Map<String, Object> map) {
        boolean status = true;
        try {
            SolrInputDocument doc = SolrKit.transformMap2SolrDocument(map);
            logger.debug("doc:"+doc);
            if (!doc.isEmpty()) {
                UpdateResponse response = server.add(doc);
                server.commit();
                if (response.getStatus() == 0) {
                    status = false;
                }
            } else {
                status = false;
            }
        } catch (Exception e) {
            status = false;
            throwException(e);
        }
        return status;
    }

    @Override
    public boolean saveOrUpdateMap(List<Map<String, Object>> list) {
        boolean status = true;
        try {
            List<SolrInputDocument> docList = SolrKit.transformMapList2SolrDocumentList(list);
            UpdateResponse response = server.add(docList);
            server.commit();
            if (response.getStatus() == 0) {
                status = false;
            }
        } catch (Exception e) {
            status = false;
            throwException(e);
        }
        return status;
    }

    @Override
    public boolean deleteByKey(String fieldName, String fieldValue) {
        boolean status = true;
        try {
            UpdateResponse response = server.deleteByQuery(fieldName + ":" + fieldValue);
            server.commit();
            if (response.getStatus() == 0) {
                status = false;
            }
        } catch (Exception e) {
            status = false;
            throwException(e);
        }
        return status;
    }

    @Override
    public boolean deleteAll() {
        boolean status = true;
        try {
            UpdateResponse response = server.deleteByQuery("*:*");
            server.commit();
            if (response.getStatus() == 0) {
                status = false;
            }
        } catch (Exception e) {
            status = false;
            throwException(e);
        }
        return status;
    }

    @Override
    public boolean deleteByKeys(String fieldName, List<String> fieldValues) {
        boolean status = true;
        StringBuffer sb = new StringBuffer();
        try {
            for (String fieldValue : fieldValues) {
                sb.append(fieldName + ":" + fieldValue + " ");
            }
            UpdateResponse response = server.deleteByQuery(sb.toString());
            server.commit();
            if (response.getStatus() == 0) {
                status = false;
            }
        } catch (Exception e) {
            status = false;
            throwException(e);
        }
        return status;
    }

    @Override
    public boolean deleteByKeyAll(String fieldName) {
        boolean status = true;
        try {
            UpdateResponse response = server.deleteByQuery(fieldName + ":*");
            server.commit();
            if (response.getStatus() == 0) {
                status = false;
            }
        } catch (Exception e) {
            status = false;
            throwException(e);
        }
        return status;
    }

    @Override
    public boolean deleteByQuery(SolrKit kit) {
        boolean status = true;
        try {
            UpdateResponse response = server.deleteByQuery(kit.toString());
            server.commit();
            if (response.getStatus() == 0) {
                status = false;
            }
        } catch (Exception e) {
            status = false;
            throwException(e);
        }
        return status;
    }

    @Override
    public <T> T queryForObject(SolrKit kit, Class<T> clazz) {
        List<T> list = queryForObjectList(kit, clazz);
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public <T> List<T> queryForObjectList(SolrKit kit, Class<T> clazz) {
        SolrQuery params = kit.handleQuery();
        QueryResponse response = null;
        List<T> list = null;
        try {
            response = server.query(params);
            SolrDocumentList docList = response.getResults();
            list = SolrKit.solrDocumentList2Bean(docList, clazz);
        } catch (SolrServerException e) {
            throwException(e);
        }
        return list;
    }

    @Override
    public Map<String, Object> queryForObject(SolrKit kit) {
        List<Map<String, Object>> list = queryForObjectList(kit);
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> queryForObjectList(SolrKit kit) {
        SolrQuery params = kit.handleQuery();
        QueryResponse response = null;
        List<Map<String,Object>> list = null;
        try {
            response = server.query(params);
            SolrDocumentList docList = response.getResults();
            list = SolrKit.solrDocumentList2MapList(docList);
        } catch (SolrServerException e) {
            throwException(e);
        }
        return list;
    }

    @Override
    public Page queryForPageList(SolrKit kit, Page page) {
        List<Map<String, Object>> results = null;
        Page resultPage = null;
        // SolrQuery params = kit.handleQuery();
        // params.set("start", (page.getPageIndex() - 1) * page.getPageSize());
        // params.set("rows", page.getPageSize());
        // if (StringUtils.isNotBlank(page.getSort())) {
        // params.set("sort", page.getSort());
        // }
        // QueryResponse response = null;
        // try {
        // logger.info("SolyQuery WithPage:" + params.toString());
        // response = server.query(params);
        // results = transformSolrDocumentList2List(response.getResults());
        // resultPage = new Page();
        // page.setPageIndex(page.getPageIndex());
        // page.setTotalCount(response.getResults().size());
        // page.setList(results);
        // } catch (SolrServerException e) {
        // logger.error("SolrServerException", e);
        // }

        return resultPage;
    }

    @Override
    public <T> Page queryForPageList(SolrKit kit, Page page, Class<T> clazz) {
        List<T> results = null;
        Page resultPage = null;
        SolrQuery params = kit.handleQuery();
        params.set("start", (page.getPageIndex() - 1) * page.getPageSize());
        params.set("rows", page.getPageSize());
        if (StringUtils.isNotBlank(page.getSort())) {
            params.set("sort", page.getSort());
        }
        QueryResponse response = null;
        try {
            response = server.query(params);
            results = (List<T>) response.getBeans(clazz);
            resultPage = new Page();
            page.setPageIndex(page.getPageIndex());
            page.setTotalCount(response.getResults().size());
            page.setList(results);
        } catch (SolrServerException e) {
            logger.error("SolrServerException", e);
        }

        return resultPage;
    }

    protected void throwException(Exception e) {
        if (e instanceof DataAccessException) {
            throw (DataAccessException) e;
        } else if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
        } else {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtils.isEmpty(serverUrl)) {
            throw new RuntimeException("请配置solr服务器地址！");
        }
        server = new HttpSolrServer(serverUrl);
        // server.ping();
        if (server == null) {
            throw new RuntimeException("无法连接solr服务器！");
        }
        logger.debug("solr连接成功！");
    }

}
