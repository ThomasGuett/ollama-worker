package de.thomas_guett.ollama_documentrouter.model;

public class Model {
    private String name;
    private String model;
    private String modified_at;
    private Long size;
    private String digest;
    private ModelDetails details;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getModified_at() {
        return modified_at;
    }

    public void setModified_at(String modified_at) {
        this.modified_at = modified_at;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public ModelDetails getDetails() {
        return details;
    }

    public void setDetails(ModelDetails details) {
        this.details = details;
    }
}
