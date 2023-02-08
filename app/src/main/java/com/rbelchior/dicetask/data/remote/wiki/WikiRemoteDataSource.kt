package com.rbelchior.dicetask.data.remote.wiki

import com.rbelchior.dicetask.data.remote.wiki.model.WikiSummaryDto

interface WikiRemoteDataSource {

    suspend fun getWikipediaSummary(
        title: String
    ): Result<WikiSummaryDto>

    suspend fun getWikipediaLink(id: String): Result<String>

}