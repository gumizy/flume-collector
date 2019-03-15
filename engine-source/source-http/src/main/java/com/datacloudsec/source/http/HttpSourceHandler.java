package com.datacloudsec.source.http;

import com.datacloudsec.config.SourceMessage;
import com.datacloudsec.core.conf.Configurable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *
 */
public interface HttpSourceHandler extends Configurable {

      List<SourceMessage> getMessage(HttpServletRequest request) throws Exception;

}
