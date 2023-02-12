package com.rbelchior.dicetask.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.rbelchior.dicetask.R
import com.rbelchior.dicetask.domain.Artist

@Composable
fun ArtistIcon(artistType: Artist.Type, modifier: Modifier) {
    when (artistType) {
        Artist.Type.PERSON -> Icon(
            Icons.Outlined.Person,
            stringResource(id = R.string.content_description_person),
            modifier,
        )
        Artist.Type.GROUP -> Icon(
            painterResource(id = R.drawable.ic_groups),
            stringResource(id = R.string.content_description_group),
            modifier,
        )
        Artist.Type.ORCHESTRA -> Icon(
            painterResource(id = R.drawable.ic_piano),
            stringResource(id = R.string.content_description_orchestra),
            modifier,
        )
        Artist.Type.CHOIR -> Icon(
            painterResource(id = R.drawable.ic_music_note),
            stringResource(id = R.string.content_description_choir),
            modifier,
        )
        Artist.Type.CHARACTER -> Icon(
            Icons.Outlined.Face,
            stringResource(id = R.string.content_description_character),
            modifier,
        )
        Artist.Type.OTHER -> Icon(
            painterResource(id = R.drawable.ic_library_music),
            stringResource(id = R.string.content_description_other),
            modifier,
        )
        Artist.Type.UNKNOWN -> {}
    }
}