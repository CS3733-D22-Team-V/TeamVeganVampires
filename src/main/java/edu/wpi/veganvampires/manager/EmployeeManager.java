package edu.wpi.veganvampires.manager;

import edu.wpi.veganvampires.objects.HospitalEmployee;
import java.util.ArrayList;

public class EmployeeManager {
  private ArrayList<HospitalEmployee> employeeList;

  public EmployeeManager() {
    employeeList = new ArrayList<>();
  }

  private static class SingletonHelper {
    private static final EmployeeManager manager = new EmployeeManager();
  }

  public static EmployeeManager getManager() {
    return EmployeeManager.SingletonHelper.manager;
  }

  public HospitalEmployee getEmployee(int userID) {
    for (HospitalEmployee he : employeeList) {
      if (he.getHospitalID() == userID) {
        return he;
      }
    }
    return null;
  }
}