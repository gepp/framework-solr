package com.jdk2010.framework.solr.client.support;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;

public class DefaultSolrClient extends AbstractSolrClient implements InitializingBean {

    Logger logger = LoggerFactory.getLogger(DefaultSolrClient.class);

    private String serverUrl;

    private SolrServer server;

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
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
        if (server == null) {
            throw new RuntimeException("无法连接solr服务器！");
        }
    }

    @Override
    public SolrServer getServer() {
        return server;
    }

}
