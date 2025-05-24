package de.thomas_guett.ollama_documentrouter;

import de.thomas_guett.ollama_documentrouter.model.*;
import de.thomas_guett.ollama_documentrouter.service.DocumentService;
import de.thomas_guett.ollama_documentrouter.service.ImageService;
import de.thomas_guett.ollama_documentrouter.service.OllamaService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

@SpringBootApplication
public class OllamaDocumentrouterApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(OllamaDocumentrouterApplication.class, args);
		listModels();
	}

	static void testOllamaService() {
		Mono<String> response = new OllamaService().getOllamaVersion();
		String version = response.block(Duration.of(1000, ChronoUnit.MILLIS));
		System.out.println("Ollama version: " + version);
	}

	static String listModels() {
		Mono<ModelListResponse> response = new OllamaService().listModels();
		ModelListResponse modelListResponse = response.block();
        assert modelListResponse != null;
        List<Model> models = modelListResponse.getModels();
		models.forEach(model -> {
			System.out.println("Model: " + model.getName());
		});

        return models.getFirst().getName();
	}

	private String getChatCompletion(List<Message> messages, String model) throws IOException {
		CompletionResponse completionResponse = new OllamaService().chatCompletion(messages, model);
        assert completionResponse != null;
        return completionResponse.getMessage().getContent();
	}

	@JobWorker(type = "local-ai-prompt", autoComplete = false)
	public void testWorker(final JobClient client, final ActivatedJob job) throws IOException {
		String prompt = (String) job.getVariable("prompt");
		String model = (String) job.getVariable("model");
		List<Message> messages = new ArrayList<>();
		Message promptMessage = new Message();
		promptMessage.setRole("user");
		promptMessage.setContent(prompt);
		messages.add(promptMessage);
		Map<String, Object> jobVariables = job.getVariablesAsMap();
		Object documentList = job.getVariable("document");
		if(null != documentList) {
			ArrayList<?> docList = (ArrayList<?>) documentList;
			Map<String, Object> docsMap = (LinkedHashMap) docList.get(0);
			String documentId = (String) docsMap.get("documentId");
			String contentHash = (String) docsMap.get("contentHash");
			byte[] readbytes = new DocumentService().downloadDocument(documentId, contentHash);
			String base64String = Base64.getEncoder().encodeToString(readbytes);
			Message imageMessage = new Message();
			imageMessage.setRole("user");
			List<String> imageMessages = new ArrayList<>();
			imageMessages.add(base64String);
			imageMessage.setImages(imageMessages);
			messages.add(imageMessage);
		}

		client.newCompleteCommand(job.getKey()).variable("aiResponse", getChatCompletion(messages, model)).send().join();
	}

	@JobWorker(type = "list-local-models", autoComplete = false)
	public void listLocalModels(final JobClient client, final ActivatedJob job) {
		client.newCompleteCommand(job.getKey()).variable("modelList", listModels()).send().join();
	}
}

