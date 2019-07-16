output "vaultName" {
    value = "${local.vaultName}"
}

output "environment_name" {
    value = "${local.local_env}"
}

output "pdf-service-access-key" {
    value = "${data.azurerm_key_vault_secret.pdf-service-access-key.value}"
}

output "pdf-service-base-url" {
    value = "${data.azurerm_key_vault_secret.docmosis_endpoint.value}"
}

output "oauth2_client_finrem" {
    value = "${data.azurerm_key_vault_secret.idam-secret.value}"
}

output "idam_s2s_url" {
    value = "${local.idam_s2s_url}"
}

output "send_letter_service_baseurl" {
    value = "${local.send_letter_service_baseurl}"
}
