package br.dev.rodrigopinheiro.B3DataManager.presentation.view.operacao;

import br.dev.rodrigopinheiro.B3DataManager.application.command.operacao.CountOperacoesCommand;
import br.dev.rodrigopinheiro.B3DataManager.application.command.operacao.ListOperacoesCommand;
import br.dev.rodrigopinheiro.B3DataManager.application.result.operacao.ListOperacoesResult;
import br.dev.rodrigopinheiro.B3DataManager.application.security.SecurityService;
import br.dev.rodrigopinheiro.B3DataManager.application.service.ErrorService;
import br.dev.rodrigopinheiro.B3DataManager.application.service.formatter.OperacaoFormatterService;
import br.dev.rodrigopinheiro.B3DataManager.application.usecase.operacao.CountOperacoesUseCase;
import br.dev.rodrigopinheiro.B3DataManager.application.usecase.operacao.ListOperacoesUseCase;

import br.dev.rodrigopinheiro.B3DataManager.presentation.dto.OperacaoDTO;
import br.dev.rodrigopinheiro.B3DataManager.presentation.view.components.PaginationHelper;
import br.dev.rodrigopinheiro.B3DataManager.presentation.view.components.ToastNotification;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.util.List;

@Route("operacoes")
@Menu(order = 4, icon = LineAwesomeIconUrl.BUSINESS_TIME_SOLID, title = "Operacoes")
@PermitAll
@Slf4j
public class OperacaoView extends Div implements HasDynamicTitle, HasUrlParameter<Long> {

    private final ListOperacoesUseCase listOperacoesUseCase;
    private final CountOperacoesUseCase countOperacoesUseCase;
    private final ErrorService errorService;
    private final OperacaoFormatterService formatterService;
    private final Grid<OperacaoDTO> grid = new Grid<>(OperacaoDTO.class, false);

    private final ComboBox<String> entradaSaidaFilter;
    private final DatePicker startDateFilter;
    private final DatePicker endDateFilter;
    private final TextField movimentacaoFilter;
    private final TextField produtoFilter;
    private final TextField instituicaoFilter;
    private final ComboBox<Boolean> duplicadoFilter;
    private final ComboBox<Boolean> dimensionadoFilter;

    private int totalAmountOfPages;
    private int itemsPerPage = 25;
    private int currentPageNumber = 1;
    private Div pageIndicator;
    private Div filters;
    private String title = "";

    public OperacaoView(ListOperacoesUseCase listOperacoesUseCase, 
                       CountOperacoesUseCase countOperacoesUseCase,
                       ErrorService errorService,
                       OperacaoFormatterService formatterService) {
        this.listOperacoesUseCase = listOperacoesUseCase;
        this.countOperacoesUseCase = countOperacoesUseCase;
        this.errorService = errorService;
        this.formatterService = formatterService;

        // Inicialização de filtros
        entradaSaidaFilter = createComboBoxFilter("Entrada/Saída", List.of("Entrada", "Saída"));
        startDateFilter = createDatePicker("Data Inicial");
        endDateFilter = createDatePicker("Data Final");
        movimentacaoFilter = createTextFieldFilter("Movimentação");
        produtoFilter = createTextFieldFilter("Produto");
        instituicaoFilter = createTextFieldFilter("Instituição");
        duplicadoFilter = createBooleanFilter("Duplicado");
        dimensionadoFilter = createBooleanFilter("Dimensionado");

        setSizeFull();
        addClassName("operacao-view");

        filters = createFilters();
        VerticalLayout layout = new VerticalLayout(
                createMobileFilters(),
                filters,
                createGrid(),
                createPaginator()
        );
        layout.setSizeFull();
        layout.setFlexGrow(1, layout.getComponentAt(2)); // Faz o grid ocupar o espaço restante
        layout.setPadding(false);
        layout.setSpacing(false);
        add(layout);

        refreshGrid();
    }

    /**
     * Cria um layout de filtros para dispositivos móveis.
     *
     * @return Um componente `HorizontalLayout` para filtros em dispositivos móveis.
     */
    private HorizontalLayout createMobileFilters() {
        HorizontalLayout mobileFilters = new HorizontalLayout();
        mobileFilters.setWidthFull();
        mobileFilters.addClassName("mobile-filters");
        mobileFilters.addClassNames(LumoUtility.Padding.MEDIUM, LumoUtility.BoxSizing.BORDER,
                LumoUtility.AlignItems.CENTER);

        Icon mobileIcon = new Icon("lumo", "plus");
        Span filtersHeading = new Span("Filtros");
        mobileFilters.add(mobileIcon, filtersHeading);
        mobileFilters.setFlexGrow(1, filtersHeading);

        mobileFilters.addClickListener(e -> {
            if (filters.getClassNames().contains("visible")) {
                filters.removeClassName("visible");
                mobileIcon.getElement().setAttribute("icon", "lumo:plus");
            } else {
                filters.addClassName("visible");
                mobileIcon.getElement().setAttribute("icon", "lumo:minus");
            }
        });

        return mobileFilters;
    }

