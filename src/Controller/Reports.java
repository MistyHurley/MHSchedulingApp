package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/** Reports screen controller */
public class Reports implements Initializable {

    @FXML
    private Button homeBtn, customersBtn, appointmentsBtn, reportsBtn, logoutBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeSidebar();
    }

    /** Switch screens depending on button click
     * @param event
     * @param scenePath
     */
    public void sceneChange(ActionEvent event, String scenePath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(scenePath));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Assigns scene changes to sidebar buttons.
     * Uses lambda expression to map buttons to scene changes.
     * Lambda expressions are used to significantly reduce code footprint.
     */
    protected void initializeSidebar() {
        homeBtn.setOnAction(event -> sceneChange(event, "/View/Home.fxml"));
        customersBtn.setOnAction(event -> sceneChange(event, "/View/Customers.fxml"));
        appointmentsBtn.setOnAction(event -> sceneChange(event, "/View/Appointments.fxml"));
        reportsBtn.setOnAction(event -> sceneChange(event, "/View/Reports.fxml"));
        logoutBtn.setOnAction(event -> sceneChange(event, "/View/Login.fxml"));
    }

    /** Changes screen and runs report upon user selection
     *
     * @param event report button clicked
     * @param reportClass initializes RunReport screen with appropriate report
     */
    private void sceneChangeToReport(ActionEvent event, String reportClass) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/View/RunReport.fxml"));
            fxmlLoader.load();

            RunReport runReport = fxmlLoader.getController();
            runReport.loadReport(reportClass);

            Parent scene = fxmlLoader.getRoot();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(scene));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Switches to RunReport screen to count the number of appointments by type and month */
    @FXML void onActionAppointmentCountByTypeAndMonth(ActionEvent event) {
        sceneChangeToReport(event, "ReportAppointmentCountByTypeAndMonth");
    }

    /** Switches to RunReport screen that will initiate a schedule report upon contact selection */
    @FXML void onActionScheduleForContact(ActionEvent event) {
        sceneChangeToReport(event, "ReportScheduleForContact");
    }

    /** Switches to RunReport screen that will initiate a report to list customers by country upon user selection  */
    @FXML void onActionCustomersByCountry(ActionEvent event) {
        sceneChangeToReport(event, "ReportCustomersByCountry");
    }
}
