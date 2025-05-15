package ru.gr362.painting;

import java.awt.*;

public interface Painter {
    void setWidth(int width);
    int  getWidth();
    void setHeight(int height);
    int  getHeight();

    void paint(Graphics g);
}