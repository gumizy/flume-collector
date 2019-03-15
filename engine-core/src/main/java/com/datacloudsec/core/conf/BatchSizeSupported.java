
package com.datacloudsec.core.conf;

/**
 * This interface indicates that a component does batching and the batch size
 * is publicly available.
 *
 */
public interface BatchSizeSupported {

  /**
   * Returns the batch size
   */
  long getBatchSize();

}
