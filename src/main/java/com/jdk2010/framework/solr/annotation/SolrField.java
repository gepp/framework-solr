package com.jdk2010.framework.solr.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SolrField {
    // 尽量将所有仅用于搜索，而不用于实际返回的字段设置stored="false"
    public boolean stored() default true;
    // 尽量将所有仅用于返回，而不用于搜索的字段设置indexed="false"
    public boolean indexed() default true;
}
