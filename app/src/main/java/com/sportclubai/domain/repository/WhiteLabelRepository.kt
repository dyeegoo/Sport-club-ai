package com.sportclubai.domain.repository

import com.sportclubai.domain.model.ClubBranding
import kotlinx.coroutines.flow.Flow

interface WhiteLabelRepository {
    fun getClubBranding(clubId: String): Flow<ClubBranding?>
    suspend fun updateClubBranding(branding: ClubBranding)
}
