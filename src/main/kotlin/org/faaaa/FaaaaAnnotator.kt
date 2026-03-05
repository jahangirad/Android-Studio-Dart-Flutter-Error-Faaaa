package org.faaaa

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.PsiReference

class FaaaaAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element is PsiErrorElement) {
            // Add the annotation for the editor to display
            holder.newAnnotation(HighlightSeverity.ERROR, "Faaaa! An error!")
                .range(element.textRange)
                .create()

            if (shouldPlayFor(element)) {
                FaaaaSoundPlayer.playWithCooldown()
            }
            return
        }

        // Also react to unresolved references (e.g. misspelled method/identifier)
        // where the IDE already provides its own highlight message.
        if (isUnresolvedReference(element) && shouldPlayFor(element)) {
            FaaaaSoundPlayer.playWithCooldown()
        }
    }

    private fun isUnresolvedReference(element: PsiElement): Boolean {
        val reference: PsiReference = element.reference ?: return false
        if (reference.isSoft) return false

        return if (reference is PsiPolyVariantReference) {
            reference.multiResolve(false).isEmpty()
        } else {
            reference.resolve() == null
        }
    }

    private fun shouldPlayFor(element: PsiElement): Boolean {
        val project = element.project
        val document = PsiDocumentManager.getInstance(project).getDocument(element.containingFile) ?: return false
        // Avoid false triggers during file open/switch and startup indexing:
        // play only when user has unsaved edits in this document.
        return FileDocumentManager.getInstance().isDocumentUnsaved(document)
    }
}
