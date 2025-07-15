import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class HomePage extends JFrame implements ActionListener {
    public HomePage() {
        setTitle("CROP DATA MANAGEMENT SYSTEM");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Maximize the frame
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Create a panel for the heading and buttons using GridBagLayout
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false); // Make panel transparent

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel headingLabel = new JLabel("CROP DATA MANAGEMENT SYSTEM");
        headingLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 5; // Span across 5 columns
        panel.add(headingLabel, gbc);

        // Set up background image
        ImageIcon backgroundImage = new ImageIcon("F:\\Team-06(1602-22-737-165 & 175)\\project\\images\\cFarm.jpg"); // Replace with your image file path
        JLabel backgroundLabel = new JLabel(backgroundImage);
        backgroundLabel.setLayout(new BorderLayout());

        // Add panel to the background label
        backgroundLabel.add(panel, BorderLayout.CENTER);

        // Set background label as content pane
        setContentPane(backgroundLabel);

        // Add buttons
        JButton[] buttons = new JButton[10];
        String[] buttonLabels = {"FARMERS", "FARM", "PESTICIDES", "PESTICIDES_FARM", "EQUIPMENT", "EQUIPMENT_FARMERS", "CROP", "PROFIT_LOSS", "CONTACTS", "GET FARMER'S DATA"};
        String[] filePaths = {
                "C:\\MyWebProjects\\FarmersManagement.java",
                "C:\\MyWebProjects\\FarmManagement.java",
                "C:\\MyWebProjects\\PesticidesManagement.java",
                "C:\\MyWebProjects\\Pesticides_FarmManagement.java",
                "C:\\MyWebProjects\\EquipmentManagement.java",
                "C:\\MyWebProjects\\Equipment_FarmersManagement.java",
                "C:\\MyWebProjects\\CropManagement.java",
                "C:\\MyWebProjects\\Profit_LossManagement.java",
                "C:\\MyWebProjects\\Farmer_F_ContactManagement.java",
                "C:\\MyWebProjects\\FarmerDataForm.java"
        };

        gbc.gridwidth = 1; // Reset to 1 for buttons
        gbc.gridy = 1; // Start from second row
        for (int i = 0; i < 10; i++) {
            buttons[i] = new JButton(buttonLabels[i]);
            buttons[i].setPreferredSize(new Dimension(200, 100)); // Adjusted button size
            buttons[i].setActionCommand(filePaths[i]);
            buttons[i].addActionListener(this);

            gbc.gridx = i % 5;
            if (i % 5 == 0 && i != 0) {
                gbc.gridy++;
            }
            panel.add(buttons[i], gbc);
        }

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String filePath = e.getActionCommand();
        openFile(filePath);
    }

    private void openFile(String filePath) {
        try {
            ProcessBuilder pb = new ProcessBuilder("javac", "-cp", ".;C:\\MyWebProjects\\mysql-connector-j-8.4.0\\mysql-connector-j-8.4.0\\mysql-connector-j-8.4.0.jar", filePath);
            Process process = pb.start();

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                String className = filePath.substring(filePath.lastIndexOf("\\") + 1, filePath.lastIndexOf("."));
                pb = new ProcessBuilder("java", "-cp", ".;C:\\MyWebProjects\\mysql-connector-j-8.4.0\\mysql-connector-j-8.4.0\\mysql-connector-j-8.4.0.jar", className);
                process = pb.start();
            } else {
                System.out.println("Compilation failed for file: " + filePath);
            }
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HomePage());
    }
}
