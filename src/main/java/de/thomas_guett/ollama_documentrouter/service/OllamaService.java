package de.thomas_guett.ollama_documentrouter.service;

import de.thomas_guett.ollama_documentrouter.model.CompletionRequest;
import de.thomas_guett.ollama_documentrouter.model.CompletionResponse;
import de.thomas_guett.ollama_documentrouter.model.Message;
import de.thomas_guett.ollama_documentrouter.model.ModelListResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class OllamaService {
	private final WebClient webClient;

	public OllamaService() {
		this.webClient = WebClient.create("http://localhost:11434/api");
	}
	
	public Mono<String> getOllamaVersion() {
		return this.webClient.get().uri("/version").retrieve().bodyToMono(String.class);
	}

	public Mono<ModelListResponse> listModels() {
		return this.webClient.get().uri("/tags").retrieve().bodyToMono(ModelListResponse.class);
	}

//	public CompletionResponse chatCompletion(String prompt) throws IOException {
//		Message message = new Message();
//		message.setRole("user");
//		message.setContent(prompt);
//		Message imageMessage = new Message();
//		imageMessage.setRole("user");
//		List<String> base64Images = new ArrayList<>();
//		base64Images.add(ImageService.readFileAsBase64("./testimages/GescannteDokumente.png"));
//		imageMessage.setImages(base64Images);
//		List<Message> messages = new ArrayList<>();
//		messages.add(message);
//		messages.add(imageMessage);
//		return chatCompletion(messages);
//	}

	public CompletionResponse chatCompletion(List<Message> messages, String model) {
		CompletionRequest completionRequest = new CompletionRequest();
		completionRequest.setModel(model);
		completionRequest.setStream(false);
		completionRequest.setMessages(messages);
		Mono<CompletionResponse> completionResponseMono = this.webClient.post().uri("/chat").body(Mono.just(completionRequest), CompletionRequest.class).retrieve().bodyToMono(CompletionResponse.class);
		return completionResponseMono.block();
	}
}
