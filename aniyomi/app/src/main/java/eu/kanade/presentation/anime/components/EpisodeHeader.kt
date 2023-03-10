package eu.kanade.presentation.anime.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import eu.kanade.tachiyomi.R

@Composable
fun EpisodeHeader(
    enabled: Boolean,
    episodeCount: Int?,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = enabled,
                onClick = onClick,
            )
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = if (episodeCount == null) {
                stringResource(R.string.episodes)
            } else {
                pluralStringResource(id = R.plurals.anime_num_episodes, count = episodeCount, episodeCount)
            },
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}
