package com.soma.app.backendrepo.app_user.profile.repository

import com.soma.app.backendrepo.app_user.profile.model.Profile
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID


interface ProfileRepository : JpaRepository<Profile, UUID>
