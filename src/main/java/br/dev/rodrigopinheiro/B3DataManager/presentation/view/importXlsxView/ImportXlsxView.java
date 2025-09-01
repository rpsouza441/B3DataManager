package br.dev.rodrigopinheiro.B3DataManager.presentation.view.importXlsxView;

import br.dev.rodrigopinheiro.B3DataManager.application.command.upload.ProcessUploadCommand;
import br.dev.rodrigopinheiro.B3DataManager.application.result.upload.UploadProcessingResult;
import br.dev.rodrigopinheiro.B3DataManager.application.security.SecurityService;
import br.dev.rodrigopinheiro.B3DataManager.application.service.ErrorService;
import br.dev.rodrigopinheiro.B3DataManager.application.usecase.upload.ProcessUploadUseCase;
import br.dev.rodrigopinheiro.B3DataManager.domain.valueobject.UsuarioId;
import br.dev.rodrigopinheiro.B3DataManager.presentation.view.components.ToastNotification;
import br.dev.rodrigopinheiro.B3DataManager.presentation.view.factory.MessageUtils;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
import com.vaadin.flow.server.streams.UploadHandler;

import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.io.ByteArrayInputStream;
import java.util.Locale;
import java.util.ResourceBundle;

@Route("importar")
@Menu(order = 4, icon = LineAwesomeIconUrl.FILE_EXCEL_SOLID, title = "Importar")
@PermitAll
@Slf4j
public class ImportXlsxView extends VerticalLayout implements HasDynamicTitle, HasUrlParameter<Long> {

    private final ProcessUploadUseCase processUploadUseCase;
    private final ErrorService errorService;
    private final MessageUtils messageUtils;
    private String title = "";
    Locale currentLocale;

    public ImportXlsxView(ProcessUploadUseCase processUploadUseCase,
                         ErrorService errorService) {
        this.processUploadUseCase = processUploadUseCase;
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

        // Declarar componentes no escopo correto
        ProgressBar progressBar = new ProgressBar();
        progressBar.setVisible(false);
        progressBar.setIndeterminate(true);

        // Botão de download personalizado
        Button downloadErrorFileButton = new Button(messageUtils.getString("importar.download.error"));
        downloadErrorFileButton.addClassName("download-error-button");
        downloadErrorFileButton.setEnabled(false);
        
        // Configuração do upload com UploadHandler.inMemory (API Vaadin 24.8+)
        UploadHandler uploadHandler = UploadHandler.inMemory((meta, bytes) -> {
            log.info("Processando arquivo: {} ({} bytes)", meta.fileName(), bytes.length);
            progressBar.setVisible(true);
            
            try {
                Long userId = SecurityService.getAuthenticatedUserId();
                if (userId == null) {
                    log.error("ID do usuário autenticado é nulo.");
                    ToastNotification.showError(messageUtils.getString("importar.user.notAuthenticated"));
                    progressBar.setVisible(false);
                    return;
                }
                
                // Processar upload usando Use Case dedicado
                ProcessUploadCommand command = new ProcessUploadCommand(bytes, meta.fileName(), new UsuarioId(userId));
                UploadProcessingResult result = processUploadUseCase.execute(command);
                
                if (result.hasErrors()) {
                    log.warn("Erros encontrados durante o processamento: {}", result.getErrorRows());
                    
                    // Criar link de download com DownloadHandler usando relatório gerado
                    DownloadHandler downloadHandler = createErrorFileDownloadHandler(result.errorReportStream());
                    Anchor downloadLink = new Anchor(downloadHandler, messageUtils.getString("importar.download.error"));
                    downloadLink.addClassName("download-error-link");
                    downloadLink.getElement().setAttribute("router-ignore", "true");
                    
                    formContainer.replace(downloadErrorFileButton, downloadLink);
                    ToastNotification.showWarning(messageUtils.getFormattedString("importar.process.errors", result.getErrorRows()));
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
        
        Upload upload = new Upload(uploadHandler);
        upload.setDropLabel(new Div(new Text(messageUtils.getString("importar.upload.dropLabel"))));
        upload.setAcceptedFileTypes(".xlsx");
        upload.setMaxFiles(1);
        
        // Configuração de internacionalização do upload (Vaadin 24.8+)
        UploadI18N i18n = new UploadI18N();
        
        // Inicializar objetos internos antes de usar
        UploadI18N.AddFiles addFiles = new UploadI18N.AddFiles();
        addFiles.setOne("Selecionar arquivo...");
        i18n.setAddFiles(addFiles);
        
        UploadI18N.DropFiles dropFiles = new UploadI18N.DropFiles();
        dropFiles.setOne("Arraste o arquivo Excel aqui");
        i18n.setDropFiles(dropFiles);
        
        UploadI18N.Error error = new UploadI18N.Error();
        error.setFileIsTooBig("Arquivo muito grande");
        error.setIncorrectFileType("Tipo de arquivo incorreto. Use apenas .xlsx");
        i18n.setError(error);
        
        upload.setI18n(i18n);

        // Configura estilo do label no botão de upload
        upload.getElement().executeJs(
                "this.shadowRoot.querySelector('vaadin-button').setAttribute('part', 'label')"
        );


        formContainer.add(upload, progressBar, downloadErrorFileButton);
        add(formContainer);
    }

    private DownloadHandler createErrorFileDownloadHandler(ByteArrayInputStream errorReportStream) {
        log.info("Criando DownloadHandler para arquivo de erros.");
        
        return DownloadHandler.fromInputStream(event -> {
            log.info("Fornecendo arquivo de erros para download.");
            
            return new DownloadResponse(
                errorReportStream,
                "relatorio_erros.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                -1 // Tamanho desconhecido
            );
        });
    }

    @Override
    public String getPageTitle() {
        return title;
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Long parameter) {
        title = "Importar Excel | B3 Data Manager";
        if (parameter != null) {
            title = title + " - " + parameter;
        }
        log.info("Título da página configurado como: {}", title);
    }
}
