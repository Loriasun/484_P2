package project2;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;

/*
    The StudentFakebookOracle class is derived from the FakebookOracle class and implements
    the abstract query functions that investigate the database provided via the <connection>
    parameter of the constructor to discover specific information.
*/
public final class StudentFakebookOracle extends FakebookOracle {
    // [Constructor]
    // REQUIRES: <connection> is a valid JDBC connection
    public StudentFakebookOracle(Connection connection) {
        oracle = connection;
    }
    
    @Override
    // Query 0
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the total number of users for which a birth month is listed
    //        (B) Find the birth month in which the most users were born
    //        (C) Find the birth month in which the fewest users (at least one) were born
    //        (D) Find the IDs, first names, and last names of users born in the month
    //            identified in (B)
    //        (E) Find the IDs, first names, and last name of users born in the month
    //            identified in (C)
    //
    // This query is provided to you completed for reference. Below you will find the appropriate
    // mechanisms for opening up a statement, executing a query, walking through results, extracting
    // data, and more things that you will need to do for the remaining nine queries
    public BirthMonthInfo findMonthOfBirthInfo() throws SQLException {
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll, FakebookOracleConstants.ReadOnly)) {
            // Step 1
            // ------------
            // * Find the total number of users with birth month info
            // * Find the month in which the most users were born
            // * Find the month in which the fewest (but at least 1) users were born
            ResultSet rst = stmt.executeQuery(
                "SELECT COUNT(*) AS Birthed, Month_of_Birth " +         // select birth months and number of uses with that birth month
                "FROM " + UsersTable + " " +                            // from all users
                "WHERE Month_of_Birth IS NOT NULL " +                   // for which a birth month is available
                "GROUP BY Month_of_Birth " +                            // group into buckets by birth month
                "ORDER BY Birthed DESC, Month_of_Birth ASC");           // sort by users born in that month, descending; break ties by birth month
            
            int mostMonth = 0;
            int leastMonth = 0;
            int total = 0;
            while (rst.next()) {                       // step through result rows/records one by one
                if (rst.isFirst()) {                   // if first record
                    mostMonth = rst.getInt(2);         //   it is the month with the most
                }
                if (rst.isLast()) {                    // if last record
                    leastMonth = rst.getInt(2);        //   it is the month with the least
                }
                total += rst.getInt(1);                // get the first field's value as an integer
            }
            BirthMonthInfo info = new BirthMonthInfo(total, mostMonth, leastMonth);
            
            // Step 2
            // ------------
            // * Get the names of users born in the most popular birth month
            rst = stmt.executeQuery(
                "SELECT User_ID, First_Name, Last_Name " +                // select ID, first name, and last name
                "FROM " + UsersTable + " " +                              // from all users
                "WHERE Month_of_Birth = " + mostMonth + " " +             // born in the most popular birth month
                "ORDER BY User_ID");                                      // sort smaller IDs first
                
            while (rst.next()) {
                info.addMostPopularBirthMonthUser(new UserInfo(rst.getLong(1), rst.getString(2), rst.getString(3)));
            }

            // Step 3
            // ------------
            // * Get the names of users born in the least popular birth month
            rst = stmt.executeQuery(
                "SELECT User_ID, First_Name, Last_Name " +                // select ID, first name, and last name
                "FROM " + UsersTable + " " +                              // from all users
                "WHERE Month_of_Birth = " + leastMonth + " " +            // born in the least popular birth month
                "ORDER BY User_ID");                                      // sort smaller IDs first
                
            while (rst.next()) {
                info.addLeastPopularBirthMonthUser(new UserInfo(rst.getLong(1), rst.getString(2), rst.getString(3)));
            }

            // Step 4
            // ------------
            // * Close resources being used
            rst.close();
            stmt.close();                            // if you close the statement first, the result set gets closed automatically

            return info;

        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
            return new BirthMonthInfo(-1, -1, -1);
        }
    }
    
    @Override
    // Query 1
    // -----------------------------------------------------------------------------------
    // GOALS: (A) The first name(s) with the most letters
    //        (B) The first name(s) with the fewest letters
    //        (C) The first name held by the most users
    //        (D) The number of users whose first name is that identified in (C)
    public FirstNameInfo findNameInfo() throws SQLException {
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll, FakebookOracleConstants.ReadOnly)) {
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                FirstNameInfo info = new FirstNameInfo();
                info.addLongName("Aristophanes");
                info.addLongName("Michelangelo");
                info.addLongName("Peisistratos");
                info.addShortName("Bob");
                info.addShortName("Sue");
                info.addCommonName("Harold");
                info.addCommonName("Jessica");
                info.setCommonNameCount(42);
                return info;
            */
            System.out.println("query1");
           ResultSet rst = stmt.executeQuery(
                "SELECT LENGTH(First_Name) AS len_first_name " +
                "FROM " + UsersTable + " " +
                "GROUP BY First_Name "+
                "ORDER BY LENGTH(First_Name) DESC");
            int longest = 0;
            int shortest = 0;
            while(rst.next()){
               if(rst.isFirst()){
                    longest = rst.getInt(1);
                    System.out.println("getting longest");
               }
               if(rst.isLast()){
                    shortest = rst.getInt(1);
                    System.out.println("getting shortest");
               }
           }

           FirstNameInfo firstname = new FirstNameInfo();

           rst = stmt.executeQuery(
                "SELECT DISTINCT First_Name " +                // select ID, first name, and last name
                "FROM " + UsersTable + " " +                              // from all users
                "WHERE LENGTH(First_Name) = " + longest + " " +            // born in the least popular birth month
                "ORDER BY First_Name"   
           );
           while(rst.next()){
               firstname.addLongName(rst.getString(1));
               System.out.println("getting longest_first_name");
           }

           rst = stmt.executeQuery(
               "SELECT DISTINCT First_Name " +                // select ID, first name, and last name
                "FROM " + UsersTable + " " +                              // from all users
                "WHERE LENGTH(First_Name) = " + shortest + " " +            // born in the least popular birth month
                "ORDER BY First_Name"   
           );
           while(rst.next()){
               firstname.addShortName(rst.getString(1));
               System.out.println("getting shortest_first_name");
           }

           // step2: finding the most common first name
            System.out.println("query1.2");
           rst = stmt.executeQuery(
            "SELECT COUNT(*) AS most_first_name, First_Name " +
            "FROM " + UsersTable + " " +
            "GROUP BY First_Name " +
            "ORDER BY most_first_name DESC, First_Name ASC "
            );
        int most_common = 0;
        while(rst.next()){
            if(rst.isFirst()){
                most_common = rst.getInt(1);
                firstname.setCommonNameCount(most_common);
                break;
            }
        }
       // int temp = 0;
        rst.first();
        rst.previous();
        while(rst.next()){
            if(rst.getInt(1) == most_common){
                firstname.addCommonName(rst.getString(2));
               // System.out.println("add commonname");
            }
            // if(rst.getInt(1)<rst.getInt(1)){
            //     break;
            // }
        }
           rst.close();
           stmt.close();
            
           return firstname;                // placeholder for compilation
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
            return new FirstNameInfo();
        }
    }
    
    @Override
    // Query 2
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the IDs, first names, and last names of users without any friends
    //
    // Be careful! Remember that if two users are friends, the Friends table only contains
    // the one entry (U1, U2) where U1 < U2.
    public FakebookArrayList<UserInfo> lonelyUsers() throws SQLException {
        FakebookArrayList<UserInfo> results = new FakebookArrayList<UserInfo>(", ");
        
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll, FakebookOracleConstants.ReadOnly)) {
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                UserInfo u1 = new UserInfo(15, "Abraham", "Lincoln");
                UserInfo u2 = new UserInfo(39, "Margaret", "Thatcher");
                results.add(u1);
                results.add(u2);
            */
           stmt.executeUpdate(
                 "CREATE VIEW no_friends AS " + 
                 "SELECT DISTINCT U.User_id "  +
                 "FROM " + UsersTable + " U " +
                 "MINUS " +
                 "SELECT DISTINCT F.user1_id "+
                 "FROM " + FriendsTable + " F " +
                 "MINUS " +
                 "SELECT DISTINCT F.user2_id " +
                 "FROM " + FriendsTable + " F" 
            );
            ResultSet rst  = stmt.executeQuery(
                "SELECT U.User_id, U.FIRST_NAME, U.Last_Name " +
                "FROM no_friends " + " N, " + UsersTable + " U " +
                "WHERE N.User_ID = U.User_id " + 
                "ORDER BY U.User_id"
            );

           

            while (rst.next()) {
                UserInfo user = new UserInfo(rst.getLong(1), rst.getString(2), rst.getString(3));
                results.add(user);
            }

            stmt.executeUpdate(
                "DROP VIEW no_friends"
            );

            rst.close();
            stmt.close();
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        return results;
    }
    
    @Override
    // Query 3
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the IDs, first names, and last names of users who no longer live
    //            in their hometown (i.e. their current city and their hometown are different)
    public FakebookArrayList<UserInfo> liveAwayFromHome() throws SQLException {
        FakebookArrayList<UserInfo> results = new FakebookArrayList<UserInfo>(", ");
        
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll, FakebookOracleConstants.ReadOnly)) {
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                UserInfo u1 = new UserInfo(9, "Meryl", "Streep");
                UserInfo u2 = new UserInfo(104, "Tom", "Hanks");
                results.add(u1);
                results.add(u2);
            */
            ResultSet rst = stmt.executeQuery(
                "SELECT User_ID, First_Name, Last_Name " +
                "FROM " + UsersTable + " " +
                "WHERE User_ID IN ( " + 
                "SELECT CC.User_id " + 
                "FROM " + CurrentCitiesTable + " CC, " +
                HometownCitiesTable + " HC " +
                "WHERE CC.User_ID = HC.User_ID " + 
                "AND CC.current_city_id IS NOT NULL " +
                "AND  HC.hometown_city_id is NOT NULL " +
                "AND CC.current_city_id <> HC.hometown_city_id ) " +
                "ORDER BY User_ID"
            );

            while(rst.next()){
                UserInfo user = new UserInfo(rst.getLong(1), rst.getString(2), rst.getString(3));
                results.add(user);
            }
            rst.close();
            stmt.close();
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        return results;
    }
    
    @Override
    // Query 4
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the IDs, links, and IDs and names of the containing album of the top
    //            <num> photos with the most tagged users
    //        (B) For each photo identified in (A), find the IDs, first names, and last names
    //            of the users therein tagged
    public FakebookArrayList<TaggedPhotoInfo> findPhotosWithMostTags(int num) throws SQLException {
        FakebookArrayList<TaggedPhotoInfo> results = new FakebookArrayList<TaggedPhotoInfo>("\n");
        
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll, FakebookOracleConstants.ReadOnly)) {
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                PhotoInfo p = new PhotoInfo(80, 5, "www.photolink.net", "Winterfell S1");
                UserInfo u1 = new UserInfo(3901, "Jon", "Snow");
                UserInfo u2 = new UserInfo(3902, "Arya", "Stark");
                UserInfo u3 = new UserInfo(3903, "Sansa", "Stark");
                TaggedPhotoInfo tp = new TaggedPhotoInfo(p);
                tp.addTaggedUser(u1);
                tp.addTaggedUser(u2);
                tp.addTaggedUser(u3);
                results.add(tp);
                
            */
            ResultSet rst = stmt.executeQuery(
                "SELECT DISTINCT Tag_Photo_id ,COUNT(*) AS num_tags " +
                "FROM " + TagsTable +  " " +
                "GROUP BY Tag_Photo_id " +
                "ORDER BY num_tags DESC, Tag_Photo_id "
            );
            System.out.println("query created");
            
            int it = 0;
            PhotoInfo photo = new PhotoInfo(0, 0, "", "");
            TaggedPhotoInfo tagphoto = new TaggedPhotoInfo(photo);
           
           while(rst.next()){
               if(num > it){
                   it ++;
               }
               // if num >= it, had finished top num photos
               else{
                   break;
               }
            System.out.println("line 357");
            try (Statement stmt_1 = oracle.createStatement(FakebookOracleConstants.AllScroll, FakebookOracleConstants.ReadOnly)){
                ResultSet rst_1 = stmt_1.executeQuery(
                    "SELECT DISTINCT P.Photo_id, P.Album_id, P.Photo_link, A.Album_name, U.User_id, U.First_Name,U.Last_Name " + 
                    "FROM " + PhotosTable + " P " + 
                    "LEFT JOIN " + TagsTable + " T on T.tag_Photo_id = " + rst.getInt(1) + " " +
                    "LEFT JOIN "+ UsersTable + " U on U.User_ID = T.Tag_subject_ID " + 
                    "LEFT JOIN " + AlbumsTable + " A ON  A.Album_id = P.Album_id " +
                    "WHERE P.Photo_id = " + rst.getInt(1) + " " +
                    "ORDER BY U.User_id"
                );
                  
                System.out.println("query1 created");
                 //int temp_count = 0;
                while(rst_1.next()){
                    if(rst_1.isFirst()){
                        photo = new PhotoInfo(rst_1.getLong(1),rst_1.getLong(2),rst_1.getString(3),rst_1.getString(4));  
                        tagphoto = new TaggedPhotoInfo(photo);
                        System.out.println("line 375");
                    }
                    UserInfo user = new UserInfo(rst_1.getLong(5),rst_1.getString(6),rst_1.getString(7));
                    tagphoto.addTaggedUser(user);
                    System.out.println("line 379");                    
                }//end inner while
                rst_1.close();
                stmt_1.close();
            }// end inner try
            catch (SQLException e) {
                System.err.println(e.getMessage());
            }
                results.add(tagphoto);
           }//end outer while  
            rst.close();
            stmt.close();
        }//end outer try
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return results;
    }
    
    @Override
    // Query 5
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the IDs, first names, last names, and birth years of each of the two
    //            users in the top <num> pairs of users that meet each of the following
    //            criteria:
    //              (i) same gender
    //              (ii) tagged in at least one common photo
    //              (iii) difference in birth years is no more than <yearDiff>
    //              (iv) not friends
    //        (B) For each pair identified in (A), find the IDs, links, and IDs and names of
    //            the containing album of each photo in which they are tagged together
    public FakebookArrayList<MatchPair> matchMaker(int num, int yearDiff) throws SQLException {
        FakebookArrayList<MatchPair> results = new FakebookArrayList<MatchPair>("\n");
        
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll, FakebookOracleConstants.ReadOnly)) {
            /*
                MatchPair(UserInfo user1, long user1yr, UserInfo user2, long user2yr)
                PhotoInfo(long pID, long aID, String link, String albName)
                UserInfo(long id, String fname, String lname)

                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                UserInfo u1 = new UserInfo(93103, "Romeo", "Montague");
                UserInfo u2 = new UserInfo(93113, "Juliet", "Capulet");
                MatchPair mp = new MatchPair(u1, 1597, u2, 1597);
                PhotoInfo p = new PhotoInfo(167, 309, "www.photolink.net", "Tragedy");
                mp.addSharedPhoto(p);
                results.add(mp);
            */
           
            ResultSet rst = stmt.executeQuery(
                "SELECT U1.User_id, U2.User_id,COUNT(T1.Tag_Photo_id) AS tag_count " + 
                "FROM " + UsersTable + " U1, " + UsersTable +  " U2, " + TagsTable+  " T1, "+ TagsTable+ " T2 " +
                "WHERE U1.Gender = U2.Gender AND ABS(U1.Year_of_birth - U2.Year_of_birth) <= " + yearDiff + " AND U1.user_id < U2.user_id " +
                "AND U1.User_ID = T1.tag_subject_id AND U2.User_ID = T2.tag_subject_id AND T1.Tag_Photo_id = T2.tag_photo_id " +
                "AND NOT EXISTS( " +
                "SELECT * FROM " + FriendsTable +  " F "+
                "WHERE U1.User_ID = F.user1_id AND U2.user_id = F.user2_id) "+
                "GROUP BY U1.User_ID, U2.User_ID " +
                "ORDER BY tag_count DESC, U1.user_id, U2.user_id"
            );
            int i = 0;
            System.out.println("query1 created");
            while(rst.next()){
                System.out.println("line 443");
                if(num >i){
                    i++;
                    // results.add(mp);
                }
                else{
                    break;
                }
                try(Statement stmt_1 = oracle.createStatement(FakebookOracleConstants.AllScroll, FakebookOracleConstants.ReadOnly)){
                      System.out.println("line 452");
                     ResultSet rst_1= stmt_1.executeQuery(
                        "SELECT U1.User_id, U2.User_id, U1.First_Name, U1.Last_Name,U2.First_Name,U2.Last_Name, U1.year_of_birth, U2.year_of_birth " + 
                        "FROM " + UsersTable + " U1, " + UsersTable +  " U2, " +
                        "WHERE U1.User_ID ="  + rst.getLong(1) +  " AND U2.User_ID = " + rst.getLong(2) + " "
                    );
                     System.out.println("query2 created");
                    UserInfo u1 = new UserInfo(rst_1.getLong(1),rst_1.getString(3),rst_1.getString(4));
                    UserInfo u2 = new UserInfo(rst_1.getLong(2),rst_1.getString(5),rst_1.getString(6));
                    MatchPair mp = new MatchPair(u1,rst_1.getInt(7),u2,rst_1.getInt(8));
                        System.out.println("Line 445");
                //  PhotoInfo(long pID, long aID, String link, String albName)
                    try (Statement stmt_2 = oracle.createStatement(FakebookOracleConstants.AllScroll, FakebookOracleConstants.ReadOnly)){
                        ResultSet rst_2 = stmt_2.executeQuery(
                            "SELECT P.Photo_id, P.Album_id, P.Photo_link, A.Album_name " +
                            "FROM " + PhotosTable + " P, " + AlbumsTable + " A, " + TagsTable + " T1, "+ TagsTable + " T2 " +
                            "WHERE P.photo_id = T1.tag_photo_id AND T1.tag_subject_id = " + rst.getLong(1) + " AND T2.tag_subject_id = " + rst.getLong(2) +
                                    "AND T1.tag_photo_id=T2.tag_photo_id AND A.Album_id = P.Album_id " +
                            "ORDER BY P.Photo_id"
                        );
                        System.out.println("Line 455");
                        while(rst_2.next()){
                            PhotoInfo p = new PhotoInfo(rst_2.getLong(1),rst_2.getLong(2),rst_2.getString(3),rst_2.getString(4));
                            mp.addSharedPhoto(p);
                        }
                        rst_2.close();
                        stmt_2.close();
                        results.add(mp);
                    }// end try
                    catch (SQLException e) {
                        System.err.println(e.getMessage());
                    }
                    rst_1.close();
                    stmt_1.close();
                }//end stmt_1 try
                catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            }//end while
            rst.close();
            stmt.close();

        }//end try
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        return results;
    }
    
    @Override
    // Query 6
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the IDs, first names, and last names of each of the two users in
    //            the top <num> pairs of users who are not friends but have a lot of
    //            common friends
    //        (B) For each pair identified in (A), find the IDs, first names, and last names
    //            of all the two users' common friends
    public FakebookArrayList<UsersPair> suggestFriends(int num) throws SQLException {
        FakebookArrayList<UsersPair> results = new FakebookArrayList<UsersPair>("\n");
        
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll, FakebookOracleConstants.ReadOnly)) {
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                UserInfo u1 = new UserInfo(16, "The", "Hacker");
                UserInfo u2 = new UserInfo(80, "Dr.", "Marbles");
                UserInfo u3 = new UserInfo(192, "Digit", "Le Boid");
                UsersPair up = new UsersPair(u1, u2);
                up.addSharedFriend(u3);
                results.add(up);
            */
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        return results;
    }
    
    @Override
    // Query 7
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the name of the state or states in which the most events are held
    //        (B) Find the number of events held in the states identified in (A)
    public EventStateInfo findEventStates() throws SQLException {
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll, FakebookOracleConstants.ReadOnly)) {
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                EventStateInfo info = new EventStateInfo(50);
                info.addState("Kentucky");
                info.addState("Hawaii");
                info.addState("New Hampshire");
                return info;
            */
            return new EventStateInfo(-1);                // placeholder for compilation
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
            return new EventStateInfo(-1);
        }
    }
    
    @Override
    // Query 8
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find the ID, first name, and last name of the oldest friend of the user
    //            with User ID <userID>
    //        (B) Find the ID, first name, and last name of the youngest friend of the user
    //            with User ID <userID>
    public AgeInfo findAgeInfo(long userID) throws SQLException {
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll, FakebookOracleConstants.ReadOnly)) {
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                UserInfo old = new UserInfo(12000000, "Galileo", "Galilei");
                UserInfo young = new UserInfo(80000000, "Neil", "deGrasse Tyson");
                return new AgeInfo(old, young);
            */
            return new AgeInfo(new UserInfo(-1, "UNWRITTEN", "UNWRITTEN"), new UserInfo(-1, "UNWRITTEN", "UNWRITTEN"));                // placeholder for compilation
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
            return new AgeInfo(new UserInfo(-1, "ERROR", "ERROR"), new UserInfo(-1, "ERROR", "ERROR"));
        }
    }
    
    @Override
    // Query 9
    // -----------------------------------------------------------------------------------
    // GOALS: (A) Find all pairs of users that meet each of the following criteria
    //              (i) same last name
    //              (ii) same hometown
    //              (iii) are friends
    //              (iv) less than 10 birth years apart
    public FakebookArrayList<SiblingInfo> findPotentialSiblings() throws SQLException {
        FakebookArrayList<SiblingInfo> results = new FakebookArrayList<SiblingInfo>("\n");
        
        try (Statement stmt = oracle.createStatement(FakebookOracleConstants.AllScroll, FakebookOracleConstants.ReadOnly)) {
            /*
                EXAMPLE DATA STRUCTURE USAGE
                ============================================
                UserInfo u1 = new UserInfo(81023, "Kim", "Kardashian");
                UserInfo u2 = new UserInfo(17231, "Kourtney", "Kardashian");
                SiblingInfo si = new SiblingInfo(u1, u2);
                results.add(si);
            */
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        return results;
    }
    
    // Member Variables
    private Connection oracle;
    private final String UsersTable = FakebookOracleConstants.UsersTable;
    private final String CitiesTable = FakebookOracleConstants.CitiesTable;
    private final String FriendsTable = FakebookOracleConstants.FriendsTable;
    private final String CurrentCitiesTable = FakebookOracleConstants.CurrentCitiesTable;
    private final String HometownCitiesTable = FakebookOracleConstants.HometownCitiesTable;
    private final String ProgramsTable = FakebookOracleConstants.ProgramsTable;
    private final String EducationTable = FakebookOracleConstants.EducationTable;
    private final String EventsTable = FakebookOracleConstants.EventsTable;
    private final String AlbumsTable = FakebookOracleConstants.AlbumsTable;
    private final String PhotosTable = FakebookOracleConstants.PhotosTable;
    private final String TagsTable = FakebookOracleConstants.TagsTable;
}
