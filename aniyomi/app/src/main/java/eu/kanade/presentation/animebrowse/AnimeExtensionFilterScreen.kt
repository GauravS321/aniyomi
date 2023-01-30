package eu.kanade.presentation.animebrowse

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import eu.kanade.presentation.components.AppBar
import eu.kanade.presentation.components.EmptyScreen
import eu.kanade.presentation.components.FastScrollLazyColumn
import eu.kanade.presentation.components.LoadingScreen
import eu.kanade.presentation.components.Scaffold
import eu.kanade.presentation.more.settings.widget.SwitchPreferenceWidget
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.ui.browse.animeextension.AnimeExtensionFilterPresenter
import eu.kanade.tachiyomi.util.system.LocaleHelper
import eu.kanade.tachiyomi.util.system.toast
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AnimeExtensionFilterScreen(
    navigateUp: () -> Unit,
    presenter: AnimeExtensionFilterPresenter,
) {
    val context = LocalContext.current
    Scaffold(
        topBar = { scrollBehavior ->
            AppBar(
                title = stringResource(R.string.label_extensions),
                navigateUp = navigateUp,
                scrollBehavior = scrollBehavior,
            )
        },
    ) { contentPadding ->
        when {
            presenter.isLoading -> LoadingScreen()
            presenter.isEmpty -> EmptyScreen(
                textResource = R.string.empty_screen,
                modifier = Modifier.padding(contentPadding),
            )
            else -> AnimeExtensionFilterContent(
                contentPadding = contentPadding,
                state = presenter,
                onClickLang = {
                    presenter.toggleLanguage(it)
                },
            )
        }
    }
    LaunchedEffect(Unit) {
        presenter.events.collectLatest {
            when (it) {
                AnimeExtensionFilterPresenter.Event.FailedFetchingLanguages -> {
                    context.toast(R.string.internal_error)
                }
            }
        }
    }
}

@Composable
private fun AnimeExtensionFilterContent(
    contentPadding: PaddingValues,
    state: AnimeExtensionFilterState,
    onClickLang: (String) -> Unit,
) {
    FastScrollLazyColumn(
        contentPadding = contentPadding,
    ) {
        items(
            items = state.items,
        ) { model ->
            val lang = model.lang
            SwitchPreferenceWidget(
                modifier = Modifier.animateItemPlacement(),
                title = LocaleHelper.getSourceDisplayName(lang, LocalContext.current),
                checked = model.enabled,
                onCheckedChanged = { onClickLang(lang) },
            )
        }
    }
}
