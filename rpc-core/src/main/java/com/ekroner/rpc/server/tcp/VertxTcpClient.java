package com.ekroner.rpc.server.tcp;

import cn.hutool.core.util.IdUtil;
import com.ekroner.rpc.RpcApplication;
import com.ekroner.rpc.model.RpcRequest;
import com.ekroner.rpc.model.RpcResponse;
import com.ekroner.rpc.model.ServiceMetaInfo;
import com.ekroner.rpc.protocol.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Vertx TCP client
 */
public class VertxTcpClient {

    /**
     * 发送请求
     *
     * @param rpcRequest
     * @param serviceMetaInfo
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) throws InterruptedException, ExecutionException {
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
        netClient.connect(serviceMetaInfo.getServicePort(), serviceMetaInfo.getServiceHost(), result -> {
            if(!result.succeeded()) {
                System.err.println("Failed to connect to TCP server");
                return;
            }
            NetSocket netSocket = result.result();

            ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
            ProtocolMessage.Header header = new ProtocolMessage.Header();
            header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
            header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
            header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
            header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
            header.setRequestId(IdUtil.getSnowflakeNextId());
            protocolMessage.setHeader(header);
            protocolMessage.setBody(rpcRequest);

            try {
                Buffer encodeBuffer = ProtocolMessageEncoder.encode(protocolMessage);
                netSocket.write(encodeBuffer);
            } catch (IOException e) {
                throw new RuntimeException("协议消息编码错误");
            }

            TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(
                    buffer -> {
                        try {
                            ProtocolMessage<RpcResponse> protocolMessage1 = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                            responseFuture.complete(protocolMessage1.getBody());
                        } catch (IOException e) {
                            throw new RuntimeException("协议消息解码错误");
                        }
                    });
            netSocket.handler(bufferHandlerWrapper);
        });

        RpcResponse rpcResponse = responseFuture.get();
        netClient.close();
        return rpcResponse;
    }
}


