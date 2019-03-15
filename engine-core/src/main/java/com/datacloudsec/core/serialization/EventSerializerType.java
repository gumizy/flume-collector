package com.datacloudsec.core.serialization;

public enum EventSerializerType {
    TEXT(BodyTextEventSerializer.Builder.class), OTHER(null);

    private final Class<? extends EventSerializer.Builder> builderClass;

    EventSerializerType(Class<? extends EventSerializer.Builder> builderClass) {
        this.builderClass = builderClass;
    }

    public Class<? extends EventSerializer.Builder> getBuilderClass() {
        return builderClass;
    }

}
