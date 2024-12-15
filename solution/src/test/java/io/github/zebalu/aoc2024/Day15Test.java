package io.github.zebalu.aoc2024;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class Day15Test {

    @ParameterizedTest
    @EnumSource(Example.class)
    void example(Example example) {
        Day15 day15 = new Day15(example.text);
        Assertions.assertAll(
                ()->Assertions.assertEquals(example.part1, Long.parseLong(day15.part1())),
                ()->Assertions.assertEquals(example.part2, Long.parseLong(day15.part2())));
    }

    static final String EXAMPLE_1 = """
            ##########
            #..O..O.O#
            #......O.#
            #.OO..O.O#
            #..O@..O.#
            #O#..O...#
            #O..O..O.#
            #.OO.O.OO#
            #....O...#
            ##########
            
            <vv>^<v^>v>^vv^v>v<>v^v<v<^vv<<<^><<><>>v<vvv<>^v^>^<<<><<v<<<v^vv^v>^
            vvv<<^>^v^^><<>>><>^<<><^vv^^<>vvv<>><^^v>^>vv<>v<<<<v<^v>^<^^>>>^<v<v
            ><>vv>v^v^<>><>>>><^^>vv>v<^^^>>v^v^<^^>v^^>v^<^v>v<>>v^v^<v>v^^<^^vv<
            <<v<^>>^^^^>>>v^<>vvv^><v<<<>^^^vv^<vvv>^>v<^^^^v<>^>vvvv><>>v^<<^^^^^
            ^><^><>>><>^^<<^^v>>><^<v>^<vv>>v>>>^v><>^v><<<<v>>v<v<v>vvv>^<><<>^><
            ^>><>^v<><^vvv<^^<><v<<<<<><^v<<<><<<^^<v<^^^><^>>^<v^><<<^>>^v<v^v<v^
            >^>>^v>vv>^<<^v<>><<><<v<<v><>v<^vv<<<>^^v^>^^>>><<^v>>v^v><^^>>^<>vv^
            <><^^>^^^<><vvvvv^v<v<<>^v<v>v<<^><<><<><<<^^<<<^<<>><<><^^^>^^<>^>v<>
            ^^>vv<^v^v<vv>^<><v<^v>^^^>>>^^vvv^>vvv<>>>^<^>>>>>^<<^v>^vvv<>^<><<v>
            v^^>>><<^^<>>^v^<v^vv<>v^<<>^<^v^v><^<<<><<^<v><v<>vv>>v><v^<vv<>v^<<^""";

    private static final String EXAMPLE_2 = """
            ########
            #..O.O.#
            ##@.O..#
            #...O..#
            #.#.O..#
            #...O..#
            #......#
            ########
            
            <^^>>>vv<v>>v<<""";

    private static final String EXAMPLE_3 = """
            #######
            #...#.#
            #.....#
            #..OO@#
            #..O..#
            #.....#
            #######
            
            <vv<<^^<<^""";

    private enum Example {
        E1(EXAMPLE_1, 10092, 9021), E2(EXAMPLE_2, 2028, 1751), E3(EXAMPLE_3, 908, 718);

        private final String text;
        private final long part1;
        private final long part2;

        Example(String text, long part1, long part2) {
            this.text = text;
            this.part1 = part1;
            this.part2 = part2;
        }

    }
}
