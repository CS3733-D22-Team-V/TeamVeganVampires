package edu.wpi.cs3733.d22.teamV.controllers;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.d22.teamV.dao.PathfindingDao;
import edu.wpi.cs3733.d22.teamV.main.RequestSystem;
import edu.wpi.cs3733.d22.teamV.map.*;
import edu.wpi.cs3733.d22.teamV.objects.Location;
import java.util.LinkedList;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.controlsfx.control.CheckComboBox;

@Setter
@Getter
public class MapController extends Controller {
  @FXML protected VBox mapVBox = new VBox(15);
  @FXML protected Button refreshButton = new Button("Refresh");
  @FXML protected CheckComboBox<String> filterCheckBox = new CheckComboBox<>();
  @FXML protected VBox controlsVBox = new VBox(15);
  @FXML protected VBox legendBox = new VBox();
  @FXML protected Pane mapPane = new Pane();
  protected final DoubleProperty deltaY = new SimpleDoubleProperty(0.0d);
  protected final Group group = new Group();
  @FXML protected ImageView mapImage = new ImageView(new Image("1st Floor.png"));
  @FXML protected StackPane stackPane = new StackPane();
  protected ZoomPane zoomPane = null;
  @FXML protected ScrollPane scrollPane = new ScrollPane(stackPane);
  protected final DoubleProperty zoomProperty = new SimpleDoubleProperty(200);
  @FXML JFXButton LL2 = new JFXButton("LL2");
  @FXML JFXButton LL1 = new JFXButton("LL1");
  @FXML JFXButton floor1 = new JFXButton("Floor 1");
  @FXML JFXButton floor2 = new JFXButton("Floor 2");
  @FXML JFXButton floor3 = new JFXButton("Floor 3");
  @FXML JFXButton floor4 = new JFXButton("Floor 4");
  @FXML JFXButton floor5 = new JFXButton("Floor 5");
  @FXML Button submitButton = new Button("Submit");
  private String floorName = "1";
  private String startLocationID = "";
  private String endLocationID = "";
  private Button showConnections = new Button("Show all connections");

  @FXML
  private Label pathfinderInstructions =
      new Label(
          "Hold down shift and double-click 2 icons to show a path between them.\n\nTo create a path, hold down alt and double-click the 2 icons you want to connect.");

  @FXML private Label startLocationLabel = new Label("Starting Location: " + startLocationID);
  @FXML private Label endLocationLabel = new Label("End Location: " + endLocationID);

  @FXML
  private VBox pathfinderVBox =
      new VBox(15, pathfinderInstructions, startLocationLabel, endLocationLabel);

  @FXML
  private Label missingPath =
      new Label(
          "Sorry, a path between those locations does not exist. Would you like to create one?");

  @FXML TitledPane pathfinderInfo = new TitledPane("Pathfinder", pathfinderVBox);

  ObservableList<String> requestTypes =
      FXCollections.observableArrayList(
          "Lab Request",
          "Equipment Delivery Request",
          "Meal Delivery Request",
          "Laundry Request",
          "Medicine Delivery Request",
          "Religious Request",
          "Sanitation Request",
          "Internal Patient Transport Request",
          "Robot Request",
          "Computer Request");

  @FXML
  ObservableList<String> filterItems =
      FXCollections.observableArrayList(
          "Locations",
          "Department",
          "Hallway",
          "Service",
          "Elevator",
          "Stairway",
          "Bathroom",
          "Labs",
          "Equipment",
          "Clean Equipment",
          "Service Requests",
          "Active Requests",
          "Lab Requests",
          "Equipment Delivery Requests",
          "Meal Delivery Requests",
          "Laundry Requests",
          "Medicine Delivery Requests",
          "Religious Requests",
          "Sanitation Requests",
          "Internal Patient Transport Requests",
          "Robot Requests",
          "Computer Requests");

  private static MapController controller;

  public static MapController getController() {
    return controller;
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    controller = this;
    init();
  }

  @Override
  public void init() {
    controller = this;
    setFloor(floorName);
    mapSetUp();
  }

