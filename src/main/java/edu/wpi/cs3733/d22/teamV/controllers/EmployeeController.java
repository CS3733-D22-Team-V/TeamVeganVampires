package edu.wpi.cs3733.d22.teamV.controllers;

import edu.wpi.cs3733.d22.teamV.dao.EmployeeDao;
import edu.wpi.cs3733.d22.teamV.main.Vdb;
import edu.wpi.cs3733.d22.teamV.objects.Employee;
import edu.wpi.cs3733.d22.teamV.objects.Patient;
import edu.wpi.cs3733.d22.teamV.servicerequests.ServiceRequest;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class EmployeeController extends MapController {

  private static final EmployeeDao employeeDao = (EmployeeDao) Vdb.requestSystem.getEmployeeDao();
  private boolean updating = false;
  private int updateID;

  public EmployeeController() {}

  private static class SingletonHelper {
    private static final EmployeeController controller = new EmployeeController();
  }

  public static EmployeeController getController() {
    return EmployeeController.SingletonHelper.controller;
  }

  @FXML private TreeTableView<Employee> employeeTable;
  @FXML private TreeTableColumn<Employee, Integer> employeeIDCol;
  @FXML private TreeTableColumn<Employee, String> firstNameCol;
  @FXML private TreeTableColumn<Employee, String> lastNameCol;
  @FXML private TreeTableColumn<Employee, String> employeePositionCol;
  @FXML private TreeTableColumn<Employee, String> patientIDSCol;
  @FXML private TreeTableColumn<Employee, String> specialtiesCol;
  @FXML private TreeTableColumn<Employee, String> employeeServiceRequestIDCol;

  @FXML private TreeTableView<Patient> patientsTable;
  @FXML private TreeTableColumn<Patient, Integer> patientIDCol;
  @FXML private TreeTableColumn<Patient, String> patientFirstNameCol;
  @FXML private TreeTableColumn<Patient, String> patientLastNameCol;
  @FXML private TreeTableColumn<Patient, String> patientServiceRequestIDCol;

  @FXML private TreeTableView<ServiceRequest> serviceRequestTable;
  @FXML private TreeTableColumn<ServiceRequest, String> serviceRequestIDCol;
  @FXML private TreeTableColumn<ServiceRequest, String> serviceRequestPatientIDSCol;
  @FXML private TreeTableColumn<ServiceRequest, String> serviceTypeCol;
  @FXML private TreeTableColumn<ServiceRequest, String> statusCol;
  @FXML private TreeTableColumn<ServiceRequest, String> locationCol;

  @FXML private TextField employeeFirstName;
  @FXML private TextField employeeLastName;
  @FXML private TextField employeePosition;
  @FXML private Button sendRequest;
  @FXML private Label statusLabel;
  private Employee selectedEmployee;

  @FXML
  public void updateEmployeeTreeTable() {
    // Set our cell values based on the MedicineDelivery Class, the Strings represent the actual
    // name of the variable we are adding to a specific column
    employeeIDCol.setCellValueFactory(new TreeItemPropertyValueFactory("employeeID"));
    patientIDSCol.setCellValueFactory(new TreeItemPropertyValueFactory("patientIDs"));
    employeePositionCol.setCellValueFactory(new TreeItemPropertyValueFactory("employeePosition"));
    firstNameCol.setCellValueFactory(new TreeItemPropertyValueFactory("firstName"));
    lastNameCol.setCellValueFactory(new TreeItemPropertyValueFactory("lastName"));
    specialtiesCol.setCellValueFactory(new TreeItemPropertyValueFactory("specialties"));
    employeeServiceRequestIDCol.setCellValueFactory(
        new TreeItemPropertyValueFactory("serviceRequestIDs"));

    // Get the current list of medicine deliveries from the DAO
    ArrayList<Employee> currEmployees = employeeDao.getAllEmployees();
    // Create a list for our tree items
    ArrayList<TreeItem> treeItems = new ArrayList<>();

    // Need to make sure the list isn't empty
    if (currEmployees.isEmpty()) {
      employeeTable.setRoot(null);
      return;
    }

    // for each loop cycling through each medicine delivery currently entered into the system
    for (Employee delivery : currEmployees) {
      TreeItem<Employee> item = new TreeItem(delivery);
      treeItems.add(item);
    }
    // VERY IMPORTANT: Because this is a Tree Table, we need to create a root, and then hide it so
    // we get the standard table functionality
    employeeTable.setShowRoot(false);
    // Root is just the first entry in our list
    TreeItem root = new TreeItem(currEmployees.get(0));
    // Set the root in the table
    employeeTable.setRoot(root);
    // Set the rest of the tree items to the root, including the one we set as the root
    root.getChildren().addAll(treeItems);
  }

  @FXML
  public void updatePatientTreeTable() {
    // Set our cell values based on the MedicineDelivery Class, the Strings represent the actual
    // name of the variable we are adding to a specific column
    patientIDCol.setCellValueFactory(new TreeItemPropertyValueFactory("patientID"));
    patientFirstNameCol.setCellValueFactory(new TreeItemPropertyValueFactory("firstName"));
    patientLastNameCol.setCellValueFactory(new TreeItemPropertyValueFactory("lastName"));
    patientServiceRequestIDCol.setCellValueFactory(new TreeItemPropertyValueFactory("serviceIDs"));

    // Get the current list of medicine deliveries from the DAO
    ArrayList<Patient> currPatients = selectedEmployee.getPatientList();
    // Create a list for our tree items
    ArrayList<TreeItem> treeItems = new ArrayList<>();

    // Need to make sure the list isn't empty
    if (currPatients.isEmpty()) {
      patientsTable.setRoot(null);
      return;
    }

    // for each loop cycling through each medicine delivery currently entered into the system
    for (Patient patient : currPatients) {
      TreeItem<Employee> item = new TreeItem(patient);
      treeItems.add(item);
    }
    // VERY IMPORTANT: Because this is a Tree Table, we need to create a root, and then hide it so
    // we get the standard table functionality
    patientsTable.setShowRoot(false);
    // Root is just the first entry in our list
    TreeItem root = new TreeItem(currPatients.get(0));
    // Set the root in the table
    patientsTable.setRoot(root);
    // Set the rest of the tree items to the root, including the one we set as the root
    root.getChildren().addAll(treeItems);
  }

  @FXML
  public void updateServiceRequestTreeTable() {
    // Set our cell values based on the MedicineDelivery Class, the Strings represent the actual
    // name of the variable we are adding to a specific column
    serviceRequestIDCol.setCellValueFactory(new TreeItemPropertyValueFactory("serviceID"));
    serviceRequestPatientIDSCol.setCellValueFactory(new TreeItemPropertyValueFactory("patientID"));
    serviceTypeCol.setCellValueFactory(new TreeItemPropertyValueFactory("type"));
    statusCol.setCellValueFactory(new TreeItemPropertyValueFactory("status"));
    locationCol.setCellValueFactory(new TreeItemPropertyValueFactory("location"));

    // Get the current list of medicine deliveries from the DAO
    ArrayList<ServiceRequest> currRequests = selectedEmployee.getServiceRequestList();
    // Create a list for our tree items
    ArrayList<TreeItem> treeItems = new ArrayList<>();

    // Need to make sure the list isn't empty
    if (currRequests.isEmpty()) {
      serviceRequestTable.setRoot(null);
      return;
    }

    // for each loop cycling through each medicine delivery currently entered into the system
    for (ServiceRequest request : currRequests) {
      TreeItem<Employee> item = new TreeItem(request);
      treeItems.add(item);
    }
    // VERY IMPORTANT: Because this is a Tree Table, we need to create a root, and then hide it so
    // we get the standard table functionality
    serviceRequestTable.setShowRoot(false);
    // Root is just the first entry in our list
    TreeItem root = new TreeItem(currRequests.get(0));
    // Set the root in the table
    serviceRequestTable.setRoot(root);
    // Set the rest of the tree items to the root, including the one we set as the root
    root.getChildren().addAll(treeItems);
  }

  @FXML
  public void updateAllTreeTables() {
    updateEmployeeTreeTable();
  }

  @FXML
  public void checkIfSelected() {
    try {
      selectedEmployee = employeeTable.getSelectionModel().getSelectedItem().getValue();
      System.out.println("Selected employee!");
      updatePatientTreeTable();
      updateServiceRequestTreeTable();
    } catch (NullPointerException e) {
      // selectedEmployee = null;
    }
  }

  @FXML
  public void validateButton() {
    if (updating) {
      sendRequest.setText("Update Request");
    } else {
      sendRequest.setText("Send Request");
    }

    try {
      if ((employeeFirstName.getText().equals("")
          && employeeLastName.getText().equals("")
          && employeePosition.getText().equals(""))) {
        sendRequest.setDisable(true);
        statusLabel.setText("Status: Blank");
        statusLabel.setTextFill(Color.web("Black"));
      } else if ((employeeFirstName.getText().equals("")
          || employeeLastName.getText().equals("")
          || employeePosition.getText().equals(""))) {
        sendRequest.setDisable(true);
        statusLabel.setText("Status: Processing");
        statusLabel.setTextFill(Color.web("Black"));
      } else {
        sendRequest.setDisable(false);
      }
    } catch (Exception e) {
      sendRequest.setDisable(true);
    }
  }

  /** Determines if an employee is valid, and sends it to the Dao */
  public void sendRequest() {
    // If any field is left blank, (except for request details) throw an error
    // Make sure the patient ID is an integer
    {
      Employee employee =
          new Employee(
              Vdb.requestSystem.getEmployeeID(),
              employeeFirstName.getText(),
              employeeLastName.getText(),
              employeePosition.getText(),
              new ArrayList<String>(),
              new ArrayList<Integer>(),
              new ArrayList<Integer>(),
              false);
      // Send the request to the Dao pattern
      try {
        if (updating) {
          employeeDao.updateEmployee(employee, updateID);
          updating = false;
        } else {
          employeeDao.addEmployee(employee);
        }

      } catch (Exception e) {
        e.printStackTrace();
      }

      resetForm(); // Set all fields to blank for another entry
      updateEmployeeTreeTable();
    }
  }

  @FXML
  public void resetForm() {
    updating = false;
    employeeFirstName.setText("");
    employeeLastName.setText("");
    employeePosition.setText("");
    sendRequest.setDisable(true);
  }

  @FXML
  private void removeSelectedRow() throws IOException, NullPointerException, SQLException {
    try {
      Employee employee = employeeTable.getSelectionModel().getSelectedItem().getValue();
      employeeDao.removeEmployee(employee);
    } catch (NullPointerException e) {
      e.printStackTrace();
    }
    updateEmployeeTreeTable();
  }

  @FXML
  private void updateSelectedRow() throws IOException, NullPointerException, SQLException {
    updating = true;
    Employee employee = employeeTable.getSelectionModel().getSelectedItem().getValue();
    updateID = employee.getEmployeeID();
    employeeFirstName.setText(String.valueOf(employee.getFirstName()));
    employeeLastName.setText(String.valueOf(employee.getLastName()));
    employeePosition.setText(employee.getEmployeePosition());

    updateEmployeeTreeTable();
  }

  @Override
  public void start(Stage primaryStage) throws Exception {}
}
