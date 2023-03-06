package com.stosik.kafka.models.consumer

import com.google.protobuf.GeneratedMessageV3
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializerConfig.SPECIFIC_PROTOBUF_VALUE_TYPE
import org.apache.kafka.clients.admin.AdminClientConfig.SECURITY_PROTOCOL_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG
import org.apache.kafka.common.serialization.Deserializer

fun <K, V> kafkaConsumer(block: KafkaConsumerBuilder.() -> Unit): KafkaConsumer<K, V> {
    return KafkaConsumerBuilder().apply(block).build()
}

class KafkaConsumerBuilder {

    private val configurationBuilder = ConfigurationBuilder()
    private val topicsBuilder = TopicsBuilder()

    fun configuration(init: ConfigurationBuilder.() -> Unit) {
        configurationBuilder.init()
    }

    fun topics(init: TopicsBuilder.() -> Unit) {
        topicsBuilder.init()
    }

    fun <K, V> build(): KafkaConsumer<K, V> = KafkaConsumer<K, V>(configurationBuilder.build())
        .also { it.subscribe(topicsBuilder.build()) }

    inner class TopicsBuilder {
        var topics: List<String> = emptyList()

        fun topic(topic: String) {
            topics = topics + topic
        }

        fun build(): List<String> = topics
    }

    inner class ConfigurationBuilder {

        var bootstrapServers: List<String> = emptyList()
        var autoOffsetReset: String = "earliest"
        var keyDeserializer: Class<out Deserializer<*>>? = null
        var valueDeserializer: Class<out Deserializer<*>>? = null
        var groupId: String? = null
        var securityProtocol: String = "PLAINTEXT"
        var protobufClass: Class<out GeneratedMessageV3>? = null
        var schemaRegistryUrl: String? = null

        fun bootstrapServers(value: List<String>) {
            bootstrapServers = bootstrapServers + value
        }

        fun autoOffsetReset(value: String) {
            autoOffsetReset = value
        }

        fun keyDeserializer(value: Class<out Deserializer<*>>) {
            keyDeserializer = value
        }

        fun valueDeserializer(value: Class<out Deserializer<*>>) {
            valueDeserializer = value
        }

        fun groupId(value: String) {
            groupId = value
        }

        fun securityProtocol(value: String) {
            securityProtocol = value
        }

        fun schemaRegistryUrl(value: String) {
            schemaRegistryUrl = value
        }

        fun protobufClass(value: Class<out GeneratedMessageV3>) {
            protobufClass = value
        }

        fun build(): Map<String, Any?> {
            require(bootstrapServers.isNotEmpty()) { "Bootstrap servers must have at least one value" }
            return mapOf(
                BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
                AUTO_OFFSET_RESET_CONFIG to autoOffsetReset,
                KEY_DESERIALIZER_CLASS_CONFIG to requireNotNull(keyDeserializer),
                VALUE_DESERIALIZER_CLASS_CONFIG to requireNotNull(valueDeserializer),
                SPECIFIC_PROTOBUF_VALUE_TYPE to protobufClass,
                GROUP_ID_CONFIG to requireNotNull(groupId),
                SECURITY_PROTOCOL_CONFIG to securityProtocol,
                "schema.registry.url" to schemaRegistryUrl,
            ).filterValues { it != null }
        }
    }
}