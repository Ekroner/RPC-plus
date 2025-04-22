package org.example.rpc.springboot.starter.bootstrap;

import com.ekroner.rpc.proxy.ServiceProxyFactory;
import lombok.extern.slf4j.Slf4j;
import org.example.rpc.springboot.starter.annotation.RpcReference;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

/**
 * RPC 服务消费者启动
 */
@Slf4j
public class RpcConsumerBootstrap implements BeanPostProcessor {

    /**
     * Bean初始化后注入对象
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        Field[] declaredFields = beanClass.getDeclaredFields();
        for(Field field : declaredFields) {
            RpcReference rpcReference = field.getAnnotation(RpcReference.class);
            if(rpcReference != null) {
                Class<?> interfaceClass = rpcReference.interfaceClass();
                if(interfaceClass == void.class) {
                    interfaceClass = field.getType();
                }
                field.setAccessible(true);
                Object proxyObjet = ServiceProxyFactory.getProxy(interfaceClass);
                try {
                    field.set(bean, proxyObjet);
                    field.setAccessible(false);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("注入代理对象失败", e);
                }
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
