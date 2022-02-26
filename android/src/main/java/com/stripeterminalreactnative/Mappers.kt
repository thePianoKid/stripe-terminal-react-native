package com.stripeterminalreactnative

import com.facebook.react.bridge.*
import com.stripe.stripeterminal.external.models.*
import com.stripe.stripeterminal.log.LogLevel

fun getStringOr(map: ReadableMap, key: String, default: String? = null): String? {
    return if (map.hasKey(key)) map.getString(key) else default
}

fun getIntOr(map: ReadableMap, key: String, default: Int? = null): Int? {
    return if (map.hasKey(key)) map.getInt(key) else default
}

fun getBoolean(map: ReadableMap, key: String): Boolean {
    return if (map.hasKey(key)) map.getBoolean(key) else false
}

fun getMapOr(map: ReadableMap, key: String, default: ReadableMap? = null): ReadableMap? {
    return if (map.hasKey(key)) map.getMap(key) else default
}

fun getArrayOr(map: ReadableMap, key: String, default: ReadableArray? = null): ReadableArray? {
    return if (map.hasKey(key)) map.getArray(key) else default
}

fun putDoubleOrNull(mapTarget: WritableMap, key: String, value: Double?) {
    value?.let {
        mapTarget.putDouble(key, it)
    } ?: run {
        mapTarget.putNull(key)
    }
}

fun putIntOrNull(mapTarget: WritableMap, key: String, value: Int?) {
    value?.let {
        mapTarget.putInt(key, it)
    } ?: run {
        mapTarget.putNull(key)
    }
}

internal fun mapFromReaders(readers: List<Reader>): WritableArray {
    val list: WritableArray = WritableNativeArray()

    readers.forEach {
        val reader = mapFromReader(it)
        list.pushMap(reader)
    }

    return list
}

internal fun mapFromReader(reader: Reader): WritableMap {
    val item: WritableMap = WritableNativeMap()
    item.putString("label", reader.label)
    item.putString("serialNumber", reader.serialNumber)
    item.putString("id", reader.id)
    item.putString("locationId", reader.location?.id)
    item.putString("deviceSoftwareVersion", reader.softwareVersion)
    item.putString("deviceType", mapFromDeviceType(reader.deviceType))
    item.putBoolean("simulated", reader.isSimulated)
    item.putString("locationStatus", mapFromLocationStatus(reader.locationStatus))
    item.putString("ipAddress", reader.ipAddress)
    item.putString("baseUrl", reader.baseUrl)
    item.putString("bootloaderVersion", reader.bootloaderVersion)
    item.putString("configVersion", reader.configVersion)
    item.putString("emvKeyProfileId", reader.emvKeyProfileId)
    item.putString("firmwareVersion", reader.firmwareVersion)
    item.putString("hardwareVersion", reader.hardwareVersion)
    item.putString("macKeyProfileId", reader.macKeyProfileId)
    item.putString("pinKeyProfileId", reader.pinKeyProfileId)
    item.putString("trackKeyProfileId", reader.trackKeyProfileId)
    item.putString("settingsVersion", reader.settingsVersion)
    item.putString("pinKeysetId", reader.pinKeysetId)

    item.putMap("availableUpdate", mapFromReaderSoftwareUpdate(reader.availableUpdate))
    item.putMap("location", mapFromLocation(reader.location))
    item.putString("status", mapFromNetworkStatus(reader.networkStatus))
    putDoubleOrNull(item, "batteryLevel", reader.batteryLevel?.toDouble())

    return item
}

internal fun mapFromNetworkStatus(status: Reader.NetworkStatus?): String {
    return when (status) {
        Reader.NetworkStatus.OFFLINE -> "offline"
        Reader.NetworkStatus.ONLINE -> "online"
        else -> "unknown"
    }
}

internal fun mapFromDeviceType(type: DeviceType): String {
    return when (type) {
        DeviceType.CHIPPER_2X -> "chipper2X"
        DeviceType.COTS_DEVICE -> "cotsDevice"
        DeviceType.STRIPE_M2 -> "stripeM2"
        DeviceType.UNKNOWN -> "unknown"
        DeviceType.VERIFONE_P400 -> "verifoneP400"
        DeviceType.WISEPAD_3 -> "wisePad3"
        DeviceType.WISEPOS_E -> "wisePosE"
        else -> "unknown"
    }
}

internal fun mapFromLocationStatus(status: LocationStatus): String {
    return when (status) {
        LocationStatus.NOT_SET -> "notSet"
        LocationStatus.SET -> "set"
        LocationStatus.UNKNOWN -> "unknown"
        else -> "unknown"
    }
}

