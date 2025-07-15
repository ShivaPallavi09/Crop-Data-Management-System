import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class FarmersManagement extends JFrame {

    private Connection connection;
    private DefaultTableModel tableModel;
    private JTable table;

    public FarmersManagement() {
        try {
            // Establish connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/CDMS?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC", "sharath", "sharath");

            // Create UI
            setTitle("Farmers Management");
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
            JButton addButton = new JButton("Add Farmer");
            JButton deleteButton = new JButton("Delete Farmer");
            JButton updateButton = new JButton("Update Farmer");
            JButton refreshButton = new JButton("Refresh");
            buttonPanel.add(addButton);
            buttonPanel.add(deleteButton);
            buttonPanel.add(updateButton);
            buttonPanel.add(refreshButton);
            panel.add(buttonPanel, BorderLayout.SOUTH);

            addButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    addFarmer();
                }
            });

            deleteButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    deleteFarmer();
                }
            });

            updateButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    updateFarmer();
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
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Farmers");
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

    private int getNextFarmerID() {
        int nextID = 0;
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(F_ID) FROM Farmers");
            if (rs.next()) {
                nextID = rs.getInt(1) + 1;
            } else {
                nextID = 1; // If there are no existing farmers, start with ID 1
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nextID;
    }

    private void addFarmer() {
        try {
            String firstName = JOptionPane.showInputDialog("Enter First Name:");
            String surname = JOptionPane.showInputDialog("Enter Surname:");
            String dob = JOptionPane.showInputDialog("Enter Date of Birth (YYYY-MM-DD):");
            String doorNo = JOptionPane.showInputDialog("Enter Door Number:");
            String streetName = JOptionPane.showInputDialog("Enter Street Name:");
            String village = JOptionPane.showInputDialog("Enter Village:");
            String type = JOptionPane.showInputDialog("Enter Type:");

            // Insert the new farmer into the database
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO Farmers (F_ID, F_FIRSTNAME, F_SURNAME, F_DOB, F_DOORNO, F_STREETNAME, F_VILLAGE, F_TYPE) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            pstmt.setInt(1, getNextFarmerID());
            pstmt.setString(2, firstName);
            pstmt.setString(3, surname);
            pstmt.setDate(4, Date.valueOf(dob));
            pstmt.setString(5, doorNo);
            pstmt.setString(6, streetName);
            pstmt.setString(7, village);
            pstmt.setString(8, type);
            pstmt.executeUpdate();

            pstmt.close();
            refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteFarmer() {
        try {
            String farmerID = JOptionPane.showInputDialog("Enter Farmer ID to delete:");

            // Delete the farmer from the database
            PreparedStatement pstmt = connection.prepareStatement("DELETE FROM Farmers WHERE F_ID = ?");
            pstmt.setInt(1, Integer.parseInt(farmerID));
            int rowsAffected = pstmt.executeUpdate();
            pstmt.close();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Farmer deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Farmer deletion failed. Farmer ID not found.");
            }

            refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateFarmer() {
        try {
            String farmerID = JOptionPane.showInputDialog("Enter Farmer ID to update:");
            String field = JOptionPane.showInputDialog("Enter field to update (F_FIRSTNAME, F_SURNAME, F_DOB, F_DOORNO, F_STREETNAME, F_VILLAGE, F_TYPE):");
            String newValue = JOptionPane.showInputDialog("Enter new value:");

            // Update the farmer in the database
            PreparedStatement pstmt = connection.prepareStatement("UPDATE Farmers SET " + field + " = ? WHERE F_ID = ?");
            pstmt.setString(1, newValue);
            pstmt.setInt(2, Integer.parseInt(farmerID));
            int rowsAffected = pstmt.executeUpdate();
            pstmt.close();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Farmer updated successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Farmer update failed. Farmer ID not found.");
            }

            refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        FarmersManagement farmersManagement = new FarmersManagement();
    }
}
