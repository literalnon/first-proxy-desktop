package server

import net.lightbody.bmp.client.ClientUtil
import net.lightbody.bmp.proxy.dns.AdvancedHostResolver
import java.net.InetAddress
import java.util.concurrent.TimeUnit

class CustomHostResolver : AdvancedHostResolver {
    private val hostResolver = ClientUtil.createNativeCacheManipulatingResolver()
    override fun resolve(p0: String?): MutableCollection<InetAddress> {
        println("CustomHostResolver :: resolve 1 :: ${p0}")
        val resolveReturnVal = hostResolver.resolve(p0)
        println("CustomHostResolver :: resolve 2 :: ${p0} :: ${resolveReturnVal}")
        return resolveReturnVal
    }

    override fun remapHosts(p0: MutableMap<String, String>?) {
        println("CustomHostResolver :: remapHosts :: ${p0}")
        hostResolver.remapHosts(p0)
    }

    override fun remapHost(p0: String?, p1: String?) {
        println("CustomHostResolver :: remapHost :: ${p0} :: ${p1}")
        hostResolver.remapHost(p0, p1)
    }

    override fun removeHostRemapping(p0: String?) {
        println("CustomHostResolver :: removeHostRemapping :: ${p0}")
        hostResolver.removeHostRemapping(p0)
    }

    override fun clearHostRemappings() {
        println("CustomHostResolver :: clearHostRemappings")
        hostResolver.clearHostRemappings()
    }

    override fun getHostRemappings(): MutableMap<String, String> {
        println("CustomHostResolver :: getHostRemappings")
        return hostResolver.hostRemappings
    }

    override fun getOriginalHostnames(p0: String?): MutableCollection<String> {
        println("CustomHostResolver :: getOriginalHostnames :: ${p0}")
        return hostResolver.getOriginalHostnames(p0)
    }

    override fun clearDNSCache() {
        println("CustomHostResolver :: clearDNSCache")
        hostResolver.clearDNSCache()
    }

    override fun setPositiveDNSCacheTimeout(p0: Int, p1: TimeUnit?) {
        println("CustomHostResolver :: setPositiveDNSCacheTimeout :: ${p0} :: ${p1}")
        hostResolver.setPositiveDNSCacheTimeout(p0, p1)
    }

    override fun setNegativeDNSCacheTimeout(p0: Int, p1: TimeUnit?) {
        println("CustomHostResolver :: setNegativeDNSCacheTimeout :: ${p0} :: ${p1}")
        hostResolver.setNegativeDNSCacheTimeout(p0, p1)
    }
}