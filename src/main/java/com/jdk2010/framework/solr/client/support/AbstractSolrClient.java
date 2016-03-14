package com.jdk2010.framework.solr.client.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;

import com.jdk2010.framework.solr.client.SolrClient;
import com.jdk2010.framework.solr.util.Page;
import com.jdk2010.framework.solr.util.SolrKit;

public abstract class AbstractSolrClient implements SolrClient {

    Logger logger = LoggerFactory.getLogger(AbstractSolrClient.class);

    public abstract SolrServer getServer();


    @Override
    public boolean saveOrUpdateBean(Object bean) {
        boolean status = true;
        try {
            SolrInputDocument doc = SolrKit.transformBean2SolrDocument(bean);
            if (!doc.isEmpty()) {
                UpdateResponse response = getServer().add(doc);
                getServer().commit();
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
            UpdateResponse response = getServer().add(docList);
            getServer().commit();
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
            logger.debug("doc:" + doc);
            if (!doc.isEmpty()) {
                UpdateResponse response = getServer().add(doc);
                getServer().commit();
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
            UpdateResponse response = getServer().add(docList);
            getServer().commit();
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
            UpdateResponse response = getServer().deleteByQuery(fieldName + ":" + fieldValue);
            getServer().commit();
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
            UpdateResponse response = getServer().deleteByQuery("*:*");
            getServer().commit();
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
            UpdateResponse response = getServer().deleteByQuery(sb.toString());
            getServer().commit();
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
            UpdateResponse response = getServer().deleteByQuery(fieldName + ":*");
            getServer().commit();
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
            UpdateResponse response = getServer().deleteByQuery(kit.toString());
            getServer().commit();
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
    public <T> List<T> queryForObjectList(SolrKit kit, Class<T> clazz) {
        SolrQuery solrQuery = kit.handleQuery();
        QueryResponse response = null;
        List<T> list = null;
        try {
            response = getServer().query(solrQuery);
            list = SolrKit.solrDocumentList2Bean(response, clazz, kit);
        } catch (SolrServerException e) {
            throwException(e);
        }
        return list;
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
        SolrQuery solrQuery = kit.handleQuery();
        QueryResponse response = null;
        List<Map<String, Object>> list = null;
        try {
            response = getServer().query(solrQuery);
            list = SolrKit.solrDocumentList2MapList(response, kit);
        } catch (SolrServerException e) {
            throwException(e);
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> queryForObjectList(SolrKit kit, int rows) {
        kit.setRows(rows);
        return queryForObjectList(kit);
    }

    @Override
    public Page queryForPageList(SolrKit kit, Page page) {
        List<Map<String, Object>> results = null;
        SolrQuery params = kit.handleQuery();
        params.set("start", (page.getPageIndex() - 1) * page.getPageSize());
        params.set("rows", page.getPageSize());
        QueryResponse response = null;
        try {
            response = getServer().query(params);
            results = SolrKit.solrDocumentList2MapList(response, kit);
            page.setTotalCount(response.getResults().getNumFound());
            page.setList(results);
        } catch (SolrServerException e) {
            results = new ArrayList<Map<String, Object>>();
            page.setTotalCount(0);
            page.setList(results);
            logger.error("SolrServerException", e);
        }

        return page;
    }

    @Override
    public <T> Page queryForPageList(SolrKit kit, Page page, Class<T> clazz) {
        List<T> results = null;
        SolrQuery solrQuery = kit.handleQuery();
        solrQuery.set("start", (page.getPageIndex() - 1) * page.getPageSize());
        solrQuery.set("rows", page.getPageSize());
        QueryResponse response = null;
        try {
            response = getServer().query(solrQuery);
            results = SolrKit.solrDocumentList2Bean(response, clazz, kit);
            page.setTotalCount(response.getResults().getNumFound());
            page.setList(results);
        } catch (SolrServerException e) {
            results = new ArrayList<T>();
            page.setTotalCount(0);
            page.setList(results);
            logger.error("SolrServerException", e);
        }

        return page;
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
}
