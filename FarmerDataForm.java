import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class FarmerDataForm extends JFrame {

    private JTextField farmerIdField;
    private JTable resultTable;
    private Connection connection;

    public FarmerDataForm() {
        // Set up the frame
        setTitle("Farmer Data Form");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize UI components
        JPanel panel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new FlowLayout());
        JLabel farmerIdLabel = new JLabel("Enter Farmer ID: ");
        farmerIdField = new JTextField(10);
        JButton fetchButton = new JButton("Fetch Data");
        JButton printButton = new JButton("Print Data");

        inputPanel.add(farmerIdLabel);
        inputPanel.add(farmerIdField);
        inputPanel.add(fetchButton);
        inputPanel.add(printButton);

        // Table to display results
        resultTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(resultTable);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        getContentPane().add(panel);

        // Add action listener to the button
        fetchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fetchFarmerData();
            }
        });

        // Add action listener to the print button
        printButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printTableData();
            }
        });

        // Establish database connection
        connectToDatabase();
    }

    private void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/CDMS?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                    "sharath", "sharath");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void fetchFarmerData() {
        String farmerId = farmerIdField.getText().trim();
        if (farmerId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Farmer ID.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String query = "SELECT F.F_ID, F.F_FIRSTNAME, F.F_SURNAME, F.F_DOB, F.F_DOORNO, F.F_STREETNAME, F.F_VILLAGE, F.F_TYPE, " +
                    "FA.FARM_AREA, FA.FARM_VILLAGE, FA.FARM_MANDAL, FA.FARM_SOILTYPE, " +
                    "P.P_NAME, P.P_MANUFACTURE, P.P_EXPIRY, P.P_USE, " +
                    "E.EQUIPMENT_TYPE, E.EQUIPMENT_COST, E.EQUIPMENT_USE, " +
                    "C.C_NAME, C.DURATION, C.SEASON_MONTH, C.INVESTMENT, " +
                    "PL.P_L_VALUE, " +
                    "FFC.F_CONTACT " +
                    "FROM Farmers F " +
                    "LEFT JOIN Farm FA ON F.F_ID = FA.F_ID " +
                    "LEFT JOIN Pesticides_Farm PF ON FA.FARM_ID = PF.FARM_ID " +
                    "LEFT JOIN Pesticides P ON PF.P_ID = P.P_ID " +
                    "LEFT JOIN Equipment_Farmers EF ON F.F_ID = EF.F_ID " +
                    "LEFT JOIN Equipment E ON EF.EQUIPMENT_ID = E.EQUIPMENT_ID " +
                    "LEFT JOIN Crop C ON FA.FARM_ID = C.FARM_ID " +
                    "LEFT JOIN Profit_Loss PL ON F.F_ID = PL.F_ID " +
                    "LEFT JOIN Farmer_F_Contact FFC ON F.F_ID = FFC.F_ID " +
                    "WHERE F.F_ID = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, Integer.parseInt(farmerId));
            ResultSet resultSet = pstmt.executeQuery();

            // Clear previous results
            resultTable.setModel(new DefaultTableModel());

            // If there is data, display it
            if (resultSet.next()) {
                String[] columnNames = {
                        "Field", "Value"
                };

                Object[][] data = {
                        {"Farmer ID", resultSet.getInt("F_ID")},
                        {"First Name", resultSet.getString("F_FIRSTNAME")},
                        {"Surname", resultSet.getString("F_SURNAME")},
                        {"Date of Birth", resultSet.getDate("F_DOB")},
                        {"Door Number", resultSet.getString("F_DOORNO")},
                        {"Street Name", resultSet.getString("F_STREETNAME")},
                        {"Village", resultSet.getString("F_VILLAGE")},
                        {"Type", resultSet.getString("F_TYPE")},
                        {"Farm Area", resultSet.getDouble("FARM_AREA")},
                        {"Farm Village", resultSet.getString("FARM_VILLAGE")},
                        {"Farm Mandal", resultSet.getString("FARM_MANDAL")},
                        {"Farm Soil Type", resultSet.getString("FARM_SOILTYPE")},
                        {"Pesticide Name", resultSet.getString("P_NAME")},
                        {"Pesticide Manufacture Date", resultSet.getDate("P_MANUFACTURE")},
                        {"Pesticide Expiry Date", resultSet.getDate("P_EXPIRY")},
                        {"Pesticide Use", resultSet.getString("P_USE")},
                        {"Equipment Type", resultSet.getString("EQUIPMENT_TYPE")},
                        {"Equipment Cost", resultSet.getDouble("EQUIPMENT_COST")},
                        {"Equipment Use", resultSet.getString("EQUIPMENT_USE")},
                        {"Crop Name", resultSet.getString("C_NAME")},
                        {"Duration", resultSet.getInt("DURATION")},
                        {"Season Month", resultSet.getString("SEASON_MONTH")},
                        {"Investment", resultSet.getDouble("INVESTMENT")},
                        {"Profit/Loss Value", resultSet.getDouble("P_L_VALUE")},
                        {"Contact", resultSet.getString("F_CONTACT")},
                };

                DefaultTableModel model = new DefaultTableModel(data, columnNames);
                resultTable.setModel(model);
            } else {
                JOptionPane.showMessageDialog(this, "No data found for Farmer ID: " + farmerId);
            }

            resultSet.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void printTableData() {
        try {
            if (!resultTable.print()) {
                System.err.println("User cancelled printing");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FarmerDataForm().setVisible(true));
    }
}
