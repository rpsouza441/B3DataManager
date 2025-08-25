package br.dev.rodrigopinheiro.B3DataManager.presentation.view.importXlsxView;

import br.dev.rodrigopinheiro.B3DataManager.application.security.SecurityService;
import br.dev.rodrigopinheiro.B3DataManager.application.service.ErrorService;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.excel.ExcelFileImporter;
import br.dev.rodrigopinheiro.B3DataManager.presentation.view.components.ToastNotification;
import br.dev.rodrigopinheiro.B3DataManager.presentation.view.factory.MessageUtils;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@Route("importar")
@Menu(order = 4, icon = LineAwesomeIconUrl.FILE_EXCEL_SOLID, title = "Importar")
@PermitAll
@Slf4j
public class ImportXlsxView extends VerticalLayout implements HasDynamicTitle {

    private final ExcelFileImporter excelFileImporter;
    private final ErrorService errorService;
    private final MessageUtils messageUtils;
    private String title = "";
    Locale currentLocale;

    public ImportXlsxView(ExcelFileImporter excelFileImporter, ErrorService errorService) {
        this.excelFileImporter = excelFileImporter;
        this.errorService = errorService;
        currentLocale = getUI().map(ui -> ui.getSession().getLocale()).orElse(Locale.getDefault());
        ResourceBundle messages = ResourceBundle.getBundle("messages", currentLocale);
        this.messageUtils = new MessageUtils(messages);

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        addClassName("import-xlsx-view");

        setupUI();
    }

    private void setupUI() {
        log.info("Iniciando configuração da interface de upload.");

        // Container principal com estilo
        Div formContainer = new Div();
        formContainer.addClassName("form-container");

        // Buffer de memória para o upload
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setDropLabel(new Div(new Text(messageUtils.getString("importar.upload.dropLabel"))));
        upload.setAcceptedFileTypes(".xlsx");
        upload.setMaxFiles(1);

        // Configura estilo do label no botão de upload
        upload.getElement().executeJs(
                "this.shadowRoot.querySelector('vaadin-button').setAttribute('part', 'label')"
        );

        ProgressBar progressBar = new ProgressBar();
        progressBar.setVisible(false);
        progressBar.setIndeterminate(true);

        // Botão de download personalizado
        Button downloadErrorFileButton = new Button(messageUtils.getString("importar.download.error"));
        downloadErrorFileButton.addClassName("download-error-button");
        downloadErrorFileButton.setEnabled(false);

        upload.addSucceededListener(event -> {
            log.info("Arquivo carregado com sucesso: {}", event.getFileName());
            progressBar.setVisible(true);

            try (InputStream inputStream = buffer.getInputStream()) {
                Long userId = SecurityService.getAuthenticatedUserId();
                if (userId == null) {
                    log.error("ID do usuário autenticado é nulo.");
                    ToastNotification.showError(messageUtils.getString("importar.user.notAuthenticated"));
                    progressBar.setVisible(false);
                    return;
                }

                List<String> errors = excelFileImporter.processFile(inputStream, userId, currentLocale);

                if (!errors.isEmpty()) {
                    log.warn("Erros encontrados durante o processamento: {}", errors.size());
                    StreamResource resource = createErrorFile(errors);

                    // Usa um componente Anchor para gerenciar o download
                    Anchor downloadLink = new Anchor(resource, messageUtils.getString("importar.download.error"));
                    downloadLink.getElement().setAttribute("download", "relatorio_erros.xlsx");
                    downloadLink.addClassName("download-error-link");

                    formContainer.replace(downloadErrorFileButton, downloadLink); // Substitui o botão pelo link
                    ToastNotification.showWarning(messageUtils.getFormattedString("importar.process.errors", errors.size()));
                } else {
                    log.info("Processamento concluído sem erros.");
                    ToastNotification.showInfo(messageUtils.getString("importar.success"));
                }
            } catch (Exception e) {
                log.error("Erro ao processar o arquivo: {}", e.getMessage(), e);
                ToastNotification.showError(errorService.getErrorMessage("importar.process.error", e.getMessage()));
            } finally {
                progressBar.setVisible(false);
                log.info("Processo de upload finalizado.");
            }
        });



        formContainer.add(upload, progressBar, downloadErrorFileButton);
        add(formContainer);
    }

    private StreamResource createErrorFile(List<String> errors) {
        log.info("Gerando arquivo de erros com {} entradas.", errors.size());
        return new StreamResource("relatorio_erros.xlsx", () -> {
            ByteArrayInputStream stream = excelFileImporter.generateErrorFile(errors, currentLocale);
            log.info("Arquivo de erros gerado com sucesso.");
            return stream;
        });
    }

    @Override
    public String getPageTitle() {
        return title;
    }
}
