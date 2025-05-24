package de.thomas_guett.ollama_documentrouter.service;

import io.netty.handler.codec.http.HttpResponse;
import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.*;
import java.net.URL;
import java.util.Base64;

public class DocumentService {
    private WebClient webClient;

    public DocumentService() {
        String baseUrl = "http://localhost:8088/v2/documents/";
        this.webClient = WebClient.builder().baseUrl(baseUrl).exchangeStrategies(useMaxMemory()).build();
        //this.webClient = WebClient.create("http://localhost:8088/v2/documents/");
    }

    public byte[] downloadDocument(String documentId, String contentHash) throws IOException {
        URL obj = new URL("http://localhost:8088/v2/documents/" + documentId + "?contentHash=" + contentHash);
        byte[] bytes = obj.openStream().readAllBytes();

//        System.out.println("trying mono");
//        Mono<byte[]> dataBuffer = this.webClient.get().uri(documentId).attribute("contentHash", contentHash).retrieve().onStatus(HttpStatusCode::is5xxServerError, response -> {
//            System.out.println("Mono receives error: " + response);
//            return null;
//        }).bodyToMono(byte[].class);

//        Mono<byte[]> mono = DataBufferUtils.join(dataBuffer).map(dataBuffer1 -> {
//            byte[] bytes = new byte[dataBuffer1.readableByteCount()];
//            dataBuffer1.read(bytes);
//            DataBufferUtils.release(dataBuffer1);
//            return bytes;
//        });
        //byte[] bytes = null != dataBuffer ? dataBuffer.block() : null;
//        PipedOutputStream osPipe = new PipedOutputStream();
//        PipedInputStream isPipe = new PipedInputStream(osPipe);
//        DataBufferUtils.write(dataBuffer, osPipe).subscribeOn(Schedulers.boundedElastic()).doOnComplete(() -> {
//            try {
//                osPipe.close();
//            } catch (IOException ignored) {
//
//            }
//        }).subscribe(DataBufferUtils.releaseConsumer());


        //WebClient.ResponseSpec responseSpec = this.webClient.get().uri(documentId).attribute("contentHash", contentHash).retrieve();
        //Flux<byte[]> flux = responseSpec.bodyToFlux(byte[].class);
        //byte[] bytes = is.readAllBytes();
        return bytes;
    }

    byte[] streamToByteArray(InputStream stream) throws IOException {
        byte[] bytes = new byte[stream.available()];
        int byteCount = stream.read(bytes, 0, bytes.length);
        return bytes;
    }

    private static ExchangeStrategies useMaxMemory() {
        //long totalMemory = Runtime.getRuntime().maxMemory();

        return ExchangeStrategies.builder().codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)).build();
    }
}
