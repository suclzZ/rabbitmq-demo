package com.sucl.config;

import lombok.Data;

/**
 * @author sucl
 * @date 2019/5/14
 */
@Data
public class AmqpConfig {
    private String host = "localhost";
    private String virtualHost = "/";
    private String username = "guest";
    private String password = "guest";
    private int port = 5672;
}
