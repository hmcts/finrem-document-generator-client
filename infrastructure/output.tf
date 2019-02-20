output "vaultName" {
    value = "${local.vaultName}"
}

output "vaultUri" {
    value = "${local.vaultUri}"
}

output "environment_name" {
    value = "${local.local_env}"
}

output "docmosis_vault" {
    value = "${local.docmosisVaultUri}"
}

output "docmosis_api_access_key" {
    value = "${data.azurerm_key_vault_secret.docmosis-api-access-key}"
}
