package com.hrtk.demo.dto;

public class Step {
    private int step;
    private String from;
    private String to;
    private String message;
    private String detail;

    public Step(int step, String from, String to, String message) {
        this.step = step;
        this.from = from;
        this.to = to;
        this.message = message;
        this.detail = null;
    }

    public Step(int step, String from, String to, String message, String detail) {
        this.step = step;
        this.from = from;
        this.to = to;
        this.message = message;
        this.detail = detail;
    }

    public int getStep() { return step; }
    public String getFrom() { return from; }
    public String getTo() { return to; }
    public String getMessage() { return message; }
    public String getDetail() { return detail; }
}