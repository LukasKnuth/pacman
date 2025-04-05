# Pacman

This is a pacman clone I built some years back when I was still learning to program.
The implementation is based heavily on the [Pacman Dossier](https://pacman.holenet.info/).

## 🕹️ Play

[Play in your Browser!](https://lukasknuth.github.io/pacman/) 

Read how I [ported my Java Game to work in the Browser](https://lknuth.dev/writings/java_in_browser/) using TeaVM.

## 📦 Build

- Run `./gradlew build` to build everything
- Run `./gradlew run` to run the desktop version as a Java Swing application
- Run `./gradlew jar` to build an executable JAR file under `desktop/build/libs/`
- Run `./gradlew war` to build the Web version under `web/build/libs/`

## 🗄️ Project Layout

- `game` - the Game logic and it's abstractions. Most of this is untouched from when I originally wrote this.
- Platform: `desktop` - the Java Swing implementation, extracted from the original game. Runs on your desktop.
- Platform: `web` - the Web version built using [TeaVM](https://teavm.org/). Runs in the browser.

## 💼 Legal

Pac-Man copyright to NAMCO Bandai, LTD and is their intellectual property.
