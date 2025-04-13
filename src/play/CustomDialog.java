package play;

import javax.swing.*;
import java.awt.*;

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
        panel.setBackground(new Color(240, 240, 255)); // light bluish
        panel.setLayout(new BorderLayout());

        JLabel title = new JLabel(titleText, SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 40));
        title.setForeground(new Color(60, 60, 120));
        panel.add(title, BorderLayout.NORTH);

        // Convert newlines to <br> for multi-line support
        messageText = "<html><div align='center'>" + messageText.replace("\n", "<br>") + "</div></html>";

        JLabel message = new JLabel(messageText, SwingConstants.CENTER);
        message.setFont(new Font("Arial", Font.PLAIN, 25));
        message.setForeground(new Color(80, 80, 80));
        panel.add(message, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 240, 255));

        JButton yesButton = new JButton("YES");
        yesButton.setBackground(new Color(144, 238, 144)); // light green
        yesButton.setFont(new Font("Arial", Font.BOLD, 25));
        yesButton.setForeground(Color.BLACK);
        yesButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        yesButton.addActionListener(e -> {
            result[0] = JOptionPane.YES_OPTION;
            dialog.dispose();
        });

        JButton noButton = new JButton("NO");
        noButton.setBackground(new Color(255, 182, 193)); // light red/pink
        noButton.setFont(new Font("Arial", Font.BOLD, 25));
        noButton.setForeground(Color.BLACK);
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

    // Custom information dialog
    public static void showCustomInformation(Component parent, String messageText, String titleText) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), true);
        dialog.setUndecorated(true);
        dialog.setSize(400, 180);
        dialog.setLocationRelativeTo(parent);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(225, 245, 254)); // light blue
        panel.setLayout(new BorderLayout());

        JLabel title = new JLabel(titleText, SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 35));
        title.setForeground(new Color(25, 118, 210)); // blue
        panel.add(title, BorderLayout.NORTH);

        // Convert newlines to <br> for multi-line support
        messageText = "<html><div align='center'>" + messageText.replace("\n", "<br>") + "</div></html>";

        JLabel message = new JLabel(messageText, SwingConstants.CENTER);
        message.setFont(new Font("Arial", Font.PLAIN, 25));
        message.setForeground(new Color(33, 33, 33));
        panel.add(message, BorderLayout.CENTER);

        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Arial", Font.PLAIN, 18));
        okButton.setBackground(new Color(144, 202, 249)); // pastel blue
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

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255)); // Light blue

        JLabel title = new JLabel(titleText, SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setForeground(new Color(25, 25, 112)); // Midnight blue
        title.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));
        panel.add(title, BorderLayout.NORTH);

        // Embed your custom content panel
        contentPanel.setBackground(new Color(240, 248, 255)); // Match background
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(contentPanel, BorderLayout.CENTER);

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
                lbl.setForeground(new Color(25, 25, 112));
            }
        }

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 248, 255));

        JButton yesButton = new JButton("YES");
        yesButton.setBackground(new Color(144, 238, 144)); // Light green
        yesButton.setFont(new Font("Arial", Font.BOLD, 22));
        yesButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        yesButton.addActionListener(e -> {
            result[0] = JOptionPane.YES_OPTION;
            dialog.dispose();
        });

        JButton noButton = new JButton("NO");
        noButton.setBackground(new Color(255, 182, 193)); // Light pink
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
        dialog.setSize(450, 220);
        dialog.setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255)); // Soft blue

        JLabel title = new JLabel(titleText, SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setForeground(new Color(25, 25, 112)); // Midnight blue
        title.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));
        panel.add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(240, 248, 255));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel question = new JLabel("<html><div style='text-align:center;'>" + questionText + "</div></html>", SwingConstants.CENTER);
        question.setFont(new Font("Arial", Font.PLAIN, 22));
        question.setForeground(new Color(50, 50, 50));

        JTextField inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.PLAIN, 20));
        inputField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        centerPanel.add(question, BorderLayout.NORTH);
        centerPanel.add(inputField, BorderLayout.CENTER);
        panel.add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 248, 255));

        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Arial", Font.BOLD, 20));
        okButton.setBackground(new Color(144, 238, 144));
        okButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        okButton.addActionListener(e -> {
            inputResult[0] = inputField.getText().trim();
            dialog.dispose();
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 20));
        cancelButton.setBackground(new Color(255, 182, 193));
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
}
