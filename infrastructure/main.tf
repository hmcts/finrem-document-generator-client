provider "azurerm" {
    version = "1.44.0"
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
  send_letter_service_baseurl       = "http://rpe-send-letter-service-${local.local_env}.service.core-compute-${local.local_env}.internal"
}

module "finrem-dgcs" {
  source                          = "git@github.com:hmcts/cnp-module-webapp?ref=master"
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
    PDF_SERVICE_BASEURL                                   = "${data.azurerm_key_vault_secret.docmosis_endpoint.value}"
    PDF_SERVICE_HEALTH_URL                                = "${data.azurerm_key_vault_secret.docmosis_endpoint.value}"
    EVIDENCE_MANAGEMENT_CLIENT_API_BASEURL                = "${local.evidence_management_client_api_url}"
    EVIDENCE_MANAGEMENT_CLIENT_API_HEALTH_ENDPOINT        = "${var.evidence_management_client_api_health_endpoint}"
    PDF_SERVICE_ACCESS_KEY                                = "${data.azurerm_key_vault_secret.pdf-service-access-key.value}"
    SWAGGER_ENABLED                                       = "${var.swagger_enabled}"
    OAUTH2_CLIENT_FINREM                                  = "${data.azurerm_key_vault_secret.idam-secret.value}"
    AUTH_PROVIDER_SERVICE_CLIENT_BASEURL                  = "${local.idam_s2s_url}"
    AUTH_PROVIDER_SERVICE_CLIENT_MICROSERVICE             = "${var.auth_provider_service_client_microservice}"
    AUTH_PROVIDER_SERVICE_CLIENT_KEY                      = "${data.azurerm_key_vault_secret.finrem-doc-s2s-auth-secret.value}"
    WEBSITE_DNS_SERVER                                    = "${var.dns_server}",
    SEND_LETTER_SERIVCE_BASEURL                           = "${local.send_letter_service_baseurl}"
      DOCUMENT_MIME_TYPES                                 = "${var.document_mime_types}"
  }
}

data "azurerm_key_vault" "finrem_key_vault" {
    name                = "${local.vaultName}"
    resource_group_name = "${local.vaultName}"
}

data "azurerm_key_vault_secret" "pdf-service-access-key" {
    name         = "docmosis-api-key"
    key_vault_id = "${data.azurerm_key_vault.finrem_key_vault.id}"
}

data "azurerm_key_vault_secret" "docmosis_endpoint" {
    name         = "docmosis-endpoint"
    key_vault_id = "${data.azurerm_key_vault.finrem_key_vault.id}"
}

data "azurerm_key_vault_secret" "finrem-doc-s2s-auth-secret" {
    name         = "finrem-doc-s2s-auth-secret"
    key_vault_id = "${data.azurerm_key_vault.finrem_key_vault.id}"
}

data "azurerm_key_vault_secret" "idam-secret" {
    name         = "idam-secret"
    key_vault_id = "${data.azurerm_key_vault.finrem_key_vault.id}"
}
