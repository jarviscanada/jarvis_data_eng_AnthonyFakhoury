-- 1
-- Group hosts by CPU number and sort by their memory size in descending order within each cpu_number group
SELECT
    cpu_number,
    id AS host_id,
    total_mem
FROM
    host_info
ORDER BY
    cpu_number,
    total_mem DESC;

-- 2
-- Create a function to round timestamps to the nearest 5-minute interval
CREATE FUNCTION round5(ts timestamp) RETURNS timestamp AS
$$
BEGIN
    RETURN date_trunc('hour', ts) + date_part('minute', ts)::int / 5 * interval '5 min';
END;
$$
    LANGUAGE PLPGSQL;

-- Verify the round5 function
SELECT host_id, "timestamp", round5("timestamp")
FROM host_usage;

-- Calculate the average used memory percentage over 5-minute intervals for each host
WITH rounded_usage AS (
    SELECT
        host_id,
        round5("timestamp") AS rounded_timestamp,
        total_mem - memory_free AS used_memory
    FROM
        host_usage
            JOIN
        host_info ON host_usage.host_id = host_info.id
)
SELECT
    host_id,
    hostname,
    rounded_timestamp AS "timestamp",
    AVG(used_memory * 100.0 / total_mem) AS avg_used_mem_percentage
FROM
    rounded_usage
        JOIN
    host_info ON rounded_usage.host_id = host_info.id
GROUP BY
    host_id, hostname, rounded_timestamp
ORDER BY
    host_id, rounded_timestamp;

-- 3
-- Create a function to round timestamps to the nearest 5-minute interval
CREATE OR REPLACE FUNCTION round5(ts timestamp) RETURNS timestamp AS
$$
BEGIN
    RETURN date_trunc('hour', ts) + date_part('minute', ts)::int / 5 * interval '5 min';
END;
$$
    LANGUAGE PLPGSQL;

-- Detect host failures
WITH rounded_usage AS (
    SELECT
        host_id,
        round5("timestamp") AS rounded_timestamp
    FROM
        host_usage
)
SELECT
    host_id,
    rounded_timestamp AS "timestamp",
    COUNT(*) AS data_points
FROM
    rounded_usage
GROUP BY
    host_id, rounded_timestamp
HAVING
    COUNT(*) < 3
ORDER BY
    host_id, rounded_timestamp;
