import java.util.LinkedList;
import java.util.List;

public class FixedObstacle{

    public void add() {
        List<Tile> list = Game2048.availableSpace();
        if (!list.isEmpty()) {
            double randy = Math.random(); //a random number from 0.0 to 1.0, for debug purpose
            int index = (int) (randy * list.size()) % list.size(); //create a random index to add a new tile
            Tile emptyTime = list.get(index);
            emptyTime.setValue(-6);
        }
    }

    public Tile[] moveLineFixedObstacle(Tile[] oldLine) { //This method makes all the tiles stack to one direction, not merge them.
        LinkedList<Tile> l = new LinkedList<Tile>();
        Tile[] newLine = new Tile[4];
        if(oldLine[0].getValue()!=-6 && oldLine[1].getValue()!=-6 && oldLine[2].getValue()!=-6 && oldLine[3].getValue()!=-6) {  //When the line has no obstacle
            for (int i = 0; i < 4; i++) {
                if (!oldLine[i].isEmpty())
                    l.addLast(oldLine[i]);
            }
            if (l.size() == 0) {
                return oldLine;
            } else {
                Game2048.ensureSize(l, 4); //Ensure that the new stacked line must have 4 elements
                for (int i = 0; i < 4; i++) {
                    newLine[i] = l.removeFirst();
                }
            }
        }
        else{
            if(oldLine[1].getValue()==-6){          // If the fixed obstacle is at index 1 in a line, element at index 0 stays unchanged, but at indices 2, 3 is moved
                for(int i=2; i<4; i++){
                    if(!oldLine[i].isEmpty())
                        l.addLast(oldLine[i]);      // add into the temporary stacked list l values in indices 2, 3 of oldLine
                }
                if (l.size() == 0) {                // If there's no tiles nor obstacle, then just return the empty oldLine
                    return oldLine;
                } else {
                    Game2048.ensureSize(l, 2);           //Ensure that the new stacked line must have 2 elements
                    newLine[0] = oldLine[0];
                    newLine[1] = oldLine[1];
                    for (int i = 2; i < 4; i++) {
                        newLine[i] = l.removeFirst();   // newLine gets popped value from l
                    }
                }
            }
            if(oldLine[2].getValue()==-6){          // Same to olLine[1] being an obstacle
                for(int i=0; i<2; i++){
                    if(!oldLine[i].isEmpty())
                        l.addLast(oldLine[i]);
                }
                if (l.size() == 0) {
                    return oldLine;
                } else {
                    Game2048.ensureSize(l, 2); //Ensure that the new stacked line must have 2 elements
                    for (int i = 0; i < 2; i++) {
                        newLine[i] = l.removeFirst();
                        newLine[2] = oldLine[2];
                        newLine[3] = oldLine[3];
                    }
                }
            }
            if(oldLine[0].getValue()==-6){          // If obstacle is at index 0, move line from index 1 to index 3
                for (int i = 1; i < 4; i++) {
                    if (!oldLine[i].isEmpty())
                        l.addLast(oldLine[i]);
                }
                if (l.size() == 0) {
                    return oldLine;
                } else {
                    Game2048.ensureSize(l, 3);           //Ensure that the new stacked to the right of the obstacle line must have 3 elements
                    newLine[0] = oldLine[0];
                    for (int i = 1; i < 4; i++) {
                        newLine[i] = l.removeFirst();
                    }
                }
            }
            if(oldLine[3].getValue()==-6){
                for (int i = 0; i < 3; i++) {
                    if (!oldLine[i].isEmpty())
                        l.addLast(oldLine[i]);
                }
                if (l.size() == 0) {
                    return oldLine;
                } else {
                    Game2048.ensureSize(l, 3); //Ensure that the new stacked to the right of the obstacle line must have 3 elements
                    for (int i = 0; i < 3; i++) {
                        newLine[i] = l.removeFirst();
                    }
                    newLine[3] = oldLine[3];
                }
            }
        }
        return newLine;
    }

