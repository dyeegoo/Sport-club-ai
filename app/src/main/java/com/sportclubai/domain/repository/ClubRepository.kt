package com.sportclubai.domain.repository

import android.net.Uri
import com.sportclubai.domain.model.Club

interface ClubRepository {
    suspend fun generateClubId(): String
    suspend fun uploadClubLogo(clubId: String, uri: Uri): String
    suspend fun saveClub(club: Club)
}
