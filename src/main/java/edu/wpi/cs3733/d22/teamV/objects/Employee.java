package edu.wpi.cs3733.d22.teamV.objects;

import edu.wpi.cs3733.d22.teamV.dao.EmployeeDao;
import edu.wpi.cs3733.d22.teamV.main.Vdb;
import edu.wpi.cs3733.d22.teamV.observer.Observer;
import edu.wpi.cs3733.d22.teamV.observer.Subject;
import edu.wpi.cs3733.d22.teamV.servicerequests.ServiceRequest;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Employee extends Subject implements Observer {
  private int employeeID;
  private String firstName;
  private String lastName;
  private String employeePosition;
  private ArrayList<String> specialties;
  private ArrayList<Integer> patientIDs;
  private ArrayList<Integer> serviceRequestIDs;
  private boolean isAdmin;
  private static EmployeeDao employeeDao = Vdb.requestSystem.getEmployeeDao();

  public Employee(
      int employeeID,
      String firstName,
      String lastName,
      String employeePosition,
      ArrayList<String> specialties,
      ArrayList<Integer> patientIDs,
      ArrayList<Integer> serviceRequestIDs,
      boolean isAdmin) {
    this.employeeID = employeeID;
    this.firstName = firstName;
    this.lastName = lastName;
    this.specialties = specialties;
    this.patientIDs = patientIDs;
    this.employeePosition = employeePosition;
    this.serviceRequestIDs = serviceRequestIDs;
  }

  public Employee(int employeeID) {
    this.employeeID = employeeID;
  }

  public Employee() {}

  public void addPatient(Patient patient) {
    patientIDs.add(patient.getPatientID());
  }

  public void addPatient(int patientID) throws SQLException, IOException {
    patientIDs.add(patientID);
    Vdb.requestSystem.getPatientDao().getPatientFromID(patientID).attach(this);
    updateAllObservers();
  }

  public void removePatient(Patient patient) {
    patientIDs.remove(patient);
  }

  public void removePatient(int patientID) {
    patientIDs.remove(patientID);
  }

  public int getPatientListSize() {
    return patientIDs.size();
  }

  public ArrayList<Patient> getPatientList() {
    ArrayList<Patient> patients = new ArrayList<>();
    for (int patientID : patientIDs) {
      for (Patient patient : Vdb.requestSystem.getPatients()) {
        if (patient.getPatientID() == patientID) {
          patients.add(patient);
        }
      }
    }
    return patients;
  }

  public int getServiceRequestListSize() {
    return serviceRequestIDs.size();
  }

  public void addServiceRequest(ServiceRequest request) {
    serviceRequestIDs.add(request.getServiceID());
  }

  public void addServiceRequest(int serviceID) {
    serviceRequestIDs.add(serviceID);
  }

  public void removeServiceRequest(ServiceRequest request) {
    serviceRequestIDs.remove(request.getServiceID());
  }

  public void removeServiceRequest(int serviceID) {
    serviceRequestIDs.remove(serviceID);
  }

  public ArrayList<ServiceRequest> getServiceRequestList() {
    ArrayList<ServiceRequest> serviceRequests = new ArrayList<>();
    for (int serviceID : serviceRequestIDs) {
      for (ServiceRequest request : Vdb.requestSystem.getEveryServiceRequest()) {
        if (request.getServiceID() == serviceID) {
          serviceRequests.add(request);
        }
      }
    }
    return serviceRequests;
  }

  // Used to update info in the observer
  @Override
  public void update(Subject subject) throws SQLException, IOException {
    if (subject instanceof Patient) {

      Patient modifyPatient = (Patient) subject;
      if (patientIDs.contains(modifyPatient.getPatientID())
          && !modifyPatient.getEmployeeIDs().contains(getEmployeeID())) {
        patientIDs.removeIf(currID -> currID == modifyPatient.getPatientID());

      } else if (!patientIDs.contains(modifyPatient.getPatientID())
          && modifyPatient.getEmployeeIDs().contains(getEmployeeID())) {
        patientIDs.add(modifyPatient.getPatientID());
      }
    } // else if servicerequest....

    // Update the Dao
    employeeDao.updateEmployee(this, getEmployeeID());
  }
}
