resource "yandex_iam_service_account" "image-puller" {
    name = "orders-sa-image-puller"
}

resource "yandex_resourcemanager_folder_iam_member" "sa" {
    for_each = toset(["container-registry.images.puller"])
    folder_id = var.folder_id
    member    = "serviceAccount:${yandex_iam_service_account.image-puller.id}"
    role      = each.key
}

data "yandex_compute_image" "coi" {
    family = "container-optimized-image"
}

resource "yandex_compute_instance" "orders-compute-cloud" {
    platform_id        = "standard-v3"
    zone               = "ru-central1-a"
    service_account_id = yandex_iam_service_account.image-puller.id
    resources {
        cores  = 2
        memory = 2
    }

    boot_disk {
        initialize_params {
            image_id = data.yandex_compute_image.coi.id
        }
    }

    network_interface {
        subnet_id = yandex_vpc_subnet.orders-vps-subnet.id
        nat       = true
    }

    metadata = {
        "docker-compose" = templatefile(
            "${path.module}/files/orders-compute-cloud/docker-compose.yaml", {
                datasource_url      = "jdbc:postgresql://${local.db_master_fqdn}:${local.db_port}/${yandex_mdb_postgresql_database.db.name}"
                datasource_username = yandex_mdb_postgresql_user.db.name
                datasource_password = yandex_mdb_postgresql_user.db.password
                orders_version      = var.orders_version
                storage_url         = "https://${yandex_storage_bucket.products.bucket_domain_name}"
                storage_bucket      = yandex_storage_bucket.products.bucket
                storage_access_key  = yandex_iam_service_account_static_access_key.storage-editor-static-key.access_key
                storage_secret_key  = yandex_iam_service_account_static_access_key.storage-editor-static-key.secret_key
                kafka_url           = "${local.kafka_fqdn}:${local.kafka_port}"
                kafka_user          = yandex_mdb_kafka_user.orders-service-user.name
                kafka_password      = yandex_mdb_kafka_user.orders-service-user.password
            }
        )

        "user-data" = file("${path.module}/files/orders-compute-cloud/user-data.yaml")
    }
}
