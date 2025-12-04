package server

import java.time.LocalDateTime
import kotlin.random.Random

sealed interface IProxySetting {
    val id: Long
    val groupName: String
    val settingName: String

    data class ChangeFieldValue(
        override val id: Long = LocalDateTime.now().nano.toLong() + Random.nextLong(),
        override val groupName: String,
        override val settingName: String,
        val changedFieldName: String,
        val changedFieldValue: String,
    ): IProxySetting

    data class ChangeText(
        override val id: Long = LocalDateTime.now().nano.toLong() + Random.nextLong(),
        override val groupName: String,
        override val settingName: String,
        val beforeChangedString: String,
        val afterChangedString: String,
    ): IProxySetting

    data class ChangeResponse(
        override val id: Long = LocalDateTime.now().nano.toLong() + Random.nextLong(),
        override val groupName: String,
        override val settingName: String,
        val url: String,
        val response: String,
    ): IProxySetting

    data class ChangeDomain(
        override val id: Long = LocalDateTime.now().nano.toLong() + Random.nextLong(),
        override val groupName: String,
        override val settingName: String,
        val domainOld: String,
        val domainNew: String,
    ): IProxySetting

    data class Sleep(
        override val id: Long = LocalDateTime.now().nano.toLong() + Random.nextLong(),
        override val groupName: String,
        override val settingName: String,
        val time: Long,
        val url: String,
    ): IProxySetting
}