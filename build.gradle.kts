plugins {
    alias(libs.plugins.geomatys.boot.convention)
}

dependencies {
    implementation(platform(libs.geomatys.backend.bom))

    implementation("com.geomatys.backend.spring.starters:geomatys-web-starter")
    implementation("com.geomatys.backend.spring.starters:geomatys-tracing-starter")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // For nullable annotations.
    compileOnly("org.jspecify:jspecify:1.0.0")

    // UNCOMMENT TO USE APACHE SIS NIGHTLY BUILD
    implementation("org.geotoolkit:geotk-utility:5.0-SNAPSHOT")
    // implementation("org.apache.sis:sis-utility:2.0-SNAPSHOT")
}
