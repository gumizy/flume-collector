package com.datacloudsec.core.serialization;

import com.datacloudsec.config.Context;
import com.datacloudsec.config.Event;

import java.io.IOException;
import java.io.OutputStream;

public interface EventSerializer {

    String CTX_PREFIX = "serializer.";

    void afterCreate() throws IOException;

    void afterReopen() throws IOException;

    void write(Event event) throws IOException;

    void flush() throws IOException;

    void beforeClose() throws IOException;

    boolean supportsReopen();

    interface Builder {
        EventSerializer build(Context context, OutputStream out);
    }

}
