# Dice Task

Sample app reading from the official [MusicBrainz API](https://musicbrainz.org/doc/MusicBrainz_API#Search), 
containing the following features:
- Home screen, allowing to search for artists
- See the detailed page of an artist, including their respective albums and EPs.

This app uses a package structure following clean architecture concepts:
- data: Include Repositories, local and remote data sources
- domain: Include the model objects and (if needed) use case classes
- ui: Activities/Fragments/Views and ViewModels

The app uses [Ktor](https://ktor.io/) for accessing the APIs and 
[Room](https://developer.android.com/training/data-storage/room) for local storage. 
Communication between layers is done
using [Coroutines and Kotlin Flow](https://kotlinlang.org/docs/coroutines-guide.html).
Dependency injection is handled with [Koin](https://insert-koin.io/).

The project includes a few unit and Espresso tests (`test/` and `androidTest/` using
[Mockk](https://mockk.io/) and [Kotest](https://kotest.io/docs/assertions/assertions.html) 
as helper libraries. The dependencies on the Espresso tests are replaced using Koin.

## Demo

...