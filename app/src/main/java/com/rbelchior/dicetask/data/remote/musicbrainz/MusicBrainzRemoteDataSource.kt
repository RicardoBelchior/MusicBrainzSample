package com.rbelchior.dicetask.data.remote.musicbrainz

import com.rbelchior.dicetask.data.remote.musicbrainz.model.ArtistDto
import com.rbelchior.dicetask.data.remote.musicbrainz.model.LookupAlbumsResultDto
import com.rbelchior.dicetask.data.remote.musicbrainz.model.SearchArtistsResultDto

interface MusicBrainzRemoteDataSource {

    suspend fun searchArtists(
        query: String, offset: Int = 0, limit: Int = 25
    ): Result<SearchArtistsResultDto>

    suspend fun lookupArtist(artistId: String): Result<ArtistDto>

    suspend fun lookupAlbums(artistId: String): Result<LookupAlbumsResultDto>
}