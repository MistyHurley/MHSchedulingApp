package Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utils.DBConnection;

import java.sql.*;
/** Manages division table in database */
public class Division {
    private int divisionId, countryId;
    private String divisionName, createdBy, lastUpdatedBy;
    private Timestamp createDate, lastUpdate;

    public Division() {}

    /** Queries the database for records matching a specified division ID
     * @param divisionId
     */
    public Division(int divisionId) {
        try {
            String sql = "SELECT * FROM first_level_divisions WHERE Division_ID=?";

            PreparedStatement statement = DBConnection.getConnection().prepareStatement(sql);
            statement.setInt(1, divisionId);

            ResultSet rs = statement.executeQuery();
            rs.next();
            populateFromResultSet(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        return getDivisionName();
    }

    /** Populates with first-level division information from result set
     * @param rs
     * @throws SQLException
     */
    private void populateFromResultSet(ResultSet rs) throws SQLException {
        this.divisionId = rs.getInt("Division_ID");
        this.divisionName = rs.getString("Division");
        this.createDate = rs.getTimestamp("Create_Date");
        this.createdBy = rs.getString("Created_By");
        this.lastUpdate = rs.getTimestamp("Last_Update");
        this.lastUpdatedBy = rs.getString("Last_Updated_By");
        this.countryId = rs.getInt("COUNTRY_ID");
    }

    /** Get list of divisions located in a specified countryId */
    public static ObservableList<Division> getDivisionsForCountryId(int countryId) {
        ObservableList<Division> divisionList = FXCollections.observableArrayList();

        try {
            String sql = "SELECT * FROM first_level_divisions WHERE COUNTRY_ID=? ORDER BY Division_ID";

            PreparedStatement statement = DBConnection.getConnection().prepareStatement(sql);
            statement.setInt(1, countryId);

            ResultSet rs = statement.executeQuery();

            while(rs.next()){
                Division division = new Division();
                division.populateFromResultSet(rs);
                divisionList.add(division);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return divisionList;
    }
    /** Get Division ID */
    public int getDivisionId() {
        return divisionId;
    }
    /** Get Division ID */
    public void setDivisionId(int divisionId) {
        this.divisionId = divisionId;
    }
    /** Get country ID */
    public int getCountryId() {
        return countryId;
    }
    /** Return country record information from country ID */
    public Country getCountry() {
        // TODO: cache a local copy?
        return new Country(countryId);
    }
    /** Set country ID */
    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }
    /** Get Division name */
    public String getDivisionName() {
        return divisionName;
    }
    /** Set Division name */
    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
    }
    /** Get creator of division record */
    public String getCreatedBy() {
        return createdBy;
    }
    /** Set creator of division record */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    /** Get username of last update to division record */
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }
    /** Set username of last update to division record */
    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }
    /** Get division record creation date */
    public Timestamp getCreateDate() {
        return createDate;
    }
    /** Set division record creation date */
    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }
    /** Get timestamp of last update to division record */
    public Timestamp getLastUpdate() {
        return lastUpdate;
    }
    /** Set timestamp of last update to division record */
    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
