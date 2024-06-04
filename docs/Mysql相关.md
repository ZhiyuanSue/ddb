在装好hadoop集群之后，存取unstructure的数据就没啥问题了。然后需要考虑的问题就是，进行数据库的操作了
研究了一下MongoDB和mysql，最后还是选择了mysql

# 配置安装mysql

按照题目给定的意思，需要包含DBMS1和DBMS2
所以，同样的，我们拿hadoop1作为管理节点，hadoop2和hadoop3作为存储节点

首先下载mysql
直接apt install mysql-server即可
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

首先需要改密码，然后重启mysql服务
```
ALTER USER 'root'@'%' IDENTIFIED BY 'root';
use mysql;
select user,host from user;
update user set host='%' where user='root';
select user,host from user;
exit
service mysql start
```

