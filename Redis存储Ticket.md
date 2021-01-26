# Redis存储Ticket

默认基于内存存储 Ticket，先更换为 Redis 存储。

## 依赖

依赖 cas-server-support-redis-ticket-registry 模块

```xml
<dependency>
  <groupId>org.apereo.cas</groupId>
  <artifactId>cas-server-support-redis-ticket-registry</artifactId>
  <version>${cas.version}</version>
</dependency>
```

## 配置

```properties
##
# CAS Redis Ticket Storage
cas.ticket.registry.redis.host=127.0.0.1
cas.ticket.registry.redis.port=6379
cas.ticket.registry.redis.database=0
cas.ticket.registry.redis.password=
cas.ticket.registry.redis.timeout=3000
cas.ticket.registry.redis.pool.enabled=true
cas.ticket.registry.redis.pool.max-active=20
cas.ticket.registry.redis.pool.max-idle=8
cas.ticket.registry.redis.pool.min-idle=0
cas.ticket.registry.redis.pool.max-wait=3000
```

重启即可。默认前缀为 `CAS_TICKET:`

![image-20210125123446702](docs/Redis存储Ticket/image-20210125123446702.png)
