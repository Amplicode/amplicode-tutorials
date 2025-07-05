# resource "yandex_kubernetes_cluster" "orders-terraform" {
#     name        = "name"
#     description = "description"
#
#     network_id = "${yandex_vpc_network.network_resource_name.id}"
#
#     master {
#         version = "1.17"
#         zonal {
#             zone      = "${yandex_vpc_subnet.subnet_resource_name.zone}"
#             subnet_id = "${yandex_vpc_subnet.subnet_resource_name.id}"
#         }
#
#         public_ip = true
#
#         security_group_ids = ["${yandex_vpc_security_group.security_group_name.id}"]
#
#         maintenance_policy {
#             auto_upgrade = true
#
#             maintenance_window {
#                 start_time = "15:00"
#                 duration   = "3h"
#             }
#         }
#
#         master_logging {
#             enabled = true
#             log_group_id = "${yandex_logging_group.log_group_resoruce_name.id}"
#             kube_apiserver_enabled = true
#             cluster_autoscaler_enabled = true
#             events_enabled = true
#             audit_enabled = true
#         }
#     }
#
#     service_account_id      = "${yandex_iam_service_account.service_account_resource_name.id}"
#     node_service_account_id = "${yandex_iam_service_account.node_service_account_resource_name.id}"
#
#
#     release_channel = "RAPID"
#     network_policy_provider = "CALICO"
#
#     kms_provider {
#         key_id = "${yandex_kms_symmetric_key.kms_key_resource_name.id}"
#     }
# }
#
# resource "yandex_iam_service_account" "sa" {
#     name = "orders-sa"
# }
#
# resource "yandex_resourcemanager_folder_iam_member" "sa" {
#     for_each = toset(["editor"])
#     folder_id = var.folder_id
#     member    = "serviceAccount:${yandex_iam_service_account.sa.id}"
#     role      = each.key
# }
#
