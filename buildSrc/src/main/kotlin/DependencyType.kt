/**
 * @author Artem Chepurnoy
 */
enum class DependencyType(val configurationName: String) {
    IMPLEMENTATION("implementation"),
    DEBUG_IMPLEMENTATION("debugImplementation"),
    KSP("ksp"),
    TEST_IMPLEMENTATION("testImplementation"),
    ANDROID_TEST_IMPLEMENTATION("androidTestImplementation"),
    ANDROID_TEST_UTIL("androidTestUtil"),
}
