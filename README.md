# microservice-demo
## 微服务架构实践

### 项目结构
```aiignore
microservice-demo/                    # 父工程（根目录，只管理依赖版本，无业务代码）
├── pom.xml                           # 父 POM，packaging=pom
├── common-api/                       # 公共 API 模块（存放实体类、Feign 接口、常量等）
│   ├── pom.xml
│   └── src/main/java/com/example/api/
│       ├── entity/User.java
│       └── feign/UserFeignClient.java
├── order-service/                    # 订单服务（业务模块）
│   ├── pom.xml
│   └── src/main/java/com/example/order/
└── user-service/                     # 用户服务（业务模块）
    ├── pom.xml
    └── src/main/java/com/example/user/
```

### 路由转发流程

```aiignore
浏览器/前端
    │
    ▼
http://localhost:10000/api/user/1     ← 请求打到网关
    │
    ▼
网关匹配 Path=/api/user/**
    │
    ▼
StripPrefix=1 去掉前缀 → /user/1
    │
    ▼
lb://user-service 从 Nacos 拉取 user-service 实例列表
    │
    ▼
负载均衡选中一个实例（如 192.168.1.100:8082）
    │
    ▼
转发请求 → http://192.168.1.100:8082/user/1
    │
    ▼
user-service 的 UserController 处理请求
    │
    ▼
返回 JSON 响应 ← 原路返回给前端
```

### 模块拆分
```aiignore
common-core/      # 核心工具类、常量、枚举、统一返回体、全局异常（无框架依赖）
common-api/       # 实体类、DTO、Feign 接口（依赖 common-core）
common-db/        # MyBatis Plus 配置、通用 Mapper（依赖 common-core）
```
### 启动顺序
```aiignore
Nacos（先启动注册中心）
↓
user-service（端口 8082）
↓
order-service（端口 8081）
↓
gateway（端口 10000，最后启动）
```

### 验证
```aiignore
# 通过网关访问用户服务（不再是直接访问 8082）
curl http://localhost:10000/api/user/1

# 通过网关访问订单服务
curl http://localhost:10000/api/order/1
```