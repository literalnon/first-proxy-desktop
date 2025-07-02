package server

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import kotlinx.serialization.json.internal.decodeStringToJsonTree
import net.lightbody.bmp.BrowserMobProxyServer
import net.lightbody.bmp.core.har.Har
import net.lightbody.bmp.core.har.HarEntry
import net.lightbody.bmp.filters.RequestFilter
import net.lightbody.bmp.filters.ResponseFilter
import net.lightbody.bmp.mitm.CertificateAndKey
import net.lightbody.bmp.mitm.CertificateAndKeySource
import net.lightbody.bmp.mitm.RootCertificateGenerator
import net.lightbody.bmp.mitm.manager.ImpersonatingMitmManager
import net.lightbody.bmp.mitm.tools.DefaultSecurityProviderTool
import net.lightbody.bmp.mitm.tools.SecurityProviderTool
import net.lightbody.bmp.proxy.CaptureType
import org.littleshoot.proxy.HttpFiltersSourceAdapter
import server.settings.SettingsDataStore
import java.io.*
import java.nio.file.Files
import java.util.stream.Collectors

class FullFeaturedProxy(
    val settingsDataStore: SettingsDataStore
) {
    //    val requests = remember { mutableStateListOf<HarEntry>() }
    val requests = MutableStateFlow<List<HarEntry>>(listOf())
    private val ioScope = CoroutineScope(Dispatchers.Default)

    //private var settings: List<IProxySetting> = listOf()
    private val proxy = BrowserMobProxyServer()

    suspend fun start(
        port: Int
    ) {
        ioScope.launch {
            settingsDataStore.enabledSettingIds.collect { enabledSettingsId ->
                settingsDataStore.allSettings.value
                    .filter { enabledSettingsId.contains(it.id) }
                    .forEach {
                        proxy.addResponseFilter(it.toResponseFilter())
                        proxy.addRequestFilter(it.toRequestFilter())
                    }
            }
        }

        //proxyServer.start()
        // 1. Создаем прокси-сервер

        //proxy.setTrustAllServers(true);  // Игнорировать ошибки сертификатов
        proxy.setMitmDisabled(false);    // Включить MITM-перехват
        proxy.setHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
        proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
        proxy.addHttpFilterFactory(object : HttpFiltersSourceAdapter() {
            override fun getMaximumRequestBufferSizeInBytes(): Int {
                return 10 * 1024 * 1024
            }

            override fun getMaximumResponseBufferSizeInBytes(): Int {
                return 10 * 1024 * 1024
            }
        })

        val certFileName = "proxy-ca-cert.pem"
        val certKeyFileName = "proxy-ca-key.pem"

        val certFile = File(certFileName)
        val certKeyFile = File(certKeyFileName)
        val securityTool: SecurityProviderTool = DefaultSecurityProviderTool()
        val keyPassword = "456123qwe"

        val certSource: CertificateAndKeySource = if (certFile.exists()) {
            val certificate = securityTool.decodePemEncodedCertificate(FileReader(certFileName))
            val privateKey = securityTool.decodePemEncodedPrivateKey(FileReader(certKeyFileName), keyPassword)

            CertificateAndKeySource { CertificateAndKey(certificate, privateKey) }
        } else {
            Files.createFile(certFile.toPath())
            Files.createFile(certKeyFile.toPath())

            RootCertificateGenerator
                .builder()
                .build().apply {
                    saveRootCertificateAsPemFile(certFile)
                    savePrivateKeyAsPemFile(certKeyFile, keyPassword)
                    consoleExec(certFile.absolutePath)
                }

        }

        println("Cert Path : ${certFile.absolutePath} ::: ${certFile.canonicalPath}")

        // Настройка MitM с этим сертификатом
        proxy.setMitmManager(
            ImpersonatingMitmManager.builder()
                .rootCertificateSource(certSource)
                //.trustAllServers(true)
                .build()
        )

        // Настройка протоколов
        //proxy.setProtocols(Arrays.asList("h2", "http/1.1"));
        //proxy.setAlpnEnabled(true);

        println("CA-сертификат сохранён в: " + certFile.absolutePath)
        //proxy.port = 12346 // Порт прокси
        // 2. Включаем захват всех данных
        proxy.enableHarCaptureTypes(
            CaptureType.REQUEST_HEADERS,
            CaptureType.REQUEST_CONTENT,
            CaptureType.RESPONSE_HEADERS,
            CaptureType.RESPONSE_CONTENT
        )

        // 3. Создаем новый HAR-лог
        val harFile = proxy.newHar("MyProxyHar")

        // 4. Добавляем обработчики для логирования
        proxy.addRequestFilter { request, contents, messageInfo ->
            //if (messageInfo.originalUrl.contains("betcity.ru")) {
            println("\n=== ЗАПРОС === ${messageInfo.originalUrl}")
            println("URL: " + request.uri)
            println("Method: " + request.method.name())
            println("Headers: " + request.headers())

            if (contents?.getTextContents() != null) {
                println("Body: " + contents.getTextContents())
            }
            //}

            request.headers().remove("Proxy-Connection");
            request.headers().remove("X-Forwarded-For");

            null
        }

        proxy.addResponseFilter { response, contents, messageInfo ->
            //if (messageInfo.originalUrl.contains("betcity.ru")) {
            println("\n=== ОТВЕТ === ${messageInfo.originalUrl}")
            System.out.println("Status: " + response.getStatus())
            System.out.println("Headers: " + response.headers())
            if (contents != null && contents.getTextContents() != null) {
                //System.out.println("Body: " + contents.getTextContents())
            }
            println("RESPONSE_TEST :: 1 :: ${contents.textContents?.contains("Испания (19)")}")

            //.replace("Россия", "Лучшая в мире страна")
            println("RESPONSE_TEST :: 2 :: ${contents.textContents?.contains("Испания (19)")}")
            println("RESPONSE_TEST :: 2.5 :: ${requests.replayCache}")
            println("RESPONSE_TEST :: 2.5 :: ${requests.replayCache}")
            println("RESPONSE_TEST :: 3 :: ${proxy.har.log.entries}")

            ioScope.launch {
                requests.emit(proxy.har.log.entries.filter { it.request.url.contains("betcity.ru") })
            }
            //}
        }

//        settings.forEach {
//            proxy.addResponseFilter(it.toResponseFilter())
//        }

        //sudo security add-trusted-cert -d -r trustRoot -k /Library/Keychains/System.keychain ca-cert-mitmproxy-ca-cert.pem
        // 5. Запускаем прокси
        proxy.start(port)

        println("Прокси запущен на http://localhost:${proxy.port}")

        // 6. Сохраняем HAR в файл при завершении (Ctrl+C)
        Runtime.getRuntime().addShutdownHook(Thread {
            val har: Har = proxy.getHar()
            try {
                har.writeTo(File("traffic.har"))
                println("HAR-лог сохранен в traffic.har")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }

    private fun consoleExec(path: String) {
        try {
            // Создание процесса с аргументами
            //val process = Runtime.getRuntime().exec("sudo security add-trusted-cert -d -r trustRoot -k /Library/Keychains/System.keychain ${path}")
            val script = String.format(
                "do shell script \"security add-trusted-cert -d -r trustRoot -k /Library/Keychains/System.keychain %s\" with administrator privileges",
                "/path/to/cert.pem" // Замените на реальный путь
            )

            val cmd = arrayOf("osascript", "-e", script)

            val builder = ProcessBuilder(*cmd)
            builder.redirectErrorStream(true)
            val process = builder.start()

            BufferedReader(
                InputStreamReader(process.inputStream)
            ).use { reader ->
                reader.lines().forEach { x: String? ->
                    println(
                        x
                    )
                }
            }
            val exitCode = process.waitFor()
            println("Exit Code: $exitCode")
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}

private fun String.encode(): String {
    return chars()
        .mapToObj { c: Int -> "\\u" + String.format("%04x", c) }
        .collect(Collectors.joining())
}


private fun IProxySetting.toRequestFilter(): RequestFilter {
    return RequestFilter { request, contents, messageInfo ->
        when (this) {
            is IProxySetting.ChangeDomain -> {
                request.setUri(request.uri.replace(domainOld, domainNew))
                null
            }

            is IProxySetting.ChangeFieldValue,
            is IProxySetting.ChangeResponse,
            is IProxySetting.ChangeText -> {
                null
            }
        }
    }
}

private fun IProxySetting.toResponseFilter(): ResponseFilter {
    return ResponseFilter { response, contents, messageInfo ->
        when (this) {
            is IProxySetting.ChangeFieldValue -> {
                try {
                    contents.textContents = Json.encodeToString(
                        Json.decodeFromString<JsonElement>(
                            contents.textContents
                        ).changeValueInJsonElement(this)
                    )
                } catch (e: Exception) {
                    e.printStackTrace()

                    return@ResponseFilter
                }
            }

            is IProxySetting.ChangeResponse -> {
                if (!messageInfo.originalUrl.contains(this.url)) {
                    return@ResponseFilter
                }

                contents.textContents = this.response
            }

            is IProxySetting.ChangeText -> {
                contents.textContents = contents.textContents
                    .replace(
                        this.beforeChangedString, this.afterChangedString
                    )
                    .replace(
                        this.beforeChangedString.encode(), this.afterChangedString
                    )
            }

            is IProxySetting.ChangeDomain -> {

            }
        }
    }
}

private fun JsonElement.changeValueInJsonElement(setting: IProxySetting.ChangeFieldValue): JsonElement {
    return when (this) {
        is JsonArray -> JsonArray(
            map { it.changeValueInJsonElement(setting) }
        )

        is JsonObject -> changeValueInJsonObject(setting)
        else -> this
    }
}

private fun JsonObject.changeValueInJsonObject(setting: IProxySetting.ChangeFieldValue): JsonObject {
    val content = this.mapValues {
        if (it.key == setting.changedFieldName) {
            return@mapValues JsonPrimitive(setting.changedFieldValue)
        }

        return@mapValues it.value.changeValueInJsonElement(setting)
    }

    return JsonObject(content)
}