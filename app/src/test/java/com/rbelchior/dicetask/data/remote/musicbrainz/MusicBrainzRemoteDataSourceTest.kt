package com.rbelchior.dicetask.data.remote.musicbrainz

import com.rbelchior.dicetask.data.remote.musicbrainz.model.ArtistDto
import com.rbelchior.dicetask.data.remote.musicbrainz.model.SearchArtistsResultDto
import com.rbelchior.dicetask.data.remote.util.createHttpClient
import io.kotest.matchers.shouldBe
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import org.junit.Test

class MusicBrainzRemoteDataSourceTest {

    @Test
    fun sampleClientTest() = runBlocking {
        val remoteDataSource = createRemoteDataSource(MockEngine {
            respond(
                content = ByteReadChannel(SearchArtistsResponseFactory.sampleResponse),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        })

        remoteDataSource.searchArtists("coldplay") shouldBe Result.success(SearchArtistsResultDto(
            "2023-02-10T02:04:51.437Z",
            11, 0,
            listOf(
                ArtistDto(
                    "cc197bad-dc9c-440d-a5b5-d52ba2e14234",
                    100,
                    "Coldplay",
                    "Group",
                    ArtistDto.AreaDto(
                        "8a754a16-0027-3a29-b6d7-2b40ea0481ed",
                        "Country",
                        "United Kingdom"
                    ),
                    ArtistDto.AreaDto(
                        "f03d09b3-39dc-4083-afd6-159e3f0d462f",
                        "City",
                        "London"
                    ),
                    listOf("000000011551394X"),
                    ArtistDto.LifeSpanDto(
                        "1996-09", null
                    ),
                    listOf(
                        ArtistDto.TagDto(5, "rock"),
                        ArtistDto.TagDto(11, "pop"),
                    ),
                    null
                ),
                ArtistDto(
                    "62c54a75-265f-4e13-ad0a-0fb001559a2e",
                    62,
                    "Viva La Coldplay",
                    "Group",
                    ArtistDto.AreaDto(
                        "8a754a16-0027-3a29-b6d7-2b40ea0481ed",
                        "Country",
                        "United Kingdom"
                    ),
                    null,
                    null,
                    ArtistDto.LifeSpanDto(
                        "2006", null
                    ),
                    null,
                    null
                ),
                ArtistDto(
                    "a7331511-edcf-4e1f-b2b4-46f3b30f7f32",
                    62,
                    "冷玩妹",
                    "Person",
                    ArtistDto.AreaDto(
                        "41637cec-9a4f-389c-86d2-fc6abf3357b5",
                        "Country",
                        "Taiwan"
                    ),
                    null,
                    null,
                    ArtistDto.LifeSpanDto(
                        null, null
                    ),
                    null,
                    null
                ),
            )
        ))
    }

    private fun createRemoteDataSource(mockEngine: MockEngine): MusicBrainzRemoteDataSource {
        val httpClient = createHttpClient(mockEngine)
        return MusicBrainzRemoteDataSourceImpl(httpClient)
    }

}