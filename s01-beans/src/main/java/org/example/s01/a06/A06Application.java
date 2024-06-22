package org.example.s01.a06;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.support.GenericApplicationContext;

/**
 * Aware接口以及 InitializingBean接口
 * @author qlk
 */
public class A06Application {
    private static final Logger log = LoggerFactory.getLogger(A06Application.class);

    public static void main(String[] args) {
        /*
        1. Aware接口用于注入一些与容器相关的服务,例如:
            a. BeanNameAware 注入bean的名字
            b. BeanFactoryAware 注入BeanFactory容器
            c. ApplicationContextAware 注入 ApplicationContext 容器
            d. EmbeddedValueResolverAware 解析 ${}
         */

        //
        GenericApplicationContext context = new GenericApplicationContext();
//        context.registerBean("myBean", MyBean.class);
//        context.registerBean("myConfig1", MyConfig1.class);  // 失效
        context.registerBean("myConfig2", MyConfig2.class);  // 使用aware,防止失效

        /*
        2. b,c,d的功能需要用 @Autowired 就能实现, 为啥还要用 Aware 接口
        简单说:
            @Autowired的解析需要用到 bean 后处理器, 才能实现拓展功能
            而 Aware 接口属于内置功能, 不加任何扩展就可实现
        某些情况下,拓展功能会失效,而内置功能不失效

         */

        // 添加拓展的后处理器,才能解析  MyBean中的 @Autowired 和
        // @PostConstruct (InitDestroyAnnotationBeanPostProcessor负责解析,
        // CommonAnnotationBeanPostProcessor继承自InitDestroyAnnotationBeanPostProcessor)
        // @Bean, @Configuration等 (ConfigurationClassPostProcessor负责解析)
        context.registerBean(AutowiredAnnotationBeanPostProcessor.class);
        context.registerBean(CommonAnnotationBeanPostProcessor.class);
        context.registerBean(ConfigurationClassPostProcessor.class);

        // context.refresh();大致执行顺序:
        // 1. 添加 bean工厂后处理器, 2. 添加 bean处理器, 3. 初始化单例
        // 3.1 执行依赖注入拓展 (@Value, @Autowired)
        // 3.2 执行初始化拓展 (@PostConstruct)
        // 3.3 执行 Aware 和 InitializingBean
        context.refresh();
        context.close();


    }
}
