# Temporary fix for template API version error on deployment
provider "azurerm" {
    version = "1.19.0"
}

locals {
  ase_name = "${data.terraform_remote_state.core_apps_compute.ase_name[0]}"
  local_env = "${(var.env == "preview" || var.env == "spreview") ? (var.env == "preview" ) ? "aat" : "saat" : var.env}"

  evidence_management_client_api_url = "http://${var.evidence_management_client_api_url_part}-${local.local_env}.service.core-compute-${local.local_env}.internal"

  idam_s2s_url   = "http://${var.idam_s2s_url_prefix}-${local.local_env}.service.core-compute-${local.local_env}.internal"

  previewVaultName = "${var.reform_team}-aat"
  nonPreviewVaultName = "${var.reform_team}-${var.env}"
  vaultName = "${var.env == "preview" ? local.previewVaultName : local.nonPreviewVaultName}"
  vaultUri = "${data.azurerm_key_vault.finrem_key_vault.vault_uri}"

  asp_name = "${var.env == "prod" ? "finrem-dgcs-prod" : "${var.raw_product}-${var.env}"}"
  asp_rg = "${var.env == "prod" ? "finrem-dgcs-prod" : "${var.raw_product}-${var.env}"}"

  docmosisVaultName = "${var.docmosis_vault}"
  docmosisVaultUri  = "${data.azurerm_key_vault.docmosis_key_vault.vault_uri}"
}

module "finrem-dgcs" {
  source                          = "git@github.com:hmcts/moj-module-webapp.git?ref=master"
  product                         = "${var.product}-${var.component}"
  location                        = "${var.location}"
  env                             = "${var.env}"
  ilbIp                           = "${var.ilbIp}"
  subscription                    = "${var.subscription}"
  appinsights_instrumentation_key = "${var.appinsights_instrumentation_key}"
  capacity                        = "${var.capacity}"
  is_frontend                     = false
  common_tags                     = "${var.common_tags}"
  asp_name                        = "${local.asp_name}"
  asp_rg                          = "${local.asp_rg}"


  app_settings = {
    REFORM_SERVICE_NAME                                   = "${var.reform_service_name}"
    REFORM_TEAM                                           = "${var.reform_team}"
    REFORM_ENVIRONMENT                                    = "${var.env}"
    PDF_SERVICE_BASEURL                                   = "${var.pdf_service_url}"
    PDF_SERVICE_HEALTH_URL                                = "${var.pdf_service_health_url}"
    EVIDENCE_MANAGEMENT_CLIENT_API_BASEURL                = "${local.evidence_management_client_api_url}"
    EVIDENCE_MANAGEMENT_CLIENT_API_HEALTH_ENDPOINT        = "${var.evidence_management_client_api_health_endpoint}"
    PDF_SERVICE_ACCESS_KEY                                = "${data.azurerm_key_vault_secret.pdf-service-access-key.value}"
    SWAGGER_ENABLED                                       = "${var.swagger_enabled}"
    OAUTH2_CLIENT_FINREM                                  = "${data.azurerm_key_vault_secret.idam-secret.value}"
    AUTH_PROVIDER_SERVICE_CLIENT_BASEURL                  = "${local.idam_s2s_url}"
    AUTH_PROVIDER_SERVICE_CLIENT_KEY                      = "${data.azurerm_key_vault_secret.finrem-doc-s2s-auth-secret.value}"
    DOCMOSIS_ACCESS_KEY                                   = "${data.azurerm_key_vault_secret.docmosis-api-access-key.value}"
  }
}

data "azurerm_key_vault" "finrem_key_vault" {
    name                = "${local.vaultName}"
    resource_group_name = "${local.vaultName}"
}

data "azurerm_key_vault" "docmosis_key_vault" {
    name                = "${local.docmosisVaultName}"
    resource_group_name = "${local.docmosisVaultName}"
}

data "azurerm_key_vault_secret" "docmosis-api-access-key" {
    name      = "${var.docmosis-api-access-key}"
    vault_uri = "${data.azurerm_key_vault.docmosis_key_vault.vault_uri}"
}

data "azurerm_key_vault_secret" "pdf-service-access-key" {
    name      = "pdf-service-access-key"
    vault_uri = "${data.azurerm_key_vault.finrem_key_vault.vault_uri}"
}

data "azurerm_key_vault_secret" "finrem-doc-s2s-auth-secret" {
    name      = "finrem-doc-s2s-auth-secret"
    vault_uri = "${data.azurerm_key_vault.finrem_key_vault.vault_uri}"
}

data "azurerm_key_vault_secret" "idam-secret" {
    name      = "idam-secret"
    vault_uri = "${data.azurerm_key_vault.finrem_key_vault.vault_uri}"
}
