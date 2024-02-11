FROM bellsoft/liberica-openjdk-alpine-musl:11 AS client-build
COPY . src/
WORKDIR /src
RUN javac Client/UnifiedClient.java

FROM bellsoft/liberica-openjdk-alpine-musl:11 AS server-build
COPY . src/
WORKDIR /src
RUN javac Server/UnifiedServer.java
# cmd to run server locally - java server.ServerApp 1111 5555
CMD ["java", "Server/UnifiedServer", "1111", "5555"]