-- Question 1: Insert some data into a table
INSERT INTO cd.facilities (facid, name, membercost, guestcost, initialoutlay, monthlymaintenance)
VALUES (9, 'Spa', 20, 30, 100000, 800);

-- Questions 2: Insert calculated data into a table
INSERT INTO cd.facilities (facid, name, membercost, guestcost, initialoutlay, monthlymaintenance)
SELECT (SELECT max(facid) FROM cd.facilities)+1, 'Spa', 20, 30, 100000, 800;

-- Question 3: Update some existing data
UPDATE cd.facilities SET initialoutlay = 10000 where facid = 1;

-- Question 4: Update a row based on the contents of another row
UPDATE cd.facilities facs
SET
    membercost = (SELECT membercost * 1.1 FROM cd.facilities WHERE facid = 0),
    guestcost = (SELECT guestcost * 1.1 FROM cd.facilities WHERE facid = 0)
WHERE facs.facid = 1;

-- Question 5: Delete all bookings
DELETE FROM cd.bookings;

-- Question 6: Delete a member from the cd.members table
DELETE FROM cd.bookings WHERE memid = 37;

-- Question 7: Control which rows are retrieved - part 2
SELECT facid, name, membercost, monthlymaintenance
FROM cd.facilities
WHERE membercost > 0 AND membercost < monthlymaintenance/50.0;

-- Question 8: Basic string searches
SELECT * FROM cd.facilities WHERE name LIKE '%Tennis%';

-- Question 9: Matching against multiple possible values
SELECT * FROM cd.facilities WHERE facid IN (1,5);

-- Question 10: Working with dates
SELECT memid, surname, firstname, joindate
FROM cd.facilities
WHERE starttime>= '2012-09-01';

-- Question 11: Combining results from multiple queries
SELECT surname FROM cd.members UNION SELECT name FROM cd.facilities;

-- Question 12: Retrieve the start times of members' bookings
SELECT bkgs.starttime
FROM cd.bookings bkgs INNER JOIN cd.members mems ON mems.memid = bkgs.memid
WHERE mems.firstname='David'AND mems.surname='Farrell';

-- Question 13: Work out the start times of bookings for tennis courts
SELECT bkgs.starttime, facs.name
FROM cd.bookings bkgs INNER JOIN cd.facilities facs ON facs.facid = bkgs.facid
WHERE bkgs.starttime >='2012-09-21'
  AND bkgs.starttime < '2012-09-22'
  AND facs.name LIKE 'Tennis Court%'
ORDER BY bkgs.starttime;

-- Question 14: Produce a list of all members, along with their recommender
SELECT mem.firstname, mem.surname, rec.firstname, rec.surname
FROM cd.members mem LEFT OUTER JOIN cd.members rec
    ON mem.recommendedby = rec.memid
ORDER BY mem.surname, mem.firstname;

-- Question 15: Produce a list of all members who have recommended another member
SELECT DISTINCT rec.firstname, rec.surname
FROM cd.members mem INNER JOIN cd.members rec
    ON mem.recommendedby = rec.memid
ORDER BY rec.surname, rec.firstname;

-- Question 16: Produce a list of all members, along with their recommender, using no joins.
SELECT DISTINCT mem.firstname || ' ' || mem.surname as membername,
    (SELECT rec.firstname || ' ' || rec.surname as recommendername
     FROM cd.members rec
     WHERE rec.memid = mem.recommendedby
    )
FROM cd.members mem
ORDER BY membername;

-- Question 17: Count the number of recommendations each member makes.
SELECT recommendedby, count(*)
FROM cd.members
WHERE recommendedby IS NOT NULL
GROUP BY recommendedby
ORDER BY recommendedby;

-- Question 18: List the total slots booked per facility
SELECT facid, sum(slots) as TotalSlots
FROM cd.bookings
GROUP BY facid
ORDER BY facid;

-- Question 19: List the total slots booked per facility in a given month
SELECT facid, sum(slots) as TotalSlots
FROM cd.bookings
WHERE starttime >= '2012-09-01' AND starttime < '2012-10-01'
GROUP BY facid
ORDER BY TotalSlots;

-- Question 20: List the total slots booked per facility per mont
SELECT facid, EXTRACT(month FROM starttime) AS month, sum(slots) as TotalSlots
FROM cd.bookings
WHERE EXTRACT(year FROM starttime) = 2012
GROUP BY facid, month
ORDER BY facid, month;

-- Question 21: Find the count of members who have made at least one booking
SELECT count(DISTINCT memid) FROM cd.bookings;

-- Question 22: List each member's first booking after September 1st 2012
SELECT mems.surname, mems.firstname, mems.memid, min(bkgs.starttime) as firsttime
FROM cd.members mems INNER JOIN cd.bookings bkgs ON bkgs.memid = mems.memid
WHERE bkgs.starttime >= '2012-09-01'
GROUP BY mems.surname, mems.firstname, mems.memid
ORDER BY mems.memid;

-- Question 23: Produce a list of member names, with each row containing the total member count
SELECT count(*) over(), firstname, surname
FROM cd.members
ORDER BY joindate;

-- Question 24: Produce a numbered list of members
SELECT row_number() over(ORDER BY joindate), firstname, surname
FROM cd.members
ORDER BY joindate;

-- Question 25: Output the facility id that has the highest number of slots booked, again
SELECT facid, total FROM
    (SELECT facid, sum(slots) AS total, rank() over (order by sum(slots) desc) AS rank
     FROM cd.bookings
     GROUP BY facid) AS ranking
WHERE rank = 1;

-- Question 26: Format the names of members
SELECT surname || ', ' || firstname AS name FROM cd.members;

-- Question 27: Find telephone numbers with parentheses
SELECT memid, telephone FROM cd.members WHERE telephone SIMILAR TO '%[()]%';

-- Question 28: Count the number of members whose surname starts with each letter of the alphabet
SELECT substr(mems.surname,1,1) as letter, count(*)
FROM cd.members mems
GROUP BY letter
ORDER BY letter;
