# Dice Task

Sample app reading from the official [MusicBrainz API](https://musicbrainz.org/doc/MusicBrainz_API#Search), 
containing the following features:
- Home screen, allowing to search for artists
- See the detailed page of an artist, including their respective albums and EPs.

Besides MusicBrainz, both [WikiData](https://www.wikidata.org/wiki/Wikidata:Data_access) 
and [Wikipedia](https://www.mediawiki.org/wiki/API:Main_page) APIs are used to get the description 
of an Artist.

This app uses a package structure following clean architecture concepts:
- data: Include Repositories, local and remote data sources
- domain: Include the model objects and (if needed) use case classes
- ui: Activities/Fragments/Views and ViewModels

The search list screen uses pagination (see DefaultPaginator) in order to display all items from
the API. The list of albums could also benefit from pagination, but that hasn't been implemented, yet.

The search list screen was implemented using an MVI approach for demonstrative purposes. 
The artist details screen has been implemented using a more simple approach. 

The app uses [Ktor](https://ktor.io/) for accessing the APIs and 
[Room](https://developer.android.com/training/data-storage/room) for local storage. 
Communication between layers is done
using [Coroutines and Kotlin Flow](https://kotlinlang.org/docs/coroutines-guide.html).
Dependency injection is handled with [Koin](https://insert-koin.io/).

The project includes a few unit and Espresso tests (`test/` and `androidTest/` using
[Mockk](https://mockk.io/), [Kotest](https://kotest.io/docs/assertions/assertions.html) and
[Turbine](https://github.com/cashapp/turbine) as helper libraries.

The project includes automated tests in the following layers:
- UI:
  - Compose: Instrumented tests using the jetpack compose testing libraries
  - ViewModels: Pure unit tests, ensuring the correct ui states and passed to the composables and the repository is called accordingly
  - Reducer: Unit testing the MVI reducer
- Data:
  - Mapper: Pure unit tests ensuring the correct mapping between local/remote/domain layers
  - Local: Instrumented unit tests using Room's in-memory DB
  - Remote: Pure unit tests using Ktor testing APIs to ensure the JSON coming from the API is properly returned
  - Repository: (tbd)

## Demo

...