  /** Allows users to zoom in and out of the map without */
  protected void mapSetUp() {
    setUpControls();
    zoom();
    pathfinderInfo.setExpanded(false);
    missingPath.setWrapText(true);
    pathfinderInstructions.setWrapText(true);
    filterCheckBox.setMaxWidth(controlsVBox.getWidth() / 3);
    scrollPane.setPrefSize(550, 550);
    controlsVBox
        .getChildren()
        .addAll(pathfinderInfo, filterCheckBox, refreshButton, showConnections);
    showConnections.setOnAction(event -> drawAllConnections());
    mapVBox.getChildren().addAll(scrollPane);
    mapVBox.setAlignment(Pos.CENTER);
    mapVBox.setSpacing(15);
    controlsVBox.setAlignment(Pos.CENTER);
    checkFilter();
  }

  /** Allows users to zoom in and out of the map without */
  @FXML
  void zoom() {
    zoomPane = new ZoomPane();
    zoomPane.getChildren().clear();
    group.getChildren().clear();
    stackPane.getChildren().clear();
    stackPane.getChildren().add(mapImage);
    stackPane.getChildren().add(mapPane);

    scrollPane.setPrefSize(550, 550);
    mapImage.setFitHeight(scrollPane.getPrefHeight() + 50);
    mapImage.setFitWidth(scrollPane.getPrefHeight() + 50);
    mapPane.setPrefSize(mapImage.getFitWidth(), mapImage.getFitHeight());
    // mapPane.autosize();
    mapImage.setPreserveRatio(true);
    scrollPane.setPannable(true);
    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    group.getChildren().add(mapImage);
    group.getChildren().add(mapPane);

    group.getChildren().add(stackPane);
    zoomProperty.bind(zoomPane.scale);
    deltaY.bind(zoomPane.deltaY);
    zoomPane.getChildren().add(group);

    MapEvent mapEvent = new MapEvent(zoomPane);

    scrollPane.setContent(zoomPane);
    zoomPane.toBack();
    scrollPane.addEventFilter(ScrollEvent.ANY, mapEvent.getOnZoomEventHandler());
  }

  /** Sets up the buttons, filter, and the mapPane */
  void setUpControls() {
    System.out.println("setting up controls");
    LL2.setOnAction(event -> setFloor("L2"));
    LL1.setOnAction(event -> setFloor("L1"));
    floor1.setOnAction(event -> setFloor("1"));
    floor2.setOnAction(event -> setFloor("2"));
    floor3.setOnAction(event -> setFloor("3"));
    floor4.setOnAction(event -> setFloor("4"));
    floor5.setOnAction(event -> setFloor("5"));
    refreshButton.setOnAction(event -> refreshMap());
    filterCheckBox = new CheckComboBox<>();
    filterCheckBox.setTitle("Filter Items");
    filterCheckBox.getItems().addAll(filterItems);
    filterCheckBox
        .focusedProperty()
        .addListener(
            (o, ov, nv) -> {
              if (nv) filterCheckBox.show();
              else filterCheckBox.hide();
            });
    filterCheckBox
        .getCheckModel()
        .getCheckedItems()
        .addListener((ListChangeListener<String>) change -> checkFilter());
    mapPane.setOnMouseClicked(
        event -> {
          if (event.getClickCount() == 2) {
            if (!event
                .getTarget()
                .getClass()
                .getTypeName()
                .equals("javafx.scene.image.ImageView")) {

              PopupController.getController().iconWindow(event);
            }
          }
        });
  }
  /*
  private Button showNodes = new Button("Toggle show nodes");
  private boolean showingNodes = false;

  private void setUpNodeControls() {
    mapPane.setOnMouseClicked(
        event -> {
          if (event.getClickCount() == 2) {
            if (!event
                .getTarget()
                .getClass()
                .getTypeName()
                .equals("javafx.scene.image.ImageView")) {
              if (event.isAltDown()) {
                // Adds a node
                RequestSystem.getSystem()
                    .addLocation(
                        new Location(
                            RequestSystem.getSystem().getNewNode(),
                            event.getX() + 7.5,
                            event.getY() + 7.5,
                            floorName,
                            "Tower",
                            "node",
                            "node",
                            "node"));
                MapManager.getManager().setUpFloors();
                mapPane.getChildren().clear();
                setFloor(floorName);
              } else {
                PopupController.getController().iconWindow(event);
              }
            }
          }
        });
    showNodes.setOnAction(
        event -> {
          showingNodes = !showingNodes;
          for (LocationIcon icon : MapManager.getManager().getFloor(floorName).getLocationIcons()) {
            if (icon.getLocation().getNodeType().equals("node")) {
              if (showingNodes) {
                icon.getImage().setOpacity(100);
              } else {
                icon.getImage().setOpacity(0);
              }
            }
          }
          refreshMap();
        });
  }*/

