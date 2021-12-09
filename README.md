[license]: https://github.com/CocoTheOwner/Strinput/tree/main/LICENSE.md
[license-shield]: https://img.shields.io/badge/license-GNU%20GPL%20v3-green
[docs]: https://cocotheowner.github.io/Strinput/index.html
[docs-shield]: https://img.shields.io/badge/Docs-Github%20Pages-green

[ ![license-shield]][license]
[![](https://jitpack.io/v/cocoTheOwner/strinput.svg)](https://jitpack.io/#cocoTheOwner/strinput)
[ ![docs-shield]][docs]


# Strinput

StrInput provides a feature-rich general-use command parsing system.
This library helps implement command systems through reducing type conversion complexity and command parsing improvements.

## Summary

1. [Installation](#installation)
2. [Setup (simple)](#setup-simple)
3. [Setup (advanced)](#setup-advanced)
4. [To-do](#to-do)

# Release with jitpack support
StrInput is now ready to be used. Though lacking some features, its main flow is working.
Examples for both [Discord](https://cocotheowner.github.io/Strinput/nl/codevs/strinput/examples/discord/package-summary.html) ([JDA](https://github.com/DV8FromTheWorld/JDA)) and [Spigot](https://cocotheowner.github.io/Strinput/nl/codevs/strinput/examples/spigotmc/package-summary.html) ([SpigotMC](https://github.com/SpigotMC)) are present in the `examples` module.

# Installation

Make sure to replace `VERSION` with the latest version:  
[![](https://jitpack.io/v/cocoTheOwner/strinput.svg)](https://jitpack.io/#cocoTheOwner/strinput)

For the latest (potentially unstable) available builds replace `VERSION` with `main-SNAPSHOT`.

**Gradle**
```gradle
repositories {
    maven { url "https://jitpack.io" }
    mavenCentral()
}

dependencies {
    implementation 'com.github.CocoTheOwner:StrInput:VERSION'
}
```
**Maven**
```xml
<dependency>
    <groupId>com.github.CocoTheOwner</groupId>
    <artifactId>StrInput</artifactId>
    <version>VERSION</version>
</dependency>
```

# Setup (simple)
Setting up the command system is quite simple and requires only three steps.
1. [User](#user)
2. [Command Center](#command-center)
3. [First Category](#first-category)

## User
To interact with the system you will need an implementation of the abstract class [StrUser](https://cocotheowner.github.io/Strinput/nl/codevs/strinput/system/StrUser.html). This implementation will handle messages from the system to the user.

```java
public class CustomUser implements StrUser {
    // To be defined
}
```

## Command Center
The command center is an instance of [StrCenter](https://cocotheowner.github.io/Strinput/nl/codevs/strinput/system/StrCenter.html).
You provide in the constructor at least a settings directory and one (or more) class instances (implementing [StrCategory](https://cocotheowner.github.io/Strinput/nl/codevs/strinput/system/StrCategory.html)), which are your root commands. Additionally, you can (as shown in [Setup (advanced)](#setup-(advanced))) optionally specify a console [StrUser](https://cocotheowner.github.io/Strinput/nl/codevs/strinput/system/StrUser.html) (which by default simply uses `System.out.println()`), extra [StrParameterHandlers](https://cocotheowner.github.io/Strinput/nl/codevs/strinput/system/parameter/StrParameterHandler.html) and [StrContextHandlers](https://cocotheowner.github.io/Strinput/nl/codevs/strinput/system/context/StrContextHandler.html). It is advisable to store the command system in a main class (such as in the Spigot example, [SpigotPlugin](https://cocotheowner.github.io/Strinput/nl/codevs/strinput/examples/spigotmc/SpigotPlugin.html)).

```java
public class MainClass {
    private static final StrCenter COMMAND_CENTER = new StrCenter(
            new File("settings"), // The file where settings are created, stored and can be edited
            new ExampleCategory() // The 'root' category (this is a vararg, so there can be multiple!)
    );
}
```
_Notice the user of `ExampleCategory`. This class will be the first category made (the main command category). See [First Category](#first-category)._

After creating the command center, whenever your system receives a user command, send it to the command center.
```java
public class MainClass {

    // Seen previously
    private static final StrCenter COMMAND_CENTER = new StrCenter(
            new File("settings"),
            new ExampleCategory()
    );
    
    public void onCommand(String command, SomeUser user) {
    
        // Handle prefixes
        if (!command.startsWith("!")) {
            return;
        }
        
        // Run other logic to prevent users from using commands
        if (!user.isCool()) {
            return;
        }
        
        // Pass the command to the command system
        COMMAND_CENTER.onCommand(command.split(" "), new CustomUser(user.getName()));
    }
}
```
Now all that is left is creating the first command category!

## First Category
Each command (sub)category gets its own class.
Command categories can extend [StrCategory](https://cocotheowner.github.io/Strinput/nl/codevs/strinput/system/StrCategory.html) (or a custom expansion of that, which you can create, see [StrCategory Extension](#strcategory-extension)) to gain access to the current [#user()](https://cocotheowner.github.io/Strinput/nl/codevs/strinput/system/StrCategory.html#user()) (the command sender) and the current [#center()](https://cocotheowner.github.io/Strinput/nl/codevs/strinput/system/StrCategory.html#center()) (the active command center).__
The category and commands therein must be annotated with [@StrInput](https://cocotheowner.github.io/Strinput/nl/codevs/strinput/system/StrInput.html) and parameters with [@Param](https://cocotheowner.github.io/Strinput/nl/codevs/strinput/system/Param.html). These annotation also contain settings such as the name, aliases and a description (and more, see the classes).

```java
public final class ExampleCategory implements SpigotCommandCategory {

    /**
     * Send a message to the user
     */
    @StrInput(description = "Send a message", aliases = "msg")
    public void message() {
        user().sendMessage(new Str("Hey!"));
    }

    /**
     * Send a message to debug
     *
     * @param message the message to send
     */
    @StrInput(description = "Send a message to debug")
    public void debug(
            @Param(
                    description = "The message to send to debug",
                    name = "message"
            )
            final String message
    ) {
        center().debug(new Str(message));
    }
}
```
_Note `new Str()`. [Str](https://cocotheowner.github.io/Strinput/nl/codevs/strinput/system/text/Str.html) is the custom text format which can contain colors, color gradients, clickable event and text-hovering._

# Setup (advanced)
A set of custom options are available to further simplify command creation.
1. [Advanced User](#advanced-user)
2. [Extra Parameter Handlers](#extra-parameter-handlers)
3. [Extra Context Handlers](#extra-context-handlers)
4. [StrCategory Extension](#strcategory-extension)

## Advanced User
TBD

## Extra Parameter Handlers
TBD

## Extra Context Handlers
TBD

## StrCategory Extension
TBD

# To-do
- Implement help messages (categories, commands & parameters)
- Add prepared Discord category, guild and permission handlers
- Add suggestions (auto-completions)
- Add Spigot colours + colour gradients
- Add Discord & Spigot on-click implementation
- Add Discord & Spigot on-hover implementation
- Expand range of testcases for:
  - Individual parameter & context handlers
  - Discord & Spigot user and Center implementations
- Style and documentation on Discord examples
- Parameter performance improvements (it's fast enough, but can be faster)
- Write advanced setup
- Add support for only-root commands (/kill, instead of /plugin kill)
- Test if multiple instances of the system running at once (meaning different implementations, such as on multiple Spigot plugins) will interfere with oneanother.
- Make sure parameters' names are saved
