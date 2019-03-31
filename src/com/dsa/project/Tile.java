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
            case 2:    return new Color(0x69cec0);
            case 4:    return new Color(0x69c5ce);
            case 8:    return new Color(0x69acce);
            case 16:   return new Color(0x6994ce);
            case 32:   return new Color(0x6994ce);
            case 64:   return new Color(0x696cce);
            case 128:  return new Color(0x8369ce);
            case 256:  return new Color(0xa569ce);
            case 512:  return new Color(0xb669ce);
            case 1024: return new Color(0xce69ae);
            case 2048: return new Color(0xce6985);
        }
        return new Color(0x3a3a3a);
    }
}
