package server

sealed interface IProxySetting {
    data class ChangeFieldValue(
        val changedFieldName: String,
        val changedFieldValue: String,
    ): IProxySetting

    data class ChangeText(
        val beforeChangedString: String,
        val afterChangedString: String,
    ): IProxySetting

    data class ChangeResponse(
        val url: String,
        val response: String,
    ): IProxySetting
}