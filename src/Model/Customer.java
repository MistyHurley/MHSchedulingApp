package Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utils.DBConnection;
import utils.Session;

import java.sql.*;
/** Manage customer table information */
public class Customer {

    private boolean newRecord;
    private int customerId, divisionId;
    private String customerName, address, zip, phone, createdBy, lastUpdatedBy;
    private Timestamp createDate, lastUpdate;

    public Customer() {
        this.newRecord = true;
    }

    public Customer(String customerName,
                    String address,
                    String zip,
                    String phone,
                    Timestamp createdDate,
                    String createdBy,
                    Timestamp lastUpdate,
                    String lastUpdatedBy,
                    int divisionId) {
        this.newRecord = true;
        this.customerName = customerName;
        this.address = address;
        this.zip = zip;
        this.phone = phone;
        this.createDate = createdDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.divisionId = divisionId;
    }

    /** Query database for records matching specified customer ID
     * @param customerId
     */
    public Customer(int customerId) {
        try {
            String sql = "SELECT * FROM customers WHERE Customer_ID=?";

            PreparedStatement statement = DBConnection.getConnection().prepareStatement(sql);
            statement.setInt(1, customerId);

            ResultSet rs = statement.executeQuery();
            rs.next();
            populateFromResultSet(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        return getCustomerName();
    }

    /** Runs SQL statement to add or modify a customer.
     * @return Returns customer ID of successfully saved record
     */
    public int save() {
        try {
            StringBuilder sqlStringBuilder = new StringBuilder();
            if (newRecord) {
                sqlStringBuilder.append("INSERT INTO customers SET");
            }
            else {
                sqlStringBuilder.append("UPDATE customers SET");
            }
            sqlStringBuilder.append(" Customer_Name=?");
            sqlStringBuilder.append(",Address=?");
            sqlStringBuilder.append(",Postal_Code=?");
            sqlStringBuilder.append(",Phone=?");
            if (newRecord) {
                sqlStringBuilder.append(",Created_By=?");
            }
            sqlStringBuilder.append(",Last_Update=CURRENT_TIMESTAMP");
            sqlStringBuilder.append(",Last_Updated_By=?");
            sqlStringBuilder.append(",Division_ID=?");
            if (!newRecord) {
                sqlStringBuilder.append(" WHERE Customer_ID=?");
            }

            PreparedStatement statement = DBConnection.getConnection().prepareStatement(sqlStringBuilder.toString(), Statement.RETURN_GENERATED_KEYS);
            int parameterIndex = 1;
            statement.setString(parameterIndex++, customerName);
            statement.setString(parameterIndex++, address);
            statement.setString(parameterIndex++, zip);
            statement.setString(parameterIndex++, phone);
            if (newRecord) {
                statement.setString(parameterIndex++, Session.getUser().getUsername());
            }
            statement.setString(parameterIndex++, Session.getUser().getUsername());
            statement.setInt(parameterIndex++, divisionId);
            if (!newRecord) {
                statement.setInt(parameterIndex++, customerId);
            }

            statement.executeUpdate();
            if (newRecord) {
                ResultSet rs = statement.getGeneratedKeys();
                rs.next();
                customerId = rs.getInt(1);
            }
            return customerId;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    /** Runs SQL statement to delete a customer record
     * @return Boolean value true if record is successfully deleted
     */
    public boolean delete() {
        try {
            String sql = "DELETE FROM customers WHERE Customer_ID=?";
            PreparedStatement statement = DBConnection.getConnection().prepareStatement(sql);
            statement.setInt(1, customerId);
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

    /** Populate with customer information from result set
     * @param rs
     * @throws SQLException
     */
    private void populateFromResultSet(ResultSet rs) throws SQLException {
        this.newRecord = false;
        this.customerId = rs.getInt("Customer_ID");
        this.customerName = rs.getString("Customer_Name");
        this.address = rs.getString("Address");
        this.zip = rs.getString("Postal_Code");
        this.phone = rs.getString("Phone");
        this.createDate = rs.getTimestamp("Create_Date");
        this.createdBy = rs.getString("Created_By");
        this.lastUpdate = rs.getTimestamp("Last_Update");
        this.lastUpdatedBy = rs.getString("Last_Updated_By");
        this.divisionId = rs.getInt("Division_ID");
    }
    /** Get list of all customers */
    public static ObservableList<Customer> getCustomers() {
        ObservableList<Customer> customerList = FXCollections.observableArrayList();

        try {
            String sql = "SELECT * FROM customers ORDER BY Customer_ID";

            Statement statement = DBConnection.getConnection().createStatement();

            ResultSet rs = statement.executeQuery(sql);

            while(rs.next()){
                Customer customer = new Customer();
                customer.populateFromResultSet(rs);
                customerList.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customerList;
    }
    /** Get customer ID */
    public int getCustomerId() {
        return customerId;
    }
    /** Set customer ID */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    /** Get division ID */
    public int getDivisionId() {
        return divisionId;
    }

    /** Get a division record
     * @return Division object
     */
    public Division getDivision() {
        // TODO: cache a local copy?
        return new Division(divisionId);
    }
    /** Get division name */
    public String getDivisionName() {
        return getDivision().getDivisionName();
    }
    /** Get country name */
    public String getCountryName() {
        return getDivision().getCountry().getCountryName();
    }
    /** Set division ID */
    public void setDivisionId(int divisionId) {
        this.divisionId = divisionId;
    }
    /** Get customer name */
    public String getCustomerName() {
        return customerName;
    }
    /** Set customer name */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    /** Get customer address */
    public String getAddress() {
        return address;
    }

    public String getFormattedAddress() {
        // TODO
        return getAddress();
    }
    /** Set customer address */
    public void setAddress(String address) {
        this.address = address;
    }
    /** Get customer postal code */
    public String getZip() {
        return zip;
    }
    /** Set customer postal code */
    public void setZip(String zip) {
        this.zip = zip;
    }
    /** Get customer phone number */
    public String getPhone() {
        return phone;
    }
    /** Set customer phone number */
    public void setPhone(String phone) {
        this.phone = phone;
    }
    /** Get customer record creator username */
    public String getCreatedBy() {
        return createdBy;
    }
    /** Get customer record creator username */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    /** Get username of last to modify record */
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }
    /** Set username of last to modify record */
    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }
    /** Get record creation date */
    public Timestamp getCreateDate() {
        return createDate;
    }
    /** Set record creation date */
    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }
    /** Get date of last update on record */
    public Timestamp getLastUpdate() {
        return lastUpdate;
    }
    /** Set date of last update on record */
    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
