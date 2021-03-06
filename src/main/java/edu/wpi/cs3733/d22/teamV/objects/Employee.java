package edu.wpi.cs3733.d22.teamV.objects;

import edu.wpi.cs3733.d22.teamV.main.Vdb;
import edu.wpi.cs3733.d22.teamV.observer.DirectionalAssoc;
import edu.wpi.cs3733.d22.teamV.servicerequests.ServiceRequest;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Employee extends DirectionalAssoc {
  private int employeeID = -1;
  private String firstName = "";
  private String lastName = "";
  private String employeePosition = "Doctor";
  private ArrayList<String> specialties = new ArrayList<>();
  private ArrayList<Integer> patientIDs = new ArrayList<>();
  private ArrayList<Integer> serviceRequestIDs = new ArrayList<>();
  private boolean isAdmin;

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

  public ArrayList<Integer> getPatientIDs() {
    ArrayList<Integer> patientIDList = new ArrayList<>();

    for (Patient patient : getPatientList()) {
      patientIDList.add(patient.getPatientID());
    }

    return patientIDList;
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
  public void update(DirectionalAssoc directionalAssoc) {
    // Check to see what updated and its type
    if (directionalAssoc instanceof Patient) {
      Patient patient = (Patient) directionalAssoc;
      int patientID = patient.getPatientID();
      boolean employeeContains = patientIDs.contains(patientID);
      boolean patientContains = patient.getEmployeeIDs().contains(getEmployeeID());

      // Check to see if the patient has a state change relevant to the employee containing it
      if (employeeContains && !patientContains) {
        patientIDs.removeIf(currID -> currID == patientID);

      } else if (!employeeContains && patientContains) {
        patientIDs.add(patientID);
      }

    } else if (directionalAssoc instanceof ServiceRequest) {

      ServiceRequest serviceRequest = (ServiceRequest) directionalAssoc;
      int employeeID = serviceRequest.getEmployee().getEmployeeID();
      int serviceID = serviceRequest.getServiceID();
      int patientID = serviceRequest.getPatient().getPatientID();
      boolean employeeContains = serviceRequestIDs.contains(serviceID);
      boolean serviceRequestContains = (employeeID == getEmployeeID());

      if (employeeContains && !serviceRequestContains || serviceRequest.toBeDeleted) {
        serviceRequestIDs.removeIf(currID -> currID == serviceID);
        patientIDs.removeIf(currID -> currID == patientID);
      } else if (!employeeContains && serviceRequestContains) {
        serviceRequestIDs.add(serviceID);
        if (!patientIDs.contains(patientID)) patientIDs.add(patientID);
      }
    }

    Vdb.requestSystem.getEmployeeDao().updateEmployee(this, getEmployeeID());
  }
}
