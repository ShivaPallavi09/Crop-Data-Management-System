import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class FarmManagement extends JFrame {

    private Connection connection;
    private DefaultTableModel tableModel;
    private JTable table;

    public FarmManagement() {
        try {
            // Establish connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/CDMS?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC", "sharath", "sharath");

            // Create UI
            setTitle("Farm Management");
            setSize(800, 600);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);

            JPanel panel = new JPanel(new BorderLayout());
            getContentPane().add(panel);

            tableModel = new DefaultTableModel();
            table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);
            panel.add(scrollPane, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel();
            JButton addButton = new JButton("Add Farm");
            JButton deleteButton = new JButton("Delete Farm");
            JButton updateButton = new JButton("Update Farm");
            JButton refreshButton = new JButton("Refresh");
            buttonPanel.add(addButton);
            buttonPanel.add(deleteButton);
            buttonPanel.add(updateButton);
            buttonPanel.add(refreshButton);
            panel.add(buttonPanel, BorderLayout.SOUTH);

            addButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    addFarm();
                }
            });

            deleteButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    deleteFarm();
                }
            });

            updateButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    updateFarm();
                }
            });

            refreshButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    refreshTable();
                }
            });

            // Display table
            refreshTable();

            setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void refreshTable() {
        try {
            // Clear table
            tableModel.setRowCount(0);

            // Fetch data from database
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Farm");
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columns = metaData.getColumnCount();

            // Add column names
            Vector<String> columnNames = new Vector<String>();
            for (int i = 1; i <= columns; i++) {
                columnNames.add(metaData.getColumnName(i));
            }
            tableModel.setColumnIdentifiers(columnNames);

            // Add data rows
            while (resultSet.next()) {
                Vector<String> rowData = new Vector<String>();
                for (int i = 1; i <= columns; i++) {
                    rowData.add(resultSet.getString(i));
                }
                tableModel.addRow(rowData);
            }

            resultSet.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getNextFarmID() {
        int nextID = 0;
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(FARM_ID) FROM Farm");
            if (rs.next()) {
                nextID = rs.getInt(1) + 1;
            } else {
                nextID = 1; // If there are no existing farms, start with ID 1
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nextID;
    }

    private void addFarm() {
        try {
            String area = JOptionPane.showInputDialog("Enter Area:");
            String village = JOptionPane.showInputDialog("Enter Village:");
            String mandal = JOptionPane.showInputDialog("Enter Mandal:");
            String soilType = JOptionPane.showInputDialog("Enter Soil Type:");
            String farmerID = JOptionPane.showInputDialog("Enter Farmer ID:");

            // Insert the new farm into the database
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO Farm (Farm_Id, Farm_Area, Farm_Village, Farm_Mandal, Farm_SoilType, F_Id) VALUES (?, ?, ?, ?, ?, ?)");
            pstmt.setInt(1, getNextFarmID());
            pstmt.setString(2, area);
            pstmt.setString(3, village);
            pstmt.setString(4, mandal);
            pstmt.setString(5, soilType);
            pstmt.setInt(6, Integer.parseInt(farmerID));
            pstmt.executeUpdate();

            pstmt.close();
            refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteFarm() {
        try {
            String farmID = JOptionPane.showInputDialog("Enter Farm ID to delete:");

            // Delete the farm from the database
            PreparedStatement pstmt = connection.prepareStatement("DELETE FROM Farm WHERE FARM_ID = ?");
            pstmt.setInt(1, Integer.parseInt(farmID));
            int rowsAffected = pstmt.executeUpdate();
            pstmt.close();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Farm deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Farm deletion failed. Farm ID not found.");
            }

            refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateFarm() {
        try {
            String farmID = JOptionPane.showInputDialog("Enter Farm ID to update:");
            String field = JOptionPane.showInputDialog("Enter field to update (FARM_AREA, FARM_VILLAGE, FARM_MANDAL, FARM_SOILTYPE, F_ID):");
            String newValue = JOptionPane.showInputDialog("Enter new value:");

            // Update the farm in the database
            PreparedStatement pstmt = connection.prepareStatement("UPDATE Farm SET " + field + " = ? WHERE FARM_ID = ?");
            pstmt.setString(1, newValue);
            pstmt.setInt(2, Integer.parseInt(farmID));
            int rowsAffected = pstmt.executeUpdate();
            pstmt.close();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Farm updated successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Farm update failed. Farm ID not found.");
            }

            refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        FarmManagement farmManagement = new FarmManagement();
    }
}
