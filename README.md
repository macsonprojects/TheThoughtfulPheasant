# Thoughtful Pheasant 🦚

A modern, interactive Android application that generates "thoughtful" (and sometimes spicy) phrases based on your mood. Built with Jetpack Compose, Material 3, and modern Android architecture.

## Features

- **Mood Selector**: Use the endless horizontal pager to choose your vibe:
  - *Inspire Me*: British politeness at its best.
  - *Thoughtful*: Observational and witty.
  - *Roast Me*: For those who can handle a bit of heat.
- **Voice Synthesis**: Uses the system Text-To-Speech engine to read phrases aloud with varying pitch and speed.
- **Mood Editor**: Fully customizable! Add your own categories and phrases, or edit/delete existing ones.
- **Persistent Storage**: All your custom moods and phrases are saved locally using Android DataStore.
- **Adaptive UI**: Clean Material 3 design with smooth animations.

## Tech Stack

- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (100% Declarative)
- **Navigation**: [Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
- **Persistence**: [DataStore Preferences](https://developer.android.com/topic/libraries/architecture/datastore)
- **Serialization**: [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization) (JSON)
- **Architecture**: MVVM with `Flow` and `ViewModel`
- **Voice**: Android TextToSpeech API

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/MacsonProjects/ThoughtfulPheasant.git
   ```
2. Open the project in **Android Studio Ladybug** or newer.
3. Build and run on an Android device (Min SDK 24).

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Author

Created by [MacsonProjects](https://github.com/MacsonProjects)
