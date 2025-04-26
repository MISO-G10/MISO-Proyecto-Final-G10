package com.example.ccpapplication.data.repository

import android.util.Log
import com.example.ccpapplication.data.model.AddVisitResponse
import com.example.ccpapplication.data.model.VisitAdd
import com.example.ccpapplication.services.CcpApiServiceAdapter
import com.example.ccpapplication.services.interceptors.TokenManager

class VisitRepositoryImpl (
    private val cppApiService: CcpApiServiceAdapter,
    private val tokenManager: TokenManager
):VisitRepository{

    override suspend fun addVisit(visit: VisitAdd): Result<AddVisitResponse> {
        return try {
            Log.d("VisitRepository", "Trying to register user: ${visit.date}")

            val visitAdd = VisitAdd(
                date = visit.date,
                hourFrom = visit.hourFrom,
                hourTo = visit.hourTo,
                comments = visit.comments,
                idUser = visit.idUser
            )

            val response = cppApiService.addVisit(visitAdd)

            if (response.isSuccessful) {
                response.body()?.let { createVisitResponse ->
                    Log.d("VisitRepository", "Visit creation successful, visit ID: ${createVisitResponse.id}")
                    Result.success(createVisitResponse)
                } ?: run {
                    Log.e("VisitRepository", "Registration data not found in response body")
                    Result.failure(Exception("Registration data not found"))
                }
            } else {
                Log.e("VisitRepository", "Visit creation failed with HTTP ${response.code()} - ${response.message()}")
                Result.failure(Exception("HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("VisitRepository", "Exception creating visit: ${e.message}", e)
            Result.failure(Exception("Add visit failed: ${e.message}"))
        }
    }
}