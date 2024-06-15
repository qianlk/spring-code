package org.example.s01.a03;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * 自定义bean后处理器
 * 给bean的生命周期添加方法
 *
 * @author qlk
 */
@Component
public class MyBeanPostProcessor implements InstantiationAwareBeanPostProcessor, DestructionAwareBeanPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(MyBeanPostProcessor.class);

    // 销毁前 DestructionAwareBeanPostProcessor
    @Override
    public void postProcessBeforeDestruction(Object o, String s) throws BeansException {
        if (s.equals("lifeCycleBean")) {
            log.debug("<<<< 销毁之前执行 (postProcessBeforeDestruction)");
        }
    }

    // 实例化前 InstantiationAwareBeanPostProcessor
    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        if (beanName.equals("lifeCycleBean")) {
            log.debug("<<<< 实例化之前执行, 这里返回的对象可以替换掉原本的bean (postProcessBeforeInstantiation)");
        }
        return null;  // 返回null,会保持原有对象不变
    }

    // 实例化后 InstantiationAwareBeanPostProcessor
    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        if (beanName.equals("lifeCycleBean")) {
            log.debug("<<<< 实例化之后执行, 这里返回false 会跳过依赖注入阶段 (postProcessAfterInstantiation)");
//            return false;
        }
        return true;
    }

    // 依赖注入阶段执行, InstantiationAwareBeanPostProcessor
    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        if (beanName.equals("lifeCycleBean")) {
            log.debug("<<<< 依赖注入阶段执行, 如 @Autowired @Value @Resource (postProcessProperties)");
        }
        return pvs;
    }

    // 初始化前, BeanPostProcessor
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (beanName.equals("lifeCycleBean")) {
            log.debug("<<<< 初始化之前执行, 返回值会替换掉之前的bean, 如 @PostConstruct @ConfigurationProperties (postProcessBeforeInitialization)");
        }
        return bean;
    }

    // 初始化后, BeanPostProcessor
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (beanName.equals("lifeCycleBean")) {
            log.debug("<<<< 初始化之后执行, 返回值会替换掉之前的bean, 如 代理增强 (postProcessAfterInitialization)");
        }
        return bean;
    }
}
