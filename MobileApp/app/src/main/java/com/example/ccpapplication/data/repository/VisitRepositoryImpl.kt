package com.example.ccpapplication.data.repository

import android.util.Log
import com.example.ccpapplication.data.model.AddVisitResponse
import com.example.ccpapplication.data.model.UpdateVisitResponse
import com.example.ccpapplication.data.model.Visit
import com.example.ccpapplication.data.model.VisitAdd
import com.example.ccpapplication.data.model.VisitUpdate
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

    override suspend fun updateVisit(visit: Visit): Result<UpdateVisitResponse> {
        return try {
            Log.d("VisitRepository", "Trying to update visit: ${visit.id}")

            val visitUpdate = VisitUpdate(
                date = visit.date,
                hourFrom = visit.hourFrom,
                hourTo = visit.hourTo,
                comments = visit.comments,
                idUser = visit.idUser,
                canceled = visit.canceled
            )

            val response = cppApiService.updateVisit(visit.id, visitUpdate)

            if (response.isSuccessful) {
                response.body()?.let { updateVisitResponse ->
                    Log.d("VisitRepository", "Visit update successful, visit ID: ${updateVisitResponse.id}")
                    Result.success(updateVisitResponse)
                } ?: run {
                    Log.e("VisitRepository", "Update data not found in response body")
                    Result.failure(Exception("Update data not found"))
                }
            } else {
                Log.e("VisitRepository", "Visit update failed with HTTP ${response.code()} - ${response.message()}")
                Result.failure(Exception("HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("VisitRepository", "Exception updating visit: ${e.message}", e)
            Result.failure(Exception("Update visit failed: ${e.message}"))
        }
    }
}