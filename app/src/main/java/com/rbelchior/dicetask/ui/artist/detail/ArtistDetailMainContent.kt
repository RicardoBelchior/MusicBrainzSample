package com.rbelchior.dicetask.ui.artist.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.rbelchior.dicetask.R
import com.rbelchior.dicetask.domain.Artist

@Composable
fun ArtistDetailMainContent(artist: Artist) {
    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(artist.thumbnailImageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Album image",
                contentScale = ContentScale.FillHeight,
                alignment = Alignment.Center,
                placeholder = painterResource(id = R.drawable.ic_album),
                fallback = painterResource(id = R.drawable.ic_album),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .size(dimensionResource(id = R.dimen.artist_detail_image_size))
            )
        }

        Text("TYPE: ${artist.type}")
        Text("AREA: ${artist.area}")
        Text("TAGS: ${artist.tags.map { t -> t.name }}")
        Text("WIKI: ${artist.wikiDescription}")
        Text("ALBUMS: ${artist.releaseGroups?.map { releaseGroup -> releaseGroup.title }}")
    }
}
