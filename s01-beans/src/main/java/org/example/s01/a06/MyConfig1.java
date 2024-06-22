package org.example.s01.a06;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * 模拟 @Autowired, @PostConstruct 功能失效
 * <br/>
 * 原因分析:<br/>
 *  context.refresh();大致执行顺序:<br/>
 *      1. 添加 bean工厂后处理器, 2. 添加 bean处理器, 3. 初始化单例
 * <br/>
 *  但配置类这种方式, 会跳过1,2; 而是先创建 MyConfig1 对象,自然注解解析失效
 * @author qlk
 */
@Configuration
public class MyConfig1 {
    private static final Logger log = LoggerFactory.getLogger(MyConfig1.class);

    @Autowired
    public void setApplicationContext(ApplicationContext context) {
        log.debug("注入 ApplicationContext");
    }

    @PostConstruct
    public void init() {
        log.debug("初始化");
    }

    // 添加一个bean工厂的后处理器 会导致注解和拓展  @Autowired 和 @PostConstruct 失效
    @Bean
    public BeanFactoryPostProcessor processor1() {
        return beanFactory -> {
            log.debug("执行 processor1");
        };
    }

}
