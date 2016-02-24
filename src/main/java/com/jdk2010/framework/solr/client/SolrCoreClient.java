package com.jdk2010.framework.solr.client;

public interface SolrCoreClient {
    /**
     * 创建core
     * @param coreName
     * @return
     */
    public boolean createCore(String coreName);

    /**
     * 重新加载core
     * @param coreName
     * @return
     */
    public  boolean reloadCore(String coreName);
    
    /**
     * 重命名core
     * @param coreName
     * @param newCoreName
     * @return
     */
    public  boolean renameCore(String coreName,String newCoreName);
}
