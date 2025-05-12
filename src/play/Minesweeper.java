package play;

import Db.database;
import Menu.*;
import Menu.playFrame;
import fistFrame.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;
import play.CustomDialog;

// STILL FIND BUG IN BONUS TILE PLACEMENT
public class Minesweeper implements ActionListener {

    // adding new class with overriding the Jbutton(row number, col number)
    private class Minetile extends JButton {

        int r, c;

        public Minetile(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }
    private ImageIcon closedTileIcon;
    private ImageIcon openedTileIcon;
    private ImageIcon crack1;
    private ImageIcon crack2;
    private ImageIcon crack3;
    private Timer crackingTimer;
    private JLabel characterLabel;
    private ImageIcon standbyGif;
    private ImageIcon diggingGif;
    private Timer returnTimer;
    private boolean isDigging = false;
    private Point currentTileLocation;
    int characterWIDTH = 0;
    int characterHEIGHT = 0;
    private ImageIcon flagIcon;
    private ImageIcon questionIcon;
    private ImageIcon virusIcon;
    String questionLevel = "";
    String[][] questionList;
    String tempQuestion = "";
    String tempAnswer = "";
    String finalquestion = "";
    String blitzScore = "";

    public String level = "";
    public int scoreTimeStart;
    public int timePerScore;

//    private int lastBonusTime = -60; // Track the last time a bonus was added
    private Minetile currentBonusTile = null; // Track the current bonus tile

    int tileSize = 50;
    int numRows;
    int mineCount;
    int numCols;
    int boardWidth;
    int boardHeight;
//    int tilesClicked = 0;
    int elapsedTime = 0;
    int elapsedTime2 = 0;
    int timeScore;
    int coinsEarned = 50;
    int questionTiles;
    int newMine = 0;
    int flagged = 0;
    int lastBombTime = -60;
    int bonusTiles;
    int timePaused = 0;
//    int power1 = 99;
//    int power2 = 99;
//    int power3 = 99;
    int power1 = database.getIntRecord("power1", menu.username);
    int power2 = database.getIntRecord("power2", menu.username);
    int power3 = database.getIntRecord("power3", menu.username);

    Timer timer;
    Timer removeTimer;
    Timer pauseTimer;
    Timer power1Timer;
    Timer countdownTimer;
    JFrame frame = new JFrame("Viper");
    JLabel timerLabel = new JLabel("Time : 0s");
    JLabel textLabel = new JLabel(); // number of mines
    JLabel textLabel1 = new JLabel(); // "Mines Cleared"
    JLabel textLabelFlagged = new JLabel(); // flagged
    JLabel textLabelMineCount = new JLabel();
    JLabel scoreLabel = new JLabel(Integer.toString(timeScore));
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JPanel outerPanel = new JPanel(new BorderLayout());
    JButton BtnBack = new JButton("");
    JButton BtnRestart = new JButton("");
    JButton power1Btn = new JButton("");
    JButton power2Btn = new JButton("");
    JLabel power1Lbl = new JLabel("Power1");
    JLabel power2Lbl = new JLabel("Power2");
    JLabel power3Lbl = new JLabel("Power3");

    // Reference of the tile we create
    Minetile[][] position;
    Minetile firstClick = null; // first click tile

    Random random = new Random();

    ArrayList<Minetile> bonusTileList = new ArrayList<>();
    ArrayList<Minetile> minelist; // List of mines
    ArrayList<Minetile> questionTileList; // List of bonus tile

    boolean gameOver = false;
    boolean start = false;
    boolean gameLost = false;
    boolean swapBonusWithBomb = false;
    boolean allowReveal3x3 = false;
    
    public void playSound(String soundFileName) 
    {
        try {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundFileName).getAbsoluteFile());
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.start();
            } 
        catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) 
        {
        ex.printStackTrace();
        }
    }

    // Minesweeper(int minecout, int rowsNcols, int scoreTimeStart, int timePerScore)
    public Minesweeper(int minecount, int rowsNcols, int scoreTimeStart, int timePerScore, String gameMode) {
        this.level = gameMode;
        this.mineCount = minecount;
        this.numRows = rowsNcols;
        this.scoreTimeStart = scoreTimeStart;
        this.timePerScore = timePerScore;
        numCols = numRows;
        position = new Minetile[numRows][numCols];
        boardWidth = 1200;
        boardHeight = 720;
        timeScore = 100;
        Color darkPinkRed = new Color(250, 20, 60);
        Color darkRed = new Color(240, 20, 60);

        flagIcon = new ImageIcon(getClass().getResource("/images/flag.png"));
        questionIcon = new ImageIcon(getClass().getResource("/images/question.png"));
        virusIcon = new ImageIcon(getClass().getResource("/images/virus.png"));

        standbyGif = new ImageIcon(getClass().getResource("/images/character_0_standby.gif"));
        diggingGif = new ImageIcon(getClass().getResource("/images/character_0_digging.gif"));

        if (mineCount == 10) {

            closedTileIcon = new ImageIcon(getClass().getResource("/images/easyclosed.png"));
            openedTileIcon = new ImageIcon(getClass().getResource("/images/easyopened.png"));
            closedTileIcon = new ImageIcon(closedTileIcon.getImage().getScaledInstance(95, 53, Image.SCALE_DEFAULT));
            openedTileIcon = new ImageIcon(openedTileIcon.getImage().getScaledInstance(95, 53, Image.SCALE_DEFAULT));
            flagIcon = new ImageIcon(flagIcon.getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT));
            questionIcon = new ImageIcon(questionIcon.getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT));
            virusIcon = new ImageIcon(virusIcon.getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT));
            characterWIDTH = 140;
            characterHEIGHT = 140;
        } else if (mineCount == 20) {
            closedTileIcon = new ImageIcon(getClass().getResource("/images/mediumclosed.png"));
            openedTileIcon = new ImageIcon(getClass().getResource("/images/mediumopened.png"));
            closedTileIcon = new ImageIcon(closedTileIcon.getImage().getScaledInstance(56, 31, Image.SCALE_DEFAULT));
            openedTileIcon = new ImageIcon(openedTileIcon.getImage().getScaledInstance(56, 31, Image.SCALE_DEFAULT));
            flagIcon = new ImageIcon(flagIcon.getImage().getScaledInstance(35, 35, Image.SCALE_DEFAULT));
            questionIcon = new ImageIcon(questionIcon.getImage().getScaledInstance(35, 35, Image.SCALE_DEFAULT));
            virusIcon = new ImageIcon(virusIcon.getImage().getScaledInstance(35, 35, Image.SCALE_DEFAULT));
            characterWIDTH = 120;
            characterHEIGHT = 120;
        } else if (mineCount == 30) {
            closedTileIcon = new ImageIcon(getClass().getResource("/images/hardclosed.png"));
            openedTileIcon = new ImageIcon(getClass().getResource("/images/hardopened.png"));           
            closedTileIcon = new ImageIcon(closedTileIcon.getImage().getScaledInstance(39, 21, Image.SCALE_DEFAULT));
            openedTileIcon = new ImageIcon(openedTileIcon.getImage().getScaledInstance(39, 21, Image.SCALE_DEFAULT));
            flagIcon = new ImageIcon(flagIcon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
            questionIcon = new ImageIcon(questionIcon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
            virusIcon = new ImageIcon(virusIcon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));;
            characterWIDTH = 100;
            characterHEIGHT = 100;
        } else if (mineCount == 15) {
            closedTileIcon = new ImageIcon(getClass().getResource("/images/blitzclosed.png"));
            openedTileIcon = new ImageIcon(getClass().getResource("/images/blitzopened.png"));          
            closedTileIcon = new ImageIcon(closedTileIcon.getImage().getScaledInstance(95, 53, Image.SCALE_DEFAULT));
            openedTileIcon = new ImageIcon(openedTileIcon.getImage().getScaledInstance(95, 53, Image.SCALE_DEFAULT));
            flagIcon = new ImageIcon(flagIcon.getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT));
            questionIcon = new ImageIcon(questionIcon.getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT));
            virusIcon = new ImageIcon(virusIcon.getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT));
            characterWIDTH = 140;
            characterHEIGHT = 140;
        }
        standbyGif = new ImageIcon(standbyGif.getImage().getScaledInstance(characterHEIGHT, characterWIDTH, Image.SCALE_DEFAULT));
        diggingGif = new ImageIcon(diggingGif.getImage().getScaledInstance(characterHEIGHT, characterWIDTH, Image.SCALE_DEFAULT));

        // Scale the images to the new size
        characterLabel = new JLabel(standbyGif);
        characterLabel.setSize(characterWIDTH, characterHEIGHT);
        characterLabel.setLocation(boardWidth / 3 - characterWIDTH / 2, boardHeight - characterHEIGHT);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());

        // "Minesweeper" Text Panel
        textLabel.setFont(new Font("Arial", Font.BOLD, 40));
        textLabel.setText(Integer.toString(mineCount + newMine));
        textLabel.setOpaque(true);
        textLabel.setBounds(885, 12, 200, 40);
        textLabel.setOpaque(false);
        textLabel.setForeground(darkPinkRed);

        // Gameover Text
        textLabel1.setFont(new Font("Arial", Font.BOLD, 25));
        textLabel1.setText("");
        textLabel1.setOpaque(true);
        textLabel1.setBounds(30, 70, 650, 40);
        textLabel1.setOpaque(false);
        textLabel1.setForeground(darkRed);

        // Flagged text
        textLabelFlagged.setFont(new Font("Arial", Font.BOLD, 40));
        textLabelFlagged.setText(Integer.toString(flagged));
        textLabelFlagged.setOpaque(true);
        textLabelFlagged.setBounds(980, 12, 200, 40);
        textLabelFlagged.setOpaque(false);
        textLabelFlagged.setForeground(darkPinkRed);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(boardWidth, boardHeight));
        layeredPane.setLayout(null); // Very important for absolute positioning

        ImageIcon VSBG;
        if (mineCount == 10) {
            VSBG = new ImageIcon(getClass().getResource("/images/easymodeenvi.png"));
        } else if (mineCount == 20) {
            VSBG = new ImageIcon(getClass().getResource("/images/mediummodeenvi.png"));
        } else if (mineCount == 30) {
            VSBG = new ImageIcon(getClass().getResource("/images/hardmodeenvi.png"));
        } else if (mineCount == 15) {
            VSBG = new ImageIcon(getClass().getResource("/images/blitzmodeenvi.png"));
        } else {
            VSBG = new ImageIcon(getClass().getResource("/images/splashscreen.gif"));
        }

        JLabel backgroundLabel = new JLabel(VSBG);
        backgroundLabel.setBounds(0, 0, 1200, 720);
        layeredPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER); // Background at base layer
        //character
        layeredPane.add(characterLabel, JLayeredPane.MODAL_LAYER);

        boardPanel.setBounds(0, 0, boardWidth, boardHeight); // Adjust if needed
        layeredPane.add(boardPanel, JLayeredPane.PALETTE_LAYER); // Add above background

        //boardPanel.setOpaque(false);
        //boardPanel.setBackground(new Color(0, 0, 0, 0)); // Fully transparent
        boardPanel.setOpaque(true);
        boardPanel.setBackground(Color.PINK); // test color

        textPanel.setBounds(0, 0, boardWidth, 100);
        layeredPane.add(textPanel, JLayeredPane.MODAL_LAYER);

        textPanel.setOpaque(false);

        frame.setContentPane(layeredPane);
        frame.pack();

        ImageIcon frameIcon = new ImageIcon(getClass().getResource("/images/virusSweeperIcon.png"));
        frame.setIconImage(frameIcon.getImage()); // Correct usage

        //timer
        timerLabel.setFont(new Font("Arial", Font.BOLD, 40));
        timerLabel.setBounds(1085, 12, 200, 40);
        timerLabel.setForeground(darkPinkRed);

        // Board panel
        boardPanel.setLayout(new GridLayout(numRows, numCols)); // Declaring tiles rows and colums

        // Outer Panel
        outerPanel.setBorder(BorderFactory.createEmptyBorder(120, 80, 100, 240));
        //    outerPanel.add(boardPanel, BorderLayout.CENTER);

        outerPanel.setLayout(new BorderLayout()); // <-- THIS IS MISSING!
        outerPanel.setBounds(0, 0, boardWidth, boardHeight); // Ensure it's the right size
        outerPanel.setOpaque(false); // Optional if background shows through

        // Add boardPanel to outerPanel
        outerPanel.add(boardPanel, BorderLayout.CENTER);

        // Add outerPanel to layeredPane (on higher layer than background)
        layeredPane.add(outerPanel, JLayeredPane.PALETTE_LAYER);

        scoreLabel.setFont(new Font("Arial", Font.BOLD, 40));
        scoreLabel.setForeground(darkPinkRed);

        BtnBack.setContentAreaFilled(false);
        BtnBack.setBorderPainted(false);
        BtnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));

        BtnRestart.setContentAreaFilled(false);
        BtnRestart.setBorderPainted(false);
        BtnRestart.setCursor(new Cursor(Cursor.HAND_CURSOR));

        power1Btn.setContentAreaFilled(false);
        power1Btn.setBorderPainted(false);
        power1Btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        power2Btn.setContentAreaFilled(false);
        power2Btn.setBorderPainted(false);
        power2Btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        //Button
        BtnBack.setBounds(45, 3, 60, 55);
        BtnRestart.setBounds(130, 3, 60, 55);
        scoreLabel.setBounds(300, 12, 200, 40);
        power1Btn.setBounds(1092, 337, 65, 62);
        power2Btn.setBounds(1092, 435, 65, 60);
        power1Lbl.setBounds(1115, 402, 60, 30);
        power2Lbl.setBounds(1115, 495, 60, 30);
        power3Lbl.setBounds(1115, 590, 60, 30);

        //Powers
        power1Btn.setText("");
        power2Btn.setText("");
        BtnBack.setText("");
        BtnRestart.setText("");
        power1Lbl.setText(String.valueOf(power1));
        power1Lbl.setFont(new Font("Arial", Font.BOLD, 30));
        power1Lbl.setForeground(darkPinkRed);
        power2Lbl.setText(String.valueOf(power2));
        power2Lbl.setFont(new Font("Arial", Font.BOLD, 30));
        power2Lbl.setForeground(darkPinkRed);
        power3Lbl.setText(String.valueOf(power3));
        power3Lbl.setFont(new Font("Arial", Font.BOLD, 30));
        power3Lbl.setForeground(darkPinkRed);

        BtnBack.addActionListener(this);
        BtnRestart.addActionListener(this);
        power1Btn.addActionListener(this);
        power2Btn.addActionListener(this);
