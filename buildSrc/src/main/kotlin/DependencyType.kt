/**
 * @author Artem Chepurnoy
 */
enum class DependencyType(val configurationName: String) {
    IMPLEMENTATION("implementation"),
    KAPT("kapt"),
    TEST_IMPLEMENTATION("testImplementation"),
    ANDROID_TEST_IMPLEMENTATION("androidTestImplementation"),
}
