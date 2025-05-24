package de.thomas_guett.ollama_documentrouter.service;

import org.apache.commons.io.FileUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class ImageService {
    public static String readFileAsBase64(String filepath) throws IOException {
        byte[] fileContent = FileUtils.readFileToByteArray(ResourceUtils.getFile("classpath:" + filepath));
        return Base64.getEncoder().encodeToString(fileContent);
    }
}
