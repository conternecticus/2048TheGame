import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Game2048 extends JPanel {
    private static final Color BACKGROUND = new Color(0x1D1D1D);
    private static final String FONT_NAME = "Arial";
    private static final int TILE_SIZE = 100;
    private static final int TILES_MARGIN = 15;

    public static boolean isObstacleExist;
    private boolean playWithMovableObstacle;
    private MovableObstacle moveObstacle = new MovableObstacle();
    private FixedObstacle fixedObstacle = new FixedObstacle();
    public static int BOSSHEALTH = -5;

    private Stack<Tile[]> boardStack = new Stack<Tile[]>();
    private Stack<Integer> scoreStack = new Stack<Integer>();

    public static Tile[] GameTiles;
    public static boolean isWon = false;
    private boolean isLost = false;
    public static int myScore = 0;
    private Image image = ImageIO.read(ResourceLoader.load("Cone.png"));
    private Image obs5 = ImageIO.read(ResourceLoader.load("Obstacle5.png"));
    private Image obs4 = ImageIO.read(ResourceLoader.load("Obstacle4.png"));
    private Image obs3 = ImageIO.read(ResourceLoader.load("Obstacle3.png"));
    private Image obs2 = ImageIO.read(ResourceLoader.load("Obstacle2.png"));
    private Image obs1 = ImageIO.read(ResourceLoader.load("Obstacle1.png"));
    private Image Jamie = ImageIO.read(ResourceLoader.load("Jamie.jpg"));
    private Image Hieu = ImageIO.read(ResourceLoader.load("James.jpg"));
    private Image Khanh = ImageIO.read(ResourceLoader.load("Ray.jpg"));
    private Image Mint = ImageIO.read(ResourceLoader.load("Mint.jpg"));


    public Game2048(boolean playWithMovableObstacle, boolean playWithFixedObstacle) throws IOException {
        this.playWithMovableObstacle = playWithMovableObstacle;

        setFocusable(true);                 // To ensure that keyboard focus is available, so keyboard events are fired
        addKeyListener(new KeyAdapter() {   // KeyAdapter is an object that registered to receive events by using addKeyListener method
            @Override
            public void keyPressed(KeyEvent keyPressed) {   // An object of class KeyEvent is passed to the KeyAdapter so KeyAdapter can handle with keyboard events
                if(keyPressed.getKeyCode() == KeyEvent.VK_A ||
                        keyPressed.getKeyCode() == KeyEvent.VK_B ||
                        keyPressed.getKeyCode() == KeyEvent.VK_C ) {
                    scoreStack.clear();
                    boardStack.clear();
                    startGame(keyPressed.getKeyCode());
                }
                if(keyPressed.getKeyCode() == KeyEvent.VK_Z) {        // Press Z to Undo
                    for(int i = 0; i <16; i++){ //This condition fixes a bug in mode B
                        if(GameTiles[i].getValue() == -5)
                            isObstacleExist = false;
                        if(GameTiles[i].getValue() == -4 || GameTiles[i].getValue() == -3 ||
                                GameTiles[i].getValue() == -2 || GameTiles[i].getValue() == -1)
                            isObstacleExist = true;
                    }
                    undo();
                }
                if (!canMove())
                    isLost = true;
                if (!isWon && !isLost) {
                    Tile[] temp = new Tile[16];
                    for(int i=0; i<16; i++)
                        temp[i] = new Tile(GameTiles[i].getValue());

                    switch (keyPressed.getKeyCode()) {
                        case KeyEvent.VK_LEFT:
                            boardStack.push(temp);
                            scoreStack.push(myScore);
                            left();
                            break;
                        case KeyEvent.VK_RIGHT:
                            boardStack.push(temp);
                            scoreStack.push(myScore);
                            right();
                            break;
                        case KeyEvent.VK_DOWN:
                            boardStack.push(temp);
                            scoreStack.push(myScore);
                            down();
                            break;
                        case KeyEvent.VK_UP:
                            boardStack.push(temp);
                            scoreStack.push(myScore);
                            up();
                            break;
                    }
                }
                repaint();      //To refresh the viewing area when wanting the game to be reset
            }   //End of overridden keyPress method
        });
        if(!playWithMovableObstacle & !playWithFixedObstacle)
            startGame(65);  //Start game in Normal Mode
        if(playWithMovableObstacle && !playWithFixedObstacle)
            startGame(KeyEvent.VK_B);
        if(playWithFixedObstacle && !playWithMovableObstacle)
            startGame(KeyEvent.VK_C);

    }

    public void undo(){
        if(!boardStack.isEmpty())
            GameTiles = boardStack.pop();
        if(!scoreStack.isEmpty())
            myScore = scoreStack.pop();
    }

    public void startGame(int keyEventCode) {
        myScore = 0;
        isWon = false;
        isLost = false;
        GameTiles = new Tile[4 * 4];
        for (int i = 0; i < GameTiles.length; i++) {
            GameTiles[i] = new Tile();
        }
        addTile();  //spawn 2 random tiles
        addTile();

        if (keyEventCode == KeyEvent.VK_B) { //if user press B, play with movable Obstacle
            playWithMovableObstacle = true;
            isObstacleExist = false;
        }
        if (keyEventCode == KeyEvent.VK_C)  // if user press C, play with fixed Obstacle
        {
            playWithMovableObstacle = false;
            fixedObstacle.add();
        }
        if(keyEventCode == KeyEvent.VK_A )   // if user press A, play with Normal mode, no obstacle is added but setting playWithMovableObstacle to false
            playWithMovableObstacle = false;
    }


    private void left() {
        boolean needAddTile = false;
        boolean needUpdate = false;

        if(!playWithMovableObstacle)            // playWithMovableObstacle is false means user is play in with mode A or C, then tiles are moved and merged in the same way for Normal mode and Fixed Obstacle mode
        {
            for (int i = 0; i < 4; i++) {       //move all 4 lines
                Tile[] line = getLine(i);
                Tile[] merged = fixedObstacle.mergeLineFixedObstacle(fixedObstacle.moveLineFixedObstacle(line));      //merged line or moved if not merge-able
                setLine(i, merged);
                if (!needAddTile && !compare(line, merged)) {
                    needAddTile = true;
                }
                if(!needUpdate && !compare(line, merged))
                    needUpdate = true;
            }
            if(needAddTile)
                addTile();
            if(!needUpdate){
                scoreStack.pop();
                boardStack.pop();
            }
        }
        else {                               // if player use to play with mode B, Movable Obstacle
            for (int i = 0; i < 4; i++) {       //move all 4 lines
                Tile[] line = getLine(i);
                Tile[] merged = moveObstacle.mergeLineMovableObstacle(moveObstacle.moveLineMovableObstacle(line));      //merged line or moved if not merge-able
                setLine(i, merged);
                if (!needAddTile && !compare(line, merged)) {
                    needAddTile = true;
                }
                if(!needUpdate && !compare(line, merged))
                    needUpdate = true;
            }
            if(needAddTile) {
                addTile();
                moveObstacle.killObstacle();
            }
            if(!needUpdate){
                scoreStack.pop();
                boardStack.pop();
            }
            moveObstacle.add();
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


    public static void ensureSize(List<Tile> l, int s) {
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

    private void right() {
        GameTiles = rotate(180);
        left();
        GameTiles = rotate(180);
    }

    private void up() {
        GameTiles = rotate(270);
        left();
        GameTiles = rotate(90);
    }

    private void down() {
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


    public static List<Tile> availableSpace() {
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

    private boolean canMove() {
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

        ImageObserver ob = new ImageObserver() {
            @Override
            public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
                return false;
            }
        };

        g.setColor(tile.getBackground());
        g.fillRoundRect(xOffset, yOffset, TILE_SIZE, TILE_SIZE, 10, 10);

        if(value <0) { //switch case for both kinds of obstacle
            switch (value) {
                case -6:
                    g.drawImage(image, xOffset, yOffset, TILE_SIZE, TILE_SIZE, ob);     //draw obstacle
                    break;
                case -5:
                    g.drawImage(obs5, xOffset, yOffset, TILE_SIZE, TILE_SIZE, ob);
                    break;
                case -4:
                    g.drawImage(obs4, xOffset, yOffset, TILE_SIZE, TILE_SIZE, ob);
                    break;
                case -3:
                    g.drawImage(obs3, xOffset, yOffset, TILE_SIZE, TILE_SIZE, ob);
                    break;
                case -2:
                    g.drawImage(obs2, xOffset, yOffset, TILE_SIZE, TILE_SIZE, ob);
                    break;
                case -1:
                    g.drawImage(obs1, xOffset, yOffset, TILE_SIZE, TILE_SIZE, ob);
                    break;
            }
        }
        else {
            // draw 2048 box
            g.setColor(new Color(0x1D1D1D));                        //2048 box to anti-alias
            g.fillRect(20, 3, 150, 70);

            // draw 2048
            g.setColor(new Color(0x79c4b4));
            g.setFont(new Font(FONT_NAME, Font.BOLD, 60));
            g.drawString("2048", 30, 65);

            // draw text in tiles
            g.setColor(new Color(0xeaeaea));

            //Put Signatures in Game board
            g.drawImage(Hieu, 0, 590, 120, 55, ob);
            g.drawImage(Mint, 120, 590, 120, 55, ob);
            g.drawImage(Khanh,240, 590, 120, 55, ob);
            g.drawImage(Jamie,350, 590, 120, 55, ob);

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
            g.drawString("Click another mode to play again!", 50, getHeight() - 40);

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
}
