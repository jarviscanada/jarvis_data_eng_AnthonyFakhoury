# Linux Cluster Monitoring Agent
This project is under development. Since this project follows the GitFlow, the final work will be merged to the main branch after Team Code Team.

## Introduction

This project is designed to capture and log hardware and usage statistics from server hosts into a PostgreSQL database for monitoring and analysis purposes.  
The primary users of this system are system administrators and IT professionals who need to monitor server performance and resource utilization in real-time.  
The project captures critical hardware details such as CPU architecture, model, and speed, as well as dynamic usage statistics like memory availability and disk space.  
Technologies employed include Bash scripting for data collection and automation, PostgreSQL for database management, and Git for version control.  
The script utilizes common Linux commands (lscpu, vmstat, df) to gather system information and integrates with PostgreSQL for persistent data storage.  
This solution offers a lightweight, automated approach to system monitoring, enabling proactive infrastructure management and performance tuning.  

## Quick Start

- Start, Stop or Create a PostgreSQL instance using psql_docker.sh:
```sh 
./scripts/psql_docker.sh start|stop|create [db_username][db_password]
```
- Create tables using ddl.sql  
    Assuming this is already done
```sh
psql -h localhost -U postgres -W
postgres=# \l
postgres=# CREATE DATABASE host_agent;
```
We input the following:
```sh
psql -h localhost -U postgres -d host_agent -f sql/ddl.sql
```
- Insert hardware specs data into the DB using host_info.sh
```sh
bash scripts/host_info.sh psql_host psql_port db_name psql_user psql_password
```
- Insert hardware usage data into the DB using host_usage.sh
```sh
bash scripts/host_usage.sh psql_host psql_port db_name psql_user psql_password
```
- Setup a crontab entry to run host_usage.sh every minute
```sh
# in terminal
crontab -e
#then write the following:
* * * * * bash /home/rocky/dev/jarvis_data_eng_AnthonyFakhoury/linux_sql/scripts/host_usage.sh localhost 5432 host_agent postgres password &> /tmp/host_usage.log
```
## Implementation

The project implementation involves several key steps, utilizing various technologies to automate the process of monitoring and storing hardware specifications and usage data.

### 1. Setting Up the PostgreSQL Database
#### Starting the PostgreSQL Instance

We begin by setting up a PostgreSQL instance using a custom Bash script, `psql_docker.sh`, which manages the lifecycle of the PostgreSQL container. This script ensures the database is running and accessible for data storage and retrieval.
```sh
./psql_docker.sh start
```
Or we create a new instance
```sh
./psql_docker.sh create db_username db_password
```
#### Creating Tables

The database schema is defined in the `ddl.sql` script. This script creates two tables, `host_info` and `host_usage`, if they do not already exist. `host_info` stores static hardware specifications, while `host_usage` stores dynamic usage metrics.

```sh
psql -h localhost -U postgres -d host_agent -f ddl.sql
```

### 2. Capturing and Storing Hardware Specifications

The `host_info.sh` script captures the hardware specifications of the host machine and inserts this data into the `host_info` table. This script uses various Linux command-line tools to gather information such as CPU details, memory, and cache sizes.

#### Hardware Information Gathering

Commands like `lscpu`, `vmstat`, and `hostname` are used to gather detailed hardware information. The script then constructs an SQL `INSERT` statement to store this data in the database.

```sh
./host_info.sh localhost 5432 host_agent postgres mypassword
```

### 3. Capturing and Storing Hardware Usage Data

The `host_usage.sh` script collects real-time usage metrics, such as CPU usage, memory usage, and disk I/O, and inserts this data into the `host_usage` table. This script is designed to be run periodically to continuously monitor the system's performance.

#### Usage Data Gathering

Tools like `vmstat`, `iostat`, and `df` are used to collect usage metrics. The script then constructs an SQL `INSERT` statement to log this data into the database.

```sh
./host_usage.sh localhost 5432 host_agent postgres mypassword
```

### 4. Automation with Crontab

To ensure continuous monitoring, the `host_usage.sh` script is scheduled to run every minute using `cron`. This automation ensures that the database is regularly updated with the latest usage metrics without manual intervention.

#### Setting Up Crontab

```sh
* * * * * /path/to/your/host_usage.sh localhost 5432 host_agent postgres mypassword &> /tmp/host_usage.log
```

#### Technologies Used

- **Bash**: For scripting and automation.
- **PostgreSQL**: As the database management system.
- **Docker**: For containerizing the PostgreSQL instance.
- **Crontab**: For scheduling periodic tasks.
- **Linux Command-Line Tools**: For gathering hardware and usage data.

This implementation ensures a robust and automated system for monitoring and storing hardware specifications and usage data, leveraging a combination of scripting, database management, and scheduling technologies.

### Architecture
![Cluster Diagram](./assets/myImage.jpg)

## Scripts

### psql_docker.sh

This script is used to set up a PostgreSQL instance using Docker. It checks if the Docker container for PostgreSQL is already running, and if not, it creates and starts a new container.