//        power3Btn.addActionListener(this);

        /* frame.add(power1Btn);
        frame.add(power2Btn);
        frame.add(power3Lbl);
        frame.add(scoreLabel);
        frame.add(timerLabel);
        frame.add(textLabel);
        frame.add(textLabel1);
        frame.add(textLabelFlagged);
        frame.add(BtnRestart);
        frame.add(BtnBack);
        frame.add(outerPanel); */
        layeredPane.add(power1Btn, JLayeredPane.MODAL_LAYER);
        layeredPane.add(power2Btn, JLayeredPane.MODAL_LAYER);
        layeredPane.add(power1Lbl, JLayeredPane.MODAL_LAYER);
        layeredPane.add(power2Lbl, JLayeredPane.MODAL_LAYER);
        layeredPane.add(power3Lbl, JLayeredPane.MODAL_LAYER);
        layeredPane.add(scoreLabel, JLayeredPane.MODAL_LAYER);
        layeredPane.add(timerLabel, JLayeredPane.MODAL_LAYER);
        layeredPane.add(textLabel, JLayeredPane.MODAL_LAYER);
        layeredPane.add(textLabel1, JLayeredPane.MODAL_LAYER);
        layeredPane.add(textLabelFlagged, JLayeredPane.MODAL_LAYER);
        layeredPane.add(BtnRestart, JLayeredPane.MODAL_LAYER);
        layeredPane.add(BtnBack, JLayeredPane.MODAL_LAYER);
        // layeredPane.add(outerPanel, JLayeredPane.PALETTE_LAYER);

        // Initialize timer variables based on mode
        int minutes = elapsedTime / 60;
        int seconds = elapsedTime % 60;
        if ("blitz".equals(level)) {
            elapsedTime = 60; // Start at 60 seconds
            timeScore = 100;

            // Set initial timer text to MM:SS
            if (!start) {
                timerLabel.setText("01:00");
            } else {
                timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
            }
        } else {
            elapsedTime = 0;
            timeScore = 100;
            timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
        }

        questionLevel = "";
        if (numRows == 9 && level != "blitz") {
            questionLevel = "easy";
        } else if (numRows == 15) {
            questionLevel = "medium";
        } else if (numRows == 21) {
            questionLevel = "hard";
        }
        // declare the questions
        questionList = generateQuestion(questionLevel, "spreadingvirus");

        // timer score function
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // BLITZ MODE
                if ("blitz".equals(level)) {
                    // Blitz mode countdown logic
                    elapsedTime--; // Decrement time
                    timeScore = (elapsedTime * 100) / 60; // Calculate score based on remaining time
                    updateCoinsBasedOnScore();

//                    timeScore = elapsedTime;
                    if (elapsedTime <= 0) {
                        CustomDialog.showCustomError(frame, "Time's up!\nGame Over.");
                        //JOptionPane.showMessageDialog(frame, "Time's up! Game Over.", "Game Over", JOptionPane.ERROR_MESSAGE);
                        textLabel1.setText("Game Over: Time's up");
                        timer.stop();
                        gameOver = true;
                        revealMines();
//                        return;
                    }
                } else {
                    elapsedTime++; // Increment time
                    elapsedTime2++;

                    // Decrement score based on scoreTimeStart and timePerScore
                    if (elapsedTime >= scoreTimeStart) {
                        if (elapsedTime == scoreTimeStart) {
                            timeScore--;
                            elapsedTime2 = 0;
                        } else if (elapsedTime2 % timePerScore == 0) {
                            if (timeScore > 0) {
                                timeScore--;
                            }
                            updateCoinsBasedOnScore();
                        }
                    }

                    // Adds new bomb every 60 seconds
                    if (elapsedTime > 60 && (start && elapsedTime - lastBombTime >= 60)) {

                        // Game over, swap bonus with mines if naabutan
                        if (isNoMoreSafeTile()) {
                            playSound("gameOver.wav");
                            revealMines();
                            CustomDialog.showCustomError(null, "Game Over:\nLast safe tile is now\ncontained with Virus");
                            //JOptionPane.showMessageDialog(null, "Game Over: Last safe tile is now contained with Virus", "Game Over", JOptionPane.ERROR_MESSAGE);
                            textLabel1.setText("Game Over: You failed to prevent virus");
//                        newMine++;
                            textLabel.setText(Integer.toString(mineCount + newMine));

                            ArrayList<Minetile> availableTiles2 = new ArrayList<>();
                            for (int r = 0; r < numRows; r++) {
                                for (int c = 0; c < numCols; c++) {
                                    Minetile tile = position[r][c];
                                    if (tile.getText() == "★") {
                                        tile.setText("#");
                                        tile.setForeground(Color.red);
                                    }

                                    if (tile.isEnabled() && !questionTileList.contains(tile)) {
                                        swapBonusWithBomb = true;
                                        tile.setText("#");
                                        tile.setForeground(Color.red);
//                                    tile.setBackground(Color.black);
                                        availableTiles2.add(tile);
                                    }
                                }
                            }

                            return;
                        } else {
                            addNewBomb();
                            lastBombTime = elapsedTime;
                            if (isNoMoreSafeTile("text")) {
                                revealMines();
                                return;
                            } else {
                                addBonusTile();
//                        swapBonusWithBomb = true;
                                // Schedule removal after 30 seconds
                                removeTimer = new Timer(30000, evt -> removeBonusTile(currentBonusTile));

                                removeTimer.setRepeats(false);
                                removeTimer.start();
                            }

//                    // Collect all enabled tiles that are not mines or question tiles
//                    ArrayList<Minetile> availableTilesToCheck = new ArrayList<>();
//                    for (int r = 0; r < numRows; r++) {
//                        for (int c = 0; c < numCols; c++) {
//                            Minetile tile = position[r][c];
//                            if (tile.isEnabled() && !minelist.contains(tile) && !questionTileList.contains(tile) && !bonusTileList.contains(tile)) {
//                                availableTilesToCheck.add(tile);
//                            }
//                        }
//                    }
                            // Auto lose if there is no more available tiles left
//                    if (availableTilesToCheck.isEmpty()) {
//                    if (isNoMoreSafeTile("text")){
//                        revealMines();
//                        return;
//                    } else {
//                        addBonusTile();
////                        swapBonusWithBomb = true;
//                        // Schedule removal after 30 seconds
//                        Timer removeTimer = new Timer(30000, evt -> removeBonusTile(currentBonusTile));
//                        
//                        removeTimer.setRepeats(false);
//                        removeTimer.start();
//                    }
                            // Check if there are available tiles for bonus
//                    if (!isNoMoreSafeTile()) {
//                        addBonusTile();
////                        swapBonusWithBomb = true;
//                        // Schedule removal after 30 seconds
//                        Timer removeTimer = new Timer(30000, evt -> removeBonusTile(currentBonusTile));
//                        
//                        removeTimer.setRepeats(false);
//                        removeTimer.start();
//                    }
                            // DECREMENT AND TIME START SHOULD VARY ON LEVELS
//                        if (elapsedTime >= scoreTimeStart) {
//                            if (elapsedTime == scoreTimeStart) {
//                                timeScore--; // decrement if it meets when the score start to decrease
//                                elapsedTime2 = 0;
//                            } else if (elapsedTime2 % timePerScore == 0) {
//                                if (timeScore > 0) {
//                                    timeScore--; // Decrement every after 'timePerScore'
//                                }
//                                // Coins based on score 
//                                if (timeScore >= 90) {
//                                    coinsEarned = 50;
//                                } else if (timeScore >= 85) {
//                                    coinsEarned = 40;
//                                } else if (timeScore >= 65) {
//                                    coinsEarned = 30;
//                                } else if (timeScore >= 45) {
//                                    coinsEarned = 20;
//                                } else if (timeScore >= 25) {
//                                    coinsEarned = 10;
//                                } else {
//                                    coinsEarned = 5;
//                                }
//                            }
//                        }
                        }
                    }
                }
