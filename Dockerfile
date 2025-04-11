FROM tomcat:9-jre17-temurin

# Create a non-root user and group

RUN rm -rf "$CATALINA_HOME"/webapps/*
COPY sabdatab.properties log4j2.xml $CATALINA_HOME/webapps/
COPY target/*.war $CATALINA_HOME/webapps/ROOT.war

# Setup a non-root user context (security)
RUN addgroup -g 1000 tomcatgroup; \
    adduser -D -s / -u 1000 tomcatuser -G tomcatgroup; \
    chown -R tomcat:tomcat "$CATALINA_HOME"

USER 1000

# Start Tomcat
CMD ["catalina.sh", "run"]