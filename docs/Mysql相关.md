在装好hadoop集群之后，存取unstructure的数据就没啥问题了。然后需要考虑的问题就是，进行数据库的操作了
研究了一下MongoDB和mysql，最后还是选择了mysql

# 配置安装mysql

按照题目给定的意思，需要包含DBMS1和DBMS2
所以，同样的，我们拿hadoop1作为管理节点，hadoop2和hadoop3作为存储节点

首先下载mysql

我直接配置的apt install mysql-server-8.0的版本
如果需要卸载（啊这个环境配起来太难了）
```
apt purge mysql-*
rm -rf /etc/mysql/ /var/lib/mysql/
apt autoremove
apt autoclean
```

然后需要
```
service mysql start
```
启动mysql服务
每次重启都需要
随后
```
mysql -uroot -p
```
直接回车登录

首先需要改密码，给权限
```
create user 'root'@'%' identified by 'root';
flush privileges;
grant all privileges on *.* to root@'%' with grant option;
flush privileges;
exit
```
除此之外，我遇到的另一个坑在于
需要修改/etc/mysql/mysql.conf.d下mysqld.cnf文件中
```
vim /etc/mysql/mysql.conf.d/mysqld.cnf

bind-address            = 0.0.0.0
```
然后重启mysql服务
```
service mysql restart
```

另外，我在运行的时候，会遇到因为user_read.sql文件过大，他的查询语句报错的情况，错误如下：
```
com.mysql.cj.jdbc.exceptions.PacketTooBigException: Packet for query is too large (109,345,691 > 67,108,864). You can change this value on the server by setting the 'max_allowed_packet' variable.
```

一个好的办法是，拆分sql查询语句，但是，我这里打算简单粗暴地更改max_allowed_packet，同样修改上述的mysqld.cnf:

```
vim /etc/mysql/mysql.conf.d/mysqld.cnf

max_allowed_packet      = 1024M

service mysql restart
```