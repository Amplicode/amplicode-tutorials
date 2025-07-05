resource "yandex_mdb_postgresql_cluster" "db" {
    name        = "db"
    environment = "PRODUCTION"
    network_id  = yandex_vpc_network.orders-vpc.id

    config {
        version = "17"
        resources {
            resource_preset_id = "s3-c2-m8"
            disk_type_id       = "network-ssd"
            disk_size          = 10
        }

        backup_retain_period_days = 7
        backup_window_start {
            hours = 0
        }

    }

    host {
        name      = "host-1"
        zone      = "ru-central1-a"
        subnet_id = yandex_vpc_subnet.orders-vps-subnet.id
    }
}

resource "yandex_mdb_postgresql_user" "db" {
    cluster_id = yandex_mdb_postgresql_cluster.db.id
    name       = "ordersdbadmin"
    password   = "ordersdbadmin"
}

resource "yandex_mdb_postgresql_database" "db" {
    cluster_id = yandex_mdb_postgresql_cluster.db.id
    name       = "orders"
    owner      = yandex_mdb_postgresql_user.db.name
    lc_collate = "C"
    lc_type    = "C"
}

locals {
    db_master_fqdn = "c-${yandex_mdb_postgresql_cluster.db.id}.rw.mdb.yandexcloud.net"
    db_port        = 6432
}

