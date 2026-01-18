package com.kaaneneskpc.supplr.home.domain

import com.kaaneneskpc.supplr.shared.fonts.Resources
import org.jetbrains.compose.resources.DrawableResource

enum class DrawerItem(
    val title: String,
    val icon: DrawableResource
) {
    Profile(
        title = "Profile",
        icon = Resources.Icon.Person
    ),
    Favorites(
        title = "Favorites",
        icon = Resources.Icon.Favorites
    ),
    Locations(
        title = "Locations",
        icon = Resources.Icon.MapPin
    ),
    OrderHistory(
        title = "My Orders",
        icon = Resources.Icon.ShoppingCart
    ),
    Settings(
        title = "Settings",
        icon = Resources.Icon.Settings
    ),
    ContactUs(
        title = "Contact Us",
        icon = Resources.Icon.Edit
    ),
    SignOut(
        title = "Sign Out",
        icon = Resources.Icon.SignOut
    ),
    Admin(
        title = "Admin Panel",
        icon = Resources.Icon.Unlock
    )
}