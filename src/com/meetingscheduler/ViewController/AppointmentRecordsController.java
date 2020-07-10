package com.meetingscheduler.ViewController;

import com.meetingscheduler.DAO.Queries;
import com.meetingscheduler.Model.Appointment;
import com.meetingscheduler.Model.Customer;
import com.meetingscheduler.Utils.TimeFunctions;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;

public class AppointmentRecordsController implements Initializable {

    @FXML
    ComboBox<Customer> comboboxCustomerName;
    @FXML
    DatePicker datepickerAppointmentDate;
    @FXML
    ComboBox<String> comboboxStartHour;
    @FXML
    ComboBox<String> comboboxStartMinute;
    @FXML
    ComboBox<String> comboboxEndHour;
    @FXML
    ComboBox<String> comboboxEndMinute;
    @FXML
    TextField textfieldAppointmentType;
    private final int MAX_TEXTFIELD_TYPE = 50;

    @FXML
    private Button buttonAddAppointment;
    @FXML
    private Button buttonModifyAppointment;
    @FXML
    private Button buttonDeleteAppointment;
    @FXML
    private Button buttonCancel;
    @FXML
    private Button buttonSaveModifications;
    @FXML
    private Button buttonCancelModifications;
    @FXML
    private RadioButton radiobuttonViewAll;
    @FXML
    private RadioButton radiobuttonViewMonth;
    @FXML
    private RadioButton radiobuttonViewWeek;
    @FXML
    private TableView<Appointment> tableviewAppointments;
    @FXML
    private TableColumn<Customer, String> columnCustomerName;
    @FXML
    private TableColumn<Appointment, String> columnAppointmentType;
    @FXML
    private TableColumn<Appointment, String> columnAppointmentStart;
    @FXML
    private TableColumn<Appointment, String> columnAppointmentEnd;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshScreen();

        columnCustomerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        columnAppointmentType.setCellValueFactory(new PropertyValueFactory<>("type"));
        columnAppointmentStart.setCellValueFactory(new PropertyValueFactory<>("start"));
        columnAppointmentEnd.setCellValueFactory(new PropertyValueFactory<>("end"));

        //configure start and end date presentation in table for readability:
        //***multi-line lambdas to override the call() anonymous function from the functional interface Callback
        columnAppointmentStart.setCellValueFactory(
                start -> {
                    SimpleStringProperty startTime = new SimpleStringProperty();
                    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy kk:mm");
                    startTime.setValue(dateFormat.format(start.getValue().getStart()));
                    return startTime;
                });
        columnAppointmentEnd.setCellValueFactory(
                end -> {
                    SimpleStringProperty endTime = new SimpleStringProperty();
                    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy kk:mm");
                    endTime.setValue(dateFormat.format(end.getValue().getEnd()));
                    return endTime;
                });

        //populate customerName drop down
        ObservableList<Customer> customers = Queries.getAllCustomers();
        for (Customer customer : customers) {
            comboboxCustomerName.getItems().add(customer);
        }

        comboboxStartHour.getItems().addAll("06", "07", "08", "09", "10", "11", "12", "13", "14", "15",
                "16", "17", "18");
        comboboxEndHour.getItems().addAll("06", "07", "08", "09", "10", "11", "12", "13", "14", "15",
                "16", "17", "18");
        comboboxStartMinute.getItems().addAll("00", "15", "30", "45");
        comboboxEndMinute.getItems().addAll("00", "15", "30", "45");

        datepickerAppointmentDate.setEditable(false);

