package org.example.rpc.springboot.starter.bootstrap;

import com.ekroner.rpc.RpcApplication;
import com.ekroner.rpc.config.RpcConfig;
import com.ekroner.rpc.server.tcp.VertxTcpServer;
import lombok.extern.slf4j.Slf4j;
import org.example.rpc.springboot.starter.annotation.EnableRpc;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * RPC 框架启动
 **/
@Slf4j
public class RpcInitBootstrap implements ImportBeanDefinitionRegistrar {

    /**
     * 初始化 RPC 框架
     *
     * @param importingClassMetaData
     * @param registry
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetaData, BeanDefinitionRegistry registry) {
        boolean needServer = (boolean) importingClassMetaData.getAnnotationAttributes(EnableRpc.class.getName()).get("needServer");

        RpcApplication.init();

        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        if(needServer) {
            VertxTcpServer vertxTcpServer = new VertxTcpServer();
            vertxTcpServer.doStart(rpcConfig.getServerPort());
        }
        else {
            log.info("不启动 server");
        }
    }
}
