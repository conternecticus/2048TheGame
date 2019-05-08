package com.dsa.project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;
import javax.imageio.ImageIO;

public class Game2048 extends JPanel {
    private static final Color BACKGROUND = new Color(0x1D1D1D);
    private static final String FONT_NAME = "Arial";
    private static final int TILE_SIZE = 100;
    private static final int TILES_MARGIN = 15;

    private Tile[] GameTiles;
    private boolean isWon = false;
    private boolean isLost = false;
    private int myScore = 0;
    private Image image = ImageIO.read(new File("Obstacle.png"));;


    public Game2048() throws IOException {
        setFocusable(true);                 //To ensure that keyboard focus is available, so keyboard events are fired
        addKeyListener(new KeyAdapter() {   //KeyAdapter is an object that registered to receive events by using addKeyListener method
            @Override
            public void keyPressed(KeyEvent keyPressed) {   //An object of class KeyEvent is passed to the KeyAdapter so KeyAdapter can handle with keyboard events
                if (keyPressed.getKeyCode() == KeyEvent.VK_SPACE) {     //Reset the game when players press SPACE
                    reset();
                }
                if (!canMove()) {
                    isLost = true;
                }

                if (!isWon && !isLost) {
                    switch (keyPressed.getKeyCode()) {
                        case KeyEvent.VK_LEFT:
                            left();

                            break;
                        case KeyEvent.VK_RIGHT:
                            right();
                            break;
                        case KeyEvent.VK_DOWN:
                            down();
                            break;
                        case KeyEvent.VK_UP:
                            up();
                            break;
                    }
                }
                repaint();      //To refresh the viewing area when wanting the game to be reset
            }   //End of overridden keyPress method
        });
        reset();    //To really reset the game
    }

    public void reset() {
        myScore = 0;
        isWon = false;
        isLost = false;
        GameTiles = new Tile[4 * 4];
        for (int i = 0; i < GameTiles.length; i++) {
            GameTiles[i] = new Tile();
        }
        addTile();  //spawn 2 random tiles
        addTile();
        addObstacle();
    }

    public void left() {
        boolean needAddTile = false;

        for (int i = 0; i < 4; i++) {       //move all 4 lines
            Tile[] line = getLine(i);
            Tile[] merged = mergeLine(moveLine(line));      //merged line or moved if not merge-able
            setLine(i, merged);
            if (!needAddTile && !compare(line, merged)) {
                needAddTile = true;
            }
        }

        if (needAddTile) {
            addTile();
        }

    }

