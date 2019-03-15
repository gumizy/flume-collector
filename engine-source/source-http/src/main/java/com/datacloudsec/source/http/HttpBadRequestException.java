
package com.datacloudsec.source.http;

import com.datacloudsec.config.CollectorEngineException;

public class HttpBadRequestException extends CollectorEngineException {

  private static final long serialVersionUID = -3540764742069390951L;

  public HttpBadRequestException(String msg) {
    super(msg);
  }

  public HttpBadRequestException(String msg, Throwable th) {
    super(msg, th);
  }

  public HttpBadRequestException(Throwable th) {
    super(th);
  }
}
