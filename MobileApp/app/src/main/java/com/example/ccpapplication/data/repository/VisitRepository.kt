package com.example.ccpapplication.data.repository

import com.example.ccpapplication.data.model.AddVisitResponse
import com.example.ccpapplication.data.model.UpdateVisitResponse
import com.example.ccpapplication.data.model.VisitAdd
import com.example.ccpapplication.data.model.Visit

interface VisitRepository {
    suspend fun addVisit(visit: VisitAdd): Result<AddVisitResponse> //creación de visitas
    suspend fun updateVisit(visit: Visit): Result<UpdateVisitResponse> //actualización de visitas
}