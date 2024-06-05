package org.example.s01.a01;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;

/**
 * BeanFactory和ApplicationContext的区别
 *
 * @author qlk
 */
@SpringBootApplication
public class A01Application {

    private static final Logger log = LoggerFactory.getLogger(A01Application.class);

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, IOException {
        ConfigurableApplicationContext context = SpringApplication.run(A01Application.class, args);
        /*
         * 1. BeanFactory
         * - 它是 ApplicationContext 的父接口
         * - 是Spring的核心容器
         * - ApplicationContext在它的基础上组合了其他的功能
         */
        System.out.println(context);

        /*
        2. BeanFactory 的功能
        - 方法表面上都是getBean
        - 实际上控制反转,基本的依赖注入,直至Bean的生命周期的各种功能,都由它提供
         */

        // 使用反射把 DefaultSingletonBeanRegistry 的 private成员 singletonObjects 拿到并打印
        Field singletonObjects = DefaultSingletonBeanRegistry.class.getDeclaredField("singletonObjects");
        singletonObjects.setAccessible(true);
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        // 反射 beanFactory 对象中用这个Field描述的成员的值
        // beanFactory 中有 singletonObjects 成员
        Map<String, Object> map = (Map<String, Object>) singletonObjects.get(beanFactory);
        map.entrySet().stream().filter(e -> e.getKey().startsWith("component"))
                .forEach(e -> {
                    System.out.println(e.getKey() + "=" + e.getValue());
                });

        /*
        3. ApplicationContext 比 BeanFactory 多的拓展功能
            3.1 MessageSource: 国际化
            3.2 ResourcePatternResolver: 资源
            3.3 EnvironmentCapable: 环境变量
            3.4 ApplicationEventPublisher: 事件发布
                事件发布用于解耦,比如用户注册,不好指定验证码发送方式,邮箱还是短信,通过事件发布
         */
        System.out.println(context.getMessage("hi", null, Locale.CHINA));
        System.out.println(context.getMessage("hi", null, Locale.ENGLISH));
        System.out.println(context.getMessage("hi", null, Locale.JAPAN));

//        Resource[] resources = context.getResources("classpath:application.properties");
        Resource[] resources = context.getResources("classpath*:META-INF/spring.factories");  // classpath* 是在类路径下jar包中寻找
        for (Resource resource : resources) {
            System.out.println(resource);
        }

        String javaHome = context.getEnvironment().getProperty("java_home");// 不区分大小写
        System.out.println("javaHome = " + javaHome);
        String msgEncoding = context.getEnvironment().getProperty("spring.messages.encoding");// 不区分大小写
        System.out.println("msgEncoding = " + msgEncoding);

//        context.publishEvent(new UserRegisteredEvent(context));
        context.getBean(Component1.class).register();

    }
}