  /** Reloads Map */
  @FXML
  public void refreshMap() {
    setFloor(floorName);
    resetPathFinder();
    controlsVBox
        .getChildren()
        .retainAll(pathfinderInfo, filterCheckBox, refreshButton, showConnections, legendBox);
  }

  /** Reset Pathfinder */
  @FXML
  private void resetPathFinder() {
    startLocationID = "";
    endLocationID = "";
    pathfinderVBox
        .getChildren()
        .retainAll(pathfinderInstructions, startLocationLabel, endLocationLabel, legendBox);
    startLocationLabel.setText("Start Location: ");
    endLocationLabel.setText("End Location: ");
  }

  /** Filters out icons based on filterCheckBox */
  @FXML
  public void checkFilter() {
    if (filterCheckBox.getCheckModel().getCheckedItems().contains("Active Requests")) {
      if (!filterCheckBox.getCheckModel().isChecked("Service Requests")) {
        filterCheckBox.getCheckModel().check("Service Requests");
      }
    }
    if (filterCheckBox.getCheckModel().getCheckedItems().contains("Clean Equipment")) {
      if (!filterCheckBox.getCheckModel().isChecked("Equipment")) {
        filterCheckBox.getCheckModel().check("Equipment");
      }
    }
    PopupController.getController().closePopUp();
    populateFloorIconArr();
  }

  /** Sets the mapImage to the corresponding floor dropdown and returns the floor string */
  public void setFloor(String floor) {
    floorName = floor;
    mapImage.setImage(MapManager.getManager().getFloor(floorName).getMap());
    populateFloorIconArr();
  }

  /** Loads the floor's icons in accordance with filter */
  @FXML
  public void populateFloorIconArr() {
    System.out.println("populating icons");
    mapPane.getChildren().clear();
    ObservableList<String> filter = filterCheckBox.getCheckModel().getCheckedItems();
    if (filter.size() > 0) {
      filterRequestsAndLocations();
      filterEquipment();
    } else {
      for (Icon icon : MapManager.getManager().getFloor(floorName).getIconList()) {
        if (!mapPane.getChildren().contains(icon.getImage())) {
          mapPane.getChildren().add(icon.getImage());
        }
      }
    }
  }
  /** Filter by Equipment */
  private void filterEquipment() {
    ObservableList<String> filter = filterCheckBox.getCheckModel().getCheckedItems();
    if (filter.contains("Equipment")) { // Filter Equipment
      for (EquipmentIcon icon : MapManager.getManager().getFloor(floorName).getEquipmentIcons()) {
        if (filter.contains("Clean Equipment")) {
          if (icon.hasCleanEquipment()) {
            mapPane.getChildren().add(icon.getImage());
          }
        } else {
          mapPane.getChildren().add(icon.getImage());
        }
      }
    }
  }

  /** Filters by service requests and locations */
  private void filterRequestsAndLocations() {
    ObservableList<String> filter = filterCheckBox.getCheckModel().getCheckedItems();
    if (filter.contains("Service Requests")
        || filter.contains("Locations")
        || filterContainsRequests()) {
      for (LocationIcon icon : MapManager.getManager().getFloor(floorName).getLocationIcons()) {
        if ((filter.contains("Service Requests") || filterContainsRequests())
            && icon.getRequestsArr().size() > 0) { // Filter Service Request
          if (filter.contains("Active Requests") && icon.hasActiveRequests()) {
            filterByActiveRequestType(icon);
          } else {
            filterByRequestType(icon);
          }
        }
        if (filter.contains("Locations")) { // Filter Location
          filterByLocation(icon);
        }
      }
    }
  }

