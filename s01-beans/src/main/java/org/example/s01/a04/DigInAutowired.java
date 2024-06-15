package org.example.s01.a04;

import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.type.MethodMetadata;

import javax.sound.midi.Soundbank;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * <p>@Autowired注解解析</p>
 * AutowiredAnnotationBeanPostProcessor.class 运行分析
 *
 * @author qlk
 */
public class DigInAutowired {
    public static void main(String[] args) throws Throwable {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerSingleton("bean2", new Bean2());  // registerSingleton方法: 创建过程,依赖注入和初始化 都由bean过程处理, 直接new
        beanFactory.registerSingleton("bean3", new Bean3());
        beanFactory.setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver());  // 解析 @Value
        beanFactory.addEmbeddedValueResolver(new StandardEnvironment()::resolvePlaceholders);  // ${} spel的解析器

        // 1. 查找哪些属性, 哪些方法加了 @Autowired
        AutowiredAnnotationBeanPostProcessor processor = new AutowiredAnnotationBeanPostProcessor();
        processor.setBeanFactory(beanFactory);  // 通过 beanFactory 间接来找注解

        // 1.1 注解解析执行方法 postProcessProperties
        Bean1 bean1 = new Bean1();
        System.out.println("bean1 = " + bean1);
        // 核心: 解析方法
        // 指定解析的类 bean1
//        processor.postProcessProperties(null, bean1, "bean1");  // @Value @Autowired
//        System.out.println("bean1 = " + bean1);

        // 1.2 postProcessProperties方法内部
        // findAutowiringMetadata, 找哪些类或属性上有 @Autowired 注解
        // 通过反射获取
        Method method = AutowiredAnnotationBeanPostProcessor.class.getDeclaredMethod("findAutowiringMetadata", String.class, Class.class, PropertyValues.class);
        method.setAccessible(true);
        // 将添加@Autowired注解的方法 封装成 InjectionMetadata
        InjectionMetadata metadata = (InjectionMetadata) method.invoke(processor, "bean1", Bean1.class, null);// 找到 bean1 上加了 @Autowired 或 @Value 的方法或属性
        System.out.println("metadata = " + metadata);  // setBean2() setHome()


        // 1.3 调用 InjectionMetadata 来进行依赖注入,注入时按类型查找
        metadata.inject(bean1, "bean1", null);
        System.out.println("bean1 = " + bean1);


        // 2. 如何按类型查找

        // 2.1 属性
        Field bean3 = Bean1.class.getDeclaredField("bean3");
        // 封装成 DependencyDescriptor
        DependencyDescriptor dd1 = new DependencyDescriptor(bean3, false);  // false: 没找到对应类型也不会报错
        Object o = beanFactory.doResolveDependency(dd1, null, null, null);// 依据 DependencyDescriptor 来找对应的 inject 类型
        System.out.println(o);  // bean3对象

        // 2.2 方法
        Method setBean2 = Bean1.class.getDeclaredMethod("setBean2", Bean2.class);
        DependencyDescriptor dd2 = new DependencyDescriptor(new MethodParameter(setBean2, 0), false);
        Object o1 = beanFactory.doResolveDependency(dd2, null, null, null);
        System.out.println(o1);  // bean2 对象

        // 2.3 值注入
        Method setHome = Bean1.class.getDeclaredMethod("setHome", String.class);
        DependencyDescriptor dd3 = new DependencyDescriptor(new MethodParameter(setHome, 0), false);
        Object o2 = beanFactory.doResolveDependency(dd3, null, null, null);
        System.out.println(o2);
    }
}
