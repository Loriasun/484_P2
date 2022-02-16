SELECT User_ID, First_Name, Last_Name
FROM Users 
WHERE User_ID IN (
    SELECT CC.User_id
    FROM User_Current_Cities CC, User_Hometown_cities HC
    WHERE CC.User_ID = HC.User_ID 
    AND CC.current_city_id IS NOT NULL 
    AND  HC.hometown_city_id is NOT NULL
    AND CC.current_city_id <> HC.hometown_city_id
)
ORDER BY User_ID;