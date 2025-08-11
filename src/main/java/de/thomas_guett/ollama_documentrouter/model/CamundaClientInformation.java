package de.thomas_guett.ollama_documentrouter.model;

public class CamundaClientInformation {
    private String cluster_id;
    private String client_id;
    private String region;
    private String client_secret;
    private String clientMode;

    public CamundaClientInformation() {}

    public CamundaClientInformation(String clientMode, String region, String cluster_id, String client_id, String client_secret) {
        this.clientMode = clientMode;
        this.region = region;
        this.cluster_id = cluster_id;
        this.client_id = client_id;
        this.client_secret = client_secret;
    }

    public String getCluster_id() {
        return cluster_id;
    }

    public void setCluster_id(String cluster_id) {
        this.cluster_id = cluster_id;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }

    public String getClientMode() {
        return clientMode;
    }

    public void setClientMode(String clientMode) {
        this.clientMode = clientMode;
    }
}
