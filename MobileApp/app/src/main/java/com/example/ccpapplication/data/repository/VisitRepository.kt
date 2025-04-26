package com.example.ccpapplication.data.repository

import com.example.ccpapplication.data.model.AddVisitResponse
import com.example.ccpapplication.data.model.VisitAdd

interface VisitRepository {
    suspend fun addVisit(visit: VisitAdd): Result<AddVisitResponse> //creación de visitas

}