        //configure datepicker to restrict non-working days:
        //***multi-line lambda to replace the call() anonymous function from the functional interface Callback
        datepickerAppointmentDate.setDayCellFactory(picker -> {
            return new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    setDisable(empty || date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY);
                }
            };
        });
        //Setup listener to limit characters entered into textfield:
        textfieldAppointmentType.textProperty().addListener((ov, oldValue, newValue) -> {
            if (textfieldAppointmentType.getText().length() > MAX_TEXTFIELD_TYPE) {
                String maxTextString = textfieldAppointmentType.getText().substring(0, MAX_TEXTFIELD_TYPE);
                textfieldAppointmentType.setText(maxTextString);
            }
        });
    }

    @FXML
    private void addButtonPushed(ActionEvent event) throws IOException {
        Appointment appointment = new Appointment();

        if (comboboxCustomerName.getValue() == null || comboboxStartHour.getSelectionModel().isEmpty() ||
                comboboxStartMinute.getSelectionModel().isEmpty() || comboboxEndHour.getSelectionModel().isEmpty() ||
                comboboxEndMinute.getSelectionModel().isEmpty() || textfieldAppointmentType.getText().isEmpty() ||
                datepickerAppointmentDate.getValue() == null) {
            showErrorDialog("All fields must contain a value.");
        } else {
            String startDate = datepickerAppointmentDate.getValue().toString();
            String startHour = comboboxStartHour.getValue();
            String startMinute = comboboxStartMinute.getValue();
            String endHour = comboboxEndHour.getValue();
            String endMinute = comboboxEndMinute.getValue();
            String type = textfieldAppointmentType.getText();
            int customerId = comboboxCustomerName.getValue().getCustomerId();
            Timestamp start = TimeFunctions.stringToTimestamp(startDate, startHour, startMinute);
            Timestamp end = TimeFunctions.stringToTimestamp(startDate, endHour, endMinute);

            appointment.setCustomerId(customerId);
            appointment.setStart(start);
            appointment.setEnd(end);
            appointment.setType(type);

            if(appointmentOverlapTest(event, appointment)) {
                showErrorDialog("New appointment times cannot occur during any existing appointments");
            } else if (start.after(end) || start.equals(end)) {
                showErrorDialog("Meeting end time must be after meeting start time.");
            } else {
                if (event.getSource().equals(buttonAddAppointment)) {
                    Queries.addAppointment(appointment);
                }
                if (event.getSource().equals(buttonSaveModifications)) {
                    appointment.setAppointmentId(tableviewAppointments.getSelectionModel().getSelectedItem().getAppointmentId());
                    Queries.updateAppointment(appointment);
                }
                refreshScreen();
            }
        }
    }

    private boolean appointmentOverlapTest(ActionEvent event, Appointment appointment) {
            ObservableList<Appointment> appointments = Queries.getAllAppointments();

            Timestamp newStart = appointment.getStart();
            Timestamp newEnd = appointment.getEnd();
            if(event.getSource().equals(buttonSaveModifications)) {
                appointment.setAppointmentId(tableviewAppointments.getSelectionModel().getSelectedItem().getAppointmentId());
            }

            for (Appointment existingApp : appointments) {
                Timestamp oldStart = existingApp.getStart();
                Timestamp oldEnd = existingApp.getEnd();

                if (appointment.getAppointmentId() == existingApp.getAppointmentId()) {      //if modifying same appointment, it shouldn't check against itself
                    continue;
                } else if (newStart.before(oldStart) && newEnd.after(oldStart)) {
                    return true;
                } else if (newStart.equals(oldStart) && newEnd.equals(oldEnd)) {
                    return true;
                } else if (newStart.after(oldStart) && newStart.before(oldEnd)) {
                    return true;
                } else if (newStart.before(oldStart) && newStart.after(oldEnd)) {
                    return true;
                } else if (newEnd.after(oldStart) && newEnd.before(oldEnd)) {
                    return true;
                } else if (newStart.after(oldStart) && newStart.before(oldEnd)) {
                    return true;
                }
            }
        return false;
    }

    @FXML
    public void deleteButtonPushed() {
        Appointment selectedAppointment = tableviewAppointments.getSelectionModel().getSelectedItem();
        if (selectedAppointment != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setTitle("Delete confirmation.");
            alert.setContentText("Are you sure you want to delete this appointment?");
            Optional<ButtonType> response = alert.showAndWait();
            if (response.get() == ButtonType.OK) {
                Queries.deleteAppointment(selectedAppointment.getAppointmentId());
            }
            refreshScreen();
        }
    }

    @FXML
    public void modifyButtonPushed(ActionEvent event) throws IOException {
        Appointment selectedAppointment = tableviewAppointments.getSelectionModel().getSelectedItem();

        if (selectedAppointment != null) {
            //parse datetime info out of Timestamps
            LocalDate startDate = LocalDate.parse(selectedAppointment.getStart().toString().substring(0, 10));
            String startHour = selectedAppointment.getStart().toString().substring(11, 13);
            String startMinute = selectedAppointment.getStart().toString().substring(14, 16);
            String endHour = selectedAppointment.getEnd().toString().substring(11, 13);
            String endMinute = selectedAppointment.getEnd().toString().substring(14, 16);

            //set fields with current appointment data
            comboboxCustomerName.setValue(selectedAppointment.getCustomer());
            datepickerAppointmentDate.setValue(startDate);
            comboboxStartHour.setValue(startHour);
            comboboxStartMinute.setValue(startMinute);
            comboboxEndHour.setValue(endHour);
            comboboxEndMinute.setValue(endMinute);
            textfieldAppointmentType.setText(selectedAppointment.getType());

            buttonAddAppointment.setVisible(false);
            buttonDeleteAppointment.setDisable(true);
            buttonModifyAppointment.setDisable(true);
            buttonSaveModifications.setVisible(true);
            buttonCancelModifications.setVisible(true);
            tableviewAppointments.setDisable(true);
        }
    }

    @FXML
    public void cancelModificationButtonPushed(ActionEvent event) throws IOException {
        refreshScreen();
    }

    private void showErrorDialog(String errorText) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error");
        alert.setHeaderText("Error on Input");
        alert.setContentText(errorText);
        alert.showAndWait();
    }

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
    public void radioButtonSelected(ActionEvent event) throws IOException {
        if(event.getSource().equals(radiobuttonViewWeek)) {
            refreshScreen();
            filterList(LocalDateTime.now().plusDays(7));
        } else if(event.getSource().equals(radiobuttonViewMonth)) {
            refreshScreen();
            filterList(LocalDateTime.now().plusMonths(1));
        } else if(event.getSource().equals(radiobuttonViewAll)){
            refreshScreen();
        }
    }

    private void filterList(LocalDateTime filterTime) {
        try {
            ObservableList<Appointment> appointments = Queries.getAllAppointments();
            FilteredList<Appointment> appointmentsFiltered = new FilteredList<>(appointments);
            //***multi-line lambda to override the test() anonymous function from the functional interface Predicate for date test
            appointmentsFiltered.setPredicate(row -> {
                LocalDateTime appointmentStart = row.getStart().toLocalDateTime();
                return appointmentStart.isAfter(LocalDateTime.now()) && appointmentStart.isBefore(filterTime);
            });
            tableviewAppointments.setItems(appointmentsFiltered);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshScreen() {
        ObservableList<Appointment> appointments = Queries.getAllAppointments();
        tableviewAppointments.setItems(appointments);
        buttonAddAppointment.setVisible(true);
        buttonDeleteAppointment.setDisable(false);
        buttonModifyAppointment.setDisable(false);
        buttonSaveModifications.setVisible(false);
        buttonCancelModifications.setVisible(false);
        tableviewAppointments.setDisable(false);

        comboboxCustomerName.setValue(null);
        datepickerAppointmentDate.setValue(null);
        comboboxStartHour.setValue(null);
        comboboxStartMinute.setValue(null);
        comboboxEndHour.setValue(null);
        comboboxEndMinute.setValue(null);
        textfieldAppointmentType.setText("");
    }
}
