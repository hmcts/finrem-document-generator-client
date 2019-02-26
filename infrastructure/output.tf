output "vaultName" {
    value = "${local.vaultName}"
}

output "environment_name" {
    value = "${local.local_env}"
}

output "docmosis_vault" {
    value = "${local.docmosisVaultUri}"
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
