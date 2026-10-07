package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Conversation
import com.example.ui.theme.CanvasAvatarBg
import com.example.ui.theme.CanvasAvatarText
import com.example.ui.theme.CanvasDrawerBackground
import com.example.ui.theme.CanvasDrawerBorder
import com.example.ui.theme.CanvasDrawerSurface
import com.example.ui.theme.CanvasDrawerSurfaceActive
import com.example.ui.theme.CanvasDrawerTextMuted
import com.example.ui.theme.CanvasDrawerTextPrimary
import com.example.ui.theme.CanvasLimeAccent
import com.example.ui.theme.CanvasLimeOnColor
import com.example.ui.theme.CanvasSparksCardBg
import com.example.ui.theme.CanvasSparksCardBorder

@Composable
fun CanvasDrawer(
    conversations: List<Conversation>,
    activeConversationId: String?,
    onSelectConversation: (String) -> Unit,
    onNewThread: () -> Unit,
    onOpenSpaces: () -> Unit,
    onOpenSearch: () -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(320.dp)
            .background(CanvasDrawerBackground)
            .statusBarsPadding()
            .padding(horizontal = 18.dp, vertical = 12.dp)
            .testTag("canvas_sidebar_drawer")
    ) {
        // Top Header: Canvas Logo & Close Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Sparkle Badge
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(CanvasLimeAccent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Canvas Logo",
                        tint = CanvasLimeOnColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Canvas",
                    color = CanvasDrawerTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(
                onClick = onCloseDrawer,
                modifier = Modifier.size(32.dp).testTag("close_drawer_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = CanvasDrawerTextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // New thread Button (Signature Bright Lime Pill)
        Surface(
            color = CanvasLimeAccent,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onNewThread()
                    onCloseDrawer()
                }
                .testTag("drawer_new_thread_button")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AddCircleOutline,
                        contentDescription = null,
                        tint = CanvasLimeOnColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "New thread",
                        color = CanvasLimeOnColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Shortcut Pill: ⌘ K
                Box(
                    modifier = Modifier
                        .border(1.dp, Color(0xFFBACF50), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "⌘ K",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF333E18)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Menu Items: Search, Spaces (4), Archive
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            DrawerMenuItem(
                icon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = CanvasDrawerTextMuted, modifier = Modifier.size(19.dp))
                },
                label = "Search",
                onClick = onOpenSearch
            )

            DrawerMenuItem(
                icon = {
                    Icon(Icons.Default.GridView, contentDescription = null, tint = CanvasDrawerTextMuted, modifier = Modifier.size(19.dp))
                },
                label = "Spaces",
                badge = "4",
                onClick = {
                    onOpenSpaces()
                    onCloseDrawer()
                }
            )

            DrawerMenuItem(
                icon = {
                    Icon(Icons.Default.Inventory2, contentDescription = null, tint = CanvasDrawerTextMuted, modifier = Modifier.size(19.dp))
                },
                label = "Archive",
                onClick = {}
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = CanvasDrawerBorder, thickness = 1.dp)
        Spacer(modifier = Modifier.height(14.dp))

        // RECENT THREADS Header
        Text(
            text = "RECENT THREADS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = CanvasDrawerTextMuted,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
        )

        // Threads List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            items(conversations) { conv ->
                val isSelected = conv.id == activeConversationId
                val timeLabel = formatRelativeTime(conv.lastUpdatedAt)

                Surface(
                    color = if (isSelected) CanvasDrawerSurfaceActive else Color.Transparent,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelectConversation(conv.id)
                            onCloseDrawer()
                        }
                        .testTag("drawer_thread_${conv.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 9.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = conv.title,
                            color = if (isSelected) CanvasDrawerTextPrimary else Color(0xFFC7CDC2),
                            fontSize = 13.5.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = timeLabel,
                            color = CanvasDrawerTextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 12 sparks left Card
        Surface(
            color = CanvasSparksCardBg,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CanvasSparksCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(CanvasSparksCardBorder),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = CanvasLimeAccent,
                        modifier = Modifier.size(15.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "12 sparks left",
                        color = CanvasDrawerTextPrimary,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Refreshes tomorrow",
                        color = CanvasDrawerTextMuted,
                        fontSize = 10.5.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // User Profile Footer: Alex Morgan
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(CanvasAvatarBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "AM",
                        color = CanvasAvatarText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Alex Morgan",
                        color = CanvasDrawerTextPrimary,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Personal space",
                        color = CanvasDrawerTextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            IconButton(onClick = {}, modifier = Modifier.size(28.dp)) {
                Icon(
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = "Options",
                    tint = CanvasDrawerTextMuted
                )
            }
        }
    }
}

@Composable
fun DrawerMenuItem(
    icon: @Composable () -> Unit,
    label: String,
    badge: String? = null,
    onClick: () -> Unit
) {
    Surface(
        color = Color.Transparent,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 9.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                icon()
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = label,
                    color = Color(0xFFD4DAD0),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            if (badge != null) {
                Text(
                    text = badge,
                    color = CanvasDrawerTextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

fun formatRelativeTime(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val minutes = diff / (60 * 1000)
    val hours = diff / (60 * 60 * 1000)
    val days = diff / (24 * 60 * 60 * 1000)

    return when {
        minutes < 5 -> "2m"
        minutes < 60 -> "${minutes}m"
        hours < 24 -> "${hours}h"
        days < 2 -> "Tue"
        days < 4 -> "Mon"
        else -> "${days}d"
    }
}
