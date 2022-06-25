package com.danchuo.pricing.model;

public record ShopError(int code, String message) {
    public static final int BAD_REQUEST = 400;
    public static final int OK = 200;
    public static final int NOT_FOUND = 404;
}
