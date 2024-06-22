package org.example.s01.a05;

import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

/**
 * @author qlk
 */
public class A05Application {
    private static final Logger log = LoggerFactory.getLogger(A05Application.class);

    public static void main(String[] args) throws IOException {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean("config", Config.class);
/*        // 1. ConfigurationClassPostProcessor 的bean工厂后处理器
        // 扫描 @ComponentScan
        // 扫描 @Bean
        // 扫描 @Import
        // 扫描 @ImportResource
        context.registerBean(ConfigurationClassPostProcessor.class);

        // 2. MapperScannerConfigurer 的bean工厂后处理器
        // 扫描 @Mapper
        context.registerBean(MapperScannerConfigurer.class, bd -> {
            bd.getPropertyValues().add("basePackage", "org.example.s01.a05.mapper");
        });*/

        // 3. 模拟 ConfigurationClassPostProcessor 的工作流程
        // 3.1 查找某个类上是否具有某个注解
//        ComponentScan annotation = AnnotationUtils.findAnnotation(Config.class, ComponentScan.class);
//
//        if (annotation != null) {
//            for (String pkg : annotation.basePackages()) {
//                System.out.println(pkg);
//                // 3.2 根据资源路径获取bean定义 (二进制)
//                // org.example.s01.a05.component --> classpath*:org/example/s01/a05/component/**/*.class
//                String path = "classpath*:" + pkg.replace(".", "/") + "/**/*.class";
//                System.out.println("path = " + path);
//                Resource[] resources = context.getResources(path);
//                // 3.3 查看是否添加了 @Component 注解
//                // 使用 CachingMetadataReaderFactory 扫描类的元信息
//                CachingMetadataReaderFactory factory = new CachingMetadataReaderFactory();
//                // 根据bean定义生成一个name
//                AnnotationBeanNameGenerator generator = new AnnotationBeanNameGenerator();
//
//                for (Resource resource : resources) {
////                    System.out.println("resource = " + resource);
//                    MetadataReader reader = factory.getMetadataReader(resource);
//                    System.out.println("类名: " + reader.getClassMetadata().getClassName());
//                    AnnotationMetadata annotationMetadata = reader.getAnnotationMetadata();
//                    System.out.println("是否有@Component: " + annotationMetadata.hasAnnotation(Component.class.getName()));
//                    System.out.println("是否有@Component的派生注解: " + annotationMetadata.hasMetaAnnotation(Component.class.getName()));
//                    // 存在就添加bean定义到beanfactory
//                    if (annotationMetadata.hasAnnotation(Component.class.getName()) ||
//                            annotationMetadata.hasMetaAnnotation(Component.class.getName())) {
//                        AbstractBeanDefinition bd = BeanDefinitionBuilder
//                                .genericBeanDefinition(reader.getClassMetadata().getClassName())
//                                .getBeanDefinition();
//                        DefaultListableBeanFactory beanFactory = context.getDefaultListableBeanFactory();
//                        String name = generator.generateBeanName(bd, beanFactory);
//                        beanFactory.registerBeanDefinition(name, bd);
//                    }
//                }
//
//
//            }
//        }


        // 3.4 封装上述代码,重新注册自定义的beanfactory后处理器
//        context.registerBean(ComponentScanPostProcessor.class);

        // 4. 模拟实现 @Configuration + @Bean 的bean定义方式 (实例工厂方法)
        // 补充:  CachingMetadataReaderFactory 类获取类的元信息是不走类加载的, 效率比反射高
//        CachingMetadataReaderFactory factory = new CachingMetadataReaderFactory();
//        MetadataReader reader = factory.getMetadataReader(new ClassPathResource("org/example/s01/a05/Config.class"));
//        Set<MethodMetadata> methods = reader.getAnnotationMetadata().getAnnotatedMethods(Bean.class.getName());
//        for (MethodMetadata method : methods) {
//            System.out.println(method);
//            // 读取@Bean注解上的初始化方法
//            String initMethod = method.getAnnotationAttributes(Bean.class.getName()).get("initMethod").toString();
//
//            // 4.1根据方法信息, 生成bean定义 (实例工厂方法)
//            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
//            builder.setFactoryMethodOnBean(method.getMethodName(), "config");
//            builder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);  // 指定工厂方法参数的自动装配
//            if (!initMethod.isEmpty()) {
//                builder.setInitMethodName(initMethod);
//            }
//            AbstractBeanDefinition bd = builder.getBeanDefinition();
//            context.getDefaultListableBeanFactory().registerBeanDefinition(method.getMethodName(), bd);
//
//        }

        // 4.1 上述代码替换自定义的beanfactory后处理器
        context.registerBean(AtBeanPostProcessor.class);

        // 4.2 替换自定义的Mapper接口处理器
        context.registerBean(MapperPostProcessor.class);




        context.refresh();

        for (String name : context.getBeanDefinitionNames()) {
            System.out.println(name);
        }

        context.close();
    }
}
