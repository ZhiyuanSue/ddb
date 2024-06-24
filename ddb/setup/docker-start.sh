service ssh start
service mysql start 

NAMENODE_DIR=/usr/local/hadoop/tmp/dfs/name
mkdir -p /usr/local/hadoop/tmp/dfs/data

if [ ! -z "$START_HADOOP" ]; then
  if [ ! "$(ls -A $NAMENODE_DIR)" ]
  then
    echo "NameNode is not formatted. Formatting..."
    hdfs namenode -format
  fi
  bash /usr/local/hadoop/sbin/start-all.sh
fi

echo Init completed.
tail -f /dev/null
