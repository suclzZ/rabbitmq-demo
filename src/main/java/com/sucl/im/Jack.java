package com.sucl.im;

import java.io.IOException;

/**
 * @author sucl
 * @date 2019/5/14
 */
public class Jack extends AbstractUser{

    public Jack(String name) {
        super(name);
    }

    @Override
    public String getPusRoutingKey() {
        return "toRose";
    }

    @Override
    public String getResQueue() {
        return "q.rose";
    }

    @Override
    public String getResBindingKey() {
        return "toJack";
    }

    public static void main(String[] args) throws IOException {
        Jack jack = new Jack("jack");
        jack.start();
    }
}
