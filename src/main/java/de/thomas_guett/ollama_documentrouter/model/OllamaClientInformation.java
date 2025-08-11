package de.thomas_guett.ollama_documentrouter.model;

public class OllamaClientInformation {
    private String authenticationType;
    private String baseUrl;
    private String userName;
    private String userPassword;

    public OllamaClientInformation(final String baseUrl) {
        this.authenticationType = "none";
        this.baseUrl = baseUrl;
        this.userName = null;
        this.userPassword = null;
    }

    public OllamaClientInformation(final String baseUrl, final String authenticationType, final String userName, final String userPassword) {
        this.baseUrl = baseUrl;
        this.authenticationType = authenticationType;
        this.userName = userName;
        this.userPassword = userPassword;
    }

    public String getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(String authenticationType) {
        this.authenticationType = authenticationType;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
}