//                String timeText;
                int minutes = elapsedTime / 60;
                int seconds = elapsedTime % 60;
//                if ("blitz".equals(level)) {
////                    timeText = String.format("%02d:%02d", minutes, seconds);
//                } else {
//                    timeText = elapsedTime + "s";
//                }
                timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
//                timerLabel.setText("Time: " + timeText);
                scoreLabel.setText("" + timeScore);
//                timerLabel.setText("Time : " + elapsedTime + "s");
//                scoreLabel.setText("Score :" + Integer.toString(timeScore));
            }

        });

        // Setting Tiles
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
//                ImageIcon frameIcon = new ImageIcon(getClass().getResource("/images/virusSweeperIcon.png"));
                Minetile tile = new Minetile(r, c);
                tile.setBackground(Color.black); // unclicked color
//                tile.setIcon(frameIcon);
//                tile.setIcon(new ImageIcon("src/images/virusSweeperIcon.png"));
                position[r][c] = tile;

                tile.setIcon(closedTileIcon);
                tile.setDisabledIcon(openedTileIcon); // Set opened tile when disabled
                tile.setHorizontalTextPosition(JButton.CENTER);
                tile.setVerticalTextPosition(JButton.CENTER);
                tile.setFocusable(false);
                tile.setMargin(new Insets(0, 0, 0, 0));
                tile.setFont(new Font("Arial Unicode Ms", Font.PLAIN, 30));
