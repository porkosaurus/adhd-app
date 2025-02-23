package com.example.myapplication

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// Data class representing a task. The isChecked field is wrapped in a mutable state.
data class Task(val name: String, val isChecked: androidx.compose.runtime.MutableState<Boolean> = mutableStateOf(false))

class MainActivity : ComponentActivity() {
    private lateinit var notificationHelper: MyNotificationHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Notification Helper
        notificationHelper = MyNotificationHelper(this)

        setContent {
            MainApp()
        }
    }
}

fun buzz(context: Context, duration: Long = 100L, amplitude: Int = VibrationEffect.DEFAULT_AMPLITUDE) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    vibrator?.let {
        if (it.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                it.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                it.vibrate(duration)
            }
        }
    }
}

@Composable
fun MainApp() {
    var isLoggedIn by remember { mutableStateOf(false) }
    var isSigningUp by remember { mutableStateOf(false) }


    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    if (isLoggedIn) {
        MainPageUI(isTaskWindowVisible = false)
    } else if (isSigningUp) {
        SignUpScreen(onSignUpSuccess = {
            isSigningUp = false
            isLoggedIn = true
        }, onBackToLogin = {
            isSigningUp = false
        })
    } else {
        LoginSignUpScreen(onLoginSuccess = { isLoggedIn = true }, onNavigateToSignUp = { isSigningUp = true })
    }
}
}

@Composable
fun BuzzIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    IconButton(
        onClick = {
            // Trigger the buzz (vibration) effect
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            vibrator?.let {
                if (it.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        it.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        it.vibrate(100)
                    }
                }
            }
            // Then execute the provided onClick action
            onClick()
        },
        modifier = modifier
    ) {
        content()
    }
}


