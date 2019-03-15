package com.datacloudsec.config.tools;

import java.util.ArrayList;
import java.util.List;

public class KeyValuePairs {

    private List<KeyValuePair> pairs = new ArrayList<KeyValuePair>();

    /**
     * 添加键值对
     *
     * @param key   key
     * @param value value
     * @return KeyValuePairs
     */
    public KeyValuePairs addKeyValuePair(String key, String value) {
        pairs.add(new KeyValuePair(key, value));
        return this;
    }

    /**
     * 返回键值对
     *
     * @return the pairs
     */
    public List<KeyValuePair> getPairs() {
        return pairs;
    }

    /**
     * 键值对
     */
    public static class KeyValuePair {

        private String key;

        private String value;

        public KeyValuePair() {
            super();
        }

        public KeyValuePair(String key, String value) {
            super();
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
