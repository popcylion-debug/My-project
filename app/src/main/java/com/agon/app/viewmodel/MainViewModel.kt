package com.agon.app.viewmodel

import androidx.lifecycle.ViewModel
import com.agon.app.data.models.*
import com.agon.app.data.repository.AppRepository
import com.agon.app.ui.theme.SaloneThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel(
    val repository: AppRepository = AppRepository()
) : ViewModel() {

    // Auth & User
    val currentUser = repository.currentUser
    val isLoggedIn = repository.isLoggedIn
    val accountTier = repository.accountTier

    // Chats & Messages
    val chats = repository.chats
    val messages = repository.messages
    val stories = repository.stories
    val callHistory = repository.callHistory
    val contacts = repository.contacts
    val allUsers = repository.allUsers
    val blockedUsers = repository.blockedUsers

    // Preferences & Settings
    val privacySettings = repository.privacySettings
    val notificationPrefs = repository.notificationPrefs
    val currentTheme = repository.currentTheme
    val currentLanguage = repository.currentLanguage
    val fontScale = repository.fontScale
    val isDarkMode = repository.isDarkMode

    // Call state
    val activeCall = repository.activeCall

    // Notifications
    val inAppNotification = repository.inAppNotification

    // Admin & Business
    val adminAnalytics = repository.adminAnalytics
    val adminReports = repository.adminReports
    val systemAnnouncements = repository.systemAnnouncements
    val businessProfile = repository.businessProfile
    val chatBusinessMeta = repository.chatBusinessMeta

    // UI Navigation & Dialog States
    private val _currentActiveChatId = MutableStateFlow<String?>(null)
    val currentActiveChatId: StateFlow<String?> = _currentActiveChatId.asStateFlow()

    private val _activeViewingStory = MutableStateFlow<StoryItem?>(null)
    val activeViewingStory: StateFlow<StoryItem?> = _activeViewingStory.asStateFlow()

    private val _previewMedia = MutableStateFlow<Message?>(null)
    val previewMedia: StateFlow<Message?> = _previewMedia.asStateFlow()

    private val _forwardingMessage = MutableStateFlow<Pair<String, Message>?>(null) // (sourceChatId, message)
    val forwardingMessage: StateFlow<Pair<String, Message>?> = _forwardingMessage.asStateFlow()

    private val _selectedUserProfile = MutableStateFlow<User?>(null)
    val selectedUserProfile: StateFlow<User?> = _selectedUserProfile.asStateFlow()

    private val _showCreateGroupSheet = MutableStateFlow(false)
    val showCreateGroupSheet: StateFlow<Boolean> = _showCreateGroupSheet.asStateFlow()

    private val _showStoryCreator = MutableStateFlow(false)
    val showStoryCreator: StateFlow<Boolean> = _showStoryCreator.asStateFlow()

    private val _showQrShareModal = MutableStateFlow(false)
    val showQrShareModal: StateFlow<Boolean> = _showQrShareModal.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Navigation setters
    fun openChat(chatId: String) {
        _currentActiveChatId.value = chatId
    }

    fun closeChat() {
        _currentActiveChatId.value = null
    }

    fun openStory(story: StoryItem) {
        repository.recordStoryView(story.id)
        _activeViewingStory.value = story
    }

    fun closeStory() {
        _activeViewingStory.value = null
    }

    fun openMediaPreview(message: Message) {
        _previewMedia.value = message
    }

    fun closeMediaPreview() {
        _previewMedia.value = null
    }

    fun startForwardMessage(sourceChatId: String, message: Message) {
        _forwardingMessage.value = Pair(sourceChatId, message)
    }

    fun cancelForward() {
        _forwardingMessage.value = null
    }

    fun completeForwardToChat(targetChatId: String) {
        val forwardData = _forwardingMessage.value ?: return
        repository.forwardMessage(forwardData.first, targetChatId, forwardData.second.id)
        _forwardingMessage.value = null
    }

    fun openUserProfile(user: User) {
        _selectedUserProfile.value = user
    }

    fun closeUserProfile() {
        _selectedUserProfile.value = null
    }

    fun setCreateGroupSheetVisible(visible: Boolean) {
        _showCreateGroupSheet.value = visible
    }

    fun setStoryCreatorVisible(visible: Boolean) {
        _showStoryCreator.value = visible
    }

    fun setQrShareModalVisible(visible: Boolean) {
        _showQrShareModal.value = visible
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearNotificationToast() {
        repository.clearInAppNotification()
    }
}
