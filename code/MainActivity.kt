package com.example.umacaculator

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.ui.text.input.ImeAction
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.umacaculator.ResultScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController, startDestination = "main") {
                composable(
                    "main/{gameNumber}/{startTime}/{endTime}/{playerNames}/{playerScores}",
                    arguments = listOf(
                        navArgument("gameNumber") { type = NavType.StringType; defaultValue = "" },
                        navArgument("startTime") { type = NavType.StringType; defaultValue = "" },
                        navArgument("endTime") { type = NavType.StringType; defaultValue = "" },
                        navArgument("playerNames") { type = NavType.StringType; defaultValue = ",,," },
                        navArgument("playerScores") { type = NavType.StringType; defaultValue = ",,," }
                    )
                ) { backStackEntry ->
                    val gameNumber = backStackEntry.arguments?.getString("gameNumber") ?: ""
                    val startTime = backStackEntry.arguments?.getString("startTime") ?: ""
                    val endTime = backStackEntry.arguments?.getString("endTime") ?: ""
                    val playerNames = backStackEntry.arguments?.getString("playerNames")?.split(",") ?: listOf("", "", "", "")
                    val playerScores = backStackEntry.arguments?.getString("playerScores")?.split(",") ?: listOf("", "", "", "")

                    MainScreen(
                        navController = navController,
                        initialGameNumber = gameNumber,
                        initialStartTime = startTime,
                        initialEndTime = endTime,
                        initialPlayerNames = playerNames,
                        initialPlayerScores = playerScores
                    )
                }

                composable("main") {
                    MainScreen(navController = navController)
                }

                composable(
                    "result/{gameNumber}/{startTime}/{endTime}/{playerNames}/{playerScores}",
                    arguments = listOf(
                        navArgument("gameNumber") { type = NavType.StringType },
                        navArgument("startTime") { type = NavType.StringType },
                        navArgument("endTime") { type = NavType.StringType },
                        navArgument("playerNames") { type = NavType.StringType },
                        navArgument("playerScores") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val gameNumber = backStackEntry.arguments?.getString("gameNumber") ?: ""
                    val startTime = backStackEntry.arguments?.getString("startTime") ?: ""
                    val endTime = backStackEntry.arguments?.getString("endTime") ?: ""
                    val playerNames = backStackEntry.arguments?.getString("playerNames")?.split(",") ?: listOf()
                    val playerScores = backStackEntry.arguments?.getString("playerScores")?.split(",") ?: listOf()

                    ResultScreen(
                        navController = navController,
                        gameNumber = gameNumber,
                        startTime = startTime,
                        endTime = endTime,
                        playerNames = playerNames,
                        playerScores = playerScores
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    navController: NavController,
    initialGameNumber: String = "",
    initialStartTime: String = "",
    initialEndTime: String = "",
    initialPlayerNames: List<String> = List(4) { "" },
    initialPlayerScores: List<String> = List(4) { "" }
) {
    var gameNumber by remember { mutableStateOf(initialGameNumber) }
    var startTime by remember { mutableStateOf(initialStartTime) }
    var endTime by remember { mutableStateOf(initialEndTime) }
    var playerNames by remember { mutableStateOf(initialPlayerNames) }
    var playerScores by remember { mutableStateOf(initialPlayerScores) }
    var errorMessage by remember { mutableStateOf("") }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Box(modifier = Modifier.fillMaxWidth()) {
            NumberInputField(value = gameNumber, onValueChange = { gameNumber = it }, placeholder = "게임 번호")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.weight(1f)) {
                TimeInputField(value = startTime, onValueChange = { startTime = it }, placeholder = "시작 시간")
            }
            Text("~", color = Color.Black, fontSize = 18.sp, modifier = Modifier.padding(horizontal = 8.dp))
            Box(modifier = Modifier.weight(1f)) {
                TimeInputField(value = endTime, onValueChange = { endTime = it }, placeholder = "종료 시간")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        repeat(4) { index ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    PlayerNameInputField(
                        value = playerNames[index],
                        onValueChange = { newValue ->
                            playerNames = playerNames.toMutableList().also { it[index] = newValue }
                        },
                        placeholder = "플레이어 ${index + 1}"
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.weight(1.5f)) {
                    NumberInputField(
                        value = playerScores[index],
                        onValueChange = { newValue ->
                            playerScores = playerScores.toMutableList().also { it[index] = newValue }
                        },
                        placeholder = "점수"
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                errorMessage = ""
                if (gameNumber.isEmpty() || startTime.isEmpty() || endTime.isEmpty() ||
                    playerNames.any { it.isEmpty() } || playerScores.any { it.isEmpty() }) {
                    errorMessage = "모든 필드를 입력해주세요."
                    return@Button
                }

                val totalScore = playerScores.sumOf { it.toIntOrNull() ?: 0 }
                if (totalScore != 100000) {
                    val difference = totalScore - 100000
                    errorMessage = if (difference > 0) {
                        "점수가 ${difference}점 초과되었습니다."
                    } else {
                        "점수가 ${-difference}점 부족합니다."
                    }
                    return@Button
                }

                navController.navigate(
                    "result/${gameNumber}/${startTime}/${endTime}/${
                        playerNames.joinToString(",")
                    }/${
                        playerScores.joinToString(",")  // 그냥 문자열 그대로 전달
                    }"
                )


            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("계산하기", fontSize = 20.sp)
        }
    }
}

@Composable
fun NumberInputField(value: String, onValueChange: (String) -> Unit, placeholder: String) {
    TextField(
        value = value,
        onValueChange = { newValue ->
            if (newValue.all { it.isDigit() }) {
                onValueChange(newValue)
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        )
        ,
        textStyle = TextStyle(fontSize = 18.sp, color = Color.Black),
        singleLine = true, // 한 줄 입력 설정
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        placeholder = { Text(text = placeholder, fontSize = 18.sp, color = Color.Gray) }
    )
}


@Composable
fun TimeInputField(value: String, onValueChange: (String) -> Unit, placeholder: String) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle(fontSize = 18.sp, color = Color.Black),
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        decorationBox = { innerTextField ->
            Box(modifier = Modifier.fillMaxWidth()) {
                if (value.isEmpty()) {
                    Text(text = placeholder, fontSize = 18.sp, color = Color.Gray)
                }
                innerTextField()
            }
        }
    )
}


@Composable
fun PlayerNameInputField(value: String, onValueChange: (String) -> Unit, placeholder: String) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle(fontSize = 18.sp, color = Color.Black),
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 8.dp),
        decorationBox = { innerTextField ->
            Box(modifier = Modifier.fillMaxWidth()) {
                if (value.isEmpty()) {
                    Text(text = placeholder, fontSize = 18.sp, color = Color.Gray)
                }
                innerTextField()
            }
        }
    )
}
