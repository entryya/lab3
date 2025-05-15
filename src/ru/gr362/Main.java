package ru.gr362;

import ru.gr362.fractals.ui.MainWindow;
import ru.gr362.math.fractals.Mandelbrot;

public class Main {
    public static void main(String[] args) {
        var wnd = new MainWindow(new Mandelbrot());
        wnd.setVisible(true);
    }
}