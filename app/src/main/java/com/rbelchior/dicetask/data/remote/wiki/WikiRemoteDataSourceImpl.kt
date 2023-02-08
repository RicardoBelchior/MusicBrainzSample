package com.rbelchior.dicetask.data.remote.wiki

import com.rbelchior.dicetask.data.remote.util.safeCall
import com.rbelchior.dicetask.data.remote.wiki.model.WikiSummaryDto
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

class WikiRemoteDataSourceImpl(
    private val client: HttpClient
) : WikiRemoteDataSource {

    override suspend fun getWikipediaSummary(title: String): Result<WikiSummaryDto> {
        return safeCall {
            client.get("https://en.wikipedia.org/api/rest_v1/page/summary/$title")
        }
    }

    override suspend fun getWikipediaLink(id: String): Result<String> {
        try {
            val response = client.get(
                "https://www.wikidata.org/w/api.php?action=wbgetentities&format=xml&props=sitelinks/urls&format=json"
            ) {
                parameter("ids", id)
            }

            val jsonElement = Json.parseToJsonElement(response.bodyAsText())
            val title = jsonElement
                .jsonObject["entities"]
                ?.jsonObject?.get(id)
                ?.jsonObject?.get("sitelinks")
                ?.jsonObject?.get("enwiki")
                ?.jsonObject?.get("title")
                ?.toString()!!

            return Result.success(title)

        } catch (e: Exception) {
            // TODO: Improve error handling
            e.printStackTrace()
            return Result.failure(e)
        }
    }
}
