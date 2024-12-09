package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

import java.util.HashSet;
import java.util.Set;

public class Day09 extends AbstractDay {
    private final int[] readed;
    public Day09() {
        this(IOUtil.readInput(9));
    }

    public Day09(String input) {
        super(input, "Disk Fragmenter", 9);
        int garbage = 0;
        for (int i = INPUT.length()-1; i>=0 && !Character.isDigit(INPUT.charAt(i)); --i) {
            ++garbage;
        }
        readed = new int[this.INPUT.length()-garbage];
        for (int i = 0; i < this.INPUT.length() && Character.isDigit(INPUT.charAt(i)); i++) {
            readed[i] = Integer.parseInt(this.INPUT.substring(i, i + 1));
        }
    }

    public String part1() {
        int[] fulll = extractHDD();
        int i = 0;
        int j = fulll.length-1;
        while (i<j) {
            if(fulll[i] == Integer.MIN_VALUE && fulll[j] != Integer.MIN_VALUE) {
                int temp = fulll[i];
                fulll[i] = fulll[j];
                fulll[j] = temp;
                ++i;
                --j;
            } else if(fulll[i] == Integer.MIN_VALUE && fulll[j] == Integer.MIN_VALUE) {
                --j;
            } else if(fulll[i] != Integer.MIN_VALUE && fulll[j] == Integer.MIN_VALUE) {
                ++i;
            } else if(fulll[i] != Integer.MIN_VALUE && fulll[j] != Integer.MIN_VALUE) {
                ++i;
            }
        }
        long sum = calculateChecksum(fulll);
        return Long.toString(sum);
    }

    private static long calculateChecksum(int[] fulll) {
        long sum =0L;
        for(int k = 0; k< fulll.length; k++) {
            long v = fulll[k];
            if(v!=Integer.MIN_VALUE) {
                sum += k*v;
            }
        }
        return sum;
    }

    public String part2() {
        int[] full = extractHDD();
        Set<Integer> moved = new HashSet<>();
        int check = full.length-1;
        while (check>0) {
            int fileStart = findFileStartBefore(check, full);
            if (fileStart>0 && moved.add(full[fileStart])) {
                int fileLength = findFileLengthFrom(fileStart, full);
                int spaceStart = findEnoughSpaceStartBefore(fileLength, fileStart, full);
                if(spaceStart>0) {
                    for (int k = 0; k < fileLength; k++) {
                        int tmp = full[spaceStart + k];
                        full[spaceStart + k] = full[fileStart + k];
                        full[fileStart + k] = tmp;
                    }
                }
                if(fileLength>1) {
                    check -= fileLength - 1;
                } else {
                    --check;
                }
            } else {
                if(fileStart>0) {
                    check = fileStart - 1;
                } else {
                    check = -1;
                }
            }
        }
        long sum = calculateChecksum(full);
        return Long.toString(sum);
    }

    private int[] extractHDD() {
        int sumSize = 0;
        for (int size : readed) {
            sumSize += size;
        }
        int[] full = new int[sumSize];
        boolean file = true;
        int fileId=0;
        int j = 0;
        for (int length : readed) {
            int value = file ? fileId : Integer.MIN_VALUE;
            int end = j + length;
            for (; j < end; ++j) {
                full[j] = value;
            }
            file = !file;
            if (file) {
                ++fileId;
            }
        }
        return full;
    }

    private int findFileStartBefore(int end, int[] full) {
        int j = end;
        if(j<0) {
            return Integer.MIN_VALUE;
        }
        int v = full[j];
        while(j>0 && v == Integer.MIN_VALUE) {
            --j;
            v = full[j];
        }
        while (v == full[j] && j>0 && full[j] != Integer.MIN_VALUE) {
            --j;
        }
        if(j==0) {
            return Integer.MIN_VALUE;
        }
        return j+1;
    }

    private int findFileLengthFrom(int fileStart, int[] full) {
        int v = full[fileStart];
        int length = 1;
        for(int i=fileStart+1; i<full.length && full[i] == v; i++) {
            ++length;
        }
        return length;
    }

    private int findEnoughSpaceStartBefore(int length, int end, int[] full) {
        int spaceStart = -1;
        int foundLength = 0;
        for(int i=0; i<end; i++) {
            if(full[i] == Integer.MIN_VALUE) {
                if(spaceStart == -1) {
                    spaceStart = i;
                    foundLength = 1;
                } else {
                    ++foundLength;
                }
                if(foundLength == length) {
                    return spaceStart;
                }
            } else {
                foundLength = 0;
                spaceStart = -1;
            }
        }
        return Integer.MIN_VALUE;
    }

    public static void main(String[] args) {
        String input = IOUtil.readInput(9);
        Day09 day09 = new Day09(input);
        System.out.println(day09.part1());
        System.out.println(day09.part2());
    }

}
