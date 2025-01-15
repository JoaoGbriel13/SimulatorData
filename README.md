```markdown
# Simulator Data Integration

Este projeto integra dados de simulação do iRacing com o Google Sheets, utilizando uma API em Java Spring e um aplicativo em Python. Ele automatiza o processamento de dados, calculando médias e armazenando informações relevantes em uma planilha do Google.

## Funcionalidades

- Receber dados de simulação via API em Java Spring.
- Processar e calcular médias de consumo de combustível e tempo de volta.
- Registrar ou atualizar dados em uma planilha do Google Sheets usando a API Google Sheets.

## Estrutura do Projeto

- **API em Java Spring**: Responsável por receber e processar os dados do iRacing.
  - Endpoints RESTful para integração.
  - Manipulação de dados usando Spring Data JPA.
- **Aplicativo Python**: Responsável por extrair os dados do iRacing e enviá-los para a API.
- **Google Sheets Integration**: Integração com a API do Google Sheets para registrar os dados processados.

## Tecnologias Utilizadas

- **Backend**:
  - Java 17+
  - Spring Boot
  - Spring Data JPA
  - Google Sheets API
- **Frontend**:
  - Planilha do Google Sheets
- **Outros**:
  - Docker (para gerenciamento de containers)
  - Python (extração de dados do iRacing)

## Configuração e Uso

### Pré-requisitos

- **Java**: Versão 17 ou superior.
- **Python**: Versão 3.8 ou superior.
- **Docker**: Para gerenciamento do PostgreSQL.
- **Credenciais do Google Sheets**: Configurar as credenciais no formato JSON e definir na variável de ambiente `GOOGLE_CREDENTIALS`.

### Instalação

1. Clone o repositório:
   ```bash
   git clone https://github.com/seu-usuario/seu-repositorio.git
   cd seu-repositorio
   ```

2. Configure o ambiente:
   - Certifique-se de ter o arquivo de credenciais do Google Sheets e configure a variável de ambiente `GOOGLE_CREDENTIALS` com seu conteúdo.

3. Inicie o banco de dados usando Docker:
   ```bash
   docker-compose up -d
   ```

4. Compile e execute a aplicação:
   ```bash
   ./mvnw spring-boot:run
   ```

5. Execute o script Python para enviar dados para a API.

### Uso

- Envie dados de simulação para o endpoint da API:
  - Endpoint: `POST /data`
  - Payload:
    ```json
    {
      "driver": "Nome do piloto",
      "car": "Modelo do carro",
      "track": "Nome da pista",
      "fuelUsed": 3.5,
      "lapTimeNumeric": 125.367,
      "trackStateEnum": "dry"
    }
    ```

### Personalização

- **Google Sheets**: Atualize o ID da planilha em `GoogleService` para usar sua própria planilha.

## Contribuição

Contribuições são bem-vindas! Sinta-se à vontade para abrir issues e enviar pull requests.

## Licença

Este projeto é distribuído sob a licença MIT. Consulte o arquivo `LICENSE` para mais detalhes.
```
