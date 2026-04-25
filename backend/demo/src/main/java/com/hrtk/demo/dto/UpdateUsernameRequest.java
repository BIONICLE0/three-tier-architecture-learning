package com.hrtk.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UpdateUsernameRequest {

    @NotBlank(message = "ユーザー名は必須です")
    @Pattern(
        regexp = "^[a-zA-Z0-9_]{4,20}$",
        message = "ユーザー名は英数字・アンダースコアのみ4〜20文字で入力してください"
    )
    private String username;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}