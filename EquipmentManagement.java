import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class EquipmentManagement extends JFrame {

    private Connection connection;
    private DefaultTableModel tableModel;
    private JTable table;

    public EquipmentManagement() {
        try {
            // Establish connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/CDMS?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC", "sharath", "sharath");

            // Create UI
            setTitle("Equipment Management");
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
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Equipment");
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
            ResultSet rs = stmt.executeQuery("SELECT MAX(Equipment_Id) FROM Equipment");
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
            String type = JOptionPane.showInputDialog("Enter Equipment Type:");
            String cost = JOptionPane.showInputDialog("Enter Equipment Cost:");
            String use = JOptionPane.showInputDialog("Enter Equipment Use:");

            // Insert the new equipment into the database
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO Equipment (Equipment_Id, Equipment_Type, Equipment_Cost, Equipment_Use) VALUES (?, ?, ?, ?)");
            pstmt.setInt(1, getNextEquipmentID());
            pstmt.setString(2, type);
            pstmt.setInt(3, Integer.parseInt(cost));
            pstmt.setString(4, use);
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
            PreparedStatement pstmt = connection.prepareStatement("DELETE FROM Equipment WHERE Equipment_Id = ?");
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
            String field = JOptionPane.showInputDialog("Enter field to update (Equipment_Type, Equipment_Cost, Equipment_Use):");
            String newValue = JOptionPane.showInputDialog("Enter new value:");

            // Update the equipment in the database
            PreparedStatement pstmt = connection.prepareStatement("UPDATE Equipment SET " + field + " = ? WHERE Equipment_Id = ?");
            if (field.equals("Equipment_Cost")) {
                pstmt.setInt(1, Integer.parseInt(newValue));
            } else {
                pstmt.setString(1, newValue);
            }
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
        EquipmentManagement equipmentManagement = new EquipmentManagement();
    }
}
