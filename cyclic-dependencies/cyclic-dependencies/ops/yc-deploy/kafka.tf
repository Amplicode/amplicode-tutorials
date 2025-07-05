resource "yandex_mdb_kafka_cluster" "orders-cluster" {
    name        = "orders-cluster"
    environment = "PRESTABLE"
    network_id  = yandex_vpc_network.orders-vpc.id
    subnet_ids = [yandex_vpc_subnet.orders-vps-subnet.id]

    config {
        version          = "3.5"
        brokers_count    = 1
        zones = ["ru-central1-a"]
        assign_public_ip = false
        schema_registry  = false

        kafka {
            resources {
                resource_preset_id = "b3-c1-m4"
                disk_type_id       = "network-hdd"
                disk_size          = 32
            }
            kafka_config {
                compression_type                = "COMPRESSION_TYPE_ZSTD"
                log_flush_interval_messages     = 1024
                log_flush_interval_ms           = 1000
                log_flush_scheduler_interval_ms = 1000
                log_retention_bytes             = 1073741824
                log_retention_hours             = 168
                log_retention_minutes           = 10080
                log_retention_ms                = 86400000
                log_segment_bytes               = 134217728
                log_preallocate                 = false
                num_partitions                  = 10
                default_replication_factor      = 1
                message_max_bytes               = 1048588
                replica_fetch_max_bytes         = 1048576
                offsets_retention_minutes       = 10080
                ssl_cipher_suites = ["TLS_DHE_RSA_WITH_AES_128_CBC_SHA", "TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256"]
                sasl_enabled_mechanisms = ["SASL_MECHANISM_SCRAM_SHA_256", "SASL_MECHANISM_SCRAM_SHA_512"]
            }
        }
    }
}

resource "yandex_mdb_kafka_topic" "orderDelivery-topic" {
    cluster_id         = yandex_mdb_kafka_cluster.orders-cluster.id
    name               = "orderDelivery"
    partitions         = 1
    replication_factor = 1
}

resource "yandex_mdb_kafka_topic" "order-pay-topic" {
    cluster_id         = yandex_mdb_kafka_cluster.orders-cluster.id
    name               = "order-pay"
    partitions         = 1
    replication_factor = 1
}

resource "yandex_mdb_kafka_user" "orders-service-user" {
    cluster_id = yandex_mdb_kafka_cluster.orders-cluster.id
    name       = "kafkauser"
    password   = "kafkapassword"

    permission {
        role       = "ACCESS_ROLE_CONSUMER"
        topic_name = yandex_mdb_kafka_topic.orderDelivery-topic.name
    }

    permission {
        role       = "ACCESS_ROLE_PRODUCER"
        topic_name = yandex_mdb_kafka_topic.order-pay-topic.name
    }
}


locals {
    kafka_fqdn = tolist(yandex_mdb_kafka_cluster.orders-cluster.host)[0].name
    kafka_port = 9092
}
