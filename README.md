#快速接入阿里iot平台服务端订阅
##按照如下方式配置即可使用
```
spring:
  yanhongbin:
    ali:
      iot:
        config:
          # 阿里云物联平台账号信息
          accessKeyId: ****
          accessKeySecret: ****
          # 物联网平台对应地区
          regionId: SHANGHAI
          # 阿里云账号UID
          uid: ****
          # 物联平台产品key
          productKey: ****
          # 消息处理器beanID配置，使用Mns模式时必须，key为产品key，value为消息处理器的对应BeanName
          message-processor-bean-id:
            a1skKPGCozo: defaultMessageProcessor
        # 连接相关配置
        connect:
          # 连接类型 支持 amqp，mns，http/2 （http/2 已被废弃）
          type: amqp
          # 服务端订阅开关，不配置默认不开启
          subscribe-switch: on
        # amqp连接信息配置，使用amqp模式时必须
        amqp:
          # 消费组id和对应的消息持利器BeanName配置
          consumer-group-message-config:
            - consumer-groupId: DEFAULT_GROUP
              message-processor-beanId: messageProcessor
          # 加密方法配置
          signMethod: hmacsha1
```