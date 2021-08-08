package Controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import Model.Customer;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/** Customer screen controller */
public class Customers implements Initializable {

    @FXML private Button homeBtn, customersBtn, appointmentsBtn, reportsBtn, logoutBtn;

    @FXML private TableView<Customer> customerTableView;
    @FXML private TableColumn<Customer, Integer> customerIdCol;
    @FXML private TableColumn<Customer, String> customerNameCol;
    @FXML private TableColumn<Customer, String> customerAddressCol;
    @FXML private TableColumn<Customer, String> customerZipCol;
    @FXML private TableColumn<Customer, String> customerDivisionCol;
    @FXML private TableColumn<Customer, String> customerCountryCol;
    @FXML private TableColumn<Customer, String> customerPhoneCol;

    @FXML private Button customerAddBtn, customerModifyBtn, customerDeleteBtn;

    /** Populates the TableView with the customers table information */
    private void populateCustomerTableView() {
        customerTableView.setItems(Customer.getCustomers());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerAddressCol.setCellValueFactory(new PropertyValueFactory<>("FormattedAddress"));
        customerZipCol.setCellValueFactory(new PropertyValueFactory<>("zip"));
        customerDivisionCol.setCellValueFactory(new PropertyValueFactory<>("DivisionName"));
        customerCountryCol.setCellValueFactory(new PropertyValueFactory<>("CountryName"));
        customerPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        populateCustomerTableView();

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

    /** Deletes a selected customer with user confirmation */
    @FXML void onActionDeleteCustomer(ActionEvent event) {
        Customer customer = customerTableView.getSelectionModel().getSelectedItem();
        if (customer == null) {
            Alert invalidSelectionAlert = new Alert(Alert.AlertType.WARNING, "Please select a Customer.");
            invalidSelectionAlert.show();
        }
        else {
            Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION, String.format("Are you sure you want to delete Customer ID %d?", customer.getCustomerId()));
            ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
            ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
            deleteAlert.getButtonTypes().setAll(yesButton, noButton);
            // get result via lambda expression
            deleteAlert.showAndWait().ifPresent(pressedButton -> {
                if (pressedButton == yesButton) {
                    if (!customer.delete()) {
                        new Alert(Alert.AlertType.ERROR, "Failed to delete Customer.\nPlease ensure all appointments for this customer are deleted first.").show();
                    }
                    else {
                        new Alert(Alert.AlertType.INFORMATION, String.format("Deleted Customer ID %d!", customer.getCustomerId())).show();
                    }
                    populateCustomerTableView();
                }
            });
        }
    }

    /** Changes to the Add Customer screen after button click */
    @FXML void onActionDisplayAddCustomer(ActionEvent event) {
        sceneChange(event, "/View/ManageCustomer.fxml");
    }

    /** Changes to the Modify Customer screen upon user selection */
    @FXML void onActionDisplayModifyCustomer(ActionEvent event) {
        Customer customer = customerTableView.getSelectionModel().getSelectedItem();
        if (customer == null) {
            Alert invalidSelectionAlert = new Alert(Alert.AlertType.WARNING, "Please select a Customer.");
            invalidSelectionAlert.show();
        }
        else {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/View/ManageCustomer.fxml"));
                fxmlLoader.load();

                ManageCustomer manageCustomer = fxmlLoader.getController();
                manageCustomer.loadCustomer(customer.getCustomerId());

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
