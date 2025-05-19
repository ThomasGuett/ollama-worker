package de.thomas_guett.ollama_documentrouter.model;

import java.util.List;

public class ModelListResponse {
    private List<Model> models;

    public List<Model> getModels() {
        return models;
    }

    public void setModels(List<Model> models) {
        this.models = models;
    }
}
