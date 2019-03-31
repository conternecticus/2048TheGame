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
        return (value == 0);
    }

    public Color getNumberColor() {
        if(value < 8) { // for 2, 4
            return new Color(0xf9f6f2);
        }
        else
            return new Color(0xf9f6f2);
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public Color getBackground() {
        switch (value) {
            case 2:    return new Color(0x79c4b4);
            case 4:    return new Color(0x79b5c4);
            case 8:    return new Color(0x79a7c4);
            case 16:   return new Color(0x798dc4);
            case 32:   return new Color(0x797fc4);
            case 64:   return new Color(0x8979c4);
            case 128:  return new Color(0x5addd2);
            case 256:  return new Color(0x5ed69e);
            case 512:  return new Color(0x5eacd6);
            case 1024: return new Color(0x5e8ad6);
            case 2048: return new Color(0x884bf2);
        }
        return new Color(0x3a3a3a);
    }
}
