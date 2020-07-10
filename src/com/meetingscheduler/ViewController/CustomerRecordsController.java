package com.meetingscheduler.ViewController;

import com.meetingscheduler.DAO.Queries;
import com.meetingscheduler.Model.Customer;
import javafx.collections.ObservableList;
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
import java.util.Optional;
import java.util.ResourceBundle;


public class CustomerRecordsController implements Initializable {
    @FXML
    private TextField textfieldName;
    @FXML
    private TextField textfieldAddress1;
    @FXML
    private TextField textfieldAddress2;
    @FXML
    private TextField textfieldCity;
    @FXML
    private TextField textfieldCountry;
    @FXML
    private TextField textfieldPostalCode;
    @FXML
    private TextField textfieldPhoneNumber;
    private final int MAX_TEXTFIELD_NAME = 45;
    private final int MAX_TEXTFIELD_ADDRESS1 = 50;
    private final int MAX_TEXTFIELD_ADDRESS2 = 50;
    private final int MAX_TEXTFIELD_CITY = 50;
    private final int MAX_TEXTFIELD_COUNTRY = 50;
    private final int MAX_TEXTFIELD_POSTALCODE = 10;
    private final int MAX_TEXTFIELD_PHONENUMBER = 20;

    @FXML
    private Button buttonAddCustomer;
    @FXML
    private Button buttonModifyCustomer;
    @FXML
    private Button buttonDeleteCustomer;
    @FXML
    private Button buttonCancel;
    @FXML
    private Button buttonSaveModifications;
    @FXML
    private Button buttonCancelModifications;
    @FXML
    private TableView<Customer> tableviewCustomer;
    @FXML
    private TableColumn<Customer, String> columnCustomerName;
    @FXML
    private TableColumn<Customer, String> columnCustomerAddress;
    @FXML
    private TableColumn<Customer, String> columnCustomerPhoneNumber;
    @FXML
    private TableColumn<Customer, String> columnCustomerCountry;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshScreen();
        columnCustomerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        columnCustomerAddress.setCellValueFactory(new PropertyValueFactory<>("address1"));
        columnCustomerPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phone"));
        columnCustomerCountry.setCellValueFactory(new PropertyValueFactory<>("country"));

        //Setup listeners to limit characters entered into textfields:
        //***multi-line lambdas for addListeners override changed() anonymous class in ChangeListener,
        // eliminating boiler plate code

        textfieldName.textProperty().addListener((ov, oldValue, newValue) -> {
            if (textfieldName.getText().length() > MAX_TEXTFIELD_NAME) {
                String maxTextString = textfieldName.getText().substring(0, MAX_TEXTFIELD_NAME);
                textfieldName.setText(maxTextString);
            }
        });

        textfieldAddress1.textProperty().addListener((ov, oldValue, newValue) -> {
            if (textfieldAddress1.getText().length() > MAX_TEXTFIELD_ADDRESS1) {
                String maxTextString = textfieldAddress1.getText().substring(0, MAX_TEXTFIELD_ADDRESS1);
                textfieldAddress1.setText(maxTextString);
            }
        });

        textfieldAddress2.textProperty().addListener((ov, oldValue, newValue) -> {
            if (textfieldAddress2.getText().length() > MAX_TEXTFIELD_ADDRESS2) {
                String maxTextString = textfieldAddress2.getText().substring(0, MAX_TEXTFIELD_ADDRESS2);
                textfieldAddress2.setText(maxTextString);
            }
        });

        textfieldCity.textProperty().addListener((ov, oldValue, newValue) -> {
            if (textfieldCity.getText().length() > MAX_TEXTFIELD_CITY) {
                String maxTextString = textfieldCity.getText().substring(0, MAX_TEXTFIELD_CITY);
                textfieldCity.setText(maxTextString);
            }
        });

        textfieldCountry.textProperty().addListener((ov, oldValue, newValue) -> {
            if (textfieldCountry.getText().length() > MAX_TEXTFIELD_COUNTRY) {
                String maxTextString = textfieldCountry.getText().substring(0, MAX_TEXTFIELD_COUNTRY);
                textfieldCountry.setText(maxTextString);
            }
        });

        textfieldPostalCode.textProperty().addListener((ov, oldValue, newValue) -> {
            if (textfieldPostalCode.getText().length() > MAX_TEXTFIELD_POSTALCODE) {
                String maxTextString = textfieldPostalCode.getText().substring(0, MAX_TEXTFIELD_POSTALCODE);
                textfieldPostalCode.setText(maxTextString);
            }
        });

