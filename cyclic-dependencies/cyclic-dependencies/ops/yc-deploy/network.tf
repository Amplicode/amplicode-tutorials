resource "yandex_vpc_network" "orders-vpc" {

}

resource "yandex_vpc_security_group" "orders-vps-sec-group" {
    network_id = yandex_vpc_network.orders-vpc.id
    name       = "orders-vps-sec-group"
}

resource "yandex_vpc_security_group_rule" "kafka-rule-ingress" {
    security_group_id      = yandex_vpc_security_group.orders-vps-sec-group.id
    security_group_binding = yandex_vpc_security_group.orders-vps-sec-group.id
    direction              = "ingress"
    protocol               = "TCP"
    from_port              = 9091
    to_port                = 9092

}

resource "yandex_vpc_subnet" "orders-vps-subnet" {
    v4_cidr_blocks = ["10.129.0.0/24"]
    zone       = "ru-central1-a"
    network_id = yandex_vpc_network.orders-vpc.id
}

