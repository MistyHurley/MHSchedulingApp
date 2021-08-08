package Controller;

import Model.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

/** Add/modify appointment screen controller */
public class ManageAppointment implements Initializable {
    @FXML
    private Button homeBtn, customersBtn, appointmentsBtn, reportsBtn, logoutBtn;

    @FXML
    private Label dynamicAppointmentLbl;

    @FXML
    private TextField appointmentIdField;

    @FXML
    private TextField titleField;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField locationField;

    @FXML
    private TextField typeField;

    @FXML
    private DatePicker startDate;

    @FXML
    private TextField startTime;

    @FXML
    private DatePicker endDate;

    @FXML
    private TextField endTime;

    @FXML
    private ComboBox<Contact> contactComboBox;

    @FXML
    private ComboBox<Customer> customerComboBox;

    @FXML
    private ComboBox<User> userComboBox;

    @FXML
    private Button saveBtn;

    @FXML
    private Button cancelBtn;

    private Appointment appointment;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeSidebar();

        appointment = new Appointment();

        populateContactComboBox();
        populateCustomerComboBox();
        populateUserComboBox();
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

    /** Populates text fields with current appointment info when modifying appointments
     *
     * @param appointmentId the appointment ID the user wishes to modify
     */
    // note reloading based on id instead of passing object to assert data is up to date
    public void loadAppointment(int appointmentId) {
        appointment = new Appointment(appointmentId);

        String formattedStartTime = appointment.getFormattedStartTime();
        String startDateValue = formattedStartTime.substring(0, formattedStartTime.indexOf(" "));
        String startTimeValue = formattedStartTime.substring(formattedStartTime.indexOf(" ") + 1);
        String formattedEndTime = appointment.getFormattedEndTime();
        String endDateValue = formattedEndTime.substring(0, formattedEndTime.indexOf(" "));
        String endTimeValue = formattedEndTime.substring(formattedEndTime.indexOf(" ") + 1);

        appointmentIdField.setText(String.valueOf(appointment.getAppointmentId()));
        titleField.setText(appointment.getTitle());
        descriptionField.setText(appointment.getDescription());
        typeField.setText(appointment.getType());
        contactComboBox.setValue(appointment.getContact());
        customerComboBox.setValue(appointment.getCustomer());
        userComboBox.setValue(appointment.getUser());
        locationField.setText(appointment.getLocation());
        startDate.setValue(LocalDate.parse(startDateValue, DateTimeFormatter.ofPattern(Appointment.DATE_FORMAT)));
        startTime.setText(startTimeValue);
        endDate.setValue(LocalDate.parse(endDateValue, DateTimeFormatter.ofPattern(Appointment.DATE_FORMAT)));
        endTime.setText(endTimeValue);

        dynamicAppointmentLbl.setText(String.format("Modify Appointment ID %d", appointmentId));
    }

    /** Populates ComboBox with a list of contacts */
    private void populateContactComboBox() {
        contactComboBox.setItems(Contact.getContacts());
    }

    /** Populates the ComboBox with a list of customers */
    private void populateCustomerComboBox() {
        customerComboBox.setItems(Customer.getCustomers());
    }

    /** Populates ComboBox with a list of users */
    private void populateUserComboBox() {
        userComboBox.setItems(User.getUsers());
    }

    /** Switches to Appointments screen when cancel button is clicked */
    @FXML void onActionCancel(ActionEvent event) {
        sceneChange(event, "/View/Appointments.fxml");
    }

    /** Saves an appointment with validation checks.
     * Checks that fields/selections are not empty, user must enter correct time format.
     * Validates appointments are within business hours, 8AM-10PM EST per spec.
     * Provides alert message with the Appointment ID saved.
     * @param event Save button clicked
     */
    @FXML void onActionSave(ActionEvent event) {
        //LocalDateTime.parse("8 AM").atZone(ZoneId.of("UTC-5")).withZoneSameInstant(ZoneId.systemDefault()).getHour();
        String beginHourLocal = LocalTime.parse("8AM", DateTimeFormatter.ofPattern("ha")).atOffset(ZoneOffset.ofHours(-5)).withOffsetSameInstant(OffsetDateTime.now().getOffset()).format(DateTimeFormatter.ofPattern(Appointment.TIME_FORMAT));
        String endHourLocal = LocalTime.parse("10PM", DateTimeFormatter.ofPattern("ha")).atOffset(ZoneOffset.ofHours(-5)).withOffsetSameInstant(OffsetDateTime.now().getOffset()).format(DateTimeFormatter.ofPattern(Appointment.TIME_FORMAT));
        String localTimeZone = LocalDateTime.now().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("z"));
        String businessHoursLocal = beginHourLocal + " and " + endHourLocal + " " + localTimeZone;

