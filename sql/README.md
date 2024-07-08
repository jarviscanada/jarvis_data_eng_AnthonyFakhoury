# Introduction
### Project Introduction: Mastering SQL Queries

This project aims to help learners master SQL by practicing additional SQL query questions. 
It involves setting up a PostgreSQL instance using Docker, optionally using a SQL IDE for enhanced development, 
and writing SQL DDL statements to create necessary tables. The project includes practical exercises on modifying data, 
basic queries, joins, aggregations, and string functions, with a focus on common interview questions. 
Learners will also verify their solutions using an online judge and document their progress in a GitHub repository. 
The goal is to ensure a solid understanding of SQL and its practical applications in database management and 
manipulation.

# SQL Queries

###### Table Setup (DDL)
```sql
CREATE TABLE cd.members
(
memid integer NOT NULL,
surname character varying(200) NOT NULL,
firstname character varying(200) NOT NULL,
address character varying(300) NOT NULL,
zipcode integer NOT NULL,
telephone character varying(20) NOT NULL,
recommendedby integer,
joindate timestamp NOT NULL,
CONSTRAINT members_pk PRIMARY KEY (memid),
CONSTRAINT fk_members_recommendedby FOREIGN KEY (recommendedby)
REFERENCES cd.members(memid) ON DELETE SET NULL
);

CREATE TABLE cd.bookings
(
    bookid integer NOT NULL,
    facid integer NOT NULL,
    memid integer NOT NULL,
    starttime timestamp NOT NULL,
    slots integer NOT NULL,
    CONSTRAINT bookings_pk PRIMARY KEY (bookid),
    CONSTRAINT fk_bookings_facid FOREIGN KEY (facid) REFERENCES cd.facilities(facid),
    CONSTRAINT fk_bookings_memid FOREIGN KEY (memid) REFERENCES cd.members(memid)
);

CREATE TABLE cd.facilities
(
    facid integer NOT NULL,
    name character varying(100) NOT NULL,
    membercost numeric NOT NULL,
    guestcost numeric NOT NULL,
    initialoutlay numeric NOT NULL,
    monthlymaintenance numeric NOT NULL,
    CONSTRAINT facilities_pk PRIMARY KEY (facid)
);
```
###### Question 1: Insert some data into a table

```sql
INSERT INTO cd.facilities (facid, name, membercost, guestcost, initialoutlay, monthlymaintenance)
VALUES (9, 'Spa', 20, 30, 100000, 800);
```

###### Questions 2: Insert calculated data into a table

```sql
INSERT INTO cd.facilities (facid, name, membercost, guestcost, initialoutlay, monthlymaintenance)
SELECT (SELECT max(facid) FROM cd.facilities)+1, 'Spa', 20, 30, 100000, 800;
```

###### Questions 3: Update some existing data

```sql
UPDATE cd.facilities SET initialoutlay = 10000 where facid = 1;
```

###### Questions 4: Update a row based on the contents of another row

```sql
UPDATE cd.facilities facs
SET
    membercost = (SELECT membercost * 1.1 FROM cd.facilities WHERE facid = 0),
    guestcost = (SELECT guestcost * 1.1 FROM cd.facilities WHERE facid = 0)
WHERE facs.facid = 1; 
```

###### Question 5: Delete all bookings

```sql
DELETE FROM cd.bookings;
```

###### Question 6: Delete a member from the cd.members table

```sql
DELETE FROM cd.bookings WHERE memid = 37;
```

###### Question 7: Control which rows are retrieved - part 2

```sql
SELECT facid, name, membercost, monthlymaintenance
FROM cd.facilities
WHERE membercost > 0 AND membercost < monthlymaintenance/50.0;
```

###### Question 8: Basic string searches

```sql
SELECT * FROM cd.facilities WHERE name LIKE '%Tennis%';
```

###### Question 9: Matching against multiple possible values

```sql
SELECT * FROM cd.facilities WHERE facid IN (1,5);
```

###### Question 10: Working with dates

```sql
SELECT memid, surname, firstname, joindate
FROM cd.facilities
WHERE starttime>= '2012-09-01';
```

###### Question 11: Combining results from multiple queries

```sql
SELECT surname FROM cd.members UNION SELECT name FROM cd.facilities; 
```

###### Question 12: Retrieve the start times of members' bookings

