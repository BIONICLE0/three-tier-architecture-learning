package com.hrtk.demo.service;

import com.hrtk.demo.dto.*;
import com.hrtk.demo.model.User;
import com.hrtk.demo.repository.UserRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserCacheService userCacheService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository, UserCacheService userCacheService) {
        this.userRepository = userRepository;
        this.userCacheService = userCacheService;
    }

    // キャッシュstep出力ヘルパー
    private void addCacheSteps(List<Step> steps, int[] i, CacheResult<User> result, String key, String selectSql) {
        if (result.hit) {
            steps.add(new Step(i[0]++, "backend", "cache", "キャッシュ取得（HIT）", "GET " + key));
        } else {
            steps.add(new Step(i[0]++, "backend", "cache", "キャッシュ取得（MISS）", "GET " + key));
            steps.add(new Step(i[0]++, "backend", "db", "DB検索", selectSql));
            if (result.cached) {
                steps.add(new Step(i[0]++, "backend", "cache", "キャッシュ保存", "SET " + key));
            }
        }
    }

    // アカウント作成
    public ApiResponse register(RegisterRequest req) {

        List<Step> steps = new ArrayList<>();
        int[] i = {1};

        steps.add(new Step(i[0]++, "browser", "frontend", "アカウント情報入力"));
        steps.add(new Step(i[0]++, "frontend", "backend", "登録API呼び出し", "POST /register"));
        steps.add(new Step(i[0]++, "backend", "backend", "ユーザー重複チェック"));

        CacheResult<User> existingResult = userCacheService.getByUsernameWithStatus(req.getUsername());
        addCacheSteps(steps, i, existingResult, "users::" + req.getUsername(),
                "SELECT * FROM users WHERE username = '" + req.getUsername() + "'");
        User existing = existingResult.value;

        if (existing != null) {
            steps.add(new Step(i[0]++, "backend", "browser", "登録失敗（既存ユーザー）"));
            return new ApiResponse(false, steps);
        }

        steps.add(new Step(i[0]++, "backend", "backend", "パスワードハッシュ化"));
        String hashed = encoder.encode(req.getPassword());

        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(hashed);

        userRepository.insert(user);
        steps.add(new Step(i[0]++, "backend", "db", "ユーザー保存", "INSERT INTO users"));
        steps.add(new Step(i[0]++, "backend", "browser", "登録成功"));

        return new ApiResponse(true, steps);
    }

    // ログイン
    public ApiResponse login(LoginRequest req, HttpSession session) {

        List<Step> steps = new ArrayList<>();
        int[] i = {1};

        steps.add(new Step(i[0]++, "browser", "frontend", "ログイン情報入力"));
        steps.add(new Step(i[0]++, "frontend", "backend", "ログインAPI呼び出し", "POST /login"));

        CacheResult<User> userResult = userCacheService.getByUsernameWithStatus(req.getUsername());
        addCacheSteps(steps, i, userResult, "users::" + req.getUsername(),
                "SELECT * FROM users WHERE username = '" + req.getUsername() + "'");

        User user = userResult.value;

        if (user == null) {
            steps.add(new Step(i[0]++, "backend", "browser", "認証失敗（ユーザーなし）"));
            return new ApiResponse(false, steps);
        }

        steps.add(new Step(i[0]++, "backend", "backend", "パスワード照合"));

        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            steps.add(new Step(i[0]++, "backend", "browser", "認証失敗（パスワード不一致）"));
            return new ApiResponse(false, steps);
        }

        steps.add(new Step(i[0]++, "backend", "backend", "認証成功"));

        session.setAttribute("userId", user.getId());
        String sessionId = session.getId();
        String encoded = java.util.Base64.getEncoder().encodeToString(sessionId.getBytes());

        steps.add(new Step(i[0]++, "backend", "backend", "セッション作成"));
        steps.add(new Step(i[0]++, "backend", "cache", "セッション保存", "SET session:userId=" + user.getId()));
        steps.add(new Step(i[0]++, "backend", "browser", "ログイン成功（Cookie付与）", "Cookie: SESSION=" + encoded));

        return new ApiResponse(true, steps);
    }

    // ログアウト
    public ApiResponse logout(HttpSession session) {

        List<Step> steps = new ArrayList<>();
        int[] i = {1};

        steps.add(new Step(i[0]++, "browser", "frontend", "ログアウト操作"));
        steps.add(new Step(i[0]++, "frontend", "backend", "ログアウトAPI呼び出し", "POST /logout"));

        session.invalidate();
        steps.add(new Step(i[0]++, "backend", "backend", "セッション削除"));
        steps.add(new Step(i[0]++, "backend", "cache", "セッション削除", "DEL session"));
        steps.add(new Step(i[0]++, "backend", "browser", "ログアウト完了"));

        return new ApiResponse(true, steps);
    }

    // アカウント名変更
    public ApiResponse updateUsername(UpdateUsernameRequest req, HttpSession session) {

        List<Step> steps = new ArrayList<>();
        int[] i = {1};

            steps.add(new Step(i[0]++, "browser", "frontend", "ユーザー名入力"));
            steps.add(new Step(i[0]++, "frontend", "backend", "ユーザー名変更API", "PUT /update/username"));

            steps.add(new Step(i[0]++, "backend", "cache", "セッション取得", "GET session:userId"));

        Object userId = session.getAttribute("userId");

        if (userId == null) {
            steps.add(new Step(i[0]++, "backend", "browser", "未ログイン"));
            return new ApiResponse(false, steps);
        }

        steps.add(new Step(i[0]++, "backend", "backend", "重複チェック"));

        CacheResult<User> existingResult = userCacheService.getByUsernameWithStatus(req.getUsername());
        addCacheSteps(steps, i, existingResult, "users::" + req.getUsername(),
                "SELECT * FROM users WHERE username = '" + req.getUsername() + "'");
        User existing = existingResult.value;

        if (existing != null) {
            steps.add(new Step(i[0]++, "backend", "browser", "変更失敗（重複）"));
            return new ApiResponse(false, steps);
        }

        User current = userRepository.findById((Long) userId);

        userCacheService.evictByUsername(current.getUsername());
        steps.add(new Step(i[0]++, "backend", "cache", "キャッシュ削除", "DEL users::" + current.getUsername()));

        current.setUsername(req.getUsername());
        userRepository.update(current);

        steps.add(new Step(i[0]++, "backend", "db", "ユーザー名更新", "UPDATE users SET username = ? WHERE id = " + userId));
        steps.add(new Step(i[0]++, "backend", "browser", "変更成功"));

        return new ApiResponse(true, steps);
    }

    // パスワード変更
    public ApiResponse updatePassword(UpdatePasswordRequest req, HttpSession session) {

        List<Step> steps = new ArrayList<>();
        int[] i = {1};

        steps.add(new Step(i[0]++, "browser", "frontend", "パスワード入力"));
        steps.add(new Step(i[0]++, "frontend", "backend", "パスワード変更API", "PUT /update/password"));
        steps.add(new Step(i[0]++, "backend", "cache", "セッション取得", "GET session:userId"));

        Object userId = session.getAttribute("userId");

        if (userId == null) {
            steps.add(new Step(i[0]++, "backend", "browser", "未ログイン"));
            return new ApiResponse(false, steps);
        }

        User current = userRepository.findById((Long) userId);
        steps.add(new Step(i[0]++, "backend", "db", "ユーザー情報取得", "SELECT * FROM users WHERE id = " + userId));

        userCacheService.evictByUsername(current.getUsername());
        steps.add(new Step(i[0]++, "backend", "cache", "キャッシュ削除", "DEL users::" + current.getUsername()));
        steps.add(new Step(i[0]++, "backend", "backend", "パスワードハッシュ化"));
        current.setPassword(encoder.encode(req.getPassword()));

        userRepository.update(current);
        steps.add(new Step(i[0]++, "backend", "db", "パスワード更新", "UPDATE users SET password = ? WHERE id = " + userId));
        steps.add(new Step(i[0]++, "backend", "browser", "変更成功"));

        return new ApiResponse(true, steps);
    }

    // アカウント削除
    public ApiResponse delete(HttpSession session) {

        List<Step> steps = new ArrayList<>();
        int[] i = {1};

        steps.add(new Step(i[0]++, "browser", "frontend", "削除ボタン押下"));
        steps.add(new Step(i[0]++, "frontend", "backend", "アカウント削除API呼び出し", "DELETE /user"));
        steps.add(new Step(i[0]++, "backend", "cache", "セッション取得", "GET session:userId"));

        Object userId = session.getAttribute("userId");

        if (userId == null) {
            steps.add(new Step(i[0]++, "backend", "browser", "未ログイン → 削除不可"));
            return new ApiResponse(false, steps);
        }

        User current = userRepository.findById((Long) userId);
        steps.add(new Step(i[0]++, "backend", "db", "ユーザー取得", "SELECT * FROM users WHERE id = " + userId));


        userCacheService.evictByUsername(current.getUsername());
        steps.add(new Step(i[0]++, "backend", "cache", "キャッシュ削除", "DEL users::" + current.getUsername()));

        userRepository.deleteById((Long) userId);
        steps.add(new Step(i[0]++, "backend", "db", "ユーザー削除", "DELETE FROM users WHERE id = " + userId));

        session.invalidate();
        steps.add(new Step(i[0]++, "backend", "cache", "セッション削除", "DEL session"));
        steps.add(new Step(i[0]++, "backend", "browser", "削除完了（Cookie無効化）"));

        return new ApiResponse(true, steps);
    }

    // ログイン状態維持
    public ApiResponse me(HttpSession session) {

        List<Step> steps = new ArrayList<>();
        int[] i = {1};

        steps.add(new Step(i[0]++, "browser", "frontend", "画面表示時に認証チェック"));
        steps.add(new Step(i[0]++, "frontend", "backend", "ログイン状態確認API", "GET /me"));
        steps.add(new Step(i[0]++, "backend", "cache", "セッション取得", "GET session:userId"));

        Object userId = session.getAttribute("userId");

        if (userId == null) {
            steps.add(new Step(i[0]++, "backend", "browser", "未ログイン"));
            return new ApiResponse(false, steps);
        }

        User current = userRepository.findById((Long) userId);
        steps.add(new Step(i[0]++, "backend", "db", "ユーザー取得", "SELECT * FROM users WHERE id = " + userId));
        steps.add(new Step(i[0]++, "backend", "browser", "ログイン済みユーザー返却"));

        return new ApiResponse(true, steps, current.getUsername());
    }
}