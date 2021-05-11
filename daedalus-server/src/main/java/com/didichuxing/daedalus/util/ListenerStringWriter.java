package com.didichuxing.daedalus.util;

import java.io.StringWriter;
import java.util.function.Consumer;

/**
 * @author : jiangxinyu
 * @date : 2020/7/21
 */
public class ListenerStringWriter extends StringWriter {
    private Consumer<String> consumer;

    public ListenerStringWriter(Consumer<String> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void write(String str, int off, int len) {
        super.write(str, off, len);
        this.consumer.accept(str.substring(off, off + len));
    }
}
