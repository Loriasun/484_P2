SELECT U1.User_id, U1.First_Name,U1.Last_Name,
        U2.First_Name,U2.Last_Name, U2.User_id, COUNT(T1.Tag_Photo_id) AS tag_count
FROM Users U1, Users  U2, Tags T1, Tags T2
WHERE U1.Gender = U2.Gender AND ABS(U1.Year_of_birth - U2.Year_of_birth) <= YearDiff AND U1.user_id < U2.user_id
    AND U1.User_ID = T1.tag_subject_id AND U2.User_ID = T2.tag_subject_id AND T1.Tag_Photo_id = T2.Photo_id
    AND NOT EXISTS(
        SELECT *
        FROM Friends F 
        WHERE U1.User_ID = F.user1_id AND U2.user_id = F.uesr2_id
    )
ORDER BY tag_count DESC, U1.user_id, U2.user_id;