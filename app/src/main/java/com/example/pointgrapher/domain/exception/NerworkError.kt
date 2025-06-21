package com.example.pointgrapher.domain.exception

class NerworkError(e: Exception) : Exception("Error while requesting points", e)
