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

public class ReportScheduleForContact implements Report {
    protected ComboBox<Contact> reportComboBox;

    @Override
    public void initialize(Label reportLabel, ComboBox reportComboBox, TableView<Map> reportTableView) {
        reportLabel.setText("Schedule For Contact");
        reportComboBox.setPromptText("Select a Contact");
        reportComboBox.setItems(Contact.getContacts());
        this.reportComboBox = reportComboBox;

        TableColumn<Map, String> appointmentIdCol = new TableColumn<Map, String>("Appointment ID");
        appointmentIdCol.setCellValueFactory(new MapValueFactory<String>("appointmentId"));
        TableColumn<Map, String> titleCol = new TableColumn<Map, String>("Title");
        titleCol.setCellValueFactory(new MapValueFactory<String>("title"));
        TableColumn<Map, String> typeCol = new TableColumn<Map, String>("Type");
        typeCol.setCellValueFactory(new MapValueFactory<String>("type"));
        TableColumn<Map, String> descriptionCol = new TableColumn<Map, String>("Description");
        descriptionCol.setCellValueFactory(new MapValueFactory<String>("description"));
        TableColumn<Map, String> startCol = new TableColumn<Map, String>("Start");
        startCol.setCellValueFactory(new MapValueFactory<String>("start"));
        TableColumn<Map, String> endCol = new TableColumn<Map, String>("End");
        endCol.setCellValueFactory(new MapValueFactory<String>("end"));
        TableColumn<Map, String> customerIdCol = new TableColumn<Map, String>("Customer ID");
        customerIdCol.setCellValueFactory(new MapValueFactory<String>("customerId"));

        reportTableView.getColumns().setAll(appointmentIdCol, titleCol, typeCol, descriptionCol, startCol, endCol, customerIdCol);
    }
    /** Returns Boolean value true to indicate that a ComboBox is used in this screen */
    @Override
    public boolean hasComboBox() {
        return true;
    }
    /** Queries the database for a list of appointments for a user-specified contact
     * @return list of items required for populating TableView, specifically as HashMap&lt;String,String&gt;
     */
    @Override
    public ObservableList<Map> getItems() {
        ObservableList<Map> rowList = FXCollections.observableArrayList();

        try {
            String sql = "SELECT Appointment_ID,Title,Type,Description,Start,End,Customer_ID FROM appointments WHERE Contact_ID=? ORDER BY Appointment_ID";

            PreparedStatement statement = DBConnection.getConnection().prepareStatement(sql);
            statement.setInt(1, reportComboBox.getValue().getContactId());

            ResultSet rs = statement.executeQuery();

            while(rs.next()){
                Map<String,String> row = new HashMap<>();
                row.put("appointmentId", String.valueOf(rs.getInt("Appointment_ID")));
                row.put("title", rs.getString("Title"));
                row.put("type", rs.getString("Type"));
                row.put("description", rs.getString("Description"));
                row.put("start", rs.getTimestamp("Start").toLocalDateTime().format(DateTimeFormatter.ofPattern(Appointment.DATETIME_FORMAT)));
                row.put("end", rs.getTimestamp("End").toLocalDateTime().format(DateTimeFormatter.ofPattern(Appointment.DATETIME_FORMAT)));
                row.put("customerId", String.valueOf(rs.getInt("Customer_ID")));
                rowList.add(row);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return rowList;
    }
}

