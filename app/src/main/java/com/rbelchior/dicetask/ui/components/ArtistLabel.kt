package com.rbelchior.dicetask.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import com.rbelchior.dicetask.domain.Artist

@Composable
fun ArtistLabel(artist: Artist, textStyle: TextStyle) {
    ArtistLabel(artist.buildLabel(), textStyle = textStyle)
}

@Composable
fun ArtistLabel(label: String, textStyle: TextStyle) {
    Text(
        text = label,
        style = textStyle
    )
}


private fun Artist.buildLabel() = buildString {
    append(type.value)
    area?.name?.let {
        append(" from $it")
    }
    lifeSpan?.begin?.let {
        area?.name?.let {
            append(",")
        }
        if (type == Artist.Type.PERSON) {
            append(" born in: $it")
        } else {
            append(" founded in: $it")
        }
    }
}
