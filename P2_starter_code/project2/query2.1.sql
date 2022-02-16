DROP VIEW no_friends;
CREATE VIEW no_friends AS 
SELECT DISTINCT U.User_id
FROM Users U
MINUS
SELECT F.user1_id
FROM  Friends F 
MINUS
SELECT F.user2_id
FROM Friends F;
-- ORDER BY UID;

SELECT U.User_id, U.FIRST_NAME, U.Last_Name
FROM  no_friends N, Users U
WHERE N.User_ID = U.User_id
ORDER BY U.User_id;

--DROP VIEW no_friends;

-- SELECT User_ID, First_Name, Last_Name 
-- FROM Users 
-- WHERE User_ID NOT IN 
-- ((SELECT USER1_ID 
-- FROM Friends 
-- ) 
-- UNION
-- (SELECT USER2_ID 
-- FROM Friends 
-- ))
-- ORDER BY User_ID ASC;