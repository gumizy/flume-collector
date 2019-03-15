package com.datacloudsec.core.output;

import com.datacloudsec.config.Context;

import java.io.File;

public interface PathManager {

    static String CTX_PREFIX = "pathManager.";

    File nextFile();

    File getCurrentFile();

    void rotate();

    File getBaseDirectory();

    void setBaseDirectory(File baseDirectory);

    interface Builder {
        public PathManager build(Context context);
    }
}
