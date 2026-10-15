package com.example.ui.screens.discussion

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.di.FirebaseModule
import com.example.domain.model.DiscussionPost
import com.example.util.NotificationHelper
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DiscussionUiState(
    val posts: List<DiscussionPost> = emptyList(),
    val newPostText: String = "",
    val selectedTag: String = "Semua",
    val tags: List<String> = listOf("Semua", "Praktikum", "PBL", "Etnosains", "HOTS"),
    val isLoading: Boolean = false
)

class DiscussionViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(DiscussionUiState())
    val uiState: StateFlow<DiscussionUiState> = _uiState.asStateFlow()

    private val firestore = try {
        FirebaseModule.provideFirestore(application)
    } catch (e: Exception) {
        null
    }
    private val auth = try {
        FirebaseModule.provideFirebaseAuth(application)
    } catch (e: Exception) {
        null
    }

    private var snapshotListener: ListenerRegistration? = null
    private var isFirstSnapshot = true

    init {
        loadInitialPosts()
        startFirestoreRealtimeSync()
    }

    private fun loadInitialPosts() {
        val samplePosts = listOf(
            DiscussionPost(
                id = "post_1",
                authorName = "Ibu Guru Rahma, S.Pd.",
                authorRole = "GURU",
                timestamp = "10 menit yang lalu",
                content = "Anak-anak, perhatikan perubahan warna ekstrak Parijoto saat ditetesi larutan cuka dapur. Mengapa warna ungu berubah menjadi merah cerah?",
                topicTag = "Praktikum",
                commentCount = 5,
                likeCount = 12
            ),
            DiscussionPost(
                id = "post_2",
                authorName = "Ahmad Budi",
                authorRole = "SISWA",
                timestamp = "1 jam yang lalu",
                content = "Apakah struktur molekul antosianin Parijoto sama persis dengan yang ada pada kubis ungu?",
                topicTag = "Etnosains",
                commentCount = 2,
                likeCount = 4
            )
        )
        _uiState.update { it.copy(posts = samplePosts) }
    }

    private fun startFirestoreRealtimeSync() {
        val fs = firestore ?: return
        try {
            snapshotListener = fs.collection("forum_posts")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w("DiscussionVM", "Firestore forum listener error: ${error.message}")
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {
                        val postsFromCloud = snapshot.documents.mapNotNull { doc ->
                            try {
                                val id = doc.getString("id") ?: doc.id
                                val authorName = doc.getString("authorName") ?: "Pengguna"
                                val authorRole = doc.getString("authorRole") ?: "SISWA"
                                val content = doc.getString("content") ?: ""
                                val topicTag = doc.getString("topicTag") ?: "Etnosains"
                                val likesCount = doc.getLong("likesCount")?.toInt() ?: 0
                                val commentsCount = doc.getLong("commentsCount")?.toInt() ?: 0
                                val ts = doc.getTimestamp("createdAt")?.toDate()
                                val timeStr = if (ts != null) {
                                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(ts)
                                } else "Baru saja"

                                DiscussionPost(
                                    id = id,
                                    authorName = authorName,
                                    authorRole = authorRole,
                                    timestamp = timeStr,
                                    content = content,
                                    topicTag = topicTag,
                                    commentCount = commentsCount,
                                    likeCount = likesCount,
                                    isLiked = false
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }

                        if (!isFirstSnapshot && postsFromCloud.isNotEmpty()) {
                            val newest = postsFromCloud.first()
                            val currentUserName = auth?.currentUser?.displayName
                            if (!currentUserName.isNullOrBlank() && newest.authorName != currentUserName) {
                                NotificationHelper.showForumNotification(
                                    getApplication(),
                                    newest.authorName,
                                    newest.content.take(60)
                                )
                            }
                        }
                        isFirstSnapshot = false

                        _uiState.update { current ->
                            val currentIds = postsFromCloud.map { it.id }.toSet()
                            val localOnly = current.posts.filter { it.id !in currentIds }
                            current.copy(posts = postsFromCloud + localOnly)
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e("DiscussionVM", "Failed to start snapshot listener", e)
        }
    }

    fun onNewPostTextChange(text: String) {
        _uiState.update { it.copy(newPostText = text) }
    }

    fun onTagSelect(tag: String) {
        _uiState.update { it.copy(selectedTag = tag) }
    }

    fun submitPost(authorName: String, authorRole: String) {
        val text = _uiState.value.newPostText.trim()
        if (text.isBlank()) return

        val postId = "post_${System.currentTimeMillis()}"
        val timeStampStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val selectedTopic = if (_uiState.value.selectedTag == "Semua") "Etnosains" else _uiState.value.selectedTag

        val newPost = DiscussionPost(
            id = postId,
            authorName = authorName,
            authorRole = authorRole,
            timestamp = "Hari ini, $timeStampStr",
            content = text,
            topicTag = selectedTopic,
            commentCount = 0,
            likeCount = 0
        )

        _uiState.update {
            it.copy(
                posts = listOf(newPost) + it.posts,
                newPostText = ""
            )
        }

        viewModelScope.launch {
            val fs = firestore
            if (fs != null) {
                try {
                    val currentUid = auth?.currentUser?.uid ?: "user_${System.currentTimeMillis()}"
                    val map = hashMapOf(
                        "id" to postId,
                        "authorId" to currentUid,
                        "authorName" to authorName,
                        "authorRole" to authorRole,
                        "title" to selectedTopic,
                        "content" to text,
                        "topicTag" to selectedTopic,
                        "likesCount" to 0,
                        "commentsCount" to 0,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                    fs.collection("forum_posts").document(postId).set(map).await()
                } catch (e: Exception) {
                    Log.w("DiscussionVM", "Gagal menyimpan post ke Firestore: ${e.message}")
                }
            }
        }
    }

    fun toggleLike(postId: String) {
        var newLikeCount = 0
        var isNowLiked = false

        _uiState.update { state ->
            val updated = state.posts.map { post ->
                if (post.id == postId) {
                    isNowLiked = !post.isLiked
                    newLikeCount = if (isNowLiked) post.likeCount + 1 else (post.likeCount - 1).coerceAtLeast(0)
                    post.copy(isLiked = isNowLiked, likeCount = newLikeCount)
                } else post
            }
            state.copy(posts = updated)
        }

        viewModelScope.launch {
            val fs = firestore
            if (fs != null) {
                try {
                    fs.collection("forum_posts").document(postId)
                        .update("likesCount", newLikeCount, "updatedAt", FieldValue.serverTimestamp())
                        .await()
                } catch (e: Exception) {
                    Log.w("DiscussionVM", "Gagal update like di Firestore: ${e.message}")
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        snapshotListener?.remove()
    }
}
