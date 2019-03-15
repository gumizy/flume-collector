package com.datacloudsec.core.interceptor;

/**
 * InterceptorType
 */
public enum InterceptorType {

    GEO(GeoInterceptor.Builder.class),

    //LONGITUDELATITUDENAME(LongitudeLatitudeNameInterceptor.Builder.class),
    DEPART(CusDepartInterceptor.Builder.class),

    FIVEMETA(FiveMetaInterceptor.Builder.class),

    TWOMETA(TwoMetaInterceptor.Builder.class),

    ASSET(AssetInfoInterceptor.Builder.class),//资产

    MALWAREIP(MalwareIpInterceptor.Builder.class),//恶意IP

    MALWAREURL(MalwareURLInterceptor.Builder.class),//恶意URL

    MALWAREDOMAIN(MalwareDomainInterceptor.Builder.class),//恶意域名

    MALWARECODE(MalwareCodeInterceptor.Builder.class),//恶意代码

    VIRUS(VirusInterceptor.Builder.class),//病毒

    VULNERABILITY(VulnerabilityInterceptor.Builder.class),//漏洞

    TROJANS(TrojansInterceptor.Builder.class),//木马

    FIELDMAP(FieldMapInterceptor.Builder.class);//字段映射


    private final Class<? extends Interceptor.Builder> builderClass;

    InterceptorType(Class<? extends Interceptor.Builder> builderClass) {
        this.builderClass = builderClass;
    }

    public Class<? extends Interceptor.Builder> getBuilderClass() {
        return builderClass;
    }
}
