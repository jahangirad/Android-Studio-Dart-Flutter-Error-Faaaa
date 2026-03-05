package org.faaaa

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.command.undo.UndoManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiErrorElement

class FaaaaAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val signalId = buildSignalId(element) ?: return

        if (element is PsiErrorElement) {
            // Add the annotation for the editor to display
            holder.newAnnotation(HighlightSeverity.ERROR, "Faaaa! An error!")
                .range(element.textRange)
                .create()

            if (shouldPlayFor(element)) {
                FaaaaSoundPlayer.playWithCooldown(signalId)
            }
        }
    }

    private fun shouldPlayFor(element: PsiElement): Boolean {
        val project = element.project
        val undoManager = UndoManager.getInstance(project)
        if (undoManager.isUndoInProgress || undoManager.isRedoInProgress) return false

        val commandName = CommandProcessor.getInstance().currentCommandName?.lowercase()
        if (commandName == "undo" || commandName == "redo") return false

        val document = PsiDocumentManager.getInstance(project).getDocument(element.containingFile) ?: return false
        val fileDocumentManager = FileDocumentManager.getInstance()

        // Do not play during active typing/undo transient states.
        if (fileDocumentManager.isDocumentUnsaved(document)) return false

        // Only play for the file currently opened in editor.
        val selectedEditor = FileEditorManager.getInstance(project).selectedTextEditor ?: return false
        return selectedEditor.document == document
    }

    private fun buildSignalId(element: PsiElement): String? {
        val file = element.containingFile?.virtualFile ?: return null
        val start = element.textRange?.startOffset ?: return null
        return "${file.path}:$start:${element.javaClass.simpleName}"
    }
}
