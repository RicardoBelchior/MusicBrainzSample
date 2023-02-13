# Music Brainz Sample App

Sample app reading from the official [MusicBrainz API](https://musicbrainz.org/doc/MusicBrainz_API#Search), 
containing the following features:
- Home screen (aka Artist Search screen)
  - There is an input field where users can search for artists 
  - When the search query is empty, users can view their saved artists (incl. offline support)
  - Each artist item has an icon corresponding to their type (person, group, orchestra, etc.)
- Details screen:
  - Detailed page of an artist, including their image, tags, albums and description
  - Once the user opens screen, the artist is saved in the db, so that users can see it offline
  - The artist's albums and description are requested from the network, in parallel
  - Users can "save" the artist using the top-right icon button
  - Clicking an Album will open a dialog with extra information

Besides MusicBrainz, both [WikiData](https://www.wikidata.org/wiki/Wikidata:Data_access) 
and [Wikipedia](https://www.mediawiki.org/wiki/API:Main_page) APIs are used to get the description 
of an Artist.

This app uses a package structure following clean architecture concepts:
- data: Includes Repositories, local and remote data sources
- domain: Includes the model objects and (if needed) use case classes
- ui: Activities/Fragments/Views and ViewModels

The search list screen uses pagination (see DefaultPaginator) in order to display all items from
the API. The list of albums could also benefit from pagination, but that hasn't been implemented, yet.

The search list screen was implemented using an MVI approach for demonstrative purposes. 
The artist details screen has been implemented using a more simple approach. 

The app uses [Ktor](https://ktor.io/) for accessing the APIs and 
[Room](https://developer.android.com/training/data-storage/room) for local storage. 
Communication between layers is done using [Coroutines and Kotlin Flow](https://kotlinlang.org/docs/coroutines-guide.html).
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

## Improvements

1) If the app would scale into a larger team, it could benefit from adding a layer for "Use Cases".
At this point the app is small but if multiple people were to work on it, or more "Repositories" 
would come into play, that would certainly help with maintainability. Right now, this would be a 
simple refactoring, in two steps. First, create the use cases, such as in the following pseudo-code:

```
// Package: com.rbelchior.dicetask.domain.usecase
class GetSavedArtistsUseCase {
  fun invoke() = repository.getSavedArtists()
}
...
class SearchArtistUseCase {
  fun invoke(query: String) = repository.searchArtist(query)
}
```

Then, replace all usages of the DiceRepository from the ViewModels and replace them with this 
use cases. The Android Developers website has good documentation on this subject
[https://developer.android.com/topic/architecture/domain-layer#use-cases-kotlin](https://developer.android.com/topic/architecture/domain-layer#use-cases-kotlin).

2) Unit tests on the `DiceRepository`. The test class has been setup but it would benefit from
more extensive unit tests.

3) Similarly to point 1), if the app would scale into a larger team, it could also benefit from
[modularisation](https://developer.android.com/topic/modularization). This app is currently a 
single-module app but could easily be re-organised in a different way. Here's one possibility:

- `domain`: Including the contents of the `com.rbelchior.dicetask.domain` package. That is, 
data models and use cases.
- `core`: Including the repositories and local/remote data sources. Optionally, local and remote 
sources could each have their own submodule
- `feature:search`: Including the contents of `com.rbelchior.dicetask.ui.search`
- `feature:detail`: Including the contents of `com.rbelchior.dicetask.ui.detail`
- `feature:common`: Including common utilities for feature modules, such as UI utilities or shared 
UI components.
- `app`: Including dependencies on the feature modules.

4) CI/CD pipeline. Run a CI build on every commit/pull-request, running Detekt and Lint for static
analysis and also running the unit and instrumented tests.

## Demo

- [Demo](https://www.loom.com/share/e190f4627feb4c2297865df77176eaea)
- [Image #1](demo/demo-1.png)
- [Image #2](demo/demo-2.png)
- [Image #3](demo/demo-3.png)
- [Image #4](demo/demo-4.png)
