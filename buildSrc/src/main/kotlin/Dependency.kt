/**
 * @author Artem Chepurnoy
 */
data class Dependency(
    val name: String,
    val version: String,
    val notation: Any,
    val type: DependencyType
)