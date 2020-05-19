package com.justyna.project.security.model;

public class Constants {

    public static final long ACCESS_TOKEN_VALIDITY_SECONDS = 60 * 60 * 5;
    public static final String SIGNING_KEY = "howtofly";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String AUTHORITIES_KEY = "scopes";
}