package Controller;

import Model.Appointment;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


/** Appointments screen controller.
 * The Appointments screen handles a TableView that displays a User's appointments by week, month, or all.
 */
public class Appointments implements Initializable {

    @FXML
    private Button homeBtn, customersBtn, appointmentsBtn, reportsBtn, logoutBtn;

    @FXML
    private Tab weekTab, monthTab, allTab;

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
    private TableColumn<Appointment, String> locationCol;

    @FXML
    private TableColumn<Appointment, String> contactCol;

    @FXML
    private TableColumn<Appointment, String> typeCol;

    @FXML
    private TableColumn<Appointment, String> customerCol;

    @FXML
    private Button apptAddBtn, apptModifyBtn, apptDeleteBtn;

    private Appointment.AppointmentRange currentAppointmentRange = Appointment.AppointmentRange.WEEK;

    // suppress tab events on load and handle as part of initialization
    protected boolean initialized = false;

    /** This method populates the user's Appointments TableView. */
    private void populateApptTableView() {
        apptTableView.setItems(Appointment.getAppointments(currentAppointmentRange));
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

        currentAppointmentRange = Appointment.AppointmentRange.WEEK;
        populateApptTableView();

        initializeSidebar();

        initialized = true;
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

    /** Displays weekly calendar TableView for User's appointments */
    @FXML void onActionWeekTab() {
        if (!initialized) return;
        currentAppointmentRange = Appointment.AppointmentRange.WEEK;
        populateApptTableView();
    }

    /** Displays monthly TableView for User's appointments */
    @FXML void onActionMonthTab() {
        if (!initialized) return;
        currentAppointmentRange = Appointment.AppointmentRange.MONTH;
        populateApptTableView();
    }

    /** Displays a TableView for all of User's appointments */
    @FXML void onActionAllTab() {
        if (!initialized) return;
        currentAppointmentRange = Appointment.AppointmentRange.ALL;
        populateApptTableView();
    }


    /** Deletes the selected appointment with user confirmation.
     * Lambda expression is used to elegantly handle user response to alert dialogue.
     * Use of ifPresent() with a passed anonymous function for handling clearly defines the operation as thread-blocking.
     */
    @FXML void onActionDeleteAppointment(ActionEvent event) {
        Appointment appointment = apptTableView.getSelectionModel().getSelectedItem();
        if (appointment == null) {
            Alert invalidSelectionAlert = new Alert(Alert.AlertType.WARNING, "Please select an Appointment.");
            invalidSelectionAlert.show();
        }
        else {
            Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION, String.format("Are you sure you want to delete Appointment type %s with ID %d?", appointment.getType(), appointment.getAppointmentId()));
            ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
            ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
            deleteAlert.getButtonTypes().setAll(yesButton, noButton);
            // get result via lambda expression
            deleteAlert.showAndWait().ifPresent(pressedButton -> {
                if (pressedButton == yesButton) {
                    if (!appointment.delete()) {
                        new Alert(Alert.AlertType.ERROR, "Failed to delete Appointment. Please try again.").show();
                    }
                    else {
                        new Alert(Alert.AlertType.INFORMATION, String.format("Deleted Appointment type %s with ID %d!", appointment.getType(), appointment.getAppointmentId())).show();
                    }
                    populateApptTableView();
                }
            });
        }
    }

    /** Changes to the Add Appointment screen when user clicks the Add button */
    @FXML void onActionDisplayAddAppointment(ActionEvent event) {
        sceneChange(event, "/View/ManageAppointment.fxml");
    }


    /**  Changes to the Modify Appointment screen when user selects to modify an appointment in the TableView */
    @FXML void onActionDisplayModifyAppointment(ActionEvent event) {
        Appointment appointment = apptTableView.getSelectionModel().getSelectedItem();
        if (appointment == null) {
            Alert invalidSelectionAlert = new Alert(Alert.AlertType.WARNING, "Please select an Appointment.");
            invalidSelectionAlert.show();
        }
        else {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/View/ManageAppointment.fxml"));
                fxmlLoader.load();

                ManageAppointment manageAppointment = fxmlLoader.getController();
                manageAppointment.loadAppointment(appointment.getAppointmentId());

                Parent scene = fxmlLoader.getRoot();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(scene));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
