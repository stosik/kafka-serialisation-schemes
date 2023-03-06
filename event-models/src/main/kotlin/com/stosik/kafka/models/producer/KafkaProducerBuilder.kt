package com.stosik.kafka.models.producer

import org.apache.kafka.clients.admin.AdminClientConfig.SECURITY_PROTOCOL_CONFIG
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG
import org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG
import org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG
import org.apache.kafka.common.serialization.Serializer

fun <K, V> kafkaProducer(block: KafkaProducerBuilder.() -> Unit): KafkaProducer<K, V> {
    return KafkaProducerBuilder().apply(block).build()
}

class KafkaProducerBuilder {

    private val configurationBuilder = ConfigurationBuilder()

    fun configuration(init: ConfigurationBuilder.() -> Unit) {
        configurationBuilder.init()
    }

    fun <K, V> build(): KafkaProducer<K, V> = KafkaProducer<K, V>(configurationBuilder.build())

    inner class ConfigurationBuilder {

        var bootstrapServers: List<String> = emptyList()
        var keyDeserializer: Class<out Serializer<*>>? = null
        var valueDeserializer: Class<out Serializer<*>>? = null
        var securityProtocol: String = "PLAINTEXT"
        var schemaRegistryUrl: String? = null

        fun bootstrapServers(value: List<String>) {
            bootstrapServers = bootstrapServers + value
        }

        fun keyDeserializer(value: Class<out Serializer<*>>) {
            keyDeserializer = value
        }

        fun valueDeserializer(value: Class<out Serializer<*>>) {
            valueDeserializer = value
        }

        fun securityProtocol(value: String) {
            securityProtocol = value
        }

        fun schemaRegistryUrl(value: String) {
            schemaRegistryUrl = value
        }

        fun build(): Map<String, Any?> {
            require(bootstrapServers.isNotEmpty()) { "Bootstrap servers must have at least one value" }

            return mapOf(
                BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
                KEY_SERIALIZER_CLASS_CONFIG to requireNotNull(keyDeserializer),
                VALUE_SERIALIZER_CLASS_CONFIG to requireNotNull(valueDeserializer),
                SECURITY_PROTOCOL_CONFIG to securityProtocol,
                "schema.registry.url" to schemaRegistryUrl
            )
        }
    }
}