    /**
     * Cria os filtros para o grid de operações.
     *
     * @return Um componente `Div` contendo os filtros.
     */
    private Div createFilters() {
        Div filters = new Div();
        filters.addClassName("filter-layout");

        HorizontalLayout dateRangeLayout = new HorizontalLayout(startDateFilter, endDateFilter);
        dateRangeLayout.setWidthFull();
        dateRangeLayout.setSpacing(true);

        HorizontalLayout booleanFiltersLayout = new HorizontalLayout(duplicadoFilter, dimensionadoFilter);
        booleanFiltersLayout.setWidthFull();
        booleanFiltersLayout.setSpacing(true);

        Button resetButton = new Button("Resetar", e -> clearFilters());
        resetButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        
        Button searchButton = new Button("Procurar", e -> refreshGrid());
        searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout buttonsLayout = new HorizontalLayout(resetButton, searchButton);
        buttonsLayout.setJustifyContentMode(FlexLayout.JustifyContentMode.END);
        buttonsLayout.setWidthFull();
        buttonsLayout.addClassName("actions");

        filters.add(
                entradaSaidaFilter,
                dateRangeLayout,
                movimentacaoFilter,
                produtoFilter,
                instituicaoFilter,
                booleanFiltersLayout,
                buttonsLayout
        );

        return filters;
    }

    /**
     * Cria o grid de operações.
     *
     * @return Um componente `Grid` configurado.
     */
    private Component createGrid() {
        grid.addColumn(OperacaoDTO::entradaSaida).setHeader("Entrada/Saída");
        grid.addColumn(OperacaoDTO::data).setHeader("Data");
        grid.addColumn(OperacaoDTO::movimentacao).setHeader("Movimentação");
        grid.addColumn(OperacaoDTO::produto).setHeader("Produto");
        grid.addColumn(OperacaoDTO::instituicao).setHeader("Instituição");
        grid.addColumn(operacao -> formatterService.formatarQuantidade(operacao.quantidade())).setHeader("Quantidade");
        grid.addColumn(operacao -> formatterService.formatarPreco(operacao.precoUnitario(), operacao.quantidade())).setHeader("Preço Unitário");
        
        // Valor original da B3
        var valorB3Column = grid.addColumn(operacao -> formatterService.formatarValor(operacao.valorOperacao(), operacao.quantidade()))
            .setHeader("Valor B3");
        
        // Valor calculado
        var valorCalculadoColumn = grid.addColumn(operacao -> formatterService.formatarValor(operacao.valorCalculado(), operacao.quantidade()))
            .setHeader("Valor Calculado");
        
        // Diferença (só mostra se houver)
        var diferencaColumn = grid.addColumn(operacao -> formatterService.formatarDiferenca(operacao.diferencaValor()))
            .setHeader("Diferença");
        
        // Aplicar estilos condicionais usando renderer
        valorB3Column.setRenderer(new ComponentRenderer<>(operacao -> {
            var span = new Span(formatterService.formatarValor(operacao.valorOperacao(), operacao.quantidade()));
            if (operacao.temDiferencaValor()) {
                span.addClassNames("valor-diferente");
            }
            return span;
        }));
        
        valorCalculadoColumn.setRenderer(new ComponentRenderer<>(operacao -> {
            var span = new Span(formatterService.formatarValor(operacao.valorCalculado(), operacao.quantidade()));
            if (operacao.temDiferencaValor()) {
                span.addClassNames("valor-calculado");
            }
            return span;
        }));
        
        diferencaColumn.setRenderer(new ComponentRenderer<>(operacao -> {
            var span = new Span(formatterService.formatarDiferenca(operacao.diferencaValor()));
            if (operacao.temDiferencaValor()) {
                span.addClassNames("diferenca-valor");
            }
            return span;
        }));

        refreshGrid();
        return grid;
    }

    /**
     * Cria a paginação para o grid.
     *
     * @return Um componente `HorizontalLayout` contendo a paginação.
     */
    private Component createPaginator() {
        if (pageIndicator == null) {
            pageIndicator = new Div();
            pageIndicator.addClassName("page-indicator");
        }

        updatePageIndicator();

        // Botões para alternar itens por página
        Button items25Button = new Button("25", e -> setItemsPerPage(25));
        items25Button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        
        Button items50Button = new Button("50", e -> setItemsPerPage(50));
        items50Button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        
        Button items100Button = new Button("100", e -> setItemsPerPage(100));
        items100Button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        // Botão Anterior
        Button previousButton = new Button("Anterior", e -> {
            if (currentPageNumber > 1) {
                currentPageNumber--;
                refreshGrid();
            }
        });
        previousButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        // Botão Próximo
        Button nextButton = new Button("Próximo", e -> {
            if (currentPageNumber < totalAmountOfPages) {
                currentPageNumber++;
                refreshGrid();
            }
        });
        nextButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        //adiciona classe aos botoes
        nextButton.addClassName("paginator-button");
        nextButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        previousButton.addClassName("paginator-button");
        previousButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Layout dos botões
        HorizontalLayout paginatorLayout = new HorizontalLayout(
                previousButton,
                pageIndicator,
                nextButton,
                items25Button,
                items50Button,
                items100Button
        );
        paginatorLayout.setWidthFull();
        paginatorLayout.setJustifyContentMode(FlexLayout.JustifyContentMode.CENTER);
        paginatorLayout.setSpacing(true);

        return paginatorLayout;
    }

