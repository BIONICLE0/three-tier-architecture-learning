package com.hrtk.demo.service;

public class CacheResult<T> {
    public final T value;
    public final boolean hit;       // true=HIT, false=MISS
    public final boolean cached;    // true=今回キャッシュ登録した

    public CacheResult(T value, boolean hit, boolean cached) {
        this.value = value;
        this.hit = hit;
        this.cached = cached;
    }
}