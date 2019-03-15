

package com.datacloudsec.core.conf;

import com.datacloudsec.config.Context;
import com.datacloudsec.core.annotations.InterfaceAudience;
import com.datacloudsec.core.annotations.InterfaceStability;

@InterfaceAudience.Public
@InterfaceStability.Stable
public interface Configurable {

    public void configure(Context context);

}
