/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package play;

// Power Ups
// Need timer

import Menu.playFrame;
import fistFrame.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class Minesweeper implements ActionListener {
    // adding new class with overriding the Jbutton(row number, col number)
    private class Minetile extends JButton{
        int r,c;
        
        public Minetile(int r, int c){
         this.r = r;
         this.c = c;
        }
    }
    
    public String level = "";
    
    int tileSize = 70;
    int numRows;
    int mineCount;
    int numCols;
    int boardWidth;
    int boardHeight;
    int tilesClicked = 0;
    int elapsedTime = 0;
    int timeScore;
    
    Timer timer;
    JFrame frame = new JFrame("Minesweeper");
    JLabel timerLabel = new JLabel("Time : 0s");
    JLabel textLabel = new JLabel();
    JLabel scoreLabel = new JLabel("Score : " + Integer.toString(timeScore));
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JPanel outerPanel = new JPanel(new BorderLayout());
    JButton BtnBack = new JButton("Back");
    JButton BtnRestart = new JButton("Restart");
    
    // Reference of the tile we create
    Minetile[][] board;
    Minetile firstClick = null; // first click tile
    
    Random random = new Random();
    
    ArrayList<Minetile> minelist; // List of mines
    
    boolean gameOver = false;
    boolean start = false;
    
    public Minesweeper(int minecount, int rowsNcols){ 
        this.mineCount = minecount;
        this.numRows = rowsNcols;
        numCols = numRows;
        board = new Minetile[numRows][numCols];
        boardWidth = 1100;
        boardHeight = 700 ;
        timeScore = 100;
        
        
        frame.setSize(boardWidth,boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());
        
        // "Minesweeper" Text Panel
        textLabel.setFont(new Font("Arial",Font.BOLD,25));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Minesweeper : " + Integer.toString(mineCount));
        textLabel.setOpaque(true);
        textLabel.setBounds(30,100,250,25);
        
        
        //timer
        timerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        timerLabel.setBounds(50, 150, 200, 20);
        
        // Board panel
        boardPanel.setLayout(new GridLayout(numRows,numCols)); // Declaring tiles rows and colums
        
        // Outer Panel
        outerPanel.setBorder(BorderFactory.createEmptyBorder(50, 400, 50, 50)); // Margin
        outerPanel.add(boardPanel, BorderLayout.CENTER);
        
        //Button
        BtnBack.setBounds(30,600,65,30);
        BtnRestart.setBounds(110,600,85,30);
        scoreLabel.setBounds(50,200,200,20);
        
        BtnBack.addActionListener(this);
        BtnRestart.addActionListener(this);
        
        frame.add(scoreLabel);
        frame.add(timerLabel);
        frame.add(textLabel);
        frame.add(BtnRestart);
        frame.add(BtnBack);
        frame.add(outerPanel);
        
        // timer score function
        timer = new Timer(1000, new ActionListener() {
        @Override
            public void actionPerformed(ActionEvent e) {
                elapsedTime++; // Decrease time
                timerLabel.setText("Time : " + elapsedTime + "s");
                scoreLabel.setText("Score :" + Integer.toString(timeScore));
                
                // decrement and timeStart should vary on levels
                if (elapsedTime >= 25){ // after 25 seconds
                    if (elapsedTime % 5 == 0) timeScore -= 1; // score decreases by 1 per 5 seconds 
                }
                    
                
//                if (timeLeft == 0) {
//                    timer.stop();
//                    gameOver = true;
//                    revealMines();
//                    textLabel.setText("Time Over! You Lost");
//                }
            }
        });
        
        for(int r =0;r<numRows;r++){
            for(int c =0; c< numCols;c++){
                Minetile tile = new Minetile(r,c);
                board[r][c] = tile;
                
                tile.setFocusable(false);
                tile.setMargin(new Insets(0,0,0,0));
                tile.setFont(new Font("Arial Unicode Ms", Font.PLAIN,30));
//                tile.setText("1");
                tile.addMouseListener(new MouseAdapter(){
                    @Override
                    public void mousePressed(MouseEvent e){
                        if (gameOver) return; // Lost
                        
                        if (!start) { // The first Click
                            firstClick = tile;
                            start = true;
                            timer.start();
                            setMines(firstClick);
                        }
                        
                        Minetile tile = (Minetile) e.getSource();
                        
                        // Left click
                        if (e.getButton() == MouseEvent.BUTTON1){
                            // Not clicked yet
                            if (tile.getText()== ""){
                               if (minelist.contains(tile)){// if There's a bomb
                                    revealMines();
                                }
                                else{ // Check neighbor tiles
                                    checkMine(tile.r,tile.c);
                                }
                            }
                        }
                        else if (e.getButton() == MouseEvent.BUTTON3){ // right click - setting flag
                            if (tile.getText() == "" && tile.isEnabled()){ // adding Flag
                                tile.setText("F");
                            }
                            else if (tile.getText() == "F"){ // removing Flag
                                tile.setText("");
                            }
                        }
                    }
                });
                
                boardPanel.add(tile);
            }
        }
        
        frame.setVisible(true);
    }
    
    void setMines(Minetile firstClick){
        minelist = new ArrayList<Minetile>();
        
//         Declaring mines
        int mineleft = mineCount;
        while (mineleft > 0){
            int r = random.nextInt(numRows); // 0 to numRows value
            int c = random.nextInt(numCols); // 0 ti numCols value
            
            Minetile tile = board[r][c];
            
            // ensures neighbor tiles are mine-free
            if (!minelist.contains(tile) && !isNeighbor(firstClick, tile)) {
                minelist.add(tile);
                mineleft -=1;
            }
        }

    }
    
    void revealMines(){
        for (int i =0;i<minelist.size();i++){
            Minetile tile = minelist.get(i);
            tile.setText("#"); // Bomb text
        }
        BtnRestart.setText("Again");
        BtnBack.setEnabled(true);
        BtnRestart.setEnabled(true);
        gameOver = true;
        scoreLabel.setText("Score :" + Integer.toString(timeScore));
        timer.stop();
        textLabel.setText("Game Over");
    }
    
    void checkMine(int r, int c){
        // if the neighbor tiles is out of bounds = exit
        if (r < 0 || r >= numRows || c < 0 || c >= numCols){
            return;
        }
        Minetile tile = board[r][c];
        // if tile is clicked = exit
        if (!tile.isEnabled()) return;
        
        tile.setEnabled(false); // disabling button
        tilesClicked += 1; // increment 'tilesClicked' if there's no bomb
        
        int minesFound = 0;
        
        // top 3 neighbor tiles
        minesFound += countMine(r-1,c-1); // top-left tile
        minesFound += countMine(r-1,c); // top-center tile
        minesFound += countMine(r-1,c+1); // top-right tile
        
        // left and right neighbor tile
        minesFound += countMine(r,c-1); // Left tile
        minesFound += countMine(r,c+1); // Right tile
        
        // Bottom 3 neighbor tile
        minesFound += countMine(r+1,c-1); // Bottom-left tile
        minesFound += countMine(r+1,c); // Bottom-Center tile
        minesFound += countMine(r+1,c+1); // Bottom-right Tile
        
        if (minesFound > 0){
            tile.setText(Integer.toString(minesFound));
            
        }else{
            tile.setText("");
            
            //Check neighboring mine
            checkMine(r-1,c-1); // top-left
            checkMine(r-1,c); // top-center
            checkMine(r-1,c+1); // top-right
            
            checkMine(r,c-1); // left
            checkMine(r,c+1); // right
            
            checkMine(r+1,c-1); // bottom-left
            checkMine(r+1,c); // bottom-center
            checkMine(r+1,c+1);// bottom-right
        }
        // Win if all tiles are clicked except with bombs
        if (tilesClicked == numRows * numCols - minelist.size()){
            // Update database score
            if (numRows == 9 && level.equals("")) level = "easyscore";
            else if (numRows == 15) level = "mediumscore";
            else if (numRows == 21) level = "hardscore";
            else level = "blitzscore";
            
            
            
            String findScore = "select "+level+" from account where username = '"+Login.username1+"'";
            String uptScore = "update account set "+level+" = "+timeScore+" where username = '"+Login.username1+"'";
            
            try {
                Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/ViperDB", "root", "root");
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(findScore);
                
                // Updating score
                if (rs.next()){
                    if (timeScore > rs.getInt(level)){
                        st.executeUpdate(uptScore);
                    }
                }
                
                st.close();
                rs.close();
                con.close();
            }catch(SQLException ex){
                ex.printStackTrace();
                System.out.println("Faied");
            }
            
            
            gameOver = true; // Players cannot click tiles anymore
            textLabel.setText("Mines Cleared");
            timer.stop();
            level = "";
        }
    }
    
    int countMine(int r, int c){
        // if out of bounds
        if (r < 0 || r >= numRows || c < 0 || c >= numCols){
            return 0 ;
        }
        if (minelist.contains(board[r][c])){ // if within tiles
            return 1;
        }
        return 0;
    }
    
    boolean isNeighbor(Minetile first, Minetile tile) {
        return Math.abs(first.r - tile.r) <= 1 && Math.abs(first.c - tile.c) <= 1;
    }
    
    public void actionPerformed(ActionEvent e){
        if (e.getSource() == BtnBack){
            int x = JOptionPane.showConfirmDialog(null, "Are you sure?", "Quit",JOptionPane.YES_NO_OPTION);
             if (x == 0){
                new playFrame();
                frame.dispose();
             } 
        }else if (e.getSource() == BtnRestart){
            int x;
             if (!gameOver){
                 x = JOptionPane.showConfirmDialog(null, "Are you sure?", "Restart",JOptionPane.YES_NO_OPTION);
                 if (x == 0){
                  frame.dispose();
                  new Minesweeper(mineCount,numRows);
                 }
             } else {
                 frame.dispose();
                 new Minesweeper(mineCount,numRows);
                 // trye pa ulit
             }
        }
    }
    
}