//                tile.setText("1");
                tile.setOpaque(false);
                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameOver) {
                            return; // Lost
                        }
                        if (!start) { // The first Click
                            firstClick = tile;
                            start = true;
                            timer.start();
                            setMines(firstClick);
                            setQuestionTile();
                        }

                        Minetile tile = (Minetile) e.getSource();

                        // Left click
                        if (e.getButton() == MouseEvent.BUTTON1 && tile.isEnabled()) {
                            if (tile.getText() == "O") {
                                return;
                            }

                            Point tileLocation = tile.getLocationOnScreen();
                            Point frameLocation = frame.getLocationOnScreen();
                            int x = tileLocation.x - frameLocation.x + tile.getWidth() / 2 - characterWIDTH / 2;
                            int y = tileLocation.y - frameLocation.y + tile.getHeight() / 2 - characterHEIGHT / 2;

                            characterLabel.setLocation(x, y);
                            characterLabel.setIcon(diggingGif);
                            isDigging = true;
                            currentTileLocation = tile.getLocation();
                            // Cancel any pending return timer
                            if (returnTimer != null && returnTimer.isRunning()) {
                                returnTimer.stop();
                            }
                            returnTimer = new Timer(700, evt -> {
                                returnToOriginalPosition();
                                // After animation, show the actual content

                            });
                            returnTimer.setRepeats(false);
                            returnTimer.start();
                            if (allowReveal3x3) {
                                reveal3x3(tile);
                                allowReveal3x3 = false;
                                return;
                            }

                            if (tile.isEnabled() && elapsedTime == 0) {
                                System.out.println("nabasag");
                            } else if (tile.isEnabled()) {
                                System.out.println("second");
                            }

                            // "" means Not clicked yet -- PAG na click tile na hindi pa napipindot
                            if (tile.getText() == "" || tile.getText() == "?" || tile.getText() == "#") {

                                // if There's a bomb/bonus tile
                                if (minelist.contains(tile)) {
                                    //if it's question tile
                                    if (questionTileList.contains(tile)) {
                                        handleQuestionTile(tile);
//                                        return;
//                                        tile.setText("?"); // Show question mark
//                                        tile.setEnabled(false);
//                                        tile.setBackground(Color.white);
//
//                                        JOptionPane.showMessageDialog(null, "Question Tile", "Question", JOptionPane.WARNING_MESSAGE);
//                                        if (!timer.isRunning() && gameOver) {
//                                            revealMines();
//                                            return;
//                                        }
//                                        // TODO - IF ANSWERED CORRECTLY GIVE CLUE ELSE IF NOT ENABLE SOME TILE - DONE
//
//                                        // Remove from minelist and questionTilelist after clicked
//                                        questionTileList.remove(tile);
//                                        minelist.remove(tile);
//
////                                         if all unclicked tile has a bomb
//                                        if (!timer.isRunning() && gameOver) {
//                                            revealMines();
//                                            return;
//                                        }
//
//                                        if (questionList != null) {
//                                            int randomQuestionIndex = random.nextInt(questionList.length);
//                                            tempQuestion = questionList[randomQuestionIndex][0];
//                                            tempAnswer = questionList[randomQuestionIndex][1];
//                                            questionList = removeQuestion(questionList, tempQuestion);
//                                        }
//                                        // Show question dialog
//                                        String answer = JOptionPane.showInputDialog(frame, tempQuestion, "Question Tile", JOptionPane.QUESTION_MESSAGE);
//
//                                        // if all unclicked tile has a bomb
//                                        if (!timer.isRunning() && gameOver) {
//                                            revealMines();
//                                            return;
//                                        }
//
//                                        if (answer != null && (answer.equalsIgnoreCase(tempAnswer))) { // Correct answer
//                                            // Remove from mine list and question list
////                                            minelist.remove(tile);
////                                            questionTileList.remove(tile);
//
//                                            // Visual feedback
//                                            tile.setBackground(Color.green);
//                                            tile.setText("✓");
//                                            tile.setForeground(Color.black);
//
////                                            tilesClicked++;
//                                            // Automatically check surrounding tiles
//                                            checkMine(tile.r, tile.c);
//
//                                        } else { // Wrong answer
//                                            enableSurroundingTiles(tile);
//                                            JOptionPane.showMessageDialog(frame, "Wrong answer! Surrounding tiles reactivated.", "Incorrect", JOptionPane.ERROR_MESSAGE);
//                                            textLabel.setText("Minesweeper : " + Integer.toString(mineCount + newMine));
//                                        }
//                                        checkWinCondition();
                                    } /*else if (bonusTileList.contains(tile)) {
                                        handleBonusTile(tile);
                                        System.out.println("Bonus tile");
                                    } */ else {
                                        // else it's a bomb

                                        // revival chance
                                        if (power3 > 0) {
                                            timer.stop(); // Pause the game timer

                                            // Create revive dialog
                                            JPanel panel = new JPanel(new BorderLayout());
                                            JLabel label = new JLabel("<html>Do you want to revive?<br>(10 seconds remaining)</html>");
                                            panel.add(label, BorderLayout.CENTER);

                                            // Use your custom dialog function instead of JOptionPane
                                            final int[] secondsRemaining = {10};
                                            countdownTimer = new Timer(1000, f -> {
                                                secondsRemaining[0]--;
                                                label.setText("<html>Do you want to revive?<br>(" + secondsRemaining[0] + " seconds remaining)</html>");
                                                //label.setText("Do you want to revive?\n(" + secondsRemaining[0] + " seconds remaining)");
                                                if (secondsRemaining[0] <= 0 && !gameLost) {
                                                    ((Timer) f.getSource()).stop();
                                                    handleReviveResponse(tile, false); // Auto-handle revive if time is up
                                                }
                                            });
                                            countdownTimer.start();

                                            // Show the custom question panel and handle the result
                                            int dialogResult = CustomDialog.showCustomQuestionPanel(frame, panel, "System Restore");

                                            if (!gameOver) {
                                                if (dialogResult == JOptionPane.YES_OPTION) {
                                                    countdownTimer.stop();
                                                    handleReviveResponse(tile, true);
                                                } else {
                                                    countdownTimer.stop();
                                                    handleReviveResponse(tile, false);
                                                }
                                            }
//                                            JPanel panel = new JPanel(new BorderLayout());
//                                            JLabel label = new JLabel("Do you want to revive? (10 seconds remaining)");
//                                            panel.add(label, BorderLayout.CENTER);
//
//                                            JOptionPane optionPane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
//                                            JDialog dialog = optionPane.createDialog(frame, "System Restore");
//                                            dialog.setModal(true);
//
//                                            final int[] secondsRemaining = {10};
//                                            countdownTimer = new Timer(1000, f -> {
//                                                secondsRemaining[0]--;
//                                                label.setText("Do you want to revive? (" + secondsRemaining[0] + " seconds remaining)");
//                                                if (secondsRemaining[0] <= 0 && !gameLost) {
//                                                    ((Timer) f.getSource()).stop();
//                                                    dialog.dispose();
//                                                    handleReviveResponse(tile, false);
//                                                }
//                                            });
//                                            countdownTimer.start();
//
//                                            //dialog.setVisible(true);
//
//                                            // Check user response after dialog closes
//                                            Object selectedValue = optionPane.getValue();
//                                            if (!gameOver) {
//                                                if (selectedValue != "uninitializedValue" && (Integer) selectedValue == 0) {
//                                                    countdownTimer.stop();
//                                                    handleReviveResponse(tile, true);
//                                                } else {
//                                                    countdownTimer.stop();
//                                                    handleReviveResponse(tile, false);
//                                                }
//                                            }
                                        } else {
                                            gameLost = true;
                                            swapBonusWithBomb = true;
                                            revealMines();
                                            playSound("gameover.wav");
                                            CustomDialog.showCustomError(frame, "Game Over:\nYou dug up a Virus");
                                            //JOptionPane.showMessageDialog(frame, "Game Over: You dug up a Virus", "Game Over", JOptionPane.ERROR_MESSAGE);
                                            textLabel1.setText("Game Over: You dug up a Virus");
                                        }
                                    }
                                } else if (bonusTileList.contains(tile)) {
                                    handleBonusTile(tile);
                                    checkWinCondition();
                                    System.out.println("Bonus tile");
                                } else { // Check neighbor tiles
                                    checkMine(tile.r, tile.c);
                                }
                            }
                        } else if (e.getButton() == MouseEvent.BUTTON3) { // right click - setting flag

                            if (tile.getText() == "" && tile.isEnabled()) { // adding Flag
                                flagged++;
                                tile.setText("O");
                                playSound("flag.wav");
                                tile.setForeground(Color.cyan); // Set text color
                                playSound("buttonn.wav");
                            } else if (tile.getText() == "O") { // removing Flag
                                tile.setText("");
                                playSound("unflag.wav");
                                if (flagged > 0) {
                                    flagged--;
                                }
                            }
                            textLabelFlagged.setText(Integer.toString(flagged));
                            playSound("efects.wav");
                        }
                    }
                });

                boardPanel.add(tile);
            }
        }
        frame.setVisible(true);

    }

    private void returnToOriginalPosition() {
        // Calculate original position directly here
        int originalX = boardWidth / 3 - characterWIDTH / 2;
        int originalY = boardHeight - characterHEIGHT;

        characterLabel.setLocation(originalX, originalY);
        characterLabel.setIcon(standbyGif);
        isDigging = false;
    }

    // Setting Bonus/Question tiles
    void setQuestionTile() {
        questionTileList = new ArrayList<Minetile>();

        // count of bonus tile
        if ("blitz".equals(level)) {
            questionTiles = 3;
        } else {
            questionTiles = 5;
        }
        while (questionTiles > 0) {
            // Declare the position
            int rowPosition = random.nextInt(numRows);
            int colPosition = random.nextInt(numCols);

            // qTile inherit the posistion
            Minetile qTile = position[rowPosition][colPosition];

            // if it's not on the bonusTileList & minelist, add it.
            if ((!questionTileList.contains(qTile) && !minelist.contains(qTile)) && !isNeighbor(firstClick, qTile)) {
                questionTileList.add(qTile);
                minelist.add(qTile);
                questionTiles--;
            }
        }
    }

    void setMines(Minetile firstClick) {
        minelist = new ArrayList<Minetile>();
//         Declaring mines
        int mineleft = mineCount;
        while (mineleft > 0) {
            // Kung saang board
            int r = random.nextInt(numRows); // 0 to numRows value
            int c = random.nextInt(numCols); // 0 ti numCols value

            // Make a tile na may bomb based on board
            Minetile tile = position[r][c];

            // if wala pang 'tile' position sa 'minelist' && ensures walang katabing bomb 
            if (!minelist.contains(tile) && !isNeighbor(firstClick, tile)) {
                minelist.add(tile);
                mineleft -= 1;
            }
        }
    }

    // Revealing mines == game end
    void revealMines() {
        // Revealing questionTile and bomb
        for (int i = 0; i < minelist.size(); i++) {
            Minetile tile = minelist.get(i);
            Minetile questionTile;
//            Minetile bonusTile;

            for (int j = 0; j < questionTileList.size(); j++) {
                questionTile = questionTileList.get(j);
                if (questionTile == tile) {
                    if (tile.getText().equals("O")) {
                        if (flagged > 0) {
                            flagged--;
                        }
                    }
                    tile.setText("?"); // Bomb text
                    tile.setForeground(Color.green); // Set text color
                }
            }
            if (tile.getText() != "?") {
                if (tile.getText() == "O" || Color.cyan.equals(tile.getForeground())) {
                    tile.setForeground(Color.cyan);
                } else {
                    tile.setForeground(Color.red); // Set text color
                }
                tile.setText("#"); // Bomb text
            }
//            for (int j =0;j<bonusTileList.size(); j++){
//                bonusTile = bonusTileList.get(j);
//                if (bonusTile == tile){
//                    tile.setText("A");
//                    tile.setForeground(Color.YELLOW);
//                }
//            }
        }

        // Reveal all bonus tile     
        for (Minetile tile : bonusTileList) {
            if (tile.isEnabled()) { // Only reveal if not already clicked
                tile.setText("★");
                tile.setForeground(Color.cyan);
//                tile.setBackground(Color.WHITE);
            }
        }
        updateRevealedTileCounts();
        BtnRestart.setText("Again");
        BtnBack.setEnabled(true);
        BtnRestart.setEnabled(true);
        gameOver = true;
        scoreLabel.setText(Integer.toString(timeScore));
        textLabelFlagged.setText(Integer.toString(flagged));
        timer.stop();

        // here
        removeAllTimers();
//        textLabel1.setText("Game Over");

        if (pauseTimer != null && pauseTimer.isRunning()) {
            pauseTimer.stop();
        }
    }

    void checkMine(int r, int c) {
        playSound("tileclick.wav");
        // if the neighbor tiles is out of bounds = exit
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) {
            return;
        }
        Minetile tile = position[r][c];
        // if tile is clicked = exit
        if (!tile.isEnabled()) {
            return;
        }
        // If it's a bonus tile, do not reveal
        if (questionTileList.contains(tile) || bonusTileList.contains(tile)) {
            return;
        }
        if ((tile.isEnabled() && tile.getText() == "O") || tile.getText() == "?") {
            flagged--;
        }

        //tile.setBackground(Color.white); // clicked color
        tile.setEnabled(false); // disabling button
