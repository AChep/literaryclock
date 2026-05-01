fun createDependencies(module: Module): List<Dependency> {
    val kotlinCoroutinesAndroid = Dependency(
        "Kotlin Coroutines Android",
        KOTLIN_COROUTINES_VERSION,
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:$KOTLIN_COROUTINES_VERSION",
        DependencyType.IMPLEMENTATION
    )
    val kotlinMockito = Dependency(
        "Mockito-Kotlin",
        KOTLIN_MOCKITO_VERSION,
        "org.mockito.kotlin:mockito-kotlin:$KOTLIN_MOCKITO_VERSION",
        DependencyType.TEST_IMPLEMENTATION
    )
    val junit = Dependency(
        "JUnit",
        JUNIT_VERSION,
        "junit:junit:$JUNIT_VERSION",
        DependencyType.TEST_IMPLEMENTATION
    )
    val robolectric = Dependency(
        "Robolectric",
        ROBOLECTRIC_VERSION,
        "org.robolectric:robolectric:$ROBOLECTRIC_VERSION",
        DependencyType.TEST_IMPLEMENTATION,
    )
    val truth = Dependency(
        "Truth",
        TRUTH_VERSION,
        "com.google.truth:truth:$TRUTH_VERSION",
        DependencyType.TEST_IMPLEMENTATION,
    )
    val kluent = Dependency(
        "Kluent",
        KLUENT_VERSION,
        "org.amshove.kluent:kluent-android:$KLUENT_VERSION",
        DependencyType.TEST_IMPLEMENTATION
    )
    val kotlinCoroutinesTest = Dependency(
        "Kotlin Coroutines Test",
        KOTLIN_COROUTINES_VERSION,
        "org.jetbrains.kotlinx:kotlinx-coroutines-test:$KOTLIN_COROUTINES_VERSION",
        DependencyType.TEST_IMPLEMENTATION
    )

    val kodeinGenericJvm = Dependency(
        "Kodein DI",
        KODEIN_VERSION,
        "org.kodein.di:kodein-di:$KODEIN_VERSION",
        DependencyType.IMPLEMENTATION
    )
    val kodeinAndroid = Dependency(
        "Kodein DI Android",
        KODEIN_VERSION,
        "org.kodein.di:kodein-di-framework-android-x:$KODEIN_VERSION",
        DependencyType.IMPLEMENTATION
    )

    val solovyevCheckout = Dependency(
        "Checkout",
        SOLOVYEV_CHECKOUT_VERSION,
        "org.solovyev.android:checkout:$SOLOVYEV_CHECKOUT_VERSION",
        DependencyType.IMPLEMENTATION
    )

    val hdodenhofCircleImageView = Dependency(
        "Circle ImageView",
        HDODENHOF_CIRCLEIMAGEVIEW_VERSION,
        "de.hdodenhof:circleimageview:$HDODENHOF_CIRCLEIMAGEVIEW_VERSION",
        DependencyType.IMPLEMENTATION
    )

    val grendergToasty = Dependency(
        "Toasty",
        GRENDERG_TOASTY_VERSION,
        "com.github.GrenderG:Toasty:$GRENDERG_TOASTY_VERSION",
        DependencyType.IMPLEMENTATION
    )
    val yukukuAmbilWarna = Dependency(
        "AmbilWarna",
        AMBIL_WARNA_VERSION,
        "com.github.yukuku:ambilwarna:$AMBIL_WARNA_VERSION",
        DependencyType.IMPLEMENTATION
    )

    val mikepenzFastAdapter = Dependency(
        "Fast Adapter",
        MIKEPENZ_FASTADAPTER_VERSION,
        "com.mikepenz:fastadapter:$MIKEPENZ_FASTADAPTER_VERSION",
        DependencyType.IMPLEMENTATION
    )

    val androidxAppCompat = Dependency(
        "AndroidX AppCompat",
        ANDROIDX_APPCOMPAT_VERSION,
        "androidx.appcompat:appcompat:$ANDROIDX_APPCOMPAT_VERSION",
        DependencyType.IMPLEMENTATION
    )
    val androidxKtx = Dependency(
        "AndroidX KTX",
        ANDROIDX_KTX_VERSION,
        "androidx.core:core-ktx:$ANDROIDX_KTX_VERSION",
        DependencyType.IMPLEMENTATION
    )
    val androidxLifecycleViewModel = Dependency(
        "AndroidX Lifecycle ViewModel",
        ANDROIDX_LIFECYCLE_VERSION,
        "androidx.lifecycle:lifecycle-viewmodel-ktx:$ANDROIDX_LIFECYCLE_VERSION",
        DependencyType.IMPLEMENTATION
    )
    val androidxLifecycleLiveData = Dependency(
        "AndroidX Lifecycle LiveData",
        ANDROIDX_LIFECYCLE_VERSION,
        "androidx.lifecycle:lifecycle-livedata-ktx:$ANDROIDX_LIFECYCLE_VERSION",
        DependencyType.IMPLEMENTATION
    )
    val androidxLifecycleRuntime = Dependency(
        "AndroidX Lifecycle Runtime",
        ANDROIDX_LIFECYCLE_VERSION,
        "androidx.lifecycle:lifecycle-runtime-ktx:$ANDROIDX_LIFECYCLE_VERSION",
        DependencyType.IMPLEMENTATION
    )
    val androidxLifecycleProcess = Dependency(
        "AndroidX Lifecycle Process",
        ANDROIDX_LIFECYCLE_VERSION,
        "androidx.lifecycle:lifecycle-process:$ANDROIDX_LIFECYCLE_VERSION",
        DependencyType.IMPLEMENTATION
    )
    val androidxBrowser = Dependency(
        "AndroidX Browser",
        ANDROIDX_BROWSER_VERSION,
        "androidx.browser:browser:$ANDROIDX_BROWSER_VERSION",
        DependencyType.IMPLEMENTATION
    )
    val androidxFragment = Dependency(
        "AndroidX Fragment",
        ANDROIDX_FRAGMENT_VERSION,
        "androidx.fragment:fragment:$ANDROIDX_FRAGMENT_VERSION",
        DependencyType.IMPLEMENTATION
    )
    val androidxFragmentKtx = Dependency(
        "AndroidX Fragment KTX",
        ANDROIDX_FRAGMENT_VERSION,
        "androidx.fragment:fragment-ktx:$ANDROIDX_FRAGMENT_VERSION",
        DependencyType.IMPLEMENTATION
    )
    val androidxFragmentTesting = Dependency(
        "AndroidX Fragment Testing",
        ANDROIDX_FRAGMENT_VERSION,
        "androidx.fragment:fragment-testing:$ANDROIDX_FRAGMENT_VERSION",
        DependencyType.DEBUG_IMPLEMENTATION,
    )
    val androidxConstraintLayout = Dependency(
        "AndroidX Constraint Layout",
        ANDROIDX_CONSTRAINTLAYOUT_VERSION,
        "androidx.constraintlayout:constraintlayout:$ANDROIDX_CONSTRAINTLAYOUT_VERSION",
        DependencyType.IMPLEMENTATION
    )
    val androidxTransition = Dependency(
        "AndroidX Transition",
        ANDROIDX_TRANSITION_VERSION,
        "androidx.transition:transition:$ANDROIDX_TRANSITION_VERSION",
        DependencyType.IMPLEMENTATION
    )
    val androidArchNavigationFragment = Dependency(
        "Android Arch Navigation Fragment",
        ANDROIDARCH_NAVIGATION_VERSION,
        "androidx.navigation:navigation-fragment:$ANDROIDARCH_NAVIGATION_VERSION",
        DependencyType.IMPLEMENTATION
    )
    val androidArchNavigationUi = Dependency(
        "Android Arch Navigation UI",
        ANDROIDARCH_NAVIGATION_VERSION,
        "androidx.navigation:navigation-ui:$ANDROIDARCH_NAVIGATION_VERSION",
        DependencyType.IMPLEMENTATION
    )
    val androidArchNavigationFragmentKtx = Dependency(
        "Android Arch Navigation Fragment KTX",
        ANDROIDARCH_NAVIGATION_VERSION,
        "androidx.navigation:navigation-fragment-ktx:$ANDROIDARCH_NAVIGATION_VERSION",
        DependencyType.IMPLEMENTATION
    )
    val androidArchNavigationUiKtx = Dependency(
        "Android Arch Navigation UI KTX",
        ANDROIDARCH_NAVIGATION_VERSION,
        "androidx.navigation:navigation-ui-ktx:$ANDROIDARCH_NAVIGATION_VERSION",
        DependencyType.IMPLEMENTATION
    )
    val androidArchWork = Dependency(
        "Android Arch Work",
        ANDROIDARCH_WORK_VERSION,
        "androidx.work:work-runtime-ktx:$ANDROIDARCH_WORK_VERSION",
        DependencyType.IMPLEMENTATION
    )
    val googleMaterial = Dependency(
        "Google Material",
        GOOGLE_MATERIAL_VERSION,
        "com.google.android.material:material:$GOOGLE_MATERIAL_VERSION",
        DependencyType.IMPLEMENTATION
    )
    val roomRuntime = Dependency(
        "AndroidX Room Runtime",
        ANDROIDX_ROOM_VERSION,
        "androidx.room:room-runtime:$ANDROIDX_ROOM_VERSION",
        DependencyType.IMPLEMENTATION
    )
    val roomKtx = Dependency(
        "AndroidX Room KTX",
        ANDROIDX_ROOM_VERSION,
        "androidx.room:room-ktx:$ANDROIDX_ROOM_VERSION",
        DependencyType.IMPLEMENTATION
    )
    val roomCompiler = Dependency(
        "AndroidX Room Compiler",
        ANDROIDX_ROOM_VERSION,
        "androidx.room:room-compiler:$ANDROIDX_ROOM_VERSION",
        DependencyType.KAPT
    )

    val googleFirebaseCore = Dependency(
        "Firebase Analytics",
        GOOGLE_FIREBASE_BOM_VERSION,
        "com.google.firebase:firebase-analytics",
        DependencyType.IMPLEMENTATION
    )
    val googleFirebaseFirestore = Dependency(
        "Firebase Firestore",
        GOOGLE_FIREBASE_BOM_VERSION,
        "com.google.firebase:firebase-firestore",
        DependencyType.IMPLEMENTATION
    )

    val acraHttpSender = Dependency(
        "ACRA HTTP sender",
        ACRA_VERSION,
        "ch.acra:acra-http:$ACRA_VERSION",
        DependencyType.IMPLEMENTATION
    )
    val roomTesting = Dependency(
        "AndroidX Room Testing",
        ANDROIDX_ROOM_VERSION,
        "androidx.room:room-testing:$ANDROIDX_ROOM_VERSION",
        DependencyType.ANDROID_TEST_IMPLEMENTATION
    )
    val androidxArchCoreTesting = Dependency(
        "AndroidX Arch Core Testing",
        ANDROIDX_ARCH_CORE_TESTING_VERSION,
        "androidx.arch.core:core-testing:$ANDROIDX_ARCH_CORE_TESTING_VERSION",
        DependencyType.TEST_IMPLEMENTATION
    )
    val androidxTestCoreJvm = Dependency(
        "AndroidX Test Core KTX",
        ANDROIDX_TEST_CORE_VERSION,
        "androidx.test:core-ktx:$ANDROIDX_TEST_CORE_VERSION",
        DependencyType.TEST_IMPLEMENTATION,
    )
    val androidxWorkTestingJvm = Dependency(
        "AndroidX Work Testing",
        ANDROIDARCH_WORK_VERSION,
        "androidx.work:work-testing:$ANDROIDARCH_WORK_VERSION",
        DependencyType.TEST_IMPLEMENTATION,
    )
    val androidArchNavigationTestingJvm = Dependency(
        "Android Arch Navigation Testing",
        ANDROIDARCH_NAVIGATION_VERSION,
        "androidx.navigation:navigation-testing:$ANDROIDARCH_NAVIGATION_VERSION",
        DependencyType.TEST_IMPLEMENTATION,
    )
    val androidxTestCoreAndroid = Dependency(
        "AndroidX Test Core KTX",
        ANDROIDX_TEST_CORE_VERSION,
        "androidx.test:core-ktx:$ANDROIDX_TEST_CORE_VERSION",
        DependencyType.ANDROID_TEST_IMPLEMENTATION
    )
    val androidxTestExtJunit = Dependency(
        "AndroidX Test Ext JUnit",
        ANDROIDX_TEST_EXT_JUNIT_VERSION,
        "androidx.test.ext:junit:$ANDROIDX_TEST_EXT_JUNIT_VERSION",
        DependencyType.ANDROID_TEST_IMPLEMENTATION
    )
    val androidxTestRunner = Dependency(
        "AndroidX Test Runner",
        ANDROIDX_TEST_CORE_VERSION,
        "androidx.test:runner:$ANDROIDX_TEST_CORE_VERSION",
        DependencyType.ANDROID_TEST_IMPLEMENTATION
    )
    val androidxTestRules = Dependency(
        "AndroidX Test Rules",
        ANDROIDX_TEST_CORE_VERSION,
        "androidx.test:rules:$ANDROIDX_TEST_CORE_VERSION",
        DependencyType.ANDROID_TEST_IMPLEMENTATION,
    )
    val androidxWorkTesting = Dependency(
        "AndroidX Work Testing",
        ANDROIDARCH_WORK_VERSION,
        "androidx.work:work-testing:$ANDROIDARCH_WORK_VERSION",
        DependencyType.ANDROID_TEST_IMPLEMENTATION
    )
    val androidArchNavigationTesting = Dependency(
        "Android Arch Navigation Testing",
        ANDROIDARCH_NAVIGATION_VERSION,
        "androidx.navigation:navigation-testing:$ANDROIDARCH_NAVIGATION_VERSION",
        DependencyType.ANDROID_TEST_IMPLEMENTATION,
    )
    val androidxTestEspressoCore = Dependency(
        "AndroidX Test Espresso Core",
        ANDROIDX_TEST_ESPRESSO_VERSION,
        "androidx.test.espresso:espresso-core:$ANDROIDX_TEST_ESPRESSO_VERSION",
        DependencyType.ANDROID_TEST_IMPLEMENTATION,
    )
    val androidxTestEspressoContrib = Dependency(
        "AndroidX Test Espresso Contrib",
        ANDROIDX_TEST_ESPRESSO_VERSION,
        "androidx.test.espresso:espresso-contrib:$ANDROIDX_TEST_ESPRESSO_VERSION",
        DependencyType.ANDROID_TEST_IMPLEMENTATION,
    )
    val androidxTestEspressoIntents = Dependency(
        "AndroidX Test Espresso Intents",
        ANDROIDX_TEST_ESPRESSO_VERSION,
        "androidx.test.espresso:espresso-intents:$ANDROIDX_TEST_ESPRESSO_VERSION",
        DependencyType.ANDROID_TEST_IMPLEMENTATION,
    )
    val androidxTestUiAutomator = Dependency(
        "AndroidX Test UI Automator",
        ANDROIDX_TEST_UIAUTOMATOR_VERSION,
        "androidx.test.uiautomator:uiautomator:$ANDROIDX_TEST_UIAUTOMATOR_VERSION",
        DependencyType.ANDROID_TEST_IMPLEMENTATION,
    )
    val androidxTestOrchestrator = Dependency(
        "AndroidX Test Orchestrator",
        ANDROIDX_TEST_CORE_VERSION,
        "androidx.test:orchestrator:$ANDROIDX_TEST_CORE_VERSION",
        DependencyType.ANDROID_TEST_UTIL,
    )

    return when (module) {
        Module.APP -> listOf(
            kotlinCoroutinesAndroid,
            roomRuntime,
            roomKtx,
            roomCompiler,
            solovyevCheckout,
            hdodenhofCircleImageView,
            grendergToasty,
            yukukuAmbilWarna,
            mikepenzFastAdapter,
            kodeinGenericJvm,
            kodeinAndroid,
            androidxAppCompat,
            androidxKtx,
            androidxLifecycleLiveData,
            androidxLifecycleViewModel,
            androidxLifecycleRuntime,
            androidxLifecycleProcess,
            androidxBrowser,
            androidxFragment,
            androidxFragmentKtx,
            androidxFragmentTesting,
            androidxConstraintLayout,
            androidxTransition,
            androidArchNavigationFragment,
            androidArchNavigationUi,
            androidArchNavigationFragmentKtx,
            androidArchNavigationUiKtx,
            androidArchWork,
            googleMaterial,
            googleFirebaseCore,
            googleFirebaseFirestore,
            acraHttpSender,
            junit,
            robolectric,
            truth,
            kotlinCoroutinesTest,
            kotlinMockito,
            kluent,
            androidxTestCoreJvm,
            androidxWorkTestingJvm,
            androidArchNavigationTestingJvm,
            roomTesting,
            androidxArchCoreTesting,
            androidxTestCoreAndroid,
            androidxTestExtJunit,
            androidxTestRunner,
            androidxTestRules,
            androidxWorkTesting,
            androidArchNavigationTesting,
            androidxTestEspressoCore,
            androidxTestEspressoContrib,
            androidxTestEspressoIntents,
            androidxTestUiAutomator,
            androidxTestOrchestrator,
        )
    }
}
