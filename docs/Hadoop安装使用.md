# 三个docker容器配置构建集群

首先安装docker。

然后pull一个ubuntu

之后

```
docker network create --driver bridge hadoop-bridge
```

生成network



```
docker run --name ddb1 -itd --network hadoop-bridge -p 10086:80 -p 10087:22 -p 10088:50070 -p 8088:8088 -p 9000:9000 -p 16010:16010 -p 2181:2181 -p 8080:8080 -p 16000:16000 -p 9020:9020 -p 42239:42239 -p 60000:60000  -v /home/susan/distribute-data-base/:/home/ddb ubuntu /bin/bash

docker run --name ddb2 -itd --network hadoop-bridge  -v /home/susan/distribute-data-base/:/home/ddb ubuntu /bin/bash

docker run --name ddb3 -itd --network hadoop-bridge  -v /home/susan/distribute-data-base/:/home/ddb ubuntu /bin/bash
```

生成三个docker的container

在这里，我们默认让ddb1作为master节点



查看网络的状况

```
docker network inspect hadoop-bridge

 % docker network inspect hadoop-bridge
[
    {
        "Name": "hadoop-bridge",
        "Id": "df668c16dcb7666f3060e0b6c6653f1e49ab6622c49b3b27e28a3e45ef100a4e",
        "Created": "2024-06-03T12:10:46.862584232+08:00",
        "Scope": "local",
        "Driver": "bridge",
        "EnableIPv6": false,
        "IPAM": {
            "Driver": "default",
            "Options": {},
            "Config": [
                {
                    "Subnet": "172.19.0.0/16",
                    "Gateway": "172.19.0.1"
                }
            ]
        },
        "Internal": false,
        "Attachable": false,
        "Ingress": false,
        "ConfigFrom": {
            "Network": ""
        },
        "ConfigOnly": false,
        "Containers": {
            "9d74a769ea31f8cfa80ed7bb90f6e12a619af8151bfe5400222f9627c02af4af": {
                "Name": "ddb1",
                "EndpointID": "e0c0be724fbacd868c584b618409a791dd4442223540b027064137e86516076a",
                "MacAddress": "02:42:ac:13:00:02",
                "IPv4Address": "172.19.0.2/16",
                "IPv6Address": ""
            },
            "aa4d294259bf305a8373711c27ccfde5d6e24f6dfef1a9e7005a501055022a98": {
                "Name": "ddb2",
                "EndpointID": "5814368a7b9b7e75361b48b695011daa069f03de05971d7359c4afc602bb1917",
                "MacAddress": "02:42:ac:13:00:03",
                "IPv4Address": "172.19.0.3/16",
                "IPv6Address": ""
            },
            "e00af42b6e13d8341f81468c11d9d7cff412db2b3e920a066bee5ab8df028dbd": {
                "Name": "ddb3",
                "EndpointID": "995311bf9fc9a3c2e3e93f8be563595e40f371b8efdfeadaa1904867e48d0b26",
                "MacAddress": "02:42:ac:13:00:04",
                "IPv4Address": "172.19.0.4/16",
                "IPv6Address": ""
            }
        },
        "Options": {},
        "Labels": {}
    }
]
```

并通过

```
docker exec -it ddbx /bin/bash	# x替换为具体的container的值
```

来执行对应的container

进入对应的容器之后，安装一些基础的软件，随后

```
vim /etc/hosts
```

在其中按照网络的情况，添加

```
172.19.0.2      hadoop1
172.19.0.3      hadoop2
172.19.0.4      hadoop3
```

测试ping命令

```
ping hadoop2
```

随后，安装openssh服务，更改config，让他可以root登录

```
apt install openssh-server
vim /etc/ssh/sshd_config
```

生成password

```
 passwd root
```

随后，使用ssh-keygen生成对应的密钥并分发

```
ssh-keygen
ssh-copy-id -i /root/.ssh/id_ed25519 -p 22 root@hadoop1
ssh-copy-id -i /root/.ssh/id_ed25519 -p 22 root@hadoop2
ssh-copy-id -i /root/.ssh/id_ed25519 -p 22 root@hadoop3
```

这样，三个节点的组网就完成了

安装jdk，jdk的部分我是直接apt install安装的



对于docker容器，如果stop了这个docker容器，那么需要首先修改/etc/hosts，因为每次都会改变。

所以需要按照上面的说法，添加hadoop123三行进去。

其次是，启动ssh服务

```
service ssh start
```

然后才能正常工作



