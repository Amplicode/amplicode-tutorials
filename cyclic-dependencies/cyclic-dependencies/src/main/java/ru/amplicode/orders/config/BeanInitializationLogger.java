package ru.amplicode.orders.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class BeanInitializationLogger implements BeanPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(BeanInitializationLogger.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        log.info("Initializing bean: '{}' [{}]", beanName, bean.getClass().getName());
        return bean;
    }
}