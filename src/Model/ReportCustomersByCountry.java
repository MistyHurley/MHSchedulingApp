package Model;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import utils.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
/** Initializes screen for RunReport screen - count the number of appointments by type and month. */
public class ReportCustomersByCountry implements Report {
    protected ComboBox<Country> reportComboBox;
    @Override
    public void initialize(Label reportLabel, ComboBox reportComboBox, TableView<Map> reportTableView) {
        reportLabel.setText("Customers by Country");
        reportComboBox.setPromptText("Select a Country");
        reportComboBox.setItems(Country.getCountries());
        this.reportComboBox = reportComboBox;

        TableColumn<Map, String> customerIdCol = new TableColumn<Map, String>("Customer ID");
        customerIdCol.setCellValueFactory(new MapValueFactory<String>("customerId"));
        TableColumn<Map, String> customerNameCol = new TableColumn<Map, String>("Name");
        customerNameCol.setCellValueFactory(new MapValueFactory<String>("customerName"));
        TableColumn<Map, String> addressCol = new TableColumn<Map, String>("Address");
        addressCol.setCellValueFactory(new MapValueFactory<String>("address"));
        TableColumn<Map, String> zipCol = new TableColumn<Map, String>("Zip");
        zipCol.setCellValueFactory(new MapValueFactory<String>("zip"));

        reportTableView.getColumns().setAll(customerIdCol, customerNameCol, addressCol, zipCol);
    }
    /** Returns Boolean value true to indicate that a ComboBox is used in this screen */
    @Override
    public boolean hasComboBox() {
        return true;
    }
    /** Queries the database for a list of customers by user-specified country.
     * @return list of items required for populating TableView, specifically as HashMap&lt;String,String&gt;
     */
    @Override
    public ObservableList<Map> getItems() {
        ObservableList<Map> rowList = FXCollections.observableArrayList();

        try {
            String sql = "SELECT cus.Customer_ID,cus.Customer_Name,cus.Address,cus.Postal_Code" +
                        " FROM customers AS cus" +
                        " INNER JOIN first_level_divisions AS divn ON divn.Division_ID=cus.Division_ID" +
                        " INNER JOIN countries AS ctry ON ctry.Country_ID=divn.COUNTRY_ID" +
                        " WHERE ctry.Country_ID=?" +
                        " ORDER BY cus.Customer_ID";

            PreparedStatement statement = DBConnection.getConnection().prepareStatement(sql);
            statement.setInt(1, reportComboBox.getValue().getCountryId());

            ResultSet rs = statement.executeQuery();

            while(rs.next()){
                Map<String,String> row = new HashMap<>();
                //reportTableView.getColumns().setAll(, customerNameCol, addressCol, zipCol);
                row.put("customerId", String.valueOf(rs.getInt("Customer_ID")));
                row.put("customerName", rs.getString("Customer_Name"));
                row.put("address", rs.getString("Address"));
                row.put("zip", rs.getString("Postal_Code"));
                rowList.add(row);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return rowList;
    }
}
