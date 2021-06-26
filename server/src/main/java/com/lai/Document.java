package com.lai;

import java.util.ArrayList;
import java.util.List;

public class Document {
    private List<Shape> shapes;
    private List<FreeShape> freeShapes;
    private List<Text> texts;

    public Document() {
        shapes = new ArrayList<Shape>();
        freeShapes = new ArrayList<FreeShape>();
        texts = new ArrayList<Text>();
    }

    public void addShape(Shape s) {
        shapes.add(s);
    }

    public void addFreeShape(FreeShape s) {
        freeShapes.add(s);
    }

    public void addText(Text t) {
        texts.add(t);
    }

    public void removeShape(Shape s) {
        shapes.remove(s);
    }

    public List<Shape> getAllShapes() {
        return shapes;
    }

    public List<FreeShape> getAllFreeShapes() {
        return freeShapes;
    }

    public List<Text> getAllTexts() {
        return texts;
    }
}
