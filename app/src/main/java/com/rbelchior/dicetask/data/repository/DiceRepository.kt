package com.rbelchior.dicetask.data.repository

import com.rbelchior.dicetask.data.mapper.toDomain
import com.rbelchior.dicetask.data.remote.musicbrainz.MusicBrainzRemoteDataSource
import com.rbelchior.dicetask.data.remote.musicbrainz.model.ArtistDto
import com.rbelchior.dicetask.data.remote.wiki.WikiRemoteDataSource
import com.rbelchior.dicetask.domain.Artist
import com.rbelchior.dicetask.domain.SearchArtistsResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DiceRepository(
    private val musicBrainzRemoteDataSource: MusicBrainzRemoteDataSource,
    private val wikiRemoteDataSource: WikiRemoteDataSource
) {

    suspend fun searchArtist(
        query: String, offset: Int = 0
    ): Result<SearchArtistsResult> {
        return musicBrainzRemoteDataSource
            .searchArtists(query, offset, SearchArtistsResult.PAGE_SIZE)
            .map { it.toDomain() }
    }

    suspend fun getArtistDetails(artistId: String): Result<Artist> {
        val artistDto = musicBrainzRemoteDataSource
            .lookupArtist(artistId)
            .getOrElse { return Result.failure(it) }

        val wikiDescription = artistDto.getWikiDescription().getOrNull()
        val artist = artistDto.toDomain().copy(wikiDescription = wikiDescription)

        return Result.success(artist)
    }

    fun getArtistDetailsFlow(artistId: String): Flow<Result<Artist>> {
        return flow {

            // Emit value when result comes from the MusicBrainz API
            val artistDtoResult: Result<ArtistDto> = musicBrainzRemoteDataSource
                .lookupArtist(artistId)
                .onSuccess { emit(Result.success(it.toDomain())) }
                .onFailure { emit(Result.failure(it)) }


            // Emit new value when result comes from the Wikipedia API
            artistDtoResult.getOrNull()?.let { artist ->
                artist.getWikiDescription()
                    .onSuccess {
                        emit(
                            Result.success(
                                artist.toDomain().copy(wikiDescription = it)
                            )
                        )
                    }
                    .onFailure { emit(Result.failure(it)) }
            }
        }
    }

    private suspend fun ArtistDto.getWikiDescription(): Result<String> {
        val firstWikiRelation = relations?.firstOrNull { it.isTypeWikipedia || it.isTypeWikidata }
            ?: return Result.failure(IllegalArgumentException("Could not find wiki relations: $this"))

        val pageTitle = firstWikiRelation.pageTitle!!

        if (firstWikiRelation.isTypeWikipedia) {
            return getWikipediaSummary(pageTitle)
        } else {
            val wikiTitle = wikiRemoteDataSource
                .getWikipediaLink(pageTitle)
                .getOrElse { return Result.failure(it) }

            return getWikipediaSummary(wikiTitle)
        }
    }

    private suspend fun getWikipediaSummary(pageTitle: String) = wikiRemoteDataSource
        .getWikipediaSummary(pageTitle)
        .mapCatching { wikiSummary -> wikiSummary.extract!! }

}