    /**
     * Atualiza os itens do grid com base nos filtros aplicados.
     */
    private void refreshGrid() {
        try {
            Long currentUserId = getCurrentUserId();
            if (currentUserId == null) {
                ToastNotification.showError("Usuário não autenticado. Faça login novamente.");
                return;
            }

            // Contagem com Use Case
            CountOperacoesCommand countCommand = new CountOperacoesCommand(
                    entradaSaidaFilter.getValue(),
                    startDateFilter.getValue(),
                    endDateFilter.getValue(),
                    movimentacaoFilter.getValue(),
                    produtoFilter.getValue(),
                    instituicaoFilter.getValue(),
                    duplicadoFilter.getValue(),
                    dimensionadoFilter.getValue(),
                    currentUserId
            );

            long totalElements = countOperacoesUseCase.execute(countCommand);
            totalAmountOfPages = PaginationHelper.calculateTotalPages(totalElements, itemsPerPage);

            // Listagem com Use Case
            ListOperacoesCommand listCommand = new ListOperacoesCommand(
                    entradaSaidaFilter.getValue(),
                    startDateFilter.getValue(),
                    endDateFilter.getValue(),
                    movimentacaoFilter.getValue(),
                    produtoFilter.getValue(),
                    instituicaoFilter.getValue(),
                    duplicadoFilter.getValue(),
                    dimensionadoFilter.getValue(),
                    currentPageNumber - 1,
                    itemsPerPage,
                    currentUserId
            );

            ListOperacoesResult result = listOperacoesUseCase.execute(listCommand);
            grid.setItems(result.operacoes());

            updatePageIndicator();
            
        } catch (Exception e) {
            String errorMessage = "Ocorreu um erro ao carregar os dados. Tente novamente mais tarde.";
            errorService.logError(errorMessage, e);
            ToastNotification.showError(errorMessage);
        }
    }

    /**
     * Altera o número de itens exibidos por página.
     *
     * @param items Número de itens por página.
     */
    private void setItemsPerPage(int items) {
        this.itemsPerPage = items;
        this.currentPageNumber = 1; // Volta para a primeira página
        refreshGrid();
        log.info("Número de itens por página alterado para: {}", items);
    }

    private void clearFilters() {
        entradaSaidaFilter.clear();
        startDateFilter.clear();
        endDateFilter.clear();
        movimentacaoFilter.clear();
        produtoFilter.clear();
        instituicaoFilter.clear();
        duplicadoFilter.clear();
        dimensionadoFilter.clear();

        currentPageNumber = 1; // Volta para a primeira página
        refreshGrid();
    }

    /**
     * Obtém o ID do usuário autenticado.
     */
    private Long getCurrentUserId() {
        return SecurityService.getAuthenticatedUserId();
    }

    /**
     * Atualiza o indicador de página.
     */
    private void updatePageIndicator() {
        if (pageIndicator != null) {
            pageIndicator.setText(String.format("Página %d de %d", currentPageNumber, totalAmountOfPages));
            log.info("Indicador de página atualizado: Página {} de {}.", currentPageNumber, totalAmountOfPages);
        }
    }

    @Override
    public String getPageTitle() {
        return title;
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Long parameter) {
        title = "Operações | B3 Data Manager";
        if (parameter != null) {
            title = title + " - " + parameter;
        }
        log.info("Título da página configurado como: {}", title);
    }

    /**
     * Cria um ComboBox com filtro.
     */
    private ComboBox<String> createComboBoxFilter(String placeholder, List<String> items) {
        ComboBox<String> comboBox = new ComboBox<>(placeholder);
        comboBox.setItems(items);
        comboBox.setPlaceholder(placeholder);
        comboBox.setClearButtonVisible(true);
        return comboBox;
    }

    /**
     * Cria um DatePicker.
     */
    private DatePicker createDatePicker(String placeholder) {
        DatePicker datePicker = new DatePicker(placeholder);
        datePicker.setPlaceholder(placeholder);
        datePicker.setClearButtonVisible(true);
        return datePicker;
    }

    /**
     * Cria um TextField com filtro.
     */
    private TextField createTextFieldFilter(String placeholder) {
        TextField textField = new TextField(placeholder);
        textField.setPlaceholder(placeholder);
        textField.setClearButtonVisible(true);
        return textField;
    }

    /**
     * Cria um ComboBox para valores Boolean.
     */
    private ComboBox<Boolean> createBooleanFilter(String placeholder) {
        ComboBox<Boolean> comboBox = new ComboBox<>(placeholder);
        comboBox.setItems(true, false);
        comboBox.setItemLabelGenerator(item -> item ? "Sim" : "Não");
        comboBox.setPlaceholder(placeholder);
        comboBox.setClearButtonVisible(true);
        return comboBox;
    }
    

}