#### Usage
```sh
# To start a PostgreSQL container
./psql_docker.sh start

# To stop the PostgreSQL container
./psql_docker.sh stop

# To create the PostgreSQL container
./psql_docker.sh create db_username db_password
```

### host_info.sh

This script captures the hardware specifications of the host machine and inserts the data into the `host_info` table in the PostgreSQL database. It retrieves CPU, memory, and other hardware information using commands like `lscpu` and `vmstat`.

#### Usage
```sh
./host_info.sh psql_host psql_port db_name psql_user psql_password
```

### host_usage.sh

This script captures the hardware usage statistics of the host machine and inserts the data into the `host_usage` table in the PostgreSQL database. It retrieves data such as memory usage, CPU idle time, and disk I/O using commands like `vmstat` and `df`.

#### Usage
```sh
./host_usage.sh psql_host psql_port db_name psql_user psql_password
```

### crontab

To automate the data collection, set up a crontab entry that periodically runs `host_usage.sh`. This ensures that the hardware usage statistics are regularly collected and stored in the database.

#### Usage
```sh
# Edit crontab file
crontab -e

# Add the following line to run host_usage.sh every minute
* * * * * /path/to/host_usage.sh psql_host psql_port db_name psql_user psql_password &> /tmp/host_usage.log
```

### queries.sql

The queries.sql file contains SQL queries to answer specific business questions related to the Linux Cluster Monitoring Agent project. These queries are designed to analyze the hardware information and usage data stored in the PostgreSQL database.

SQL Queries in queries.sql  
Group Hosts by Hardware Info: This query groups hosts by the number of CPUs and sorts them by memory size in descending order within each CPU group.  
Average Memory Usage: This query calculates the average used memory percentage over 5-minute intervals for each host.  
Detect Host Failures: This query detects host failures by identifying hosts that insert less than three data points within a 5-minute interval.  

#### Usage
```sh
# To run the queries
psql -h psql_host -p psql_port -d db_name -U psql_user -f queries.sql
```

## Database Modeling

### `host_info` Table Schema

| Column            | Data Type   | Constraints       | Description                                  |
|-------------------|-------------|-------------------|----------------------------------------------|
| id                | SERIAL      | PRIMARY KEY       | Auto-incrementing unique identifier          |
| hostname          | VARCHAR     | NOT NULL, UNIQUE  | Fully qualified hostname                     |
| cpu_number        | INT2        | NOT NULL          | Number of CPUs                               |
| cpu_architecture  | VARCHAR     | NOT NULL          | CPU architecture (e.g., x86_64)              |
| cpu_model         | VARCHAR     | NOT NULL          | Model of the CPU                             |
| cpu_mhz           | FLOAT8      | NOT NULL          | CPU frequency in MHz                         |
| l2_cache          | INT4        | NOT NULL          | L2 cache size in kB                          |
| total_mem         | INT4        | NOT NULL          | Total memory in kB                           |
| timestamp         | TIMESTAMP   | NOT NULL          | Current time in UTC                          |

### `host_usage` Table Schema

| Column            | Data Type   | Constraints       | Description                                  |
|-------------------|-------------|-------------------|----------------------------------------------|
| timestamp         | TIMESTAMP   | NOT NULL          | Current time in UTC                          |
| host_id           | SERIAL      | PRIMARY KEY       | Foreign key referencing `host_info` table    |
| memory_free       | INT4        | NOT NULL          | Free memory in MB                            |
| cpu_idle          | INT2        | NOT NULL          | CPU idle time in percentage                  |
| cpu_kernel        | INT2        | NOT NULL          | CPU kernel time in percentage                |
| disk_io           | INT4        | NOT NULL          | Number of disk I/O operations                |
| disk_available    | INT4        | NOT NULL          | Available disk space in MB                   |

## Test

### Testing Bash Scripts

To ensure the reliability and functionality of the bash scripts (`psql_docker.sh`, `host_info.sh`, `host_usage.sh`), each script was subjected to rigorous testing. Below are the testing steps and results for each script:

#### `psql_docker.sh`

**Testing Steps:**
1. Execute the script to start/create a PostgreSQL instance using Docker:
   ```sh
   ./psql_docker.sh start
   ./psql_docker.sh create db_username db_password
   ```
2. Verify the PostgreSQL container is running:
   ```sh
   docker ps
   ```
3. Stop the PostgreSQL instance using the script:
   ```sh
   ./psql_docker.sh stop
   ```
4. Verify the PostgreSQL container has stopped:
   ```sh
   docker ps -a
   ```

**Results:**
- The PostgreSQL container started and stopped successfully without errors.
- Verified the container's running state using `docker ps`.

#### `host_info.sh`

**Testing Steps:**
1. Execute the script to insert hardware specs data into the database:
   ```sh
   ./host_info.sh localhost 5432 host_agent postgres your_password
   ```
2. Verify the data insertion by querying the `host_info` table:
   ```sql
   SELECT * FROM host_info;
   ```

**Results:**
- Hardware specs data was successfully inserted into the `host_info` table.
- Verified the correctness of the inserted data with a SQL query.

