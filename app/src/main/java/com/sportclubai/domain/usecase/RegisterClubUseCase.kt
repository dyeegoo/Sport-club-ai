package com.sportclubai.domain.usecase

import android.net.Uri
import com.sportclubai.domain.model.Club
import com.sportclubai.domain.model.User
import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.repository.ClubRepository
import javax.inject.Inject

class RegisterClubUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val clubRepository: ClubRepository
) {
    suspend operator fun invoke(
        firstName: String, lastName: String, email: String, password: String,
        mobileNumber: String, profilePhotoUri: Uri?,
        clubName: String, sportType: String, country: String, city: String,
        address: String, clubPhone: String, website: String, clubLogoUri: Uri?,
        language: String, currency: String, timeZone: String
    ) {
        // 1. Create User via AuthRepository
        val authResult = authRepository.registerOwner(email, password)
        val uid = authResult.uid
        
        // 2. Upload Profile Photo if present
        val photoUrl = profilePhotoUri?.let { uri ->
            authRepository.uploadProfilePhoto(uid, uri)
        } ?: ""

        // 3. Save User Data to Firestore
        val user = User(
            uid = uid,
            role = "owner",
            firstName = firstName,
            lastName = lastName,
            phone = mobileNumber,
            email = email,
            profilePhotoUrl = photoUrl
        )
        authRepository.saveUserToDatabase(user)

        // 4. Generate Club ID
        val clubId = clubRepository.generateClubId()

        // 5. Upload Club Logo if present
        val logoUrl = clubLogoUri?.let { uri ->
            clubRepository.uploadClubLogo(clubId, uri)
        } ?: ""

        // 6. Save Club Data to Firestore
        val club = Club(
            clubId = clubId,
            name = clubName,
            logoUrl = logoUrl,
            sportType = sportType,
            country = country,
            city = city,
            address = address,
            phone = clubPhone,
            website = website,
            language = language,
            currency = currency,
            timeZone = timeZone,
            ownerId = uid
        )
        clubRepository.saveClub(club)
    }
}
