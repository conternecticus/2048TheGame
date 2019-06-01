package com.dsa.project;
import java.util.LinkedList;
import java.util.List;

public class MovableObstacle {

    public void add() {
        //if obstacle is already there
        if(Game2048.isObstacleExist)
            return;
//        for (int i = 0; i < 16; i++) {
//            if (GameTiles[i].getValue() < 0)
//                return;
//        }
        int random = (int) (Math.random() * 100);
        if (random < 30) {
            List<Tile> list = Game2048.availableSpace();
            if (!Game2048.availableSpace().isEmpty()) {
                double randy = Math.random(); //a random number from 0.0 to 1.0, for debug purpose
                int index = (int) (randy * list.size()) % list.size(); //create a random index to add a new tile
                Tile emptyTime = list.get(index);
                emptyTime.setValue(Game2048.BOSSHEALTH);
            }
            Game2048.isObstacleExist = true;
        }
    }

    public void killObstacle(){
        if(Game2048.isObstacleExist) {
            for (int i = 0; i < 16; i++) {
                if (Game2048.GameTiles[i].getValue() < 0) {
                    Game2048.GameTiles[i].setValue(Game2048.GameTiles[i].getValue() + 1);   // increment negative value of obstacle till 0 is attained
                    if(Game2048.GameTiles[i].getValue() == 0)
                        Game2048.isObstacleExist = false;
                }
            }
        }
    }
    public Tile[] moveLineMovableObstacle(Tile[] oldLine) { //This method makes all the tiles stack to one direction, not merge them.
        LinkedList<Tile> l = new LinkedList<Tile>();
        for (int i = 0; i < 4; i++) {
            if (!oldLine[i].isEmpty())
                l.addLast(oldLine[i]);
        }
        if (l.size() == 0) {
            return oldLine;
        } else {
            Tile[] newLine = new Tile[4];
            Game2048.ensureSize(l, 4); //Ensure that the new stacked line must have 4 elements
            for (int i = 0; i < 4; i++) {
                newLine[i] = l.removeFirst();
            }
            return newLine;
        }
    }

    public Tile[] mergeLineMovableObstacle(Tile[] oldTile) {
        LinkedList<Tile> list = new LinkedList<Tile>();
        final int maxValue = 2048;

        for (int i = 0; i < 4 && !(oldTile[i].isEmpty()); i++) {        // oldLine is NOT empty
            int num = oldTile[i].getValue();                            // current value of current Tile
            if ((i < 3) && (oldTile[i].getValue() == oldTile[i + 1].getValue())) {  //if current Tile and next Tile is equal
                num *= 2;                       // num is now doubled
                Game2048.myScore += num;        // update score

                if (num == maxValue) {
                    Game2048.isWon = true;
                }

                i++;
            }
            list.add(new Tile(num));
        }

        if (list.size() == 0) {
            return oldTile;
        } else {
            Game2048.ensureSize(list, 4);
            return list.toArray(new Tile[4]);
        }
    }
}
