import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class Farmer_F_ContactManagement extends JFrame {

    private Connection connection;
    private DefaultTableModel tableModel;
    private JTable table;

    public Farmer_F_ContactManagement() {
        try {
            // Establish connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/CDMS?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC", "sharath", "sharath");

            // Create UI
            setTitle("Farmer Contact Management");
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
            JButton addButton = new JButton("Add Contact");
            JButton deleteButton = new JButton("Delete Contact");
            JButton updateButton = new JButton("Update Contact");
            JButton refreshButton = new JButton("Refresh");
            buttonPanel.add(addButton);
            buttonPanel.add(deleteButton);
            buttonPanel.add(updateButton);
            buttonPanel.add(refreshButton);
            panel.add(buttonPanel, BorderLayout.SOUTH);

            addButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    addContact();
                }
            });

            deleteButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    deleteContact();
                }
            });

            updateButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    updateContact();
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
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Farmer_F_Contact");
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

    private int getNextContactID() {
        int nextID = 0;
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(F_Id) FROM Farmer_F_Contact");
            if (rs.next()) {
                nextID = rs.getInt(1) + 1;
            } else {
                nextID = 1; // If there are no existing contacts, start with ID 1
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nextID;
    }

    private void addContact() {
        try {
            String farmerId = JOptionPane.showInputDialog("Enter Farmer ID:");
            String contact = JOptionPane.showInputDialog("Enter Contact Number:");

            // Insert the new contact into the database
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO Farmer_F_Contact (F_ID, F_CONTACT) VALUES (?, ?)");
            pstmt.setInt(1, Integer.parseInt(farmerId));
            pstmt.setString(2, contact);
            pstmt.executeUpdate();

            pstmt.close();
            refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteContact() {
        try {
            String contactID = JOptionPane.showInputDialog("Enter Contact ID to delete:");

            // Delete the contact from the database
            PreparedStatement pstmt = connection.prepareStatement("DELETE FROM Farmer_F_Contact WHERE F_ID = ?");
            pstmt.setInt(1, Integer.parseInt(contactID));
            int rowsAffected = pstmt.executeUpdate();
            pstmt.close();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Contact deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Contact deletion failed. Contact ID not found.");
            }

            refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateContact() {
        try {
            String contactID = JOptionPane.showInputDialog("Enter Farmer ID to update:");
            String newContact = JOptionPane.showInputDialog("Enter new contact number:");

            // Update the contact in the database
            PreparedStatement pstmt = connection.prepareStatement("UPDATE Farmer_F_Contact SET F_CONTACT = ? WHERE F_ID = ?");
            pstmt.setString(1, newContact);
            pstmt.setInt(2, Integer.parseInt(contactID));
            int rowsAffected = pstmt.executeUpdate();
            pstmt.close();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Contact updated successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Contact update failed. Contact ID not found.");
            }

            refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Farmer_F_ContactManagement contactManagement = new Farmer_F_ContactManagement();
    }
}
