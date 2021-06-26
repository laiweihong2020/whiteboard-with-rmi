package com.lai;

public class Text {
    private String str;
    private double x;
    private double y;

    public Text(String s, double param1, double param2) {
        str = s;
        x = param1;
        y = param2;
    }

    public String getText() {
        return this.str;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }
}
