package de.thomas_guett.ollama_documentrouter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.thomas_guett.ollama_documentrouter.model.*;
import de.thomas_guett.ollama_documentrouter.service.DocumentService;
import de.thomas_guett.ollama_documentrouter.service.OllamaService;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import io.camunda.spring.client.annotation.JobWorker;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

@SpringBootApplication
public class OllamaDocumentrouterApplication {
	@Value("${camunda.client.mode}")
	String clientMode;
	@Value("${camunda.client.auth.client-id}")
	String client_id;
	@Value("${camunda.client.auth.client-secret}")
	String client_secret;
	@Value("${camunda.client.cloud.cluster-id}")
	String cluster_id;
	@Value("${camunda.client.cloud.region}")
	String region;

	static String ollamaDefaultBaseUrl = "http://localhost:11434/api";

	public static void main(String[] args) throws IOException {
		SpringApplication.run(OllamaDocumentrouterApplication.class, args);
	}

	static String listModels() {
		OllamaClientInformation ollamaClientInformation = new OllamaClientInformation(ollamaDefaultBaseUrl);
		Mono<ModelListResponse> response = new OllamaService(ollamaClientInformation).listModels();
		ModelListResponse modelListResponse = response.block();
        assert modelListResponse != null;
        List<Model> models = modelListResponse.getModels();
		models.forEach(model -> {
			System.out.println("Model: " + model.getName());
		});

        return models.getFirst().getName();
	}

	private String getChatCompletion(List<Message> messages, OllamaClientInformation clientInformation, String model, ResponseFormat responseFormat) throws IOException {
		CompletionResponse completionResponse = new OllamaService(clientInformation).chatCompletion(messages, model, responseFormat);
        assert completionResponse != null;
        return completionResponse.getMessage().getContent();
	}

	//@JobWorker(type = "com.ollama.connector", autoComplete = false)
	@JobWorker(type = "com.ollama.connector", autoComplete = false)
	public void testWorker(final JobClient client, final ActivatedJob job) throws IOException {
		// determine mode
		Map<String, Object> jobVariables = job.getVariablesAsMap();
		Object mode = jobVariables.getOrDefault("operationMode", null);
		Object systemPrompt = jobVariables.getOrDefault("systemPrompt", null);
		Object responseFormat = jobVariables.getOrDefault("responseFormat", null);
		Object jsonSchema = jobVariables.getOrDefault("jsonSchema", null);
		String apiBaseUrl = (String) jobVariables.get("apiBaseUrl");
		String prompt = (String) jobVariables.get("userPrompt");
		String model = (String) jobVariables.get("model");

		// extract ollama auth requirements
		String authType = (String) jobVariables.get("authenticationType");
		OllamaClientInformation ollamaClientInformation;
		if("basicAuth".equalsIgnoreCase(authType)) {
			String userName = (String) jobVariables.get("apiUserName");
			String userPassword = (String) jobVariables.get("apiUserPassword");
			ollamaClientInformation = new OllamaClientInformation(apiBaseUrl, authType, userName, userPassword);
		} else {
			ollamaClientInformation = new OllamaClientInformation(apiBaseUrl);
		}

		List<Message> messages = new ArrayList<>();
		Message promptMessage = new Message();
		promptMessage.setRole("user");
		promptMessage.setContent(prompt);
		messages.add(promptMessage);
		if (systemPrompt instanceof String) {
			Message systemMessage = new Message();
			systemMessage.setRole("system");
			systemMessage.setContent((String) systemPrompt);
			messages.add(systemMessage);
		}
		if (mode instanceof String && "agentic".equalsIgnoreCase((String) mode)) {
			Message agentInstruction = new Message();
			agentInstruction.setRole("system");
			// look for next actions
			Object optionalNextActions = job.getVariable("possibleActions");
			String possibleActionsString = (String) optionalNextActions;
			String agentPrompt = new StringBuilder("you are a helpful agent that selects all relevant next actions based on these options: ")
					.append(possibleActionsString)
							.append("\n respond in a JSON String as following: {\"nextActions\":[String], \"reasoning\":\"String\"}. DO NOT use any additional text formatting.").toString();
			agentInstruction.setContent(agentPrompt);
			messages.add(agentInstruction);
		}
		Object documentList = jobVariables.getOrDefault("document", null);
		if(null != documentList) {
			ArrayList<?> docList = (ArrayList<?>) documentList;
			Map<String, Object> docsMap = (LinkedHashMap) docList.get(0);
			String documentId = (String) docsMap.get("documentId");
			String contentHash = (String) docsMap.get("contentHash");
			CamundaClientInformation clientInformation = new CamundaClientInformation(clientMode, region, cluster_id, client_id, client_secret);
			byte[] readbytes = new DocumentService(clientInformation).downloadDocument(documentId, contentHash);
			System.out.println("byte size: " + readbytes.length);
			String base64String = Base64.getEncoder().encodeToString(readbytes);
			Message imageMessage = new Message();
			imageMessage.setRole("user");
			List<String> imageMessages = new ArrayList<>();
			imageMessages.add(base64String);
			imageMessage.setImages(imageMessages);
			messages.add(imageMessage);
		}
		// infer local ai
		ResponseFormat responseJson = null;
		if (responseFormat instanceof String) {
			System.out.println("format: " + responseFormat);

			if(null != jsonSchema) {
				ObjectMapper om = new ObjectMapper();
				responseJson = om.convertValue(jsonSchema, ResponseFormat.class);
			}
		}
		String completion = getChatCompletion(messages, ollamaClientInformation, model, responseJson);
		jobVariables.put("aiResponse", completion);
		if(mode instanceof String && "agentic".equalsIgnoreCase((String) mode)) {
			// ensure proper return values
			System.out.println("completion response: " + completion);
			if(completion.startsWith("{")) {
				JSONObject jsonObject = new JSONObject(completion);
				List<String> nextActions = new ArrayList<>();
				JSONArray jsonNextActions = jsonObject.optJSONArray("nextActions");
				jsonNextActions.forEach(jsonAction -> {
					nextActions.add(jsonAction.toString());
				});
				jobVariables.put("nextActions", nextActions);
				jobVariables.put("aiReasoning", jsonObject.optString("reasoning"));
			}
		}

		client.newCompleteCommand(job.getKey()).variables(jobVariables).send().join();
	}

	@JobWorker(type = "list-local-models", autoComplete = false)
	public void listLocalModels(final JobClient client, final ActivatedJob job) {
		client.newCompleteCommand(job.getKey()).variable("modelList", listModels()).send().join();
	}
}

