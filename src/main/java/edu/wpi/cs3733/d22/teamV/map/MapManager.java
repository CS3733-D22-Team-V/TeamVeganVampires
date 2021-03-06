package edu.wpi.cs3733.d22.teamV.map;

import edu.wpi.cs3733.d22.teamV.main.RequestSystem;
import edu.wpi.cs3733.d22.teamV.main.Vdb;
import edu.wpi.cs3733.d22.teamV.objects.Equipment;
import edu.wpi.cs3733.d22.teamV.objects.Location;
import edu.wpi.cs3733.d22.teamV.servicerequests.ServiceRequest;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.image.Image;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapManager {
  private ArrayList<Floor> floorList = new ArrayList<>();
  List<? extends ServiceRequest> serviceRequests = new ArrayList<>();
  RequestSystem requestSystem = Vdb.requestSystem;
  public Image cleanEquipment = new Image("cleanEquipment.png");
  public Image dirtyEquipment = new Image("dirtyEquipment.png");
  public Image locationMarker = new Image("locationMarker.png");
  public Image requestMarker = new Image("requestMarker.png");
  public Image nodeMarker = new Image("nodeMarker.png");

  /** Gets every service request and sets up the floors */
  public void init() {
    serviceRequests = requestSystem.getEveryServiceRequest();
    Floor l1 = new Floor("L1", new Image("Lower Level 1.png"));
    Floor l2 = new Floor("L2", new Image("Lower Level 2.png"));
    Floor f1 = new Floor("1", new Image("1st Floor.png"));
    Floor f2 = new Floor("2", new Image("2nd Floor.png"));
    Floor f3 = new Floor("3", new Image("3rd Floor.png"));
    Floor f4 = new Floor("4", new Image("4th Floor.png"));
    Floor f5 = new Floor("5", new Image("5th Floor.png"));
    floorList.add(l1);
    floorList.add(l2);
    floorList.add(f1);
    floorList.add(f2);
    floorList.add(f3);
    floorList.add(f4);
    floorList.add(f5);
    setUpFloors();
  }

  private static class SingletonHelper {
    private static final MapManager manager = new MapManager();
  }

  public static MapManager getManager() {
    return SingletonHelper.manager;
  }

  /** Sets up floor elements */
  public void setUpFloors() {
    for (Floor floor : floorList) {
      floor.getEquipmentIcons().clear();
      floor.getLocationIcons().clear();
    }
    loadEquipment();
    setUpLocations();
    loadRequests();
  }

  private void setUpLocations() {
    for (Location l : requestSystem.getLocations()) {
      switch (l.getFloor()) {
        case "L1":
          loadLocations(0, l);
          break;
        case "L2":
          loadLocations(1, l);
          break;
        case "1":
          loadLocations(2, l);
          break;
        case "2":
          loadLocations(3, l);
          break;
        case "3":
          loadLocations(4, l);
          break;
        case "4":
          loadLocations(5, l);
          break;
        case "5":
          loadLocations(6, l);
          break;
      }
    }
  }

  public void loadEquipment() {
    for (Equipment e : requestSystem.getEquipment()) {
      if (getFloor(e.getFloor()).containsIcon(e.getX(), e.getY())) {
        getFloor(e.getFloor()).addToEquipmentIcon(e.getX(), e.getY(), e);
        e.setFloor(e.getFloor());
      } else {
        EquipmentIcon icon = new EquipmentIcon(new Location(e.getX(), e.getY(), e.getFloor()));
        icon.addToEquipmentList(e);
        e.setIcon(icon);
        getFloor(e.getFloor()).addIcon(e.getIcon());
        icon.setFloor(getFloor(e.getFloor()));
        icon.setImage();
        icon.setFloor(getFloor(e.getFloor()));
      }
    }
  }

  /** Loads every location and gives each the correct icon image */
  public void loadLocations(int i, Location l) {
    if (floorList.size() > 0) {
      LocationIcon locationIcon = new LocationIcon(l);
      l.setIcon(locationIcon);
      floorList.get(i).addIcon(locationIcon);
      locationIcon.setFloor(floorList.get(i));
    }
  }

  /** Loads each request into it's corresponding location's list */
  public void loadRequests() {
    if (requestSystem.getEveryServiceRequest().size() > 0) {
      for (ServiceRequest serviceRequest : requestSystem.getEveryServiceRequest()) {
        if (serviceRequest.getLocation() != null) {
          if (serviceRequest.getLocation().getNodeID() != null) {

            requestSystem
                .getLocation(serviceRequest.getLocation().getNodeID())
                .getIcon()
                .addToRequests(serviceRequest);
            serviceRequest.setFloor(
                MapManager.getManager().getFloor(serviceRequest.getLocation().getFloor()));
          }
        }
      }
    }
  }

  /**
   * @param str
   * @return Floor that corresponds to str
   */
  public Floor getFloor(String str) {
    switch (str) {
      case "L1":
        return floorList.get(0);
      case "L2":
        return floorList.get(1);
      case "1":
        return floorList.get(2);
      case "2":
        return floorList.get(3);
      case "3":
        return floorList.get(4);
      case "4":
        return floorList.get(5);
      case "5":
        return floorList.get(6);
    }
    return null;
  }
}
