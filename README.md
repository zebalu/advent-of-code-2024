# Advent of Code 2024 -- Java 23

This repository contains my AoC24 solutions. Hopefully all of them by the end of the game. I use Java 23 this year,
which means I can meet previous years' goals with proper reuse of methods. Years ago I have decided to write solutions
that can be run without proper understanding of Java. Why do I do that? Sometimes I read and test other solutions in
languages I don't know, and in some cases this means I have to spend a lot of time with understanding compiler / 
runtime error messages. It's much easier to work with solutions where you can just run 1 file, and it does all.

This is easy to achieve with script languages such as Ruby, Python, etc. but much harder with compiled code like C,
C++, C# (or Java; well, not for me, but the pattern is the same), where you might even mess up the directory path,
where you execute the compile command. And no matter what: even if you ace compile and run commands missing 
dependencies can still make you miserable.

This is why I have decided earlier to write such solutions that do not depend on any dependency (however I include a
buildscript -- gradle -- in my solution), just pure Java. So anyone who wants to read and understand my solution should
only read the files in my repository. I have also decided to be compatible with the ["Single-File Source-Code Programs" 
JEP 330](https://openjdk.org/jeps/330), so anybody who only knows how to run a script file can stand next to my solution
of the Day, and say `java DayX.java` and magic happens, and he gets the results. This has also meant I can not write
my own reusable utility methods (not even in the same repository), as it would mean "complex" compile and execute 
commands should be understood by my guests.

This is not the case anymore, as Java 23 has introduced ["Launch Multi-File Source-Code Programs" JEP 458](https://openjdk.org/jeps/458)
which allows to use a Java "as a script language", and lets me collect utils as it should be.

I do not use preview features dough (again, `--enable-preview --source 23` _both_ at compile _and_ runtime) is a Java
speciality that I can not expect from every visitor. So Java 23 only means this feature above, and MarkDown comments 
for me... :(

## Goals

* solve all days
* pure java solutions (nothing else just plain old JDK 23; no other external dependencies)
* easy to run solutions (given by Java 23 as detailed in the preface)

### Stretch Goals

* solve __every *day*__ under 1 seconds (so parsing and part1+part2 is less than 1 second all together)
* solve __all day__ under 10 seconds (including parsing and JVM start)

## License

I provide you this repository under Apache License 2.0 so you can read, use, reuse, distribute, whatever with this code
for the prosperity of the human race. Have fun with it!

# Thanks

Many thanks to [Eric Wastl](https://github.com/topaz) for [Advent of Code](https://adventofcode.com)!
