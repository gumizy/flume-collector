package com.datacloudsec.core.serialization;

import com.datacloudsec.config.Context;
import com.datacloudsec.config.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class BodyTextEventSerializer implements EventSerializer {

    private static final Logger logger = LoggerFactory.getLogger(BodyTextEventSerializer.class);

    private final String APPEND_NEWLINE = "appendNewline";
    private final boolean APPEND_NEWLINE_DFLT = true;

    private final OutputStream out;
    private final boolean appendNewline;

    private BodyTextEventSerializer(OutputStream out, Context ctx) {
        this.appendNewline = ctx.getBoolean(APPEND_NEWLINE, APPEND_NEWLINE_DFLT);
        this.out = out;
    }

    @Override
    public boolean supportsReopen() {
        return true;
    }

    @Override
    public void afterCreate() {
        // noop
    }

    @Override
    public void afterReopen() {
        // noop
    }

    @Override
    public void beforeClose() {
        // noop
    }

    @Override
    public void write(Event e) throws IOException {
        out.write(e.valueMapToJSONString().getBytes(Charset.defaultCharset()));
        if (appendNewline) {
            out.write('\n');
        }
    }

    @Override
    public void flush() throws IOException {
        // noop
    }

    public static class Builder implements EventSerializer.Builder {

        @Override
        public EventSerializer build(Context context, OutputStream out) {
            BodyTextEventSerializer s = new BodyTextEventSerializer(out, context);
            return s;
        }

    }

}
