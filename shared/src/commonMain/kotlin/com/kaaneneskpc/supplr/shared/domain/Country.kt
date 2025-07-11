package com.kaaneneskpc.supplr.shared.domain

import com.kaaneneskpc.supplr.shared.fonts.Resources
import org.jetbrains.compose.resources.DrawableResource

enum class Country(
    val dialCode: Int,
    val code: String,
    val flag: DrawableResource
) {
    Turkey(
        dialCode = 90,
        code = "TR",
        flag = Resources.Flag.Turkey
    ),
    Serbia(
        dialCode = 381,
        code = "RS",
        flag = Resources.Flag.Serbia
    ),
    India(
        dialCode = 91,
        code = "IN",
        flag = Resources.Flag.India
    ),
    Usa(
        dialCode = 1,
        code = "US",
        flag = Resources.Flag.Usa
    ),
    UnitedKingdom(
        dialCode = 44,
        code = "GB",
        flag = Resources.Flag.UnitedKingdom
    ),
    Germany(
        dialCode = 49,
        code = "DE",
        flag = Resources.Flag.Germany
    ),
    France(
        dialCode = 33,
        code = "FR",
        flag = Resources.Flag.France
    ),
    Canada(
        dialCode = 1,
        code = "CA",
        flag = Resources.Flag.Canada
    ),
    Australia(
        dialCode = 61,
        code = "AU",
        flag = Resources.Flag.Australia
    )
}