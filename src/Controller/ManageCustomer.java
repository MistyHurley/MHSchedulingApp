package Controller;

import Model.Country;
import Model.Customer;
import Model.Division;
import javafx.event.ActionEvent;
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
import java.util.ResourceBundle;

/** Add/modify Customer screen controller */
public class ManageCustomer implements Initializable {

    @FXML
    private Button homeBtn;

    @FXML
    private Button customersBtn;

    @FXML
    private Button appointmentsBtn;

    @FXML
    private Button reportsBtn;

    @FXML
    private Button logoutBtn;

    @FXML
    private Label dynamicCustomerLbl;

    @FXML
    private TextField customerNameField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField zipField;

    @FXML
    private TextField phoneField;

    @FXML
    private RadioButton activeYesRadioBtn;

    @FXML
    private ToggleGroup ActiveTG;

    @FXML
    private RadioButton activeNoRadioBtn;

    @FXML
    private ComboBox<Country> countryComboBox;

    @FXML
    private ComboBox<Division> divisionComboBox;

    @FXML
    private TextField customerIdField;

    @FXML
    private Button saveBtn;

    @FXML
    private Button cancelBtn;

    private Customer customer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeSidebar();

        customer = new Customer();

        populateCountryComboBox();
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

    /** Populates text fields with current appointment info when modifying customers
     *
     * @param customerId the customer ID the user wishes to modify
     */
    // note reloading based on id instead of passing object to assert data is up to date
    public void loadCustomer(int customerId) {
        customer = new Customer(customerId);

        customerIdField.setText(String.valueOf(customer.getCustomerId()));
        customerNameField.setText(customer.getCustomerName());
        addressField.setText(customer.getAddress());
        zipField.setText(customer.getZip());
        countryComboBox.setValue(customer.getDivision().getCountry());
        populateDivisionComboBox();
        divisionComboBox.setValue(customer.getDivision());
        phoneField.setText(customer.getPhone());

        dynamicCustomerLbl.setText(String.format("Modify Customer ID %d", customerId));
    }

    /** Populates ComboBox with a list of countries */
    private void populateCountryComboBox() {
        countryComboBox.setItems(Country.getCountries());
    }

    /** Populates ComboBox with a list of first-level divisions */
    private void populateDivisionComboBox() {
        int countryId = countryComboBox.getValue().getCountryId();
        divisionComboBox.setItems(Division.getDivisionsForCountryId(countryId));
        divisionComboBox.setDisable(false);
    }

    /** Changing the country re-populates the ComboBox with appropriate first-level divisions */
    @FXML void onActionCountryChanged(ActionEvent event) {
        divisionComboBox.setValue(null);
        populateDivisionComboBox();
    }

    /** Go back to Customers screen upon cancel button clicked */
    @FXML void onActionCancel(ActionEvent event) {
        sceneChange(event, "/View/Customers.fxml");
    }

    /** Saves an appointment with validation checks.
     * Checks that fields/selections are not empty.
     * Provides alert message with the Customer ID saved or error message.
     * @param event Save button clicked
     */
    @FXML void onActionSave(ActionEvent event) {
        // TODO: maybe validate this stuff better
        if (customerNameField.getText().trim().length() == 0) {
            new Alert(Alert.AlertType.ERROR, "Please enter Customer Name.").show();
            customerNameField.requestFocus();
            return;
        }
        if (addressField.getText().trim().length() == 0) {
            new Alert(Alert.AlertType.ERROR, "Please enter Address.").show();
            addressField.requestFocus();
            return;
        }
        if (zipField.getText().trim().length() == 0) {
            new Alert(Alert.AlertType.ERROR, "Please enter Zip.").show();
            zipField.requestFocus();
            return;
        }
        if (countryComboBox.getSelectionModel().getSelectedItem() == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a Country.").show();
            countryComboBox.requestFocus();
            return;
        }
        if (divisionComboBox.getSelectionModel().getSelectedItem() == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a First-level Division.").show();
            divisionComboBox.requestFocus();
            return;
        }
        if (phoneField.getText().trim().length() == 0) {
            // TODO: validate phone format?
            new Alert(Alert.AlertType.ERROR, "Please enter Phone.").show();
            phoneField.requestFocus();
            return;
        }
        customer.setCustomerName(customerNameField.getText().trim());
        customer.setAddress(addressField.getText().trim());
        customer.setZip(zipField.getText().trim());
        customer.setDivisionId(divisionComboBox.getSelectionModel().getSelectedItem().getDivisionId());
        customer.setPhone(phoneField.getText().trim());

        int savedId = customer.save();
        if (savedId >= 0) {
            new Alert(Alert.AlertType.INFORMATION, String.format("Saved Customer ID %d!", savedId)).show();
            sceneChange(event, "/View/Customers.fxml");
        }
        else {
            new Alert(Alert.AlertType.ERROR, "Failed to save Customer.").show();
        }
    }
}