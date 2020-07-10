package com.meetingscheduler.ViewController;

import com.meetingscheduler.DAO.Queries;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class ReportsController implements Initializable {
    @FXML
    private Button buttonCancel;
    @FXML
    private Button buttonReport1;
    @FXML
    private Button buttonReport2;
    @FXML
    private Button buttonReport3;
    @FXML
    private TextArea textareaReportDisplay;

    @FXML
    public void cancelButtonPushed(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Scheduler Main Menu");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }

    @FXML
    public void report1ButtonPushed() {
        textareaReportDisplay.clear();
        String resultString = Queries.runReport(Queries.REPORT_APPT_TYPE_BY_MONTH);
        textareaReportDisplay.setText(resultString);
    }

    @FXML
    public void report2ButtonPushed() {
        textareaReportDisplay.clear();
        String resultString = Queries.runReport(Queries.REPORT_APPTS_FOREACH_CONSULTANT);
        textareaReportDisplay.setText(resultString);
    }

    @FXML
    public void report3ButtonPushed() {
        textareaReportDisplay.clear();
        String resultString = Queries.runReport(Queries.REPORT_NUM_OF_APPTS_PER_CUST);
        textareaReportDisplay.setText(resultString);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textareaReportDisplay.clear();
        textareaReportDisplay.setStyle("-fx-font-family: monospace");
    }
}
