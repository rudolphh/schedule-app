package controller;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.DBConnection;

import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/login.fxml"));

        ResourceBundle resourceBundle = App.getResourceBundle();
        loader.setResources(resourceBundle);
        Parent root = loader.load();

        primaryStage.setTitle(resourceBundle.getString("title"));
        primaryStage.setResizable(false);// for login screen
        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.show();

    }

    public static void main(String[] args) {
        DBConnection.startConnection();
        launch(args);
        DBConnection.closeConnection();
    }


    // Helpers
    static Optional<ButtonType> dialog(Alert.AlertType alertType, String title, String header, String content){
        Alert alert = new Alert(alertType);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert.showAndWait();
    }

    static void closeThisWindow(ActionEvent actionEvent){
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.close();
    }

    static ResourceBundle getResourceBundle(){

        Locale loc = Locale.getDefault();// 1. Check for current locale

        // 2. Get appropriate Resource Bundle
        if(!loc.toLanguageTag().equals("en-US")){ // if locale is NOT english (default)

            loc = new Locale("es", "ES"); // explicitly set locale to spanish
        }

        return ResourceBundle.getBundle("lang.login", loc);
    }

}// end App
