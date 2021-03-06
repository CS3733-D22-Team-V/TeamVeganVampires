package edu.wpi.cs3733.d22.teamV.servicerequests;

import edu.wpi.cs3733.d22.teamV.main.RequestSystem;
import edu.wpi.cs3733.d22.teamV.main.Vdb;
import edu.wpi.cs3733.d22.teamV.observer.DirectionalAssoc;
import java.sql.Timestamp;
import java.time.Instant;

public class EquipmentDelivery extends ServiceRequest {
  private final String equipment;
  private final int quantity;

  public EquipmentDelivery(
      int employeeID,
      int patientID,
      String patientFirstName,
      String patientLastName,
      String nodeID,
      String equipment,
      String notes,
      int quantity,
      String status,
      String date) {
    if (date != "") {
      this.timeMade = Timestamp.valueOf(date);

    } else {
      this.timeMade = Timestamp.from(Instant.now());
    }
    this.location = RequestSystem.getSystem().getLocation(nodeID);
    this.employee = Vdb.requestSystem.getEmployeeDao().getEmployee(employeeID);
    this.patient = Vdb.requestSystem.getPatientDao().getPatient(patientID);
    this.equipment = equipment;
    this.details = notes;
    this.type = "Equipment Delivery";
    this.status = status;
    this.quantity = quantity;
    this.status = status;
    this.dao = RequestSystem.Dao.EquipmentDelivery;
  }

  public EquipmentDelivery(
      int employeeID,
      int patientID,
      String nodeID,
      String equipment,
      String notes,
      int quantity,
      String status,
      String date) {
    if (!date.equals("")) {
      this.timeMade = Timestamp.valueOf(date);
    } else {
      this.timeMade = Timestamp.from(Instant.now());
    }
    this.location = RequestSystem.getSystem().getLocation(nodeID);
    this.employee = Vdb.requestSystem.getEmployeeDao().getEmployee(employeeID);
    this.patient = Vdb.requestSystem.getPatientDao().getPatient(patientID);
    this.equipment = equipment;
    this.details = notes;
    this.type = "Equipment Delivery Request";
    this.quantity = quantity;
    this.status = status;
  }

  public String getFloorName() {
    return floor.getFloorName();
  }

  public EquipmentDelivery(
      int employeeID,
      int patientID,
      String nodeID,
      String equipment,
      String notes,
      int quantity,
      String status) {
    this.timeMade = Timestamp.from(Instant.now());
    this.location = RequestSystem.getSystem().getLocation(nodeID);
    this.employee = Vdb.requestSystem.getEmployeeDao().getEmployee(employeeID);
    this.patient = Vdb.requestSystem.getPatientDao().getPatient(patientID);
    this.equipment = equipment;
    this.details = notes;
    notes = equipment + "x" + quantity;
    this.type = "Equipment Delivery Request";
    this.quantity = quantity;
    this.status = status;
    setServiceID(RequestSystem.getServiceID());
  }

  public EquipmentDelivery(String nodeID, String equipment) {
    this.timeMade = Timestamp.from(Instant.now());
    this.location = RequestSystem.getSystem().getLocation(nodeID);
    this.employee = Vdb.requestSystem.getEmployeeDao().getEmployee(-1);
    this.patient = Vdb.requestSystem.getPatientDao().getPatient(-1);
    this.equipment = equipment;
    this.details = notes;
    notes = equipment + "x" + 1;
    this.type = "Equipment Delivery Request";
    this.quantity = 1;
    this.status = "Not Started";
    setServiceID(RequestSystem.getServiceID());
  }

  public String getEquipment() {
    return equipment;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setServiceID(int serviceID) {
    super.setServiceID(serviceID);
    DirectionalAssoc.link(employee, patient, this);
    updateAllObservers();
  }

  @Override
  public void update(DirectionalAssoc directionalAssoc) {
    super.update(directionalAssoc);
    Vdb.requestSystem
        .getDao(RequestSystem.Dao.EquipmentDelivery)
        .updateServiceRequest(this, getServiceID());
  }
}