# 安装hadoop

```
wget https://archive.apache.org/dist/hadoop/common/hadoop-3.4.0/hadoop-3.4.0.tar.gz
```

(实际上网速太慢我是host端好然后放过去的)

然后解压之后，放在usr/local/hadoop文件夹下，配置好环境变量

```
vim ~/.bashrc

export PATH=$PATH:/usr/local/hadoop/bin
```

随后退出



然后配置JAVA，我是直接apt install openjdk安装的java，所以需要修改hadoop的env

```
vim /usr/local/hadoop/etc/hadoop/hadoop-env.sh

修改
export JAVA_HOME=/usr
```



# 配置Hadoop

1/core-site.xml

```
cd /usr/local/hadoop/etc/hadoop
vim core-site.xml
```



```
<configuration>
        <property>
                <name>fs.defaultFS</name>
                <value>hdfs://hadoop1:8020</value>
        </property>
        <property>
                <name>hadoop.tmp.dir</name>
                <value>/usr/local/hadoop/tmp</value>
        </property>
</configuration>
```

2/hdfs-site.xml

```
cd /usr/local/hadoop/etc/hadoop
vim hdfs-site.xml
```



```
<configuration>
    <property>
        <name>dfs.namenode.name.dir</name>
        <value>file://${hadoop.tmp.dir}/dfs/name</value>
    </property>
    <property>
        <name>dfs.datanode.data.dir</name>
        <value>file://${hadoop.tmp.dir}/dfs/data</value>
    </property>
    <property>
        <name>dfs.replication</name>
        <value>3</value>
    </property>
    <property>
        <name>dfs.blocksize</name>
        <value>134217728</value>
    </property>
    <property>
        <name>dfs.namenode.secondary.http-address</name>
        <value>hadoop2:9001</value>
    </property>
        <property>
          <name>dfs.namenode.http-address</name>
          <value>hadoop1:50070</value>
        </property>
</configuration>
```

3/mapred-site.xml

```
cd /usr/local/hadoop/etc/hadoop
vim mapred-site.xml
```



```
<configuration>
    <property>
        <name>mapreduce.framework.name</name>
        <value>yarn</value>
    </property>
    <property>
        <name>mapreduce.jobhistory.address</name>
        <value>hadoop1:10020</value>
    </property>
    <property>
        <name>mapreduce.jobhistory.webapp.address</name>
        <value>hadoop01:19888</value>
    </property>
</configuration>
```



4/yarn-site.xml

```
cd /usr/local/hadoop/etc/hadoop
vim yarn-site.xml
```



```
<configuration>

<!-- Site specific YARN configuration properties -->
    <property>
        <name>yarn.nodemanager.aux-services</name>
       <value>mapreduce_shuffle</value>
    </property>
    <property>
        <name>yarn.resourcemanager.hostname</name>
        <value>hadoop1</value>
    </property>
        <property>
        <name>yarn.nodemanager.aux-services.mapreduce_shuffle.class</name>
     <value>org.apache.hadoop.mapred.ShuffleHandler</value>
        </property>

        <property>
        <name>yarn.resourcemanager.webapp.address</name>
        <value>hadoop1:8088</value>
        </property>
</configuration>
```

5/workers

```
cd /usr/local/hadoop/etc/hadoop
vim workers
```



```
hadoop2
hadoop3
```



并且，参考网上的配置，我还修改了sbin下的start-dfs，stop-dfs，start-yarn，stop-yarn

```
cd /usr/local/hadoop/sbin
```

修改start-dfs.sh和stop-dfs.sh

添加

```
HDFS_DATANODE_USER=root
HDFS_DATANODE_SECURE_USER=hdfs
HDFS_NAMENODE_USER=root
HDFS_SECONDARYNAMENODE_USER=root
```

修改start-yarn.sh和stop-yarn.sh

添加

```
YARN_RESOURCEMANAGER_USER=root
HDFS_DATANODE_SECURE_USER=root
YARN_NODEMANAGER_USER=root
```



# 启动hadoop

在sbin文件夹下运行start-all.sh

随后在三个节点下使用jps查看

```
root@9d74a769ea31:/usr/local/hadoop/sbin# jps
8912 NameNode
9719 Jps
9325 ResourceManager
```

```
root@aa4d294259bf:/# jps
7048 NodeManager
6922 SecondaryNameNode
7163 Jps
6830 DataNode
```

```
root@e00af42b6e13:/# jps
6978 NodeManager
7093 Jps
6853 DataNode
```