//        tilesClicked += 1; // increment 'tilesClicked' if there's no bomb

        // SPRITE ANIMATION
        System.out.println("nabasag");

        int minesFound = 0;

        // top 3 neighbor tiles
        minesFound += countMine(r - 1, c - 1); // top-left tile
        minesFound += countMine(r - 1, c); // top-center tile
        minesFound += countMine(r - 1, c + 1); // top-right tile

        // left and right neighbor tile
        minesFound += countMine(r, c - 1); // Left tile
        minesFound += countMine(r, c + 1); // Right tile

        // Bottom 3 neighbor tile
        minesFound += countMine(r + 1, c - 1); // Bottom-left tile
        minesFound += countMine(r + 1, c); // Bottom-Center tile
        minesFound += countMine(r + 1, c + 1); // Bottom-right Tile

        if (minesFound > 0) {
            tile.setText(Integer.toString(minesFound));

        } else {
            tile.setText("");
            playSound("unflag.wav");

            //Check neighboring mine
            checkMine(r - 1, c - 1); // top-left
            if (gameOver == true) {
                return;
            }
            checkMine(r - 1, c); // top-center
            if (gameOver == true) {
                return;
            }
            checkMine(r - 1, c + 1); // top-right
            if (gameOver == true) {
                return;
            }

            checkMine(r, c - 1); // left
            if (gameOver == true) {
                return;
            }
            checkMine(r, c + 1); // right
            if (gameOver == true) {
                return;
            }

            checkMine(r + 1, c - 1); // bottom-left
            if (gameOver == true) {
                return;
            }
            checkMine(r + 1, c); // bottom-center
            if (gameOver == true) {
                return;
            }
            checkMine(r + 1, c + 1);// bottom-right
            if (gameOver == true) {
                return;
            }
        }
        checkWinCondition();
        textLabelFlagged.setText(Integer.toString(flagged));
    }

    int countMine(int r, int c) {
        // if out of bounds
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) {
            return 0;
        }
        if (minelist.contains(position[r][c])) { // if within tiles
            // if it's bonus tile do not reveal
            if (questionTileList.contains(position[r][c]) || bonusTileList.contains(position[r][c])) {
                return 0;
            } else {
                return 1;
            }
        }
        return 0;
    }

    boolean isNeighbor(Minetile first, Minetile tile) {
        return Math.abs(first.r - tile.r) <= 1 && Math.abs(first.c - tile.c) <= 1;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == BtnBack) {
             playSound("clickButton.wav");
            int x = CustomDialog.showCustomConfirmation(null, "Are you sure?", "Quit");

            //int x = JOptionPane.showConfirmDialog(null, "Are you sure?", "Quit", JOptionPane.YES_NO_OPTION);
            if (x == 0) {
                timer.stop();
                removeAllTimers();
                new playFrame();
                frame.dispose();
            }
        } else if (e.getSource() == BtnRestart) {
            playSound("clickButton.wav");
            int x;

//            Timer timer;
//            Timer removeTimer;
//            Timer pauseTimer;
//            Timer power1Timer;
            if (!gameOver) {
                x = CustomDialog.showCustomConfirmation(null, "Are you sure?", "Restart");
                //x = JOptionPane.showConfirmDialog(null, "Are you sure?", "Restart", JOptionPane.YES_NO_OPTION);
                if (x == 0) {
                    timer.stop();
                    removeAllTimers();
//                    removeTimer.stop();
//                    pauseTimer.stop();
//                    power1Timer.stop();
//                    countdownTimer.stop();
//                    if (removeTimer != null && removeTimer.isRunning()) {
//                        removeTimer.stop();
//                    }
//                    if (pauseTimer != null && pauseTimer.isRunning()) {
//                        pauseTimer.stop();
//                    }
                    frame.dispose();
                    if (level == "blitz") {
                        new Minesweeper(mineCount, numRows, scoreTimeStart, timePerScore, "blitz");
                    } else {
                        new Minesweeper(mineCount, numRows, scoreTimeStart, timePerScore, "");
                    }
                }
            } else {
                timer.stop();
                removeAllTimers();
//                if (removeTimer != null && removeTimer.isRunning()) {
//                    removeTimer.stop();
//                }
//                if (pauseTimer != null && pauseTimer.isRunning()) {
//                    pauseTimer.stop();
//                }
                frame.dispose();
                if (level == "blitz") {
                    new Minesweeper(mineCount, numRows, scoreTimeStart, timePerScore, "blitz");
                } else {
                    new Minesweeper(mineCount, numRows, scoreTimeStart, timePerScore, "");
                }
                // trye pa ulit
            }
        } else if (e.getSource() == power1Btn && power1 > 0 && start && !gameOver) {
            playSound("clickButton.wav");
//            activatePower1();
            if (timer.isRunning()) {
                pauseTimePower();
                power1--;
                power1Lbl.setText("" + String.valueOf(power1));
            } else if (pauseTimer.isRunning() && !gameOver) {
                //CustomDialog.showCustomWarning(null, "Wait the duration of\nlast debugger");
                CustomDialog.showCustomInformation(null, "Wait the duration of\nlast debugger", "Debugger");
                //JOptionPane.showMessageDialog(null, "Wait the duration of last debugger", "Debugger", 2);
            }
        } else if (e.getSource() == power2Btn && !gameOver && (power2 > 0 && start) || allowReveal3x3) {
            playSound("clickButton.wav");
            if (!allowReveal3x3) {
                allowReveal3x3 = true;
                power2--;
                power2Lbl.setText("" + String.valueOf(power2));
            } else if (allowReveal3x3) {
                allowReveal3x3 = false;
                power2++;
                power2Lbl.setText("" + String.valueOf(power2));
            } else {
                CustomDialog.showCustomWarning(null, "Radar is enable!");
                //JOptionPane.showMessageDialog(null, "Radar is enable!", "Radar", 2);
            }
        }
        updatePowerUps();
        /*else if (e.getSource() == power3Btn && power3 > 0 && start) {
//            activatePower3();
            power3--;
            power3Lbl.setText("System Restore : " + String.valueOf(power2));
        }*/
    }

    // Enable surrounding tiles and possibly add a new bomb (50%)
    void enableSurroundingTiles(Minetile tile) {
        ArrayList<Minetile> reEnabledTiles = new ArrayList<>();
        // Enable all neighbors (and self) if they are not enabled and not quesionTile
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                int nr = tile.r + dr;
                int nc = tile.c + dc;
                if (nr >= 0 && nr < numRows && nc >= 0 && nc < numCols) {
                    Minetile neighbor = position[nr][nc];
                    if (!neighbor.isEnabled() && !questionTileList.contains(neighbor) && !bonusTileList.contains(neighbor)) {
                        neighbor.setEnabled(true);
                        neighbor.setBackground(Color.black);
                        neighbor.setText("");
//                        tilesClicked--; // Adjust the counter since we are it is unreaveling
                        reEnabledTiles.add(neighbor);
                    }
                }
            }
        }
        // Ensure the questiontile itself is re-enabled.
        if (!tile.isEnabled()) {
            tile.setEnabled(true);
            tile.setText("");
            playSound("unflag.wav");
            tile.setBackground(Color.black);
            if (!reEnabledTiles.contains(tile)) {
                reEnabledTiles.add(tile);
            }
        }

        // With 50% chance to add new 1 bomb in 1 of newly enabled tile
        if (random.nextBoolean() && !reEnabledTiles.isEmpty()) {
            int idx = random.nextInt(reEnabledTiles.size());
            Minetile newBombTile = reEnabledTiles.get(idx);
            if (!minelist.contains(newBombTile)) {
                minelist.add(newBombTile);
                // If the chosen tile was a question tile, remove it from the bonus list.
                questionTileList.remove(newBombTile);
                newMine++;
//                textLabel.setText("Minecount : " + minelist.size());
                updateRevealedTileCounts();
                checkWinCondition();
            }
        }
    }

    void updateRevealedTileCounts() {

        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                Minetile tile = position[r][c];

                // If it's disabled (already revealed) && not itself a mine,
                // then re-calculate how many bombs surround it.
                if (!tile.isEnabled() && !minelist.contains(tile)) {
                    int minesFound = 0;

                    // Count bombs in all 8 directions
                    // top
                    minesFound += countMine(r - 1, c - 1);
                    minesFound += countMine(r - 1, c);
                    minesFound += countMine(r - 1, c + 1);

                    // side
                    minesFound += countMine(r, c - 1);
                    minesFound += countMine(r, c + 1);

                    // bottom
                    minesFound += countMine(r + 1, c - 1);
                    minesFound += countMine(r + 1, c);
                    minesFound += countMine(r + 1, c + 1);

                    // Update the "Minesweeper" count
                    if (minesFound > 0) {
                        tile.setText(Integer.toString(minesFound));
                    } else {
                        if (tile.getText() == "?" || tile.getText() == "★") {
                            return;
                        } else {
                            tile.setText("");
                             playSound("unflag.wav");
                        }
                    }
                }
            }
        }
    }

    void checkWinCondition() {
        if (gameOver) {
            return;
        }
        int safeTiles = numRows * numCols - minelist.size();
        int revealedCount = 0;

        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                Minetile tile = position[r][c];
                // tile is revealed if it's disabled and NOT a mine
                if (!tile.isEnabled() && !minelist.contains(tile)) {
                    revealedCount++;
                }
            }
        }

        // Win if all tiles are clicked except with bombs
        if (revealedCount == safeTiles && start) {
            // Try
            // Last final question (10 seconds)
             playSound("byouWin.wav");
            timer.stop();

            if ("blitz".equals(level)) {
//                timeScore = elapsedTime; // Final score = remaining time
                blitzScore = "00:0" + String.valueOf(elapsedTime / 60) + ":" + String.valueOf(elapsedTime % 60);
            }

            if (pauseTimer != null && pauseTimer.isRunning()) {
                pauseTimer.stop();
            }

            questionList = generateQuestion(questionLevel, "finalquestion");
            if (level == "") { // Easy, Medium, Hard
                tempQuestion = questionList[0][0];
                tempAnswer = questionList[0][1];
            } else { // Blitz
                Random rand = new Random();
                int questionNum = rand.nextInt(questionList.length);
                tempQuestion = questionList[questionNum][0];
                tempAnswer = questionList[questionNum][1];
            }
            /*JPanel panel = new JPanel(new BorderLayout());
            JLabel label = new JLabel(tempQuestion + " (10 seconds remaining)");
            JTextField userAnswer = new JTextField(); // Input field
            panel.add(label, BorderLayout.NORTH);
            panel.add(userAnswer, BorderLayout.CENTER);

            JOptionPane optionPane = new JOptionPane(
                    panel,
                    JOptionPane.QUESTION_MESSAGE,
                    JOptionPane.OK_CANCEL_OPTION // Use OK/Cancel buttons
            );
            JDialog dialog = optionPane.createDialog(frame, "Final Question");
            dialog.setModal(true);

            final int[] secondsRemaining = {10};
            countdownTimer = new Timer(1000, e -> {
                secondsRemaining[0]--;
                label.setText(tempQuestion + " (" + secondsRemaining[0] + " seconds remaining)");

                if (secondsRemaining[0] <= 0) {
                    ((Timer) e.getSource()).stop();
                    dialog.dispose(); // Auto-close on timeout
                }
            });

            dialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    countdownTimer.stop();
                }
            });
            countdownTimer.start();
            dialog.setVisible(true);*/
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(new Color(255, 235, 205)); // Light orange

            JLabel label = new JLabel(tempQuestion + " (10 seconds remaining)");
            label.setForeground(Color.RED);
            label.setFont(new Font("SansSerif", Font.BOLD, 30));
            label.setOpaque(true);
            label.setBackground(new Color(255, 235, 205));

            JTextField userAnswer = new JTextField();
            userAnswer.setBackground(new Color(255, 250, 240));
            userAnswer.setForeground(Color.DARK_GRAY);
            userAnswer.setFont(new Font("SansSerif", Font.PLAIN, 25));
            userAnswer.setOpaque(true);

            panel.add(label, BorderLayout.NORTH);
            panel.add(userAnswer, BorderLayout.CENTER);

// Style UIManager (affects buttons and panels globally)
            UIManager.put("Button.background", new Color(139, 69, 19));
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.font", new Font("Arial", Font.BOLD, 25));
            UIManager.put("OptionPane.background", new Color(255, 235, 205));
            UIManager.put("Panel.background", new Color(255, 235, 205));

// Create the JOptionPane
            JOptionPane optionPane = new JOptionPane(
                    panel,
                    JOptionPane.QUESTION_MESSAGE,
                    JOptionPane.OK_CANCEL_OPTION
            );

// Create custom dialog (undecorated)
            JDialog dialog = new JDialog((Frame) null, "Final Question", true);
            dialog.setUndecorated(true);
            dialog.setContentPane(optionPane);
            dialog.getRootPane().setBorder(BorderFactory.createLineBorder(Color.RED, 4));
            dialog.pack();
            dialog.setLocationRelativeTo(null);

// Countdown logic
            final int[] secondsRemaining = {10};
            Timer countdownTimer = new Timer(1000, e -> {
                secondsRemaining[0]--;
                label.setText(tempQuestion + " (" + secondsRemaining[0] + " seconds remaining)");
                if (secondsRemaining[0] <= 0) {
                    ((Timer) e.getSource()).stop();
                    optionPane.setValue(JOptionPane.CLOSED_OPTION); // Triggers dialog to close
                }
            });

// Handle OK/Cancel click
            optionPane.addPropertyChangeListener(evt -> {
                String prop = evt.getPropertyName();
                if (dialog.isVisible()
                        && evt.getSource() == optionPane
                        && JOptionPane.VALUE_PROPERTY.equals(prop)) {
                    dialog.setVisible(false);
                }
            });

// Stop timer when window is closed manually
            dialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    countdownTimer.stop();
                }
            });

