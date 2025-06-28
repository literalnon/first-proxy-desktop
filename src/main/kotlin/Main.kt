//import androidx.compose.desktop.ui.tooling.preview.Preview
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.material.Button
//import androidx.compose.material.Card
//import androidx.compose.material.MaterialTheme
//import androidx.compose.material.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.window.Window
//import androidx.compose.ui.window.application
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import net.lightbody.bmp.core.har.HarEntry
//import server.FullFeaturedProxy
//import ui.ActiveScreenState
//
//@androidx.compose.material.ExperimentalMaterialApi
//fun main() = application {
//    val ioScope = CoroutineScope(Dispatchers.IO)
//    val proxy = FullFeaturedProxy()
//    var screenState by remember {
//        mutableStateOf(
//            ActiveScreenState.NotStartedScreen()
//        )
//    }
//
//    var text by remember { mutableStateOf("Click to start") }
//    val requests = remember { mutableStateListOf<HarEntry>() }
//    var requestInfo by remember { mutableStateOf<HarEntry?>(null) }
//
//    Window(onCloseRequest = ::exitApplication) {
//        AppTheme {
//            Column {
//                RequestInfo(requestInfo, onBackClick = {
//                    requestInfo = null
//                })
//                Button(onClick = {
//                    text = "started"
//                    //proxy.start(ioScope)
//                    ioScope.launch {
//                        proxy.requests.collect {
//                            println("proxy.requests.collect : ${it}")
//                            requests.addAll(it.drop(requests.size))
//                        }
//                    }
//                }) {
//                    Text(text)
//                }
//                Spacer(modifier = Modifier.height(16.dp))
//                RequestList(requests, {
//                    requestInfo = it
//                })
//            }
//        }
//    }
//}
//
//
//@Composable
//@androidx.compose.material.ExperimentalMaterialApi
//fun RequestList(requests: List<HarEntry>, onClick: (HarEntry) -> Unit) {
//    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
//        items(
//            count = requests.size
//        ) { entryIndex ->
//            RequestItem(requests[entryIndex], onClick)
//            Spacer(modifier = Modifier.height(8.dp))
//        }
//    }
//}
//
//@Composable
//@androidx.compose.material.ExperimentalMaterialApi
//fun RequestInfo(harEntry: HarEntry?, onBackClick: () -> Unit) {
//    if (harEntry == null) {
//        Text("Нихуя нету")
//        return
//    }
//
//    LazyColumn(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        item {
//            Button(onClick = {
//                onBackClick()
//            }) {
//                Text("Назад")
//            }
//        }
//        item {
//            // Статический блок с внутренней прокруткой
//            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
//                Text(text = "${harEntry.request.url}")
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(text = "${harEntry.request.method}")
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(text = "${harEntry.request.headers.toString()}")
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(text = "${harEntry.request.comment}")
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(text = "${harEntry.request.httpVersion}")
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(text = "${harEntry.request.bodySize.toString()}")
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(text = "${harEntry.request.cookies.toString()}")
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(text = "${harEntry.request.queryString.toString()}")
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(text = "${harEntry.request.postData.toString()}")
//                Spacer(modifier = Modifier.height(20.dp))
//                Text(text = "${harEntry.response.statusText}")
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(text = "${harEntry.response.comment}")
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(text = "${harEntry.response.httpVersion}")
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(text = "${harEntry.response.headers.toString()}")
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(text = "${harEntry.response.status.toString()}")
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(text = "${harEntry.response.error}")
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(text = "${harEntry.response.redirectURL}")
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(text = "${harEntry.response.content.toString()}")
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(text = "${harEntry.response.cookies.toString()}")
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(text = "${harEntry.comment}")
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(text = "${harEntry.pageref}")
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(text = "${harEntry.connection}")
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(text = "${harEntry.serverIPAddress}")
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(text = "${harEntry.startedDateTime.toString()}")
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(text = "${harEntry.time.toString()}")
//            }
//        }
//    }
//}
//
//@Composable
//@androidx.compose.material.ExperimentalMaterialApi
//fun RequestItem(entry: HarEntry, onClick: (HarEntry) -> Unit) {
//    Card(
//        onClick = {
//            onClick(entry)
//        },
//        modifier = Modifier.fillMaxWidth(),
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            Text(
//                text = entry.request.url,
//                style = MaterialTheme.typography.body2,
//                color = MaterialTheme.colors.primary
//            )
//            Spacer(modifier = Modifier.height(4.dp))
//            Text("Method: ${entry.request.method}")
//            Text("Status: ${entry.response.status}")
//            Text("Size: ${entry.response.content?.size ?: 0} bytes")
//        }
//    }
//}
//
//@Composable
//fun AppTheme(content: @Composable () -> Unit) {
//    MaterialTheme(
//        content = content
//    )
//}
