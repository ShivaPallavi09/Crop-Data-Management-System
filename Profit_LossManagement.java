import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Vector;

public class Profit_LossManagement extends JFrame {

    private Connection connection;
    private DefaultTableModel tableModel;
    private JTable table;

    public Profit_LossManagement() {
        try {
            // Establish connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/CDMS?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC", "sharath", "sharath");

            // Create UI
            setTitle("Profit Loss Management");
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
            JButton addButton = new JButton("Add Profit/Loss");
            JButton deleteButton = new JButton("Delete Profit/Loss");
            JButton updateButton = new JButton("Update Profit/Loss");
            JButton refreshButton = new JButton("Refresh");
            buttonPanel.add(addButton);
            buttonPanel.add(deleteButton);
            buttonPanel.add(updateButton);
            buttonPanel.add(refreshButton);
            panel.add(buttonPanel, BorderLayout.SOUTH);

            addButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    addProfitLoss();
                }
            });

            deleteButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    deleteProfitLoss();
                }
            });

            updateButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    updateProfitLoss();
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
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Profit_Loss");
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columns = metaData.getColumnCount();

            // Add column names
            Vector<String> columnNames = new Vector<>();
            for (int i = 1; i <= columns; i++) {
                columnNames.add(metaData.getColumnName(i));
            }
            tableModel.setColumnIdentifiers(columnNames);

            // Add data rows
            while (resultSet.next()) {
                Vector<String> rowData = new Vector<>();
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

    private void addProfitLoss() {
        try {
            String farmerId = JOptionPane.showInputDialog("Enter Farmer ID:");
            String profitLossValue = JOptionPane.showInputDialog("Enter Profit/Loss Value:");

            // Insert the new profit/loss into the database
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO Profit_Loss (F_Id, P_L_Value) VALUES (?, ?)");
            pstmt.setInt(1, Integer.parseInt(farmerId));
            pstmt.setBigDecimal(2, new BigDecimal(profitLossValue));
            pstmt.executeUpdate();

            pstmt.close();
            refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteProfitLoss() {
        try {
            String farmerId = JOptionPane.showInputDialog("Enter Farmer ID to delete:");

            // Delete the profit/loss from the database
            PreparedStatement pstmt = connection.prepareStatement("DELETE FROM Profit_Loss WHERE F_Id = ?");
            pstmt.setInt(1, Integer.parseInt(farmerId));
            int rowsAffected = pstmt.executeUpdate();
            pstmt.close();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Profit/Loss deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Profit/Loss deletion failed. Farmer ID not found.");
            }

            refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateProfitLoss() {
        try {
            String farmerId = JOptionPane.showInputDialog("Enter Farmer ID to update:");
            String newValue = JOptionPane.showInputDialog("Enter new Profit/Loss Value:");

            // Update the profit/loss in the database
            PreparedStatement pstmt = connection.prepareStatement("UPDATE Profit_Loss SET P_L_Value = ? WHERE F_Id = ?");
            pstmt.setBigDecimal(1, new BigDecimal(newValue));
            pstmt.setInt(2, Integer.parseInt(farmerId));
            int rowsAffected = pstmt.executeUpdate();
            pstmt.close();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Profit/Loss updated successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Profit/Loss update failed. Farmer ID not found.");
            }

            refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Profit_LossManagement profitLossManagement = new Profit_LossManagement();
    }
}
