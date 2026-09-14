package com.artemchep.literaryclock.ui.activities

import com.google.common.truth.Truth.assertThat
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList

@RunWith(RobolectricTestRunner::class)
class MainActivityManifestTest {
    @Test
    fun chromeOsWindowPreferencesAreDeclaredOnMainActivity() {
        val document = DocumentBuilderFactory.newInstance()
            .apply { isNamespaceAware = true }
            .newDocumentBuilder()
            .parse(manifestFile())
        val androidNamespace = "http://schemas.android.com/apk/res/android"
        val preferencePrefix = "WindowManagerPreference:"

        val manifestMetaData = document.documentElement.childElements("meta-data")
            .map {
                it.getAttributeNS(androidNamespace, "name")
            }
            .filter { it.startsWith(preferencePrefix) }
        assertThat(manifestMetaData).isEmpty()

        val mainActivity = document.getElementsByTagName("activity")
            .asElements()
            .single {
                it.getAttributeNS(androidNamespace, "name") == ".ui.activities.MainActivity"
            }
        val activityPreferences = mainActivity.childElements("meta-data")
            .associate {
                it.getAttributeNS(androidNamespace, "name") to
                    it.getAttributeNS(androidNamespace, "value")
            }

        assertThat(activityPreferences).containsAtLeast(
            "WindowManagerPreference:SuppressWindowControlNavigationButton",
            "true",
            "WindowManagerPreference:FreeformWindowSize",
            "tablet",
            "WindowManagerPreference:FreeformWindowOrientation",
            "landscape",
        )
    }

    private fun manifestFile(): File = listOf(
        File("app/src/main/AndroidManifest.xml"),
        File("src/main/AndroidManifest.xml"),
    ).first(File::exists)

    private fun Element.childElements(tagName: String): List<Element> =
        childNodes.asElements()
            .filter { it.tagName == tagName }

    private fun NodeList.asElements(): List<Element> =
        (0 until length)
            .map { item(it) }
            .filter { it.nodeType == Node.ELEMENT_NODE }
            .map { it as Element }
}
