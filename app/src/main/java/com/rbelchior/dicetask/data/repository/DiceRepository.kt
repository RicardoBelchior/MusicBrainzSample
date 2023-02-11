package com.rbelchior.dicetask.data.repository

import com.rbelchior.dicetask.data.mapper.toDomain
import com.rbelchior.dicetask.data.remote.musicbrainz.MusicBrainzRemoteDataSource
import com.rbelchior.dicetask.data.remote.musicbrainz.model.ArtistDto
import com.rbelchior.dicetask.data.remote.wiki.WikiRemoteDataSource
import com.rbelchior.dicetask.data.remote.wiki.model.WikiSummaryDto
import com.rbelchior.dicetask.domain.Artist
import com.rbelchior.dicetask.domain.ReleaseGroupsResult
import com.rbelchior.dicetask.domain.SearchArtistsResult
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

class DiceRepository(
    private val musicBrainzRemoteDataSource: MusicBrainzRemoteDataSource,
    private val wikiRemoteDataSource: WikiRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun searchArtist(
        query: String, offset: Int = 0
    ): Result<SearchArtistsResult> {
        return musicBrainzRemoteDataSource
            .searchArtists(query, offset, SearchArtistsResult.PAGE_SIZE)
            .map { it.toDomain() }
    }

    fun getArtistDetails(artistId: String): Flow<Result<Artist>> {
        return flow {

            // Emit value when result comes from the MusicBrainz API
            val artistDtoResult = musicBrainzRemoteDataSource
                .lookupArtist(artistId)
                .onSuccess { emit(Result.success(it.toDomain())) }
                .onFailure { emit(Result.failure(it)) }

            val artistDto = artistDtoResult.getOrNull() ?: return@flow

            // In parallel, get the wikipedia description and list of albums
            coroutineScope {
                listOf(
                    async(ioDispatcher) { artistDto.updateWithDescription() },
                    async(ioDispatcher) { artistDto.updateWithReleaseGroups() }
                ).awaitAll().let {
                    mergeArtist(it[0], it[1])
                }
            }
        }
    }

    private suspend fun ArtistDto.updateWithDescription(): Result<Artist> {
        return this.getWikiSummary().map {
            this.toDomain().copy(
                wikiDescription = it.extract,
                thumbnailImageUrl = it.thumbnailImage?.source
            )
        }
    }

    private suspend fun ArtistDto.updateWithReleaseGroups(): Result<Artist> {
        return getReleaseGroups(id).map { this.toDomain().copy(releaseGroups = it.releaseGroups) }
    }

    private suspend fun getReleaseGroups(artistId: String): Result<ReleaseGroupsResult> {
        return musicBrainzRemoteDataSource.lookupAlbums(artistId).map { it.toDomain() }
    }

    private suspend fun ArtistDto.getWikiSummary(): Result<WikiSummaryDto> {
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

    private suspend fun getWikipediaSummary(pageTitle: String) =
        wikiRemoteDataSource.getWikipediaSummary(pageTitle)


    // Not very pretty but working code, merging description and albums into a single artist
    private suspend fun FlowCollector<Result<Artist>>.mergeArtist(
        artistWithDescriptionResult: Result<Artist>, artistWithReleaseGroupsResult: Result<Artist>
    ) {
        val artistWithDescription = artistWithDescriptionResult
            .onFailure { emit(Result.failure(it)) }
            .getOrNull()

        val artistWithReleaseGroups = artistWithReleaseGroupsResult
            .onFailure { emit(Result.failure(it)) }
            .getOrNull()

        if (artistWithDescription == null && artistWithReleaseGroups == null) {
            return
        }

        val mergedArtist =
            artistWithDescription?.copy(releaseGroups = artistWithReleaseGroups?.releaseGroups)
                ?: artistWithReleaseGroups?.copy(
                    wikiDescription = artistWithDescription?.wikiDescription,
                    thumbnailImageUrl = artistWithDescription?.thumbnailImageUrl
                )

        mergedArtist?.let { emit(Result.success(it)) }
    }
}