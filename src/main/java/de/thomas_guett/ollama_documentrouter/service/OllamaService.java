package de.thomas_guett.ollama_documentrouter.service;

import de.thomas_guett.ollama_documentrouter.model.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class OllamaService {
	private final WebClient webClient;
	private final OllamaClientInformation ollamaClientInformation;

	public OllamaService(final OllamaClientInformation ollamaClientInfo) {
		this.webClient = WebClient.create(ollamaClientInfo.getBaseUrl());
		this.ollamaClientInformation = ollamaClientInfo;
	}
	
	public Mono<String> getOllamaVersion() {

		return this.webClient.get().uri("/version").retrieve().bodyToMono(String.class);
	}

	public Mono<ModelListResponse> listModels() {
		return this.webClient.get().uri("/tags").retrieve().bodyToMono(ModelListResponse.class);
	}

	public CompletionResponse chatCompletion(List<Message> messages, String model, ResponseFormat format) {
		CompletionRequest completionRequest = new CompletionRequest();
		completionRequest.setModel(model);
		completionRequest.setStream(false);
		completionRequest.setMessages(messages);
		if(null != format) {
			completionRequest.setFormat(format);
		}
		if(null != ollamaClientInformation.getAuthenticationType() && "basicAuth".equalsIgnoreCase(ollamaClientInformation.getAuthenticationType())) {
			return this.webClient.post().uri("/chat")
					.headers(h -> h.setBasicAuth(ollamaClientInformation.getUserName(), ollamaClientInformation.getUserPassword()))
					.body(Mono.just(completionRequest), CompletionRequest.class)
					.retrieve().bodyToMono(CompletionResponse.class)
					.onErrorMap(e -> new Exception("Error receiving chat completion. ", e)).block();
		}
		return this.webClient.post().uri("/chat").body(Mono.just(completionRequest), CompletionRequest.class)
				.retrieve().bodyToMono(CompletionResponse.class)
				.onErrorMap(e -> new Exception("Error receiving chat completion. ", e)).block();
	}
}
