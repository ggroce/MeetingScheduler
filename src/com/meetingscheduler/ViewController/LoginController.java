package com.meetingscheduler.ViewController;

import com.meetingscheduler.DAO.Queries;
import com.meetingscheduler.Model.User;
import com.meetingscheduler.Utils.TimeFunctions;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField textfieldUsername;
    @FXML private PasswordField passwordfieldUserPassword;
    @FXML private Label labelUsername;
    @FXML private Label labelUserPassword;
    @FXML private Label labelheader;
    @FXML private Button buttonUserLogin;
    public static String currentUser;
    public static User user = new User();
    public static FileWriter logFile = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //Uncomment following line for Spanish language as default for login screens:
        //Locale.setDefault(new Locale("es", "MX"));
        ResourceBundle rb = ResourceBundle.getBundle("com/meetingscheduler/lang", Locale.getDefault());

        labelheader.setText(rb.getString("header"));
        labelUsername.setText(rb.getString("username"));
        labelUserPassword.setText(rb.getString("password"));
        buttonUserLogin.setText(rb.getString("login"));

        try {
            logFile = new FileWriter("logfile.txt", true);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error creating logfile.txt!");
            try {
                if(logFile != null) {
                    logFile.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    public void loginButtonPushed(ActionEvent event) throws IOException {
        ResourceBundle rb = ResourceBundle.getBundle("com/meetingscheduler/lang", Locale.getDefault());
        String submittedUsername = textfieldUsername.getText();
        String submittedPassword = passwordfieldUserPassword.getText();

        if (submittedUsername.equals("") || submittedPassword.equals("")) {
            showErrorDialog(rb.getString("noValue"));
        } else {
                user = Queries.validateUser(submittedUsername, submittedPassword);
                if (user == null) {
                    showErrorDialog(rb.getString("noUser"));
                    passwordfieldUserPassword.setText("");
                    logFile.append(TimeFunctions.timeUtc().toString() + " Invalid login attempt with username: " + submittedUsername + "\n");
                } else {
                    currentUser = user.getUserName();
                    logFile.append(TimeFunctions.timeUtc().toString() + " Successful login for username: " + currentUser + "\n");
                    openMainScreen(event);          //Validation passed and currentUser set, continue into program
                }
        }
    }

    private void showErrorDialog(String errorText) {
        ResourceBundle rb = ResourceBundle.getBundle("com/meetingscheduler/lang", Locale.getDefault());

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(rb.getString("error"));
        alert.setHeaderText(rb.getString("errorOnInput"));
        alert.setContentText(errorText);
        alert.showAndWait();
    }

    private void openMainScreen(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Scheduler Main Menu");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }
}


