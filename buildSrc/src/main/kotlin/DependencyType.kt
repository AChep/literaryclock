/**
 * @author Artem Chepurnoy
 */
enum class DependencyType(val configurationName: String) {
    IMPLEMENTATION("implementation"),
    TEST_IMPLEMENTATION("testImplementation")
}
