package com.hrtk.demo.dto;

import java.util.List;
import java.util.Map;

public class ApiResponse {
    private boolean success;
    private List<Step> steps;
    private List<Map<String, String>> errors;
    private String username;

    public ApiResponse(boolean success, List<Step> steps) {
        this.success = success;
        this.steps = steps;
        this.errors = List.of();
        this.username = null;
    }

    public ApiResponse(boolean success, List<Step> steps, List<Map<String, String>> errors) {
        this.success = success;
        this.steps = steps;
        this.errors = errors;
        this.username = null;
    }

    public ApiResponse(boolean success, List<Step> steps, String username) {
        this.success = success;
        this.steps = steps;
        this.errors = List.of();
        this.username = username;
    }

    public boolean isSuccess() { return success; }
    public List<Step> getSteps() { return steps; }
    public List<Map<String, String>> getErrors() { return errors; }
    public String getUsername() { return username; }

}