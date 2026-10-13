package com.example.domain.model

data class QuizQuestion(
    val id: Int,
    val questionText: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String
)

data class QuizDuelResult(
    val player1Name: String,
    val player1Score: Int,
    val player2Name: String,
    val player2Score: Int,
    val isWinner: Boolean
)

data class DiscussionPost(
    val id: String,
    val authorName: String,
    val authorRole: String, // "SISWA" atau "GURU"
    val timestamp: String,
    val content: String,
    val topicTag: String,
    val commentCount: Int,
    val likeCount: Int,
    val isLiked: Boolean = false
)

data class DiscussionComment(
    val id: String,
    val postId: String,
    val authorName: String,
    val authorRole: String,
    val timestamp: String,
    val commentText: String
)
