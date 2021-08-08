package Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utils.DBConnection;

import java.sql.*;
/** Manage the Contact object */
public class Contact {
    private int contactId;
    private String contactName, email;

    public Contact() {}

    /** Queries database for a contact records
     * @param contactId
     */
    public Contact(int contactId) {
        try {
            String sql = "SELECT * FROM contacts WHERE Contact_ID=?";

            PreparedStatement statement = DBConnection.getConnection().prepareStatement(sql);
            statement.setInt(1, contactId);

            ResultSet rs = statement.executeQuery();
            rs.next();
            populateFromResultSet(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        return getContactName();
    }

    /** Load contact information from result set
     * @param rs
     * @throws SQLException
     */
    private void populateFromResultSet(ResultSet rs) throws SQLException {
        this.contactId = rs.getInt("Contact_ID");
        this.contactName = rs.getString("Contact_Name");
        this.email = rs.getString("Email");
    }

    /** Get list of all contacts */
    public static ObservableList<Contact> getContacts() {
        ObservableList<Contact> contactList = FXCollections.observableArrayList();

        try {
            String sql = "SELECT * FROM contacts ORDER BY Contact_ID";

            Statement statement = DBConnection.getConnection().createStatement();

            ResultSet rs = statement.executeQuery(sql);

            while(rs.next()){
                Contact contact = new Contact();
                contact.populateFromResultSet(rs);
                contactList.add(contact);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contactList;
    }
    /** Get contact ID */
    public int getContactId() {
        return contactId;
    }
    /** Set contact ID */
    public void setContactId(int contactId) {
        this.contactId = contactId;
    }
    /** Get contact name */
    public String getContactName() {
        return contactName;
    }
    /** Set contact name */
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
    /** Get contact email */
    public String getEmail() {
        return email;
    }
    /** Set contact email */
    public void setEmail(String email) {
        this.email = email;
    }
}
