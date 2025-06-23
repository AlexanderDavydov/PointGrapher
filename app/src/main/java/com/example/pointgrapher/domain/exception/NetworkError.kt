package com.example.pointgrapher.domain.exception

class NetworkError(e: Exception) : Exception("Error while requesting points", e)
