package com.rbelchior.dicetask.data.repository

import com.rbelchior.dicetask.data.local.LocalDataSource
import com.rbelchior.dicetask.data.remote.musicbrainz.MusicBrainzRemoteDataSource
import com.rbelchior.dicetask.data.remote.musicbrainz.model.SearchArtistsResultDto
import com.rbelchior.dicetask.data.remote.wiki.WikiRemoteDataSource
import com.rbelchior.dicetask.domain.SearchArtistsResult
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DiceRepositoryTest {

    private lateinit var musicBrainzRemoteDataSource: MusicBrainzRemoteDataSource
    private lateinit var wikiRemoteDataSource: WikiRemoteDataSource
    private lateinit var localDataSource: LocalDataSource
    private lateinit var diceRepository: DiceRepository

    private val searchArtistsResultDto = Result.success(
        SearchArtistsResultDto("", 0, 0, emptyList())
    )

    @Before
    fun setUp() {
        musicBrainzRemoteDataSource = mockk {
            coEvery { searchArtists(any(), any(), any()) } returns searchArtistsResultDto
        }
        wikiRemoteDataSource = mockk()
        localDataSource = mockk()

        diceRepository = DiceRepository(
            musicBrainzRemoteDataSource, wikiRemoteDataSource, localDataSource
        )
    }

    @Test
    fun search_artists() = runTest {
        diceRepository.searchArtist("query", 0) shouldBe Result.success(
            SearchArtistsResult(0, 0, emptyList())
        )

        coVerify {
            musicBrainzRemoteDataSource.searchArtists(
                "query",
                0,
                SearchArtistsResult.PAGE_SIZE
            )
        }
    }
}
