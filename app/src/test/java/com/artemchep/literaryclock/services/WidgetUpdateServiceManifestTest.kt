package com.artemchep.literaryclock.services

import android.content.pm.PackageManager
import com.google.common.truth.Truth.assertThat
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class WidgetUpdateServiceManifestTest {
    @Test
    fun specialUseForegroundServiceDeclaresSubtype() {
        val manifestFile = listOf(
            File("app/src/main/AndroidManifest.xml"),
            File("src/main/AndroidManifest.xml"),
        ).first(File::exists)
        val document = DocumentBuilderFactory.newInstance()
            .apply { isNamespaceAware = true }
            .newDocumentBuilder()
            .parse(manifestFile)
        val services = document.getElementsByTagName("service")

        val widgetUpdateService = (0 until services.length)
            .map { services.item(it) }
            .single { service ->
                service.attributes.getNamedItemNS(
                    "http://schemas.android.com/apk/res/android",
                    "name",
                ).nodeValue == ".services.WidgetUpdateService"
            }
        val properties = widgetUpdateService.childNodes
        val subtype = (0 until properties.length)
            .map { properties.item(it) }
            .filter { it.nodeName == "property" }
            .single {
                it.attributes.getNamedItemNS(
                    "http://schemas.android.com/apk/res/android",
                    "name",
                )?.nodeValue == PackageManager.PROPERTY_SPECIAL_USE_FGS_SUBTYPE
            }
            .attributes
            .getNamedItemNS(
                "http://schemas.android.com/apk/res/android",
                "value",
            )
            .nodeValue

        assertThat(subtype).isEqualTo("keep_literary_clock_widgets_current")
    }
}
