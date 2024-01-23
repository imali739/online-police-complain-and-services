import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class App extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;
    private JFrame mainAppFrame;

    // JDBC Database connection details
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/userinformation";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "12345678";

    public App() {
        setTitle("Online Police Complaint System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLayout(new GridLayout(3, 2));

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        loginButton = new JButton("Login");
        signupButton = new JButton("Signup");

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (authenticateUser(username, password)) {
                    openMainApplication(username);
                } else {
                    JOptionPane.showMessageDialog(mainAppFrame, "Invalid credentials", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openSignupForm();
            }
        });

        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(loginButton);
        add(signupButton);

        setVisible(true);
    }

    private boolean authenticateUser(String username, String password) {
        String query = "SELECT * FROM users WHERE name = ? AND password = ?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            return preparedStatement.executeQuery().next();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mainAppFrame, "Failed to connect to the database", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void openMainApplication(String username) {
        mainAppFrame = new JFrame("Welcome, " + username + "!");
        mainAppFrame.setSize(600, 400);
        mainAppFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainAppFrame.setLayout(new GridLayout(3, 2));

        JButton fileComplaintButton = createReportButton("File a Complaint", "complaints");
        JButton reportCrimeButton = createReportButton("Report a Crime", "crimes");
        JButton characterCertificateButton = createReportButton("Request Character Certificate", "certificates");
        JButton tenantRegistrationButton = createReportButton("Tenant Registration", "registration");
        JButton policeVerificationButton = createReportButton("Police Verification", "verification");

        mainAppFrame.add(fileComplaintButton);
        mainAppFrame.add(reportCrimeButton);
        mainAppFrame.add(characterCertificateButton);
        mainAppFrame.add(tenantRegistrationButton);
        mainAppFrame.add(policeVerificationButton);

        mainAppFrame.setVisible(true);
    }

    private JButton createReportButton(String buttonText, String tableName) {
        JButton reportButton = new JButton(buttonText);

        reportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel reportPanel = new JPanel(new GridLayout(4, 2));

                JLabel nameLabel = new JLabel("Name:");
                JTextField nameField = new JTextField();
                reportPanel.add(nameLabel);
                reportPanel.add(nameField);

                JLabel cnicLabel = new JLabel("CNIC:");
                JTextField cnicField = new JTextField();
                reportPanel.add(cnicLabel);
                reportPanel.add(cnicField);

                JLabel descriptionLabel = new JLabel("Description:");
                JTextField descriptionField = new JTextField();
                reportPanel.add(descriptionLabel);
                reportPanel.add(descriptionField);

                int result = JOptionPane.showConfirmDialog(mainAppFrame, reportPanel, buttonText,
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    String enteredName = nameField.getText();
                    String enteredCnic = cnicField.getText();
                    String enteredDescription = descriptionField.getText();

                    insertDataIntoTable(tableName, enteredName, enteredCnic, enteredDescription);

                    String message = "Name: " + enteredName + "\nCNIC: " + enteredCnic +
                            "\nDescription: " + enteredDescription;
                    JOptionPane.showMessageDialog(mainAppFrame, message, "Report Filed Successfully", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        return reportButton;
    }

    private void openSignupForm() {
        JPanel signupPanel = new JPanel(new GridLayout(6, 2));

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();
        signupPanel.add(nameLabel);
        signupPanel.add(nameField);

        JLabel cnicLabel = new JLabel("CNIC:");
        JTextField cnicField = new JTextField();
        signupPanel.add(cnicLabel);
        signupPanel.add(cnicField);

        JLabel addressLabel = new JLabel("Address:");
        JTextField addressField = new JTextField();
        signupPanel.add(addressLabel);
        signupPanel.add(addressField);

        JLabel phoneNumberLabel = new JLabel("Phone Number:");
        JTextField phoneNumberField = new JTextField();
        signupPanel.add(phoneNumberLabel);
        signupPanel.add(phoneNumberField);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        signupPanel.add(passwordLabel);
        signupPanel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(mainAppFrame, signupPanel, "Signup",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String enteredName = nameField.getText();
            String enteredCnic = cnicField.getText();
            String enteredAddress = addressField.getText();
            String enteredPhoneNumber = phoneNumberField.getText();
            String enteredPassword = new String(passwordField.getPassword());

            registerUser(enteredName, enteredCnic, enteredAddress, enteredPhoneNumber, enteredPassword);

            String signupMessage = "Name: " + enteredName + "\nCNIC: " + enteredCnic +
                    "\nAddress: " + enteredAddress + "\nPhone Number: " + enteredPhoneNumber +
                    "\nPassword: " + enteredPassword;
            JOptionPane.showMessageDialog(mainAppFrame, signupMessage, "Signup Successful", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void registerUser(String name, String cnic, String address, String phoneNumber, String password) {
        String query = "INSERT INTO users (name, cnic, address, phone_number, password) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, cnic);
            preparedStatement.setString(3, address);
            preparedStatement.setString(4, phoneNumber);
            preparedStatement.setString(5, password);

            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mainAppFrame, "Failed to register user", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void insertDataIntoTable(String tableName, String name, String cnic, String description) {
        String query = "INSERT INTO " + tableName + " (name, cnic, description) VALUES (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, cnic);
            preparedStatement.setString(3, description);

            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mainAppFrame, "Failed to insert data into " + tableName, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new App();
            }
        });
    }
}
