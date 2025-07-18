rootProject.name = "Supplr"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}
include(":data")
include(":di")
include(":di")
include(":feature:admin_panel")
include(":feature:cart")
include(":feature:cart")
include(":feature:categories")
include(":feature:categories:category_search")
include(":feature:checkout")
include(":feature:home")
include(":feature:manage_product")
include(":feature:manage_product")
include(":feature:payment_completed")
include(":feature:product_details")
include(":feature:products_overview")
include(":feature:profile")
include(":feautre:categories")
include(":navigation")
include(":feature:auth")
include(":navigation")
include(":composeApp")
include(":shared")
