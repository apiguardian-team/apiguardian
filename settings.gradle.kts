rootProject.name = "apiguardian-api"

require(JavaVersion.current().isJava11) {
    "The @API Guardian build requires Java 11. Currently executing with Java ${JavaVersion.current()}."
}
