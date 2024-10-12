package com.ehyundai.app.chatgpt.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.tcp.TcpClient;

import java.time.Duration;

@Configuration
public class ChatGptWebClientConfig {

    @Bean
    @Qualifier("chatGptWebClient")
    public WebClient chatGptWebClient() {
        // ConnectionProvider를 사용하여 커넥션 풀 설정
        ConnectionProvider provider = ConnectionProvider.builder("chatGptProvider")
                .maxConnections(50) // 최대 커넥션 수 설정
                .pendingAcquireTimeout(Duration.ofSeconds(60)) // 커넥션 대기 시간 설정
                .build();

        // TcpClient 설정을 통해 DNS 및 연결 타임아웃 설정
        TcpClient tcpClient = TcpClient.create(provider)
                .resolver(spec -> spec.queryTimeout(Duration.ofSeconds(10))) // DNS 조회 타임아웃 설정
                .option(io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000) // 연결 타임아웃 설정 (5초)
                .doOnConnected(connection ->
                        connection.addHandlerLast(new io.netty.handler.timeout.ReadTimeoutHandler(10)) // 읽기 타임아웃 (10초)
                                .addHandlerLast(new io.netty.handler.timeout.WriteTimeoutHandler(10)) // 쓰기 타임아웃 (10초)
                );

        // HttpClient 설정을 통해 WebClient 구성
        HttpClient httpClient = HttpClient.from(tcpClient)
                .responseTimeout(Duration.ofSeconds(10)); // 전체 응답 타임아웃 설정

        return WebClient.builder()
                .baseUrl("https://api.openai.com")
                .clientConnector(new ReactorClientHttpConnector(httpClient)) // ReactorClientHttpConnector 사용 가능
                .build();
    }
}