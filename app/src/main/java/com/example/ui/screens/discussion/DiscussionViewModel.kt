package com.example.ui.screens.discussion

import androidx.lifecycle.ViewModel
import com.example.domain.model.DiscussionPost
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

class DiscussionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DiscussionUiState())
    val uiState: StateFlow<DiscussionUiState> = _uiState.asStateFlow()

    init {
        loadInitialPosts()
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

    fun onNewPostTextChange(text: String) {
        _uiState.update { it.copy(newPostText = text) }
    }

    fun onTagSelect(tag: String) {
        _uiState.update { it.copy(selectedTag = tag) }
    }

    fun submitPost(authorName: String, authorRole: String) {
        val text = _uiState.value.newPostText.trim()
        if (text.isBlank()) return

        val timeStampStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val newPost = DiscussionPost(
            id = "post_${System.currentTimeMillis()}",
            authorName = authorName,
            authorRole = authorRole,
            timestamp = "Hari ini, $timeStampStr",
            content = text,
            topicTag = if (_uiState.value.selectedTag == "Semua") "Etnosains" else _uiState.value.selectedTag,
            commentCount = 0,
            likeCount = 0
        )

        _uiState.update {
            it.copy(
                posts = listOf(newPost) + it.posts,
                newPostText = ""
            )
        }
    }

    fun toggleLike(postId: String) {
        _uiState.update { state ->
            val updated = state.posts.map { post ->
                if (post.id == postId) {
                    val isLikedNow = !post.isLiked
                    val newCount = if (isLikedNow) post.likeCount + 1 else post.likeCount - 1
                    post.copy(isLiked = isLikedNow, likeCount = newCount)
                } else post
            }
            state.copy(posts = updated)
        }
    }
}
