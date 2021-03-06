package edu.wpi.cs3733.d22.teamV.dao;

import edu.wpi.cs3733.d22.teamV.interfaces.DaoInterface;
import edu.wpi.cs3733.d22.teamV.main.VApp;
import edu.wpi.cs3733.d22.teamV.main.Vdb;
import edu.wpi.cs3733.d22.teamV.servicerequests.InternalPatientTransportation;
import edu.wpi.cs3733.d22.teamV.servicerequests.ServiceRequest;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;

public class InternalPatientTransportationDao extends DaoInterface {

  // A local list of all internal patient transportations, updated via Vdb
  private static ArrayList<InternalPatientTransportation> allInternalPatientTransportations;

  /** Initialize the arraylist */
  public InternalPatientTransportationDao() {
    allInternalPatientTransportations = new ArrayList<InternalPatientTransportation>();
    createSQLTable();
    loadFromCSV();
  }

  public void setAllInternalPatientTransportations(
      ArrayList<InternalPatientTransportation> internalPatientTransportations) {
    allInternalPatientTransportations = internalPatientTransportations;
  }

  public ArrayList<InternalPatientTransportation> getInternalPatientTransportations() {
    return allInternalPatientTransportations;
  }

  @Override
  public void loadFromCSV() {
    try {

      String line = "";
      FileReader fr = new FileReader(VApp.currentPath + "/PatientTransportations.CSV");
      BufferedReader br = new BufferedReader(fr);
      String headerLine = br.readLine();
      String splitToken = ",";
      ArrayList<InternalPatientTransportation> transportations = new ArrayList<>();
      while ((line = br.readLine()) != null) // should create a database based on csv file
      {
        String[] data;
        data = line.split(splitToken);
        int i = 0;
        for (String d : data) {
          System.out.println(i + " " + d);
          i++;
        }
        InternalPatientTransportation transportation =
            new InternalPatientTransportation(
                data[0],
                Integer.parseInt(data[1]),
                Integer.parseInt(data[2]),
                data[3],
                data[4],
                data[6]);
        transportation.setServiceID(Integer.parseInt(data[5]));
        addServiceRequest(transportation);
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void saveToCSV() {
    try {

      FileWriter fw = new FileWriter(VApp.currentPath + "/PatientTransportations.csv");
      BufferedWriter bw = new BufferedWriter(fw);
      bw.append("location,patientID,employeeID,requestDetails,serviceID");

      for (ServiceRequest request : getAllServiceRequests()) {

        InternalPatientTransportation internalPatientTransportation =
            (InternalPatientTransportation) request;

        String[] outputData = {
          internalPatientTransportation.getNodeID(),
          String.valueOf(internalPatientTransportation.getPatientID()),
          String.valueOf(internalPatientTransportation.getEmployeeID()),
          internalPatientTransportation.getRequestDetails(),
          internalPatientTransportation.getStatus(),
          String.valueOf(internalPatientTransportation.getServiceID()),
          internalPatientTransportation.getTimeMade().toString()
        };
        bw.append("\n");
        for (String s : outputData) {
          bw.append(s);
          bw.append(',');
        }
      }
      bw.close();
      fw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void createSQLTable() {
    try {

      Connection connection = Vdb.Connect();
      Statement exampleStatement = connection.createStatement();
      DatabaseMetaData meta = connection.getMetaData();

      ResultSet set = meta.getTables(null, null, "PATIENTTRANSPORTATION", new String[] {"TABLE"});
      String query = "";

      if (!set.next()) {
        query =
            "CREATE TABLE PATIENTTRANSPORTATION(location char(50), patientID int, employeeID int, requestDetails char(250), status char(50),serviceID int,date_time timestamp)";
        exampleStatement.execute(query);
      } else {
        query = "DROP TABLE PATIENTTRANSPORTATION";
        exampleStatement.execute(query);
        createSQLTable();
        return;
      }
      for (InternalPatientTransportation internalPatientTransportation :
          allInternalPatientTransportations) {
        addToSQLTable(internalPatientTransportation);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void addToSQLTable(ServiceRequest request) {
    try {
      //              + "location,patientID,employeeID,requestDetails,status,serviceID,date_time)
      // VALUES "
      Connection connection = Vdb.Connect();
      InternalPatientTransportation internalPatientTransportation =
          (InternalPatientTransportation) request;
      String query = "INSERT INTO PATIENTTRANSPORTATION VALUES(?,?,?,?,?,?,?)";
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setString(1, internalPatientTransportation.getNodeID());
      statement.setInt(2, internalPatientTransportation.getPatientID());
      statement.setInt(3, internalPatientTransportation.getEmployeeID());
      statement.setString(4, internalPatientTransportation.getRequestDetails());
      statement.setString(5, internalPatientTransportation.getStatus());
      statement.setInt(6, internalPatientTransportation.getServiceID());
      statement.setTimestamp(7, internalPatientTransportation.getTimeMade());
      statement.executeUpdate();
      statement.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void updateServiceRequest(ServiceRequest request, int serviceID) {
    InternalPatientTransportation transportation = (InternalPatientTransportation) request;
    transportation.setServiceID(serviceID);
    removeServiceRequest(transportation);
    allInternalPatientTransportations.add(transportation);
    addToSQLTable(transportation);
    saveToCSV();
  }

  @Override
  public void removeFromSQLTable(ServiceRequest request) {
    try {

      InternalPatientTransportation internalPatientTransportation =
          (InternalPatientTransportation) request;
      String query = "";
      Connection connection = Vdb.Connect();
      assert connection != null;
      Statement statement = connection.createStatement();

      query =
          "DELETE FROM PATIENTTRANSPORTATION WHERE serviceID = "
              + internalPatientTransportation.getServiceID();
      statement.execute(query);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void addServiceRequest(ServiceRequest request) {
    InternalPatientTransportation internalPatientTransportation =
        (InternalPatientTransportation) request;
    allInternalPatientTransportations.add(internalPatientTransportation);

    addToSQLTable(internalPatientTransportation);
    saveToCSV();
  }

  @Override
  public void removeServiceRequest(ServiceRequest request) {
    allInternalPatientTransportations.removeIf(
        value -> value.getPatientID() == request.getPatient().getPatientID());
    request.detachAll();
    removeFromSQLTable(request);
    saveToCSV();
  }

  @Override
  public ArrayList<? extends ServiceRequest> getAllServiceRequests() {
    return getInternalPatientTransportations();
  }

  @Override
  public void setAllServiceRequests(ArrayList<? extends ServiceRequest> serviceRequests) {
    allInternalPatientTransportations = (ArrayList<InternalPatientTransportation>) serviceRequests;
  }
}
