package org.example.s01.a03;

import java.util.ArrayList;
import java.util.List;

/**
 * 模板方法设计模式
 * 模拟bean后处理器 在bean生命周期阶段执行方法
 *
 * @author qlk
 */
public class TestMethodTemplate {

    public static void main(String[] args) {
        MyBeanFactory beanFactory = new MyBeanFactory();
        beanFactory.addBeanPostProcessor(bean -> System.out.println("解析 @Autowired"));
        beanFactory.addBeanPostProcessor(bean -> System.out.println("解析 @Resource"));

        beanFactory.getBean();
    }

    static class MyBeanFactory {

        private List<BeanPostProcessor> processors = new ArrayList<>();

        public void addBeanPostProcessor(BeanPostProcessor processor) {
            processors.add(processor);
        }

        public Object getBean() {
            Object bean = new Object();
            System.out.println("实例化 " + bean);
            System.out.println("依赖注入 " + bean);
            for (BeanPostProcessor processor : processors) {
                processor.inject(bean);
            }
            System.out.println("初始化 " + bean);
            return bean;
        }
    }

    /**
     * 模板接口
     */
    interface BeanPostProcessor {
        void inject(Object bean);
    }
}
