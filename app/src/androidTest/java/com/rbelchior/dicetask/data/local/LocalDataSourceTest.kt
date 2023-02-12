package com.rbelchior.dicetask.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.rbelchior.dicetask.domain.ReleaseGroup
import com.rbelchior.dicetask.ui.artist.search.ArtistFactory
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LocalDataSourceTest {

    private lateinit var artistsDatabase: ArtistsDatabase
    private lateinit var localDataSource: LocalDataSource

    @Before
    fun setUp() {
        artistsDatabase = createDb()
        localDataSource = LocalDataSourceImpl(artistsDatabase)
    }

    @After
    fun tearDown() {
        artistsDatabase.close()
    }

    @Test
    fun insertArtistAndGetFromDb() = runTest {
        val artist = ArtistFactory.artistWithBasicData

        localDataSource.getArtist(artist.id) shouldBe null

        localDataSource.insertArtist(artist)

        localDataSource.getArtist(artist.id) shouldBe artist
    }

    @Test
    fun saveWikiDetailsAndGetFromDb() = runTest {
        val artist = ArtistFactory.artistWithBasicData

        localDataSource.insertArtist(artist)
        localDataSource.setWikiDetails(
            artist.id, "wiki description", "thumbnailUrl"
        )

        localDataSource.getArtist(artist.id)?.apply {
            wikiDescription shouldBe "wiki description"
            thumbnailImageUrl shouldBe "thumbnailUrl"
        }
    }

    @Test
    fun saveReleaseGroupsAndGetFromDb() = runTest {
        val artist = ArtistFactory.artistWithBasicData
        val expectedReleaseGroups = listOf(
            ReleaseGroup(
                "album1", "Parachutes", "album", null, "1995", "thumbnailUrl1"
            ), ReleaseGroup(
                "album2", "Another", "ep", null, "1996", "thumbnailUrl2"
            )
        )

        localDataSource.insertArtist(artist)
        localDataSource.setReleaseGroups(
            artist.id, expectedReleaseGroups
        )

        localDataSource.getArtist(artist.id)?.apply {
            releaseGroups shouldBe expectedReleaseGroups
        }
    }

    @Test
    fun saveArtistAndGetFromDb() = runTest {
        val artist = ArtistFactory.artistWithBasicData

        localDataSource.getSavedArtists().test {
            awaitItem() shouldBe emptyList()
        }

        localDataSource.insertArtist(artist)
        localDataSource.setArtistSaved(artist.id, true)

        localDataSource.getSavedArtists().test {
            awaitItem() shouldBe listOf(artist.copy(isSaved = true))
        }
    }

    @Test
    fun insertArtistShouldKeepExistingFields() = runTest {
        val artist = ArtistFactory.artistWithBasicData
        val expectedReleaseGroups = listOf(
            ReleaseGroup(
                "album1", "Parachutes", "album", null, "1995", "thumbnailUrl1"
            )
        )

        localDataSource.insertArtist(artist)
        localDataSource.setArtistSaved(artist.id, true)
        localDataSource.setReleaseGroups(artist.id, expectedReleaseGroups)
        localDataSource.setWikiDetails(
            artist.id, "wiki description", "thumbnailUrl"
        )

        // Insert artist again
        localDataSource.insertArtist(artist)

        localDataSource.getArtist(artist.id)?.apply {
            isSaved shouldBe true
            releaseGroups shouldBe expectedReleaseGroups
            wikiDescription shouldBe "wiki description"
            thumbnailImageUrl shouldBe "thumbnailUrl"
        }
    }

    private fun createDb() = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(), ArtistsDatabase::class.java
    ).build()
}
