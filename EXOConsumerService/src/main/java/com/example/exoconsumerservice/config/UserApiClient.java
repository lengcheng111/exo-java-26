package com.example.exoconsumerservice.config;

import com.example.exoconsumerservice.dto.FolderResponse;
import com.example.exoconsumerservice.dto.UserFolderResponse;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Configuration
public class UserApiClient {

    private final WebClient webClient;
    private final Retry retry;
    private final Duration requestTimeout;

    public UserApiClient(
            @Value("${mock-api.base-url}") String baseUrl,
            @Value("${mock-api.connect-timeout-ms:3000}") int connectTimeoutMs,
            @Value("${mock-api.read-timeout-ms:5000}") int readTimeoutMs,
            @Value("${mock-api.write-timeout-ms:5000}") int writeTimeoutMs,
            @Value("${mock-api.request-timeout-ms:10000}") long requestTimeoutMs,
            @Value("${mock-api.retry.max-attempts:3}") long maxAttempts,
            @Value("${mock-api.retry.backoff-ms:500}") long backoffMs) {

        HttpClient httpClient = HttpClient.create()
                .option(
                        ChannelOption.CONNECT_TIMEOUT_MILLIS,
                        connectTimeoutMs
                )
                .responseTimeout(Duration.ofMillis(readTimeoutMs))
                .doOnConnected(connection -> connection
                        .addHandlerLast(new ReadTimeoutHandler(
                                readTimeoutMs,
                                TimeUnit.MILLISECONDS
                        ))
                        .addHandlerLast(new WriteTimeoutHandler(
                                writeTimeoutMs,
                                TimeUnit.MILLISECONDS
                        )));

        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();

        this.requestTimeout = Duration.ofMillis(requestTimeoutMs);

        this.retry = Retry.backoff(
                        maxAttempts - 1,
                        Duration.ofMillis(backoffMs)
                )
                .filter(this::isRetryable)
                .onRetryExhaustedThrow(
                        (retrySpec, signal) -> signal.failure()
                );
    }

    public List<UserFolderResponse> getUserFolders(String email) {
        return webClient.get()
                .uri("/users/{email}/folders", email)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        ClientResponse::createException
                )
                .bodyToFlux(UserFolderResponse.class)
                .collectList()
                .timeout(requestTimeout)
                .retryWhen(retry)
                .block();
    }

    public List<FolderResponse> getFolders() {
        return webClient.get()
                .uri("/folders")
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        ClientResponse::createException
                )
                .bodyToFlux(FolderResponse.class)
                .collectList()
                .timeout(requestTimeout)
                .retryWhen(retry)
                .block();
    }

    private boolean isRetryable(Throwable throwable) {
        if (throwable instanceof TimeoutException
                || throwable instanceof IOException) {
            return true;
        }

        if (throwable instanceof WebClientResponseException exception) {
            return exception.getStatusCode().is5xxServerError()
                    || exception.getStatusCode().is4xxClientError();
        }

        return false;
    }
}