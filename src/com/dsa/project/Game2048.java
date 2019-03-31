package com.dsa.project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Game2048 extends JPanel {
    private static final Color BG_COLOR = new Color(0x1D1D1D);
    private static final String FONT_NAME = "Arial";
    private static final int TILE_SIZE = 100;
    private static final int TILES_MARGIN = 15;

    private Tile[] GameTiles;
    private boolean isWon = false;
    private boolean isLost = false;
    private int myScore = 0;

    public Game2048() {
        setPreferredSize(new Dimension(1000, 1000));
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    reset();
                }
                if (!canMove()) {
                    isLost = true;
                }

                if (!isWon && !isLost) {
                    switch (e.getKeyCode()) {
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

                if (!isWon && !canMove()) {
                    isLost = true;
                }
                repaint();
            }
        });
        reset();
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
    }

    public void left() {
        boolean needAddTile = false;
        for (int i = 0; i < 4; i++) {
            Tile[] line = getLine(i);
            Tile[] merged = mergeLine(moveLine(line));
            setLine(i, merged);
            if (!needAddTile && !compare(line, merged)) {
                needAddTile = true;
            }
        }

        if (needAddTile) {
            addTile();
        }
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
        List<Tile> list = availableSpace();
        if (!availableSpace().isEmpty()) {
            int index = (int) (Math.random() * list.size()) % list.size();
            Tile emptyTime = list.get(index);
            emptyTime.setValue(Math.random() < 0.7 ? 2 : 4);  //chance of spawning 4 is less than 2
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

    private Tile[] moveLine(Tile[] oldLine) {
        LinkedList<Tile> l = new LinkedList<Tile>();
        for (int i = 0; i < 4; i++) {
            if (!oldLine[i].isEmpty())
                l.addLast(oldLine[i]);
        }
        if (l.size() == 0) {
            return oldLine;
        } else {
            Tile[] newLine = new Tile[4];
            ensureSize(l, 4);
            for (int i = 0; i < 4; i++) {
                newLine[i] = l.removeFirst();
            }
            return newLine;
        }
    }

    private Tile[] mergeLine(Tile[] oldLine) {
        LinkedList<Tile> list = new LinkedList<Tile>();
        for (int i = 0; i < 4 && !oldLine[i].isEmpty(); i++) {
            int num = oldLine[i].getValue();
            if (i < 3 && oldLine[i].getValue() == oldLine[i + 1].getValue()) {
                num *= 2;
                myScore += num;
                int ourTarget = 2048;
                if (num == ourTarget) {
                    isWon = true;
                }
                i++;
            }
            list.add(new Tile(num));
        }
        if (list.size() == 0) {
            return oldLine;
        } else {
            ensureSize(list, 4);
            return list.toArray(new Tile[4]);
        }
    }

    private static void ensureSize(java.util.List<Tile> l, int s) {
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

    private void setLine(int index, Tile[] re) {
        System.arraycopy(re, 0, GameTiles, index * 4, 4);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(BG_COLOR);
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
        g.setColor(tile.getBackground());
        g.fillRoundRect(xOffset, yOffset, TILE_SIZE, TILE_SIZE, 10, 10);
        g.setColor(tile.getNumberColor());
        final int size = value < 100 ? 36 : value < 1000 ? 32 : 24;
        final Font font = new Font(FONT_NAME, Font.PLAIN, size);
        g.setFont(font);

        String s = String.valueOf(value);
        final FontMetrics fm = getFontMetrics(font);

        final int w = fm.stringWidth(s);
        final int h = -(int) fm.getLineMetrics(s, g).getBaselineOffsets()[2];

        // draw 2048 box
        g.setColor(new Color(0x1D1D1D));                        //2048 box to anti-alias
        g.fillRect(20, 3, 150, 70);

        // draw 2048
        g.setColor(new Color(0xBEBEBE));
        g.setFont(new Font(FONT_NAME, Font.BOLD, 60));
        g.drawString("2048", 30, 65);

        // draw text in tiles
        g.setColor(new Color(0xeaeaea));

        if(value != 0) {
            if (value <= 8) {    //one-digit number
                g.drawString(s, xOffset + (TILE_SIZE - w) / 2 - 5, yOffset + TILE_SIZE - (TILE_SIZE - h) / 2 + 3);
            } else if (value <= 64) {   //two-digit number
                g.drawString(s, xOffset + (TILE_SIZE - w) / 2 - 15, yOffset + TILE_SIZE - (TILE_SIZE - h) / 2 + 3);
            } else if (value <= 512) {  //three-digit number
                g.setFont(new Font(FONT_NAME, Font.BOLD, 50));
                g.drawString(s, xOffset + (TILE_SIZE - w) / 2  - 17, yOffset + TILE_SIZE - (TILE_SIZE - h) / 2 + 4);
            } else if (value <= 2048) { //four-digit number
                g.setFont(new Font(FONT_NAME, Font.BOLD, 40));
                g.drawString(s, xOffset + (TILE_SIZE - w) / 2 - 17, yOffset + TILE_SIZE - (TILE_SIZE - h) / 2 + 5);
            }
        }

        //draw message 
        if (isWon || isLost) {
            g.setColor(new Color(0x393939));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(new Color(0xdddddd));
            g.setFont(new Font(FONT_NAME, Font.BOLD, 48));
            if (isWon) {
                g.drawString("Victory!", 150, 310);
            }
            if (isLost) {
                g.drawString("Game Over!", 105, 310);
            }
            if (isWon || isLost) {
                g.setFont(new Font(FONT_NAME, Font.BOLD, 25));
                g.drawString("Press Space to restart", 110, getHeight() - 40);
            }
        }

        //Score
        g.setColor(new Color(0xBEBEBE));
        g.fillRect(280, 25, 140, 40 );          //Score box
        g.setFont(new Font(FONT_NAME, Font.BOLD, 18));
        g.setColor(Color.BLACK);
        g.drawString("Score: " + myScore, 300, 50);


    }

    private static int offsetCoors(int arg) {
        return arg * (TILES_MARGIN + TILE_SIZE) + TILES_MARGIN;
    }

    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setTitle("2048 Game");
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setSize(490, 700);
        //game.pack();
        window.setResizable(false);

        window.add(new Game2048());

        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}
