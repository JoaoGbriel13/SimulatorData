# Etapa 1: Build
FROM eclipse-temurin:21-jdk AS build

# Configura o diretório de trabalho no container
WORKDIR /app

# Copia o arquivo pom.xml e outros arquivos necessários para o build
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Realiza o download das dependências para cache
RUN ./mvnw dependency:resolve

# Copia o código fonte para dentro do container
COPY src ./src

# Compila o projeto e cria o JAR
RUN ./mvnw clean package -DskipTests

# Etapa 2: Runtime
FROM eclipse-temurin:21-jre

# Configura o diretório de trabalho
WORKDIR /app

# Copia o JAR gerado na etapa de build
COPY --from=build /app/target/*.jar app.jar

# Porta padrão usada pela aplicação
EXPOSE 8080

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
