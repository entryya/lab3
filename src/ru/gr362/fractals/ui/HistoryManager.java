package ru.gr362.fractals.ui;

import ru.gr362.converting.Converter;

import java.util.ArrayDeque;
import java.util.Deque;

public class HistoryManager {

    private static final int MAX_HISTORY = 100;
    private final Deque<Converter> history = new ArrayDeque<>();

    public void save(Converter conv) {
        if (history.size() == MAX_HISTORY) {
            history.removeFirst();
        }
        history.addLast(new Converter(conv.getxMin(), conv.getxMax(), conv.getyMin(), conv.getyMax()));
    }

    public Converter undo() {
        if (!history.isEmpty()) {
            return history.removeLast();
        }
        return null;
    }

    public boolean canUndo() {
        return !history.isEmpty();
    }
}