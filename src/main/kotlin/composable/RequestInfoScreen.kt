package composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.lightbody.bmp.core.har.HarEntry
import store.ActiveScreenState

@Composable
@androidx.compose.material.ExperimentalMaterialApi
fun requestInfoScreen(
    state: ActiveScreenState.RequestInfoScreen,
    onBackClick: () -> Unit
) {
    requestInfo(harEntry = state.request, onBackClick)
}

@Composable
@androidx.compose.material.ExperimentalMaterialApi
fun requestInfo(harEntry: HarEntry, onBackClick: () -> Unit) {

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Button(onClick = {
                onBackClick()
            }) {
                Text("Покинуть")
            }
        }
        item {
            // Статический блок с внутренней прокруткой
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text(text = "${harEntry.request.url}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${harEntry.request.method}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${harEntry.request.headers.toString()}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${harEntry.request.comment}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${harEntry.request.httpVersion}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${harEntry.request.bodySize.toString()}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${harEntry.request.cookies.toString()}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${harEntry.request.queryString.toString()}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${harEntry.request.postData.toString()}")
                Spacer(modifier = Modifier.height(20.dp))
                Text(text = "${harEntry.response.statusText}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${harEntry.response.comment}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${harEntry.response.httpVersion}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${harEntry.response.headers.toString()}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${harEntry.response.status.toString()}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${harEntry.response.error}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${harEntry.response.redirectURL}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${harEntry.response.content.toString()}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${harEntry.response.cookies.toString()}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${harEntry.comment}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${harEntry.pageref}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${harEntry.connection}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${harEntry.serverIPAddress}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${harEntry.startedDateTime.toString()}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${harEntry.time.toString()}")
            }
        }
    }
}
