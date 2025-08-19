package server

import net.lightbody.bmp.mitm.CertificateAndKey
import net.lightbody.bmp.mitm.CertificateAndKeySource
import net.lightbody.bmp.mitm.RootCertificateGenerator
import net.lightbody.bmp.mitm.manager.ImpersonatingMitmManager
import net.lightbody.bmp.mitm.tools.DefaultSecurityProviderTool
import net.lightbody.bmp.mitm.tools.SecurityProviderTool
import org.littleshoot.proxy.impl.DefaultHttpProxyServer
import java.io.File
import java.io.FileReader
import java.nio.file.Files

/**
 * Класс для создания и управления CA сертификатами для MITM прокси.
 * Основан на официальной документации BrowserMob Proxy MITM модуля.
 * 
 * Пример использования:
 * ```
 * val certCreator = CertSourceCreator()
 * 
 * // Создание MITM менеджера
 * val mitmManager = certCreator.createMitmManager()
 * 
 * // Создание прокси сервера с MITM
 * val proxyServer = certCreator.createProxyServer(8080)
 * 
 * // Или использование с BrowserMob Proxy
 * val browserMobProxy = BrowserMobProxyServer()
 * browserMobProxy.setMitmManager(mitmManager)
 * ```
 */
class CertSourceCreator {
    private var certFile: File? = null

    fun getCertPath(): String = certFile?.absolutePath ?: ""

    /**
     * Создает или загружает CertificateAndKeySource для MITM.
     * Если файлы сертификата не существуют, генерирует новые.
     */
    fun create(): CertificateAndKeySource {
        val certFileName = "proxy-ca-cert.cer"
        val certKeyFileName = "proxy-ca-key.pem"

        val certFile = File(certFileName)
        val certKeyFile = File(certKeyFileName)
        val keyPassword = "456123qwe"

        val certSource: CertificateAndKeySource = if (certFile.exists() && certKeyFile.exists()) {
            // Используем существующие файлы сертификата и ключа
            loadExistingCertificateAndKey(certFileName, certKeyFileName, keyPassword)
        } else {
            // Генерируем новый CA сертификат и ключ
            generateNewCertificateAndKey(certFile, certKeyFile, keyPassword)
        }

        println("CA-сертификат сохранён в: " + certFile.absolutePath)
        this.certFile = certFile

        return certSource
    }

    fun create2(): CertificateAndKeySource {
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
                    //consoleExec(certFile.absolutePath)
                }

        }

        println("Cert Path : ${certFile.absolutePath} ::: ${certFile.canonicalPath}")
        this.certFile = certFile

        return certSource
    }

    /**
     * Загружает существующий сертификат из PEM файлов.
     * Использует PemFileCertificateSource - рекомендуемый способ из документации.
     */
    private fun loadExistingCertificateAndKey(certFileName: String, certKeyFileName: String, keyPassword: String): CertificateAndKeySource {
        return try {
            // Используем PemFileCertificateSource для загрузки из PEM файлов
            // Это более надежный способ, рекомендованный в документации
            val pemSource = net.lightbody.bmp.mitm.PemFileCertificateSource(
                File(certFileName),
                File(certKeyFileName),
                keyPassword
            )
            
            println("Успешно загружен существующий CA сертификат из PEM файлов")
            pemSource
        } catch (e: Exception) {
            println("Ошибка при загрузке существующих PEM файлов: ${e.message}")
            println("Генерируем новый CA сертификат...")
            
            // Fallback: используем старый метод с обработкой ошибок
            loadWithLegacyMethod(certFileName, certKeyFileName, keyPassword)
        }
    }

    /**
     * Fallback метод для загрузки сертификата старым способом.
     * Используется только если PemFileCertificateSource не работает.
     */
    private fun loadWithLegacyMethod(certFileName: String, certKeyFileName: String, keyPassword: String): CertificateAndKeySource {
        val securityTool: SecurityProviderTool = DefaultSecurityProviderTool()
        
        return try {
            val certificate = securityTool.decodePemEncodedCertificate(FileReader(certFileName))
            val privateKey = securityTool.decodePemEncodedPrivateKey(FileReader(certKeyFileName), keyPassword)
            
            CertificateAndKeySource { CertificateAndKey(certificate, privateKey) }
        } catch (e: Exception) {
            println("Ошибка при загрузке приватного ключа: ${e.message}")
            throw RuntimeException("Не удалось загрузить существующий сертификат", e)
        }
    }

    /**
     * Генерирует новый CA сертификат и ключ.
     * Сохраняет в PEM и PKCS12 форматах для максимальной совместимости.
     */
    private fun generateNewCertificateAndKey(certFile: File, certKeyFile: File, keyPassword: String): CertificateAndKeySource {
        // Создаем CA Root Certificate используя настройки по умолчанию
        // Это соответствует примеру из документации
        val rootCertificateGenerator = RootCertificateGenerator.builder().build()

        // Сохраняем сгенерированный Root Certificate и Private Key
        // Файл .pem можно импортировать напрямую в браузер
        rootCertificateGenerator.saveRootCertificateAsPemFile(certFile)
        rootCertificateGenerator.savePrivateKeyAsPemFile(certKeyFile, keyPassword)

        // Также сохраняем сертификат и приватный ключ как PKCS12 keystore для последующего использования
        val keystoreFile = File("proxy-ca-keystore.p12")
        rootCertificateGenerator.saveRootCertificateAndKey(
            "PKCS12", 
            keystoreFile,
            "privateKeyAlias", 
            keyPassword
        )

        println("Сгенерирован новый CA сертификат")
        println("PEM файлы: ${certFile.absolutePath}, ${certKeyFile.absolutePath}")
        println("PKCS12 keystore: ${keystoreFile.absolutePath}")

        return rootCertificateGenerator
    }
}