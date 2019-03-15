 
## 采集引擎说明（启动时第一个参数作为http接口的port）
###### 所有配置参考example.properties
###### 开发新组件参见SourceType，ChannelType，SinkType以及configure方法

### 一 、默认解析模式
如果不配置channel，只配置source、sink，日志流转方式如下：
具体配置见example.properties、example-mutisink.properites
```
S1[Source]--|                     |--K1[Sink]
            |----->C[Channel]---->|
S2[Source]--|                     |--K2[Sink]

附注：
source.s1.type=netflow
source.s1.port=5140
source.s1.host=127.0.0.1

source.s2.type=syslogudp
source.s2.port=5141
source.s2.host=127.0.0.1

sink.k1.type=syslog
sink.k1.port=514
sink.k1.host=127.0.0.1

sink.k2.type=kafka
sink.k2.servers=192.168.101.61:9200
```
即只有一个默认的channel，接收所有source日志数据，sink端将读取channel数据，
此时 `S1+S2=C=K1+K2`

如果需要多个channel，此时 `S1+S2=C1+C2=K1+K2`，则配置如下：
  ```
  S1[Source]--|----->C1[Channel]---->|--K1[Sink]
              |                     |
  S2[Source]--|----->C2[Channel]---->|--K2[Sink]
  
  附注：
  
source.s1.type=netflow
source.s1.port=60001
source.s1.host=127.0.0.1
source.s1.channel=c1

source.s2.type=syslogudp
source.s2.port=514
source.s2.host=127.0.0.1
source.s2.channel=c1

sink.k1.type=syslog
sink.k1.port=514
sink.k1.host=127.0.0.1
sink.k1.channel=c1

sink.k2.type=kafka
sink.k2.servers=192.168.101.61:9200
sink.k2.channel=c1

channel.c1.type = memory
channel.c2.type = memory
```
### 二、转发模式

```$xslt
source.s1.type=syslogudp
source.s1.port=514
source.s1.host=127.0.0.1
#是否需要进行数据解析
source.s1.normalization=false
#接收数据是否是转发组件（如果是原始设备即：false）
source.s1.forward=false
#source.s1.forwardCharset=UTF-8

sink.k1.type=syslog
sink.k1.port=514
sink.k1.host=127.0.0.1
```
#### 附注：
forwardCharset只针对第一次采集解码才可以设置（如果设备编码是UTF-8,则不用设置），
当进入采集引擎之后，所有的编码都是UTF-8，将不需要设置forwardCharset。

[查看监听信息](http://192.168.101.61:60000/collectorManager/counterConfig)

 
 