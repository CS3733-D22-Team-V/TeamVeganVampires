package edu.wpi.cs3733.d22.teamV.controllers;

import com.jfoenix.controls.JFXComboBox;
import edu.wpi.cs3733.d22.teamV.dao.SanitationRequestDao;
import edu.wpi.cs3733.d22.teamV.main.RequestSystem;
import edu.wpi.cs3733.d22.teamV.main.RequestSystem.Dao;
import edu.wpi.cs3733.d22.teamV.main.Vdb;
import edu.wpi.cs3733.d22.teamV.servicerequests.SanitationRequest;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class SanitationRequestController extends RequestController {
  @FXML private TextField hospitalID;
  @FXML private TextField patientID;
  @FXML private TextField roomLocation;
  @FXML private JFXComboBox<Object> sanitationDropDown;
  @FXML private Button sendRequest;
  @FXML private TextArea requestDetails;
  @FXML private Label statusLabel;

  @FXML private TreeTableView<SanitationRequest> sanitationRequestTable;
  @FXML private TreeTableColumn<SanitationRequest, Integer> patientIDCol;
  @FXML private TreeTableColumn<SanitationRequest, Integer> hospitalIDCol;
  @FXML private TreeTableColumn<SanitationRequest, String> firstNameCol;
  @FXML private TreeTableColumn<SanitationRequest, String> lastNameCol;
  @FXML private TreeTableColumn<SanitationRequest, String> roomLocationCol;
  @FXML private TreeTableColumn<SanitationRequest, String> hazardCol;
  @FXML private TreeTableColumn<SanitationRequest, String> requestDetailsCol;
  private boolean updating = false;
  private int updateServiceID;

  private static final SanitationRequestDao SanitationRequestDao =
      (SanitationRequestDao) Vdb.requestSystem.getDao(Dao.SanitationRequest);

  private static class SingletonHelper {
    private static final SanitationRequestController controller = new SanitationRequestController();
  }

  public static SanitationRequestController getController() {
    return SanitationRequestController.SingletonHelper.controller;
  }

  // private static SanitationRequest sanitationRequest;

  @Override
  public void init() {
    mapSetUp();
    updateTreeTable();
    filterCheckBox.getCheckModel().check("Sanitation Requests");
    setTitleText("Sanitation Request Service");
    fillTopPane();
    updating = false;
  }
  // if the fields get erased, can still send request
  @FXML
  private void checkValidation() {
    if (!(patientID.getText().equals("")
        || hospitalID.getText().equals("")
        || roomLocation.getText().equals("")
        || requestDetails.getText().equals("")
        || sanitationDropDown.getValue() == null)) {
      sendRequest.setDisable(false);
    }
  }

  /** Determines if a medical delivery request is valid, and sends it to the Dao */
  @FXML
  void validateButton() {

    // If any field is left blank, (except for request details) throw an error
    if (patientID.getText().equals("")
        || hospitalID.getText().equals("")
        || roomLocation.getText().equals("")
        || requestDetails.getText().equals("")
        || sanitationDropDown.getValue() == null) {

      // Set the text and color of the status label
      statusLabel.setText("All fields must be entered!");
      statusLabel.setTextFill(Color.web("Red"));

      // Make sure the patient ID is an integer
    } else if (!isInteger(patientID.getText())) {
      statusLabel.setText("Status: Failed. Patient ID must be a number!");
      statusLabel.setTextFill(Color.web("Red"));

      // If all conditions pass, create the request
    } else {

      // Set the label to green, and let the user know it has been processed
      statusLabel.setText("Status: Processed Successfully");
      statusLabel.setTextFill(Color.web("Green"));

      resetForm(); // Set all fields to blank for another entry
    }
  }

  @Override
  public void start(Stage primaryStage) throws Exception {}

  /**
   * Determines if a String is an integer or not
   *
   * @param input is a string
   * @return true if the string is an integer, false if not
   */
  public boolean isInteger(String input) {
    try {
      Integer.parseInt(input);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  @FXML
  void sendRequest()
      throws SQLException, IOException { // must check to see if its updating or new req
    System.out.println("WE GOT HERE");
    SanitationRequest request =
        new SanitationRequest(
            Integer.parseInt(hospitalID.getText()),
            Integer.parseInt(patientID.getText()),
            roomLocation.getText(),
            sanitationDropDown.getValue().toString(),
            requestDetails.getText());
    System.out.println("HEYO");
    if (updating) {
      SanitationRequestDao.updateServiceRequest(request, request.getServiceID());
    } else {
      SanitationRequestDao.addServiceRequest(request);
    }
    updating = false;
    // System.out.println(hospitalID + patientID + roomLocation,sanitationDropDown,requestDetails);
    updateTreeTable();
    resetForm(); // Set all fields to blank for another entry
  }

  @FXML
  void updateTreeTable() {
    hospitalIDCol.setCellValueFactory(new TreeItemPropertyValueFactory("hospitalID"));
    patientIDCol.setCellValueFactory(new TreeItemPropertyValueFactory("patientID"));
    firstNameCol.setCellValueFactory(new TreeItemPropertyValueFactory("patientFirstName"));
    lastNameCol.setCellValueFactory(new TreeItemPropertyValueFactory("patientLastName"));
    roomLocationCol.setCellValueFactory(new TreeItemPropertyValueFactory("roomLocation"));
    hazardCol.setCellValueFactory(new TreeItemPropertyValueFactory("hazardName"));
    requestDetailsCol.setCellValueFactory(new TreeItemPropertyValueFactory("requestDetails"));

    ArrayList<SanitationRequest> currSanitationRequests =
        (ArrayList<SanitationRequest>)
            RequestSystem.getSystem().getAllServiceRequests(Dao.SanitationRequest);

    ArrayList<TreeItem> treeItems = new ArrayList<>();

    if (currSanitationRequests.isEmpty()) {
      sanitationRequestTable.setRoot(null);
    } else {
      for (SanitationRequest delivery : currSanitationRequests) {
        TreeItem<SanitationRequest> item = new TreeItem(delivery); // claims null pointer
        treeItems.add(item);
      }
      sanitationRequestTable.setShowRoot(false);
      TreeItem root = new TreeItem(currSanitationRequests.get(0));
      sanitationRequestTable.setRoot(root);
      root.getChildren().addAll(treeItems);
    }
  }

  /** Sets all the fields to their default value for another entry */
  @FXML
  void resetForm() {
    patientID.setText("");
    hospitalID.setText("");
    roomLocation.setText("");
    sanitationDropDown.setValue(null);
    requestDetails.setText("");
  }
  // same error as remove, pressing sendrequest causes issue
  @FXML
  private void updateSelectedRow() throws IOException, NullPointerException, SQLException {
    updating = true;
    if (sanitationRequestTable.getSelectionModel().getSelectedItem() != null) {
      SanitationRequest request =
          sanitationRequestTable.getSelectionModel().getSelectedItem().getValue();

      hospitalID.setText(String.valueOf(request.getHospitalID()));
      patientID.setText(String.valueOf(request.getPatientID()));
      roomLocation.setText(request.getRoomLocation());
      sanitationDropDown.setValue(request.getHazardName());
      requestDetails.setText(request.getRequestDetails());
      updateServiceID = request.getServiceID();
      updateTreeTable();
    }
  }
  // has error
  @FXML
  private void removeSelectedRow() throws IOException, NullPointerException, SQLException {
    try {
      SanitationRequest delivery =
          sanitationRequestTable.getSelectionModel().getSelectedItem().getValue();
      SanitationRequestDao.removeServiceRequest(delivery);
    } catch (NullPointerException e) {
      e.printStackTrace();
    }
    updateTreeTable();
  }
}
