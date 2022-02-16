-- CREATE VIEW top_photo AS 
-- SELECT DISTINCT COUNT(*) AS num_tags, Tag_Photo_id
-- FROM TAGS 
-- GROUP BY Tag_Photo_id
-- -- ORDER BY num_tags DESC, Tag_Photo_id DESC;

-- SELECT DISTINCT P.Photo_id, P.Album_id, P.Photo_link,A.Album_name, U.User_id,U.First_Name,U.Last_Name, top_photo.num_tags
-- FROM Photos P, Users U, top_photo, Albums A
-- WHERE P.Photo_id = top_photo.Tag_Photo_id AND A.Album_id = P.Album_id AND U.User_ID IN(
--     SELECT T.tag_subject_id
--     FROM TAGS T, top_photo TP 
--     WHERE T.Tag_Photo_id = TP.Tag_Photo_id
-- )
-- ORDER BY top_photo.num_tags DESC, P.Photo_id ASC, U.User_id ASC;
 
SELECT DISTINCT P.Photo_id, P.Album_id, P.Photo_link, A.Album_name, U.User_id, U.First_Name,U.Last_Name 
FROM Photos P
LEFT JOIN  Tags T on T.tag_Photo_id = P.Photo_id 
LEFT JOIN Users U on U.User_ID = T.Tag_subject_ID 
LEFT JOIN  Albums A ON  A.Album_id = P.Album_id
WHERE P.Photo_id = 4524  
ORDER BY U.User_id;