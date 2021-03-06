package edu.wpi.cs3733.d22.teamV.dao;

import edu.wpi.cs3733.d22.teamV.interfaces.DaoInterface;
import edu.wpi.cs3733.d22.teamV.main.RequestSystem;
import edu.wpi.cs3733.d22.teamV.main.VApp;
import edu.wpi.cs3733.d22.teamV.main.Vdb;
import edu.wpi.cs3733.d22.teamV.servicerequests.ReligiousRequest;
import edu.wpi.cs3733.d22.teamV.servicerequests.ServiceRequest;
import java.io.*;
import java.io.IOException;
import java.sql.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ReligiousRequestDao extends DaoInterface {
  private static ArrayList<ReligiousRequest>
      allReligiousRequest; // A local list of all religious requests, updated via Vdb

  /** Initialize the arraylist */
  public ReligiousRequestDao() throws SQLException, IOException {
    allReligiousRequest = new ArrayList<ReligiousRequest>();
    createSQLTable();
    loadFromCSV();
  }

  @Override
  public void createSQLTable() {
    try {
      String query = "";
      Connection connection = Vdb.Connect();
      Statement statement = connection.createStatement();
      DatabaseMetaData meta = connection.getMetaData();
      ResultSet set = meta.getTables(null, null, "RELIGIOUSREQUESTS", new String[] {"TABLE"});
      if (!set.next()) {
        statement.execute(
            "CREATE TABLE RELIGIOUSREQUESTS(pID int, empID int, nodeID char(50), religion char(35), request char(200), status char(50),serviceID int,date_time timestamp)");
      } else {
        statement.execute("DROP TABLE RELIGIOUSREQUESTS");
        createSQLTable();
        return;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public List<ReligiousRequest> getAllReligiousRequest() {
    return allReligiousRequest;
  }

  public void saveToCSV() {
    try {
      FileWriter fw = new FileWriter(VApp.currentPath + "/ReligiousRequest.csv");
      BufferedWriter bw = new BufferedWriter(fw);
      bw.append("PatientID,EmpID,Location,Religion,Details,serviceID");

      for (ServiceRequest request : getAllServiceRequests()) {

        ReligiousRequest religiousRequest = (ReligiousRequest) request;

        String[] outputData = {
          Integer.toString(religiousRequest.getPatientID()),
          Integer.toString(religiousRequest.getEmployeeID()),
          religiousRequest.getLocation().getNodeID(),
          religiousRequest.getReligion(),
          religiousRequest.getDetails(),
          religiousRequest.getStatus(),
          Integer.toString(religiousRequest.getServiceID()),
          religiousRequest.getTimeMade().toString()
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

  public void loadFromCSV() {
    try {
      String line = "";
      FileReader fr = new FileReader(VApp.currentPath + "/ReligiousRequest.CSV");
      BufferedReader br = new BufferedReader(fr);
      String headerLine = br.readLine();
      String splitToken = ",";
      ArrayList<ReligiousRequest> requests = new ArrayList<>();
      while ((line = br.readLine()) != null) // should create a database based on csv file
      {
        String[] data;
        data = line.split(splitToken);
        ReligiousRequest request =
            new ReligiousRequest(
                Integer.parseInt(data[0]),
                Integer.parseInt(data[1]),
                data[2],
                data[3],
                data[4],
                data[5],
                data[7]);

        request.setServiceID(Integer.parseInt(data[6]));
        requests.add(request);
      }
      allReligiousRequest = requests;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void addToSQLTable(ServiceRequest Request) {
    ReligiousRequest newReligiousRequest = (ReligiousRequest) Request;
    Connection connection = Vdb.Connect();
    String query = "INSERT INTO RELIGIOUSREQUESTS VALUES(?,?,?,?,?,?,?,?)";
    try {
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setInt(1, newReligiousRequest.getEmployeeID());
      statement.setInt(2, newReligiousRequest.getPatientID());
      statement.setString(3, newReligiousRequest.getLocation().getNodeID());
      statement.setString(4, newReligiousRequest.getReligion());
      statement.setString(5, newReligiousRequest.getDetails());
      statement.setString(6, newReligiousRequest.getStatus());
      statement.setInt(7, newReligiousRequest.getServiceID());
      statement.setTimestamp(8, newReligiousRequest.getTimeMade());
      statement.executeUpdate(); // uninit params
    } catch (Exception e) {

    }
  }

  @Override
  public void removeFromSQLTable(ServiceRequest request) {
    try {
      int serviceID = request.getServiceID();
      Connection connection = Vdb.Connect();
      String query = "DELETE FROM RELIGIOUSREQUESTS WHERE serviceID=?";
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setInt(1, serviceID);
      statement.executeUpdate();
      statement.close();
    } catch (Exception e) {

    }
  }

  @Override
  public void addServiceRequest(ServiceRequest request) {
    ReligiousRequest newReligiousRequest = (ReligiousRequest) request;
    request.setServiceID(RequestSystem.getServiceID());
    newReligiousRequest.setServiceID(RequestSystem.getServiceID());
    allReligiousRequest.add(newReligiousRequest);
    try {
      addToSQLTable(request);
    } catch (Exception e) {

    }
    saveToCSV();
  }

  public void removeServiceRequest(ServiceRequest request) {
    int serviceID = request.getServiceID();
    Predicate<ReligiousRequest> condition =
        religiousRequest -> religiousRequest.getServiceID() == serviceID;
    allReligiousRequest.removeIf(condition);
    request.detachAll();
    try {
      removeFromSQLTable(request);
    } catch (Exception e) {

    }
    saveToCSV();
  }

  public ArrayList<? extends ServiceRequest> getAllServiceRequests() {
    return allReligiousRequest;
    //
  }

  @Override
  public void setAllServiceRequests(ArrayList<? extends ServiceRequest> serviceRequests) {
    allReligiousRequest.clear();
    for (ServiceRequest r : serviceRequests) {
      ReligiousRequest request = (ReligiousRequest) r;
      allReligiousRequest.add(request);
    }
    createSQLTable();
  }

  @Override
  public void updateServiceRequest(ServiceRequest request, int serviceID) {
    ReligiousRequest newRequest = (ReligiousRequest) request;
    newRequest.setServiceID(serviceID);
    int index = -1;
    for (int i = 0; i < allReligiousRequest.size(); i++) {
      if (allReligiousRequest.get(i).getServiceID() == request.getServiceID()) {
        index = i;
        break;
      }
    }
    if (index >= 0) {
      System.out.println("WE updating");
      allReligiousRequest.set(index, newRequest);
      try {
        updateSQLTable(request);
      } catch (Exception e) {

      }
      saveToCSV();
    }
  }

  public void updateSQLTable(ServiceRequest Request) throws SQLException {
    ReligiousRequest newRequest = (ReligiousRequest) Request;
    Connection connection = Vdb.Connect();
    String query =
        "UPDATE RELIGIOUSREQUESTS"
            + " SET pID=?,empID=?,nodeID=?,religion=?,request=?,status=?,date_time=?"
            + " WHERE serviceID=?";
    PreparedStatement statement = connection.prepareStatement(query);
    statement.setInt(1, newRequest.getEmployeeID());
    statement.setInt(2, newRequest.getPatientID());
    statement.setString(3, newRequest.getLocation().getNodeID());
    statement.setString(4, newRequest.getReligion());
    statement.setString(5, newRequest.getDetails());
    statement.setString(6, newRequest.getStatus());
    statement.setTimestamp(7, newRequest.getTimeMade());
    statement.setInt(8, newRequest.getServiceID());
    statement.executeUpdate();
    statement.close();
  }
}
