/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package play;

// Power Ups
// Need timer

import Menu.playFrame;
import java.awt.*;
import java.awt.event.*;
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
    
    int tileSize = 70;
    int numRows;
    int mineCount;
    int numCols;
    int boardWidth;
    int boardHeight;
    int tilesClicked = 0;
    
    JFrame frame = new JFrame("Minesweeper");
    JLabel textLabel = new JLabel();
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
        
        
//        textPanel.setLayout(new BorderLayout());
//        textPanel.add(textLabel);
//        frame.add(textPanel,BorderLayout.NORTH);

        // Board panel
        boardPanel.setLayout(new GridLayout(numRows,numCols)); // Declaring tiles rows and colums
        
        // Outer Panel
        outerPanel.setBorder(BorderFactory.createEmptyBorder(50, 400, 50, 50)); // Margin
        outerPanel.add(boardPanel, BorderLayout.CENTER);
        
        //Button
        BtnBack.setBounds(30,600,65,30);
        BtnRestart.setBounds(110,600,85,30);
        
        BtnBack.addActionListener(this);
        BtnRestart.addActionListener(this);
        
        frame.add(textLabel);
        frame.add(BtnRestart);
        frame.add(BtnBack);
        frame.add(outerPanel);
        
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
            gameOver = true; // Players cannot click tiles anymore
            textLabel.setText("Mines Cleared");
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