internal fun mapToDiscoveryMethod(method: String?): DiscoveryMethod {
    return when (method) {
        "bluetoothScan" -> DiscoveryMethod.BLUETOOTH_SCAN
        "internet" -> DiscoveryMethod.INTERNET
        "embedded" -> DiscoveryMethod.EMBEDDED
        "localMobile" -> DiscoveryMethod.LOCAL_MOBILE
        "handoff" -> DiscoveryMethod.HANDOFF
        else -> DiscoveryMethod.INTERNET
    }
}

internal fun createResult(key: String, value: WritableMap): WritableMap =
    WritableNativeMap().apply {
        putMap(key, value)
    }

internal fun mapFromPaymentIntent(paymentIntent: PaymentIntent): WritableMap =
    WritableNativeMap().apply {
        putInt("amount", paymentIntent.amount.toInt())
        putString("currency", paymentIntent.currency)
        putString("id", paymentIntent.id)
        putString("description", paymentIntent.description)
        putString("status", mapFromPaymentIntentStatus(paymentIntent.status))
        putArray("charges", mapFromChargesList(paymentIntent.getCharges()))
        putString("created", convertToUnixTimestamp(paymentIntent.created))
    }

internal fun mapFromSetupIntent(setupIntent: SetupIntent): WritableMap = WritableNativeMap().apply {
    putString("created", convertToUnixTimestamp(setupIntent.created))
    putString("id", setupIntent.id)
    putString("status", mapFromSetupIntentStatus(setupIntent.status))
    putMap("latestAttempt", mapFromSetupAttempt(setupIntent.latestAttempt))
    putString("usage", mapFromSetupIntentUsage(setupIntent.usage))
    putString("applicationId", setupIntent.applicationId)
    putString("clientSecret", setupIntent.clientSecret)
    putString("description", setupIntent.description)
    putString("mandateId", setupIntent.mandateId)
    putString("onBehalfOfId", setupIntent.onBehalfOfId)
    putString("paymentMethodId", setupIntent.paymentMethodId)
    putString("singleUseMandateId", setupIntent.singleUseMandateId)
}

internal fun mapFromSetupAttempt(attempt: SetupAttempt?): WritableMap? = attempt?.let {
    WritableNativeMap().apply {
        putString("created", convertToUnixTimestamp(it.created))
        putString("id", it.id)
        putString("status", mapFromSetupAttemptStatus(it.status))
        putString("usage", mapFromSetupIntentUsage(it.usage))
        putBoolean("isLiveMode", it.isLiveMode)
        putMap(
            "paymentMethodDetails",
            mapFromSetupIntentPaymentMethodDetails(it.paymentMethodDetails)
        )
        putString("customer", it.customerId)
        putString("setupIntentId", it.setupIntentId)
        putString("onBehalfOfId", it.onBehalfOfId)
        putString("applicationId", it.applicationId)
        putString("paymentMethodId", it.paymentMethodId)
    }
}

internal fun mapFromSetupIntentPaymentMethodDetails(
    details: SetupIntentPaymentMethodDetails
): WritableMap = WritableNativeMap().apply {
    putMap("cardPresent", mapFromSetupIntentCardPresentDetails(details.cardPresentDetails))
    putMap("interacPresent", mapFromSetupIntentCardPresentDetails(details.interacPresentDetails))
}

internal fun mapFromSetupIntentCardPresentDetails(
    details: SetupIntentCardPresentDetails?
): WritableMap? = details?.let {
    WritableNativeMap().apply {
        putString("emvAuthData", it.emvAuthData)
        putString("generatedCard", it.generatedCard)
    }
}

internal fun mapFromSetupIntentUsage(usage: SetupIntentUsage?): String? = usage?.let {
    when (it) {
        SetupIntentUsage.OFF_SESSION -> "offSession"
        SetupIntentUsage.ON_SESSION -> "onSession"
        else -> "unknown"
    }
}

internal fun mapFromSetupAttemptStatus(method: SetupAttemptStatus): String {
    return when (method) {
        SetupAttemptStatus.ABANDONED -> "abandoned"
        SetupAttemptStatus.FAILED -> "failed"
        SetupAttemptStatus.PROCESSING -> "processing"
        SetupAttemptStatus.REQUIRES_ACTION -> "requiresAction"
        SetupAttemptStatus.REQUIRES_CONFIRMATION -> "requiresConfirmation"
        SetupAttemptStatus.SUCCEEDED -> "succeeded"
        else -> "unknown"
    }
}

