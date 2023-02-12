package com.rbelchior.dicetask.ui.artist.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.rbelchior.dicetask.R
import com.rbelchior.dicetask.domain.ReleaseGroup

@Composable
fun AlbumDetailsDialog(releaseGroup: ReleaseGroup, onDismissDialog: () -> Unit) {

    Dialog(onDismissRequest = { onDismissDialog() }) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = Color.White
        ) {
            Column(modifier = Modifier) {

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(releaseGroup.thumbnailImageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Artist image",
                    contentScale = ContentScale.FillWidth,
                    alignment = Alignment.Center,
                    placeholder = painterResource(id = R.drawable.ic_album),
                    fallback = painterResource(id = R.drawable.ic_album),
                    modifier = Modifier
                        .clip(albumImageShape)
                        .fillMaxWidth()
                        .height(dimensionResource(id = R.dimen.artist_detail_image_size))
                )
                Spacer(modifier = Modifier.size(16.dp))

                Column(Modifier.padding(horizontal = 16.dp)) {

                    Text(
                        text = releaseGroup.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.size(8.dp))

                    releaseGroup.firstReleaseDate?.let {
                        Text(
                            text = "Released in: ${releaseGroup.firstReleaseDate}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Light
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                    }

                    releaseGroup.primaryType?.let {
                        Text(
                            text = "Type: ${releaseGroup.primaryType}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Light
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                    }

                    releaseGroup.secondaryTypesJoined?.let {
                        Text(
                            text = "Other details: $it",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Light
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                    }

                    Spacer(modifier = Modifier.size(8.dp))

                    Box(Modifier.fillMaxWidth()) {
                        ElevatedButton(
                            onClick = onDismissDialog,
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Text("Dismiss")
                        }
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                }
            }
        }

    }
}
