package com.rbelchior.dicetask.data.repository

import com.rbelchior.dicetask.data.local.LocalDataSource
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
    private val localDataSource: LocalDataSource,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
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

            val artistFromDb = localDataSource.getArtist(artistId)
            if (artistFromDb != null) {
                emit(Result.success(artistFromDb))
            }
            val isSaved = artistFromDb != null

            // Emit value when result comes from the MusicBrainz API
            val artistDtoResult = musicBrainzRemoteDataSource
                .lookupArtist(artistId)
                .onSuccess { emit(Result.success(it.toDomain().copy(isSaved = isSaved))) }
                .onFailure { emit(Result.failure(it)) }

            val artistDto = artistDtoResult.getOrNull() ?: return@flow

            // In parallel, get the wikipedia description and list of albums
            coroutineScope {
                listOf(
                    async(defaultDispatcher) { artistDto.updateWithDescription() },
                    async(defaultDispatcher) { artistDto.updateWithReleaseGroups() }
                ).awaitAll().let {
                    mergeArtist(it[0], it[1], isSaved)
                }
            }
        }
    }

    fun getSavedArtists(): Flow<List<Artist>> {
        return localDataSource.getSavedArtists()
    }

    suspend fun saveArtist(artist: Artist) {
        localDataSource.saveArtist(artist)
    }

    suspend fun removeArtist(artist: Artist) {
        localDataSource.removeArtist(artist)
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
        artistWithDescriptionResult: Result<Artist>,
        artistWithReleaseGroupsResult: Result<Artist>,
        isSaved: Boolean
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
            artistWithDescription?.copy(
                releaseGroups = artistWithReleaseGroups?.releaseGroups,
                isSaved = isSaved
            ) ?: artistWithReleaseGroups?.copy(
                wikiDescription = artistWithDescription?.wikiDescription,
                thumbnailImageUrl = artistWithDescription?.thumbnailImageUrl,
                isSaved = isSaved
            )

        mergedArtist?.let { emit(Result.success(it)) }
    }
}