internal fun mapFromChargesList(charges: List<Charge>): WritableArray {
    val list: WritableArray = WritableNativeArray()

    charges.forEach {
        val reader = mapFromCharge(it)
        list.pushMap(reader)
    }

    return list
}

internal fun mapFromListLocations(locations: List<Location>): WritableArray {
    val list: WritableArray = WritableNativeArray()

    locations.forEach {
        mapFromLocation(it)?.let { location ->
            list.pushMap(location)
        }
    }

    return list
}

internal fun mapFromReaderInputOptions(options: ReaderInputOptions): WritableArray {
    val mappedOptions: WritableArray = WritableNativeArray()
    val optionsArray = options.toString().split('/')
    optionsArray.forEach {
        when (it.trim()) {
            "Insert" -> mappedOptions.pushString("insertCard")
            "Swipe" -> mappedOptions.pushString("swipeCard")
            "Tap" -> mappedOptions.pushString("tapCard")
        }
    }
    return mappedOptions
}

internal fun mapFromReaderDisplayMessage(message: ReaderDisplayMessage): String {
    return when (message) {
        ReaderDisplayMessage.CHECK_MOBILE_DEVICE -> "checkMobileDevice"
        ReaderDisplayMessage.INSERT_CARD -> "insertCard"
        ReaderDisplayMessage.INSERT_OR_SWIPE_CARD -> "insertOrSwipeCard"
        ReaderDisplayMessage.MULTIPLE_CONTACTLESS_CARDS_DETECTED -> "multipleContactlessCardsDetected"
        ReaderDisplayMessage.REMOVE_CARD -> "removeCard"
        ReaderDisplayMessage.RETRY_CARD -> "retryCard"
        ReaderDisplayMessage.SWIPE_CARD -> "swipeCard"
        ReaderDisplayMessage.TRY_ANOTHER_CARD -> "tryAnotherCard"
        ReaderDisplayMessage.TRY_ANOTHER_READ_METHOD -> "tryAnotherReadMethod"
        else -> "unknown"
    }
}

internal fun mapFromCharge(reader: Charge): WritableMap = WritableNativeMap().apply {
    putString("id", reader.id)
    putString("status", reader.status)
    putString("currency", reader.currency)
    putInt("amount", reader.amount.toInt())
    putString("description", reader.description)
}

internal fun mapFromLocation(location: Location?): WritableMap? = location?.let {
    WritableNativeMap().apply {
        putString("id", it.id)
        putString("displayName", it.displayName)

        mapFromAddress(it.address)?.let {
            putMap("address", it)
        } ?: run {
            putNull("address")
        }

        it.livemode?.let {
            putBoolean("livemode", it)
        } ?: run {
            putNull("livemode")
        }
    }
}

internal fun mapFromAddress(address: Address?): WritableMap? = address?.let {
    WritableNativeMap().apply {
        putString("country", it.country)
        putString("city", it.city)
        putString("postalCode", it.postalCode)
        putString("line1", it.line1)
        putString("line2", it.line2)
        putString("state", it.state)
    }
}

internal fun mapFromPaymentIntentStatus(status: PaymentIntentStatus?): String {
    return when (status) {
        PaymentIntentStatus.CANCELED -> "canceled"
        PaymentIntentStatus.REQUIRES_CAPTURE -> "requiresCapture"
        PaymentIntentStatus.REQUIRES_CONFIRMATION -> "requiresConfirmation"
        PaymentIntentStatus.REQUIRES_PAYMENT_METHOD -> "requiresPaymentMethod"
        PaymentIntentStatus.SUCCEEDED -> "succeeded"
        else -> "unknown"
    }
}

internal fun mapToLogLevel(status: String?): LogLevel {
    return when (status) {
        "error" -> LogLevel.ERROR
        "info" -> LogLevel.INFO
        "verbose" -> LogLevel.VERBOSE
        "warning" -> LogLevel.WARNING
        "none" -> LogLevel.NONE
        else -> LogLevel.NONE
    }
}

internal fun mapFromConnectionStatus(status: ConnectionStatus): String {
    return when (status) {
        ConnectionStatus.CONNECTED -> "connected"
        ConnectionStatus.NOT_CONNECTED -> "notConnected"
        ConnectionStatus.CONNECTING -> "connecting"
        else -> "unknown"
    }
}

internal fun mapFromPaymentStatus(status: PaymentStatus): String {
    return when (status) {
        PaymentStatus.NOT_READY -> "notReady"
        PaymentStatus.PROCESSING -> "processing"
        PaymentStatus.READY -> "ready"
        PaymentStatus.WAITING_FOR_INPUT -> "waitingForInput"
        else -> "unknown"
    }
}

