import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class PesticidesManagement extends JFrame {

    private Connection connection;
    private DefaultTableModel tableModel;
    private JTable table;

    public PesticidesManagement() {
        try {
            // Establish connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/CDMS?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC", "sharath", "sharath");

            // Create UI
            setTitle("Pesticides Management");
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
            JButton addButton = new JButton("Add Pesticide");
            JButton deleteButton = new JButton("Delete Pesticide");
            JButton updateButton = new JButton("Update Pesticide");
            JButton refreshButton = new JButton("Refresh");
            buttonPanel.add(addButton);
            buttonPanel.add(deleteButton);
            buttonPanel.add(updateButton);
            buttonPanel.add(refreshButton);
            panel.add(buttonPanel, BorderLayout.SOUTH);

            addButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    addPesticide();
                }
            });

            deleteButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    deletePesticide();
                }
            });

            updateButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    updatePesticide();
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
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Pesticides");
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

    private int getNextPesticideID() {
        int nextID = 0;
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(P_Id) FROM Pesticides");
            if (rs.next()) {
                nextID = rs.getInt(1) + 1;
            } else {
                nextID = 1; // If there are no existing pesticides, start with ID 1
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nextID;
    }

    private void addPesticide() {
        try {
            String name = JOptionPane.showInputDialog("Enter Pesticide Name:");
            String manufactureDate = JOptionPane.showInputDialog("Enter Manufacture Date (YYYY-MM-DD):");
            String expiryDate = JOptionPane.showInputDialog("Enter Expiry Date (YYYY-MM-DD):");
            String use = JOptionPane.showInputDialog("Enter Use:");

            // Insert the new pesticide into the database
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO Pesticides (P_Id, P_Name, P_Manufacture, P_Expiry, P_Use) VALUES (?, ?, ?, ?, ?)");
            pstmt.setInt(1, getNextPesticideID());
            pstmt.setString(2, name);
            pstmt.setDate(3, Date.valueOf(manufactureDate));
            pstmt.setDate(4, Date.valueOf(expiryDate));
            pstmt.setString(5, use);
            pstmt.executeUpdate();

            pstmt.close();
            refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deletePesticide() {
        try {
            String pesticideID = JOptionPane.showInputDialog("Enter Pesticide ID to delete:");

            // Delete the pesticide from the database
            PreparedStatement pstmt = connection.prepareStatement("DELETE FROM Pesticides WHERE P_Id = ?");
            pstmt.setInt(1, Integer.parseInt(pesticideID));
            int rowsAffected = pstmt.executeUpdate();
            pstmt.close();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Pesticide deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Pesticide deletion failed. Pesticide ID not found.");
            }

            refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updatePesticide() {
        try {
            String pesticideID = JOptionPane.showInputDialog("Enter Pesticide ID to update:");
            String field = JOptionPane.showInputDialog("Enter field to update (P_Name, P_Manufacture, P_Expiry, P_Use):");
            String newValue = JOptionPane.showInputDialog("Enter new value:");

            // Update the pesticide in the database
            PreparedStatement pstmt = connection.prepareStatement("UPDATE Pesticides SET " + field + " = ? WHERE P_Id = ?");
            pstmt.setString(1, newValue);
            pstmt.setInt(2, Integer.parseInt(pesticideID));
            int rowsAffected = pstmt.executeUpdate();
            pstmt.close();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Pesticide updated successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Pesticide update failed. Pesticide ID not found.");
            }

            refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        PesticidesManagement pesticidesManagement = new PesticidesManagement();
    }
}
