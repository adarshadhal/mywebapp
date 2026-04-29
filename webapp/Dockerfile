FROM tomcat:latest
COPY /target/*war /usr/local/tomcat/webapps/
EXPOSE 8080
CMD ["/usr/local/tomcat/bin/catlin.sh","run"]
