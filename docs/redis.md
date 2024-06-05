# 安装
在三个节点上都分别
正常安装
```
apt install redis-server
```
然后去/etc/redis/redis.conf里面注释掉
```
bind 127.0.0.1 -::1
```
并且取消掉注释，设置远程登录密码为root
```
requirepass root
```
并重启
```
service redis-server restart
```