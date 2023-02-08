package com.rbelchior.dicetask.data.remote.musicbrainz

import com.rbelchior.dicetask.data.remote.musicbrainz.model.ArtistDto
import com.rbelchior.dicetask.data.remote.musicbrainz.model.LookupAlbumsResultDto
import com.rbelchior.dicetask.data.remote.musicbrainz.model.SearchArtistsResultDto
import com.rbelchior.dicetask.data.remote.util.safeCall
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.delay

class MusicBrainzRemoteDataSourceImpl(
    private val client: HttpClient
) : MusicBrainzRemoteDataSource {

    private val baseUrl = "https://musicbrainz.org/ws/2"

    override suspend fun searchArtists(
        query: String, offset: Int, limit: Int
    ): Result<SearchArtistsResultDto> {
        return safeCall {
            client.get(urlString = "$baseUrl/artist") {
                parameter("query", query)
                parameter("offset", offset)
                parameter("limit", limit)
            }
        }
    }

    override suspend fun lookupArtist(artistId: String): Result<ArtistDto> {
        return safeCall {
            client.get(urlString = "$baseUrl/artist/$artistId") {
                parameter("inc", "url-rels")
            }
        }
    }

    override suspend fun lookupAlbums(artistId: String): Result<LookupAlbumsResultDto> {
        return safeCall {
            client.get(urlString = "$baseUrl/release-group") {
                parameter("artist", artistId)
                parameter("type", "album|ep")
            }
        }
    }
}
