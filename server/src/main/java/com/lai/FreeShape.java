package com.lai;

import java.util.ArrayList;
import java.util.List;

public class FreeShape {
    List<Shape> points;

    public FreeShape() {
        points = new ArrayList<Shape>();
    }

    public void addPoints(Shape s) {
        points.add(s);
    }

    public List<Shape> getAllPoints() {
        return points;
    }

    public void setNull() {
        points = new ArrayList<Shape>();
    }
}
