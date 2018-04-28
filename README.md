# uc-server
统一用户管理和权限认证服务后端（restful-api）
## 功能列表
* 用户统一管理
* 支持第三方AD同步用户和用户组
* 支持基于角色授权和用户组授权
* 服务间通信，支持两种认证体系（Mac Token和Bearer Token ）

   Mac Token 主要用于前后端交互的认证方式
   
   Bearer Token 主要用于服务间的交互认证方式
* 托管所有服务的权限管理和鉴权，提供统一的权限认证服务(基于restful-api)