// Start countdown and show dialog
            countdownTimer.start();
            optionPane.setInitialValue(null);
            optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
            dialog.setVisible(true); // Only call ONCE!
            // Get result after dialog closes
            String userInput = null;
            Object result = optionPane.getValue();
            gameOver = true;

            // Capture input if OK clicked
            if (result != "uninitializedValue" && (Integer) result == 0) {
                userInput = userAnswer.getText();
                if (userInput.equalsIgnoreCase(tempAnswer)) { // correct
                    // Update database score
                    if (numRows == 9 && level.equals("")) {
                        level = "easyscore";
                    } else if (numRows == 15) {
                        level = "mediumscore";
                    } else if (numRows == 21) {
                        level = "hardscore";
                    } else {
                        level = "blitzscore";
                    }
//                    int oldCoin = menu.coins;
                    int oldCoin = 0;
                    // String findScore = "select "+level+" from account where username = '"+Login.username1+"'";
                    String findScore = "select * from account where username = '" + Login.username1 + "'";
                    try {
                        Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/ViperDB", "root", "root");
                        Statement st = con.createStatement();
                        ResultSet rs = st.executeQuery(findScore);
                        // Updating score & coins
                        if (rs.next()) {
                            oldCoin += rs.getInt("coins") + coinsEarned;
                            if (level.equals("blitzscore")) {
                                // Compare time
                                String oldTimeScore = rs.getString(level).substring(3);
                                String newTimeScore = timerLabel.getText().substring(3);

                                if (oldTimeScore.compareTo(newTimeScore) < 0) {
                                    st.executeUpdate("update account set " + level + " = '" + blitzScore + "',coins = " + oldCoin + " where username = '" + Login.username1 + "'");
                                } else {
                                    st.executeUpdate("update account set coins = " + oldCoin + " where username = '" + Login.username1 + "'");
                                }
                            } else {
                                if (timeScore > rs.getInt(level)) {
                                    st.executeUpdate("update account set " + level + " = " + timeScore + ",coins = " + oldCoin + " where username = '" + Login.username1 + "'");
                                } else {
                                    st.executeUpdate("update account set coins = " + oldCoin + " where username = '" + Login.username1 + "'");
                                }
                            }
                        }

                        st.close();
                        rs.close();
                        con.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        System.out.println("Failed");
                    }

                    revealMines();
                    gameOver = true; // Players cannot click tiles anymore
                    textLabel1.setText("Mines Cleared");
                    CustomDialog.showCustomMessage(frame, "You earned " + coinsEarned + " coins!", "Congratulations!");
                    //JOptionPane.showMessageDialog(frame, "You earned " + coinsEarned + " coins!");
                    level = "";
                    countdownTimer.stop();
                } else { // wrong
//                    timer.stop();
                    CustomDialog.showCustomError(null, "Game Over:\nYour answer is wrong!");

                    //JOptionPane.showMessageDialog(null, "Game Over: Your answer is wrong", "Game Over", JOptionPane.ERROR_MESSAGE);
                    textLabel1.setText("Game Over: You failed to\nanswer the final question");
                    revealMines();
                    countdownTimer.stop();
                }
            } // Handle result (if user pressed Cancel)
            else if (result != "uninitializedValue" && (Integer) result == 2) {
//                processInput(userInput);
                CustomDialog.showCustomError(null, "Game Over:\nYou failed!");
                //JOptionPane.showMessageDialog(null, "Game Over: You failed!", "Game Over", JOptionPane.ERROR_MESSAGE);
                textLabel1.setText("Game Over: You failed to answer the final question");
                revealMines();
                countdownTimer.stop();
            } else {
//                processTimeout();
//                timer.stop();
                CustomDialog.showCustomError(null, "Game Over:\nTime's Up!");
                //JOptionPane.showMessageDialog(null, "Game Over: Time's Up!", "Game Over", JOptionPane.ERROR_MESSAGE);
                textLabel1.setText("Game Over: You failed to answer the final question");
                revealMines();
                countdownTimer.stop();
            }

            // End
//            gameOver = true;
            // One last final question