    private Tile[] rotate(int angle) {
        Tile[] newTiles = new Tile[16];
        int offsetX = 3, offsetY = 3;
        if (angle == 90) {
            offsetY = 0;
        } else if (angle == 270) {
            offsetX = 0;
        }

        double rad = Math.toRadians(angle);
        int cos = (int) Math.cos(rad);
        int sin = (int) Math.sin(rad);
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                int newX = (x * cos) - (y * sin) + offsetX;
                int newY = (x * sin) + (y * cos) + offsetY;
                newTiles[(newX) + (newY) * 4] = tileAt(x, y);

            }
        }
        return newTiles;
    }

    private Tile[] moveLine(Tile[] oldLine) { //This method makes all the tiles stack to one direction, not merge them.
        LinkedList<Tile> l = new LinkedList<Tile>();
        Tile[] newLine = new Tile[4];
        if(((oldLine[0].getValue() + oldLine[1].getValue() + oldLine[2].getValue() + oldLine[3].getValue())%2)==0) {  //When the line has no obstacle
            for (int i = 0; i < 4; i++) {
                if (!oldLine[i].isEmpty())
                    l.addLast(oldLine[i]);
            }
            if (l.size() == 0) {
                return oldLine;
            } else {
                ensureSize(l, 4); //Ensure that the new stacked line must have 4 elements
                for (int i = 0; i < 4; i++) {
                    newLine[i] = l.removeFirst();
                }
            }
        }
        else{
            if(oldLine[1].getValue()==-1){
                for(int i=2; i<4; i++){
                    if(!oldLine[i].isEmpty())
                        l.addLast(oldLine[i]);
                }
                if (l.size() == 0) {
                    return oldLine;
                } else {
                    ensureSize(l, 2); //Ensure that the new stacked line must have 2 elements
                    newLine[0] = oldLine[0];
                    newLine[1] = oldLine[1];
                    for (int i = 2; i < 4; i++) {
                        newLine[i] = l.removeFirst();
                    }
                }
            }
            if(oldLine[2].getValue()==-1){
                for(int i=0; i<2; i++){
                    if(!oldLine[i].isEmpty())
                        l.addLast(oldLine[i]);
                }
                if (l.size() == 0) {
                    return oldLine;
                } else {
                    ensureSize(l, 2); //Ensure that the new stacked line must have 2 elements
                    for (int i = 0; i < 2; i++) {
                        newLine[i] = l.removeFirst();
                        newLine[2] = oldLine[2];
                        newLine[3] = oldLine[3];
                    }
                }
            }
            if(oldLine[0].getValue()==-1){
                for (int i = 1; i < 4; i++) {
                    if (!oldLine[i].isEmpty())
                        l.addLast(oldLine[i]);
                }
                if (l.size() == 0) {
                    return oldLine;
                } else {
                    ensureSize(l, 3); //Ensure that the new stacked to the right of the obstacle line must have 3 elements
                    newLine[0] = oldLine[0];
                    for (int i = 1; i < 4; i++) {
                        newLine[i] = l.removeFirst();
                    }
                }
            }
            if(oldLine[3].getValue()==-1){
                for (int i = 0; i < 3; i++) {
                    if (!oldLine[i].isEmpty())
                        l.addLast(oldLine[i]);
                }
                if (l.size() == 0) {
                    return oldLine;
                } else {
                    ensureSize(l, 3); //Ensure that the new stacked to the right of the obstacle line must have 3 elements
                    for (int i = 0; i < 3; i++) {
                        newLine[i] = l.removeFirst();
                    }
                    newLine[3] = oldLine[3];
                }
            }
        }
        return newLine;
    }

    private Tile[] mergeLine(Tile[] oldTile) {
        LinkedList<Tile> list = new LinkedList<Tile>();
        Tile[] newLine = new Tile[4];
        if(((oldTile[0].getValue() + oldTile[1].getValue() + oldTile[2].getValue() + oldTile[3].getValue())%2)==0) {  //When the line has no obstacle
            for (int i = 0; i < 4 && !(oldTile[i].isEmpty()); i++) {        // oldLine is NOT empty
                int num = oldTile[i].getValue();                            // current value of current Tile
                if ((i < 3) && (oldTile[i].getValue() == oldTile[i + 1].getValue())) {  //if current Tile and next Tile is equal
                    num *= 2;                       // num is now doubled
                    myScore += num;                 // update score

                    if (num == 2048) {
                        isWon = true;
                    }

                    i++;
                }
                list.add(new Tile(num));
            }
            if (list.size() == 0) {
                return oldTile;
            } else {
                ensureSize(list, 4);
                for(int i=0;i<4;i++)
                    newLine[i]=list.remove();
            }
        }
        else{                                               // when there's an obstacle
            if(oldTile[1].getValue()==-1){                  // if the obstacle is in index 1, then we just need to merge the two Tiles to the right of the obstacle
                if(oldTile[2].getValue()==oldTile[3].getValue()){   // do the merging for the two last tiles to the right of the obstacle
                    int num = oldTile[2].getValue();
                    num *=2;
                    myScore += num;
                    if (num == 2048) {
                        isWon = true;
                    }
                    list.add(new Tile(num));
                }
                if (list.size() == 0) {
                    return oldTile;
                } else {
                    ensureSize(list, 2);                // ensure list is having 2 Tiles, which
                    newLine[0] = oldTile[0];
                    newLine[1] = oldTile[1];
                    newLine[2] = list.remove();
                    newLine[3] = list.remove();
                }
            }
            if(oldTile[2].getValue()==-1){
                if(oldTile[0].getValue()==oldTile[1].getValue()){
                    int num = oldTile[1].getValue();
                    num *=2;
                    myScore += num;
                    if (num == 2048) {
                        isWon = true;
                    }
                    list.add(new Tile(num));
                }
                if (list.size() == 0) {
                    return oldTile;
                } else {
                    ensureSize(list, 2);
                    newLine[0] = list.remove();
                    newLine[1] = list.remove();
                    newLine[2] = oldTile[2];
                    newLine[3] = oldTile[3];
                }
            }
            if(oldTile[0].getValue()==-1){
                for(int i=1; i<4 && !oldTile[i].isEmpty();i++){
                    int num = oldTile[i].getValue();                            // current value of current Tile
                    if ((i < 3) && (oldTile[i].getValue() == oldTile[i + 1].getValue())) {  //if current Tile and next Tile is equal
                        num *= 2;                       // num is now doubled
                        myScore += num;                 // update score

                        if (num == 2048) {
                            isWon = true;
                        }

                        i++;
                    }
                    list.add(new Tile(num));
                }
                if (list.size() == 0) {
                    return oldTile;
                } else {
                    ensureSize(list, 3);
                    newLine[0] = oldTile[0];
                    for(int i=1;i<4;i++)
                        newLine[i]=list.remove();
                }
            }
            if(oldTile[3].getValue()==-1){
                for(int i=0; i<3 && !oldTile[i].isEmpty();i++){
                    int num = oldTile[i].getValue();                            // current value of current Tile
                    if ((i < 2) && (oldTile[i].getValue() == oldTile[i + 1].getValue())) {  //if current Tile and next Tile is equal
                        num *= 2;                       // num is now doubled
                        myScore += num;                 // update score

                        if (num == 2048) {
                            isWon = true;
                        }

                        i++;
                    }
                    list.add(new Tile(num));
                }
                if (list.size() == 0) {
                    return oldTile;
                } else {
                    ensureSize(list, 3);
                    for(int i=0;i<3;i++)
                        newLine[i]=list.remove();
                    newLine[3] = oldTile[3];
                }
            }
        }
        return newLine;
    }

    private static void ensureSize(List<Tile> l, int s) {
        while (l.size() != s) {
            l.add(new Tile());
        }
    }

    private Tile[] getLine(int index) {
        Tile[] result = new Tile[4];
        for (int i = 0; i < 4; i++) {
            result[i] = tileAt(i, index);
        }
        return result;
    }

    public void right() {
        GameTiles = rotate(180);
        left();
        GameTiles = rotate(180);
    }

    public void up() {
        GameTiles = rotate(270);
        left();
        GameTiles = rotate(90);
    }

    public void down() {
        GameTiles = rotate(90);
        left();
        GameTiles = rotate(270);
    }

    private Tile tileAt(int x, int y) {
        return GameTiles[x + y * 4];
    }

    private void addTile() {
        List<Tile> list = availableSpace();     //List of only spaces in the GameTiles
        if (!list.isEmpty()) {
            double randy = Math.random(); //a random number from 0.0 to 1.0, for debug purpose
            int index = (int) (randy * list.size()) % list.size(); //create a random index to add a new tile
            Tile emptyTime = list.get(index);   //emptyTime conveys the new value (2 or 4)
            emptyTime.setValue(Math.random() < 0.7 ? 2 : 4); //  chance of spawning 4 is less than 2, 0.7 here can be any number that > 0.5 to make sure that spawning 4 is less than 2
        }
    }


    private void addObstacle() {
        List<Tile> list = availableSpace();
        if (!list.isEmpty()) {
            double randy = Math.random(); //a random number from 0.0 to 1.0, for debug purpose
            int index = (int) (randy * list.size()) % list.size(); //create a random index to add a new tile
            Tile emptyTime = list.get(index);
            emptyTime.setValue(-1);
        }
    }

    private List<Tile> availableSpace() {
        final List<Tile> list = new ArrayList<Tile>(16);    //declare a list with fixed amount of tiles
        for (Tile t : GameTiles) {
            if (t.isEmpty()) {
                list.add(t);
            }
        }
        return list;
    }

    private boolean isFull() {
        return availableSpace().size() == 0;
    }

    boolean canMove() {
        if (!isFull()) {
            return true;
        }
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                Tile t = tileAt(x, y);
                if ((x < 3 && t.getValue() == tileAt(x + 1, y).getValue())
                        || ((y < 3) && t.getValue() == tileAt(x, y + 1).getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean compare(Tile[] line1, Tile[] line2) {
        if (line1 == line2) {
            return true;
        } else if (line1.length != line2.length) {
            return false;
        }

        for (int i = 0; i < line1.length; i++) {
            if (line1[i].getValue() != line2[i].getValue()) {
                return false;
            }
        }
        return true;
    }



    private void setLine(int index, Tile[] re) {
        System.arraycopy(re, 0, GameTiles, index * 4, 4);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(BACKGROUND);
        g.fillRect(0, 0, this.getSize().width, this.getSize().height);
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                drawTile(g, GameTiles[x + y * 4], x, y);
            }
        }
    }

    private void drawTile(Graphics g2, Tile tile, int x, int y) {
        Graphics2D g = ((Graphics2D) g2);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR);


        int value = tile.getValue();
        int xOffset = offsetCoors(x);
        int yOffset = offsetCoors(y+1);

        BufferedImageOp op = new BufferedImageOp() {
            @Override
            public BufferedImage filter(BufferedImage src, BufferedImage dest) {
                return null;
            }

            @Override
            public Rectangle2D getBounds2D(BufferedImage src) {
                return null;
            }

            @Override
            public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel destCM) {
                return null;
            }

            @Override
            public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
                return null;
            }

            @Override
            public RenderingHints getRenderingHints() {
                return null;
            }
        };
        ImageObserver ob = new ImageObserver() {
            @Override
            public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
                return false;
            }
        };
        if(value == -1){
            g.setColor(tile.getBackground());
            g.fillRoundRect(xOffset, yOffset, TILE_SIZE, TILE_SIZE, 10, 10);
            g.drawImage(image, xOffset, yOffset, TILE_SIZE, TILE_SIZE, ob);
        }
        else {

            g.setColor(tile.getBackground());
            g.fillRoundRect(xOffset, yOffset, TILE_SIZE, TILE_SIZE, 10, 10);

            // draw 2048 box
            g.setColor(new Color(0x1D1D1D));                        //2048 box to anti-alias
            g.fillRect(20, 3, 150, 70);

            // draw 2048
            g.setColor(new Color(0x79c4b4));
            g.setFont(new Font(FONT_NAME, Font.BOLD, 60));
            g.drawString("2048", 30, 65);

            // draw text in tiles
            g.setColor(new Color(0xeaeaea));

            final int size = value <= 64 ? 60 : value <= 512 ? 50 : 40;
            final Font font = new Font(FONT_NAME, Font.BOLD, size);
            g.setFont(font);

            String s = String.valueOf(value);
            final FontMetrics fm = getFontMetrics(font);

            //fm: sun.font.FontDesignMetrics[font=java.awt.Font[family=Arial,name=Arial,style=bold,size=60]ascent=56, descent=12, height=70]
            final int w = fm.stringWidth(s);        // w = 34, Returns the total advance width for showing the specified String in this Font.
            //          Advance width is the distance from the origin of the text
            // to the position of a subsequently rendered string.

            final int h = -(int) fm.getLineMetrics(s, g).getBaselineOffsets()[2];   //h = 55
            //Returns the baseline offsets of the text, relative to the baseline of the text.

            if (value != 0) {
                g.drawString(s, xOffset + (TILE_SIZE - w) / 2, yOffset + TILE_SIZE - (TILE_SIZE - h) / 2 - 5);
            }
        }

        //draw message
        if (isWon || isLost) {
            g.setColor(new Color(0x393939));                    //set game ending background color
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(new Color(0x79c4b4));

            g.setFont(new Font(FONT_NAME, Font.BOLD, 25));
            g.drawString("Press Space to restart", 110, getHeight() - 40);

            g.setFont(new Font(FONT_NAME, Font.BOLD, 48));

            if (isWon) {
                g.drawString("Victory!", 150, 310);
            }
            if (isLost) {
                g.drawString("Game Over!", 105, 310);
            }
        }

        //Score
        g.setColor(new Color(0x79c4b4));
        g.fillRect(280, 25, 140, 40 );          //Score box
        g.setFont(new Font(FONT_NAME, Font.BOLD, 18));
        g.setColor(Color.BLACK);
        g.drawString("Score: " + myScore, 300, 50);

    }

    private static int offsetCoors(int arg) {
        return arg * (TILES_MARGIN + TILE_SIZE) + TILES_MARGIN;
    }

    public static void main(String[] args) throws IOException {
        JFrame game = new JFrame();
        game.setTitle("2048 DSA IU");                // set title of game window
        game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);   //exit problem when window is closed
        game.setSize(490, 700);                             //set window size

        game.setResizable(false);                                       // cannot resize window

        game.add(new Game2048());                                       // add new Game2048 object to JFrame

        game.setLocationRelativeTo(null);                               // window appear in the middle of the screen
        game.setVisible(true);                      // set visibility of window, program will stop if 'false'
    }
}
