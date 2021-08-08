package Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utils.DBConnection;

import java.sql.*;

/** Data management for User table */
// NOTE: password intentionally omitted for security reasons
public class User {
    private int userId;
    private String username, createdBy, lastUpdatedBy;
    private Timestamp createDate, lastUpdate;

    public User() {}
/** Queries the database for the user record matching the given User ID */
    public User(int userId) {
        try {
            String sql = "SELECT * FROM users WHERE User_ID=?";

            PreparedStatement statement = DBConnection.getConnection().prepareStatement(sql);
            statement.setInt(1, userId);

            ResultSet rs = statement.executeQuery();
            rs.next();
            populateFromResultSet(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Login handler queries the database for matching username and password
     * @param username user-inputted
     * @param password user-inputted
     * @return User object
     */
    public static User login(String username, String password) {
        try {
            String sql = "SELECT * FROM users WHERE User_Name=? AND Password=?";

            PreparedStatement statement = DBConnection.getConnection().prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.populateFromResultSet(rs);

                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String toString() {
        return getUsername();
    }

    /** Populate with User information from ResultSet
     * @param rs
     * @throws SQLException
     */
    private void populateFromResultSet(ResultSet rs) throws SQLException {
        this.userId = rs.getInt("User_ID");
        this.username = rs.getString("User_Name");
    }

    /** Get list of all users */
    public static ObservableList<User> getUsers() {
        ObservableList<User> userList = FXCollections.observableArrayList();

        try {
            String sql = "SELECT * FROM users ORDER BY User_ID";

            Statement statement = DBConnection.getConnection().createStatement();

            ResultSet rs = statement.executeQuery(sql);

            while(rs.next()){
                User user = new User();
                user.populateFromResultSet(rs);
                userList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userList;
    }
    /** Get user ID */
    public int getUserId() {
        return userId;
    }
    /** Set user ID */
    public void setUserId(int userId) {
        this.userId = userId;
    }
    /** Get username */
    public String getUsername() {
        return username;
    }
    /** Set username */
    public void setUsername(String username) {
        this.username = username;
    }
    /** Get user record creator */
    public String getCreatedBy() {
        return createdBy;
    }
    /** Set user record creator */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    /** Get last username to update record */
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }
    /** Set last username to update record */
    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }
    /** Get timestamp of username record creation */
    public Timestamp getCreateDate() {
        return createDate;
    }
    /** Set timestamp of username record creation */
    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }
    /** Get timestamp of last update to user record */
    public Timestamp getLastUpdate() {
        return lastUpdate;
    }
    /** Set timestamp of last update to user record */
    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
