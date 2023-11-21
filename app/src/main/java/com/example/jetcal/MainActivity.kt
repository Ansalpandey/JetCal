package com.example.jetcal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetcal.ui.theme.Cyan
import com.example.jetcal.ui.theme.JetCalTheme
import com.example.jetcal.ui.theme.Red

class MainActivity : ComponentActivity() {
  private val viewModel: AppViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      JetCalTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.secondary,
        ) {
          val calculatorButtons = remember {
            mutableStateListOf(
                CalculatorButton("AC", CalculatorButtonType.Reset),
                CalculatorButton("AC", CalculatorButtonType.Reset),
                CalculatorButton("AC", CalculatorButtonType.Reset),
                CalculatorButton("/", CalculatorButtonType.Action),
                CalculatorButton("7", CalculatorButtonType.Normal),
                CalculatorButton("8", CalculatorButtonType.Normal),
                CalculatorButton("9", CalculatorButtonType.Normal),
                CalculatorButton("x", CalculatorButtonType.Action),
                CalculatorButton("4", CalculatorButtonType.Normal),
                CalculatorButton("5", CalculatorButtonType.Normal),
                CalculatorButton("6", CalculatorButtonType.Normal),
                CalculatorButton("-", CalculatorButtonType.Action),
                CalculatorButton("1", CalculatorButtonType.Normal),
                CalculatorButton("2", CalculatorButtonType.Normal),
                CalculatorButton("3", CalculatorButtonType.Normal),
                CalculatorButton("+", CalculatorButtonType.Action),
                CalculatorButton(
                    icon = Icons.Outlined.Refresh,
                    type = CalculatorButtonType.Reset,
                ),
                CalculatorButton("0", CalculatorButtonType.Normal),
                CalculatorButton(".", CalculatorButtonType.Normal),
                CalculatorButton("=", CalculatorButtonType.Action),
            )
          }

          val (input, setInput) = remember { mutableStateOf<String?>(null) }

          val (uiText, setUiText) = remember { mutableStateOf<String>("0") }

          LaunchedEffect(uiText) {
            if (uiText.startsWith("0") && uiText != "0") {
              setUiText(uiText.substring(1))
            }
          }

          Box(
              modifier = Modifier.fillMaxSize(),
              contentAlignment = Alignment.BottomCenter,
          ) {
            Column {
              Text(
                  modifier = Modifier.padding(horizontal = 12.dp),
                  text = uiText,
                  fontSize = 44.sp,
                  fontWeight = FontWeight.Bold,
                  color = White)
              Spacer(modifier = Modifier.height(32.dp))
              LazyVerticalGrid(
                  modifier =
                      Modifier.clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                          .background(MaterialTheme.colorScheme.primary)
                          .padding(8.dp),
                  columns = GridCells.Fixed(4),
                  verticalArrangement = Arrangement.spacedBy(8.dp),
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                  contentPadding = PaddingValues(16.dp),
              ) {
                items(calculatorButtons) {
                  calButton(
                      button = it,
                      onClick = {
                        when (it.type) {
                          CalculatorButtonType.Normal -> {
                            runCatching { setUiText(uiText.toInt().toString() + it.text) }
                                .onFailure { throwable -> setUiText(uiText + it.text) }

                            setInput((input ?: "") + it.text)

                            if (viewModel.mAction.value?.isNotEmpty() == true) {
                              if (viewModel.secondNumber.value == null) {
                                viewModel.setSecondNum(it.text!!.toDouble())
                              } else {
                                if (viewModel.secondNumber.value.toString().split(".")[1] == "0") {
                                  viewModel.setSecondNum(
                                      (viewModel.secondNumber.value.toString().split(".").first() +
                                              it.text!!)
                                          .toDouble())
                                } else {
                                  viewModel.setSecondNum(
                                      (viewModel.secondNumber.value.toString() + it.text!!)
                                          .toDouble())
                                }
                              }
                            }
                          }
                          CalculatorButtonType.Action -> {

                            if (it.text == "=") {
                              val result = viewModel.getResult()
                              setUiText(result.toString())
                              setInput(null)
                              viewModel.resetAll()
                            } else {
                              runCatching { setUiText(uiText.toInt().toString() + it.text) }
                                  .onFailure { throwable -> setUiText(uiText + it.text) }
                              if (input != null) {

                                if (viewModel.firstNumber.value == null) {
                                  viewModel.setFirstNum(input.toDouble())
                                } else {

                                  viewModel.setSecondNum(input.toDouble())
                                }
                                viewModel.setAction(it.text!!)
                              }
                            }
                          }
                          CalculatorButtonType.Reset -> {
                            setUiText("")
                            setInput(null)
                            viewModel.resetAll()
                          }
                        }
                      },
                  )
                }
              }
            }
          }

          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier =
                    Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        .clip(
                            RoundedCornerShape(8.dp),
                        ),
            ) {
              Icon(
                  modifier = Modifier.size(21.dp),
                  painter = painterResource(id = R.drawable.light_mode),
                  contentDescription = null,
                  tint = Color.White,
              )
              Icon(
                  modifier = Modifier.size(21.dp),
                  painter = painterResource(id = R.drawable.outline_dark_mode),
                  contentDescription = null,
                  tint = Color.White,
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun calButton(button: CalculatorButton, onClick: () -> Unit) {
  Box(
      modifier =
          Modifier.clip(RoundedCornerShape(16.dp))
              .background(MaterialTheme.colorScheme.secondary)
              .fillMaxHeight()
              .aspectRatio(1f)
              .clickable { onClick() },
      contentAlignment = Alignment.Center,
  ) {
    val contentColor =
        when (button.type) {
          CalculatorButtonType.Normal -> Color.White
          CalculatorButtonType.Action -> Red
          else -> Cyan
        }
    if (button.text != null) {
      Text(
          button.text,
          color = contentColor,
          fontWeight = FontWeight.Bold,
          fontSize =
              if (button.type == CalculatorButtonType.Action) {
                24.sp
              } else {
                21.sp
              },
      )
    } else {
      Icon(
          modifier = Modifier.size(32.dp),
          imageVector = button.icon!!,
          contentDescription = null,
          tint = contentColor,
      )
    }
  }
}

data class CalculatorButton(
    val text: String? = null,
    val type: CalculatorButtonType,
    val icon: ImageVector? = null,
)

enum class CalculatorButtonType {
  Normal,
  Action,
  Reset
}
