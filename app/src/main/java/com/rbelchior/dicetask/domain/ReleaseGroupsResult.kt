package com.rbelchior.dicetask.domain

data class ReleaseGroupsResult(
    val count: Int,
    val offset: Int,
    val releaseGroups: List<ReleaseGroup>
)
