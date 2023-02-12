package com.rbelchior.dicetask.domain

/**
 * Represent exceptions that are expected or not necessarily a blocker.
 * For ex. when we cannot grab wiki details.
 */
class FriendlyException(message: String): Throwable(message)