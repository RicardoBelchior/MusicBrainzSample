@file:OptIn(FlowPreview::class)

package com.rbelchior.dicetask.data.repository

import com.rbelchior.dicetask.data.local.LocalDataSource
import com.rbelchior.dicetask.data.mapper.toDomain
import com.rbelchior.dicetask.data.remote.musicbrainz.MusicBrainzRemoteDataSource
import com.rbelchior.dicetask.data.remote.wiki.WikiRemoteDataSource
import com.rbelchior.dicetask.data.remote.wiki.model.WikiSummaryDto
import com.rbelchior.dicetask.domain.Artist
import com.rbelchior.dicetask.domain.ReleaseGroupsResult
import com.rbelchior.dicetask.domain.SearchArtistsResult
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class DiceRepository(
    private val musicBrainzRemoteDataSource: MusicBrainzRemoteDataSource,
    private val wikiRemoteDataSource: WikiRemoteDataSource,
    private val localDataSource: LocalDataSource
) {

    suspend fun searchArtist(
        query: String, offset: Int = 0
    ): Result<SearchArtistsResult> {
        return musicBrainzRemoteDataSource
            .searchArtists(query, offset, SearchArtistsResult.PAGE_SIZE)
            .map { it.toDomain() }
    }

    fun getArtistDetails(artistId: String): Flow<Result<Artist>> {
        return warmDataWithCacheInitialAsFlow(
            getCacheData = { localDataSource.getArtist(artistId) },
            getRemoteData = { musicBrainzRemoteDataSource.lookupArtist(artistId) },
            setToLocalSource = { localDataSource.insertArtist(it.toDomain()) }
        ).flatMapMerge { fetchExtraDetails(it) }.map { it.data }
    }

    /**
     * If the last emission was coming from the network, then this method will also request,
     * in parallel, the artist's albums and wiki details.
     */
    private fun fetchExtraDetails(data: DataWrapper.DataState<Result<Artist>>) =
        if (data is DataWrapper.LiveData && data.data.isSuccess) {
            fetchExtraDetails(data.data.getOrThrow())
        } else {
            flowOf(data)
        }

    private fun fetchExtraDetails(artist: Artist): Flow<DataWrapper.DataState<Result<Artist>>> {
        return merge(
            flowOf(DataWrapper.LiveData(Result.success(artist))),
            saveWikiDetails(artist),
            saveReleaseGroups(artist)
        )
    }

    fun getSavedArtists(): Flow<List<Artist>> {
        return localDataSource.getSavedArtists()
    }

    suspend fun toggleArtistSaved(artist: Artist) {
        localDataSource.setArtistSaved(artist.id, !artist.isSaved)
    }

    private suspend fun getReleaseGroups(artist: Artist): Result<ReleaseGroupsResult> {
        return musicBrainzRemoteDataSource.lookupAlbums(artist.id).map { it.toDomain() }
    }

    private fun saveReleaseGroups(artist: Artist):
            Flow<DataWrapper.DataState<Result<Artist>>> {

        return fetchAndGetFromStorageFlow(
            { localDataSource.getArtist(artist.id) },
            { getReleaseGroups(artist) },
            { localDataSource.setReleaseGroups(artist.id, it.releaseGroups) }
        )
    }

    private fun saveWikiDetails(artist: Artist):
            Flow<DataWrapper.DataState<Result<Artist>>> {

        return fetchAndGetFromStorageFlow(
            { localDataSource.getArtist(artist.id) },
            { getWikiSummary(artist) },
            { localDataSource.setWikiDetails(artist.id, it.extract, it.thumbnailImage?.source) }
        )
    }

    private suspend fun getWikiSummary(artist: Artist): Result<WikiSummaryDto> {
        val firstWikiRelation = artist.relations
            ?.firstOrNull { it.isTypeWikipedia || it.isTypeWikidata }
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

}
