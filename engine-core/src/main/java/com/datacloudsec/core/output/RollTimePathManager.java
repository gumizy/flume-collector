

package com.datacloudsec.core.output;

import com.datacloudsec.config.Context;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;

/**
 *
 */
public class RollTimePathManager extends DefaultPathManager {

    private final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMddHHmmss");
    private String lastRoll;

    public RollTimePathManager(Context context) {
        super(context);
    }

    @Override
    public File nextFile() {
        StringBuilder sb = new StringBuilder();
        String date = formatter.print(LocalDateTime.now());
        if (!date.equals(lastRoll)) {
            getFileIndex().set(0);
            lastRoll = date;
        }
        sb.append(getPrefix()).append(date).append("-");
        sb.append(getFileIndex().incrementAndGet());
        if (getExtension().length() > 0) {
            sb.append(".").append(getExtension());
        }
        currentFile = new File(getBaseDirectory(), sb.toString());

        return currentFile;
    }

    public static class Builder implements PathManager.Builder {
        @Override
        public PathManager build(Context context) {
            return new RollTimePathManager(context);
        }
    }

}
