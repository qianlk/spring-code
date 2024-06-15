package org.example.s01.a02;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebApplicationContext;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * ApplicationContext的实现
 *
 * @author qlk
 */
public class A02Application {

    private static final Logger log = LoggerFactory.getLogger(A02Application.class);

    public static void main(String[] args) {
//        testClassPathXmlApplicationContext();
//        testFileSystemXMlApplicationContext();
//        xmlReader();

//        testAnnotationConfigApplicationContext();

        testAnnotationConfigServletWebServerApplicationContext();

    }

    /**
     * ClassPathXmlApplicationContext() 和 FileSystemXMlApplicationContext() 读取原理
     * 通过 XmlBeanDefinitionReader 读取配置文件中的bean定义 到 BeanFactory
     * - ClassPathResource
     * - FileSystemResource
     */
    private static void xmlReader() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        Arrays.stream(beanFactory.getBeanDefinitionNames()).forEach(System.out::println);
        System.out.println("读取后");
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
//        reader.loadBeanDefinitions(new ClassPathResource("b01.xml"));
        reader.loadBeanDefinitions(new FileSystemResource("src\\main\\resources\\b01.xml"));
        Arrays.stream(beanFactory.getBeanDefinitionNames()).forEach(System.out::println);
    }

    /**
     * 经典容器, 基于 classpath 下的 xml 格式的配置文件来创建容器
     */
    private static void testClassPathXmlApplicationContext() {
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("b01.xml");

        for (String name : context.getBeanDefinitionNames()) {
            System.out.println("name = " + name);
        }

        System.out.println(context.getBean(Bean2.class).getBean1());
    }

    /**
     * 基于磁盘路径下 xml 格式的配置文件来创建
     */
    private static void testFileSystemXMlApplicationContext() {
        FileSystemXmlApplicationContext context =
                new FileSystemXmlApplicationContext("src\\main\\resources\\b01.xml");  // 指定一下配置文件路径
//        FileSystemXmlApplicationContext context =
//                new FileSystemXmlApplicationContext("D:\\ProjectsRepo\\spring-code\\s01-beans\\src\\main\\resources\\b01.xml");

        System.out.println(context.getBean(Bean2.class).getBean1());
    }

    /**
     * 基于 java 配置类创建容器
     * <p>
     * 除去添加指定标签外,还添加了一些后处理器
     *      org.springframework.context.annotation.internalConfigurationAnnotationProcessor
     *      org.springframework.context.annotation.internalAutowiredAnnotationProcessor
     *      org.springframework.context.annotation.internalCommonAnnotationProcessor
     *      org.springframework.context.event.internalEventListenerProcessor
     *      org.springframework.context.event.internalEventListenerFactory
     * <p>
     * 等价于 xml 配置文件中添加
     * <p>
     * <context:annotation-config />
     */
    private static void testAnnotationConfigApplicationContext() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(Config.class);

        Arrays.stream(context.getBeanDefinitionNames()).forEach(System.out::println);
        System.out.println(context.getBean(Bean2.class).getBean1());
    }

    /**
     * 基于java配置类创建的, 用于 web 环境
     */
    private static void testAnnotationConfigServletWebServerApplicationContext() {
        AnnotationConfigServletWebServerApplicationContext context =
                new AnnotationConfigServletWebServerApplicationContext(WebConfig.class);
        Arrays.stream(context.getBeanDefinitionNames()).forEach(System.out::println);


    }

    @Configuration
    static class WebConfig {

        // servlet容器
        @Bean
        public ServletWebServerFactory servletWebServerFactory() {
            // tomcat 内嵌容器
            return new TomcatServletWebServerFactory();
        }

        // 前控制器,所有的请求进入
        @Bean
        public DispatcherServlet dispatcherServlet() {
            return new DispatcherServlet();
        }

        // 前控制器路径注册, 关联DispatcherServlet和ServletWebServer
        @Bean
        public DispatcherServletRegistrationBean registerBean(DispatcherServlet dispatcherServlet) {
            return new DispatcherServletRegistrationBean(dispatcherServlet, "/");
        }

        // 控制器
        @Bean("/hello")
        public Controller controller1() {
            return new Controller() {
                @Override
                public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    response.getWriter().print("hello");
                    return null;
                }
            };
            
        }

    }

    @Configuration
    static class Config {

        @Bean
        public Bean1 bean1() {
            return new Bean1();
        }

        @Bean
        public Bean2 bean2(Bean1 bean1) {
            Bean2 bean2 = new Bean2();
            bean2.setBean1(bean1);
            return bean2;
        }
    }

    static class Bean1 {

    }

    static class Bean2 {
        private Bean1 bean1;

        public Bean1 getBean1() {
            return bean1;
        }

        public void setBean1(Bean1 bean1) {
            this.bean1 = bean1;
        }
    }

}
