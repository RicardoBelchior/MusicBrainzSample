package com.rbelchior.dicetask.ui.util.paginator

class DefaultPaginator<Key, Page>(
    private val initialKey: Key,
    private val onLoadUpdated: (Boolean) -> Unit,
    private val onRequest: suspend (nextKey: Key) -> Result<Page>,
    private val getNextKey: suspend (Page) -> Key,
    private val onError: suspend (Throwable) -> Unit,
    private val onSuccess: suspend (page: Page, newKey: Key) -> Unit
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
