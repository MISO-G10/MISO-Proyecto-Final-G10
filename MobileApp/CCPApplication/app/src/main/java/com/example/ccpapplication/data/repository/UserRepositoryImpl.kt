package com.example.ccpapplication.data.repository

import com.example.ccpapplication.data.model.User
import com.example.ccpapplication.data.model.UserLogin
import com.example.ccpapplication.services.CcpApiServiceAdapter

class UserRepositoryImpl (
    private val cppApiService: CcpApiServiceAdapter
):UserRepository{
    override suspend fun getUser(userId: String): User {
        TODO("Not yet implemented")
    }

    override suspend fun login(userLogin: UserLogin): User? {
        return User(id=12334,username="user1",password="1234",name="user1",rol="user")
    }


}