package com.jdk2010.framework.solr.client.support;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.apache.solr.common.util.NamedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;

import com.jdk2010.framework.solr.client.SolrCoreClient;

public class DefaultSolrCoreClient implements SolrCoreClient,InitializingBean{

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private String serverUrl;

    private SolrServer server;

    private String defaultCoreName;
    
    @Override
    public boolean createCore(String coreName) {
        boolean createStatus = true;

        if (StringUtils.isEmpty(defaultCoreName)) {
            defaultCoreName = "default_base";
        }
        NamedList<Object> list = null;
        // 获得solr.xml配置好的cores作为默认，获得默认core的路径
        try {
            list = CoreAdminRequest.getStatus(defaultCoreName, server).getCoreStatus().get(defaultCoreName);
            String path = (String) list.get("instanceDir");
            // 获得solrhome,也就是solr放置索引的主目录
            String solrHome = path.substring(0, path.indexOf(defaultCoreName));
            File corePath = new File(solrHome + File.separator + coreName);
            if (!corePath.exists()) {
                corePath.mkdir();
            }
            // 建立新core下的conf文件夹
            File confPath = new File(corePath.getAbsolutePath() + File.separator + "conf/");
            if (!confPath.exists()) {
                confPath.mkdir();
            }
            // 将默认core下conf里的solrconfig.xml和schema.xml拷贝到新core的conf下。这步是必须的
            // 因为新建的core solr会去其conf文件夹下找这两个文件，如果没有就会报错，新core则不会创建成功
            FileUtils.copyFile(new File(path + "conf/solrconfig.xml"), new File(confPath.getAbsolutePath()
                    + File.separator + "solrconfig.xml"));
            FileUtils.copyFile(new File(path + "conf/schema.xml"), new File(confPath.getAbsolutePath() + File.separator
                    + "schema.xml"));

            // 创建新core,同时会把新core的信息添加到solr.xml里
            CoreAdminResponse response = CoreAdminRequest.createCore(coreName, coreName, server);
            if (response.getStatus() == 0) {
                createStatus = false;
            }
        } catch (SolrServerException e) {
            createStatus = false;
            throwException(e);
        } catch (IOException e) {
            createStatus = false;
            throwException(e);
        }
        return createStatus;
    }

    @Override
    public boolean reloadCore(String coreName) {
        boolean status = true;
        try {
            CoreAdminResponse response = CoreAdminRequest.reloadCore(coreName, server);
            if (response.getStatus() == 0) {
                status = false;
            }
        } catch (SolrServerException e) {
            status = false;
            throwException(e);
        } catch (IOException e) {
            status = false;
            throwException(e);
        }
        return status;
    }

    @Override
    public boolean renameCore(String coreName, String newCoreName) {
        boolean status = true;
        try {
            CoreAdminResponse response = CoreAdminRequest.renameCore(coreName, newCoreName, server);
            if (response.getStatus() == 0) {
                status = false;
            }
        } catch (SolrServerException e) {
            status = false;
            e.printStackTrace();
        } catch (IOException e) {
            status = false;
            e.printStackTrace();
        }
        return status;
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
        //server.ping();
        if (server==null) {
            throw new RuntimeException("无法连接solr服务器！");
        }
        logger.debug("solr连接成功！");
    }
}
