# IVP Hosts — JavaFX 21 + Apache POI (sem Maven/Gradle)

## Dependências configuradas neste projeto

O módulo IntelliJ usa Java 25, JavaFX SDK 25.0.4 e Apache POI 5.5.1.
Os JARs do POI e suas dependências ficam em `lib/`, referenciada pela biblioteca
`apache-poi` do IntelliJ. Para restaurá-los, execute na pasta do projeto:

```powershell
.\download-dependencies.ps1
```

O script baixa versões fixas pelo Maven Central e verifica SHA512. Para artefatos
sem checksum SHA512 publicado, compara com uma cópia do espelho do Google.

## Requisitos
- Windows 11; Eclipse Adoptium JDK 21.0.10; JavaFX SDK 21.0.12; IntelliJ IDEA 2025.3.3.
- O projeto utiliza bibliotecas JAR locais, sem gerenciador de dependências.

## 1. Instalar as bibliotecas
1. Baixe o **JavaFX SDK 21.0.12 para Windows** em https://gluonhq.com/products/javafx/ e extraia em `C:\javafx-sdk-21.0.12` (a pasta deve conter `lib`).
2. Baixe o **Apache POI 5.4.1 binary distribution** (ou uma versão compatível com Java 21) em https://poi.apache.org/download.html . Caso a distribuição binária não esteja disponível, obtenha os JARs e suas dependências transitivas pelo Maven Central manualmente. Coloque **todos** os JARs necessários para `poi-ooxml` (incluindo `poi`, `poi-ooxml`, `poi-ooxml-lite`, `xmlbeans`, `commons-compress`, `commons-io`, `commons-collections4`, `commons-codec`, `commons-math3`, `log4j-api`, `SparseBitSet` e demais dependências requeridas pela versão) dentro da pasta `lib/` do projeto. A pasta `lib/` é automaticamente referenciada pelo módulo IntelliJ.

## 2. Abrir no IntelliJ
1. **File > Open**, selecione a pasta `IvpHostsJavaFX`.
2. **File > Project Structure > Project**: escolha o JDK 21.0.10. Caso apareça como ausente, use **Add SDK > JDK** e selecione a pasta do Adoptium.
3. **Project Structure > Modules > Dependencies**: confirme que a biblioteca de JARs da pasta `lib` está presente.
4. **Run > Edit Configurations > + > Application**:
   - Main class: `br.com.ivp.Launcher`
   - Use classpath of module: `IvpHostsJavaFX`
   - VM options: `--module-path "C:\javafx-sdk-21.0.12\lib" --add-modules javafx.controls,javafx.fxml`
   - JRE: JDK 21.0.10.
5. Execute `Launcher`.

**Importante:** o JavaFX SDK e os JARs do Apache POI não são redistribuídos neste ZIP. Antes de adicionar os JARs do POI, o projeto não compilará.

## 3. Planilha
Crie a pasta `%APPDATA%\Ivp_data` e o arquivo `data_hosts.xlsx` com cabeçalho na primeira linha:

| Hostname | IP Address | Location |
|---|---|---|
| srv-01 | 192.168.1.10 | Rio de Janeiro |
| srv-02 | 192.168.1.11 | São Paulo |

A aplicação lê a **primeira aba**, ignora a primeira linha e linhas vazias. A planilha é carregada na inicialização e pelo botão **Recarregar planilha**. Se o arquivo não existir ou estiver inválido, a mensagem de erro aparece no rodapé e no console.

## Estrutura
- `Launcher.java`: contém o `main` e somente inicia a aplicação.
- `MainApp.java`: JavaFX Application e carregamento do FXML.
- `MainController.java`: configura o TableView e aciona a leitura.
- `Host.java`: modelo de dados.
- `ExcelService.java`: leitura do Excel com Apache POI.
- `main-view.fxml`: interface.

Sem `module-info.java`: execução pelo classpath, com JavaFX no module-path.
