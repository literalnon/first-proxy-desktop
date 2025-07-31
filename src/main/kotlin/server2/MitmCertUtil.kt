package server2

import io.netty.handler.ssl.SslContext
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.cert.X509CertificateHolder
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.operator.ContentSigner
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import java.io.FileInputStream
import java.math.BigInteger
import java.security.*
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.KeyManagerFactory
import io.netty.handler.ssl.SslContextBuilder
import net.lightbody.bmp.mitm.trustmanager.InsecureTrustManagerFactory
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelPipeline
import io.netty.channel.Channel
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.FullHttpResponse
import io.netty.handler.codec.http.HttpContent
import io.netty.util.ReferenceCountUtil
import io.netty.handler.codec.http.HttpServerCodec
import io.netty.handler.codec.http.HttpClientCodec
import io.netty.handler.codec.http.HttpObjectAggregator

object MitmCertUtil {
    private var caCert: X509Certificate? = null
    private var caKey: PrivateKey? = null
    private val certCache = mutableMapOf<String, SslContext>()

    init {
        Security.addProvider(BouncyCastleProvider())
        loadCACertificate()
    }

    private fun loadCACertificate() {
        try {
            // Загружаем CA-сертификат и ключ
            val caKeyStore = KeyStore.getInstance("JKS")
            val caFile = java.io.File("ca.jks")
            if (!caFile.exists()) {
                println("CA file ca.jks not found!")
                println("Please run ./generate_ca.sh first")
                return
            }
            
            caKeyStore.load(FileInputStream("ca.jks"), "password".toCharArray())
            caCert = caKeyStore.getCertificate("ca") as X509Certificate
            caKey = caKeyStore.getKey("ca", "password".toCharArray()) as PrivateKey
            println("CA certificate loaded successfully")
            println("CA Subject: ${caCert?.subjectX500Principal}")
            println("CA Issuer: ${caCert?.issuerX500Principal}")
        } catch (e: Exception) {
            println("Failed to load CA certificate: ${e.message}")
            println("Please create ca.jks with your CA certificate and private key")
            e.printStackTrace()
        }
    }

    fun generateServerCert(serverHost: String): SslContext? {
        if (caCert == null || caKey == null) {
            println("CA certificate not loaded, cannot generate server certificate")
            return null
        }

        // Проверяем кэш
        certCache[serverHost]?.let { return it }

        try {
            val keyPairGen = KeyPairGenerator.getInstance("RSA")
            keyPairGen.initialize(2048)
            val serverKeyPair = keyPairGen.generateKeyPair()

            val now = Date()
            val until = Date(now.time + 365 * 24 * 60 * 60 * 1000L)
            val certBuilder = JcaX509v3CertificateBuilder(
                X500Name("CN=$serverHost"),
                BigInteger.valueOf(System.currentTimeMillis()),
                now,
                until,
                X500Name("CN=$serverHost"),
                serverKeyPair.public
            )

            val signer: ContentSigner = JcaContentSignerBuilder("SHA256WithRSAEncryption")
                .build(caKey)
            val certHolder: X509CertificateHolder = certBuilder.build(signer)
            val serverCert: X509Certificate = JcaX509CertificateConverter()
                .setProvider(BouncyCastleProvider())
                .getCertificate(certHolder)

            val ks = KeyStore.getInstance("JKS")
            ks.load(null, null)
            ks.setKeyEntry(
                "alias",
                serverKeyPair.private,
                "password".toCharArray(),
                arrayOf(serverCert, caCert)
            )

            val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
            keyManagerFactory.init(ks, "password".toCharArray())
            val sslCtx = SslContextBuilder.forServer(keyManagerFactory).build()

            // Кэшируем результат
            certCache[serverHost] = sslCtx
            println("Generated SSL context for $serverHost")
            return sslCtx
        } catch (e: Exception) {
            println("Failed to generate server certificate for $serverHost: ${e.message}")
            return null
        }
    }

    fun createClientSslContext(): SslContext {
        return SslContextBuilder.forClient()
            .trustManager(InsecureTrustManagerFactory.INSTANCE) // Игнорируем проверку сертификатов сервера
            .build()
    }
} 