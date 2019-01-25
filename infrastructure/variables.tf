variable "reform_service_name" {
  default = "dgcs"
}

variable "reform_team" {
  default = "finrem"
}

variable "capacity" {
    default = "1"
}

variable "component" {
  type = "string"
}

variable "env" {
  type = "string"
}

variable "product" {
  type = "string"
}

variable "raw_product" {
  default = "finrem"
}

variable "client_id" {
  description = "(Required) The object ID of a user, service principal or security group in the Azure Active Directory tenant for the vault. The object ID must be unique for the list of access policies. This is usually sourced from environment variables and not normally required to be specified."
}

variable "jenkins_AAD_objectId" {
  type        = "string"
  description = "(Required) The Azure AD object ID of a user, service principal or security group in the Azure Active Directory tenant for the vault. The object ID must be unique for the list of access policies."
}

variable "appinsights_instrumentation_key" {
    description = "Instrumentation key of the App Insights instance this webapp should use. Module will create own App Insights resource if this is not provided"
    default = ""
}

variable "evidence_management_client_api_url_part" {
  default = "finrem-emca"
}

variable "evidence_management_client_api_health_endpoint" {
  default = "/health"
}

variable "subscription" {}

variable "location" {
  type    = "string"
  default = "UK South"
}

variable "ilbIp" {}

variable "vault_env" {}

variable "common_tags" {
    type = "map"
}
