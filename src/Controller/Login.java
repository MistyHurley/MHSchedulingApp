package Controller;

import Model.Appointment;
import Model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import utils.Session;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.ResourceBundle;

/** Login screen controller */
public class Login implements Initializable {
    Scene scene;
    Stage stage;
    Parent root;
    ResourceBundle resourceBundle;

    @FXML private Label loginLbl, titleLbl, timeZoneLbl;

    @FXML private TextField usernameField, passwordField;

    @FXML private Button loginBtn, exitBtn;

    //@FXML private ChoiceBox languageChoiceBox; //language selection tool to implement later

    /** Handles localization settings based on user's system display language.
     *  Currently supports English and French.
     */
    private void setLocale() {
        resourceBundle = ResourceBundle.getBundle("Locale", Locale.getDefault());
        titleLbl.setText(resourceBundle.getString("titleLbl"));
        loginLbl.setText(resourceBundle.getString("loginLbl"));
        usernameField.setPromptText(resourceBundle.getString("usernameField"));
        usernameField.setText(resourceBundle.getString("usernameField"));
        passwordField.setPromptText(resourceBundle.getString("passwordField"));
        loginBtn.setText(resourceBundle.getString("loginBtn"));
        exitBtn.setText(resourceBundle.getString("exitBtn"));

        timeZoneLbl.setText(String.format("%s %s",
                LocalDateTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)),
                ZoneId.systemDefault().getDisplayName(TextStyle.FULL, Locale.getDefault())));

        //languageChoiceBox.setValue(Locale.getDefault().getDisplayLanguage());

    }

    /** Handles user login.
     * If username and password are correct, switches to the home screen
     * Displays an alert message if username and/or password are incorrect
     * @param actionEvent login button clicked or enter button pressed
     */
    @FXML private void onActionLogin(ActionEvent actionEvent)
    {
        String username = usernameField.getText();
        String password = passwordField.getText();

        User user = User.login(username, password);
        if (user != null) {
            Session.logActivity(String.format("User ID %d Username %s logged in", user.getUserId(), user.getUsername()));
            Session.setUser(user);
            try {
                root = FXMLLoader.load(getClass().getResource("/View/Home.fxml"));
                stage = (Stage) loginBtn.getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
                Appointment upcomingAppointment = Appointment.getUpcomingAppointmentByUserId(Session.getUser().getUserId());
                if (upcomingAppointment == null) {
                    new Alert(Alert.AlertType.INFORMATION, "You have no upcoming appointments in the next 15 minutes.").show();
                }
                else {
                    Alert upcomingWarning = new Alert(Alert.AlertType.WARNING, String.format("Appointment ID %d starts at %s.", upcomingAppointment.getAppointmentId(), upcomingAppointment.getFormattedStartTime()));
                    upcomingWarning.setHeaderText("Warning: Appointment within 15 minutes!");
                    upcomingWarning.show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            // login failed
            Session.logActivity(String.format("Attempted login as Username %s failed", username.trim()));
            Alert incorrectPasswordMessage = new Alert(Alert.AlertType.ERROR, resourceBundle.getString("incorrectLogin"));
            incorrectPasswordMessage.show();
        }
    }

   //deprecated language selection tool
    /*@FXML
    private void onActionSetLocale(ActionEvent actionEvent) {
        String s = languageChoiceBox.getValue().toString();
        if s.equals("English") {
            ResourceBundle resourceBundle = ResourceBundle.getBundle("Locale", "Locale_en");
        }
    }
*/

    /** Exits the application upon button click */
    @FXML void onActionExit(ActionEvent actionEvent) {
    System.exit(0);
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //languageChoiceBox.getItems().add("English");
        //languageChoiceBox.getItems().add("French");
        setLocale();
        loginBtn.setDefaultButton(true); // press enter to activate the login button
        //loginBtn.setOnAction(actionEvent -> onActionLogin(actionEvent));

        Session.setUser(null);
    }






/*
    public void showMe(ActionEvent actionEvent) {
        ObservableList<Countries> countryList = DBCountries.getAllCountries();
        for(Countries c : countryList) {
            System.out.println("Country ID: " + c.getId()) + " Name : " + c.getName();
        }
    }
*/
}
