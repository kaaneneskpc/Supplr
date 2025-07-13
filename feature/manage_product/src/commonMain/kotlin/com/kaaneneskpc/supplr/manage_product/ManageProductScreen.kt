
package com.kaaneneskpc.supplr.manage_product

import androidx.compose.runtime.Composable
import com.kaaneneskpc.supplr.shared.component.CommonScaffold

@Composable
fun ManageProductScreen(
    id: String?,
    navigateBack: () -> Unit
) {
    CommonScaffold(
        title = if (id == null) "New Product" else "Edit Product",
        navigateBack = navigateBack
    ) {

    }
}