#### `host_usage.sh` and `crontab`

**Testing Steps:**
1. Execute the script to insert hardware usage data into the database and make sure the crontab is configured:
   ```sh
   ./host_usage.sh localhost 5432 host_agent postgres your_password
   ```
2. Verify the data insertion by querying the `host_usage` table:
   ```sql
   SELECT * FROM host_usage;
   ```

**Results:**
- Hardware usage data was successfully inserted into the `host_usage` table.
- Verified the correctness of the inserted data with a SQL query.

#### `DDL.sql`

**Testing Steps:**
1. Execute the SQL commands in `DDL.sql` to create the necessary tables:
   ```sh
   psql -h localhost -U postgres -d host_agent -f ddl.sql
   ```
2. Verify the table creation by checking the schema:
   ```sql
   \dt
   ```

**Results:**
- The `host_info` and `host_usage` tables were created successfully without errors.
- Verified the table schemas to ensure all columns and constraints were correctly implemented.

### Overall Testing Result

All scripts were tested and verified to function as expected. The database operations, including table creation and data insertion, were executed successfully without any errors. The project setup and data management processes are confirmed to be reliable and efficient.

## Deployment

### Deployment Overview

The deployment of this project involves setting up the necessary environment, ensuring that the database and scripts are correctly placed and configured, and automating the data collection processes using crontab for continuous operation. Below are the detailed deployment steps.

### Steps

1. **GitHub Repository**
    - All project files, including scripts (`psql_docker.sh`, `host_info.sh`, `host_usage.sh`), SQL files (`ddl.sql`), and documentation, are maintained in a GitHub repository for version control and collaboration.

2. **Docker**
    - **Docker Image Setup:**
        - A Docker image containing PostgreSQL is used to ensure a consistent and isolated environment.
        - The `psql_docker.sh` script is used to start and manage the PostgreSQL container.
    - **Commands to Start Docker:**
      ```sh
      ./psql_docker.sh start
      ```

3. **Database Setup**
    - **Database Creation:**
        - After starting the Docker container, the database `host_agent` is manually created using `psql` command-line tool.
        - Connect to PostgreSQL instance and create the database:
          ```sql
          CREATE DATABASE host_agent;
          ```
    - **Table Creation:**
        - Use the `ddl.sql` script to create the necessary tables (`host_info` and `host_usage`).
        - Command to execute `ddl.sql`:
          ```sh
          psql -h localhost -U postgres -d host_agent -f ddl.sql
          ```

4. **Scripts Execution**
    - **Hardware Information Insertion:**
        - Execute `host_info.sh` to insert hardware specifications data into the `host_info` table.
          ```sh
          ./host_info.sh localhost 5432 host_agent postgres your_password
          ```
    - **Hardware Usage Insertion:**
        - Execute `host_usage.sh` to insert hardware usage data into the `host_usage` table.
          ```sh
          ./host_usage.sh localhost 5432 host_agent postgres your_password
          ```

5. **Crontab Setup**
    - **Automate Data Collection:**
        - Crontab is used to schedule the `host_usage.sh` script to run at regular intervals (e.g., every minute) to continuously collect and insert hardware usage data.
    - **Crontab Configuration:**
        - Edit the crontab file using:
          ```sh
          crontab -e
          ```
        - Add the following line to run `host_usage.sh` every minute:
          ```sh
          * * * * * /path/to/host_usage.sh localhost 5432 host_agent postgres your_password
          ```

### Summary

- **GitHub:** Repository management and version control.
- **Docker:** Consistent environment setup with PostgreSQL container.
- **Database:** Manual creation and configuration of `host_agent` database using `psql`.
- **Scripts:** Execution of bash scripts (`host_info.sh` and `host_usage.sh`) to insert data.
- **Crontab:** Automation of data collection at regular intervals.

By following these steps, the project is deployed in a structured and automated manner, ensuring continuous data collection and efficient management.

## Improvements
It looks like you're developing a Linux Cluster Monitoring Agent using various technologies like Bash scripting and PostgreSQL. Here are three potential improvements for your project:

1. **Enhanced Error Handling and Logging**: Implement robust error handling mechanisms in your Bash scripts (`host_info.sh` and `host_usage.sh`). Ensure that errors during data collection or database operations are properly logged to facilitate troubleshooting and maintenance. This could involve logging errors to a dedicated log file or integrating with system logging utilities.

2. **Data Validation and Integrity Checks**: Add data validation steps to ensure the integrity of collected data before insertion into the database. For instance, validate numeric values like memory usage and CPU metrics to avoid storing incorrect or misleading information. Implement checks to handle edge cases such as network failures during data transmission.

3. **Implementing Data Retention Policies**: Define and implement data retention policies for the `host_usage` table to manage the growth of your database over time. This can involve periodic archiving of old data to maintain optimal performance and storage efficiency. Consider strategies like partitioning data based on timestamps or using PostgreSQL features like table partitioning for better data management.

These improvements will help enhance the reliability, scalability, and maintainability of your monitoring agent, ensuring it meets long-term operational requirements effectively.
