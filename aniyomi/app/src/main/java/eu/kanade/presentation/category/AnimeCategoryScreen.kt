package eu.kanade.presentation.category

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import eu.kanade.domain.category.model.Category
import eu.kanade.presentation.category.components.CategoryContent
import eu.kanade.presentation.category.components.CategoryFloatingActionButton
import eu.kanade.presentation.components.AppBar
import eu.kanade.presentation.components.EmptyScreen
import eu.kanade.presentation.components.Scaffold
import eu.kanade.presentation.util.horizontalPadding
import eu.kanade.presentation.util.plus
import eu.kanade.presentation.util.topPaddingValues
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.ui.animecategory.AnimeCategoryScreenState

@Composable
fun AnimeCategoryScreen(
    state: AnimeCategoryScreenState.Success,
    onClickCreate: () -> Unit,
    onClickRename: (Category) -> Unit,
    onClickDelete: (Category) -> Unit,
    onClickMoveUp: (Category) -> Unit,
    onClickMoveDown: (Category) -> Unit,
    navigateUp: () -> Unit,
) {
    val lazyListState = rememberLazyListState()
    Scaffold(
        topBar = { scrollBehavior ->
            AppBar(
                title = stringResource(R.string.action_edit_categories),
                navigateUp = navigateUp,
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            CategoryFloatingActionButton(
                lazyListState = lazyListState,
                onCreate = onClickCreate,
            )
        },
    ) { paddingValues ->
        if (state.isEmpty) {
            EmptyScreen(
                textResource = R.string.information_empty_category,
                modifier = Modifier.padding(paddingValues),
            )
            return@Scaffold
        }

        CategoryContent(
            categories = state.categories,
            lazyListState = lazyListState,
            paddingValues = paddingValues + topPaddingValues + PaddingValues(horizontal = horizontalPadding),
            onClickRename = onClickRename,
            onClickDelete = onClickDelete,
            onMoveUp = onClickMoveUp,
            onMoveDown = onClickMoveDown,
        )
    }
}
