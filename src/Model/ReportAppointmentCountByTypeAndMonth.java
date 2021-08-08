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
import java.util.HashMap;
import java.util.Map;

/** Initializes screen for RunReport screen - count the number of appointments by type and month
 */
public class ReportAppointmentCountByTypeAndMonth implements Report {
    @Override
    public void initialize(Label reportLabel, ComboBox reportComboBox, TableView<Map> reportTableView) {
        reportLabel.setText("Appointment Count by Type and Month");
        reportComboBox.setVisible(false);

        TableColumn<Map, String> typeCol = new TableColumn<Map, String>("Type");
        typeCol.setCellValueFactory(new MapValueFactory<String>("type"));
        TableColumn<Map, String> monthCol = new TableColumn<Map, String>("Month");
        monthCol.setCellValueFactory(new MapValueFactory<String>("month"));
        TableColumn<Map, String> countCol = new TableColumn<Map, String>("Count");
        countCol.setCellValueFactory(new MapValueFactory<String>("count"));

        reportTableView.getColumns().setAll(typeCol, monthCol, countCol);
    }
/** No ComboBox is required for this screen, so Boolean is false */
    @Override
    public boolean hasComboBox() {
        return false;
    }

    /** Queries the database for number of appointments by month
     * @return list of items required for populating TableView, specifically as HashMap&lt;String,String&gt;
     */
    @Override
    public ObservableList<Map> getItems() {
        ObservableList<Map> rowList = FXCollections.observableArrayList();

        try {
            String sql = "SELECT Type,MONTH(Start)MonthVal,MONTHNAME(Start)Month,COUNT(*) AS Count FROM appointments GROUP BY Type,MonthVal,Month ORDER BY MonthVal";

            PreparedStatement statement = DBConnection.getConnection().prepareStatement(sql);

            ResultSet rs = statement.executeQuery();

            while(rs.next()){
                Map<String,String> row = new HashMap<>();
                row.put("type", rs.getString("Type"));
                row.put("month", rs.getString("Month"));
                row.put("count", String.valueOf(rs.getInt("Count")));
                rowList.add(row);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return rowList;
    }
}
