import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class CropManagement extends JFrame {

    private Connection connection;
    private DefaultTableModel tableModel;
    private JTable table;

    public CropManagement() {
        try {
            // Establish connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/CDMS?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC", "sharath", "sharath");

            // Create UI
            setTitle("Crop Management");
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
            JButton addButton = new JButton("Add Crop");
            JButton deleteButton = new JButton("Delete Crop");
            JButton updateButton = new JButton("Update Crop");
            JButton refreshButton = new JButton("Refresh");
            buttonPanel.add(addButton);
            buttonPanel.add(deleteButton);
            buttonPanel.add(updateButton);
            buttonPanel.add(refreshButton);
            panel.add(buttonPanel, BorderLayout.SOUTH);

            addButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    addCrop();
                }
            });

            deleteButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    deleteCrop();
                }
            });

            updateButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    updateCrop();
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
            ResultSet resultSet = statement.executeQuery("SELECT * FROM crop");
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

    private int getNextCropID() {
        int nextID = 0;
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(C_Id) FROM crop");
            if (rs.next()) {
                nextID = rs.getInt(1) + 1;
            } else {
                nextID = 1; // If there are no existing crops, start with ID 1
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nextID;
    }

    private void addCrop() {
        try {
            String farmId = JOptionPane.showInputDialog("Enter Farm ID:");
            String name = JOptionPane.showInputDialog("Enter Crop Name:");
            String duration = JOptionPane.showInputDialog("Enter Duration:");
            String seasonMonth = JOptionPane.showInputDialog("Enter Season Month:");
            String investment = JOptionPane.showInputDialog("Enter Investment:");

            // Insert the new crop into the database
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO crop (C_Id, Farm_Id, C_Name, Duration, Season_Month, Investment) VALUES (?, ?, ?, ?, ?, ?)");
            pstmt.setInt(1, getNextCropID());
            pstmt.setInt(2, Integer.parseInt(farmId));
            pstmt.setString(3, name);
            pstmt.setInt(4, Integer.parseInt(duration));
            pstmt.setString(5, seasonMonth);
            pstmt.setInt(6, Integer.parseInt(investment));
            pstmt.executeUpdate();

            pstmt.close();
            refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteCrop() {
        try {
            String cropID = JOptionPane.showInputDialog("Enter Crop ID to delete:");

            // Delete the crop from the database
            PreparedStatement pstmt = connection.prepareStatement("DELETE FROM crop WHERE C_Id = ?");
            pstmt.setInt(1, Integer.parseInt(cropID));
            int rowsAffected = pstmt.executeUpdate();
            pstmt.close();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Crop deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Crop deletion failed. Crop ID not found.");
            }

            refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateCrop() {
        try {
            String cropID = JOptionPane.showInputDialog("Enter Crop ID to update:");
            String field = JOptionPane.showInputDialog("Enter field to update (Farm_Id, C_Name, Duration, Season_Month, Investment):");
            String newValue = JOptionPane.showInputDialog("Enter new value:");

            // Update the crop in the database
            PreparedStatement pstmt = connection.prepareStatement("UPDATE crop SET " + field + " = ? WHERE C_Id = ?");
            pstmt.setString(1, newValue);
            pstmt.setInt(2, Integer.parseInt(cropID));
            int rowsAffected = pstmt.executeUpdate();
            pstmt.close();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Crop updated successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Crop update failed. Crop ID not found.");
            }

            refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        CropManagement cropManagement = new CropManagement();
    }
}
