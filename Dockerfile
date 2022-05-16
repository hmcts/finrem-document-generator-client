ARG APP_INSIGHTS_AGENT_VERSION=2.5.1
ARG PLATFORM=""
FROM hmctspublic.azurecr.io/base/java${PLATFORM}:17-distroless

# Mandatory!
ENV APP finrem-document-generator.jar

COPY build/libs/$APP /opt/app/
COPY lib/AI-Agent.xml /opt/app/

EXPOSE 4009

CMD ["finrem-document-generator.jar"]
