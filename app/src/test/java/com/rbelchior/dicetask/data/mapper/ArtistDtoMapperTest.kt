package com.rbelchior.dicetask.data.mapper

import com.rbelchior.dicetask.data.remote.musicbrainz.SearchArtistsResponseFactory
import com.rbelchior.dicetask.data.remote.musicbrainz.model.SearchArtistsResultDto
import com.rbelchior.dicetask.domain.Artist
import com.rbelchior.dicetask.domain.SearchArtistsResult
import com.rbelchior.dicetask.util.JsonStringConverter
import io.kotest.matchers.shouldBe
import logcat.LogcatLogger
import logcat.PrintLogger
import org.junit.Before
import org.junit.Test

class ArtistDtoMapperTest {

    private val searchArtistsResultDto = JsonStringConverter()
        .decodeFromString<SearchArtistsResultDto>(
            SearchArtistsResponseFactory.sampleResponse
        )!!

    @Before
    fun setUp() {
        LogcatLogger.install(PrintLogger)
    }

    @Test
    fun test_dto_to_domain_mapping() {
        searchArtistsResultDto.toDomain() shouldBe SearchArtistsResult(
            11, 0, listOf(
                Artist(
                    "cc197bad-dc9c-440d-a5b5-d52ba2e14234",
                    "Coldplay",
                    Artist.Type.GROUP,
                    Artist.Area(
                        "8a754a16-0027-3a29-b6d7-2b40ea0481ed",
                        "Country",
                        "United Kingdom"
                    ),
                    Artist.Area(
                        "f03d09b3-39dc-4083-afd6-159e3f0d462f",
                        "City",
                        "London"
                    ),
                    listOf("000000011551394X"),
                    Artist.LifeSpan(
                        "1996-09", null
                    ),
                    listOf(
                        Artist.Tag(11, "pop"),
                        Artist.Tag(5, "rock"),
                    ),
                    emptyList(),
                    null
                ),
                Artist(
                    "62c54a75-265f-4e13-ad0a-0fb001559a2e",
                    "Viva La Coldplay",
                    Artist.Type.GROUP,
                    Artist.Area(
                        "8a754a16-0027-3a29-b6d7-2b40ea0481ed",
                        "Country",
                        "United Kingdom"
                    ),
                    null,
                    emptyList(),
                    Artist.LifeSpan(
                        "2006", null
                    ),
                    emptyList(),
                    emptyList(),
                    null
                ),
                Artist(
                    "a7331511-edcf-4e1f-b2b4-46f3b30f7f32",
                    "冷玩妹",
                    Artist.Type.PERSON,
                    Artist.Area(
                        "41637cec-9a4f-389c-86d2-fc6abf3357b5",
                        "Country",
                        "Taiwan"
                    ),
                    null,
                    emptyList(),
                    Artist.LifeSpan(
                        null, null
                    ),
                    emptyList(),
                    emptyList(),
                    null
                )
            )
        )
    }
}