```sql
SELECT bkgs.starttime
FROM cd.bookings bkgs INNER JOIN cd.members mems ON mems.memid = bkgs.memid
WHERE mems.firstname='David'AND mems.surname='Farrell'; 
```

###### Question 13: Work out the start times of bookings for tennis courts

```sql
SELECT bkgs.starttime, facs.name
FROM cd.bookings bkgs INNER JOIN cd.facilities facs ON facs.facid = bkgs.facid
WHERE bkgs.starttime >='2012-09-21'
  AND bkgs.starttime < '2012-09-22'
  AND facs.name LIKE 'Tennis Court%'
ORDER BY bkgs.starttime;
```

###### Question 14: Produce a list of all members, along with their recommender

```sql
SELECT mem.firstname, mem.surname, rec.firstname, rec.surname
FROM cd.members mem LEFT OUTER JOIN cd.members rec
    ON mem.recommendedby = rec.memid
ORDER BY mem.surname, mem.firstname;
```

###### Question 15: Produce a list of all members who have recommended another member

```sql
SELECT DISTINCT rec.firstname, rec.surname
FROM cd.members mem INNER JOIN cd.members rec
    ON mem.recommendedby = rec.memid
ORDER BY rec.surname, rec.firstname;
```

###### Question 16: Produce a list of all members, along with their recommender, using no joins.

```sql
SELECT DISTINCT mem.firstname || ' ' || mem.surname as membername,
    (SELECT rec.firstname || ' ' || rec.surname as recommendername
     FROM cd.members rec
     WHERE rec.memid = mem.recommendedby
    )
FROM cd.members mem
ORDER BY membername;
```

###### Question 17: Count the number of recommendations each member makes.

```sql
SELECT recommendedby, count(*)
FROM cd.members
WHERE recommendedby IS NOT NULL
GROUP BY recommendedby
ORDER BY recommendedby;
```

###### Question 18: List the total slots booked per facility

```sql
SELECT facid, sum(slots) as TotalSlots
FROM cd.bookings
GROUP BY facid
ORDER BY facid;
```

###### Question 19: List the total slots booked per facility in a given month

```sql
SELECT facid, sum(slots) as TotalSlots
FROM cd.bookings
WHERE starttime >= '2012-09-01' AND starttime < '2012-10-01'
GROUP BY facid
ORDER BY TotalSlots;
```

###### Question 20: List the total slots booked per facility per month

```sql
SELECT facid, EXTRACT(month FROM starttime) AS month, sum(slots) as TotalSlots
FROM cd.bookings
WHERE EXTRACT(year FROM starttime) = 2012
GROUP BY facid, month
ORDER BY facid, month;
```

###### Question 21: Find the count of members who have made at least one booking

```sql
SELECT count(DISTINCT memid) FROM cd.bookings;
```

###### Question 22: List each member's first booking after September 1st 2012

```sql
SELECT mems.surname, mems.firstname, mems.memid, min(bkgs.starttime) as firsttime
FROM cd.members mems INNER JOIN cd.bookings bkgs ON bkgs.memid = mems.memid
WHERE bkgs.starttime >= '2012-09-01'
GROUP BY mems.surname, mems.firstname, mems.memid
ORDER BY mems.memid;
```

###### Question 23: Produce a list of member names, with each row containing the total member count

```sql
SELECT count(*) over(), firstname, surname
FROM cd.members
ORDER BY joindate;
```

###### Question 24: Produce a numbered list of members

```sql
SELECT row_number() over(ORDER BY joindate), firstname, surname
FROM cd.members
ORDER BY joindate;
```

###### Question 25: Output the facility id that has the highest number of slots booked, again

```sql
SELECT facid, total FROM
    (SELECT facid, sum(slots) AS total, rank() over (order by sum(slots) desc) AS rank
     FROM cd.bookings
     GROUP BY facid) AS ranking
WHERE rank = 1;
```

###### Question 26: Format the names of members

```sql
SELECT surname || ', ' || firstname AS name FROM cd.members; 
```

###### Question 27: Find telephone numbers with parentheses

```sql
SELECT memid, telephone FROM cd.members WHERE telephone SIMILAR TO '%[()]%';
```

###### Question 28: Count the number of members whose surname starts with each letter of the alphabet

```sql
SELECT substr(mems.surname,1,1) as letter, count(*)
FROM cd.members mems
GROUP BY letter
ORDER BY letter; 
```