  /**
   * Puts location icons on the mapPane if the filter contains a request type that the location has
   */
  public boolean filterContainsRequests() {
    ObservableList<String> filter = filterCheckBox.getCheckModel().getCheckedItems();
    if (filter.contains("Lab Requests")
        || filter.contains("Equipment Delivery Requests")
        || filter.contains("Meal Delivery Requests")
        || filter.contains("Laundry Requests")
        || filter.contains("Medicine Delivery Requests")
        || filter.contains("Religious Requests")
        || filter.contains("Sanitation Requests")
        || filter.contains("Internal Patient Transport Requests")
        || filter.contains("Robot Requests")
        || filter.contains("Computer Requests")) {
      if (!filter.contains("Service Requests")) {
        filterCheckBox.getCheckModel().check("Service Requests");
      }
      return true;
    } else {
      return false;
    }
  }

  /** Filters by active request and by request type */
  public void filterByActiveRequestType(LocationIcon icon) {
    if (!filterContainsRequests() && icon.hasActiveRequests()) {
      if (!mapPane.getChildren().contains(icon.getImage())) {
        mapPane.getChildren().add(icon.getImage());
      }
    } else {
      for (String requestType : requestTypes) {
        filterRequests(requestType, icon);
      }
    }
  }

  /** Filter by request type */
  public void filterByRequestType(LocationIcon icon) {
    if (!filterContainsRequests()) {
      if (!mapPane.getChildren().contains(icon.getImage())) {
        mapPane.getChildren().add(icon.getImage());
      }
    } else {
      for (String requestType : requestTypes) {
        filterRequests(requestType, icon);
      }
    }
  }

  /**
   * Puts location icons on the map depending on the type of service request they have and their
   * active status
   */
  private void filterRequests(String requestType, LocationIcon icon) {
    ObservableList<String> filter = filterCheckBox.getCheckModel().getCheckedItems();
    if (filter.contains("Active Requests")) {
      if (filter.contains(requestType + "s") && icon.hasActiveRequestType(requestType)) {
        if (!mapPane.getChildren().contains(icon.getImage())) {
          mapPane.getChildren().addAll(icon.getImage());
        }
      }
    } else {
      if (filter.contains(requestType + "s") && icon.hasRequestType(requestType)) {
        if (!mapPane.getChildren().contains(icon.getImage())) {
          mapPane.getChildren().addAll(icon.getImage());
        }
      }
    }
  }

  /** Filter by location type */
  public void filterByLocation(LocationIcon icon) {
    ObservableList<String> filter = filterCheckBox.getCheckModel().getCheckedItems();
    if (!filter.contains("Department")
        && !filter.contains("Hallway")
        && !filter.contains("Service")
        && !filter.contains("Elevator")
        && !filter.contains("Stairway")
        && !filter.contains("Bathroom")
        && !filter.contains("Labs")) {
      if (!mapPane.getChildren().contains(icon.getImage())) {
        mapPane.getChildren().add(icon.getImage());
      }
    } else if ((filter.contains("Department") && icon.getLocation().getNodeType().equals("DEPT"))
        || (filter.contains("Hallway") && icon.getLocation().getNodeType().equals("HALL"))
        || (filter.contains("Service") && icon.getLocation().getNodeType().equals("SERV"))
        || (filter.contains("Elevator") && icon.getLocation().getNodeType().equals("ELEV"))
        || (filter.contains("Stairway") && icon.getLocation().getNodeType().equals("STAI"))
        || (filter.contains("Bathroom")
            && (icon.getLocation().getNodeType().equals("BATH")
                || icon.getLocation().getNodeType().equals("REST")))
        || (filter.contains("Labs") && icon.getLocation().getNodeType().equals("LABS"))) {
      if (!mapPane.getChildren().contains(icon.getImage())) {
        mapPane.getChildren().add(icon.getImage());
      }
    }
  }

