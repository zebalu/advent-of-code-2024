/// Copyright 2024 Balázs Zaicsek
///
/// Licensed under the Apache License, Version 2.0 (the "License");
/// you may not use this file except in compliance with the License.
/// You may obtain a copy of the License at
///
///   [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)
///
/// Unless required by applicable law or agreed to in writing, software
/// distributed under the License is distributed on an "AS IS" BASIS,
/// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
/// See the License for the specific language governing permissions and
/// limitations under the License.

package io.github.zebalu.aoc2024;

import io.github.zebalu.aoc2024.utils.IOUtil;

abstract class AbstractDay implements Day {
    protected final String INPUT;
    protected final String TITLE;
    protected final int dayNumber;
    AbstractDay(String title, int dayNumber) {
        this(IOUtil.readInput(dayNumber), title, dayNumber);
    }
    AbstractDay(String input, String title, int dayNumber) {
        this.INPUT = input;
        this.TITLE = title;
        this.dayNumber = dayNumber;
    }

    public String getFormattedTitle(int size) {
        int sideLength = (size - TITLE.length() - 2) / 2;
        StringBuilder sb = new StringBuilder();
        boolean isOdd = sb.length() % 2 == 1;
        int dashLength = sideLength/2;
        int equalsLength = isOdd? dashLength+1:dashLength;
        sb.repeat('-', dashLength);
        sb.repeat('=', equalsLength);
        sb.append(' ');
        sb.append(TITLE);
        sb.append(' ');
        sb.repeat('=', equalsLength);
        sb.repeat('-', size-sb.length());
        return sb.toString();
    }
}
