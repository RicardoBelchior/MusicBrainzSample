package com.rbelchior.dicetask.data.repository

import com.rbelchior.dicetask.data.mapper.toDomain
import com.rbelchior.dicetask.data.remote.musicbrainz.MusicBrainzRemoteDataSource
import com.rbelchior.dicetask.data.remote.musicbrainz.model.ArtistDto
import com.rbelchior.dicetask.data.remote.wiki.WikiRemoteDataSource
import com.rbelchior.dicetask.domain.Artist
import com.rbelchior.dicetask.domain.SearchArtistsResult

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

    private suspend fun ArtistDto.getWikiDescription(): Result<String> {
        val firstWikiRelation = relations?.first { it.isTypeWikipedia || it.isTypeWikidata }
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