package com.datacloudsec.core;

import com.datacloudsec.core.annotations.InterfaceAudience;
import com.datacloudsec.core.annotations.InterfaceStability;

@InterfaceAudience.Public
@InterfaceStability.Stable
public interface OriginalComponent {

    void setName(String name);

    String getName();

    void setType(String type);

    String getType();
}
