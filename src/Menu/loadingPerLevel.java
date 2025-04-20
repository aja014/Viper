/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Menu;

//import static fistFrame.opening.LOADING_DELAY;
//import static fistFrame.opening.SCREEN_HEIGHT;
//import static fistFrame.opening.SCREEN_WIDTH;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import play.Minesweeper;

/**
 *
 * @author Asus
 */
public class loadingPerLevel extends JFrame {

    public static final int SCREEN_WIDTH = 1200;
    public static final int SCREEN_HEIGHT = 720;
    public static final int SPLASH_DELAY = 20;
    public static final int LOADING_DELAY = 50;

    public final Font ButtonFont = new Font("Verdana", Font.BOLD, 14);
    public final Font ProgressBar = new Font("Verdana", Font.BOLD, 18);
    public final Font infoFont = new Font("Verdana", Font.BOLD, 15);
    public final Color navyBlue = new Color(0, 0, 128);

    public JProgressBar progressBar;
    public JLabel lbLogo;
    public JButton proceedButton;
    public JPanel topPanel, bottomPanel;
    public Timer splashTimer, progressTimer;
    public int splashHeight = SCREEN_HEIGHT / 2;

    public JLabel loadingText;
    private int currentDots;
    private String currentLoadingMessage;
    private Timer dotsTimer;

    String level = "";

    public loadingPerLevel(String level) {
        this.level = level;
        lbLogo = new JLabel();
        ImageIcon VSBG;

        // Different kinds of bg dito
        if (level.equals("easy")) {
            VSBG = new ImageIcon(getClass().getResource("/images/easymode.gif"));// Modify mo path
        } else if (level.equals("medium")) {
            VSBG = new ImageIcon(getClass().getResource("/images/mediummode.gif"));// Modify mo path
        } else if (level.equals("hard")) {
            VSBG = new ImageIcon(getClass().getResource("/images/hardmode.gif"));// Modify mo path
        } else if (level.equals("blitz")) {
            VSBG = new ImageIcon(getClass().getResource("/images/blitzmode.gif"));// Modify mo path
        } else {
            VSBG = new ImageIcon(getClass().getResource("/images/splashscreen.gif"));//  default path
        }
        ImageIcon frameIcon = new ImageIcon(getClass().getResource("/images/virusSweeperIcon.png"));
        setIconImage(frameIcon.getImage());

        lbLogo.setIcon(VSBG);
        lbLogo.setHorizontalAlignment(SwingConstants.CENTER);
        lbLogo.setVerticalAlignment(SwingConstants.CENTER);
        lbLogo.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        setTitle("Viper");
        setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        layeredPane.add(lbLogo, Integer.valueOf(0));

//        topPanel = new JPanel();
//        topPanel.setBackground(Color.white);
//        topPanel.setBounds(0, 0, SCREEN_WIDTH, splashHeight);
//        layeredPane.add(topPanel, Integer.valueOf(1));
//
//        bottomPanel = new JPanel();
//        bottomPanel.setBackground(Color.white);
//        bottomPanel.setBounds(0, SCREEN_HEIGHT - splashHeight, SCREEN_WIDTH, splashHeight);
//        layeredPane.add(bottomPanel, Integer.valueOf(1));
        JLabel verControl = new JLabel("Version 1.25.0");
//        JLabel loadingText = new JLabel("Initializing Objects");
        verControl.setFont(infoFont);
        verControl.setForeground(Color.WHITE);
        verControl.setBounds(1000, 600, 300, 50);
        layeredPane.add(verControl, Integer.valueOf(1));

        loadingText = new JLabel();
        loadingText.setBounds(380, 520, 700, 25); // Centered below progress bar
        loadingText.setHorizontalAlignment(SwingConstants.CENTER);
        loadingText.setFont(ProgressBar);
        loadingText.setForeground(Color.WHITE);
        layeredPane.add(loadingText, JLayeredPane.PALETTE_LAYER);

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(255, 186, 186));
        progressBar.setBackground(Color.white);
        progressBar.setFont(ProgressBar);
        progressBar.setBounds(380, 470, 700, 40);
        progressBar.setVisible(false);
        layeredPane.add(progressBar, Integer.valueOf(1));

//        proceedButton = new JButton("Proceed");
//        proceedButton.setFont(ButtonFont);
//        proceedButton.setForeground(Color.WHITE);
//        proceedButton.setBackground(new Color(255, 123, 123));
//        proceedButton.setBounds(650, 540, 150, 40);
//        proceedButton.setVisible(false);
//        proceedButton.addActionListener(this);
//        layeredPane.add(proceedButton, Integer.valueOf(1));
//        add(loadingText);
        add(layeredPane);
        setVisible(true);

