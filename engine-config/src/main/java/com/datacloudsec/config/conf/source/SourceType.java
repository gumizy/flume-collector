/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.datacloudsec.config.conf.source;

import com.datacloudsec.config.conf.ComponentWithClassName;

public enum SourceType implements ComponentWithClassName {

    OTHER(null),

    FTP("com.datacloudsec.source.ftp.FtpFileSource"),

    NETFLOW("com.datacloudsec.source.netflow.NetFlowSource"),

    NTAFLOW("com.datacloudsec.source.ntaflow.NtaFlowSource"),

    JDBC("com.datacloudsec.source.jdbc.JdbcSource"),

    SYSLOGUDP("com.datacloudsec.source.udp.SyslogUDPSource"),

    NETTYSYSLOGUDP("com.datacloudsec.source.udp.NettySyslogUDPSource"),

    HTTP("com.datacloudsec.source.http.HttpSource"),

    KAFKA("com.datacloudsec.source.kafka.KafkaSource"),

    REDIS("com.datacloudsec.source.redis.RedisSource");

    private final String sourceClassName;

    SourceType(String sourceClassName) {
        this.sourceClassName = sourceClassName;
    }

    @Override
    public String getClassName() {
        return sourceClassName;
    }
}
