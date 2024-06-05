package org.example.s01.a02;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import javax.annotation.Resource;

/**
 * @author qlk
 */
public class TestBeanFactory {

    public static void main(String[] args) {
        /*
        1. DefaultListableBeanFactory
            只做bean定义的注册,功能较为简单
         */
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // bean定义
        AbstractBeanDefinition beanDefinition =
                BeanDefinitionBuilder.genericBeanDefinition(Config.class)
                .setScope("singleton")
                .getBeanDefinition();
        // bean注册
        beanFactory.registerBeanDefinition("config", beanDefinition);

//        for (String name : beanFactory.getBeanDefinitionNames()) {
//            System.out.println(name);
//        }

        /*
        2. AnnotationConfigUtils.registerAnnotationConfigProcessors

        org.springframework.context.annotation.internalConfigurationAnnotationProcessor
        org.springframework.context.annotation.internalAutowiredAnnotationProcessor
        org.springframework.context.annotation.internalCommonAnnotationProcessor
        org.springframework.context.event.internalEventListenerProcessor
        org.springframework.context.event.internalEventListenerFactory

            - 给 beanFactory 添加解析注解的 后处理器(只是加入到了bean工厂) ,功能扩展
            - bean工厂后处理器 需要执行 postProcessBeanFactory 方法才能使用
            - beanFactory 后处理器主要功能, 补充一些 bean 定义
         */
        AnnotationConfigUtils.registerAnnotationConfigProcessors(beanFactory);

        beanFactory.getBeansOfType(BeanFactoryPostProcessor.class).values().
                forEach(beanFactoryPostProcessor -> {
                    // 真正执行解析注解的方法
                    beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);
                });

//        for (String name : beanFactory.getBeanDefinitionNames()) {
//            System.out.println(name);
//        }

        /*
        3. Bean 后处理器,针对 bean 的生命周期的各个阶段提供扩展,(比如 依赖注入)
            例如:
             @Autowired:  AutowiredAnnotationBeanPostProcessor
             @Resource: CommonAnnotationBeanPostProcessor
         */

//        beanFactory.getBeansOfType(BeanPostProcessor.class).values()
//                .forEach(beanPostProcessor -> {
//                    System.out.println("beanPostProcessor = >>> " + beanPostProcessor);
//                    beanFactory.addBeanPostProcessor(beanPostProcessor);
//                });

        // 修改 bean后处理器的执行顺序
        beanFactory.getBeansOfType(BeanPostProcessor.class).values().stream()
                // 比较器排序
                /*
                比较器是在
                AnnotationConfigUtils.registerAnnotationConfigProcessors(beanFactory);
                时添加的
                AnnotationAwareOrderComparator.INSTANCE

                比较是通过 bean后处理器的 order 属性
                    例如:
                   1.
                        public CommonAnnotationBeanPostProcessor() {
                            setOrder(Ordered.LOWEST_PRECEDENCE - 3);
                            ...
                        }
                   2.
                        AutowiredAnnotationBeanPostProcessor
                         ...
                         private int order = Ordered.LOWEST_PRECEDENCE - 2;
                 */
                .sorted(beanFactory.getDependencyComparator())
                .forEach(beanPostProcessor -> {
                    System.out.println("beanPostProcessor = >>> " + beanPostProcessor);
                    beanFactory.addBeanPostProcessor(beanPostProcessor);
                });

        // 打印Order优先级
        System.out.println("Common:" + (Ordered.LOWEST_PRECEDENCE - 3));
        System.out.println("Autowired:" + (Ordered.LOWEST_PRECEDENCE - 2));

        for (String name : beanFactory.getBeanDefinitionNames()) {
            System.out.println(name);
        }

        // 提前创建
//        beanFactory.preInstantiateSingletons();
        System.out.println(">>>>>>>>>");
        // 依赖注入
        System.out.println(beanFactory.getBean(Bean1.class).getBean2());

        // @Autowired和@Resource
        System.out.println(beanFactory.getBean(Bean1.class).getInter());

        /*
        小结:
        a. BeanFactory 不会做的事情
            1. 不会主动调用 BeanFactory 后处理器
            2. 不会主动添加 Bean 后处理器
            3. 不会主动初始化单例
            4. 不会解析 BeanFactory ,
            5. 不会解析 ${} #{}

         b. Bean 后处理器 有先后顺序

         */


    }

    @Configuration
    static class Config {

        @Bean
        public Bean1 bean1() {
            return new Bean1();
        }

        @Bean
        public Bean2 bean2() {
            return new Bean2();
        }

        @Bean
        public Bean3 bean33() {
            return new Bean3();
        }

        @Bean
        public Bean4 bean4() {
            return new Bean4();
        }
    }

    static class Bean1 {
        private static Logger log = LoggerFactory.getLogger(Bean1.class);

        @Resource
        private Bean2 bean2;

        /*
        @Autowired优先按类型匹配
            如果存在多个相同类型的类,需要方式让其按名称匹配
                - 指定 @Qualifier("bean3")
                - 修改 private Inter inter; 为  private Inter bean3;
         */
//        @Autowired
//        @Qualifier("bean3")
//        private Inter inter;

//        @Autowired
//        private Inter bean33;

        /*
        @Autowired
        @Resource(name = "bean4")
        同时使用,会注册成bean33
        原因是
            beanFactory::addBeanPostProcessor
            添加bean后处理的顺序决定其优先级,先加入的优先级高
                解析 @Autowired 的后处理器, 先添加

                    beanPostProcessor = >>> org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor@2b91004a
                    beanPostProcessor = >>> org.springframework.context.annotation.CommonAnnotationBeanPostProcessor@20ccf40b


         可以使用比较器,来修改bean后处理器的执行顺序,当 CommonAnnotationBeanPostProcessor先注册,那么解析的结果将是 @Resource

         */
        @Autowired
        @Resource(name = "bean4")
        private Inter bean33;

        public Inter getInter() {
            return bean33;
        }

        public Bean1() {
            log.debug("构造bean1");
        }

        public Bean2 getBean2() {
            return bean2;
        }

    }

    static class Bean2 {
        private static Logger log = LoggerFactory.getLogger(Bean2.class);

        public Bean2() {
            log.debug("构造bean2");
        }
    }

    interface Inter {

    }

    static class Bean3 implements Inter {

    }

    static class Bean4 implements Inter {

    }
}
