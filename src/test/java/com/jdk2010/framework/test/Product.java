package com.jdk2010.framework.test;
import java.util.Date;
import com.jdk2010.framework.solr.annotation.SolrField;

public class Product {
    @SolrField
    private Integer id;
    @SolrField
    private String productName;
    @SolrField
    private Integer productStore;
    @SolrField
    private Integer brandId;
    @SolrField
    private Double price;
    @SolrField
    private Date upTime;
    @SolrField
    private Integer star;

    private Integer isUp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getProductStore() {
        return productStore;
    }

    public void setProductStore(Integer productStore) {
        this.productStore = productStore;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Date getUpTime() {
        return upTime;
    }

    public void setUpTime(Date upTime) {
        this.upTime = upTime;
    }

    public Integer getStar() {
        return star;
    }

    public void setStar(Integer star) {
        this.star = star;
    }

    public Integer getIsUp() {
        return isUp;
    }

    public void setIsUp(Integer isUp) {
        this.isUp = isUp;
    }

}