@Composable
fun MainPageUI(isTaskWindowVisible: Boolean) {
    val isWindowVisible = remember { mutableStateOf(isTaskWindowVisible) }
    val tasksState = remember { mutableStateListOf<Task>() }
    val level = remember { mutableStateOf(1) }
    val points = remember { mutableStateOf(0) }  // <-- New points state
    val showAddTaskDialog = remember { mutableStateOf(false) }

    // New state for tracking the current day – starting January 1, 2023.
    val currentCalendar = remember {
        Calendar.getInstance().apply {
            set(2023, Calendar.JANUARY, 1)
        }
    }
    // Formatter for displaying the date (e.g., "January 1")
    val dateFormat = SimpleDateFormat("MMMM d", Locale.getDefault())

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

    // Controls the scrapbook task window
    val isScrapbookVisible = remember { mutableStateOf(isTaskWindowVisible) }

    val flowerIndex = remember { mutableStateOf(0) }

    val flowers = listOf(
        R.drawable.flower1,
        R.drawable.flower2,
        R.drawable.flower3,
        R.drawable.flower4,
        R.drawable.flower5
    )






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

            // Progress Bar – green segments based on points accumulated
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 80.dp)
                    .fillMaxWidth(0.35f)
                    .offset(y = if (isWindowVisible.value) (-200).dp else 0.dp)
                    .height(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (i in 0 until 5) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(
                                color = if (i < points.value) Color.Green else Color.Gray,
                                shape = RoundedCornerShape(8.dp)
                            )
                    )
                    if (i < 4) {
                        Spacer(
                            modifier = Modifier
                                .width(4.dp)
                                .fillMaxHeight()
                                .background(Color.Black)
                        )
                    }
                }
            }

            // Text Below Progress Bar showing remaining tasks for next level
            Text(
                text = "Only ${5 - points.value} tasks until your next level!",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 120.dp)
                    .offset(y = if (isWindowVisible.value) (-200).dp else 0.dp)
            )


            // Task Icon (Top Right) - toggles the task window
            BuzzIconButton(
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
            if(!isWindowVisible.value){
            BuzzIconButton(
                onClick = { isScrapbookVisible.value = !isScrapbookVisible.value },
                modifier = Modifier
                    .size(110.dp)
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
        }

        // Sliding Task Window with date display, task list, plus button, and submit button
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
                // Display current day at the top
                Text(
                    text = "Date: ${dateFormat.format(currentCalendar.time)}",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Render each task row
                tasksState.forEachIndexed { index, task ->
                    TaskRow(
                        taskName = task.name,
                        isChecked = task.isChecked.value,
                        onToggle = { task.isChecked.value = !task.isChecked.value },
                        onDelete = { tasksState.remove(task) }
                    )
                }

                // Plus Button to add new tasks (visible only when less than 3 tasks exist)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    if (tasksState.size < 6) {
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

                // Submit Button – advances the day, adds points, updates level (and image), and resets the checklist
                Button(
                    onClick = {
                        // Count checked tasks and add that many points
                        val completedCount = tasksState.count { it.isChecked.value }
                        points.value += completedCount

                        // Check if enough points have been earned (5 points needed per level)
                        if (points.value >= 5) {
                            val levelsGained = points.value / 5  // Calculate how many levels to increase
                            level.value += levelsGained         // Update level which triggers the flower image update
                            points.value %= 5

                            // Initialize notificationHelper with the current context
                            val notificationHelper = MyNotificationHelper(context)  // <-- Pass the valid context

                            // Call to show notification
                            notificationHelper.showNotification()
                            // Retain any leftover points
                        }

                        Log.d("MainPageUI", "Completed tasks: $completedCount, Points: ${points.value}, Level: ${level.value}")

                        // Reset all task checkboxes
                        tasksState.forEach { it.isChecked.value = false }

                        // Advance the day
                        currentCalendar.add(Calendar.DAY_OF_MONTH, 1)
                    },
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 16.dp)
                ) {
                    Text(text = "Submit")
                }


            }
        }
        // Sliding Task Window for ScrapBook
        AnimatedVisibility(
            visible = isScrapbookVisible.value,
            enter = slideInVertically(
                animationSpec = tween(durationMillis = 300),
                initialOffsetY = { fullHeight -> fullHeight }
            ),
            exit = slideOutVertically(
                animationSpec = tween(durationMillis = 300),
                targetOffsetY = { fullHeight -> fullHeight }
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.scrapbook),
                    contentDescription = "Scrapbook",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(600.dp)
                )
                Image (
                    painter = painterResource(id = flowers[(flowerIndex.value - 1 + flowers.size) % flowers.size]),
                    contentDescription = "Left Flower",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.CenterStart)
                        .offset(x = 30.dp, y = (-115).dp)

                )
                Image(
                    painter = painterResource(id = flowers[flowerIndex.value]),
                    contentDescription = "Right Flower",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.CenterEnd)
                        .offset(x = (-30).dp,y = (-115).dp)

                )
                Box(modifier = Modifier.fillMaxSize()) {

                    BuzzIconButton(
                        onClick = {
                            // Action for IconButton click (you can add functionality here)
                            flowerIndex.value = (flowerIndex.value + 1) % flowers.size
                        },
                        modifier = Modifier
                            .size(100.dp)
                            .padding(top = 16.dp)
                            .align(Alignment.BottomEnd)
                        // Space between image and IconButton
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.arrow),
                            contentDescription = "Add Task",
                            modifier = Modifier

                        )
                    }

                    BuzzIconButton(
                        onClick = {
                         flowerIndex.value = (flowerIndex.value - 1 + flowers.size) % flowers.size
                        },
                        modifier = Modifier
                            .size(100.dp)
                            .padding(top = 16.dp)
                            .align(Alignment.BottomStart)

                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.arrow),
                            contentDescription = "Add Task",
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer(
                                    scaleX = -1f,
                                    scaleY = 1f

                                )
                        )


                        //Icon to return to menu:

                    }

                    BuzzIconButton(
                        onClick = {isScrapbookVisible.value = !isScrapbookVisible.value},
                        modifier = Modifier.size(100.dp)
                            .padding(top = 16.dp)
                            .align(Alignment.BottomCenter)
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
                                painter = painterResource(id = R.drawable.home),
                                contentDescription = "Scrapbook",
                                modifier = Modifier
                                    .size(42.dp)
                                    .align(Alignment.Center),
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                        }


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

                BuzzIconButton(onClick = {
                    if (newTaskName.isNotBlank() && tasksState.size < 6) {
                        tasksState.add(Task(newTaskName))
                    }
                    showAddTaskDialog.value = false
                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                BuzzIconButton(onClick = {
                    showAddTaskDialog.value = false
                }) {
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
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            // Draw a thin black bottom border for the entire task row
            .drawBehind {
                val strokeWidth = 1.dp.toPx()
                drawLine(
                    color = Color.Black,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = strokeWidth
                )
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Increase the font size for the task name
        Text(
            text = taskName,
            fontSize = 40.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )

        if (isChecked) {
            BuzzIconButton(
                onClick = { onToggle() },
                modifier = Modifier.size(48.dp)
            ) {
                Image(
                    imageVector = Check_box,
                    contentDescription = "Checked",
                    modifier = Modifier.fillMaxSize(),
                    colorFilter = ColorFilter.tint(Color.Black)
                )
            }
        } else {
            BuzzIconButton(
                onClick = { onToggle() },
                modifier = Modifier.size(48.dp)
            ) {
                Image(
                    imageVector = Check_box_outline_blank,
                    contentDescription = "Unchecked",
                    modifier = Modifier.fillMaxSize(),
                    colorFilter = ColorFilter.tint(Color.Black)
                )
            }
        }


        // Delete Button (Trash Icon)
        BuzzIconButton(
            onClick = onDelete, // Call the onDelete lambda when clicked
            modifier = Modifier.size(48.dp)
        ) {
            Image(
                imageVector = Trash,
                contentDescription = "Delete",
                modifier = Modifier.size(28.dp),
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