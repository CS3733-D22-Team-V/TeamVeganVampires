package edu.wpi.cs3733.d22.teamV.controllers;

import com.jfoenix.controls.JFXComboBox;
import edu.wpi.cs3733.d22.teamV.main.RequestSystem;
import edu.wpi.cs3733.d22.teamV.main.Vdb;
import edu.wpi.cs3733.d22.teamV.manager.MapManager;
import edu.wpi.cs3733.d22.teamV.map.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.controlsfx.control.CheckComboBox;

@Setter
@Getter
public class MapController extends Controller {
  protected Floor currFloor;
  RequestSystem requestSystem = new RequestSystem();
  boolean drag = false;

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
          "Internal Patient Transport Requests");

  @FXML protected VBox mapVBox = new VBox(15);
  @FXML protected CheckComboBox<String> filterCheckBox = new CheckComboBox<>();
  @FXML protected HBox mapHBox = new HBox(15);
  @FXML protected Pane mapPane = new Pane();
  protected final DoubleProperty deltaY = new SimpleDoubleProperty(0.0d);
  protected final Group group = new Group();
  @FXML protected ImageView mapImage = new ImageView(new Image("1st Floor.png"));
  @FXML protected StackPane stackPane = new StackPane();
  protected ZoomPane zoomPane = null;
  @FXML protected ScrollPane scrollPane = new ScrollPane(stackPane);
  protected final DoubleProperty zoomProperty = new SimpleDoubleProperty(200);

  @FXML
  protected JFXComboBox floorDropDown =
      new JFXComboBox<>(
          FXCollections.observableArrayList(
              "Lower Level 2",
              "Lower Level 1",
              "1st Floor",
              "2nd Floor",
              "3rd Floor",
              "4th Floor",
              "5th Floor"));

  @Override
  public void start(Stage primaryStage) throws Exception {
    init();
  }

  private static class SingletonHelper {
    private static final MapController controller = new MapController();
  }

  public static MapController getController() {
    return MapController.SingletonHelper.controller;
  }

  /** Allows users to zoom in and out of the map without */
  @FXML
  void zoom() {
    stackPane.getChildren().add(mapImage);
    stackPane.getChildren().add(mapPane);
    mapImage.setPreserveRatio(true);
    scrollPane.setPannable(true);
    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    group.getChildren().add(mapImage);
    group.getChildren().add(stackPane);

    zoomPane = new ZoomPane();
    zoomProperty.bind(zoomPane.scale);
    deltaY.bind(zoomPane.deltaY);
    zoomPane.getChildren().add(group);

    MapEvent mapEvent = new MapEvent(zoomPane);

    scrollPane.setContent(zoomPane);
    zoomPane.toBack();
    mapPane.setOnMouseClicked(
        event -> {
          if (event.getClickCount() == 2) {
            openIconFormWindow(event);
          }
        });
    scrollPane.addEventFilter(ScrollEvent.ANY, mapEvent.getOnZoomEventHandler());
  }

  void setUpControls() {
    floorDropDown.setValue("1st Floor");
    floorDropDown.setOnAction(
        event -> {
          checkDropDown();
        });
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
        .addListener(
            (ListChangeListener<String>)
                change -> {
                  checkDropDown();
                });
  }

  @Override
  public void init() {
    mapSetUp();
  }

  protected void mapSetUp() {
    setUpControls();
    zoom();
    currFloor = MapManager.getManager().getFloor("1");
    mapVBox.setFillWidth(true);

    scrollPane.setMaxSize(470, 470);
    mapHBox.getChildren().addAll(floorDropDown, filterCheckBox);
    mapVBox.getChildren().addAll(scrollPane, mapHBox);
    mapVBox.setAlignment(Pos.TOP_CENTER);
    mapVBox.setSpacing(15);
    mapHBox.setAlignment(Pos.CENTER);
    checkDropDown();
  }

  /** Checks the value of the floor drop down and matches it with the corresponding map png */
  @FXML
  void checkDropDown() {
    ObservableList<String> filter = filterCheckBox.getCheckModel().getCheckedItems();
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
    String url = floorDropDown.getValue().toString() + ".png";
    mapImage.setImage(new Image(url));
    mapImage.setFitWidth(600);
    mapImage.setFitHeight(600);
    getFloor();
  }

  // Sets the mapImage to the corresponding floor dropdown and returns the floor string
  public String getFloor() {
    String result = "";
    switch (floorDropDown.getValue().toString()) {
      case "Lower Level 1":
        currFloor = Vdb.mapManager.getFloor("L1");
        result = "L1";
        break;
      case "Lower Level 2":
        currFloor = Vdb.mapManager.getFloor("L2");
        result = "L2";
        break;
      case "1st Floor":
        currFloor = Vdb.mapManager.getFloor("1");
        result = "1";
        break;
      case "2nd Floor":
        currFloor = Vdb.mapManager.getFloor("2");
        result = "2";
        break;
      case "3rd Floor":
        currFloor = Vdb.mapManager.getFloor("3");
        result = "3";
        System.out.println("3");
        break;
      case "4th Floor":
        currFloor = Vdb.mapManager.getFloor("4");
        result = "2";
        break;
      case "5th Floor":
        currFloor = Vdb.mapManager.getFloor("5");
        result = "3";
        break;
    }

    populateFloorIconArr();
    return result;
  }

  // Loads the floor's icons in accordance with filter
  @FXML
  public void populateFloorIconArr() {
    mapPane.getChildren().clear();
    ObservableList<String> filter = filterCheckBox.getCheckModel().getCheckedItems();
    for (Icon icon : currFloor.getIconList()) {
      if (filter.size() > 0) {
        // System.out.println(icon.iconType);
        if (filter.contains("Service Requests") && icon.iconType.equals("Request")) {
          RequestIcon requestIcon = (RequestIcon) icon;
          if (filter.contains("Active Requests")) {
            if (requestIcon.hasActiveRequests()) {
              filterByActiveRequestType(requestIcon);
            }
          } else {
            filterByRequestType(requestIcon);
          }
        }
        if (filter.contains("Equipment") && icon.iconType.equals("Equipment")) {
          EquipmentIcon equipmentIcon = (EquipmentIcon) icon;
          if (filter.contains("Clean Equipment")) {
            if (equipmentIcon.hasCleanEquipment()) {
              mapPane.getChildren().add(icon.getImage());
            }
          } else {
            mapPane.getChildren().add(icon.getImage());
          }
        }
        if (filter.contains("Locations") && icon.iconType.equals("Location")) {
          filterByLocation((LocationIcon) icon);
        }
      } else {
        if (!mapPane.getChildren().contains(icon.getImage())) {
          mapPane.getChildren().add(icon.getImage());
        }
      }
    }
  }

  public void filterByActiveRequestType(RequestIcon icon) {
    ObservableList<String> filter = filterCheckBox.getCheckModel().getCheckedItems();
    if (!filter.contains("Lab Requests")
        && !filter.contains("Equipment Delivery Requests")
        && !filter.contains("Meal Delivery Requests")
        && !filter.contains("Laundry Requests")
        && !filter.contains("Medicine Delivery Requests")
        && !filter.contains("Religious Requests")
        && !filter.contains("Sanitation Requests")
        && !filter.contains("Internal Patient Transport Requests")) {
      mapPane.getChildren().add(icon.getImage());
    } else if ((filter.contains("Lab Requests") && icon.hasActiveRequestType("Lab Request"))
        || (filter.contains("Equipment Delivery Requests")
            && icon.hasActiveRequestType("Equipment Delivery Request"))
        || (filter.contains("Meal Delivery Requests")
            && icon.hasActiveRequestType("Meal Delivery Request"))
        || (filter.contains("Laundry Requests") && icon.hasActiveRequestType("Laundry Request"))
        || (filter.contains("Medicine Delivery Requests")
            && icon.hasActiveRequestType("Medicine Delivery Request"))
        || (filter.contains("Religious Requests") && icon.hasActiveRequestType("Religious Request"))
        || (filter.contains("Sanitation Requests")
            && icon.hasActiveRequestType("Sanitation Request"))
        || (filter.contains("Internal Patient Transport Requests")
            && icon.hasActiveRequestType("Internal Patient Transport Request"))) {
      mapPane.getChildren().add(icon.getImage());
    }
  }

  public void filterByRequestType(RequestIcon icon) {
    ObservableList<String> filter = filterCheckBox.getCheckModel().getCheckedItems();
    if (!filter.contains("Lab Requests")
        && !filter.contains("Equipment Delivery Requests")
        && !filter.contains("Meal Delivery Requests")
        && !filter.contains("Laundry Requests")
        && !filter.contains("Medicine Delivery Requests")
        && !filter.contains("Religious Requests")
        && !filter.contains("Sanitation Requests")
        && !filter.contains("Internal Patient Transport Requests")) {
      mapPane.getChildren().add(icon.getImage());
    } else if ((filter.contains("Lab Requests") && icon.hasRequestType("Lab Request"))
        || (filter.contains("Equipment Delivery Requests")
            && icon.hasRequestType("Equipment Delivery Request"))
        || (filter.contains("Meal Delivery Requests")
            && icon.hasRequestType("Meal Delivery Request"))
        || (filter.contains("Laundry Requests") && icon.hasRequestType("Laundry Request"))
        || (filter.contains("Medicine Delivery Requests")
            && icon.hasRequestType("Medicine Delivery Request"))
        || (filter.contains("Religious Requests") && icon.hasRequestType("Religious Request"))
        || (filter.contains("Sanitation Requests") && icon.hasRequestType("Sanitation Request"))
        || (filter.contains("Internal Patient Transport Requests")
            && icon.hasRequestType("Internal Patient Transport Request"))) {
      mapPane.getChildren().add(icon.getImage());
    }
  }

  public void filterByLocation(LocationIcon icon) {
    ObservableList<String> filter = filterCheckBox.getCheckModel().getCheckedItems();
    if (!filter.contains("Department")
        && !filter.contains("Hallway")
        && !filter.contains("Service")
        && !filter.contains("Elevator")
        && !filter.contains("Stairway")
        && !filter.contains("Bathroom")
        && !filter.contains("Labs")) {
      mapPane.getChildren().add(icon.getImage());
    } else if (filter.contains("Department") && icon.getLocation().getNodeType().equals("DEPT")) {
      mapPane.getChildren().add(icon.getImage());
    } else if (filter.contains("Hallway") && icon.getLocation().getNodeType().equals("HALL")) {
      mapPane.getChildren().add(icon.getImage());
    } else if (filter.contains("Service") && icon.getLocation().getNodeType().equals("SERV")) {
      mapPane.getChildren().add(icon.getImage());
    } else if (filter.contains("Elevator") && icon.getLocation().getNodeType().equals("ELEV")) {
      mapPane.getChildren().add(icon.getImage());
    } else if (filter.contains("Stairway") && icon.getLocation().getNodeType().equals("STAI")) {
      mapPane.getChildren().add(icon.getImage());
    } else if (filter.contains("Bathroom")
        && (icon.getLocation().getNodeType().equals("BATH")
            || icon.getLocation().getNodeType().equals("REST"))) {
      mapPane.getChildren().add(icon.getImage());
    } else if (filter.contains("Labs") && icon.getLocation().getNodeType().equals("LABS")) {
      mapPane.getChildren().add(icon.getImage());
    }
  }

  // Adds icon to map
  public void addIcon(Icon icon) {
    switch (icon.iconType) {
      case "Location":
        requestSystem.getLocations().add(icon.getLocation());
    }
    PopupController.getController().closePopUp();
    MapController.getController()
        .getMapPane()
        .getChildren()
        .remove(MapManager.getManager().getTempIcon());
    MapManager.getManager().getFloor(getFloor()).addIcon(icon);
    MapManager.getManager().getTempIcon().setVisible(false);
    checkDropDown();
  }

  public void setSubmitLocation(double xPos, double yPos) {
    PopupController.getController()
        .submitIcon
        .setOnAction(
            event1 -> {
              if (PopupController.getController().checkLocationFields()) {
                addIcon(
                    new LocationIcon(
                        PopupController.getController()
                            .getLocation(xPos + 25, yPos + 15, getFloor())));
              } else {
                Text missingFields = new Text("Please fill all fields");
                missingFields.setFill(Color.RED);
                missingFields.setTextAlignment(TextAlignment.CENTER);
                PopupController.getController().sceneVbox.getChildren().add(missingFields);
                // System.out.println("MISSING FIELD");
              }
            });
  }

  // Opens and manages the location adding form
  @FXML
  public void openIconFormWindow(MouseEvent event) {
    if (!event.getTarget().getClass().getTypeName().equals("javafx.scene.image.ImageView")) {
      PopupController popupController = PopupController.getController();
      // X and Y coordinates
      double xPos = event.getX() - 15;
      double yPos = event.getY() - 25;

      // PopupController.getController().locationAdditionForm(event, false);
      // PopupController.getController().equipmentAdditionForm();
      PopupController.getController().iconWindow(event);
      setSubmitLocation(xPos, yPos);
      // Place Icon
      MapManager.getManager().placeTempIcon(xPos, yPos);
    }
  }
}