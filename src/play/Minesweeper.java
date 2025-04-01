package play;

import Menu.playFrame;
import fistFrame.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

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

    String questionLevel = "";
    String[][] questionList;
    String tempQuestion = "";
    String tempAnswer = "";
    String finalquestion = "";

    public String level = "";
    public int scoreTimeStart;
    public int timePerScore;

    private int lastBonusTime = -60; // Track the last time a bonus was added
    private Minetile currentBonusTile = null; // Track the current bonus tile

    int tileSize = 70;
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

    Timer timer;
    Timer removeTimer;
    JFrame frame = new JFrame("Minesweeper");
    JLabel timerLabel = new JLabel("Time : 0s");
    JLabel textLabel = new JLabel(); // number of mines
    JLabel textLabel1 = new JLabel(); // "Mines Cleared"
    JLabel textLabelFlagged = new JLabel(); // flagged
    JLabel textLabelMineCount = new JLabel();
    JLabel scoreLabel = new JLabel("Score : " + Integer.toString(timeScore));
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JPanel outerPanel = new JPanel(new BorderLayout());
    JButton BtnBack = new JButton("Back");
    JButton BtnRestart = new JButton("Restart");

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

    // Minesweeper(int minecout, int rowsNcols, int scoreTimeStart, int timePerScore)
    public Minesweeper(int minecount, int rowsNcols, int scoreTimeStart, int timePerScore) {
        this.mineCount = minecount;
        this.numRows = rowsNcols;
        this.scoreTimeStart = scoreTimeStart;
        this.timePerScore = timePerScore;
        numCols = numRows;
        position = new Minetile[numRows][numCols];
        boardWidth = 1100;
        boardHeight = 700;
        timeScore = 100;

        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());

        // "Minesweeper" Text Panel
        textLabel.setFont(new Font("Arial", Font.BOLD, 25));
        textLabel.setText("Minesweeper : " + Integer.toString(mineCount + newMine));
        textLabel.setOpaque(true);
        textLabel.setBounds(30, 100, 250, 25);

        // Gameover Text
        textLabel1.setFont(new Font("Arial", Font.BOLD, 25));
        textLabel1.setText("");
        textLabel1.setOpaque(true);
        textLabel1.setBounds(30, 25, 650, 25);

        // Flagged text
        textLabelFlagged.setFont(new Font("Arial", Font.BOLD, 30));
        textLabelFlagged.setText("Flagged tiles : " + Integer.toString(flagged));
        textLabelFlagged.setOpaque(true);
        textLabelFlagged.setBounds(30, 150, 250, 25);

        //timer
        timerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        timerLabel.setBounds(50, 250, 200, 20);

        // Board panel
        boardPanel.setLayout(new GridLayout(numRows, numCols)); // Declaring tiles rows and colums

        // Outer Panel
        outerPanel.setBorder(BorderFactory.createEmptyBorder(50, 400, 50, 50)); // Margin
        outerPanel.add(boardPanel, BorderLayout.CENTER);

        //Button
        BtnBack.setBounds(30, 600, 65, 30);
        BtnRestart.setBounds(110, 600, 85, 30);
        scoreLabel.setBounds(50, 300, 200, 20);

        BtnBack.addActionListener(this);
        BtnRestart.addActionListener(this);

        frame.add(scoreLabel);
        frame.add(timerLabel);
        frame.add(textLabel);
        frame.add(textLabel1);
        frame.add(textLabelFlagged);
        frame.add(BtnRestart);
        frame.add(BtnBack);
        frame.add(outerPanel);

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
                elapsedTime++; // Decrease time
                elapsedTime2++;
                timerLabel.setText("Time : " + elapsedTime + "s");
                scoreLabel.setText("Score :" + Integer.toString(timeScore));

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
                        revealMines();
                        JOptionPane.showMessageDialog(null, "Game Over: Last safe tile is now contained with Virus", "Game Over", JOptionPane.ERROR_MESSAGE);
                        textLabel1.setText("Game Over: You failed to prevent virus");
                        newMine++;
                        textLabel.setText("Minesweeper : " + Integer.toString(mineCount + newMine));

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
        });

        // Setting Tiles
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                Minetile tile = new Minetile(r, c);
                tile.setBackground(Color.black); // unclicked color
                position[r][c] = tile;

                tile.setFocusable(false);
                tile.setMargin(new Insets(0, 0, 0, 0));
                tile.setFont(new Font("Arial Unicode Ms", Font.PLAIN, 30));
