package edu.wpi.veganvampires.dao;

import edu.wpi.veganvampires.interfaces.EquipmentDeliveryImpl;
import edu.wpi.veganvampires.main.Vdb;
import edu.wpi.veganvampires.objects.EquipmentDelivery;
import java.sql.*;
import java.util.ArrayList;

public class EquipmentDeliveryDao implements EquipmentDeliveryImpl {

  private static ArrayList<EquipmentDelivery> allEquipmentDeliveries;

  /** Initialize the array list */
  public EquipmentDeliveryDao() {
    allEquipmentDeliveries = new ArrayList<EquipmentDelivery>();
  }

  public EquipmentDeliveryDao(ArrayList<EquipmentDelivery> allEquipmentDeliveries) {
    this.allEquipmentDeliveries = allEquipmentDeliveries;
  }

  public void setAllEquipmentDeliveries(ArrayList<EquipmentDelivery> equipmentDeliveryArrayList) {
    allEquipmentDeliveries = equipmentDeliveryArrayList;
  }

  /**
   * Getter
   *
   * @return
   */
  @Override
  public ArrayList<EquipmentDelivery> getAllEquipmentDeliveries() {
    return allEquipmentDeliveries;
  }

  /**
   * Adds equipment to the CSV
   * @
   * @param location
   * @param equipment
   * @param notes
   * @param quantity
   * @throws SQLException
   */
  @Override
  public void addEquipmentDelivery(int eID, int pID, String fname, String lname, String location, String equipment, String notes, int quantity, String status)
      throws SQLException {
    EquipmentDelivery newEquipmentDelivery =
        new EquipmentDelivery( eID, pID, fname, lname, location, equipment, notes, quantity,status);

    System.out.println("Adding to local arraylist...");
    allEquipmentDeliveries.add(newEquipmentDelivery);

    System.out.println("Adding to database");
    try {
      Connection connection = Vdb.Connect();
      Statement exampleStatement = connection.createStatement();
      Vdb.saveToFile(Vdb.Database.EquipmentDelivery);
      exampleStatement.execute(
          "INSERT INTO LOCATIONS VALUES (newEquipmentDelivery.getEquipment(), newEquipmentDelivery.getNotes(), newEquipmentDelivery.getLocation(), newEqipmentDelivery.getQuantity()) ");

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * TODO: Make sure that this doesn't remove someone else's equipment Remove the equipment by
   * finding the string of the equipment and using is to remove it from the arraylist
   *
   * @param equipment a string of the desired equipment to remove
   * @throws SQLException
   */
  @Override
  public void removeEquipmentDelivery(String equipment) throws SQLException {

    System.out.println("Removing from arraylist...");
    allEquipmentDeliveries.removeIf(e -> e.getEquipment().equals(equipment));

    try {
      System.out.println("Removing from database...");
      Connection connection;
      connection = DriverManager.getConnection("jdbc:derby:VDB;create=true", "admin", "admin");
      Statement exampleStatement = connection.createStatement();
      for (EquipmentDelivery e : allEquipmentDeliveries)
        exampleStatement.execute("DELETE FROM LOCATIONS WHERE equipment.equals(e.getEquipment())");

      Vdb.saveToFile(Vdb.Database.EquipmentDelivery);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
