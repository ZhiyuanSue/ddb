set -e

# if lock file not exists
if [ ! -f /perconna-lock ]; then
    # run command
    pmm-agent setup --config-file=/usr/local/percona/pmm2/config/pmm-agent.yaml --server-address=percona-server --server-insecure-tls --server-username=admin --server-password=admin
fi

pmm-agent --config-file=/usr/local/percona/pmm2/config/pmm-agent.yaml &
pid_command1=$!

if [ ! -f /usr/local/percona/lock ]; then
    pmm-admin add mysql ddb1 hadoop1 --username=root --password=root --query-source=perfschema --tls-skip-verify
    pmm-admin add mysql ddb2 hadoop2 --username=root --password=root --query-source=perfschema --tls-skip-verify
    pmm-admin add mysql ddb3 hadoop3 --username=root --password=root --query-source=perfschema --tls-skip-verify
    # create lock file
    touch /usr/local/percona/lock
fi

wait $pid_command1
