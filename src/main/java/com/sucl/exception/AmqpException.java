package com.sucl.exception;

import lombok.NoArgsConstructor;

/**
 * @author sucl
 * @date 2019/5/14
 */
@NoArgsConstructor
public class AmqpException extends RuntimeException{

    public AmqpException(String message){
        super(message);
    }
}
