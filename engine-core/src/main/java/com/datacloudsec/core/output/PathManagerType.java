
package com.datacloudsec.core.output;

import com.datacloudsec.core.annotations.InterfaceAudience;
import com.datacloudsec.core.annotations.InterfaceStability;

/**
 *
 */
@InterfaceAudience.Private
@InterfaceStability.Unstable
public enum PathManagerType {
  DEFAULT(DefaultPathManager.Builder.class),
  ROLLTIME(RollTimePathManager.Builder.class),
  OTHER(null);

  private final Class<? extends PathManager.Builder> builderClass;

  PathManagerType(Class<? extends PathManager.Builder> builderClass) {
    this.builderClass = builderClass;
  }

  public Class<? extends PathManager.Builder> getBuilderClass() {
    return builderClass;
  }
}