    public Tile[] mergeLineFixedObstacle(Tile[] oldTile) {
        LinkedList<Tile> list = new LinkedList<Tile>();
        Tile[] newLine = new Tile[4];
        if(oldTile[0].getValue()!=-6 && oldTile[1].getValue()!=-6 && oldTile[2].getValue()!=-6 && oldTile[3].getValue()!=-6) {  //When the line has no obstacle
            for (int i = 0; i < 4 && !(oldTile[i].isEmpty()); i++) {        // oldLine is NOT empty
                int num = oldTile[i].getValue();                            // current value of current Tile
                if ((i < 3) && (oldTile[i].getValue() == oldTile[i + 1].getValue())) {  //if current Tile and next Tile is equal
                    num *= 2;                       // num is now doubled
                    Game2048.myScore += num;                 // update score

                    if (num == 2048) {
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
                for(int i=0;i<4;i++)
                    newLine[i]=list.remove();
            }
        }
        else{                                               // Obstacle
            if(oldTile[1].getValue()==-6){                  // if the obstacle is in index 1, then we just need to merge the two Tiles to the right of the obstacle
                if(oldTile[2].getValue()==oldTile[3].getValue()){   // do the merging for the two last tiles to the right of the obstacle
                    int num = oldTile[2].getValue();
                    num *=2;
                    Game2048.myScore += num;
                    if (num == 2048) {
                        Game2048.isWon = true;
                    }
                    list.add(new Tile(num));
                }
                if (list.size() == 0) {
                    return oldTile;
                } else {
                    Game2048.ensureSize(list, 2);                // ensure list is having 2 Tiles, which
                    newLine[0] = oldTile[0];
                    newLine[1] = oldTile[1];
                    newLine[2] = list.remove();
                    newLine[3] = list.remove();
                }
            }
            if(oldTile[2].getValue()==-6){    // obstacle is in index 2
                if(oldTile[0].getValue()==oldTile[1].getValue()){
                    int num = oldTile[1].getValue();
                    num *=2;
                    Game2048.myScore += num;
                    if (num == 2048) {
                        Game2048.isWon = true;
                    }
                    list.add(new Tile(num));
                }
                if (list.size() == 0) {
                    return oldTile;
                } else {
                    Game2048.ensureSize(list, 2);
                    newLine[0] = list.remove();
                    newLine[1] = list.remove();
                    newLine[2] = oldTile[2];
                    newLine[3] = oldTile[3];
                }
            }
            if(oldTile[0].getValue()==-6){    //obstacle in index 0
                for(int i=1; i<4 && !oldTile[i].isEmpty();i++){
                    int num = oldTile[i].getValue();                            // current value of current Tile
                    if ((i < 3) && (oldTile[i].getValue() == oldTile[i + 1].getValue())) {  //if current Tile and next Tile is equal
                        num *= 2;                       // num is now doubled
                        Game2048.myScore += num;                 // update score

                        if (num == 2048) {
                            Game2048.isWon = true;
                        }

                        i++;
                    }
                    list.add(new Tile(num));
                }
                if (list.size() == 0) {
                    return oldTile;
                } else {
                    Game2048.ensureSize(list, 3);
                    newLine[0] = oldTile[0];
                    for(int i=1;i<4;i++)
                        newLine[i]=list.remove();
                }
            }
            if(oldTile[3].getValue()==-6){      // obstacle in index 3
                for(int i=0; i<3 && !oldTile[i].isEmpty();i++){
                    int num = oldTile[i].getValue();                            // current value of current Tile
                    if ((i < 2) && (oldTile[i].getValue() == oldTile[i + 1].getValue())) {  //if current Tile and next Tile is equal
                        num *= 2;                       // num is now doubled
                        Game2048.myScore += num;                 // update score

                        if (num == 2048) {
                            Game2048.isWon = true;
                        }

                        i++;
                    }
                    list.add(new Tile(num));
                }
                if (list.size() == 0) {
                    return oldTile;
                } else {
                    Game2048.ensureSize(list, 3);
                    for(int i=0;i<3;i++)
                        newLine[i]=list.remove();
                    newLine[3] = oldTile[3];
                }
            }
        }
        return newLine;
    }
}
