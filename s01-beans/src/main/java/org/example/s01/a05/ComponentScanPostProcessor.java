package org.example.s01.a05;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 抽取A05Application.class的代码
 * 封装成处理 @ComponentScan 注解的bean工厂后处理器
 * @author qlk
 */
public class ComponentScanPostProcessor implements BeanFactoryPostProcessor {
    // 执行 context.refresh() 后执行
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        try {
            ComponentScan annotation = AnnotationUtils.findAnnotation(Config.class, ComponentScan.class);

            if (annotation != null) {
                for (String pkg : annotation.basePackages()) {
                    System.out.println(pkg);
                    // 3.2 根据资源路径获取bean定义 (二进制)
                    // org.example.s01.a05.component --> classpath*:org/example/s01/a05/component/**/*.class
                    String path = "classpath*:" + pkg.replace(".", "/") + "/**/*.class";
                    System.out.println("path = " + path);
                    Resource[] resources = new PathMatchingResourcePatternResolver().getResources(path);
                    // 3.3 查看是否添加了 @Component 注解
                    // 使用 CachingMetadataReaderFactory 扫描类的元信息
                    CachingMetadataReaderFactory factory = new CachingMetadataReaderFactory();
                    // 根据bean定义生成一个name
                    AnnotationBeanNameGenerator generator = new AnnotationBeanNameGenerator();

                    for (Resource resource : resources) {
                        MetadataReader reader = factory.getMetadataReader(resource);
                        System.out.println("类名: " + reader.getClassMetadata().getClassName());
                        AnnotationMetadata annotationMetadata = reader.getAnnotationMetadata();
                        System.out.println("是否有@Component: " + annotationMetadata.hasAnnotation(Component.class.getName()));
                        System.out.println("是否有@Component的派生注解: " + annotationMetadata.hasMetaAnnotation(Component.class.getName()));
                        // 存在就添加bean定义到beanfactory
                        if (annotationMetadata.hasAnnotation(Component.class.getName()) ||
                                annotationMetadata.hasMetaAnnotation(Component.class.getName())) {
                            AbstractBeanDefinition bd = BeanDefinitionBuilder
                                    .genericBeanDefinition(reader.getClassMetadata().getClassName())
                                    .getBeanDefinition();
                            if (configurableListableBeanFactory instanceof DefaultListableBeanFactory beanFactory) {
                                String name = generator.generateBeanName(bd, beanFactory);
                                beanFactory.registerBeanDefinition(name, bd);
                            }

                        }
                    }


                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
