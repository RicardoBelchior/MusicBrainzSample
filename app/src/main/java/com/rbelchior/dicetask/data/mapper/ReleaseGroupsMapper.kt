package com.rbelchior.dicetask.data.mapper

import com.rbelchior.dicetask.data.remote.musicbrainz.model.LookupAlbumsResultDto
import com.rbelchior.dicetask.domain.ReleaseGroup
import com.rbelchior.dicetask.domain.ReleaseGroupsResult

fun LookupAlbumsResultDto.toDomain(): ReleaseGroupsResult {
    return ReleaseGroupsResult(
        releaseGroupCount, releaseGroupOffset,
        releaseGroups?.map { it.toDomain() } ?: emptyList()
    )
}

fun LookupAlbumsResultDto.ReleaseGroupDto.toDomain(): ReleaseGroup {
    return ReleaseGroup(
        id,
        title,
        primaryType,
        secondaryType,
        firstReleaseDate,
        "https://coverartarchive.org/release-group/${id}/front-250"
    )
}
