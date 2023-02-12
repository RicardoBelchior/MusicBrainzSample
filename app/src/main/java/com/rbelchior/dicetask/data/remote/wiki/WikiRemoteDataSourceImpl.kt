package com.rbelchior.dicetask.data.remote.wiki

import com.rbelchior.dicetask.data.remote.util.safeCall
import com.rbelchior.dicetask.data.remote.wiki.model.WikiSummaryDto
import com.rbelchior.dicetask.domain.FriendlyException
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class WikiRemoteDataSourceImpl(
    private val client: HttpClient
) : WikiRemoteDataSource {

    override suspend fun getWikipediaSummary(title: String): Result<WikiSummaryDto> {
        return safeCall {
            client.get("https://en.wikipedia.org/api/rest_v1/page/summary") {
                url {
                    appendPathSegments(title)
                }
            }
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
                ?.jsonObject?.get("enwiki") // TODO: Not all 'sitelinks' have enwiki. Some pages are only available in other languages.
                ?.jsonObject?.get("title")
                ?.jsonPrimitive
                ?.content!!

            return Result.success(title)

        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure(FriendlyException("Could not fetch wikipedia link"))
        }
    }
}
