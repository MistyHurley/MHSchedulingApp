package utils;

import Model.User;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/** Manages user login session */
public class Session {
    private static User user;

    /** Gets user information to grab username
     * @return user object
     */
    public static User getUser() {
        return user;
    }
/** Establish the logged in user */
    public static void setUser(User user) {
        Session.user = user;
    }

    /** Records all user log-in attempts and writes this information to a txt file.
     * Records usernames, timestamps, and whether each attempt was successful in a file named login_activity.txt
     * @param activity
     */
    public static void logActivity(String activity) {
        try {
            // per Part C, write timestamped log to login_activity.txt
            ZonedDateTime logTime = ZonedDateTime.now(ZoneId.of("UTC"));
            long timestamp = logTime.toEpochSecond();
            String formattedTimestamp = logTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z"));
            Path path = Paths.get("./login_activity.txt");
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
            Files.write(path, (formattedTimestamp + " (Unix timestamp " +timestamp + "): " + activity + "\n").getBytes(), StandardOpenOption.APPEND);
            // write to file: timestamp + ": " + activity;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
