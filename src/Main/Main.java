package Main;

import utils.DBConnection;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.*;

/** Initializes the schedule application. */
public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/View/Login.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /** Launches Login as initial screen.
     * Initiates database connection.
     * @param args
     * @throws SQLException
     */
    public static void main(String[] args) throws SQLException {

        DBConnection.startConnection();
        launch(args);
        DBConnection.closeConnection();
    }
}
