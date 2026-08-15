package com.agon.app.data

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

class MqttBus(
    private val onEnvelope: (Envelope) -> Unit,
) {
    enum class Link { Offline, Connecting, Live }

    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
    private val worker = Executors.newSingleThreadExecutor()
    private val started = AtomicBoolean(false)

    private val _link = MutableStateFlow(Link.Offline)
    val link: StateFlow<Link> = _link.asStateFlow()

    @Volatile
    private var client: MqttClient? = null

    fun start(clientIdSuffix: String) {
        if (!started.compareAndSet(false, true)) return
        worker.execute { connectLoop("snwy-${clientIdSuffix.take(18)}-${UUID.randomUUID().toString().take(6)}") }
    }

    fun publish(envelope: Envelope) {
        worker.execute {
            try {
                val payload = json.encodeToString(Envelope.serializer(), envelope).toByteArray(Charsets.UTF_8)
                val message = MqttMessage(payload).apply {
                    qos = 1
                    isRetained = false
                }
                client?.publish(APP_CHANNEL, message)
            } catch (e: Exception) {
                Log.w(TAG, "publish failed: ${e.message}")
            }
        }
    }

    fun shutdown() {
        worker.execute {
            try {
                client?.disconnect()
                client?.close()
            } catch (_: Exception) {
            }
            client = null
            _link.value = Link.Offline
        }
    }

    private fun connectLoop(clientId: String) {
        val servers = listOf(
            "tcp://broker.hivemq.com:1883",
            "tcp://broker.emqx.io:1883",
            "tcp://test.mosquitto.org:1883",
        )
        var index = 0
        while (started.get()) {
            val uri = servers[index % servers.size]
            index++
            try {
                _link.value = Link.Connecting
                val mqtt = MqttClient(uri, clientId, MemoryPersistence())
                mqtt.setCallback(object : MqttCallbackExtended {
                    override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                        _link.value = Link.Live
                        try {
                            mqtt.subscribe(APP_CHANNEL, 1)
                        } catch (e: Exception) {
                            Log.w(TAG, "subscribe: ${e.message}")
                        }
                    }

                    override fun connectionLost(cause: Throwable?) {
                        _link.value = Link.Connecting
                    }

                    override fun messageArrived(topic: String?, message: MqttMessage?) {
                        val raw = message?.payload?.toString(Charsets.UTF_8) ?: return
                        try {
                            val env = json.decodeFromString(Envelope.serializer(), raw)
                            if (env.app == APP_MARK) onEnvelope(env)
                        } catch (e: Exception) {
                            Log.w(TAG, "bad envelope: ${e.message}")
                        }
                    }

                    override fun deliveryComplete(token: IMqttDeliveryToken?) = Unit
                })
                val options = MqttConnectOptions().apply {
                    isAutomaticReconnect = true
                    isCleanSession = true
                    connectionTimeout = 20
                    keepAliveInterval = 30
                    mqttVersion = MqttConnectOptions.MQTT_VERSION_3_1_1
                }
                mqtt.connect(options)
                client = mqtt
                if (!mqtt.isConnected) {
                    mqtt.subscribe(APP_CHANNEL, 1)
                }
                _link.value = Link.Live
                return
            } catch (e: Exception) {
                Log.w(TAG, "connect $uri failed: ${e.message}")
                _link.value = Link.Offline
                try {
                    Thread.sleep(2500)
                } catch (_: InterruptedException) {
                    return
                }
            }
        }
    }

    companion object {
        private const val TAG = "MqttBus"
    }
}