internal fun mapFromSetupIntentStatus(status: SetupIntentStatus?): String {
    return when (status) {
        SetupIntentStatus.CANCELLED -> "canceled"
        SetupIntentStatus.REQUIRES_ACTION -> "requiresAction"
        SetupIntentStatus.REQUIRES_CONFIRMATION -> "requiresConfirmation"
        SetupIntentStatus.REQUIRES_PAYMENT_METHOD -> "requiresPaymentMethod"
        SetupIntentStatus.SUCCEEDED -> "succeeded"
        else -> "unknown"
    }
}

internal fun mapFromSimulateReaderUpdate(update: String): SimulateReaderUpdate {
    return when (update) {
        "available" -> SimulateReaderUpdate.UPDATE_AVAILABLE
        "none" -> SimulateReaderUpdate.NONE
        "random" -> SimulateReaderUpdate.RANDOM
        "required" -> SimulateReaderUpdate.REQUIRED
        else -> SimulateReaderUpdate.NONE
    }
}


private fun convertToUnixTimestamp(timestamp: Long): String = (timestamp * 1000).toString()

internal fun mapFromReaderSoftwareUpdate(update: ReaderSoftwareUpdate?): ReadableMap? =
    update?.let {
        WritableNativeMap().apply {
            putString("deviceSoftwareVersion", it.version)
            putString(
                "estimatedUpdateTime",
                mapFromUpdateTimeEstimate(it.timeEstimate)
            )
            putString("requiredAt", convertToUnixTimestamp(it.requiredAt.time))
        }
    }

internal fun mapFromUpdateTimeEstimate(time: ReaderSoftwareUpdate.UpdateTimeEstimate): String {
    return when (time) {
        ReaderSoftwareUpdate.UpdateTimeEstimate.FIVE_TO_FIFTEEN_MINUTES -> "estimate5To15Minutes"
        ReaderSoftwareUpdate.UpdateTimeEstimate.LESS_THAN_ONE_MINUTE -> "estimateLessThan1Minute"
        ReaderSoftwareUpdate.UpdateTimeEstimate.ONE_TO_TWO_MINUTES -> "estimate1To2Minutes"
        ReaderSoftwareUpdate.UpdateTimeEstimate.TWO_TO_FIVE_MINUTES -> "estimate2To5Minutes"
        else -> "unknown"
    }
}

internal fun mapToCartLineItems(cartLineItems: ReadableArray): List<CartLineItem> =
    cartLineItems.toArrayList().mapNotNull {
        (it as? HashMap<*, *>)?.let { item ->
            mapToCartLineItem(item)
        }
    }

internal fun mapToCartLineItem(cartLineItem: HashMap<*, *>): CartLineItem? {
    val displayName = cartLineItem["displayName"] as? String ?: return null
    val quantity = cartLineItem["quantity"] as? Double ?: return null
    val amount = cartLineItem["amount"] as? Double ?: return null

    return CartLineItem.Builder(displayName, amount.toInt(), quantity.toLong()).build()
}

internal fun mapFromRefund(refund: Refund): WritableMap = WritableNativeMap().apply {
    putIntOrNull(this, "amount", refund.amount?.toInt())
    putString("balanceTransaction", refund.balanceTransaction)
    putString("chargeId", refund.chargeId)
    putString("currency", refund.currency)
    putString("paymentIntentId", refund.paymentIntentId)
    putString("description", refund.description)
    putString("failureBalanceTransaction", refund.failureBalanceTransaction)
    putString("failureReason", refund.failureReason)
    putString("id", refund.id)
    putString("reason", refund.reason)
    putString("receiptNumber", refund.receiptNumber)
    putString("status", refund.status)
    putString("sourceTransferReversal", refund.sourceTransferReversal)
    putString("transferReversal", refund.transferReversal)
}

internal fun mapFromCardDetails(cardDetails: CardDetails?): WritableMap =
    WritableNativeMap().apply {
        putString("brand", cardDetails?.brand)
        putString("country", cardDetails?.country)
        putInt("expMonth", cardDetails?.expMonth ?: 0)
        putInt("expYear", cardDetails?.expYear ?: 0)
        putString("fingerprint", cardDetails?.fingerprint)
        putString("funding", cardDetails?.funding)
        putString("last4", cardDetails?.last4)
    }

internal fun mapFromPaymentMethod(paymentMethod: PaymentMethod): WritableMap =
    WritableNativeMap().apply {
        putString("id", paymentMethod.id)
        putString("customer", paymentMethod.customer)
        putBoolean("livemode", paymentMethod.livemode)
        putMap("cardDetails", mapFromCardDetails(paymentMethod.cardDetails))
    }