        // TODO: Validate end time is after start time
        if (titleField.getText().trim().length() == 0) {
            new Alert(Alert.AlertType.ERROR, "Please enter Title.").show();
            titleField.requestFocus();
            return;
        }
        if (descriptionField.getText().trim().length() == 0) {
            new Alert(Alert.AlertType.ERROR, "Please enter Description.").show();
            descriptionField.requestFocus();
            return;
        }
        if (typeField.getText().trim().length() == 0) {
            new Alert(Alert.AlertType.ERROR, "Please enter Type.").show();
            typeField.requestFocus();
            return;
        }
        if (contactComboBox.getSelectionModel().getSelectedItem() == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a Contact.").show();
            contactComboBox.requestFocus();
            return;
        }
        if (customerComboBox.getSelectionModel().getSelectedItem() == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a Customer.").show();
            customerComboBox.requestFocus();
            return;
        }
        if (userComboBox.getSelectionModel().getSelectedItem() == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a User.").show();
            userComboBox.requestFocus();
            return;
        }
        if (locationField.getText().trim().length() == 0) {
            new Alert(Alert.AlertType.ERROR, "Please enter Location.").show();
            locationField.requestFocus();
            return;
        }
        if (startDate.getValue() == null) {
            new Alert(Alert.AlertType.ERROR, "Please enter Start Date.").show();
            startDate.requestFocus();
            return;
        }
        if (startTime.getText().trim().length() == 0) {
            new Alert(Alert.AlertType.ERROR, "Please enter Start Time.").show();
            startTime.requestFocus();
            return;
        }

        try {
            LocalTime.parse(startTime.getText().trim(), DateTimeFormatter.ofPattern(Appointment.TIME_FORMAT));
        }
        catch (DateTimeParseException e) {
            new Alert(Alert.AlertType.ERROR, "Start Time must be in format HH:MM AM/PM.").show();
            startTime.requestFocus();
            return;
        }
        String startDateValue = startDate.getValue().format(DateTimeFormatter.ofPattern(Appointment.DATE_FORMAT));
        appointment.setStartTime(startDateValue + " " + startTime.getText().trim());
        // start time as ZonedDateTime in EST
        ZonedDateTime startHoursCheck = appointment.getStartTime().toInstant().atZone(ZoneId.of("UTC-5"));
        if (startHoursCheck.getHour() < 8 || startHoursCheck.getHour() >= 22) {
            new Alert(Alert.AlertType.ERROR, String.format("Start time must be during the business hours of %s.", businessHoursLocal)).show();
            return;
        }

        if (endDate.getValue() == null) {
            new Alert(Alert.AlertType.ERROR, "Please enter End Date.").show();
            endDate.requestFocus();
            return;
        }
        if (endTime.getText().trim().length() == 0) {
            new Alert(Alert.AlertType.ERROR, "Please enter End Time.").show();
            endTime.requestFocus();
            return;
        }
        String endDateValue = endDate.getValue().format(DateTimeFormatter.ofPattern(Appointment.DATE_FORMAT));
        appointment.setEndTime(endDateValue + " " + endTime.getText().trim());
        // end time as ZonedDateTime in EST
        ZonedDateTime endHoursCheck = appointment.getEndTime().toInstant().atZone(ZoneId.of("UTC-5"));
        if (endHoursCheck.getHour() < 8 || endHoursCheck.getHour() > 22 || (endHoursCheck.getHour() == 22 && endHoursCheck.getMinute() != 0)) {
            new Alert(Alert.AlertType.ERROR, String.format("End time must be during the business hours of %s.", businessHoursLocal)).show();
            endTime.requestFocus();
            return;
        }

        try {
            LocalTime.parse(endTime.getText().trim(), DateTimeFormatter.ofPattern(Appointment.TIME_FORMAT));
        }
        catch (DateTimeParseException e) {
            new Alert(Alert.AlertType.ERROR, "End time must be in format HH:MM AM/PM.").show();
            endTime.requestFocus();
            return;
        }

        appointment.setCustomerId(customerComboBox.getValue().getCustomerId());
        if (appointment.overlapsExistingAppointments()) {
            new Alert(Alert.AlertType.ERROR, "The selected time range overlaps with an existing appointment for the selected Customer!").show();
            return;
        }

        appointment.setTitle(titleField.getText().trim());
        appointment.setDescription(descriptionField.getText().trim());
        appointment.setType(typeField.getText().trim());
        appointment.setContactId(contactComboBox.getValue().getContactId());
        appointment.setUserId(userComboBox.getValue().getUserId());
        appointment.setLocation(locationField.getText().trim());

        int savedId = appointment.save();
        if (savedId >= 0) {
            new Alert(Alert.AlertType.INFORMATION, String.format("Saved Appointment type %s with ID %d!", appointment.getType(), savedId)).show();
            sceneChange(event, "/View/Appointments.fxml");
        }
        else {
            new Alert(Alert.AlertType.ERROR, "Failed to save Appointment.").show();
        }
    }
}
