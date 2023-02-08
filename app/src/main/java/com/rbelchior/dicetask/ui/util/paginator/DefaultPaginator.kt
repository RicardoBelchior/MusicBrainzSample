package com.rbelchior.dicetask.ui.util.paginator

class DefaultPaginator<Key, Page>(
    private val initialKey: Key,
    private inline val onLoadUpdated: (Boolean) -> Unit,
    private inline val onRequest: suspend (nextKey: Key) -> Result<Page>,
    private inline val getNextKey: suspend (Page) -> Key,
    private inline val onError: suspend (Throwable) -> Unit,
    private inline val onSuccess: suspend (page: Page, newKey: Key) -> Unit
) : Paginator<Key, Page> {

    private var currentKey = initialKey
    private var isMakingRequest = false

    override suspend fun loadNextPage() {
        if (isMakingRequest) {
            return
        }
        isMakingRequest = true
        onLoadUpdated(true)
        val result = onRequest(currentKey)
        isMakingRequest = false
        val page = result.getOrElse {
            onError(it)
            onLoadUpdated(false)
            return
        }
        currentKey = getNextKey(page)
        onSuccess(page, currentKey)
        onLoadUpdated(false)
    }

    override fun reset() {
        currentKey = initialKey
    }
}
