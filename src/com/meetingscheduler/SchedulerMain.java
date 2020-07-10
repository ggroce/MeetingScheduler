package com.meetingscheduler;

import com.meetingscheduler.DAO.DBConnection;
import com.meetingscheduler.ViewController.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class SchedulerMain extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("ViewController/LoginScreen.fxml"));
        primaryStage.setTitle("Meeting Scheduler");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();
        DBConnection.createConnection();            //Connect to database here
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        DBConnection.closeConnection();             //Disconnect from database here
        LoginController.logFile.close();
    }
}
