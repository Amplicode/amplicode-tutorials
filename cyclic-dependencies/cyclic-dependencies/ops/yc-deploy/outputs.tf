output "orders-compute-cloud-public-ip" {
    value = yandex_compute_instance.orders-compute-cloud.network_interface[0].nat_ip_address
}

