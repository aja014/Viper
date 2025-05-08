package play;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CustomDialog {

    // Custom error dialog
    public static void showCustomError(Component parent, String messageText) {
        JDialog dialog = new JDialog((Frame) parent, true);
        dialog.setUndecorated(true);
        dialog.setSize(400, 150);
        dialog.setLocationRelativeTo(parent);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 240, 245)); // Light pink
        panel.setLayout(new BorderLayout());

        // Convert newlines to <br> for multi-line support
        messageText = "<html><div align='center'>" + messageText.replace("\n", "<br>") + "</div></html>";
        
        JLabel message = new JLabel(messageText, SwingConstants.CENTER);
        message.setFont(new Font("Arial", Font.BOLD, 30));
        message.setForeground(Color.RED);

        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Arial", Font.PLAIN, 18));
        okButton.setBackground(new Color(255, 182, 193)); // Soft pastel red-pink
        okButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        okButton.addActionListener(e -> dialog.dispose());

        panel.add(message, BorderLayout.CENTER);
        panel.add(okButton, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    // Custom success dialog
    public static void showCustomSuccess(Component parent, String messageText) {
        JDialog dialog = new JDialog((Frame) parent, true);
        dialog.setUndecorated(true);
        dialog.setSize(400, 150);
        dialog.setLocationRelativeTo(parent);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(220, 255, 220)); // Soft green
        panel.setLayout(new BorderLayout());

        // Convert newlines to <br> for multi-line support
        messageText = "<html><div align='center'>" + messageText.replace("\n", "<br>") + "</div></html>";

        JLabel message = new JLabel(messageText, SwingConstants.CENTER);
        message.setFont(new Font("Arial", Font.BOLD, 30));
        message.setForeground(new Color(34, 139, 34)); // Forest green

        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Arial", Font.PLAIN, 18));
        okButton.setBackground(new Color(144, 238, 144)); // Light green
        okButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        okButton.addActionListener(e -> dialog.dispose());

        panel.add(message, BorderLayout.CENTER);
        panel.add(okButton, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    // Custom confirmation dialog
    public static int showCustomConfirmation(Component parent, String messageText, String titleText) {
        final int[] result = { -1 };

        JDialog dialog = new JDialog((Frame) parent, true);
        dialog.setUndecorated(true);
        dialog.setSize(400, 180);
        dialog.setLocationRelativeTo(parent);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 204, 128)); // light orange/yellowish
        panel.setLayout(new BorderLayout());

        JLabel title = new JLabel(titleText, SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 40));
        title.setForeground(new Color(220, 20, 60)); // Crimson red
        panel.add(title, BorderLayout.NORTH);

        messageText = "<html><div align='center'>" + messageText.replace("\n", "<br>") + "</div></html>";
        JLabel message = new JLabel(messageText, SwingConstants.CENTER);
        message.setFont(new Font("Arial", Font.PLAIN, 25));
        message.setForeground(new Color(255, 87, 34)); // Dark orange
        panel.add(message, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(255, 235, 205)); // light yellow-orange

        JButton yesButton = new JButton("YES");
        yesButton.setBackground(new Color(255, 215, 0)); // Gold/Yellow
        yesButton.setFont(new Font("Arial", Font.BOLD, 25));
        yesButton.setForeground(Color.WHITE);
        yesButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        yesButton.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));
        yesButton.addActionListener(e -> {
            result[0] = JOptionPane.YES_OPTION;
            dialog.dispose();
        });

        JButton noButton = new JButton("NO");
        noButton.setBackground(new Color(220, 20, 60)); // Crimson red
        noButton.setFont(new Font("Arial", Font.BOLD, 25));
        noButton.setForeground(Color.WHITE);
        noButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        noButton.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));
        noButton.addActionListener(e -> {
            result[0] = JOptionPane.NO_OPTION;
            dialog.dispose();
        });

        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);

        return result[0];
    }

    // Custom warning dialog
    public static void showCustomWarning(Component parent, String messageText) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), true);
        dialog.setUndecorated(true);
        dialog.setSize(400, 150);
        dialog.setLocationRelativeTo(parent);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 250, 205)); // Lemon Chiffon / light yellow
        panel.setLayout(new BorderLayout());

        // Convert newlines to <br> for multi-line support
        messageText = "<html><div align='center'>" + messageText.replace("\n", "<br>") + "</div></html>";

        JLabel message = new JLabel(messageText, SwingConstants.CENTER);
        message.setFont(new Font("Arial", Font.BOLD, 30));
        message.setForeground(new Color(184, 134, 11)); // Dark goldenrod

        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Arial", Font.PLAIN, 16));
        okButton.setForeground(Color.BLACK);
        okButton.setBackground(new Color(255, 215, 0)); // Gold
        okButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        okButton.addActionListener(e -> dialog.dispose());

        panel.add(message, BorderLayout.CENTER);
        panel.add(okButton, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    public static void showCustomWarningMess(Component parent, String messageText, String titleText) {
        Frame owner = (parent != null) ? (Frame) SwingUtilities.getWindowAncestor(parent) : new JFrame();
        JDialog dialog = new JDialog(owner, true);
        dialog.setUndecorated(true);
        dialog.setSize(450, 150);
        dialog.setLocationRelativeTo(parent);

        Color background = new Color(255, 239, 184); // Soft gold
        Color titleColor = new Color(139, 69, 19);   // Saddle brown
        Color messageColor = new Color(165, 42, 42); // Brown-red

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(background);

        JLabel title = new JLabel(titleText, SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(titleColor);
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        panel.add(title, BorderLayout.NORTH);

        messageText = "<html><div style='text-align: center;'>" + messageText.replace("\n", "<br>") + "</div></html>";

        JLabel message = new JLabel(messageText, SwingConstants.CENTER);
        message.setFont(new Font("Arial", Font.PLAIN, 30));
        message.setForeground(messageColor);
        message.setBorder(BorderFactory.createEmptyBorder(5, 20, 10, 20));
        panel.add(message, BorderLayout.CENTER);

        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Arial", Font.BOLD, 18));
        okButton.setBackground(new Color(220, 20, 60)); // Crimson red
        okButton.setForeground(Color.WHITE);
        okButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        okButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(background);
        buttonPanel.add(okButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    // Custom information dialog
    public static void showCustomInformation(Component parent, String messageText, String titleText) {
        Window window = (parent != null) ? SwingUtilities.getWindowAncestor(parent) : null;
        JDialog dialog = new JDialog(window instanceof Frame ? (Frame) window : null, true);

        dialog.setUndecorated(true);
        dialog.setSize(400, 180);
        dialog.setLocationRelativeTo(parent);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 235, 205)); // Light orange
        panel.setLayout(new BorderLayout());

        JLabel title = new JLabel(titleText, SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 35));
        title.setForeground(new Color(220, 20, 60)); // Red
        panel.add(title, BorderLayout.NORTH);

        messageText = "<html><div align='center' style='color: #CC5500;'>" + messageText.replace("\n", "<br>") + "</div></html>";
        JLabel message = new JLabel(messageText, SwingConstants.CENTER);
        message.setFont(new Font("Arial", Font.PLAIN, 25));
        panel.add(message, BorderLayout.CENTER);

        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Arial", Font.BOLD, 18));
        okButton.setBackground(new Color(255, 215, 0)); // Yellow
        okButton.setForeground(new Color(139, 0, 0)); // Dark red
        okButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        okButton.addActionListener(e -> dialog.dispose());

        panel.add(okButton, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    public static int showCustomQuestionPanel(Component parent, JPanel contentPanel, String titleText) {
        final int[] result = { -1 };

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), true);
        dialog.setUndecorated(true);
        dialog.setSize(450, 250);
        dialog.setLocationRelativeTo(parent);

        // Main panel with a soft golden background
        JPanel panel = new JPanel(new BorderLayout());
        Color backgroundColor = new Color(255, 239, 184); // Soft gold
        panel.setBackground(backgroundColor);

        // Title
        JLabel title = new JLabel(titleText, SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setForeground(new Color(139, 69, 19)); // Saddle brown
        title.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));
        panel.add(title, BorderLayout.NORTH);

        // Content panel setup
        contentPanel.setBackground(backgroundColor);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(contentPanel, BorderLayout.CENTER);

        // Apply consistent style to all JLabels inside the content panel
        
        for (Component comp : contentPanel.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel lbl = (JLabel) comp;

                // Handle newline (\n) by converting to HTML <br> and wrapping in <html>
                String text = lbl.getText();
                if (text != null && text.contains("\n")) {
                    text = "<html>" + text.replace("\n", "<br>") + "</html>";
                    lbl.setText(text);
                }

                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(new Font("Arial", Font.PLAIN, 30));
                lbl.setForeground(new Color(165, 42, 42)); 
            }
        }

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(backgroundColor);

        JButton yesButton = new JButton("YES");
        yesButton.setBackground(new Color(255, 204, 0)); // Strong golden yellow
        yesButton.setForeground(new Color(102, 51, 0)); // Deep brown text
        yesButton.setFont(new Font("Arial", Font.BOLD, 22));
        yesButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        yesButton.addActionListener(e -> {
            result[0] = JOptionPane.YES_OPTION;
            dialog.dispose();
        });

        JButton noButton = new JButton("NO");
        noButton.setBackground(new Color(220, 20, 60)); // Crimson red
        noButton.setForeground(Color.WHITE);
        noButton.setFont(new Font("Arial", Font.BOLD, 22));
        noButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        noButton.addActionListener(e -> {
            result[0] = JOptionPane.NO_OPTION;
            dialog.dispose();
        });

        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);

        return result[0];
    }
    
    public static String showCustomInputDialog(Component parent, String questionText, String titleText) {
        final String[] inputResult = { null };

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), true);
        dialog.setUndecorated(true);
        dialog.setSize(450, 230);
        dialog.setLocationRelativeTo(parent);

        Color background = new Color(255, 239, 184); // Soft yellow
        Color titleColor = new Color(139, 69, 19);   // Saddle brown
        Color questionColor = new Color(165, 42, 42); // Brown-red

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(background);

        JLabel title = new JLabel(titleText, SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setForeground(titleColor);
        title.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));
        panel.add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(background);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel question = new JLabel("<html><div style='text-align:center;'>" + questionText + "</div></html>", SwingConstants.CENTER);
        question.setFont(new Font("Arial", Font.PLAIN, 22));
        question.setForeground(questionColor);

        JTextField inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.PLAIN, 20));
        inputField.setBorder(BorderFactory.createLineBorder(new Color(160, 82, 45), 1)); // Sienna

        centerPanel.add(question, BorderLayout.NORTH);
        centerPanel.add(inputField, BorderLayout.CENTER);
        panel.add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(background);

        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Arial", Font.BOLD, 20));
        okButton.setBackground(new Color(220, 20, 60)); // Crimson red
        okButton.setForeground(Color.WHITE);
        okButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        okButton.addActionListener(e -> {
            inputResult[0] = inputField.getText().trim();
            dialog.dispose();
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 20));
        cancelButton.setBackground(new Color(160, 82, 45)); // Sienna (warm brown)
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(e -> {
            inputResult[0] = null;
            dialog.dispose();
        });

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);

        return inputResult[0];
    }
    
    public static void showCustomMessage(Component parent, String messageText, String titleText) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), true);
        dialog.setUndecorated(true);
        dialog.setSize(400, 180);
        dialog.setLocationRelativeTo(parent);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 236, 179)); // Light yellow
        panel.setLayout(new BorderLayout());

        JLabel title = new JLabel(titleText, SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 35));
        title.setForeground(new Color(93, 64, 55)); // Brown
        panel.add(title, BorderLayout.NORTH);

        // Convert newlines to <br> for multi-line support
        messageText = "<html><div align='center'>" + messageText.replace("\n", "<br>") + "</div></html>";

        JLabel message = new JLabel(messageText, SwingConstants.CENTER);
        message.setFont(new Font("Arial", Font.PLAIN, 25));
        message.setForeground(new Color(93, 64, 55)); // Brown text
        panel.add(message, BorderLayout.CENTER);

        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Arial", Font.PLAIN, 18));
        okButton.setBackground(new Color(255, 204, 128)); // Soft orange/yellow
        okButton.setForeground(Color.BLACK);
        okButton.setFocusPainted(false);
        okButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        okButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(255, 236, 179));
        buttonPanel.add(okButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    public static String showCustomFinalQuestion(Component parent, String titleText) {
        final String[] userInput = {null};

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), true);
        dialog.setUndecorated(true);
        dialog.setSize(450, 250);
        dialog.setLocationRelativeTo(parent);

        // Main panel
        JPanel panel = new JPanel(new BorderLayout());
        Color backgroundColor = new Color(255, 239, 184); // Soft gold
        panel.setBackground(backgroundColor);

        // Title
        JLabel title = new JLabel(titleText + " (10 seconds remaining)", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(139, 69, 19)); // Saddle brown
        title.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        panel.add(title, BorderLayout.NORTH);

        // Input field
        JTextField inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.PLAIN, 20));
        inputField.setHorizontalAlignment(JTextField.CENTER);
        inputField.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(backgroundColor);
        centerPanel.add(inputField, BorderLayout.CENTER);
        panel.add(centerPanel, BorderLayout.CENTER);

        // Enter key listener
        inputField.addActionListener(e -> {
            userInput[0] = inputField.getText().trim();
            dialog.dispose();
        });

        // Countdown timer
        final int[] secondsRemaining = {10};
        Timer countdownTimer = new Timer(1000, e -> {
            secondsRemaining[0]--;
            title.setText(titleText + " (" + secondsRemaining[0] + " seconds remaining)");

            if (secondsRemaining[0] <= 0) {
                ((Timer) e.getSource()).stop();
                dialog.dispose(); // Close on timeout
            }
        });
        countdownTimer.start();

        // Stop timer if dialog is closed
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                countdownTimer.stop();
            }
        });

        dialog.add(panel);
        dialog.setVisible(true);

        return userInput[0]; // null if timed out, or the user's input
    }

    
    
}