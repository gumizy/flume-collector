
package com.datacloudsec.core.annotations;

import java.lang.annotation.Documented;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class InterfaceAudience {
  /**
   * Intended for use by any project or application.
   */
  @Documented public @interface Public {};

  /**
   * Intended only for the project(s) specified in the annotation.
   * For example, "Common", "HDFS", "MapReduce", "ZooKeeper", "HBase".
   */
  @Documented public @interface LimitedPrivate {
    String[] value();
  };

  /**
   * Intended for use only within Flume
   */
  @Documented public @interface Private {};

  private InterfaceAudience() {} // Audience can't exist on its own
}
