#! /bin/sh

# Capture CLI arguments
psql_host=$1
psql_port=$2
db_name=$3
psql_user=$4
psql_password=$5

# Validate arg number
if [ "$#" -ne 5 ]; then
    echo "Illegal number of parameters"
    exit 1
fi

# Capture machine stats (in MB) and current machine hostname
lscpu_out=`lscpu`
vmstat_mb=$(vmstat --unit M)
hostname=$(hostname -f)

# Get hardware info
cpu_number= $(echo "$lscpu_out"  | egrep "^CPU\(s\):" | awk '{print $2}' | xargs)
cpu_architecture= $(echo "$lscpu_out"  | egrep "^Architecture:" | awk '{print $2}' | xargs)
cpu_model= $(echo "$lscpu_out"  | egrep "^Model:" | awk '{print $2}' | xargs)
cpu_mhz= $(cat /proc/cpuinfo | egrep "cpu MHz" | awk '{print $4}' | head -n1 | xargs)
l2_cache= $(echo "$lscpu_out"  | egrep "^L2\scache:" | awk '{print $3}' | xargs)
total_mem= $(echo "$vmstat_mb" | tail -1 | awk '{print $4}')

# Get time in UTC format
timestamp= $(vmstat -t | awk '{print $18, $19}'| tail -1 | xargs)

## Check if host_id exists and create a new one if not
#host_id=$(psql -h $psql_host -p $psql_port -d $db_name -U $psql_user -t -c "SELECT id FROM host_info WHERE hostname='$hostname';" | xargs)
## If host_id is empty, insert a new record and get the new host_id
#if [ -z "$host_id" ]; then
#  # Query for the highest host_id
#  max_host_id=$(psql -h $psql_host -p $psql_port -d $db_name -U $psql_user -t -c "SELECT COALESCE(MAX(id), 0) FROM host_info;" | xargs)
#  host_id=$((max_host_id + 1))
#fi

# Insert hardware info into host_info table
insert_stmt="INSERT INTO host_info (hostname, cpu_number, cpu_architecture, cpu_model, cpu_mhz, l2_cache, "timestamp", total_mem)
VALUES($hostname, $cpu_number, $cpu_architecture, $cpu_model, $cpu_mhz, $l2_cache, '$timestamp', $total_mem);"

#set up env var for pql cmd
export PGPASSWORD=$psql_password
#Insert date into a database
psql -h $psql_host -p $psql_port -d $db_name -U $psql_user -c "$insert_stmt"
exit $?
