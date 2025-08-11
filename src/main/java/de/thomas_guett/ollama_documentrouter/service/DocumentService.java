package de.thomas_guett.ollama_documentrouter.service;

import de.thomas_guett.ollama_documentrouter.model.CamundaAuthTokenResponse;
import de.thomas_guett.ollama_documentrouter.model.CamundaClientInformation;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.*;
import java.net.URL;

public class DocumentService {
    private WebClient webClient;
    private String baseUrl;
    private CamundaClientInformation clientInfo;

    public DocumentService(CamundaClientInformation clientInfo) {
        this.clientInfo = clientInfo;
        this.baseUrl = null != clientInfo.getClientMode() && clientInfo.getClientMode().equalsIgnoreCase("saas")
                && null != clientInfo.getCluster_id() && null != clientInfo.getRegion() ?
                new StringBuilder("https://")
                        .append(clientInfo.getRegion())
                        .append(".zeebe.camunda.io:443/")
                        .append(clientInfo.getCluster_id())
                        .append("/v2").toString() : "http://localhost:8088/v2";
        this.webClient = WebClient.builder().baseUrl(baseUrl).exchangeStrategies(useMaxMemory()).build();
    }

    public byte[] downloadDocument(String documentId, String contentHash) throws IOException {
        String url = this.baseUrl + "/documents/" + documentId + "?contentHash=" + contentHash;
        // differentiate between saas and local (saas requires auth)
        // TODO: add auth support for self-managed
        if(null != clientInfo.getClientMode() && clientInfo.getClientMode().equalsIgnoreCase("saas")) {
            String authUrl = "https://login.cloud.camunda.io";
            WebClient tokenClient = WebClient.builder().baseUrl(authUrl).build();
            CamundaAuthTokenResponse tokenResponse = tokenClient.post().uri("/oauth/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters
                            .fromFormData("grant_type", "client_credentials")
                            .with("audience", "zeebe.camunda.io")
                            .with("client_id", clientInfo.getClient_id())
                            .with("client_secret", clientInfo.getClient_secret()))
                    .retrieve().bodyToFlux(CamundaAuthTokenResponse.class)
                    .onErrorMap(e -> new Exception("Authentication Error during Document Download.", e))
                    .blockLast();
            return this.webClient.get().uri(url).headers(h -> h.setBearerAuth(tokenResponse.getAccess_token()))
                    .retrieve().bodyToMono(byte[].class).block();
        }


        URL obj = new URL(url);
        byte[] bytes = obj.openStream().readAllBytes();
        return bytes;
    }

    private static ExchangeStrategies useMaxMemory() {

        return ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)).build();
    }
}
