package com.example.pointgrapher.domain.exception

class BatchNotFoundException(batchId: String) : Exception("Batch with ID '$batchId' not found")