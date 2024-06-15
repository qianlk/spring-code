package org.example.s01.a04;

import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;
import org.springframework.context.support.GenericApplicationContext;

/**
 * bean后处理器的作用
 * @author qlk
 */
public class A04Application {
    public static void main(String[] args) {

        // 干净的容器, 没有注册其他内容
        GenericApplicationContext context = new GenericApplicationContext();
//        for (String name : context.getBeanDefinitionNames()) {
//            System.out.println("name = " + name);
//        }

        // 向干净的容器注入是哪个bean
        context.registerBean("bean1", Bean1.class);
        context.registerBean("bean2", Bean2.class);
        context.registerBean("bean3", Bean3.class);
        context.registerBean("bean4", Bean4.class);

        // 添加后处理器
        context.getDefaultListableBeanFactory().setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver());  // 解析 @Value 获取值
        context.registerBean(AutowiredAnnotationBeanPostProcessor.class);  // 解析 @Autowired @Resource
        context.registerBean(CommonAnnotationBeanPostProcessor.class);  // 解析 @Resource @PostConstruct @PreDestroy

        // springboot中的 @ConfigurationProperties
        ConfigurationPropertiesBindingPostProcessor.register(context.getDefaultListableBeanFactory());  // 给 bean工厂添加 解析 @ConfigurationProperties 的后处理器

        // 调用refresh方法后,工厂才生效, 可以执行bean工厂的后处理器和bean后处理器, 添加单例bean
        context.refresh();

        System.out.println(context.getBean("bean4"));

        context.close();

    }
}
