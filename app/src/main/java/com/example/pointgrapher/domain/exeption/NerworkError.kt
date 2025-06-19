package com.example.pointgrapher.domain.exeption

class NerworkError(e: Exception) : Exception("Error while requesting points", e)
