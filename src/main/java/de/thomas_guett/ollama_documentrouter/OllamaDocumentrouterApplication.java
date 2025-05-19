package de.thomas_guett.ollama_documentrouter;

import de.thomas_guett.ollama_documentrouter.model.CompletionResponse;
import de.thomas_guett.ollama_documentrouter.model.Model;
import de.thomas_guett.ollama_documentrouter.model.ModelListResponse;
import de.thomas_guett.ollama_documentrouter.service.OllamaService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

@SpringBootApplication
public class OllamaDocumentrouterApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(OllamaDocumentrouterApplication.class, args);

		testOllamaService();
		listModels();
		getChatCompletion();
	}

	static void testOllamaService() {
		Mono<String> response = new OllamaService().getOllamaVersion();
		String version = response.block(Duration.of(1000, ChronoUnit.MILLIS));
		System.out.println("Ollama version: " + version);
	}

	static void listModels() {
		Mono<ModelListResponse> response = new OllamaService().listModels();
		ModelListResponse modelListResponse = response.block();
        assert modelListResponse != null;
        List<Model> models = modelListResponse.getModels();
		String firstModel = models.getFirst().getName();

		System.out.println("local models: " + firstModel);
	}

	static void getChatCompletion() throws IOException {
		Mono<CompletionResponse> responseMono = new OllamaService().chatCompletion("Why is the sky blue?");
		CompletionResponse completionResponse = responseMono.block();
        assert completionResponse != null;
        String message = completionResponse.getMessage().getContent();

		System.out.println("chat completion: " + message);
	}
}

