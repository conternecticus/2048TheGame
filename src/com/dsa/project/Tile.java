package com.dsa.project;

import java.awt.*;

public class Tile {
    private int value;

    public Tile(int num) {
        value = num;
    }

    public Tile() {
        this(0);    //default constructor
    }

    public boolean isEmpty() {
        if(value==0)
            return true;
        else
            return false;
    }

    public Color getNumberColor() {
            return new Color(0xf9f6f2);
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void deleteValue(){value = 0;}

    public int getValue() {
        return value;
    }

    public Color getBackground() {
        switch (value) {
            case -1:   return new Color(0x3a3a3a); //obstacle
            case 2:    return new Color(0x79c4b4);
            case 4:    return new Color(0x79b5c4);
            case 8:    return new Color(0x79a7c4);
            case 16:   return new Color(0x7984c4);
            case 32:   return new Color(0x9279c4);
            case 64:   return new Color(0xa879c4);
            case 128:  return new Color(0x5addd2);
            case 256:  return new Color(0x57c9db);
            case 512:  return new Color(0x5e96d6);
            case 1024: return new Color(0x6e5ed6);
            case 2048: return new Color(0xaf57db);
        }
        return new Color(0x3a3a3a);
    }
}
