package org.faaaa

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class FaaaaAnnotatorTest : BasePlatformTestCase() {

    /**
     * This is the crucial part. It tells the test framework to find and load your
     * plugin.xml file from the specified path. The path should be relative
     * to the project root.
     */
    override fun getTestDataPath(): String = "src/main/resources"

    fun `test annotator adds error for syntax error`() {
        // 1. Create a file with a syntax error.
        // We use a simple XML file because the base test platform always supports XML parsing
        // and it produces PsiErrorElements for malformed content.
        myFixture.configureByText("MyFile.xml", "<root><unclosed></root>")

        // 2. Run inspections, which will trigger the annotator.
        myFixture.checkHighlighting(true, false, true, true)

        // 3. Retrieve all highlights.
        val highlights = myFixture.doHighlighting()

        // Print all highlights for debugging.
        println("--- Found Highlights ---")
        highlights.forEach { highlight ->
            println("Severity: ${highlight.severity}, Description: ${highlight.description}, Text: '${highlight.text}'")
        }
        println("------------------------")

        // 4. Verify that an error annotation was created by our annotator.
        val hasOurError = highlights.any {
            it.severity == HighlightSeverity.ERROR && it.description != null && it.description.contains("Faaaa! An error!")
        }

        assertTrue("Annotator should have added an error highlight with 'Faaaa! An error!' description", hasOurError)
    }
}