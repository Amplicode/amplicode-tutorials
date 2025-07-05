terraform {
    required_providers {
        yandex = {
            source = "yandex-cloud/yandex"
        }
    }
}

provider "yandex" { folder_id = var.folder_id }

data "yandex_vpc_network" "default" {
    name = "default"
}

data "yandex_vpc_subnet" "default-ru-central1-d" {
    name = "default-ru-central1-d"
}
