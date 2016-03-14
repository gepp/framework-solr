package com.jdk2010.framework.solr.client.support;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.common.cloud.ClusterState;
import org.apache.solr.common.cloud.ZkStateReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class DefaultSolrCloudClient extends AbstractSolrClient implements InitializingBean {

    Logger logger = LoggerFactory.getLogger(DefaultSolrCloudClient.class);

    private CloudSolrServer cloudSolrServer;

    public String getZkHost() {
        return zkHost;
    }

    public void setZkHost(String zkHost) {
        this.zkHost = zkHost;
    }

    public Integer getZkClientTimeout() {
        return zkClientTimeout;
    }

    public void setZkClientTimeout(Integer zkClientTimeout) {
        this.zkClientTimeout = zkClientTimeout;
    }

    public Integer getZkConnectTimeout() {
        return zkConnectTimeout;
    }

    public void setZkConnectTimeout(Integer zkConnectTimeout) {
        this.zkConnectTimeout = zkConnectTimeout;
    }

    public String getCoreName() {
        return coreName;
    }

    public void setCoreName(String coreName) {
        this.coreName = coreName;
    }

    private String zkHost;

    private String coreName;

    private Integer zkClientTimeout;

    private Integer zkConnectTimeout;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtils.isBlank(zkHost)) {
            throw new RuntimeException("zkHost不能为空");
        }
        if (StringUtils.isBlank(coreName)) {
            throw new RuntimeException("coreName不能为空");
        }
        cloudSolrServer = new CloudSolrServer(zkHost);
        cloudSolrServer.setDefaultCollection(coreName);
        if (zkClientTimeout != null) {
            cloudSolrServer.setZkClientTimeout(zkClientTimeout);
        } else {
            cloudSolrServer.setZkClientTimeout(20000);

        }
        if (zkConnectTimeout != null) {
            cloudSolrServer.setZkConnectTimeout(zkConnectTimeout);
        } else {
            cloudSolrServer.setZkConnectTimeout(1000);
        }
        cloudSolrServer.connect();  
//        ZkStateReader zkStateReader = cloudSolrServer.getZkStateReader();
//        ClusterState cloudState = zkStateReader.getClusterState();
//        System.out.println(cloudState);
            
    }

    @Override
    public SolrServer getServer() {
        return cloudSolrServer;
    }

}
