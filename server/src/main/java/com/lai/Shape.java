package com.lai;

import java.util.ArrayList;
import java.util.List;

public class Shape {
    private String type;
    private List<Double> paramList;
    private String color;

    public Shape() {
        paramList = new ArrayList<Double>();
    }

    public String getType() {
        return this.type;
    }

    public double getParam(int index) {
        return paramList.get(index);
    }

    public List<Double> getParamList() {
        return this.paramList;
    }

    public String getColor() {
        return this.color;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setParamList(ArrayList<Double> list) {
        this.paramList = list;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Shape)) {
            return false;
        }

        Shape s = (Shape) o;

        return (type.equals(s.getType()) && compareParams(s) && color.equals(s.getColor()));
    }

    private boolean compareParams(Shape s) {
        if (paramList.size() != s.getParamList().size()) {
            return false;
        }

        for (int i = 0; i < paramList.size(); i++) {
            if (s.getParam(i) != paramList.get(i)) {
                return false;
            }
        }

        return true;
    }
}
