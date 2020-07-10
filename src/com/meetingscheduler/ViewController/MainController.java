package com.meetingscheduler.ViewController;


import com.meetingscheduler.DAO.Queries;
import com.meetingscheduler.Model.Appointment;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML private Button buttonCustomerRecords;
    @FXML private Button buttonAppointmentRecords;
    @FXML private Button buttonReports;

    @FXML
    public void openCustomerRecordsScreen(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CustomerRecordsScreen.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setTitle("Customer Records");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }

    @FXML
    public void openAppointmentRecordsScreen(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AppointmentRecordsScreen.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setTitle("Appointment Records");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }

    @FXML
    public void openReportsScreen(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ReportsScreen.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setTitle("Report Generation");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    //Check for upcoming appointments:
        ObservableList<Appointment> appointments = Queries.getAllAppointments();
        LocalDateTime currentTime = LocalDateTime.now(ZoneId.systemDefault());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("kk:mm");

        for(Appointment existingApp : appointments) {
            LocalDateTime startingTime = existingApp.getStart().toLocalDateTime();
            if(startingTime.isAfter(currentTime) && startingTime.isBefore(currentTime.plusMinutes(15))) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Appointment");
                alert.setHeaderText("Appointment about to start.");
                alert.setContentText("There is an appointment very soon with " + existingApp.getCustomerName() +
                        " at " + startingTime.format(dateTimeFormatter) + "!");
                alert.showAndWait();
            }
        }
    }
}
