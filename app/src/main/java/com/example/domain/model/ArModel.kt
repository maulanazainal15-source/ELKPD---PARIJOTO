package com.example.domain.model

data class Ar3DModelItem(
    val id: String,
    val title: String,
    val description: String,
    val modelGlbUrl: String,
    val isAnthocyaninMolecule: Boolean,
    val interactiveNodes: List<ArNodeDetail>
)

data class ArNodeDetail(
    val nodeId: String,
    val label: String,
    val scientificExplanation: String
)

data class StudentReportExport(
    val studentId: String,
    val studentName: String,
    val classGroup: String,
    val hotsIndex: Float,
    val category: String,
    val quizAverage: Float,
    val pblScore: Float,
    val forumScore: Float,
    val completionTimestamp: String
)