        currentLoadingMessage = "Initializing Objects";
        currentDots = 0;
        setupDotsAnimation();
        intro();
    }

    private void setupDotsAnimation() {
        dotsTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentDots = (currentDots + 1) % 3;
                StringBuilder dots = new StringBuilder();
                for (int i = 0; i <= currentDots; i++) {
                    dots.append(".");
                }
                loadingText.setText(currentLoadingMessage + dots);
            }
        });
        dotsTimer.start();
    }

//    public void intro() {
//        Timer splashTimer = new Timer(SPLASH_DELAY, new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent ev) {
//                ((Timer) ev.getSource()).stop();
//                loadingProgress();
//            }
//        });
//        splashTimer.start();
//    }
    public void intro() {
        splashTimer = new Timer(SPLASH_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ev) {
                splashHeight -= 10;
//                topPanel.setBounds(0, 0, SCREEN_WIDTH, splashHeight);
//                bottomPanel.setBounds(0, SCREEN_HEIGHT - splashHeight, SCREEN_WIDTH, splashHeight);

//                if (splashHeight <= 0) {
                ((Timer) ev.getSource()).stop();
                loadingProgress();
//                }
            }
        });
        splashTimer.start();
    }

    public void loadingProgress() {
        progressBar.setVisible(true);
        loadingText.setText(currentLoadingMessage + ".");
        setupDotsAnimation();

        progressTimer = new Timer(LOADING_DELAY, new ActionListener() {
            private int progress = 0;

            @Override
            public void actionPerformed(ActionEvent ev) {
                if (progress <= 100) {
                    progress += 1;
                    progressBar.setValue(progress);
                }

                // Pause points
                if (progress <= 100) {
                    progressTimer.stop();
                    int delay = 0;

                    if (progress == 20 || progress == 21) {
                        delay = 500;
                        currentLoadingMessage = "Loading assets";
                        loadingText.setText(currentLoadingMessage + ".");
                    } else if (progress >= 30 && progress <= 38) {
                        delay = 50;
                        currentLoadingMessage = "Optimizing resources";
                        loadingText.setText(currentLoadingMessage + ".");
                    } else if (progress >= 50 && progress <= 56) {
                        currentLoadingMessage = "Setting tiles";
                        loadingText.setText(currentLoadingMessage + ".");
                        switch (progress) {
                            case 50:
                                delay = 1300;
                                break;
                            case 56:
                                delay = 1300;
                                break;
                            case 52:
                                delay = 1000;
                                break;
                            case 54:
                                delay = 1000;
                                currentLoadingMessage = "Setting up sounds";
                                loadingText.setText(currentLoadingMessage + ".");
                                break;
                            default:
                                delay = 100;
                                break;
                        }
                    } else if (progress >= 65 && progress <= 66) {
                        delay = 1000;
                        currentLoadingMessage = "Setting up viruses";
                        loadingText.setText(currentLoadingMessage + ".");
                    } else if (progress == 75) {
                        delay = 300; // 2 seconds delay at 75%
                    } else if (progress == 86) {
                        delay = 200; // 1 second delay at 99%
                        currentLoadingMessage = "Setting up Bonuses";
                        loadingText.setText(currentLoadingMessage + ".");
                    } else if (progress == 99) {
                        delay = 5000; // 1 second delay at 99%
                        currentLoadingMessage = "Almost there";
                        loadingText.setText(currentLoadingMessage + ".");
                    } else if (progress == 100) {
                        delay = 1000; // 1 second delay at 99%
                        currentLoadingMessage = "Launching";
                        loadingText.setText(currentLoadingMessage + ".");
                    }
                    Timer delayTimer = new Timer(delay, (e) -> {
                        progressTimer.start();
                    });
                    delayTimer.setRepeats(false);
                    delayTimer.start();
                }

                if (progress > 100) {
//                    loadingText.setText("Done!");
                    ((Timer) ev.getSource()).stop();
                    dispose();
                    if (level.equals("easy")) {
                        Minesweeper easy = new Minesweeper(10, 9, 15, 1, "");
                    } else if (level.equals("medium")) {
                        Minesweeper medium = new Minesweeper(20, 15, 25, 3, "");
                    } else if (level.equals("hard")) {
                        Minesweeper hard = new Minesweeper(30, 21, 45, 5, "");
                    } else if (level.equals("blitz")) {
                        Minesweeper blitz = new Minesweeper(15, 9, 20, 3, "blitz");
                    }
                }
            }
        });
        progressTimer.start();
    }