//            questionList = generateQuestion(questionLevel, "finalquestion");
//            tempQuestion = questionList[0][0];
//            tempAnswer = questionList[0][1];
//
//            String answer = JOptionPane.showInputDialog(frame, tempQuestion, "Question Tile", JOptionPane.QUESTION_MESSAGE);
//            if (!timer.isRunning() && gameOver) {
//                revealMines();
//                return;
//            }
//            if (pauseTimer != null && pauseTimer.isRunning()) {
//                pauseTimer.stop();
//            }
//
//            gameOver = true;
//            // Correct answer
//            if (answer != null && answer.equalsIgnoreCase(tempAnswer)) {
//                // Update database score
//                if (numRows == 9 && level.equals("")) {
//                    level = "easyscore";
//                } else if (numRows == 15) {
//                    level = "mediumscore";
//                } else if (numRows == 21) {
//                    level = "hardscore";
//                } else {
//                    level = "blitzscore";
//                }
//                int oldCoin = 0;
//                //            String findScore = "select "+level+" from account where username = '"+Login.username1+"'";
//                String findScore = "select * from account where username = '" + Login.username1 + "'";
//                try {
//                    Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/ViperDB", "root", "root");
//                    Statement st = con.createStatement();
//                    ResultSet rs = st.executeQuery(findScore);
//                    // Updating score & coins
//                    if (rs.next()) {
//                        oldCoin += rs.getInt("coins") + coinsEarned;
//                        if (timeScore > rs.getInt(level)) {
//                            st.executeUpdate("update account set " + level + " = " + timeScore + ",coins = " + oldCoin + " where username = '" + Login.username1 + "'");
//                        } else {
//                            st.executeUpdate("update account set coins = " + oldCoin + " where username = '" + Login.username1 + "'");
//                        }
//                    }
//
//                    st.close();
//                    rs.close();
//                    con.close();
//                } catch (SQLException ex) {
//                    ex.printStackTrace();
//                    System.out.println("Failed");
//                }
//
//                revealMines();
//                gameOver = true; // Players cannot click tiles anymore
//                textLabel1.setText("Mines Cleared");
//                JOptionPane.showMessageDialog(frame, "You earned " + coinsEarned + " coins!");
//                level = "";
//            } else {
//                timer.stop();
//                JOptionPane.showMessageDialog(null, "Game Over: Your answer is wrong", "Game Over", JOptionPane.ERROR_MESSAGE);
//                textLabel1.setText("Game Over: You failed to answer the final question");
//                revealMines();
//            }
        }
    }

    String[][] generateQuestion(String difficulty, String questionType) {
//        String question[];
        ArrayList<String[]> questionList = new ArrayList<>(); // Temporary list
        String sqlFindQuestions = "";

//        int numQuestion = 0;
//        try {
//            Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/ViperDB", "root", "root");
//            Statement st = con.createStatement();
//            ResultSet rs = st.executeQuery("select count(ID) as total from questions");
//            if (rs.next()) {
//                numQuestion = rs.getInt("total");
//            }
//
//            rs.close();
//            st.close();
//            con.close();
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//            System.out.println("Failed");
//        }
        if (difficulty != "") {
            sqlFindQuestions = "select question,answer from questions where diff = '" + difficulty + "' and type = '" + questionType + "' order by id asc";
        } else {
            sqlFindQuestions = "select question,answer from questions where type = '" + questionType + "' order by id asc";
//            Random rand = new Random();
//            int num = rand.nextInt(numQuestion);
        }
        try {
            Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/ViperDB", "root", "root");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sqlFindQuestions);

            while (rs.next()) {
                String question = rs.getString("question");
                String answer = rs.getString("answer");
                questionList.add(new String[]{question, answer});
            }

            con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Failed");
        }

        // Convert ArrayList to String[][]
        return questionList.toArray(new String[0][0]);
    }

    String[][] removeQuestion(String[][] questions, String questionToRemove) {
        int newSize = 0;

        // Count how many questions will remain
        for (String[] q : questions) {
            if (!q[0].equals(questionToRemove)) {
                newSize++;
            }
        }

        // Create a new array with the reduced size
        String[][] newQuestions = new String[newSize][2];
        int index = 0;

        // Copy only the questions that should remain
        for (String[] q : questions) {
            if (!q[0].equals(questionToRemove)) {
                newQuestions[index++] = q;
            }
        }

        return newQuestions;
    }

    // Method to add a new bomb every 60 seconds
    void addNewBomb() {

//        if (isNoMoreSafeTile()){
//            revealMines();
//            textLabel1.setText("Game Over: ");
//            return;
//        }
        ArrayList<Minetile> availableTiles1 = new ArrayList<>();

        // Collect all enabled tiles that are not mines or question tiles
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                Minetile tile = position[r][c];
                if (tile.isEnabled() && !minelist.contains(tile) && !questionTileList.contains(tile) && !bonusTileList.contains(tile)) {
                    availableTiles1.add(tile);
                }
            }
        }

        if (!availableTiles1.isEmpty()) {
            // Select a random tile and add it to the mine list
            int randomIndex = random.nextInt(availableTiles1.size());
            Minetile newBombTile = availableTiles1.get(randomIndex);
            minelist.add(newBombTile);
            newMine++;
            textLabel.setText("" + (mineCount + newMine));
//            newBombTile.setText("#");
            updateRevealedTileCounts();
        }
    }

    // Method to find is there anymore unlclicked tile
    boolean isNoMoreSafeTile() {
        ArrayList<Minetile> availableTiles2 = new ArrayList<>();

        // Collect all enabled tiles that are not mines or question tiles
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                Minetile tile = position[r][c];
                if (tile.isEnabled() && !minelist.contains(tile) && !questionTileList.contains(tile) && !bonusTileList.contains(tile)) {
                    availableTiles2.add(tile);
                }
            }
        }

        if (availableTiles2.isEmpty()) {
            gameOver = true;
            return true;
        } else {
            return false;
        }
    }

    boolean isNoMoreSafeTile(String type) {
        ArrayList<Minetile> availableTiles2 = new ArrayList<>();
        // Collect all enabled tiles that are not mines or question tiles
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                Minetile tile = position[r][c];
                if (tile.isEnabled() && (!minelist.contains(tile) && !questionTileList.contains(tile) && !bonusTileList.contains(tile))) {
                    availableTiles2.add(tile);
                }
            }
        }

        if (availableTiles2.isEmpty()) {
            if (type == "text") {
                gameOver = true;
                CustomDialog.showCustomError(null, "Game Over:\nLast safe tile is now\ncontained with Virus!");
                //JOptionPane.showMessageDialog(null, "Game Over: Last safe tile is now contained with Virus", "Game Over", JOptionPane.ERROR_MESSAGE);
                textLabel1.setText("Game Over: You failed to prevent virus");
            }
            return true;
        } else {
            return false;
        }
    }

    // Implement handleBonusTile method
    void handleBonusTile(Minetile tile) {
        tile.setBackground(Color.WHITE);
        tile.setEnabled(false);
//        tile.setText("★");
//        tile.setForeground(Color.yellow);
        bonusTileList.remove(tile);
        minelist.remove(tile);

        // Add 20 points
//        timeScore += 20;
//        scoreLabel.setText("Score : " + timeScore);
//        System.out.println("You got bonus tile");
        Random rand = new Random();
        int chance = rand.nextInt(100); // 0-99
//        int chance = 94;

        if (chance < 50) { // 50% chance for 5 coins
            int bonusCoin = 5;
            timer.stop();
            CustomDialog.showCustomMessage(frame, "You found 5 coins!", "Bonus");
            //JOptionPane.showMessageDialog(frame, "Bonus: You found 5 coins!");

            String sqlCoin = "update account set coins = " + (database.getIntRecord("coins", menu.username) + bonusCoin) + " where username = '" + menu.username + "'";
            try {
                Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/ViperDB", "root", "root");
                Statement st = con.createStatement();
                st.executeUpdate(sqlCoin);
            } catch (SQLException ex) {
                ex.printStackTrace();
                System.out.println("Failed");
            }

            timer.start();
        } else if (chance < 65) { // 15% chance for +20 score
            timeScore += 20;
            scoreLabel.setText("" + timeScore);
            timer.stop();
            CustomDialog.showCustomMessage(frame, "+20 Score!", "Bonus");
            //JOptionPane.showMessageDialog(frame, "Bonus: +20 Score!");
            timer.start();
        } else if (chance < 80) { // 15% chance for 30s pause
            power1++;
            CustomDialog.showCustomInformation(null, "+1 Debugger!", "Bonus");
            //JOptionPane.showMessageDialog(null, "Bonus: +1 Debugger!", "Bonus", JOptionPane.INFORMATION_MESSAGE);
//            timePaused += 5000;
//            pauseTimePower();
//            if (timer.isRunning()) {
//                timer.stop();
//                timer.start();
//                timer.stop();
//                if (pauseTimer != null && pauseTimer.isRunning()) {
//                    pauseTimer.stop();
//                }
//                timePaused += 10000;
//
//                pauseTimer = new Timer(timePaused, e -> {
//                    if (!gameOver) {
//                        timer.start();
//                    }
//                    if (gameOver || gameLost) {
//                        pauseTimer.stop();
//                        return;
//                    }
////                    JOptionPane.showMessageDialog(frame, "Timer resumed!");
//                });
//                pauseTimer.setRepeats(false);
//                pauseTimer.start();
//            }
        } else if (chance < 95) { // 15% chance for 3x3 reveal
            power2++;
            //CustomDialog.showCustomInformation(null, "Bonus: +1 Radar!", "Bonus");
            CustomDialog.showCustomMessage(null, "+1 Radar!", "Bonus");
            //JOptionPane.showMessageDialog(null, "Bonus: +1 Radar!", "Bonus", JOptionPane.INFORMATION_MESSAGE);
//            allowReveal3x3 = true;
//            JOptionPane.showMessageDialog(frame, "Bonus: Click any tile to reveal its 3x3 neighbors!");
        } else { // 5% chance for Power 4
            power3++;
            CustomDialog.showCustomMessage(null, "+1 System Restore!", "Bonus");
            //CustomDialog.showCustomInformation(null, "Bonus: +1 System Restore!", "Bonus");
            //JOptionPane.showMessageDialog(null, "Bonus: +1 System Restore!", "Bonus", JOptionPane.INFORMATION_MESSAGE);
//            timeScore += 4;
//            coinsEarned += 4;
//            scoreLabel.setText("Score : " + timeScore);
//            JOptionPane.showMessageDialog(frame, "Power 4! +4 Score and Coins!");
        }
        power1Btn.setText(String.valueOf(power1));
        power2Btn.setText(String.valueOf(power2));
        power3Lbl.setText(String.valueOf(power3));
        updateRevealedTileCounts();
        updatePowerUps();
        // Remove a random non-question mine
//        checkWinCondition();
    }

    private void addBonusTile() {
        ArrayList<Minetile> availableTiles = new ArrayList<>();
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                Minetile tile = position[r][c];
                if (tile.isEnabled() && !minelist.contains(tile) && !questionTileList.contains(tile) && !bonusTileList.contains(tile)) {
                    availableTiles.add(tile);

                }
            }
        }
        if (!availableTiles.isEmpty()) {
            currentBonusTile = availableTiles.get(random.nextInt(availableTiles.size()));
            bonusTileList.add(currentBonusTile);
//            minelist.add(currentBonusTile);
//            currentBonusTile.setText("B");
            currentBonusTile.setBackground(Color.cyan);
//            currentBonusTile.setForeground(Color.cyan);
        }
    }
    // you have problem here if mine swapped with bomb, the bomb disappear

    private void removeBonusTile(Minetile tile) {
        if (tile != null && bonusTileList.contains(tile) && timer.isRunning()) {
            if (tile.isEnabled() && !swapBonusWithBomb) {
                if (tile.getText().equals("O")); else {
                    tile.setText("");
                    playSound("unflag.wav");
                }
                tile.setBackground(Color.black);
                bonusTileList.remove(tile);
                minelist.remove(tile);
                currentBonusTile = null;
            } else if (tile.isEnabled() && gameLost) {
                // dig up a bomb
                System.out.println("Dig up virus");
            } else {

            }
            updateRevealedTileCounts();
        }
    }

    void updateCoinsBasedOnScore() {
        if (timeScore >= 90) {
            coinsEarned = 50;
        } else if (timeScore >= 85) {
            coinsEarned = 40;
        } else if (timeScore >= 65) {
            coinsEarned = 30;
        } else if (timeScore >= 45) {
            coinsEarned = 20;
        } else if (timeScore >= 25) {
            coinsEarned = 10;
        } else {
            coinsEarned = 5;
        }
    }

    // Add reveal3x3 method // YOU HAVE SOMETHING TO FIX HERE
    private void reveal3x3(Minetile centerTile) {
        for (int r = centerTile.r - 1; r <= centerTile.r + 1; r++) {
            for (int c = centerTile.c - 1; c <= centerTile.c + 1; c++) {
                if (r >= 0 && r < numRows && c >= 0 && c < numCols) {
                    Minetile tile = position[r][c];

                    if (!tile.isEnabled()) {
                        continue;
                    }

                    if (minelist.contains(tile)) {
                        if (questionTileList.contains(tile)) {
                            // Handle question tile
                            if (tile.getText().equals("O")) {
                                flagged--;
                            }
                            tile.setText("?");
                            tile.setForeground(Color.green);
//                            flagged--;
//                            tile.setEnabled(false);
//                            tile.setBackground(Color.white);
//                            questionTileList.remove(tile);
//                            minelist.remove(tile);

                            // Show question dialog
//                            handleQuestionTile(tile);
                        } /*else if (bonusTileList.contains(tile)) {
                            // Handle bonus tile
                            handleBonusTile(tile);
                        } */ else {
                            // Regular mine - just reveal it
                            if (!tile.getText().equals("#")) {
                                if (!tile.getText().equals("O")) {
                                    flagged++;
                                }
                            }

                            tile.setText("#");
                            tile.setForeground(Color.cyan);
//                            tile.setEnabled(false);
//                            tile.setBackground(Color.white);
                        }
                    } else if (bonusTileList.contains(tile)) {
                        // Handle bonus tile
                        handleBonusTile(tile);
                    } else {
                        // Safe tile - reveal normally
                        checkMine(tile.r, tile.c);
//                        return;
//                        flagged--;
                    }
                }
            }
        }
        checkWinCondition();
        textLabelFlagged.setText(Integer.toString(flagged));
    }

    void handleQuestionTile(Minetile tile) {
        tile.setText("?");
        tile.setEnabled(false);
        tile.setBackground(Color.white);

        CustomDialog.showCustomWarningMess(null, "Question Tile", "Question");
        //JOptionPane.showMessageDialog(null, "Question Tile", "Question", JOptionPane.WARNING_MESSAGE);
        if (gameOver) { // IF not answered when game ends == set default
            tile.setEnabled(true);
            tile.setText("?");
            tile.setBackground(Color.black);
            tile.setForeground(Color.green);
            return;
        } // new
//        if (!timer.isRunning()) {
//            revealMines();
//            return;
//        }

        // Remove from lists
        questionTileList.remove(tile);
        minelist.remove(tile);

        // Get question
        if (questionList != null && questionList.length > 0) {
            int randomQuestionIndex = random.nextInt(questionList.length);
            tempQuestion = questionList[randomQuestionIndex][0];
            tempAnswer = questionList[randomQuestionIndex][1];
            questionList = removeQuestion(questionList, tempQuestion);
        }

        String answer = CustomDialog.showCustomInputDialog(frame, tempQuestion, "Question Tile");
        //    String answer = JOptionPane.showInputDialog(frame, tempQuestion, "Question Tile", JOptionPane.QUESTION_MESSAGE);
        if (gameOver) {
            tile.setEnabled(true);
            tile.setBackground(Color.black);
            tile.setForeground(Color.green);
            return;
        } // new
//        if (!timer.isRunning()) {
//            revealMines();
//            return;
//        }
//        checkWinCondition(); // new
        if (answer != null && answer.equalsIgnoreCase(tempAnswer)) {
            tile.setBackground(Color.green);
            tile.setText("✓");
            tile.setForeground(Color.black);
            checkMine(tile.r, tile.c);
            updateRevealedTileCounts();
        } else {
            enableSurroundingTiles(tile);
            CustomDialog.showCustomError(frame, "Wrong answer!\nSurrounding tiles reactivated.");
            //JOptionPane.showMessageDialog(frame, "Wrong answer! Surrounding tiles reactivated.", "Incorrect", JOptionPane.ERROR_MESSAGE);
            textLabel.setText("" + (mineCount + newMine));
        }
        checkWinCondition();
    }

