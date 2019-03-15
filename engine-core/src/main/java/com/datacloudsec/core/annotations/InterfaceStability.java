package com.datacloudsec.core.annotations;

import java.lang.annotation.Documented;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class InterfaceStability {
    @Documented
    public @interface Stable {
    }

    @Documented
    public @interface Evolving {
    }

    @Documented
    public @interface Unstable {
    }

}