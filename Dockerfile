FROM tomcat:11.0-jre11
COPY target/ROOT.war /usr/local/tomcat/webapps/ROOT.war
