Game Library 2D
===============

About
-----
Game Library 2D (name suggestions are welcome) is an extensible library used to develop high performance 2D games in Java.

The goal has been to make an easy-to-use library with plenty of features and few constraints. The API provides fully implemented features but depends primarily on interfaces. This makes it easy to extend and replace existing components. Abstract classes are available to support customization without having to reinvent the wheel. The underlying graphics and audio library, [LWJGL3](https://www.lwjgl.org/), is wrapped inside a "framework" module. This makes it possible to implement similar modules for other libraries, e.g. to support different platforms.

Game Library 2D includes support for particles, lightning, animations, collision detection, sounds, music and much more. A key focus has been support for multiplayer games. You can create split screen games with full joystick/controller support or create your own game server and network game. The easiest way to get started is to look at the demo applications. Each demo focuses on a key feature of Game Library 2D.

Requirements
------------
- Java JDK 13
- Apache Maven

Getting started
---------------
The API ships in several modules, each a separate maven dependency.

To start developing your game you need to add at least the following two dependencies:

```xml
<dependency>
  <groupId>com.gamelibrary2d</groupId>
  <artifactId>gamelibrary2d-core</artifactId>
  <version>0.1.0</version>
</dependency>
```

```xml
<dependency>
  <groupId>com.gamelibrary2d</groupId>
  <artifactId>gamelibrary2d-framework-lwjgl</artifactId>
  <version>0.1.0</version>
</dependency>
```

Other modules are available in order to provide sounds, collision detection, network support, etc.

You can see how these modules are used by looking at the source code of the demo applications.

Building
--------
The Game Library 2D jars are built by cloning the repository and running the following Maven command from the root folder:
```
mvn package
```
A jar-folder will be created with the following three sub folders:
- demos - The Demo applications.
- api - The Game Library 2D modules.
- tools - Tools and applications.

Versioning
----------
No major version has been released. Breaking changes might occur at any time.