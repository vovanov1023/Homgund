plugins {
    id("java")
    alias(libs.plugins.shadow)
}

group = "me.vovanov"
version = "2.8.1.3"

repositories {
    mavenCentral()

    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo1.maven.org/maven2/net/luckperms/api/")
    maven("https://jitpack.io")
    maven("https://nexus.scarsz.me/content/groups/public/")
}

dependencies {
//    compileOnly("net.luckperms:api:$LuckPermsApiVersion")
//    compileOnly("com.github.LeonMangler:SuperVanish:$SuperVanishApiVersion")
//    compileOnly("com.github.Gecolay.GSit:core:$GSitVersion")
//    implementation("net.dv8tion:JDA:$JDAVersion") {exclude module: 'opus-java'}
//    compileOnly("com.discordsrv:discordsrv:$DiscordSRVVersion")

    compileOnly(libs.paper)
    compileOnly(libs.luckperms)
    compileOnly(libs.supervanish)
    compileOnly(libs.gsit)
    implementation(libs.jda)
    compileOnly(libs.discordsrv)
}

//java {
//    val javaVersion = JavaVersion.toVersion(21)
//    sourceCompatibility = javaVersion
//    targetCompatibility = javaVersion
//    if (JavaVersion.current() < javaVersion) {
//        toolchain.languageVersion = JavaLanguageVersion.of(21)
//    }
//}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release = 21
}
