package Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utils.DBConnection;
import utils.Session;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/** Manages the Appointment class */
public class Appointment {
    private boolean newRecord;
    private int appointmentId, customerId, userId, contactId;
    private String title, description, location, type, createdBy, lastUpdatedBy;
    private Timestamp startTime, endTime, createDate, lastUpdate;

    public static final String DATE_FORMAT = "M/d/yyyy";
    public static final String TIME_FORMAT = "h:mm a";
    public static final String DATETIME_FORMAT = "M/d/yyyy h:mm a";

    public enum AppointmentRange {
        DAY,
        WEEK,
        MONTH,
        ALL
    }

    /** Appointment object constructor for new record */
    public Appointment() {
        this.newRecord = true;
    }

    public Appointment(String title,
                       String description,
                       String location,
                       String type,
                       Timestamp startTime,
                       Timestamp endTime,
                       Timestamp createdDate,
                       String createdBy,
                       Timestamp lastUpdate,
                       String lastUpdatedBy,
                       int customerId,
                       int userId,
                       int contactId) {
        this.newRecord = true;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createDate = createdDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.customerId = customerId;
        this.userId = userId;
        this.contactId = contactId;
    }

    /** Load existing appointment by ID
     * @param appointmentId
     */
    public Appointment(int appointmentId) {
        try {
            String sql = "SELECT * FROM appointments WHERE Appointment_ID=?";

            PreparedStatement statement = DBConnection.getConnection().prepareStatement(sql);
            statement.setInt(1, appointmentId);

            ResultSet rs = statement.executeQuery();
            rs.next();
            populateFromResultSet(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Runs SQL statement too add or modify an appointment
     * @return Appointment ID for successfully saved appointment
     */
    public int save() {
        try {
            StringBuilder sqlStringBuilder = new StringBuilder();
            if (newRecord) {
                sqlStringBuilder.append("INSERT INTO appointments SET");
            }
            else {
                sqlStringBuilder.append("UPDATE appointments SET");
            }
            sqlStringBuilder.append(" Title=?");
            sqlStringBuilder.append(",Description=?");
            sqlStringBuilder.append(",Location=?");
            sqlStringBuilder.append(",Type=?");
            sqlStringBuilder.append(",Start=?");
            sqlStringBuilder.append(",End=?");
            if (newRecord) {
                sqlStringBuilder.append(",Created_By=?");
            }
            sqlStringBuilder.append(",Last_Update="+getCurrentTimestamp());
            sqlStringBuilder.append(",Last_Updated_By=?");
            sqlStringBuilder.append(",Customer_ID=?");
            sqlStringBuilder.append(",User_ID=?");
            sqlStringBuilder.append(",Contact_ID=?");
            if (!newRecord) {
                sqlStringBuilder.append(" WHERE Appointment_ID=?");
            }

            PreparedStatement statement = DBConnection.getConnection().prepareStatement(sqlStringBuilder.toString(), Statement.RETURN_GENERATED_KEYS);
            int parameterIndex = 1;
            statement.setString(parameterIndex++, title);
            statement.setString(parameterIndex++, description);
            statement.setString(parameterIndex++, location);
            statement.setString(parameterIndex++, type);
            statement.setTimestamp(parameterIndex++, startTime);
            statement.setTimestamp(parameterIndex++, endTime);
            if (newRecord) {
                statement.setString(parameterIndex++, Session.getUser().getUsername());
            }
            statement.setString(parameterIndex++, Session.getUser().getUsername());
            statement.setInt(parameterIndex++, customerId);
            statement.setInt(parameterIndex++, userId);
            statement.setInt(parameterIndex++, contactId);
            if (!newRecord) {
                statement.setInt(parameterIndex++, appointmentId);
            }

            statement.executeUpdate();
            if (newRecord) {
                ResultSet rs = statement.getGeneratedKeys();
                rs.next();
                appointmentId = rs.getInt(1);
            }
            return appointmentId;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    /** Runs SQL statement to delete an appointment
     * @return Boolean value is true if the appointment is successfully deleted
     */
    public boolean delete() {
        try {
            String sql = "DELETE FROM appointments WHERE Appointment_ID=?";
            PreparedStatement statement = DBConnection.getConnection().prepareStatement(sql);
            statement.setInt(1, appointmentId);
            statement.executeUpdate();
            return true;
        }
        catch (SQLIntegrityConstraintViolationException e) {
            return false;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Grab appointment data from database
     * @param rs
     * @throws SQLException
     */
    private void populateFromResultSet(ResultSet rs) throws SQLException {
        this.newRecord = false;
        this.appointmentId = rs.getInt("Appointment_ID");
        this.title = rs.getString("Title");
        this.description = rs.getString("Description");
        this.location = rs.getString("Location");
        this.type = rs.getString("Type");
        this.startTime = rs.getTimestamp("Start");
        this.endTime = rs.getTimestamp("End");
        this.createDate = rs.getTimestamp("Create_Date");
        this.createdBy = rs.getString("Created_By");
        this.lastUpdate = rs.getTimestamp("Last_Update");
        this.lastUpdatedBy = rs.getString("Last_Updated_By");
        this.customerId = rs.getInt("Customer_ID");
        this.userId = rs.getInt("User_ID");
        this.contactId = rs.getInt("Contact_ID");
    }


    /** Return a user's appointment that starts within 15 minutes of login time
     * @param userId
     * @return Appointment ID of a user's upcoming appointment, if exists
     */
    public static Appointment getUpcomingAppointmentByUserId(int userId) {
        try {
            String currentTimeStamp = getCurrentTimestamp();
            String sql = "SELECT * FROM appointments WHERE User_ID=? AND Start BETWEEN "+currentTimeStamp+" AND DATE_ADD("+currentTimeStamp+", INTERVAL 15 MINUTE) ORDER BY Start LIMIT 1";

            PreparedStatement statement = DBConnection.getConnection().prepareStatement(sql);
            statement.setInt(1, userId);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.populateFromResultSet(rs);

                return appointment;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static ObservableList<Appointment> getAppointments() {
        return getAppointments(AppointmentRange.ALL);
    }

    /** Grabs a list of appointments within a specified range
     * @param appointmentRange DAY, WEEK, MONTH, ALL
     * @return An ObservableList of appointments within a specified range
     */
    public static ObservableList<Appointment> getAppointments(AppointmentRange appointmentRange) {
        ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
        String currentTimeStamp = getCurrentTimestamp();

        try {
            StringBuilder sqlStringBuilder = new StringBuilder();
            sqlStringBuilder.append("SELECT * FROM appointments");
            switch(appointmentRange) {
                case DAY:
                    sqlStringBuilder.append(" WHERE Start BETWEEN "+currentTimeStamp+" AND DATE_ADD("+currentTimeStamp+", INTERVAL 1 DAY)");
                    break;
                case WEEK:
                    sqlStringBuilder.append(" WHERE Start BETWEEN "+currentTimeStamp+" AND DATE_ADD("+currentTimeStamp+", INTERVAL 1 WEEK)");
                    break;
                case MONTH:
                    sqlStringBuilder.append(" WHERE Start BETWEEN "+currentTimeStamp+" AND DATE_ADD("+currentTimeStamp+", INTERVAL 1 MONTH)");
                    break;
                case ALL:
                default:
                    break;
            }
            sqlStringBuilder.append(" ORDER BY Start");

            Statement statement = DBConnection.getConnection().createStatement();

            ResultSet rs = statement.executeQuery(sqlStringBuilder.toString());

            while(rs.next()){
                Appointment appointment = new Appointment();
                appointment.populateFromResultSet(rs);
                appointmentList.add(appointment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appointmentList;
    }

    /** Determines whether existing appointments overlap with the current object's start and end timestamps */
    public boolean overlapsExistingAppointments() {
        try {
            // note that it's okay for an appointment to
            // end exactly as another starts or start exactly as another ends
            String sql = "SELECT * FROM appointments WHERE (" +
                "(Start>=? AND End<=?) OR" + // can't enclose existing (or be identical)
                "(Start<=? AND End>?) OR" + // can't start within existing
                "(Start<? AND End>=?)" + // can't end within existing
                ") AND Customer_ID=?";
            if (!newRecord) {
                sql += " AND Appointment_ID!=?";
            }

            PreparedStatement statement = DBConnection.getConnection().prepareStatement(sql);
            statement.setTimestamp(1, getStartTime());
            statement.setTimestamp(2, getEndTime());
            statement.setTimestamp(3, getStartTime());
            statement.setTimestamp(4, getStartTime());
            statement.setTimestamp(5, getEndTime());
            statement.setTimestamp(6, getEndTime());
            statement.setInt(7, getCustomerId());
            if (!newRecord) {
                statement.setInt(8, getAppointmentId());
            }

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private static String getCurrentTimestamp() {
        // for testing (time honors system clock even if it's wrong)
        return "'"+ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))+"'";
        // for production (time is honored based on database clock)
        // return "CURRENT_TIMESTAMP";
    }

    /** Returns an Appointment ID */
    public int getAppointmentId() {
        return appointmentId;
    }
    /** Sets an Appointment ID */
    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }
    /** Get a customer ID */
    public int getCustomerId() {
        return customerId;
    }
    /** Return a customer object */
    public Customer getCustomer() {
        return new Customer(customerId);
    }
    /** Get a customer name */
    public String getCustomerName() {
        return getCustomer().getCustomerName();
    }
    /** Set a customer name */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    /** Get a User ID */
    public int getUserId() {
        return userId;
    }
    /** Returns a User object */
    public User getUser() {
        return new User(userId);
    }
    /** Get a username */
    public String getUsername() {
        return getUser().getUsername();
    }
    /** Set a user ID */
    public void setUserId(int userId) {
        this.userId = userId;
    }
    /** Get a contact ID */
    public int getContactId() {
        return contactId;
    }
    /** Return a Contact object */
    public Contact getContact() {
        return new Contact(contactId);
    }
    /** Get a contact name */
    public String getContactName() {
        return getContact().getContactName();
    }
    /** Set a contact ID */
    public void setContactId(int contactId) {
        this.contactId = contactId;
    }
    /** Get an appointment title */
    public String getTitle() {
        return title;
    }
    /** Set an appointment title */
    public void setTitle(String title) {
        this.title = title;
    }
    /** Get an appointment description */
    public String getDescription() {
        return description;
    }
    /** Set an appointment description */
    public void setDescription(String description) {
        this.description = description;
    }
    /** Get an appointment location */
    public String getLocation() {
        return location;
    }
    /** Set an appointment location */
    public void setLocation(String location) {
        this.location = location;
    }
    /** Get an appointment type */
    public String getType() {
        return type;
    }
    /** Set an appointment type */
    public void setType(String type) {
        this.type = type;
    }
    /** Get an appointment's creator */
    public String getCreatedBy() {
        return createdBy;
    }
    /** Set an appointment's creator */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    /** Get username for last update */
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }
    /** Set username for last update */
    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }
    /** Get appointment start time */
    public Timestamp getStartTime() {
        return startTime;
    }
    /** Get appointment start time converted to user's local date time */
    public String getFormattedStartTime() {
        return startTime.toLocalDateTime().format(DateTimeFormatter.ofPattern(DATETIME_FORMAT));
    }
    /** Set appointment start time */
    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }
    /** Set appointment start time converted to user's local time */
    public void setStartTime(String formattedStartTime) {
        this.startTime = Timestamp.from(LocalDateTime.parse(formattedStartTime, DateTimeFormatter.ofPattern(DATETIME_FORMAT)).atZone(ZoneId.systemDefault()).toInstant());
    }
    /** Get appointment end time */
    public Timestamp getEndTime() {
        return endTime;
    }
    /** Get appointment end time converted to user's local time */
    public String getFormattedEndTime() {
        return endTime.toLocalDateTime().format(DateTimeFormatter.ofPattern(DATETIME_FORMAT));
    }
    /** Set appointment end time */
    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }
    /** Set appointment end time converted from local time to database time */
    public void setEndTime(String formattedEndTime) {
        this.endTime = Timestamp.from(LocalDateTime.parse(formattedEndTime, DateTimeFormatter.ofPattern(DATETIME_FORMAT)).atZone(ZoneId.systemDefault()).toInstant());
    }
    /** Get appointment create date */
    public Timestamp getCreateDate() {
        return createDate;
    }
    /** Set appointment create date */
    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }
    /** Get time of last update */
    public Timestamp getLastUpdate() {
        return lastUpdate;
    }
    /** Set time of last update */
    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
