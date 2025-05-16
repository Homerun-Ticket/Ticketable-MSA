package com.example.modulegame.config;

import com.example.grpc.ticket.TicketServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {
    @Value("${grpc.host}")
    private String host;

    @Value("${grpc.port}")
    private int port;
    @Bean
    public TicketServiceGrpc.TicketServiceBlockingStub ticketServiceBlockingStub() {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(host, port) // 티켓 서버 주소/포트.
                .usePlaintext() // TLS 없이 통신
                .build();
        return TicketServiceGrpc.newBlockingStub(channel);
    }
}