  /** Draws a path from one location to another */
  @FXML
  public void drawPath(LocationIcon icon, LocationIcon icon1) {
    double x1 = icon.getImage().getTranslateX() + 7.5;
    double x2 = icon1.getImage().getTranslateX() + 7.5;
    double y1 = icon.getImage().getTranslateY() + 7.5;
    double y2 = icon1.getImage().getTranslateY() + 7.5;
    Line path = new Line(x1, y1, x2, y2);
    path.setStrokeWidth(3);
    path.setStroke(Color.RED);
    path.setStrokeDashOffset(20d);
    path.setFill(Color.RED);
    mapPane.getChildren().add(0, path);
  }

  /** Draws a path between icons you click on */
  public void drawPath() {
    System.out.println(startLocationID);
    System.out.println(endLocationID);
    if (!startLocationID.isEmpty() && !endLocationID.isEmpty()) {
      System.out.println("Start: " + startLocationID);
      System.out.println("End: " + endLocationID);
      LinkedList<Location> locations =
          RequestSystem.getSystem().getPaths(startLocationID, endLocationID);
      if (locations.size() > 1) {
        Location currLocation = null;
        Location prevLoc;
        for (Location location : locations) {
          Location temp = currLocation;
          currLocation = location;
          prevLoc = temp;
          System.out.println(location.getNodeID());
          if (prevLoc != null) {
            drawPath(currLocation.getIcon(), prevLoc.getIcon());
          }
        }
      }
    }
    startLocationID = "";
    endLocationID = "";
  }

  /** Makes a path between icons you click on. */
  public void makePath() {
    if (!startLocationID.equals("") && !endLocationID.equals("")) {
      RequestSystem.getSystem().makePaths(startLocationID, endLocationID);
      drawPath();
      startLocationID = "";
      endLocationID = "";
    }
  }

  /** Draws all the paths on the floor */
  @FXML
  private void drawAllConnections() {
    for (PathfindingDao.Edge edge : PathfindingDao.getEdges()) {
      if (RequestSystem.getSystem().getLocation(edge.getNodeOne()).getFloor().equals(floorName)) {
        drawPath(
            RequestSystem.getSystem().getLocation(edge.getNodeOne()).getIcon(),
            RequestSystem.getSystem().getLocation(edge.getNodeTwo()).getIcon());
      }
    }
  }

  /**
   * Sets the startLocationID and endLocationID with the given nodeID Calls makePaths() if the Alt
   * key is down DrawPaths() if the Shift key is down RemoveLink() if the Control key is down
   */
  public void setPathfinderID(MouseEvent event, String nodeID) {
    if (startLocationID.isEmpty()) {
      startLocationID = nodeID;
      endLocationID = "";
    } else {
      if (!endLocationID.isEmpty()) {
        startLocationID = endLocationID;
      }
      endLocationID = nodeID;
      // Call relevant functions
      if (event.isShiftDown() && !event.isAltDown() && !event.isControlDown()) {
        MapController.getController().drawPath();
      } else if (event.isAltDown() && !event.isShiftDown() && !event.isControlDown()) {
        MapController.getController().makePath();
      } else if (event.isControlDown() && !event.isShiftDown() && !event.isAltDown()) {
        removeLink(nodeID);
      }
    }
    startLocationLabel.setText("Starting Location: " + startLocationID);
    endLocationLabel.setText("End Location: " + endLocationID);
  }

  /** Removes a link between the given nodeID and either the startLocationID or the endLocationID */
  private void removeLink(String nodeID) {
    if (!startLocationID.equals(nodeID)) {
      RequestSystem.getSystem().getPathfinderDao().removeLink(nodeID, startLocationID);
    } else {
      RequestSystem.getSystem().getPathfinderDao().removeLink(nodeID, endLocationID);
    }
    refreshMap();
  }
}
