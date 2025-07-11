package composable

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import store.ActiveScreenState
import store.action.AppActions

@Composable
fun notStartedScreen(
    state: ActiveScreenState.NotStartedScreen,
    startButtonClick: (AppActions.NotStartedScreen.StartProxyClick) -> Unit
) {
    var textPort by remember { mutableStateOf(state.port) }
    var btnText by remember { mutableStateOf("start port ${textPort}") }

    fun updateBtnText() {
        btnText = "start port ${textPort}"
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        TextField(
            value = textPort.toString(),
            onValueChange = { port ->
                port.toIntOrNull()?.let {
                    textPort = it
                    updateBtnText()
                }
            },
            label = { Text("port") },
        )

        Spacer(Modifier.height(8.dp))

        Button(onClick = {
            startButtonClick(
                AppActions.NotStartedScreen.StartProxyClick(
                    port = textPort,
                )
            )
        }) {
            Text(btnText)
        }
    }

}