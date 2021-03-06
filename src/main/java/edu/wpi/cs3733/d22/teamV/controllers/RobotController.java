package edu.wpi.cs3733.d22.teamV.controllers;

import com.jfoenix.controls.JFXComboBox;
import edu.wpi.cs3733.d22.teamV.dao.LocationDao;
import edu.wpi.cs3733.d22.teamV.dao.RobotDao;
import edu.wpi.cs3733.d22.teamV.main.RequestSystem;
import edu.wpi.cs3733.d22.teamV.main.RequestSystem.*;
import edu.wpi.cs3733.d22.teamV.main.Vdb;
import edu.wpi.cs3733.d22.teamV.objects.Employee;
import edu.wpi.cs3733.d22.teamV.servicerequests.RobotRequest;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.stage.Stage;

public class RobotController extends RequestController {
  @FXML private TreeTableView<RobotRequest> table;
  @FXML private TreeTableColumn<RobotRequest, String> botIDCol;
  @FXML private TreeTableColumn<RobotRequest, Integer> employeeIDCol;
  @FXML private TreeTableColumn<RobotRequest, Integer> nodeIDCol;
  @FXML private TreeTableColumn<RobotRequest, String> detailsCol;
  @FXML private TreeTableColumn<RobotRequest, String> statusCol;
  @FXML private TreeTableColumn<RobotRequest, String> timeStampCol;

  private static final RobotDao RobotDao = (RobotDao) Vdb.requestSystem.getDao(Dao.RobotRequest);
  @FXML private TextField employeeID;
  @FXML private TextField botID;
  @FXML private TextField nodeID;
  @FXML private TextField details;
  @FXML private JFXComboBox<Object> statusDropDown;
  @FXML private Label status;
  @FXML private Button sendRequest;

  private boolean updating = false;
  private int updateServiceID;

  private static class SingletonHelper {
    private static final RobotController manager = new RobotController();
  }

  private static final LocationDao LocationDao = Vdb.requestSystem.getLocationDao();

  public static RobotController getManager() {
    return RobotController.SingletonHelper.manager;
  }

  @Override
  public void init() {
    setTitleText("Robot Request");
    fillTopPane();
    validateButton();
  }

  @Override
  @FXML
  public void resetForm() {
    nodeID.setText("");
    statusDropDown.setValue("Status: ");
    employeeID.setText("");
    botID.setText("");
    details.setText("");
    sendRequest.setDisable(true);
    sendRequest.setText("Send Request");
  }

  boolean findEmployee() { // returns true if finds patient
    boolean result = false;
    if (!employeeID.getText().isEmpty() && isInteger(employeeID.getText())) {
      for (Employee e : Vdb.requestSystem.getEmployees()) {
        if (e.getEmployeeID() == Integer.parseInt(employeeID.getText())) {
          result = true;
          break;
        }
      }
    }
    return result;
  }

  // Checks to see if the user can submit info
  @Override
  public void validateButton() {
    sendRequest.setDisable(true);
    try {
      if ((employeeID.getText().isEmpty())
          && botID.getText().isEmpty()
          && statusDropDown.getValue() == null
          && nodeID.getText().isEmpty()) {
        status.setText("Status: Blank");
      } else if ((employeeID.getText().isEmpty())
          || (botID.getText().isEmpty())
          || statusDropDown.getValue() == null
          || nodeID.getText().isEmpty()) {
        status.setText("Status: Processing");
      } else if (LocationDao.getLocation(nodeID.getText()) == null) {
        status.setText("Status: Invalid Location");
      } else if (!findEmployee()) {
        status.setText("Status: Invalid Employee");
      } else if (!isInteger(botID.getText())) {
        status.setText("Status: Invalid Bot ID");
      } else {
        sendRequest.setDisable(false);
      }
    } catch (NullPointerException e) {

    }
  }

  /** Runs whenever we switch to the table, or update a value */
  @Override
  public void updateTreeTable() {
    // Set our cell values based on the RobotRequest Class, the Strings represent the actual
    // name of the variable we are adding to a specific column
    nodeIDCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("nodeID"));
    employeeIDCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("employeeID"));
    botIDCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("botID"));
    detailsCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("details"));
    statusCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("status"));
    timeStampCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("timeMade"));
    // Get the current list of lab requests from the DAO
    ArrayList<RobotRequest> currRobotRequests =
        (ArrayList<RobotRequest>) RobotDao.getAllServiceRequests();

    // Create a list for our tree items
    ArrayList<TreeItem<RobotRequest>> treeItems = new ArrayList<>();

    // Need to make sure the list isn't empty
    if (currRobotRequests.isEmpty()) {
      table.setRoot(null);
    } else {
      // for each loop cycling through each lab request currently entered into the system
      for (RobotRequest lab : currRobotRequests) {
        TreeItem<RobotRequest> item = new TreeItem<>(lab);
        treeItems.add(item);
      }
      // VERY IMPORTANT: Because this is a Tree Table, we need to create a root, and then hide it so
      // we get the standard table functionality
      table.setShowRoot(false);
      // Root is just the first entry in our list
      TreeItem<RobotRequest> root = new TreeItem<>(currRobotRequests.get(0));
      // Set the root in the table
      table.setRoot(root);
      // Set the rest of the tree items to the root, including the one we set as the root
      root.getChildren().addAll(treeItems);
    }
  }

  public void sendRequest() {
    // Make sure the patient ID is an integer
    if (!isInteger(botID.getText()) || !isInteger(employeeID.getText())) {
      status.setText("Status: Failed. Bot/Hospital ID must be a number!");

      // If all conditions pass, create the request
    } else {
      RobotRequest r =
          new RobotRequest(
              Integer.parseInt(employeeID.getText()),
              Integer.parseInt(botID.getText()),
              nodeID.getText(),
              details.getText(),
              statusDropDown.getValue().toString(),
              Timestamp.from(Instant.now()).toString());
      try {
        if (updating) {
          Vdb.requestSystem.getDao(Dao.RobotRequest).updateServiceRequest(r, updateServiceID);
          updating = false;
        } else {
          RequestSystem.getSystem().addServiceRequest(r, Dao.RobotRequest);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }

      resetForm();
      updateTreeTable();
    }
  }

  @FXML
  private void updateSelectedRow() throws NullPointerException {
    System.out.println("here");
    updating = true;
    RobotRequest request = table.getSelectionModel().getSelectedItem().getValue();
    updateServiceID = request.getServiceID();

    nodeID.setText(request.getNodeID());
    employeeID.setText(Integer.toString(request.getEmployeeID()));
    botID.setText(Integer.toString(request.getBotID()));
    details.setText(request.getDetails());
    statusDropDown.setValue(request.getStatus());

    sendRequest.setText("Update");
    updateTreeTable();
  }

  @FXML
  private void removeSelectedRow() throws IOException, NullPointerException, SQLException {
    try {
      RobotRequest request = table.getSelectionModel().getSelectedItem().getValue();
      RequestSystem.getSystem().getDao(Dao.RobotRequest).removeServiceRequest(request);
    } catch (NullPointerException e) {
      e.printStackTrace();
    }
    updateTreeTable();
  }

  @Override
  public void start(Stage primaryStage) {}
}
