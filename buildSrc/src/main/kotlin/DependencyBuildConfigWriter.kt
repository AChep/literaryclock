
private val DEPENDENCY_CLASS = """
    public static final class Dependency {
        public final String name;
        public final String versionName;
        public Dependency(String name, String versionName) {
            this.name = name;
            this.versionName = versionName;
        }
    }
""".trimIndent()

fun List<Dependency>.toJavaField(): Pair<String, String> {
    val fieldType = "Dependency[]"
    val fieldValue = toJavaFieldValue()
    return fieldType to "$fieldValue; $DEPENDENCY_CLASS //" // inject the class
}

private fun List<Dependency>.toJavaFieldValue() =
    joinToString(separator = ",") {
        """new Dependency("${it.name}", "${it.version}")"""
    }.let {
        "new Dependency[]{$it}"
    }
