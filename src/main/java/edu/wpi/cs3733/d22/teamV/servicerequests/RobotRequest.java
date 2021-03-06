package edu.wpi.cs3733.d22.teamV.servicerequests;

import edu.wpi.cs3733.d22.teamV.main.RequestSystem;
import edu.wpi.cs3733.d22.teamV.main.Vdb;
import edu.wpi.cs3733.d22.teamV.objects.Employee;
import edu.wpi.cs3733.d22.teamV.observer.DirectionalAssoc;
import java.sql.Timestamp;
import java.time.Instant;

public class RobotRequest extends ServiceRequest {
  private String nodeID;
  private int botID;

  public RobotRequest(
      int hospitalID, int botID, String nodeID, String details, String status, String date) {
    employee = Vdb.requestSystem.getEmployeeDao().getEmployee(hospitalID);
    if (!date.equals("")) {
      this.timeMade = Timestamp.valueOf(date);

    } else {
      this.timeMade = Timestamp.from(Instant.now());
    }
    this.location = RequestSystem.getSystem().getLocation(nodeID);
    this.botID = botID;
    this.nodeID = nodeID;
    if (details != "") {
      this.details = details;
    } else {
      this.details = "";
    }

    this.status = status;
    setServiceID(RequestSystem.getServiceID());
    if (serviceID < 0) { // calls system to set id
      // setServiceID(RequestSystem.getServiceID());
    } else {
      // setServiceID(serviceID);
    }
    this.type = "Robot Request";
  }

  public RobotRequest(int hospitalID, int botID, String nodeID, String details, String status) {
    this.timeMade = Timestamp.from(Instant.now());
    employee = Vdb.requestSystem.getEmployeeDao().getEmployee(hospitalID);
    this.location = RequestSystem.getSystem().getLocation(nodeID);
    this.botID = botID;
    this.nodeID = nodeID;
    if (details != "") {
      this.details = details;
    } else {
      this.details = "";
    }

    this.status = status;
    setServiceID(RequestSystem.getServiceID());
    if (serviceID < 0) { // calls system to set id
      // setServiceID(RequestSystem.getServiceID());
    } else {
      // setServiceID(serviceID);
    }
    this.type = "Robot Request";
  }

  public Employee getEmployee() {
    return employee;
  }

  public int getBotID() {
    return botID;
  }

  @Override
  public int getPatientID() {
    return botID;
  }

  @Override
  public void update(DirectionalAssoc directionalAssoc) {
    super.update(directionalAssoc);
    Vdb.requestSystem
        .getDao(RequestSystem.Dao.RobotRequest)
        .updateServiceRequest(this, getServiceID());
  }
}
