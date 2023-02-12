package com.rbelchior.dicetask.ui.artist.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.rbelchior.dicetask.R
import com.rbelchior.dicetask.domain.Artist
import com.rbelchior.dicetask.domain.ReleaseGroup
import com.rbelchior.dicetask.ui.components.ArtistLabel

private val tagShape = CutCornerShape(10.dp)
private val albumImageShape = RoundedCornerShape(
    topStart = 8.dp, topEnd = 8.dp, bottomStart = 0.dp, bottomEnd = 0.dp
)

@Composable
fun ArtistDetailMainContent(artist: Artist) {
    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {

        ArtistImage(artist.thumbnailImageUrl)
        Spacer(modifier = Modifier.size(8.dp))

        ArtistDetailLabel(artist)

        ArtistTags(artist)
        Spacer(modifier = Modifier.size(8.dp))

        if (artist.releaseGroups?.isNotEmpty() == true) {
            ArtistAlbums(artist.releaseGroups)
        }

        AnimatedVisibility(visible = artist.wikiDescription != null) {
            ArtistDescription(artist.wikiDescription!!)
        }
        Spacer(modifier = Modifier.size(24.dp))
    }
}

@Composable
private fun ArtistDescription(wikiDescription: String) {
    Column {
        Text(
            text = stringResource(id = R.string.detail_screen_description),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 16.dp),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(text = wikiDescription)
    }
}

@Composable
private fun ArtistAlbums(releaseGroups: List<ReleaseGroup>) {
    Column {
        Text(
            text = stringResource(id = R.string.detail_screen_albums),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 16.dp),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.size(8.dp))

        LazyRow {
            itemsIndexed(releaseGroups) { i, releaseGroup ->
                ArtistAlbum(releaseGroup)

                // Display space between each item
                if (i < releaseGroups.lastIndex) {
                    Spacer(modifier = Modifier.padding(8.dp))
                }
            }
        }
        Spacer(modifier = Modifier.size(16.dp))
    }
}

@Composable
private fun ArtistAlbum(releaseGroup: ReleaseGroup) {
    ElevatedCard(modifier = Modifier.clickable {}) {

        Column(
            Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(releaseGroup.thumbnailImageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Album image",
                contentScale = ContentScale.FillHeight,
                alignment = Alignment.Center,
                placeholder = painterResource(id = R.drawable.ic_album),
                fallback = painterResource(id = R.drawable.ic_album),
                modifier = Modifier
                    .clip(albumImageShape)
                    .size(dimensionResource(id = R.dimen.album_detail_image_size))
            )

            Box(
                Modifier
                    .width(dimensionResource(id = R.dimen.album_detail_image_size))
                    .padding(8.dp)
            ) {
                Text(
                    text = releaseGroup.title,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
private fun ArtistDetailLabel(artist: Artist) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        ArtistLabel(artist = artist, textStyle = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ArtistTags(artist: Artist) {
    val tags = remember(artist.tags) { artist.tags.map { it.name } }

    LazyRow {
        itemsIndexed(tags) { i, tag ->
            AssistChip(
                onClick = {},
                shape = tagShape,
                label = {
                    Text(
                        text = "#$tag",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace
                    )
                }
            )

            // Display space between each item
            if (i < tags.lastIndex) {
                Spacer(modifier = Modifier.padding(4.dp))
            }
        }
    }
}

@Composable
private fun ArtistImage(imageUrl: String?) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Artist image",
            contentScale = ContentScale.FillHeight,
            alignment = Alignment.Center,
            placeholder = painterResource(id = R.drawable.ic_album),
            fallback = painterResource(id = R.drawable.ic_album),
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .size(dimensionResource(id = R.dimen.artist_detail_image_size))
        )
    }
}
