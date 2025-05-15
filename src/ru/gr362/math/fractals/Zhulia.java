package ru.gr362.math.fractals;

import ru.gr362.math.Complex;

public class Zhulia implements Fractal {

    private int maxIter = 2000;
    private final double R2 = 4.0;
    private Complex c;

    public Zhulia(Complex c) {
        this.c = c;
    }

    @Override
    public String getName() {
        return "Множество Жюлиа";
    }

    @Override
    public float isInSet(Complex z0){
        int i = 0;
        Complex z = z0;
        while (++i <= maxIter && z.abs2() < R2){
            z.timesAssign(z);
            z.plusAssign(c);
        }
        return (float) i / maxIter;
    }
}
