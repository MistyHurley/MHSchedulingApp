package Controller;

import Model.Appointment;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.Node;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import utils.Session;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/** Home screen controller class */
public class Home implements Initializable {
    @FXML
    private Button homeBtn, customersBtn, appointmentsBtn, reportsBtn, logoutBtn;

    @FXML
    private Button apptAddBtn, apptModifyBtn, partDeleteBtn, customerAddBtn, customerModifyBtn, customerDeleteBtn;

    @FXML
    private Label dynamicWelcomeLbl;

    @FXML
    private TableView<Appointment> apptTableView;

    @FXML
    private TableColumn<Appointment, Integer> appointmentIdCol;

    @FXML
    private TableColumn<Appointment, String> startTimeCol;

    @FXML
    private TableColumn<Appointment, String> endTimeCol;

    @FXML
    private TableColumn<Appointment, String> titleCol;

    @FXML
    private TableColumn<Appointment, String> descriptionCol;

    @FXML
    private TableColumn<Appointment, String> typeCol;

    @FXML
    private TableColumn<Appointment, String> customerCol;

    @FXML
    private TableColumn<Appointment, String> contactCol;

    @FXML
    private TableColumn<Appointment, String> locationCol;

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
        catch (IOException e) {}
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

    /** Populates the tableview with the user's appointments occurring within 24 hours of login */
    private void populateApptTableView() {
        apptTableView.setItems(Appointment.getAppointments(Appointment.AppointmentRange.DAY));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        appointmentIdCol.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        startTimeCol.setCellValueFactory(new PropertyValueFactory<>("FormattedStartTime"));
        endTimeCol.setCellValueFactory(new PropertyValueFactory<>("FormattedEndTime"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        customerCol.setCellValueFactory(new PropertyValueFactory<>("CustomerName"));
        contactCol.setCellValueFactory(new PropertyValueFactory<>("ContactName"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        populateApptTableView();

        // grabs username
        dynamicWelcomeLbl.setText(String.format("Welcome %s!", Session.getUser().getUsername()));

        initializeSidebar();
    }
}
