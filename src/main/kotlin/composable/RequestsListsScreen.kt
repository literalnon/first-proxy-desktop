package composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.lightbody.bmp.core.har.HarEntry
import store.ActiveScreenState

@Composable
@androidx.compose.material.ExperimentalMaterialApi
fun requestsListsScreen(
    state: ActiveScreenState.RequestsListsScreen,
    requestInfoClick: (HarEntry) -> Unit
) {
    Text("Proxy активен")

    RequestList(requests = state.requests, {
        requestInfoClick(it)
    })
}

@Composable
@androidx.compose.material.ExperimentalMaterialApi
fun RequestList(requests: List<HarEntry>, onClick: (HarEntry) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(
            count = requests.size
        ) { entryIndex ->
            RequestItem(requests[entryIndex], onClick)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
@androidx.compose.material.ExperimentalMaterialApi
fun RequestItem(entry: HarEntry, onClick: (HarEntry) -> Unit) {
    Card(
        onClick = {
            onClick(entry)
        },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = entry.request.url,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("Method: ${entry.request.method}")
            Text("Status: ${entry.response.status}")
            Text("Size: ${entry.response.content?.size ?: 0} bytes")
        }
    }
}