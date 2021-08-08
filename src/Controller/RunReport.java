package Controller;

import Model.Report;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/** RunReport screen controller */
public class RunReport implements Initializable {
    @FXML
    private Button homeBtn, customersBtn, appointmentsBtn, reportsBtn, logoutBtn;

    @FXML
    private ComboBox<?> reportComboBox;

    @FXML
    private Label dynamicReportLbl;

    @FXML
    private TableView<Map> reportTableView;

    @FXML
    private Button doneBtn;

    Report report;

    /** Clicking the Done button will return to the Reports screen */
    @FXML void onActionDone(ActionEvent event) {
        sceneChange(event, "/View/Reports.fxml");
    }

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
/** Initializes RunReport screen based on user-selected report type */
    public void loadReport(String reportClass) {
        try {
            report = (Report) Class.forName("Model." + reportClass).getDeclaredConstructor().newInstance();
            report.initialize(dynamicReportLbl, reportComboBox, reportTableView);
            if (!report.hasComboBox()) {
                reportTableView.setItems(report.getItems());
            }
        }
        catch (ClassNotFoundException e) {}
        catch (InstantiationException e) {}
        catch (InvocationTargetException e) {}
        catch (IllegalAccessException e) {}
        catch (NoSuchMethodException e) {}
    }

    /** Populates the TableView when the user makes a ComboBox selection */
    @FXML void onActonReportComboBoxChanged(ActionEvent event) {
        reportTableView.setItems(report.getItems());
    }
}
