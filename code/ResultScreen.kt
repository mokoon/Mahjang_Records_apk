package com.example.umacaculator

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlin.math.round
import androidx.compose.ui.unit.sp

@Composable
fun ResultScreen(
    navController: NavController,
    gameNumber: String,
    startTime: String,
    endTime: String,
    playerNames: List<String>,
    playerScores: List<String>
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    // 점수를 Int로 변환
    val playerScoresInt = playerScores.map { it.toIntOrNull() ?: 0 }

    // 점수 기준으로 1~4등 정렬
    val sortedPlayers = playerNames.zip(playerScoresInt)
        .sortedByDescending { it.second }

    val formattedScores = sortedPlayers.mapIndexed { index, (name, score) ->
        val uma = (score - 25000) / 1000.0
        "${index + 1}. $name $score ${if (uma >= 0) "+" else ""}${round(uma * 10) / 10}"
    }

    val resultText = """
        $gameNumber
        $startTime~$endTime
        ${formattedScores[0]}
        ${formattedScores[1]}
        ${formattedScores[2]}
        ${formattedScores[3]}
    """.trimIndent()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(resultText)
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(resultText))
            Toast.makeText(context, "기록이 복사되었습니다", Toast.LENGTH_SHORT).show()
        },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("기록 양식 텍스트 복사", fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                navController.navigate("main/${gameNumber}/${startTime}/${endTime}/${
                    playerNames.joinToString(",")
                }/${
                    playerScores.joinToString(",")
                }") {
                    popUpTo("main") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("초기화면으로 돌아가기", fontSize = 20.sp)
        }

    }
}
