package com.lain.soft.claramobilechallenge.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.lain.soft.claramobilechallenge.R

@Composable
fun ArtistListItem(
    modifier: Modifier = Modifier,
    imageModifier: Modifier = Modifier,
    nameModifier: Modifier = Modifier,
    isLoading: Boolean = false,
    id: Int,
    name: String,
    thumbnail: String?,
    onClick: (id: Int) -> Unit
) {
    Surface(
        modifier = modifier,
        shadowElevation = 40.dp,
        onClick = {
            onClick(id)
        },
        color = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Column{
            Surface(
                modifier = Modifier.height(150.dp)
                    .fillMaxWidth()
                    .shimmer(
                        isLoading = isLoading,
                        cornerRadius = 12.dp
                    ),
                shape = RoundedCornerShape(12.dp),
            ) {
                AsyncImage(
                    modifier = imageModifier
                        .height(150.dp)
                        .fillMaxWidth(),
                    model = thumbnail,
                    contentDescription = name,
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.artist_placeholder),
                    error = painterResource(R.drawable.artist_placeholder),
                    fallback = painterResource(R.drawable.artist_placeholder),
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                modifier = nameModifier
                    .fillMaxWidth()
                    .shimmer(cornerRadius = 8.dp, isLoading = isLoading),
                text = name,
                maxLines = 2,
                minLines = 2,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}
