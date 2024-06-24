FROM ubuntu:noble

COPY ./ubuntu.sources /etc/apt/sources.list.d/ubuntu.sources
RUN apt-get update && \
    apt-get install -y wget

RUN wget https://dlcdn.apache.org/maven/maven-3/3.9.7/binaries/apache-maven-3.9.7-bin.tar.gz --progress=bar:force:noscroll && \
    mkdir -p /home/ddb && \
    tar -xzf apache-maven-3.9.7-bin.tar.gz -C /usr/local && \
    rm apache-maven-3.9.7-bin.tar.gz

RUN apt-get install -y openjdk-8-jdk && \
    apt-get purge -y wget && \
    apt-get autopurge -y && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

ENV MAVEN_HOME=/usr/local/apache-maven-3.9.7
ENV PATH=$PATH:$MAVEN_HOME/bin
