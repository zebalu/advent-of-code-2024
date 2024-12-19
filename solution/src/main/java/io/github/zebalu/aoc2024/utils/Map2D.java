package io.github.zebalu.aoc2024.utils;

import java.util.Arrays;
import java.util.BitSet;

public class Map2D implements Cloneable {
    private BitSet[] map;

    public Map2D(int width, int height) {
        map = new BitSet[height];
        for (int i = 0; i < height; ++i) {
            map[i] = new BitSet(width);
        }
    }

    public boolean isMarked(int x, int y) {
        if (y < 0 || y >= map.length || x < 0 || x >= map[0].size()) {
            return false;
        }
        return map[y].get(x);
    }

    public boolean isValid(int x, int y) {
        return 0 <= y && y < map.length && 0 <= x && x < map[0].size();
    }

    public void mark(int x, int y) {
        map[y].set(x);
    }

    public void unMark(int x, int y) {
        map[y].set(x);
    }

    public void markAll(Map2D other) {
        if(map.length != other.map.length || map[0].size() != other.map[0].size()) {
            throw new IllegalArgumentException("Different map sizes can not be merged");
        }
        for (int i = 0; i < map.length; ++i) {
            map[i].or(other.map[i]);
        }
    }

    public int size() {
        return Arrays.stream(map).mapToInt(BitSet::cardinality).sum();
    }

    @Override
    public Map2D clone() {
        try {
            Map2D clone = (Map2D) super.clone();
            clone.map = new BitSet[map.length];
            for (int i = 0; i < map.length; ++i) {
                clone.map[i] = (BitSet) map[i].clone();
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            //Can not reach
            throw new RuntimeException(e);
        }
    }
}