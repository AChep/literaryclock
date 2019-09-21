fun createDependencies(module: Module): List<Dependency> {
    val kotlinStdlib = Dependency(
        "Kotlin StdLib",
        KOTLIN_VERSION,
        "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$KOTLIN_VERSION",
        DependencyType.IMPLEMENTATION
    )
    val kotlinCoroutinesCore = Dependency(
        "Kotlin Coroutines Core",
        KOTLIN_COROUTINES_VERSION,
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:$KOTLIN_COROUTINES_VERSION",
        DependencyType.IMPLEMENTATION
    )
    val kotlinCoroutinesAndroid = Dependency(
        "Kotlin Coroutines Android",
        KOTLIN_COROUTINES_VERSION,
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:$KOTLIN_COROUTINES_VERSION",
        DependencyType.IMPLEMENTATION
    )
    val kotlinMockito = Dependency(
        "Mockito-Kotlin",
        KOTLIN_MOCKITO_VERSION,
        "com.nhaarman.mockitokotlin2:mockito-kotlin:$KOTLIN_MOCKITO_VERSION",
        DependencyType.TEST_IMPLEMENTATION
    )
    val junit = Dependency(
        "JUnit",
        JUNIT_VERSION,
        "junit:junit:$JUNIT_VERSION",
        DependencyType.TEST_IMPLEMENTATION
    )
    val kluent = Dependency(
        "Kluent",
        KLUENT_VERSION,
        "org.amshove.kluent:kluent-android:$KLUENT_VERSION",
        DependencyType.TEST_IMPLEMENTATION
    )

    val kodeinGenericJvm = Dependency(
        "Kodein Generic JVM",
        KODEIN_VERSION,
        "org.kodein.di:kodein-di-generic-jvm:$KODEIN_VERSION",
        DependencyType.IMPLEMENTATION
    )
    val kodeinAndroid = Dependency(
        "Kodein Android",
        KODEIN_VERSION,
        "org.kodein.di:kodein-di-framework-android-x:$KODEIN_VERSION",
        DependencyType.IMPLEMENTATION
    )

    val artemchepConfig = Dependency(
        "Config",
        ARTEMCHEP_CONFIG_VERSION,
        "com.artemchep.config:config:$ARTEMCHEP_CONFIG_VERSION",
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

    val theblueallianceSpectrum = Dependency(
        "Spectrum",
        THEBLUEALLIANCE_SPECTRUM_VERSION,
        "com.thebluealliance:spectrum:$THEBLUEALLIANCE_SPECTRUM_VERSION",
        DependencyType.IMPLEMENTATION
    )

    val zhanghaiMaterialProgressBar = Dependency(
        "Material Progress Bar",
        ZHANGHAI_MPB_VERSION,
        "me.zhanghai.android.materialprogressbar:library:$ZHANGHAI_MPB_VERSION",
        DependencyType.IMPLEMENTATION
    )

    val grendergToasty = Dependency(
        "Toasty",
        GRENDERG_TOASTY_VERSION,
        "com.github.GrenderG:Toasty:$GRENDERG_TOASTY_VERSION",
        DependencyType.IMPLEMENTATION
    )

    val yarolegovichDiscreteScrollView = Dependency(
        "Discrete ScrollView",
        YAROLEGOVICH_DISCRETESCROLLVIEW_VERSION,
        "com.yarolegovich:discrete-scrollview:$YAROLEGOVICH_DISCRETESCROLLVIEW_VERSION",
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
    val androidxLifecycleExt = Dependency(
        "AndroidX Lifecycle Ext",
        ANDROIDX_LIFECYCLE_VERSION,
        "androidx.lifecycle:lifecycle-extensions:$ANDROIDX_LIFECYCLE_VERSION",
        DependencyType.IMPLEMENTATION
    )
    val androidxBrowser = Dependency(
        "AndroidX Browser",
        ANDROIDX_BROWSER_VERSION,
        "androidx.browser:browser:$ANDROIDX_BROWSER_VERSION",
        DependencyType.IMPLEMENTATION
    )
    val androidxConstraintLayout = Dependency(
        "AndroidX Constraint Layout",
        ANDROIDX_CONSTRAINTLAYOUT_VERSION,
        "androidx.constraintlayout:constraintlayout:$ANDROIDX_CONSTRAINTLAYOUT_VERSION",
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
    val androidArchWorkGcm = Dependency(
        "Android Arch Work [GCMNetworkManager support]",
        ANDROIDARCH_WORK_VERSION,
        "androidx.work:work-gcm:$ANDROIDARCH_WORK_VERSION",
        DependencyType.IMPLEMENTATION
    )

    val googleMaterial = Dependency(
        "Google Material",
        GOOGLE_MATERIAL_VERSION,
        "com.google.android.material:material:$GOOGLE_MATERIAL_VERSION",
        DependencyType.IMPLEMENTATION
    )

    val googleFirebaseCore = Dependency(
        "Firebase Core",
        GOOGLE_FIREBASE_CORE,
        "com.google.firebase:firebase-core:$GOOGLE_FIREBASE_CORE",
        DependencyType.IMPLEMENTATION
    )
    val googleFirebaseFirestore = Dependency(
        "Firebase Firestore",
        GOOGLE_FIREBASE_FIRESTORE,
        "com.google.firebase:firebase-firestore:$GOOGLE_FIREBASE_FIRESTORE",
        DependencyType.IMPLEMENTATION
    )

    return when (module) {
        Module.APP -> listOf(
            kotlinStdlib,
            kotlinCoroutinesCore,
            kotlinCoroutinesAndroid,
            artemchepConfig,
            solovyevCheckout,
            hdodenhofCircleImageView,
            theblueallianceSpectrum,
            zhanghaiMaterialProgressBar,
            grendergToasty,
            yarolegovichDiscreteScrollView,
            mikepenzFastAdapter,
            kodeinGenericJvm,
            kodeinAndroid,
            androidxAppCompat,
            androidxKtx,
            androidxLifecycleExt,
            androidxBrowser,
            androidxConstraintLayout,
            androidArchNavigationFragment,
            androidArchNavigationUi,
            androidArchNavigationFragmentKtx,
            androidArchNavigationUiKtx,
            androidArchWork,
            androidArchWorkGcm,
            googleMaterial,
            googleFirebaseCore,
            googleFirebaseFirestore,
            junit,
            kotlinMockito,
            kluent
        )
    }
}