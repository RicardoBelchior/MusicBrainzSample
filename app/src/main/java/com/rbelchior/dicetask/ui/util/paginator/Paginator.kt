package com.rbelchior.dicetask.ui.util.paginator

interface Paginator<Key, Item> {
    suspend fun loadNextPage()
    fun reset()
}
