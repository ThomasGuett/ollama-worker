package de.thomas_guett.ollama_documentrouter.model;

import java.util.List;

public class CompletionResponse {
    private String model;
    private String created_at;
    private String response;
    private boolean done;
    private String done_reason;
    private List<Integer> context;
    private Long total_duration;
    private Long load_duration;
    private Integer prompt_eval_count;
    private Long prompt_eval_duration;
    private Integer eval_count;
    private Long eval_duration;
    private Message message;

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getDone_reason() {
        return done_reason;
    }

    public void setDone_reason(String done_reason) {
        this.done_reason = done_reason;
    }

    public List<Integer> getContext() {
        return context;
    }

    public void setContext(List<Integer> context) {
        this.context = context;
    }

    public Long getTotal_duration() {
        return total_duration;
    }

    public void setTotal_duration(Long total_duration) {
        this.total_duration = total_duration;
    }

    public Long getLoad_duration() {
        return load_duration;
    }

    public void setLoad_duration(Long load_duration) {
        this.load_duration = load_duration;
    }

    public Integer getPrompt_eval_count() {
        return prompt_eval_count;
    }

    public void setPrompt_eval_count(Integer prompt_eval_count) {
        this.prompt_eval_count = prompt_eval_count;
    }

    public Long getPrompt_eval_duration() {
        return prompt_eval_duration;
    }

    public void setPrompt_eval_duration(Long prompt_eval_duration) {
        this.prompt_eval_duration = prompt_eval_duration;
    }

    public Integer getEval_count() {
        return eval_count;
    }

    public void setEval_count(Integer eval_count) {
        this.eval_count = eval_count;
    }

    public Long getEval_duration() {
        return eval_duration;
    }

    public void setEval_duration(Long eval_duration) {
        this.eval_duration = eval_duration;
    }
}
