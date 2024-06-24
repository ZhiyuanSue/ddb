# Manual

Welcome!

This project is a content management system (CMS) that supports batch importing blog articles, managing users, recommending articles, uploading files, etc.
It is written in Java and based on MySQL, Hadhoop, and Redis, etc.
It can be deployed on a single machine or a cluster.

## Depolyment

This project is deployed using [Docker](https://www.docker.com/).
Please install [Docker](https://docs.docker.com/get-docker/) and [Docker Compose](https://docs.docker.com/compose/install/) according to the official guide.

> Note: Connection to the Internet is required to download the Docker images.
If you are in China, you may need to configure the Docker registry mirror to speed up the download.

After installation, clone this repository and enter the source code directory.

```
git clone https://github.com/ZhiyuanSue/ddb
```

Suppose that the repository is cloned to `~/ddb/`.

To generate mock data, please extract `db-generation.rar` provided to the `db-generation/` directory under the repository.
Then run:
```bash
cd ~/ddb/db-generation
python3 genTable_sql_relationalDB10G.py
# or any other SQL dataset you would like
```

Now we are ready to start up the service:

```bash
cd ~/ddb/ddb
sudo docker-compose up -d
```

You can check that the services are indeed running using

```bash
sudo docker-compose ps -a
```

or check the logs of the services

```bash
sudo docker-compose logs -f
```

Deploying to a cluster is supported via Docker Compose v3 and we refer readers to [online resources](https://stackoverflow.com/questions/40737389/docker-compose-deploying-service-in-multiple-hosts).

If you would like to persist the data, we can mount the volumes to the host machine.
Modify the `docker-compose.yml` file to add the following lines to the `volumes` section of the services you want to persist the data.
```yaml
volumes:
  - mysql:/var/lib/mysql
  - hadoop:/usr/local/hadoop/tmp
```

## Usage

The application is written in Java and it needs to be compiled before running.
We have set up a compilation environment in a Docker container, so you can compile and run the application directly in the container.

Enter the client container:

```bash
sudo docker-compose exec client bash
```

The current directory should be `/home/ddb/ddb`. Inside the container, compile and run the application:

```bash
mvn compile && mvn exec:java -Dexec.mainClass="com.yizhu.thu.App" -Dexec.cleanupDaemonThreads=false
```

or equivalently

```bash
bash run.sh
```

It might take a while to compile and finish the initial upload of the files to Hadoop cluster.
Then we can input the commands to interact with the application.
The supported commands are:
```
bulk user ../db-generation/user.sql
bulk article ../db-generation/article.sql
bulk user_read ../db-generation/user_read.sql
populate be_read
populate popular_rank
query 5 popular_rank
```

The commands are self-explanatory. They can be executed one-by-one.

## Monitoring

The application provides web interfaces for monitoring the status of the services.

For Hadoop, visit

- [http://127.0.0.1:10088](http://127.0.0.1:10088)
- [http://127.0.0.1:8088](http://127.0.0.1:8088)
- Use the `jps` command inside Hadoop nodes.

For MySQL,
first enter the corresponding container:

Then use
```bash
innotop
```

We are also [Percona](https://www.percona.com/) on [http://127.0.0.1:8443](http://127.0.0.1:8443), but it requires manual configuration and we refer readers to the [official guide](https://docs.percona.com/percona-monitoring-and-management/setting-up/client/index.html).
