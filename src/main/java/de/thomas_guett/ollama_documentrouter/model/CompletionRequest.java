package de.thomas_guett.ollama_documentrouter.model;

import java.util.List;

public class CompletionRequest {
    private String model;
    private List<Message> messages;
    private boolean stream;
    private ResponseFormat format;
    private ResponseOptions options;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public boolean isStream() {
        return stream;
    }

    public void setStream(boolean stream) {
        this.stream = stream;
    }

    public ResponseFormat getFormat() {
        return format;
    }

    public void setFormat(ResponseFormat format) {
        this.format = format;
    }

    public ResponseOptions getOptions() {
        return options;
    }

    public void setOptions(ResponseOptions options) {
        this.options = options;
    }
}
