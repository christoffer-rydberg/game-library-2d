Game Library 2D
===============

About
-----
Game Library 2D (name suggestions are welcome) is an extensible library used to make high performance 2D games in Java.

The goal has been to make an easy-to-use library with plenty of features and few constraints. The API provides fully implemented features but dependends primarily on interfaces. This makes it easy to extend and replace existing behavior. Abstract classes are available to support customization without having to reinvent the wheel. The underlying graphics and audio library, [LWJGL3](https://https://www.lwjgl.org/), is wrapped inside a single module. This makes it possible to implement modules for other libraries as well, e.g. to support different platforms.

Game Library 2D includes support for particles, lightning, animations, collision detection, sounds, music and much more. A key focus has been support for multiplayer games. You can easily create split screen games with full joystick/controller support or create your own game server and network game. The easiest way to get started is to look at the demo applications. Each demo focuses on a key feature of Game Library 2D.

Requirements
------------
- Java JDK 13

Building
--------
Clone the repository and run the following Maven command from the root folder:
```
mvn package
```
You should now see a jar-folder in the root with the following three sub folders:
- demo - Demo applications.
- modules - The Game Library 2D modules.
- tools - Helpful tools and applications.

Versioning
---------------
No version has been set yet. Breaking changes might occur at any time.