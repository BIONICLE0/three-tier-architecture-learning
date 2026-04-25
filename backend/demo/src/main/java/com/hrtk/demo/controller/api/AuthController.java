package com.hrtk.demo.controller.api;

import com.hrtk.demo.dto.*;
import com.hrtk.demo.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse handleValidationError(MethodArgumentNotValidException ex, HttpServletRequest request) {

    List<Step> steps = new ArrayList<>();
    int i = 1;

    String path = request.getRequestURI();

        // =====================
        // フロント→バックの流れ（分岐）
        // =====================
        if (path.equals("/api/register")) {
            steps.add(new Step(i++, "browser", "frontend", "アカウント情報入力"));
            steps.add(new Step(i++, "frontend", "backend", "登録API呼び出し", "POST /register"));

        } else if (path.equals("/api/login")) {
            steps.add(new Step(i++, "browser", "frontend", "ログイン情報入力"));
            steps.add(new Step(i++, "frontend", "backend", "ログインAPI呼び出し", "POST /login"));

        } else if (path.equals("/api/update/username")) {
            steps.add(new Step(i++, "browser", "frontend", "ユーザー名入力"));
            steps.add(new Step(i++, "frontend", "backend", "ユーザー名変更API", "PUT /update/username"));

        } else if (path.equals("/api/update/password")) {
            steps.add(new Step(i++, "browser", "frontend", "パスワード入力"));
            steps.add(new Step(i++, "frontend", "backend", "パスワード変更API", "PUT /update/password"));

        } 

        // バックエンド内
        steps.add(new Step(i++, "backend", "backend", "リクエスト受信"));
        steps.add(new Step(i++, "backend", "backend", "バリデーションチェック"));

        // エラー発生
        steps.add(new Step(i++, "backend", "frontend", "400 Bad Request"));
        steps.add(new Step(i++, "frontend", "browser", "バリデーションエラー表示"));

        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors()
            .stream()
            .map(e -> Map.of("field", e.getField(), "message", e.getDefaultMessage()))
            .collect(Collectors.toList());
        return new ApiResponse(false, steps, errors);
    }

    @PostMapping("/register")
    public ApiResponse register(@RequestBody @Valid RegisterRequest req) {
        return authService.register(req);
    }

    @PostMapping("/login")
    public ApiResponse login(@RequestBody @Valid LoginRequest req, HttpSession session) {
        return authService.login(req, session);
    }

    @PostMapping("/logout")
    public ApiResponse logout(HttpSession session) {
        return authService.logout(session);
    }

    @PutMapping("/update/username")
    public ApiResponse updateUsername(@RequestBody @Valid UpdateUsernameRequest req, HttpSession session) {
        return authService.updateUsername(req, session);
    }

    @PutMapping("/update/password")
    public ApiResponse updatePassword(@RequestBody @Valid UpdatePasswordRequest req, HttpSession session) {
        return authService.updatePassword(req, session);
    }

    @DeleteMapping("/user")
    public ApiResponse delete(HttpSession session) {
        return authService.delete(session);
    }

    @GetMapping("/me")
    public ApiResponse me(HttpSession session) {
        return authService.me(session);
    }
}