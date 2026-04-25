package com.hrtk.demo.service;

import com.hrtk.demo.model.User;
import com.hrtk.demo.repository.UserRepository;

import org.springframework.cache.CacheManager;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class UserCacheService {

    private final UserRepository userRepository;
    private final CacheManager cacheManager;

    public UserCacheService(UserRepository userRepository, CacheManager cacheManager) {
        this.userRepository = userRepository;
        this.cacheManager = cacheManager;
    }

    // DBキャッシュ付きユーザー検索（username）
    // @Cacheable(value = "users", key = "#username", unless = "#result == null")
    // public User getByUsername(String username) {
    //     System.out.println("★DBアクセス: " + username);
    //     return userRepository.findByUsername(username);
    // }

    // DBキャッシュ付きユーザー検索（id）
    // @Cacheable(value = "usersById", key = "#id", unless = "#result == null")
    // public User getById(Long id) {
    //     System.out.println("★DBアクセス: " + id);
    //     return userRepository.findById(id);
    // }

    // DBキャッシュ削除（username）
    @CacheEvict(value = "users", key = "#username")
    public void evictByUsername(String username) {}

    // 追加：step可視化用（HIT/MISS/登録を判別できる）
    public CacheResult<User> getByUsernameWithStatus(String username) {
        Cache cache = cacheManager.getCache("users");
        Cache.ValueWrapper wrapper = cache.get(username);

        if (wrapper != null) {
            return new CacheResult<>((User) wrapper.get(), true, false); // HIT
        }
        System.out.println("★DBアクセス(username): " + username); // ← ここ
        User user = userRepository.findByUsername(username);

        if (user != null) {
            cache.put(username, user);
            return new CacheResult<>(user, false, true); // MISS → 登録
        }

        return new CacheResult<>(null, false, false); // MISS → nullなので登録しない
    }
    
}