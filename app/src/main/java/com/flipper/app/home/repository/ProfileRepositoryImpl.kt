package com.flipper.app.home.repository

import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor() : ProfileRepository {
    override fun getUserName(): String {
        return "Flipper"
    }
}
