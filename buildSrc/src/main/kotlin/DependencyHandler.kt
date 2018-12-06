import org.gradle.kotlin.dsl.DependencyHandlerScope

fun handle(scope: DependencyHandlerScope, dependencies: List<Dependency>) {
    dependencies.forEach {
        scope.add(it.type.configurationName, it.notation)
    }
}