//    void activatePower1() {
//        if (gameOver) {
//            return;
//        }
//
//        // Cancel existing timer if any
//        if (power1Timer != null && power1Timer.isRunning()) {
//            power1Timer.stop();
//        }
//
//        timer.stop();
//        power1Timer = new Timer(10000, e -> {
//            if (!gameOver) {  // Only restart if game is still running
//                timer.start();
//            }
//        });
//        power1Timer.setRepeats(false);
//        power1Timer.start();
//    }
    void pauseTimePower() {
        if (timer.isRunning()) {
            timer.stop();
            CustomDialog.showCustomMessage(frame, "Timer paused for 30 seconds!", "Bonus");
            //JOptionPane.showMessageDialog(frame, "Bonus: Timer paused for 30 seconds!");
//            timer.start();
//            timer.stop();
            if (pauseTimer != null && pauseTimer.isRunning()) {
                pauseTimer.stop();
            }
            timePaused += 30000;

            pauseTimer = new Timer(timePaused, e -> {
//                if (!gameOver && !timer.isRunning()) {
//                    timer.start(); // Resume the timer only if the game isn't over
//                    timePaused = 0;
//                }
                if (!gameOver) {
                    timer.start();
                    timePaused = 0;
                }
                if (gameOver || gameLost) {
                    pauseTimer.stop();
                    return;
                }
//                    JOptionPane.showMessageDialog(frame, "Timer resumed!");
            });
            pauseTimer.setRepeats(false);
            pauseTimer.start();
        }
    }

    private void handleReviveResponse(Minetile tile, boolean revive) {
        if (revive) {
            if (!tile.getText().equals("#")) {
                flagged++;
            }
            power3--;
            power3Lbl.setText("" + power3);
            gameLost = false;
            gameOver = false;
            swapBonusWithBomb = false;
            tile.setEnabled(true);
//            tile.setBackground(Color.black);
            tile.setText("#");
            tile.setForeground(Color.cyan);
            textLabelFlagged.setText(Integer.toString(flagged));
            timer.start(); // Resume the game timer
        } else {
            gameLost = true;
            swapBonusWithBomb = true;
            revealMines();
            playSound("gameover.wav");
            CustomDialog.showCustomError(frame, "Game Over:\nYou dug up a Virus!");
            //JOptionPane.showMessageDialog(frame, "Game Over: You dug up a Virus", "Game Over", JOptionPane.ERROR_MESSAGE);
            textLabel1.setText("Game Over: You dug up a Virus");
        }
    }

    void updatePowerUps() {
        String sql = "update account set power1 = " + power1 + ", power2 = " + power2 + ", power3 = " + power3 + " where username = '" + menu.username + "'";
        try {
            Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/ViperDB", "root", "root");
            Statement st = con.createStatement();
            st.executeUpdate(sql);
            con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Failed");
        }
    }

//    private void handleTimerTick() {
//        if ("blitz".equals(level)) {
//            handleBlitzModeTimer();
//        } else {
//            handleNormalModeTimer();
//        }
//        updateScoreDisplay();
////        checkSpecialEvents();
//    }
//    private void handleBlitzModeTimer() {
//        elapsedTime--;
//        timeScore = elapsedTime;
//
//        if (elapsedTime <= 0) {
//            handleTimeExpiration();
//        }
//    }
//    private void handleNormalModeTimer() {
//        elapsedTime++;
//        elapsedTime2++;
//
//        if (elapsedTime >= scoreTimeStart) {
////            updateScoreBasedOnElapsedTime();
//        }
//
//        if (elapsedTime > 60 && (start && elapsedTime - lastBombTime >= 60)) {
////            handleBombAddition();
//        }
//    }
//    private void handleTimeExpiration() {
//        revealMines();
//        JOptionPane.showMessageDialog(frame,
//                "Time's up! Game Over.",
//                "Game Over",
//                JOptionPane.ERROR_MESSAGE
//        );
//        textLabel1.setText("Game Over: Time's up");
//        timer.stop();
//        gameOver = true;
//    }
    // New method to update time display
//    private void updateScoreDisplay() {
//        String timeText = "blitz".equals(level)
//                ? String.format("Time: %02d", elapsedTime)
//                : "Time: " + elapsedTime;
//
//        timerLabel.setText(timeText + "s");
//        scoreLabel.setText("Score: " + timeScore);
//    }
    void removeAllTimers() {
        if (removeTimer != null && removeTimer.isRunning()) {
            removeTimer.stop();
        }
        if (pauseTimer != null && pauseTimer.isRunning()) {
            pauseTimer.stop();
        }
        if (power1Timer != null && power1Timer.isRunning()) {
            power1Timer.stop();
        }
        if (countdownTimer != null && countdownTimer.isRunning()) {
            countdownTimer.stop();
        }
    }

}
