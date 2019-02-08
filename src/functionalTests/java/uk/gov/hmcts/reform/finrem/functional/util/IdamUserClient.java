package uk.gov.hmcts.reform.finrem.functional.util;

public interface IdamUserClient {

    String generateUserTokenWithNoRoles(String username, String password);

}
