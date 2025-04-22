package com.example.ccpapplication.data.repository

import android.util.Log
import com.example.ccpapplication.data.model.AuthResponse
import com.example.ccpapplication.data.model.User
import com.example.ccpapplication.data.model.UserLogin
import com.example.ccpapplication.services.CcpApiServiceAdapter
import com.example.ccpapplication.services.interceptors.TokenManager

class UserRepositoryImpl (
    private val cppApiService: CcpApiServiceAdapter,
    private val tokenManager: TokenManager
):UserRepository{
    override suspend fun getUser(): Result<User> {
        return try{

            val response= cppApiService.getUser()
            Log.d("UserRepository", "Response received: ${response.code()}")
            if (response.isSuccessful) {
                response.body()?.let { userResponse ->
                    tokenManager.saveUser(userResponse)
                    Result.success(userResponse)
                }?: run {
                    Log.e("UserRepository", "User data not found in response body")
                    Result.failure(Exception("User data not found"))
                }

            }
            else {
                Log.e("UserRepository", "me failed with HTTP ${response.code()} - ${response.message()}")
                Result.failure(Exception("HTTP ${response.code()}"))
            }
        }
        catch (e: Exception) {
            Log.e("UserRepository", "Exception during me: ${e.message}", e)
            Result.failure(Exception("Me failed: ${e.message}"))
        }

    }

    override suspend fun login(userLogin: UserLogin): Result<AuthResponse> {
        return try {
            Log.d("UserRepository", "Trying login with: ${userLogin.username}")

            val response = cppApiService.login(userLogin)

            Log.d("UserRepository", "Response received: ${response.code()}")

            if (response.isSuccessful) {
                response.body()?.let { authResponse ->
                    Log.d("UserRepository", "Login successful, token: ${authResponse.token}")
                    tokenManager.saveToken(authResponse.token)
                    val responseMe=cppApiService.getUser()
                    Log.d("UserRepository", "Response received: ${response.code()}")
                    if (responseMe.isSuccessful) {
                        responseMe.body()?.let { userResponse ->
                            tokenManager.saveUser(userResponse)
                            Result.success(userResponse)
                        }?: run {
                            Log.e("UserRepository", "User data not found in response body")
                            Result.failure(Exception("User data not found"))
                        }
                        Result.success(authResponse)
                    }
                    else {
                        Log.e("UserRepository", "me failed with HTTP ${response.code()} - ${response.message()}")
                        Result.failure(Exception("HTTP ${response.code()}"))
                    }

                } ?: run {
                    Log.e("UserRepository", "Auth data not found in response body")
                    Result.failure(Exception("Auth data not found"))
                }
            } else {
                Log.e("UserRepository", "Login failed with HTTP ${response.code()} - ${response.message()}")
                Result.failure(Exception("HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Exception during login: ${e.message}", e)
            Result.failure(Exception("Login failed: ${e.message}"))
        }
    }


}