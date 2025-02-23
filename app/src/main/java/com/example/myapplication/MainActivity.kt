package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.R
import kotlinx.coroutines.delay

// Data class representing a task. The isChecked field is wrapped in a mutable state.
data class Task(val name: String, val isChecked: androidx.compose.runtime.MutableState<Boolean> = mutableStateOf(false))

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainPageUI(isTaskWindowVisible = false)
        }
    }
}

@Composable
fun MainPageUI(isTaskWindowVisible: Boolean) {
    // Controls the sliding task window
    val isWindowVisible = remember { mutableStateOf(isTaskWindowVisible) }
    // Dynamic list of tasks starting empty
    val tasksState = remember { mutableStateListOf<Task>() }
    // Level state (starting at 1)
    val level = remember { mutableStateOf(1) }
    // Controls visibility of the Add Task popup
    val showAddTaskDialog = remember { mutableStateOf(false) }

    // Get the current context to access resources
    val context = LocalContext.current

    // Recalculate the flower resource ID whenever the level changes
    val flowerResourceId = remember(level.value) {
        // Build the resource name dynamically based on level value
        val flowerResourceName = "stage${level.value}_lily"
        // Look up the drawable resource id
        context.resources.getIdentifier(
            flowerResourceName,
            "drawable",
            context.packageName
        )
    }

    // Fallback to a default image if the resource is not found (e.g., stage1_lily)
    val flowerPainter = if (flowerResourceId != 0) {
        painterResource(id = flowerResourceId)
    } else {
        painterResource(id = R.drawable.stage1_lily)
    }

    // Watch for all tasks to be completed; if so, level up and reset tasks.
    LaunchedEffect(tasksState.count { it.isChecked.value }) {
        val completedCount = tasksState.count { it.isChecked.value }
        Log.d("MainPageUI", "Completed tasks count: $completedCount")
        if (tasksState.isNotEmpty() && tasksState.all { it.isChecked.value }) {
            delay(300)
            level.value += 1  // Increase level
            Log.d("MainPageUI", "Level updated to: ${level.value}")
            tasksState.forEach { it.isChecked.value = false }  // Reset checkboxes
        }
    }

    // Background Image
    Image(
        painter = painterResource(id = R.drawable.background),
        contentDescription = "Background",
        modifier = Modifier
            .fillMaxSize()
            .offset(y = if (isWindowVisible.value) (-420).dp else 0.dp),
        contentScale = ContentScale.Crop
    )

    // Main Content
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            // Updated Flower Image using the dynamic flowerPainter
            Image(
                painter = flowerPainter,
                contentDescription = "Flower",
                modifier = Modifier
                    .size(450.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = if (isWindowVisible.value) 12.dp else (-12).dp),
                contentScale = ContentScale.Crop
            )

            // Level Text – dynamically shows the current level
            Text(
                text = "Level ${level.value}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = if (isWindowVisible.value) (-200).dp else 0.dp)
                    .padding(top = 32.dp)
            )

            // Progress Bar – green segments based on tasks completed
            val completedCount = tasksState.count { it.isChecked.value }
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 80.dp)
                    .fillMaxWidth(0.35f)
                    .offset(y = if (isWindowVisible.value) (-200).dp else 0.dp)
                    .height(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (i in 0 until 3) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(
                                color = if (i < completedCount) Color.Green else Color.Gray,
                                shape = RoundedCornerShape(8.dp)
                            )
                    )
                    if (i < 2) {
                        Spacer(
                            modifier = Modifier
                                .width(4.dp)
                                .fillMaxHeight()
                                .background(Color.Black)
                        )
                    }
                }
            }

            // Text Below Progress Bar
            Text(
                text = "Only 9 days until your next level up!",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 120.dp)
                    .offset(y = if (isWindowVisible.value) (-200).dp else 0.dp)
            )

            // Task Icon (Top Right) - toggles the task window
            IconButton(
                onClick = { isWindowVisible.value = !isWindowVisible.value },
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .offset(y = 25.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(500.dp)
                        .background(
                            Color(0x71412C13),
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Image(
                        imageVector = Lists,
                        contentDescription = "Tasks",
                        modifier = Modifier
                            .size(36.dp)
                            .align(Alignment.Center),
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                }
            }

            // Scrapbook Icon (Bottom Right)
            IconButton(
                onClick = { /* Navigate to Scrapbook Screen */ },
                modifier = Modifier
                    .size(130.dp)
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .offset(y = (-20).dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(500.dp)
                        .background(
                            Color(0xFF71412C),
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Image(
                        imageVector = Book_2,
                        contentDescription = "Scrapbook",
                        modifier = Modifier
                            .size(42.dp)
                            .align(Alignment.Center),
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                }
            }
        }

        // Sliding Task Window remains unchanged...
        AnimatedVisibility(
            visible = isWindowVisible.value,
            enter = slideInVertically(
                animationSpec = tween(durationMillis = 300),
                initialOffsetY = { fullHeight -> fullHeight }
            ),
            exit = slideOutVertically(
                animationSpec = tween(durationMillis = 300),
                targetOffsetY = { fullHeight -> fullHeight }
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f)
                    .background(Color(0xFFF5F5DC))
                    .padding(16.dp)
            ) {
                tasksState.forEachIndexed { index, task ->
                    TaskRow(
                        taskName = task.name,
                        isChecked = task.isChecked.value,
                        onToggle = { task.isChecked.value = !task.isChecked.value },
                        onDelete = { tasksState.remove(task) } // Remove the task from the list
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    if (tasksState.size < 3) {
                        Image(
                            imageVector = Add,
                            contentDescription = "Add",
                            modifier = Modifier
                                .size(48.dp)
                                .clickable { showAddTaskDialog.value = true },
                            colorFilter = ColorFilter.tint(Color.Black)
                        )
                    }
                }
            }
        }
    }

    // Add Task Popup Dialog remains unchanged...
    if (showAddTaskDialog.value) {
        var newTaskName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddTaskDialog.value = false },
            title = { Text("Add Task") },
            text = {
                OutlinedTextField(
                    value = newTaskName,
                    onValueChange = { newTaskName = it },
                    label = { Text("Task Name") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (newTaskName.isNotBlank() && tasksState.size < 3) {
                        tasksState.add(Task(newTaskName))
                    }
                    showAddTaskDialog.value = false
                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                Button(onClick = { showAddTaskDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun TaskRow(
    taskName: String,
    isChecked: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit // Lambda to handle task deletion
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Task Name
        Text(
            text = taskName,
            fontSize = 32.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )

        // Checkbox
        if (isChecked) {
            Image(
                imageVector = Check_box,
                contentDescription = "Checked",
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onToggle() },
                colorFilter = ColorFilter.tint(Color.Black)
            )
        } else {
            Image(
                imageVector = Check_box_outline_blank,
                contentDescription = "Unchecked",
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onToggle() },
                colorFilter = ColorFilter.tint(Color.Black)
            )
        }

        // Delete Button (Trash Icon)
        IconButton(
            onClick = onDelete, // Call the onDelete lambda when clicked
            modifier = Modifier.size(40.dp)
        ) {
            Image(
                imageVector = Trash,
                contentDescription = "Delete",
                modifier = Modifier.size(24.dp),
                colorFilter = ColorFilter.tint(Color.Black)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainPageUI() {
    MainPageUI(isTaskWindowVisible = false)
}

// Icon rows

public val Lists: ImageVector
    get() {
        if (_Lists != null) {
            return _Lists!!
        }
        _Lists = ImageVector.Builder(
            name = "Lists",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(80f, 800f)
                verticalLineToRelative(-160f)
                horizontalLineToRelative(160f)
                verticalLineToRelative(160f)
                close()
                moveToRelative(240f, 0f)
                verticalLineToRelative(-160f)
                horizontalLineToRelative(560f)
                verticalLineToRelative(160f)
                close()
                moveTo(80f, 560f)
                verticalLineToRelative(-160f)
                horizontalLineToRelative(160f)
                verticalLineToRelative(160f)
                close()
                moveToRelative(240f, 0f)
                verticalLineToRelative(-160f)
                horizontalLineToRelative(560f)
                verticalLineToRelative(160f)
                close()
                moveTo(80f, 320f)
                verticalLineToRelative(-160f)
                horizontalLineToRelative(160f)
                verticalLineToRelative(160f)
                close()
                moveToRelative(240f, 0f)
                verticalLineToRelative(-160f)
                horizontalLineToRelative(560f)
                verticalLineToRelative(160f)
                close()
            }
        }.build()
        return _Lists!!
    }

private var _Lists: ImageVector? = null



public val Check_box_outline_blank: ImageVector
    get() {
        if (_Check_box_outline_blank != null) {
            return _Check_box_outline_blank!!
        }
        _Check_box_outline_blank = ImageVector.Builder(
            name = "Check_box_outline_blank",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(200f, 840f)
                quadToRelative(-33f, 0f, -56.5f, -23.5f)
                reflectiveQuadTo(120f, 760f)
                verticalLineToRelative(-560f)
                quadToRelative(0f, -33f, 23.5f, -56.5f)
                reflectiveQuadTo(200f, 120f)
                horizontalLineToRelative(560f)
                quadToRelative(33f, 0f, 56.5f, 23.5f)
                reflectiveQuadTo(840f, 200f)
                verticalLineToRelative(560f)
                quadToRelative(0f, 33f, -23.5f, 56.5f)
                reflectiveQuadTo(760f, 840f)
                close()
                moveToRelative(0f, -80f)
                horizontalLineToRelative(560f)
                verticalLineToRelative(-560f)
                horizontalLineTo(200f)
                close()
            }
        }.build()
        return _Check_box_outline_blank!!
    }

private var _Check_box_outline_blank: ImageVector? = null



public val Book_2: ImageVector
    get() {
        if (_Book_2 != null) {
            return _Book_2!!
        }
        _Book_2 = ImageVector.Builder(
            name = "Book_2",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(300f, 880f)
                quadToRelative(-58f, 0f, -99f, -41f)
                reflectiveQuadToRelative(-41f, -99f)
                verticalLineToRelative(-520f)
                quadToRelative(0f, -58f, 41f, -99f)
                reflectiveQuadToRelative(99f, -41f)
                horizontalLineToRelative(500f)
                verticalLineToRelative(600f)
                quadToRelative(-25f, 0f, -42.5f, 17.5f)
                reflectiveQuadTo(740f, 740f)
                reflectiveQuadToRelative(17.5f, 42.5f)
                reflectiveQuadTo(800f, 800f)
                verticalLineToRelative(80f)
                close()
                moveToRelative(-60f, -267f)
                quadToRelative(14f, -7f, 29f, -10f)
                reflectiveQuadToRelative(31f, -3f)
                horizontalLineToRelative(20f)
                verticalLineToRelative(-440f)
                horizontalLineToRelative(-20f)
                quadToRelative(-25f, 0f, -42.5f, 17.5f)
                reflectiveQuadTo(240f, 220f)
                close()
                moveToRelative(160f, -13f)
                horizontalLineToRelative(320f)
                verticalLineToRelative(-440f)
                horizontalLineTo(400f)
                close()
                moveToRelative(-160f, 13f)
                verticalLineToRelative(-453f)
                close()
                moveToRelative(60f, 187f)
                horizontalLineToRelative(373f)
                quadToRelative(-6f, -14f, -9.5f, -28.5f)
                reflectiveQuadTo(660f, 740f)
                quadToRelative(0f, -16f, 3f, -31f)
                reflectiveQuadToRelative(10f, -29f)
                horizontalLineTo(300f)
                quadToRelative(-26f, 0f, -43f, 17.5f)
                reflectiveQuadTo(240f, 740f)
                quadToRelative(0f, 26f, 17f, 43f)
                reflectiveQuadToRelative(43f, 17f)
            }
        }.build()
        return _Book_2!!
    }

private var _Book_2: ImageVector? = null


public val Trash: ImageVector
    get() {
        if (_Trash != null) {
            return _Trash!!
        }
        _Trash = ImageVector.Builder(
            name = "Trash",
            defaultWidth = 16.dp,
            defaultHeight = 16.dp,
            viewportWidth = 16f,
            viewportHeight = 16f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(5.5f, 5.5f)
                arcTo(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 6f, 6f)
                verticalLineToRelative(6f)
                arcToRelative(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = true, -1f, 0f)
                verticalLineTo(6f)
                arcToRelative(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0.5f, -0.5f)
                moveToRelative(2.5f, 0f)
                arcToRelative(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0.5f, 0.5f)
                verticalLineToRelative(6f)
                arcToRelative(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = true, -1f, 0f)
                verticalLineTo(6f)
                arcToRelative(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0.5f, -0.5f)
                moveToRelative(3f, 0.5f)
                arcToRelative(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, -1f, 0f)
                verticalLineToRelative(6f)
                arcToRelative(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 1f, 0f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF000000)),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(14.5f, 3f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, -1f, 1f)
                horizontalLineTo(13f)
                verticalLineToRelative(9f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2f, 2f)
                horizontalLineTo(5f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2f, -2f)
                verticalLineTo(4f)
                horizontalLineToRelative(-0.5f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, -1f, -1f)
                verticalLineTo(2f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 1f, -1f)
                horizontalLineTo(6f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 1f, -1f)
                horizontalLineToRelative(2f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 1f, 1f)
                horizontalLineToRelative(3.5f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 1f, 1f)
                close()
                moveTo(4.118f, 4f)
                lineTo(4f, 4.059f)
                verticalLineTo(13f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 1f, 1f)
                horizontalLineToRelative(6f)
                arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 1f, -1f)
                verticalLineTo(4.059f)
                lineTo(11.882f, 4f)
                close()
                moveTo(2.5f, 3f)
                horizontalLineToRelative(11f)
                verticalLineTo(2f)
                horizontalLineToRelative(-11f)
                close()
            }
        }.build()
        return _Trash!!
    }

private var _Trash: ImageVector? = null


public val Add: ImageVector
    get() {
        if (_Add != null) {
            return _Add!!
        }
        _Add = ImageVector.Builder(
            name = "Add",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(440f, 520f)
                horizontalLineTo(200f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(240f)
                verticalLineToRelative(-240f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(240f)
                horizontalLineToRelative(240f)
                verticalLineToRelative(80f)
                horizontalLineTo(520f)
                verticalLineToRelative(240f)
                horizontalLineToRelative(-80f)
                close()
            }
        }.build()
        return _Add!!
    }

private var _Add: ImageVector? = null




public val Check_box: ImageVector
    get() {
        if (_Check_box != null) {
            return _Check_box!!
        }
        _Check_box = ImageVector.Builder(
            name = "Check_box",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(424f, 648f)
                lineToRelative(282f, -282f)
                lineToRelative(-56f, -56f)
                lineToRelative(-226f, 226f)
                lineToRelative(-114f, -114f)
                lineToRelative(-56f, 56f)
                close()
                moveTo(200f, 840f)
                quadToRelative(-33f, 0f, -56.5f, -23.5f)
                reflectiveQuadTo(120f, 760f)
                verticalLineToRelative(-560f)
                quadToRelative(0f, -33f, 23.5f, -56.5f)
                reflectiveQuadTo(200f, 120f)
                horizontalLineToRelative(560f)
                quadToRelative(33f, 0f, 56.5f, 23.5f)
                reflectiveQuadTo(840f, 200f)
                verticalLineToRelative(560f)
                quadToRelative(0f, 33f, -23.5f, 56.5f)
                reflectiveQuadTo(760f, 840f)
                close()
                moveToRelative(0f, -80f)
                horizontalLineToRelative(560f)
                verticalLineToRelative(-560f)
                horizontalLineTo(200f)
                close()
                moveToRelative(0f, -560f)
                verticalLineToRelative(560f)
                close()
            }
        }.build()
        return _Check_box!!
    }

private var _Check_box: ImageVector? = null