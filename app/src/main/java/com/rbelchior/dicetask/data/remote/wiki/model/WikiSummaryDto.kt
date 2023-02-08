package com.rbelchior.dicetask.data.remote.wiki.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WikiSummaryDto(
    val title: String?,
    @SerialName("originalimage")
    val originalImage: OriginalImage?,
    val extract: String?,
    @SerialName("extract_html")
    val extractHtml: String?,
) {
    @Serializable
    data class OriginalImage(
        val source: String?,
        val width: Int?,
        val height: Int?
    )
}
