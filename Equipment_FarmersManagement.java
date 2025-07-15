import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class Equipment_FarmersManagement extends JFrame {

    private Connection connection;
    private DefaultTableModel tableModel;
    private JTable table;

    public Equipment_FarmersManagement() {
        try {
            // Establish connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/CDMS?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC", "sharath", "sharath");

            // Create UI
            setTitle("Equipment Farmers Management");
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
            JButton addButton = new JButton("Add Equipment");
            JButton deleteButton = new JButton("Delete Equipment");
            JButton updateButton = new JButton("Update Equipment");
            JButton refreshButton = new JButton("Refresh");
            buttonPanel.add(addButton);
            buttonPanel.add(deleteButton);
            buttonPanel.add(updateButton);
            buttonPanel.add(refreshButton);
            panel.add(buttonPanel, BorderLayout.SOUTH);

            addButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    addEquipment();
                }
            });

            deleteButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    deleteEquipment();
                }
            });

            updateButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    updateEquipment();
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
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Equipment_Farmers");
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

    private int getNextEquipmentID() {
        int nextID = 0;
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(Equipment_Id) FROM Equipment_Farmers");
            if (rs.next()) {
                nextID = rs.getInt(1) + 1;
            } else {
                nextID = 1; // If there are no existing equipment, start with ID 1
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nextID;
    }

    private void addEquipment() {
        try {
            String equipmentId = JOptionPane.showInputDialog("Enter Equipment ID:");
            String farmerId = JOptionPane.showInputDialog("Enter Farmer ID:");

            // Insert the new equipment into the database
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO Equipment_Farmers (Equipment_Id, F_Id) VALUES (?, ?)");
            pstmt.setInt(1, getNextEquipmentID());
            pstmt.setInt(2, Integer.parseInt(farmerId));
            pstmt.executeUpdate();

            pstmt.close();
            refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteEquipment() {
        try {
            String equipmentID = JOptionPane.showInputDialog("Enter Equipment ID to delete:");

            // Delete the equipment from the database
            PreparedStatement pstmt = connection.prepareStatement("DELETE FROM Equipment_Farmers WHERE Equipment_Id = ?");
            pstmt.setInt(1, Integer.parseInt(equipmentID));
            int rowsAffected = pstmt.executeUpdate();
            pstmt.close();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Equipment deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Equipment deletion failed. Equipment ID not found.");
            }

            refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateEquipment() {
        try {
            String equipmentID = JOptionPane.showInputDialog("Enter Equipment ID to update:");
            String field = JOptionPane.showInputDialog("Enter field to update (F_Id):");
            String newValue = JOptionPane.showInputDialog("Enter new value:");

            // Update the equipment in the database
            PreparedStatement pstmt = connection.prepareStatement("UPDATE Equipment_Farmers SET " + field + " = ? WHERE Equipment_Id = ?");
            pstmt.setString(1, newValue);
            pstmt.setInt(2, Integer.parseInt(equipmentID));
            int rowsAffected = pstmt.executeUpdate();
            pstmt.close();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Equipment updated successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Equipment update failed. Equipment ID not found.");
            }

            refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Equipment_FarmersManagement equipmentFarmersManagement = new Equipment_FarmersManagement();
    }
}

