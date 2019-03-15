package com.datacloudsec.source.ntaflow;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class ParamsManager {

    public static boolean v9TemplateOverwrite = false;

    public static boolean template_refreshFromHD = false;

    public static boolean ip2ipsConvert = true;

    public static long[] ipSrcExcludes = null;

    public static long[] ipSrcIncludes = null;

    public static boolean isSrcExcludes(long addr) {
        if (ipSrcExcludes != null) {
            for (int idx = 0; idx < ipSrcExcludes.length; idx++) {
                if ((addr & ipSrcExcludes[idx]) == ipSrcExcludes[idx]) {
                    if ((addr | ipSrcExcludes[idx]) == addr) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isSrcIncludes(long addr) {
        if (ipSrcIncludes != null) {
            for (int idx = 0; idx < ipSrcIncludes.length; idx++) {
                if ((addr & ipSrcIncludes[idx]) == ipSrcIncludes[idx]) {
                    if ((addr | ipSrcIncludes[idx]) == addr) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static long[] ipDstExcludes = null;// init in Collector.java

    public static long[] ipDstIncludes = null;

    public static boolean isDstExcludes(long addr) {
        if (ipDstExcludes != null) {
            for (int idx = 0; idx < ipDstExcludes.length; idx++) {
                if ((addr & ipDstExcludes[idx]) == ipDstExcludes[idx]) {
                    if ((addr | ipDstExcludes[idx]) == addr) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isDstIncludes(long addr) {
        if (ipDstIncludes != null) {
            for (int idx = 0; idx < ipDstIncludes.length; idx++) {
                if ((addr & ipDstIncludes[idx]) == ipDstIncludes[idx]) {
                    if ((addr | ipDstIncludes[idx]) == addr) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean DEBUG = false;

    public static String encoding = "GBK";

    public static String path = null;// like "D:\Dev\workspace\netflow\bin"

    static {
        path = ParamsManager.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        File directory = new File(path);
        if (path.trim().toLowerCase().endsWith(".jar")) {
            directory = directory.getParentFile();
        }
        try {
            path = directory.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");

    public static String getCurrentTime() {
        return f.format(new Date());
    }
}