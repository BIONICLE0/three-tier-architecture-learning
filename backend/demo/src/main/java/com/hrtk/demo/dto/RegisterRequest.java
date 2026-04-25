package com.hrtk.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;


public class RegisterRequest {

    @NotBlank(message = "ユーザー名は必須です")
    @Pattern(
        regexp = "^[a-zA-Z0-9_]{4,20}$",
        message = "ユーザー名は英数字・アンダースコアのみ4〜20文字で入力してください"
    )
    private String username;

    @NotBlank(message = "パスワードは必須です")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=]).{8,128}$",
        message = "パスワードは大文字・小文字・数字・記号をそれぞれ1文字以上含む8〜128文字で入力してください"
    )
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
