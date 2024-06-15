package org.example.s01.a03;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author qlk
 */
@Component
public class LifeCycleBean {

    private static final Logger log = LoggerFactory.getLogger(LifeCycleBean.class);

    // 0
    public LifeCycleBean() {
        log.debug("构造器实例化");
    }

    // 1
    @Autowired
    public void autowired(@Value("${Path}") String home) {
        log.debug("依赖注入: {}", home);
    }

    // 2
    @PostConstruct
    public void init() {
        log.debug("初始化");
    }

    // 3
    @PreDestroy
    public void destroy() {
        log.debug("销毁前");
    }
}
