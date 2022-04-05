package edu.wpi.veganvampires.controllers;

import com.jfoenix.controls.JFXComboBox;
import edu.wpi.veganvampires.dao.EquipmentDeliveryDao;
import edu.wpi.veganvampires.main.Vdb;
import edu.wpi.veganvampires.objects.EquipmentDelivery;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.stage.Stage;

public class EquipmentRequestController extends Controller {

  @FXML private TreeTableView<EquipmentDelivery> equipmentRequestTable;
  @FXML private TreeTableColumn<EquipmentDelivery, Integer> patientIDCol;
  @FXML private TreeTableColumn<EquipmentDelivery, Integer> employeeIDCol;
  @FXML private TreeTableColumn<EquipmentDelivery, String> firstNameCol;
  @FXML private TreeTableColumn<EquipmentDelivery, String> lastNameCol;
  @FXML private TreeTableColumn<EquipmentDelivery, String> posCol;
  @FXML private TreeTableColumn<EquipmentDelivery, String> equipCol;
  @FXML private TreeTableColumn<EquipmentDelivery, Integer> quantCol;
  @FXML private TreeTableColumn<EquipmentDelivery, String> notesCol;

  @FXML private TextField patientID;
  @FXML private TextField employeeID;
  @FXML private TextField firstName;
  @FXML private TextField lastName;
  @FXML private Label status;
  @FXML private TextField pos;
  @FXML private JFXComboBox<Object> dropDown;
  @FXML private TextField quant;
  @FXML private TextArea notes;
  @FXML private Button sendRequest;

  private static EquipmentDeliveryDao equipmentDeliveryDao = Vdb.equipmentDeliveryDao;

  @FXML
  private void updateTreeTable() {
    employeeIDCol.setCellValueFactory(new TreeItemPropertyValueFactory("employeeID"));
    patientIDCol.setCellValueFactory(new TreeItemPropertyValueFactory("patientID"));
    firstNameCol.setCellValueFactory(new TreeItemPropertyValueFactory("patientFirstName"));
    lastNameCol.setCellValueFactory(new TreeItemPropertyValueFactory("patientLastName"));
    posCol.setCellValueFactory(new TreeItemPropertyValueFactory("location"));
    equipCol.setCellValueFactory(new TreeItemPropertyValueFactory("equipment"));
    quantCol.setCellValueFactory(new TreeItemPropertyValueFactory("quantity"));
    notesCol.setCellValueFactory(new TreeItemPropertyValueFactory("notes"));

    ArrayList<EquipmentDelivery> currEquipmentDeliveries =
        (ArrayList<EquipmentDelivery>) equipmentDeliveryDao.getAllEquipmentDeliveries();

    ArrayList<TreeItem> treeItems = new ArrayList<>();

    if (!currEquipmentDeliveries.isEmpty()) {

      for (EquipmentDelivery delivery : currEquipmentDeliveries) {
        TreeItem<EquipmentDelivery> item = new TreeItem(delivery);
        treeItems.add(item);
      }
      equipmentRequestTable.setShowRoot(false);
      TreeItem root = new TreeItem(equipmentDeliveryDao.getAllEquipmentDeliveries().get(0));
      equipmentRequestTable.setRoot(root);
      root.getChildren().addAll(treeItems);
    }
  }

  @FXML
  private void resetForm() {
    employeeID.setText("");
    patientID.setText("");
    firstName.setText("");
    lastName.setText("");
    status.setText("Status: Blank");
    pos.setText("");
    notes.setText("");
    quant.setText("");
    dropDown.setValue(null);
    sendRequest.setDisable(true);
  }

  @FXML
  private void validateButton() {
    if (!(employeeID.getText().isEmpty())
        && !(patientID.getText().isEmpty())
        && !(employeeID.getText().isEmpty())
        && !(firstName.getText().isEmpty())
        && !(lastName.getText().isEmpty())
        && !(pos.getText().isEmpty())
        && !(dropDown.getValue() == null)
        && !(notes.getText().isEmpty())
        && !(quant.getText().isEmpty())
        && isInteger(quant.getText())) {
      status.setText("Status: Done");
      sendRequest.setDisable(false);

    } else if (!(employeeID.getText().isEmpty())
        || !(patientID.getText().isEmpty())
        || !(employeeID.getText().isEmpty())
        || !(firstName.getText().isEmpty())
        || !(lastName.getText().isEmpty())
        || !(status.getText().isEmpty())
        || !(pos.getText().isEmpty())
        || !(dropDown.getValue() == null)
        || !(notes.getText().isEmpty())
        || !(quant.getText().isEmpty())) {
      status.setText("Status: Processing");
      sendRequest.setDisable(true);

      if (!isInteger(quant.getText()) && !quant.getText().isEmpty()) {
        status.setText("Status: Quantity is not a number");
      }

    } else {
      status.setText("Status: Blank");
      sendRequest.setDisable(true);
    }
  }

  @FXML
  private void sendRequest() throws SQLException {
    equipmentDeliveryDao.addEquipmentDelivery(
        pos.getText(),
        dropDown.getValue().toString(),
        notes.getText(),
        Integer.parseInt(quant.getText()));
    resetForm();
    updateTreeTable();
  }

  @Override
  public void start(Stage primaryStage) {}

  // used to get coordinates after clicking map
  @FXML private TextArea coordinates;
  private Point point = new Point();
  private int xCoord, yCoord;

  @FXML
  private void mapCoordTracker() {
    point = MouseInfo.getPointerInfo().getLocation();
    xCoord = point.x - 712;
    yCoord = point.y - 230;
    coordinates.setText("X: " + xCoord + " Y: " + yCoord);
  }
}
