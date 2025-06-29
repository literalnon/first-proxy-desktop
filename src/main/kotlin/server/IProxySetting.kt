package server

import kotlin.random.Random

sealed interface IProxySetting {
    val id: Long
    val groupName: String

    data class ChangeFieldValue(
        override val id: Long = Random.nextLong(),
        override val groupName: String,
        val changedFieldName: String,
        val changedFieldValue: String,
    ): IProxySetting

    data class ChangeText(
        override val id: Long = Random.nextLong(),
        override val groupName: String,
        val beforeChangedString: String,
        val afterChangedString: String,
    ): IProxySetting

    data class ChangeResponse(
        override val id: Long = Random.nextLong(),
        override val groupName: String,
        val url: String,
        val response: String,
    ): IProxySetting
}