//    public void endSplashScreen() {
//        Timer splashTimer = new Timer(SPLASH_DELAY, new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent ev) {
//                splashHeight += 10;
//                topPanel.setBounds(0, 0, SCREEN_WIDTH, splashHeight);
//                bottomPanel.setBounds(0, SCREEN_HEIGHT - splashHeight, SCREEN_WIDTH, splashHeight);
//
//                if (splashHeight >= SCREEN_HEIGHT / 2) {
//                    ((Timer) ev.getSource()).stop();
//                    dispose();
//                    dispose();
////               new JavaApply();
//                    /*if (Database.getAccess()) {
//                   new JavaApply();
//                }
//                else {
//                    Login.openLogin();
//                }*/
//                }
//            }
//        });
//
//        splashTimer.start();
//    }
//    @Override
//    public void actionPerformed(ActionEvent ev) {
//        if (ev.getSource() == proceedButton) {
//            dispose();
//            new Login();
//        }
//    }
    
    private void showCustomError(String messageText) {
        javax.swing.JDialog dialog = new javax.swing.JDialog(this, true);
        dialog.setUndecorated(true);
        dialog.setSize(400, 150);
        dialog.setLocationRelativeTo(this);

        javax.swing.JPanel panel = new javax.swing.JPanel();
        panel.setBackground(new java.awt.Color(255, 240, 245)); // Light pink
        panel.setLayout(new java.awt.BorderLayout());

        javax.swing.JLabel message = new javax.swing.JLabel(messageText, javax.swing.SwingConstants.CENTER);
        message.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 30));
        message.setForeground(java.awt.Color.RED);

        javax.swing.JButton okButton = new javax.swing.JButton("OK");
        okButton.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 18));
        okButton.setBackground(new java.awt.Color(255, 182, 193)); // Soft pastel red-pink
        okButton.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 18));
        okButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        okButton.addActionListener(e -> dialog.dispose());

        panel.add(message, java.awt.BorderLayout.CENTER);
        panel.add(okButton, java.awt.BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void showCustomSuccess(String messageText) {
        javax.swing.JDialog dialog = new javax.swing.JDialog(this, true);
        dialog.setUndecorated(true);
        dialog.setSize(400, 150);
        dialog.setLocationRelativeTo(this);

        javax.swing.JPanel panel = new javax.swing.JPanel();
        panel.setBackground(new java.awt.Color(220, 255, 220)); // Soft green
        panel.setLayout(new java.awt.BorderLayout());

        javax.swing.JLabel message = new javax.swing.JLabel(messageText, javax.swing.SwingConstants.CENTER);
        message.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 30));
        message.setForeground(new java.awt.Color(34, 139, 34)); // Forest green

        javax.swing.JButton okButton = new javax.swing.JButton("OK");
        okButton.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 18));
        okButton.setBackground(new java.awt.Color(144, 238, 144)); // Light green
        okButton.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 18));
        okButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        okButton.addActionListener(e -> dialog.dispose());

        panel.add(message, java.awt.BorderLayout.CENTER);
        panel.add(okButton, java.awt.BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private int showCustomConfirmation(String messageText, String titleText) {
        final int[] result = { -1 };

        javax.swing.JDialog dialog = new javax.swing.JDialog(this, true);
        dialog.setUndecorated(true);
        dialog.setSize(400, 180);
        dialog.setLocationRelativeTo(this);

        javax.swing.JPanel panel = new javax.swing.JPanel();
        panel.setBackground(new java.awt.Color(240, 240, 255)); // light bluish
        panel.setLayout(new java.awt.BorderLayout());

        javax.swing.JLabel title = new javax.swing.JLabel(titleText, javax.swing.SwingConstants.CENTER);
        title.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 40));
        title.setForeground(new java.awt.Color(60, 60, 120));
        panel.add(title, java.awt.BorderLayout.NORTH);

        javax.swing.JLabel message = new javax.swing.JLabel(messageText, javax.swing.SwingConstants.CENTER);
        message.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 25));
        message.setForeground(new java.awt.Color(80, 80, 80));
        panel.add(message, java.awt.BorderLayout.CENTER);

        javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
        buttonPanel.setBackground(new java.awt.Color(240, 240, 255));

        javax.swing.JButton yesButton = new javax.swing.JButton("YES");
        yesButton.setBackground(new java.awt.Color(144, 238, 144)); // light green
        yesButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 25));
        yesButton.setForeground(Color.BLACK);
        yesButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        yesButton.addActionListener(e -> {
            result[0] = JOptionPane.YES_OPTION;
            dialog.dispose();
        });

        javax.swing.JButton noButton = new javax.swing.JButton("NO");
        noButton.setBackground(new java.awt.Color(255, 182, 193)); // light red/pink
        noButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 25));
        noButton.setForeground(Color.BLACK);
        noButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        noButton.addActionListener(e -> {
            result[0] = JOptionPane.NO_OPTION;
            dialog.dispose();
        });

        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);
        panel.add(buttonPanel, java.awt.BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);

        return result[0];
    }
    
    public static void main(String[] args) {
        new loadingPerLevel("");
    }
}