        textfieldPhoneNumber.textProperty().addListener((ov, oldValue, newValue) -> {
            if (textfieldPhoneNumber.getText().length() > MAX_TEXTFIELD_PHONENUMBER) {
                String maxTextString = textfieldPhoneNumber.getText().substring(0, MAX_TEXTFIELD_PHONENUMBER);
                textfieldPhoneNumber.setText(maxTextString);
            }
        });
    }

    @FXML
    public void addButtonPushed(ActionEvent event) throws IOException {
        Customer customer = new Customer();
        String customerName = textfieldName.getText();
        String address1 = textfieldAddress1.getText();
        String address2 = textfieldAddress2.getText();
        String postalCode = textfieldPostalCode.getText();
        String phone = textfieldPhoneNumber.getText();
        String city = textfieldCity.getText();
        String country = textfieldCountry.getText();

        if (customerName.isEmpty() || address1.isEmpty() || postalCode.isEmpty() ||
                phone.isEmpty() || city.isEmpty() || country.isEmpty()) {
            showErrorDialog("All fields except 'Address 2' must contain a value.");
        } else {
            customer.setCustomerName(customerName);
            customer.setAddress1(address1);
            customer.setAddress2(address2);
            customer.setPostalCode(postalCode);
            customer.setPhone(phone);
            customer.setCity(city);
            customer.setCountry(country);

            if(event.getSource().equals(buttonAddCustomer)) {
                Queries.addCustomer(customer);
            }
            if(event.getSource().equals(buttonSaveModifications)) {
                customer.setCustomerId(tableviewCustomer.getSelectionModel().getSelectedItem().getCustomerId());
                Queries.updateCustomer(customer);
            }
            refreshScreen();
        }
    }

    @FXML
    public void deleteButtonPushed() {
        Customer selectedCustomer = tableviewCustomer.getSelectionModel().getSelectedItem();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.APPLICATION_MODAL);

        if (selectedCustomer != null) {
            int customerId = selectedCustomer.getCustomerId();
            if(Queries.queryAppointmentForCustomer(customerId)) {
                alert.setTitle("Delete all?");
                alert.setContentText("This customer still has appointments.  Delete customer and all related appointments too?");
                Optional<ButtonType> response = alert.showAndWait();
                if (response.get() == ButtonType.OK) {
                    // process deletion if confirmed
                    Queries.deleteAllCustAppt(customerId);
                    Queries.deleteCustomer(customerId);
                }
            } else {
                alert.setTitle("Delete confirmation.");
                alert.setContentText("Are you sure you want to delete this customer?");
                Optional<ButtonType> response = alert.showAndWait();
                if (response.get() == ButtonType.OK) {
                    Queries.deleteCustomer(customerId);
                }
            }
            refreshScreen();
        }
    }

    @FXML
    public void modifyButtonPushed(ActionEvent event) throws IOException {
        Customer selectedCustomer = tableviewCustomer.getSelectionModel().getSelectedItem();
        if (selectedCustomer != null) {
            textfieldName.setText(selectedCustomer.getCustomerName());
            textfieldAddress1.setText(selectedCustomer.getAddress1());
            textfieldAddress2.setText(selectedCustomer.getAddress2());
            textfieldPostalCode.setText(selectedCustomer.getPostalCode());
            textfieldPhoneNumber.setText(selectedCustomer.getPhone());
            textfieldCity.setText(selectedCustomer.getCity());
            textfieldCountry.setText(selectedCustomer.getCountry());

            buttonDeleteCustomer.setDisable(true);
            buttonModifyCustomer.setDisable(true);
            buttonAddCustomer.setVisible(false);
            buttonSaveModifications.setVisible(true);
            buttonCancelModifications.setVisible(true);
            tableviewCustomer.setDisable(true);
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
        openMainScreen(event);
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

    private void refreshScreen() {
        textfieldName.setText("");
        textfieldAddress1.setText("");
        textfieldAddress2.setText("");
        textfieldCity.setText("");
        textfieldCountry.setText("");
        textfieldPhoneNumber.setText("");
        textfieldPostalCode.setText("");

        buttonDeleteCustomer.setDisable(false);
        buttonModifyCustomer.setDisable(false);
        buttonAddCustomer.setVisible(true);
        buttonSaveModifications.setVisible(false);
        buttonCancelModifications.setVisible(false);
        tableviewCustomer.setDisable(false);
        tableviewCustomer.getItems().clear();
        ObservableList<Customer> customers = Queries.getAllCustomers();
        tableviewCustomer.setItems(customers);
    }
}
