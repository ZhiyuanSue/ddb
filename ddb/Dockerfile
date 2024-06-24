FROM ubuntu:noble

COPY ./setup/ubuntu.sources /etc/apt/sources.list.d/ubuntu.sources
RUN apt-get update && \
    apt-get install -y wget

RUN wget https://dlcdn.apache.org/hadoop/common/hadoop-3.4.0/hadoop-3.4.0.tar.gz --progress=bar:force:noscroll && \
    tar -xzf hadoop-3.4.0.tar.gz -C /usr/local && \
    mv /usr/local/hadoop-3.4.0 /usr/local/hadoop && \
    rm hadoop-3.4.0.tar.gz

RUN apt-get install -y openssh-server && \
    sed -i 's/PermitRootLogin no/PermitRootLogin yes/g' /etc/ssh/sshd_config && \
    ssh-keygen -t ed25519 -f ~/.ssh/id_ed25519 && \
    cat ~/.ssh/id_ed25519.pub >> ~/.ssh/authorized_keys && \
    chmod 600 ~/.ssh/authorized_keys && \
    echo "export JAVA_HOME=/usr" >> /usr/local/hadoop/etc/hadoop/hadoop-env.sh

RUN apt-get install -y mysql-server-8.0 && \
    service mysql start && \
    sed -i 's/^bind-address\s*=.*/bind-address = 0.0.0.0/' /etc/mysql/mysql.conf.d/mysqld.cnf && \
    sed -i 's/^max_allowed_packet\s*=.*/max_allowed_packet = 1024M/' /etc/mysql/mysql.conf.d/mysqld.cnf && \
    echo "CREATE USER 'root'@'%' IDENTIFIED BY 'root'; \
           FLUSH PRIVILEGES; \
           GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION; \
           FLUSH PRIVILEGES;" | mysql -u root -proot && \
    service mysql stop

RUN apt-get install -y openjdk-8-jre sudo mariadb-client && \
    apt-get purge -y wget && \
    apt-get autopurge -y && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

ENV HADOOP_HOME=/usr/local/hadoop
ENV PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin
