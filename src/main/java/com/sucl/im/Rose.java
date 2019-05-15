package com.sucl.im;

import java.io.IOException;

/**
 * @author sucl
 * @date 2019/5/14
 */
public class Rose extends AbstractUser{

    public Rose(String name) {
        super(name);
    }

    @Override
    public String getPusRoutingKey() {
        return "toJack";
    }

    @Override
    public String getResQueue() {
        return "q.jack";
    }

    @Override
    public String getResBindingKey() {
        return "toRose";
    }

    public static void main(String[] args) throws IOException {
        Rose rose = new Rose("rose");
        rose.start();
    }
}
