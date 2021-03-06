package edu.wpi.cs3733.d22.teamV.controllers;

import edu.wpi.cs3733.d22.teamV.main.RequestSystem;
import edu.wpi.cs3733.d22.teamV.main.Vdb;
import edu.wpi.cs3733.d22.teamV.map.EquipmentIcon;
import edu.wpi.cs3733.d22.teamV.map.Floor;
import edu.wpi.cs3733.d22.teamV.map.MapManager;
import edu.wpi.cs3733.d22.teamV.objects.Equipment;
import edu.wpi.cs3733.d22.teamV.objects.Patient;
import edu.wpi.cs3733.d22.teamV.servicerequests.ServiceRequest;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MapDashboardController extends Controller {

  private @FXML TreeTableView<Object> equipmentTable = new TreeTableView<>();
  private @FXML TreeTableColumn<Equipment, Integer> equipmentIDCol = new TreeTableColumn<>();
  private @FXML TreeTableColumn<Equipment, String> isDirtyCol = new TreeTableColumn<>();
  private @FXML TreeTableView<Object> serviceRequestTable = new TreeTableView<>();
  private @FXML TreeTableColumn<ServiceRequest, String> typeCol = new TreeTableColumn<>();
  private @FXML TreeTableColumn<ServiceRequest, String> locationCol = new TreeTableColumn<>();
  private @FXML TreeTableColumn<ServiceRequest, String> startTimeCol = new TreeTableColumn<>();
  private @FXML TreeTableView<Object> patientTable = new TreeTableView<>();
  private @FXML TreeTableColumn<Patient, Integer> patientIDCol = new TreeTableColumn<>();
  private @FXML TreeTableColumn<Patient, String> lastCol = new TreeTableColumn<>();
  private @FXML TreeTableColumn<Patient, String> SRCol = new TreeTableColumn<>();
  private @FXML TextArea countsArea = new TextArea();
  private @FXML TextArea alertArea = new TextArea();
  private @FXML VBox rightVBox;
  private @FXML ImageView mapButton;
  private @FXML ImageView imageView;
  private @FXML ArrayList<String> alertTable;
  private @FXML Label floorLabel;

  private @FXML Button ll2;
  private @FXML Button ll1;
  private @FXML Button f1;
  private @FXML Button f2;
  private @FXML Button f3;
  private @FXML Button f4;
  private @FXML Button f5;

  private @FXML TitledPane sideViewTPane;
  private @FXML TitledPane infoTPane;
  @FXML TitledPane countsTPane;
  private @FXML TitledPane mapTPane;
  private @FXML TitledPane alertsTPane;

  private @FXML BarChart bedBarChart;

  private @FXML AnchorPane dashboardPane;
  private @FXML VBox dashboardVBox;

  private Parent root;
  FXMLLoader loader = new FXMLLoader();
  FXMLLoader loader2 = new FXMLLoader();

  public void goHome(javafx.scene.input.MouseEvent event) throws IOException {
    root =
        FXMLLoader.load(
            Objects.requireNonNull(getClass().getClassLoader().getResource("FXML/home.fxml")));
    loader2.setLocation(getClass().getClassLoader().getResource("FXML/home.fxml"));
    try {
      root = loader2.load();
    } catch (IOException e) {
      e.printStackTrace();
    }

    HomeController controller = loader2.getController();
    controller.init();

    PopupController.getController().closePopUp();
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  public void goExit(javafx.scene.input.MouseEvent event) {
    stop();
  }

  public void openMap(MouseEvent event) throws IOException {
    root =
        FXMLLoader.load(
            Objects.requireNonNull(getClass().getClassLoader().getResource("FXML/Map.fxml")));
    loader.setLocation(getClass().getClassLoader().getResource("FXML/Map.fxml"));
    try {
      root = loader.load();
    } catch (IOException e) {
      e.printStackTrace();
    }

    MapController controller = loader.getController();
    switch (curFloor.getFloorName()) {
      case "L1":
        controller.setFloorName("L1");
        break;

      case "L2":
        controller.setFloorName("L2");
        break;

      case "1":
        controller.setFloorName("1");
        break;

      case "2":
        controller.setFloorName("2");
        break;

      case "3":
        controller.setFloorName("3");
        break;

      case "4":
        controller.setFloorName("4");
        break;

      case "5":
        controller.setFloorName("5");
        break;
    }
    controller.init();

    PopupController.getController().closePopUp();
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  public void addBedAlertToArray(boolean alert, ArrayList<String> dirtyBedsFloor) {}

  private static class SingletonHelper {
    private static final MapDashboardController controller = new MapDashboardController();
  }

  public static MapDashboardController getController() {
    return SingletonHelper.controller;
  }

  @Override
  public void start(Stage primaryStage) throws Exception {}

  private Floor curFloor = MapManager.getManager().getFloor("1");

  public class ButtonSubject {
    private Button button;
    private Floor floor;

    public ButtonSubject(Button button, Floor floor) {
      this.button = button;
      this.floor = floor;
    }

    public void setFloor(Floor floor) {
      this.floor = floor;
    }
  }

  public class DashboardListener {
    private Floor floor;
    private TitledPane tPane;

    public DashboardListener(Floor floor, TitledPane tPane) {
      this.floor = floor;
      this.tPane = tPane;
    }

    public void setFloor(Floor floor) {
      this.floor = floor;
    }
  }

  @FXML private ArrayList<ButtonSubject> buttonSubjects = new ArrayList<ButtonSubject>();

  @FXML private ArrayList<DashboardListener> listeners = new ArrayList<DashboardListener>();

  public void setUpButtonSubjects() {
    ButtonSubject ll2sub = new ButtonSubject(ll2, MapManager.getManager().getFloor("L2"));
    ButtonSubject ll1sub = new ButtonSubject(ll1, MapManager.getManager().getFloor("L1"));
    ButtonSubject f1sub = new ButtonSubject(f1, MapManager.getManager().getFloor("1"));
    ButtonSubject f2sub = new ButtonSubject(f2, MapManager.getManager().getFloor("2"));
    ButtonSubject f3sub = new ButtonSubject(f3, MapManager.getManager().getFloor("3"));
    ButtonSubject f4sub = new ButtonSubject(f4, MapManager.getManager().getFloor("4"));
    ButtonSubject f5sub = new ButtonSubject(f5, MapManager.getManager().getFloor("5"));

    buttonSubjects.add(ll2sub);
    buttonSubjects.add(ll1sub);
    buttonSubjects.add(f1sub);
    buttonSubjects.add(f2sub);
    buttonSubjects.add(f3sub);
    buttonSubjects.add(f4sub);
    buttonSubjects.add(f5sub);
  }

  public void setUpDashboardListeners() {
    DashboardListener sideViewTPaneListener =
        new DashboardListener(MapManager.getManager().getFloor("1"), sideViewTPane);
    DashboardListener infoTPaneListener =
        new DashboardListener(MapManager.getManager().getFloor("1"), infoTPane);
    DashboardListener countsTPaneListener =
        new DashboardListener(MapManager.getManager().getFloor("1"), countsTPane);
    DashboardListener mapTPaneListener =
        new DashboardListener(MapManager.getManager().getFloor("1"), mapTPane);
    DashboardListener alertsTPaneListener =
        new DashboardListener(MapManager.getManager().getFloor("1"), alertsTPane);
    listeners.add(sideViewTPaneListener);
    listeners.add(infoTPaneListener);
    listeners.add(countsTPaneListener);
    listeners.add(mapTPaneListener);
    listeners.add(alertsTPaneListener);
  }

  /** Switches to lower level 2 and updates all information */
  @FXML
  public void switchToLL2() {
    curFloor = MapManager.getManager().getFloor("L2");
    updateListeners(curFloor);
    updateAll();
    floorLabel.setText("LL2");
    updateMap("L2");
  }

  /** Switches to lower level 1 and updates all information */
  @FXML
  public void switchToLL1() {
    curFloor = MapManager.getManager().getFloor("L1");
    updateListeners(curFloor);
    updateAll();
    floorLabel.setText("LL1");
    updateMap("L1");
  }

  /** Switches to floor 1 and updates all information */
  @FXML
  public void switchToF1() {
    curFloor = MapManager.getManager().getFloor("1");
    updateListeners(curFloor);
    updateAll();
    floorLabel.setText("Floor 1");
    updateMap("F1");
  }

  /** Switches to floor 2 and updates all information */
  @FXML
  public void switchToF2() {
    curFloor = MapManager.getManager().getFloor("2");
    updateListeners(curFloor);
    updateAll();
    floorLabel.setText("Floor 2");
    updateMap("F2");
  }

  /** Switches to floor 3 and updates all information */
  @FXML
  public void switchToF3() {
    curFloor = MapManager.getManager().getFloor("3");
    updateListeners(curFloor);
    updateAll();
    floorLabel.setText("Floor 3");
    updateMap("F3");
  }

  /** Switches to floor 4 and updates all information */
  @FXML
  public void switchToF4() {
    curFloor = MapManager.getManager().getFloor("4");
    updateListeners(curFloor);
    updateAll();
    floorLabel.setText("Floor 4");
    updateMap("F4");
  }

  /** Switches to floor 5 and updates all information */
  @FXML
  public void switchToF5() {
    curFloor = MapManager.getManager().getFloor("5");
    updateListeners(curFloor);
    updateAll();
    floorLabel.setText("Floor 5");
    updateMap("F5");
  }

  @FXML
  public void updateListeners(Floor floor) {
    for (DashboardListener l : listeners) {
      l.setFloor(floor);
    }
  }

  /** Updates values in the Equipment table based on the current floor */
  @FXML
  private void updateEquipmentTable() {
    equipmentIDCol.setCellValueFactory(new TreeItemPropertyValueFactory("ID"));
    isDirtyCol.setCellValueFactory(new TreeItemPropertyValueFactory("isDirtyString"));

    ArrayList<Equipment> currEquipment = Vdb.requestSystem.getEquipment();
    ArrayList<TreeItem> treeItems = new ArrayList<>();

    if (!currEquipment.isEmpty()) {

      for (Equipment pos : currEquipment) {
        if (pos.getFloor().equals(curFloor.getFloorName())) {
          TreeItem<Equipment> item = new TreeItem(pos);
          treeItems.add(item);
        }
      }

      equipmentTable.setShowRoot(false);
      TreeItem root = new TreeItem(RequestSystem.getSystem().getEquipment().get(0));
      equipmentTable.setRoot(root);
      root.getChildren().addAll(treeItems);
    }
  }

  /** Updates values in the Service Request table based on the current floor */
  @FXML
  private void updateServiceRequestTable() {
    try {

      typeCol.setCellValueFactory(new TreeItemPropertyValueFactory("type"));
      locationCol.setCellValueFactory(new TreeItemPropertyValueFactory("nodeID"));
      startTimeCol.setCellValueFactory(new TreeItemPropertyValueFactory("timeMade"));
      ArrayList<ServiceRequest> currRequests =
          (ArrayList<ServiceRequest>) Vdb.requestSystem.getEveryServiceRequest();
      ArrayList<TreeItem> treeItems = new ArrayList<>();

      if (!currRequests.isEmpty()) {

        for (ServiceRequest pos : currRequests) {
          if (pos.getLocation() != null) {
            if (pos.getLocation().getFloor() != null) {
              if (pos.getLocation().getFloor().equals(curFloor.getFloorName())) {
                TreeItem<ServiceRequest> item = new TreeItem(pos);
                treeItems.add(item);
              }
            }
          }
        }

        serviceRequestTable.setShowRoot(false);
        TreeItem root = new TreeItem(RequestSystem.getSystem().getEveryServiceRequest().get(0));
        serviceRequestTable.setRoot(root);
        root.getChildren().addAll(treeItems);
      }
    } catch (NullPointerException e) {

    }
  }

  /** Updates the values in the patient table with values based on the current floor */
  @FXML
  private void updatePatientTable() {
    try {
      patientIDCol.setCellValueFactory(new TreeItemPropertyValueFactory("patientID"));
      lastCol.setCellValueFactory(new TreeItemPropertyValueFactory("lastName"));
      SRCol.setCellValueFactory(new TreeItemPropertyValueFactory("serviceIDs"));
      ArrayList<Patient> currPatients = Vdb.requestSystem.getPatients();
      ArrayList<TreeItem> treeItems = new ArrayList<>();

      if (!currPatients.isEmpty()) {

        for (Patient pos : currPatients) {
          for (ServiceRequest s : RequestSystem.getSystem().getEveryServiceRequest()) {
            for (int i : pos.getServiceIDs()) {
              if (i == s.getServiceID()) {
                if (s.getLocation() != null) {
                  if (s.getLocation().getFloor() != null) {
                    if (s.getLocation().getFloor().equals(curFloor.getFloorName())) {
                      TreeItem<Patient> item = new TreeItem(pos);
                      treeItems.add(item);
                    }
                  }
                }
              }
            }
          }
        }

        patientTable.setShowRoot(false);
        TreeItem root = new TreeItem(RequestSystem.getSystem().getPatients().get(0));
        patientTable.setRoot(root);
        root.getChildren().addAll(treeItems);
      }
    } catch (NullPointerException e) {

    }
  }

  /** Updates text field with correct equipment counts */
  @FXML
  public void updateCounts() {
    curFloor = MapManager.getManager().getFloor(curFloor.getFloorName());
    int srCount = 0;
    int cleanBeds = 0;
    int dirtyBeds = 0;
    int cleanPumps = 0;
    int dirtyPumps = 0;

    for (Equipment equipment : RequestSystem.getSystem().getEquipment()) {
      if (equipment.getFloor().equals(curFloor.getFloorName())) {
        if (equipment.getIsDirty() && equipment.getName().equals("Infusion Pump")) {
          dirtyPumps++;
        } else if (equipment.getIsDirty() && equipment.getName().equals("Bed")) {
          dirtyBeds++;
        } else if (!equipment.getIsDirty() && equipment.getName().equals("Infusion Pump")) {
          cleanPumps++;
        } else {
          cleanBeds++;
        }
      }
    }

    for (ServiceRequest request : RequestSystem.getSystem().getEveryServiceRequest()) {
      if (request.getLocation() != null) {
        if (request.getLocation().getFloor() != null) {
          if (request.getLocation().getFloor().equals(curFloor.getFloorName())) {
            srCount++;
          }
        }
      }
    }
    countsArea.setText(
        "Active Service Requests: "
            + srCount
            + "\n"
            + "Clean Beds: "
            + cleanBeds
            + "\n"
            + "Dirty Beds: "
            + dirtyBeds
            + "\n"
            + "Clean Pumps: "
            + cleanPumps
            + "\n"
            + "Dirty Pumps: "
            + dirtyPumps);
    if (bedBarChart != null) bedBarChart.getData().clear();
    updateBarChart(new int[] {cleanBeds, dirtyBeds}, new int[] {cleanPumps, dirtyPumps});
  }

  /* was used to check for beds that are dirty, could be used again if main function has to be changed in the future.
  public void checkAlertSixBeds(String m1, boolean d1, String m2, boolean d2) {
    if (m1.equals("bed") && d1 == true && m2.equals("Bed") && d2 == true) {
      return 1;
    } else {
      return 0;
    }
    return 0;
  } */

  /* was used to add dirty beds to alertsArea, could be used again if main function has to be changed in the future.
  @FXML
  public void addBedAlertToArray(int b) {

    // adds strings from alerTable to alertsArea
    if (b > 5) {
      alertsArea.setText("There are 6+ dirty beds");
    }
  }
  /
   */

  // gets alert string from alertSixBeds on EquipmentIcon class
  String tempBedAlertString = "";

  public void addNewBedAlert(String newAlertString) {
    tempBedAlertString = newAlertString;
  }

  @FXML
  private void updateAlerts() {
    ArrayList<String> alerts = new ArrayList<>();
    alerts.add(tempBedAlertString);
    ArrayList<EquipmentIcon> pumpList = curFloor.getPumpAlertIcons();
    ArrayList<EquipmentIcon> bedList = curFloor.getBedAlertIcons();

    int cleanPumps;
    int dirtyPumps;

    String alertText = "";

    for (EquipmentIcon e : pumpList) {
      cleanPumps = e.getCleanPumps();
      dirtyPumps = e.getDirtyPumps();
      if ((cleanPumps < 5)) {
        if (cleanPumps == 0) {
          alerts.add(
              "ALERT there are no clean pumps at location " + e.getXCoord() + ", " + e.getYCoord());
        } else {
          alerts.add(
              "ALERT there are only "
                  + cleanPumps
                  + " clean pumps at location "
                  + e.getXCoord()
                  + ", "
                  + e.getYCoord());
        }
      }
      if (dirtyPumps > 9) {
        alerts.add(
            "ALERT! there are "
                + dirtyPumps
                + " dirty pumps at location "
                + e.getXCoord()
                + ", "
                + e.getYCoord());
        e.createRequests();
      }
    }

    for (String a : alerts) {
      alertText = alertText + a + "\n";
    }
    System.out.println(alertText);
    alertArea.setText(alertText);
  }

  /** Updates all information on the map dashboard based on the current floor. */
  @FXML
  private void updateAll() {
    updateEquipmentTable();
    updatePatientTable();
    updateServiceRequestTable();
    updateCounts();
    updateAlerts();
  }

  @Override
  public void init() {
    setUpButtonSubjects();
    setUpDashboardListeners();
    updateAll();

    dashboardPane
        .widthProperty()
        .addListener(
            new ChangeListener<Number>() {
              @Override
              public void changed(
                  ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double w = dashboardPane.getWidth();

                dashboardVBox.setLayoutX(w / 2 - 585);
              }
            });
  }

  @FXML
  private void updateMap(String floor) {
    Image m = null;
    URL _url;
    switch (floor) {
      case "L2":
        _url = this.getClass().getResource("/Lower Level 2.png");

        try {
          m = new Image(_url.toExternalForm());

        } catch (NullPointerException e) {
          e.printStackTrace();
        }

        mapButton.setImage(m);
        break;

      case "L1":
        _url = this.getClass().getResource("/Lower Level 1.png");

        try {
          m = new Image(_url.toExternalForm());

        } catch (NullPointerException e) {
          e.printStackTrace();
        }
        mapButton.setImage(m);
        break;

      case "F1":
        _url = this.getClass().getResource("/1st Floor.png");

        try {
          m = new Image(_url.toExternalForm());

        } catch (NullPointerException e) {
          e.printStackTrace();
        }

        mapButton.setImage(m);
        break;

      case "F2":
        _url = this.getClass().getResource("/2nd Floor.png");

        try {
          m = new Image(_url.toExternalForm());

        } catch (NullPointerException e) {
          e.printStackTrace();
        }

        mapButton.setImage(m);
        break;

      case "F3":
        _url = this.getClass().getResource("/3rd Floor.png");

        try {
          m = new Image(_url.toExternalForm());

        } catch (NullPointerException e) {
          e.printStackTrace();
        }

        mapButton.setImage(m);
        break;

      case "F4":
        _url = this.getClass().getResource("/4th Floor.png");

        try {
          m = new Image(_url.toExternalForm());

        } catch (NullPointerException e) {
          e.printStackTrace();
        }

        mapButton.setImage(m);
        break;

      case "F5":
        _url = this.getClass().getResource("/5th Floor.png");

        try {
          m = new Image(_url.toExternalForm());

        } catch (NullPointerException e) {
          e.printStackTrace();
        }

        mapButton.setImage(m);
        break;
    }
  }

  /** series for clean and dirty pumps and beds */
  @FXML XYChart.Series equipment = new XYChart.Series();

  /** set up dashboard bar chart. Used in init() */

  /** updates bar chart on floor switch / equipment change */
  @FXML
  public void updateBarChart(int[] beds, int pumps[]) {
    equipment.getData().clear();
    equipment = new XYChart.Series<>();
    updateBeds(beds);
    updatePumps(pumps);
    if (bedBarChart != null) bedBarChart.getData().add(equipment);
  }

  /** Updates bed counts for bar chart */
  @FXML
  public void updateBeds(int[] beds) {
    int cleanBeds = beds[0];
    int dirtyBeds = beds[1];
    equipment.getData().add(new XYChart.Data("Clean \n" + "Beds", cleanBeds));
    equipment.getData().add(new XYChart.Data("Dirty \n" + "Beds", dirtyBeds));
  }

  /** Updates pump count for bar chart */
  @FXML
  public void updatePumps(int[] pumps) {
    int cleanPumps = pumps[0];
    int dirtyPumps = pumps[1];
    equipment.getData().add(new XYChart.Data("Clean \n" + "Pumps", cleanPumps));
    equipment.getData().add(new XYChart.Data("Dirty \n" + "Pumps", dirtyPumps));

    /*
    XYChart.Series c = new XYChart.Series();
    c.setName("Pumps");
    int dirt = 0;
    int clean = 0;
    for (int i = 0; i < curFloor.getEquipmentIcons().size(); i++) {
      for (int j = 0; j < curFloor.getEquipmentIcons().get(i).getEquipmentList().size(); j++) {
        if (curFloor
            .getEquipmentIcons()
            .get(i)
            .getEquipmentList()
            .get(j)
            .getName()
            .equalsIgnoreCase("infusion pump")) {
          if (curFloor.getEquipmentIcons().get(i).getEquipmentList().get(j).getIsDirty()) {
            dirt++;
          } else {
            clean++;
          }
        }
      }

     */
  }
}