//                tile.setText("1");
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
//                            setBonusTile(); // Add this line
                        }

                        Minetile tile = (Minetile) e.getSource();

                        // Left click
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            // "" means Not clicked yet
                            if (tile.getText() == "") {
                                // if There's a bomb/bonus tile
                                if (minelist.contains(tile)) {
                                    //if it's question tile
                                    if (questionTileList.contains(tile)) {
                                        tile.setText("?"); // Show question mark
                                        tile.setEnabled(false);
                                        tile.setBackground(Color.white);

                                        JOptionPane.showMessageDialog(null, "Question Tile", "Question", JOptionPane.WARNING_MESSAGE);
                                        if (!timer.isRunning()) {
                                            revealMines();
                                            return;
                                        }
                                        // TODO - IF ANSWERED CORRECTLY GIVE CLUE ELSE IF NOT ENABLE SOME TILE - DONE

                                        // Remove from minelist and questionTilelist after clicked
                                        questionTileList.remove(tile);
                                        minelist.remove(tile);

//                                         if all unclicked tile has a bomb
                                        if (!timer.isRunning()) {
                                            revealMines();
                                            return;
                                        }

                                        if (questionList != null) {
                                            int randomQuestionIndex = random.nextInt(questionList.length);
                                            tempQuestion = questionList[randomQuestionIndex][0];
                                            tempAnswer = questionList[randomQuestionIndex][1];
                                            questionList = removeQuestion(questionList, tempQuestion);
                                        }
                                        // Show question dialog
                                        String answer = JOptionPane.showInputDialog(frame, tempQuestion, "Question Tile", JOptionPane.QUESTION_MESSAGE);

                                        // if all unclicked tile has a bomb
                                        if (!timer.isRunning()) {
                                            revealMines();
                                            return;
                                        }

                                        if (answer != null && (answer.equalsIgnoreCase(tempAnswer))) { // Correct answer
                                            // Remove from mine list and question list
//                                            minelist.remove(tile);
//                                            questionTileList.remove(tile);

                                            // Visual feedback
                                            tile.setBackground(Color.green);
                                            tile.setText("✓");
                                            tile.setForeground(Color.black);

//                                            tilesClicked++;
                                            // Automatically check surrounding tiles
                                            checkMine(tile.r, tile.c);

                                        } else { // Wrong answer
                                            enableSurroundingTiles(tile);
                                            JOptionPane.showMessageDialog(frame, "Wrong answer! Surrounding tiles reactivated.", "Incorrect", JOptionPane.ERROR_MESSAGE);
                                            textLabel.setText("Minesweeper : " + Integer.toString(mineCount + newMine));
                                        }
                                        checkWinCondition();
                                    } else if (bonusTileList.contains(tile)) {
                                        handleBonusTile(tile);
                                        System.out.println("Bonus tile");
                                    } else {
                                        // else it's a bomb
                                        gameLost = true;
                                        swapBonusWithBomb = true;
                                        revealMines();
                                        JOptionPane.showMessageDialog(null, "Game Over: You dig up a Virus", "Game Over", JOptionPane.ERROR_MESSAGE);
                                        textLabel1.setText("Game Over: You dug up a Virus");
                                    }
                                }/* else if(bonusTileList.contains(tile)){
                                    handleBonusTile(tile);
                                        System.out.println("Bonus tile");
                                }*/ else { // Check neighbor tiles
                                    checkMine(tile.r, tile.c);
                                }
                            }
                        } else if (e.getButton() == MouseEvent.BUTTON3) { // right click - setting flag

                            if (tile.getText() == "" && tile.isEnabled()) { // adding Flag
                                flagged++;
                                tile.setText("O");
                                tile.setForeground(Color.YELLOW); // Set text color
                            } else if (tile.getText() == "O") { // removing Flag
                                tile.setText("");
                                if (flagged > 0) {
                                    flagged--;
                                }
                            }
                            textLabelFlagged.setText("Flagged tiles : " + Integer.toString(flagged));

                        }
                    }
                });

                boardPanel.add(tile);
            }
        }

        frame.setVisible(true);
    }

    // Setting Bonus/Question tiles
    void setQuestionTile() {
        questionTileList = new ArrayList<Minetile>();

        questionTiles = 1; // count of bonus tile

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
            Minetile bonusTile;
            tile.setText("#"); // Bomb text
            tile.setForeground(Color.red); // Set text color

            for (int j = 0; j < questionTileList.size(); j++) {
                questionTile = questionTileList.get(j);
                if (questionTile == tile) {
                    tile.setText("?"); // Bomb text
                    tile.setForeground(Color.green); // Set text color
                }
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
        scoreLabel.setText("Score :" + Integer.toString(timeScore));
        timer.stop();
//        textLabel1.setText("Game Over");
    }

    void checkMine(int r, int c) {
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
        if (tile.isEnabled() && tile.getText() == "O") {
            flagged--;
            textLabelFlagged.setText("Flagged tiles : " + Integer.toString(flagged));
        }

        tile.setBackground(Color.white); // clicked color
        tile.setEnabled(false); // disabling button
//        tilesClicked += 1; // increment 'tilesClicked' if there's no bomb

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
            int x = JOptionPane.showConfirmDialog(null, "Are you sure?", "Quit", JOptionPane.YES_NO_OPTION);
            if (x == 0) {
                new playFrame();
                frame.dispose();
            }
        } else if (e.getSource() == BtnRestart) {
            int x;
            if (!gameOver) {
                x = JOptionPane.showConfirmDialog(null, "Are you sure?", "Restart", JOptionPane.YES_NO_OPTION);
                if (x == 0) {
                    timer.stop();
                    if (removeTimer != null && removeTimer.isRunning()) {
                        removeTimer.stop();
                    }
                    frame.dispose();
                    new Minesweeper(mineCount, numRows, scoreTimeStart, timePerScore);
                }
            } else {
                timer.stop();
                if (removeTimer != null && removeTimer.isRunning()) {
                        removeTimer.stop();
                }
                frame.dispose();
                new Minesweeper(mineCount, numRows, scoreTimeStart, timePerScore);
                // trye pa ulit
            }
        }
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
                        }
                    }
                }
            }
        }
    }

    void checkWinCondition() {
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
        if (revealedCount == safeTiles) {

            // One last final question
            questionList = generateQuestion(questionLevel, "finalquestion");
            tempQuestion = questionList[0][0];
            tempAnswer = questionList[0][1];

            String answer = JOptionPane.showInputDialog(frame, tempQuestion, "Question Tile", JOptionPane.QUESTION_MESSAGE);

            if (!timer.isRunning()) {
                revealMines();
                return;
            }

            // Correct answer
            if (answer != null && answer.equalsIgnoreCase(tempAnswer)) {
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
                int oldCoin = 0;
                //            String findScore = "select "+level+" from account where username = '"+Login.username1+"'";
                String findScore = "select * from account where username = '" + Login.username1 + "'";
                try {
                    Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/ViperDB", "root", "root");
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(findScore);
                    // Updating score & coins
                    if (rs.next()) {
                        oldCoin += rs.getInt("coins") + coinsEarned;
                        if (timeScore > rs.getInt(level)) {
                            st.executeUpdate("update account set " + level + " = " + timeScore + ",coins = " + oldCoin + " where username = '" + Login.username1 + "'");
                        } else {
                            st.executeUpdate("update account set coins = " + oldCoin + " where username = '" + Login.username1 + "'");
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
                JOptionPane.showMessageDialog(frame, "You earned " + coinsEarned + " coins!");
                level = "";
            } else {
                timer.stop();
                JOptionPane.showMessageDialog(null, "Game Over: Your answer is wrong", "Game Over", JOptionPane.ERROR_MESSAGE);
                textLabel1.setText("Game Over: You failed to answer the final question");
                revealMines();
            }
        }
    }

    String[][] generateQuestion(String difficulty, String questionType) {
//        String question[];
        String sqlFindQuestions = "select question,answer from questions where diff = '" + difficulty + "' and type = '" + questionType + "' order by id asc";
        ArrayList<String[]> questionList = new ArrayList<>(); // Temporary list
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
            textLabel.setText("Minesweeper : " + (mineCount + newMine));
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
                JOptionPane.showMessageDialog(null, "Game Over: Last safe tile is now contained with Virus", "Game Over", JOptionPane.ERROR_MESSAGE);
                textLabel1.setText("Game Over: You failed to prevent virus");
            }
            return true;
        } else {
            return false;
        }
    }

    // Add the setBonusTile method
    void setBonusTile() {
        bonusTileList = new ArrayList<Minetile>();
        int bonusTiles = 1; // Only 1 Bonus Tile
        int maxAttempts = 1000; // Prevent infinite loops
        Minetile bTile;
        while (bonusTiles > 0 && maxAttempts > 0) {
            int r = random.nextInt(numRows);
            int c = random.nextInt(numCols);
            bTile = position[r][c];

            // Ensure tile is NOT a mine, NOT a question tile, and NOT adjacent to first click
            if (!minelist.contains(bTile) && !questionTileList.contains(bTile) && !bonusTileList.contains(bTile) && !isNeighbor(firstClick, bTile)) {
                bonusTileList.add(bTile);
//                minelist.add(bTile);
//                bTile.setText("B");
                bonusTiles--;
            }
            maxAttempts--;
        }

        if (bonusTiles > 0) {
            System.out.println("Warning: Bonus Tile not placed!");
        }
    }

    // Implement handleBonusTile method
    void handleBonusTile(Minetile tile) {
        tile.setBackground(Color.WHITE);
        tile.setEnabled(false);
        tile.setText("★");
        tile.setForeground(Color.BLUE);
        bonusTileList.remove(tile);
        minelist.remove(tile);

        // Add 20 points
        timeScore += 20;
        scoreLabel.setText("Score : " + timeScore);
        System.out.println("You got bonus tile");

        // Remove a random non-question mine
        checkWinCondition();
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
            minelist.add(currentBonusTile);
//            currentBonusTile.setText("");
            currentBonusTile.setForeground(Color.cyan);
        }
    }
    // you have problem here if mine swapped with bomb, the bomb disappear

    private void removeBonusTile(Minetile tile) {
        if (tile != null && bonusTileList.contains(tile) && timer.isRunning()) {
            if (tile.isEnabled() && !swapBonusWithBomb) {
                tile.setText("");
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

}
