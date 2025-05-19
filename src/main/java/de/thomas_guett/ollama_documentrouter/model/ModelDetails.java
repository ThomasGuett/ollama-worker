package de.thomas_guett.ollama_documentrouter.model;

import java.util.List;

public class ModelDetails {
    private String parent;
    private String format;
    private String family;
    private List<String> families;
    private String parameter_size;
    private String quantization_level;

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public List<String> getFamilies() {
        return families;
    }

    public void setFamilies(List<String> families) {
        this.families = families;
    }

    public String getParameter_size() {
        return parameter_size;
    }

    public void setParameter_size(String parameter_size) {
        this.parameter_size = parameter_size;
    }

    public String getQuantization_level() {
        return quantization_level;
    }

    public void setQuantization_level(String quantization_level) {
        this.quantization_level = quantization_level;
    }
}
