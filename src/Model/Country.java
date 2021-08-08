package Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utils.DBConnection;

import java.sql.*;
/** Manage Country object */
public class Country {
    private int countryId;
    private String countryName, createdBy, lastUpdatedBy;
    private Timestamp createDate, lastUpdate;

    public Country() {}

    /** Queries the database for records matching specified country ID
     * @param countryId
     */
    public Country(int countryId) {
        try {
            String sql = "SELECT * FROM countries WHERE Country_ID=?";

            PreparedStatement statement = DBConnection.getConnection().prepareStatement(sql);
            statement.setInt(1, countryId);

            ResultSet rs = statement.executeQuery();
            rs.next();
            populateFromResultSet(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        return getCountryName();
    }

    /** Populate with country information from result set
     * @param rs
     * @throws SQLException
     */
    private void populateFromResultSet(ResultSet rs) throws SQLException {
        this.countryId = rs.getInt("Country_ID");
        this.countryName = rs.getString("Country");
        this.createDate = rs.getTimestamp("Create_Date");
        this.createdBy = rs.getString("Created_By");
        this.lastUpdate = rs.getTimestamp("Last_Update");
        this.lastUpdatedBy = rs.getString("Last_Updated_By");
    }

    /** Get list of all countries */
    public static ObservableList<Country> getCountries() {
        ObservableList<Country> countryList = FXCollections.observableArrayList();

        try {
            String sql = "SELECT * FROM countries ORDER BY Country_ID";

            Statement statement = DBConnection.getConnection().createStatement();

            ResultSet rs = statement.executeQuery(sql);

            while(rs.next()){
                Country country = new Country();
                country.populateFromResultSet(rs);
                countryList.add(country);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return countryList;
    }
/** Get country ID */
    public int getCountryId() {
        return countryId;
    }
    /** Set country ID */
    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }
    /** Get country name */
    public String getCountryName() {
        return countryName;
    }
    /** Set country name */
    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
    /** Get username of record creator */
    public String getCreatedBy() {
        return createdBy;
    }
    /** Set username of record creator */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    /** Get username of record updater */
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }
    /** Set username of record updater */
    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }
    /** Get date/time of creation */
    public Timestamp getCreateDate() {
        return createDate;
    }
    /** Set date/time of creation */
    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }
    /** Get date/time of last update */
    public Timestamp getLastUpdate() {
        return lastUpdate;
    }
    /** Set date/time of creation */
    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

}
