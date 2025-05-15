package ru.gr362.math.fractals;

import ru.gr362.math.Complex;

public interface Fractal {
    float isInSet(Complex z);
    String getName();
}
