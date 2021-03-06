package edu.wpi.cs3733.d22.teamV.dao;

import edu.wpi.cs3733.d22.teamV.main.VApp;
import edu.wpi.cs3733.d22.teamV.main.Vdb;
import edu.wpi.cs3733.d22.teamV.map.Pathfinder;
import edu.wpi.cs3733.d22.teamV.objects.Location;
import java.io.*;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

public class PathfindingDao {

  private Pathfinder pathfinder = new Pathfinder();

  @Getter private static ArrayList<Edge> edges = new ArrayList<>();

  public PathfindingDao() {
    loadFromCSV();
  }

  public Pathfinder getPathfinder() {
    return pathfinder;
  }

  private boolean loading = true; // To make sure we don't override the CSV when we load in

  /** Loads in edge information from the CSV */
  public void loadFromCSV() {
    try {

      String line = "";
      FileReader fr = new FileReader(VApp.currentPath + "/Pathfinding.CSV");
      BufferedReader br = new BufferedReader(fr);
      String splitToken = ",";
      String headerLine = br.readLine();
      // int ID, String name, floor,double x, double y, String description, Boolean isDirty) {
      while ((line = br.readLine()) != null) // should create a database based on csv file
      {
        String[] data;
        data = line.split(splitToken);
        addPathNode(data[1], data[2]);
      }
      saveToCSV();
      loading = false;

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Getter
  @Setter
  public class Edge {
    private String name, nodeOne, nodeTwo;

    public Edge(String nodeOne, String nodeTwo) {
      this.name = nodeOne + "_" + nodeTwo; // Name based on CSV edgeID
      this.nodeOne = nodeOne;
      this.nodeTwo = nodeTwo;
      edges.add(this);
    }

    public ArrayList<Edge> getEdges() {
      return edges;
    }

    // remove a selected edge from the list of edges (for removal)
    public void removeEdge() {
      edges.remove(this);
    }

    // check if an edge contains a node
    public boolean containsNode(String nodeName) {
      return nodeOne.equalsIgnoreCase(nodeName) || nodeTwo.equalsIgnoreCase(nodeName);
    }

    public boolean equals(String name1, String name2) {
      String fullName = name1 + "_" + name2;
      String reverseName = name2 + "_" + name1;
      return name.equalsIgnoreCase(fullName) || name.equalsIgnoreCase(reverseName);
    }
  }

  /**
   * Add a pathfinding edge to both the arraylist and to the pathfinding class. Requires two
   * locations
   *
   * @param nodeOne a location ID
   * @param nodeTwo a location ID
   */
  public void addPathNode(String nodeOne, String nodeTwo) {
    if ((!nodeOne.isEmpty()) && (!nodeTwo.isEmpty())) {
      // Scan for duplicates
      String edgeName = nodeOne + "_" + nodeTwo;
      for (Edge edge : getEdges()) {
        if (edge.getName().equalsIgnoreCase(edgeName)) {
          // System.out.println("Already contains this edge! " + edgeName);
          return;
        }
      }
      // Create and store the edge
      Edge edge = new Edge(nodeOne, nodeTwo);

      if (!loading) {
        saveToCSV();
      }

      // Find the locations
      Location startLocation = Vdb.requestSystem.getLocationDao().getLocationPathfinding(nodeOne);
      Location endLocation = Vdb.requestSystem.getLocationDao().getLocationPathfinding(nodeTwo);

      if (!(startLocation == null || endLocation == null)) {
        // Create start and end nodes, where nodeOne is the start and nodeTwo is the end
        Pathfinder.Node startNode;
        Pathfinder.Node endNode;

        // Check and see if we already made these nodes before
        startNode = Pathfinder.getNodeFromName(nodeOne);
        endNode = Pathfinder.getNodeFromName(nodeTwo);

        // System.out.println(nodeOne + " " + nodeTwo);

        if (startNode == null) {
          startNode = new Pathfinder.Node(nodeOne);
          Pathfinder.addNode(startNode);
        }
        if (endNode == null) {
          endNode = new Pathfinder.Node(nodeTwo);
          Pathfinder.addNode(endNode);
        }
        // Get the X and Y coordinates
        double x1 = startLocation.getXCoord();
        double x2 = endLocation.getXCoord();
        double y1 = startLocation.getYCoord();
        double y2 = startLocation.getYCoord();

        // Distance formula
        double distance = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));

        // Add a link between the two of them so they can pathfind to each other
        startNode.addLink(new Pathfinder.Link(endNode, distance));
        endNode.addLink(new Pathfinder.Link(startNode, distance));
      } else {
        // System.out.println("Couldn't find node locations");
      }
    }
  }

  /** Save the contents of the arraylist edges into Pathfinding.CSV */
  public void saveToCSV() {
    try {
      FileWriter fw = new FileWriter(VApp.currentPath + "/Pathfinding.csv");
      BufferedWriter bw = new BufferedWriter(fw);
      bw.append("edgeID,startNode,endNode");
      for (Edge edge : getEdges()) {

        String[] outputData = {edge.name, edge.nodeOne, edge.nodeTwo};
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

  /** Removes a node and all links connected to it */
  public void removePathNode(String nodeID) {
    // Get the node in Pathfinder and remove it if it exists
    Pathfinder.Node nodeToRemove = Pathfinder.getNodeFromName(nodeID);
    if (nodeToRemove != null) {
      Pathfinder.removeNode(nodeToRemove);
    }
    // Remove all edges if it contains the name
    edges.removeIf(edge -> edge.containsNode(nodeID));
    // Save the changes to the CSV
    saveToCSV();
  }

  /** Removes a link between two nodes with the given names */
  public void removeLink(String nodeID1, String nodeID2) {
    Pathfinder.Node node1 = Pathfinder.getNodeFromName(nodeID1);
    Pathfinder.Node node2 = Pathfinder.getNodeFromName(nodeID2);

    if (node1 != null && node2 != null) {
      node1.getLinks().removeIf(link -> link.containsNode(node2));
      node2.getLinks().removeIf(link -> link.containsNode(node1));
      edges.removeIf(edge -> edge.equals(node1.getName(), node2.getName()));
      saveToCSV();
    }
  }
}
