# Faaaa Sound Plugin

`Faaaa Sound` is an IntelliJ Platform plugin that plays a sound when code problems are detected.

It is currently configured for:
- `XML`
- `Dart` (Flutter/Dart workflows)

Vendor: **Appnixor**

## Features

- Plays `sound.mp3` when syntax issues are detected.
- Reacts to unresolved references in supported files.
- Adds a custom editor annotation message for syntax errors:
  - `Faaaa! An error!`
- Cooldown logic to reduce repeated playback noise.

## Compatibility

- IntelliJ Platform build range: `231` to `253.*`
- Works with Android Studio `AI-253` family.

## Installation (From ZIP)

1. Build plugin ZIP:

```powershell
.\gradlew buildPlugin

2. Find ZIP in:

build/distributions/
3. Install in Android Studio / IntelliJ IDEA:
• Settings -> Plugins -> gear icon -> Install Plugin from Disk...
• Select generated ZIP
• Restart IDE Development
Run tests: .\gradlew test
Run sandbox IDE (IntelliJ Community): .\gradlew runIde


Project Structure

• Plugin descriptor: src/main/resources/META-INF/plugin.xml
• Annotator: src/main/kotlin/org/faaaa/FaaaaAnnotator.kt
• Sound player: src/main/kotlin/org/faaaa/FaaaaSoundPlayer.kt
• Audio file: src/main/resources/sound.mp3
