package com.example.util

import androidx.compose.foundation.layout.sizeIn
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp

/**
 * Ekstensi modifier untuk menjamin kepatuhan standar aksesibilitas WCAG 2.1 Level AA/AAA
 * pada seluruh komponen interaktif aplikasi E-LKPD Etnosains Parijoto.
 */
object AccessibilityExtensions {

    /**
     * Memastikan elemen interaktif memiliki target sentuh minimal 48x48 dp
     * dan deskripsi konten yang jelas untuk pembaca layar (TalkBack).
     */
    fun Modifier.accessibleInteractiveElement(
        description: String,
        elementRole: Role = Role.Button,
        currentStateDescription: String? = null
    ): Modifier = this
        .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
        .semantics(mergeDescendants = true) {
            contentDescription = description
            role = elementRole
            if (!currentStateDescription.isNullOrBlank()) {
                stateDescription = currentStateDescription
            }
        }

    /**
     * Menandai elemen teks sebagai judul bagian (Section Heading)
     * agar pengguna TalkBack dapat bernavigasi antar-bagian dengan cepat.
     */
    fun Modifier.accessibleHeading(titleDescription: String? = null): Modifier = this
        .semantics {
            heading()
            if (!titleDescription.isNullOrBlank()) {
                contentDescription = titleDescription
            }
        }
}
