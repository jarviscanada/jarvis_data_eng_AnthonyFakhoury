-- 1) Switch to 'host_agent'
-- connect to the psql instance
psql -h localhost -U postgres -W

-- list all database
postgres=# \l

-- create a database
postgres=# CREATE DATABASE host_agent;

-- connect to the new database;
postgres=# \c host_agent;

-- 2) Create `host_info` table if not exist
-- table columns
id,hostname,cpu_number,cpu_architecture,cpu_model,cpu_mhz,L2_cache,total_mem,timestamp

-- sample values
-- all fields are required (NOT NULL)
id=1      # auto-increment
hostname=spry-framework-236416.internal  # fully qualified hostname
cpu_number=1
cpu_architecture=x86_64
cpu_model=Intel(R) Xeon(R) CPU @ 2.30GHz
cpu_mhz=2300.000
L2_cache=256     # in kB
total_mem=601324 # in kB
timestamp=2019-05-29 17:49:53 # Current time in UTC time zone

-- Create a host_info table
CREATE TABLE IF NOT EXISTS PUBLIC.host_info
  (
     id               SERIAL NOT NULL,
     hostname         VARCHAR NOT NULL,
     cpu_number       INT2 NOT NULL,
     cpu_architecture VARCHAR NOT NULL,
     cpu_model        VARCHAR NOT NULL,
     cpu_mhz          FLOAT8 NOT NULL,
     l2_cache         INT4 NOT NULL,
     "timestamp"      TIMESTAMP NULL,
     total_mem        INT4 NULL,
     CONSTRAINT host_info_pk PRIMARY KEY (id),
     CONSTRAINT host_info_un UNIQUE (hostname)
  );

-- Insert three sample data rows
INSERT INTO host_info (id, hostname, cpu_number, cpu_architecture, cpu_model, cpu_mhz, l2_cache, "timestamp", total_mem)
VALUES(1, 'jrvs-remote-desktop-centos7-6.us-central1-a.c.spry-framework-236416.internal', 1, 'x86_64', 'Intel(R) Xeon(R) CPU @ 2.30GHz', 2300, 256, '2019-05-29 17:49:53.000', 601324);

INSERT INTO host_info (id, hostname, cpu_number, cpu_architecture, cpu_model, cpu_mhz, l2_cache, "timestamp", total_mem)
VALUES(2, 'noe1', 1, 'x86_64', 'Intel(R) Xeon(R) CPU @ 2.30GHz', 2300, 256, '2019-05-29 17:49:53.000', 601324);

INSERT INTO host_info (id, hostname, cpu_number, cpu_architecture, cpu_model, cpu_mhz, l2_cache, "timestamp", total_mem)
VALUES(3, 'noe2', 1, 'x86_64', 'Intel(R) Xeon(R) CPU @ 2.30GHz', 2300, 256, '2019-05-29 17:49:53.000', 601324);

-- Verify inserted data
SELECT * FROM host_info;

-- 3) Create `host_usage` table if not exist
-- table columns
timestamp,host_id,memory_free,cpu_idle,cpu_kernel,disk_io,disk_available

-- sample values
-- all fields are required (NOT NULL)
timestamp=2019-05-29 16:53:28 #UTC time zone
host_id=1                     #host id from `hosts` table
memory_free= 256              #in MB
cpu_idle=95                   #in percentage
cpu_kernel=0                  #in percentage
disk_io=0                     #number of disk I/O
disk_available=31220          #in MB. root directory avaiable disk

-- Create a host_usage table
CREATE TABLE IF NOT EXISTS PUBLIC.host_usage
(
    "timestamp"    TIMESTAMP NOT NULL,
    host_id        SERIAL NOT NULL,
    memory_free    INT4 NOT NULL,
    cpu_idle       INT2 NOT NULL,
    cpu_kernel     INT2 NOT NULL,
    disk_io        INT4 NOT NULL,
    disk_available INT4 NOT NULL,
    CONSTRAINT host_usage_host_info_fk FOREIGN KEY (host_id) REFERENCES
        host_info(id)
);

-- Insert sample data
INSERT INTO host_usage ("timestamp", host_id, memory_free, cpu_idle, cpu_kernel, disk_io, disk_available)
VALUES('2019-05-29 15:00:00.000', 1, 300000, 90, 4, 2, 3);
INSERT INTO host_usage ("timestamp", host_id, memory_free, cpu_idle, cpu_kernel, disk_io, disk_available)
VALUES('2019-05-29 15:01:00.000', 1, 200000, 90, 4, 2, 3);

-- Verify inserted data
SELECT * FROM host_usage;