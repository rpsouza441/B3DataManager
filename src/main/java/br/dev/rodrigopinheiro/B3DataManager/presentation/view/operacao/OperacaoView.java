package br.dev.rodrigopinheiro.B3DataManager.presentation.view.operacao;

import br.dev.rodrigopinheiro.B3DataManager.application.service.ErrorService;
import br.dev.rodrigopinheiro.B3DataManager.application.service.OperacaoService;
import br.dev.rodrigopinheiro.B3DataManager.domain.entity.Operacao;
import br.dev.rodrigopinheiro.B3DataManager.presentation.view.components.PaginationHelper;
import br.dev.rodrigopinheiro.B3DataManager.presentation.view.components.ToastNotification;
import br.dev.rodrigopinheiro.B3DataManager.presentation.view.factory.FilterFactory;
import br.dev.rodrigopinheiro.B3DataManager.presentation.view.factory.MessageUtils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@Route("operacoes")
@Menu(order = 4, icon = LineAwesomeIconUrl.BUSINESS_TIME_SOLID, title = "Operacoes")
@PermitAll
@Slf4j
public class OperacaoView extends Div implements HasDynamicTitle, HasUrlParameter<Long> {

    private final OperacaoService operacaoService;
    private final ErrorService errorService;
    private final Grid<Operacao> grid = new Grid<>(Operacao.class, false);
    private final MessageUtils messageUtils;
    private final FilterFactory filterFactory;

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
    private final ResourceBundle messages;
    private Locale currentLocale;

    public OperacaoView(OperacaoService operacaoService, ErrorService errorService) {
        this.operacaoService = operacaoService;
        this.errorService = errorService;

        currentLocale = getUI().map(ui -> ui.getSession().getLocale()).orElse(Locale.getDefault());
        messages = ResourceBundle.getBundle("messages", currentLocale);
        this.messageUtils = new MessageUtils(messages);
        this.filterFactory = new FilterFactory(messages);

        // Inicialização de filtros
        entradaSaidaFilter = filterFactory.createComboBoxFilter("operacao.grid.entradaSaida", List.of("Entrada", "Saída"));
        startDateFilter = filterFactory.createDatePicker("operacao.grid.dateRangeStart.placeholder");
        endDateFilter = filterFactory.createDatePicker("operacao.grid.dateRangeEnd.placeholder");
        movimentacaoFilter = filterFactory.createTextFieldFilter("operacao.grid.movimentacao");
        produtoFilter = filterFactory.createTextFieldFilter("operacao.grid.produto");
        instituicaoFilter = filterFactory.createTextFieldFilter("operacao.grid.instituicao");
        duplicadoFilter = filterFactory.createBooleanFilter("operacao.grid.duplicado");
        dimensionadoFilter = filterFactory.createBooleanFilter("operacao.grid.dimensionado");

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
        Span filtersHeading = new Span(messages.getString("operacao.grid.mobileFilters"));
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

        Button resetButton = filterFactory.createButton("operacao.grid.reset", ButtonVariant.LUMO_TERTIARY, e -> clearFilters());
        Button searchButton = filterFactory.createButton("operacao.grid.search", ButtonVariant.LUMO_PRIMARY, e -> refreshGrid());

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
        grid.addColumn(Operacao::getEntradaSaida).setHeader(messages.getString("operacao.grid.entradaSaida"));
        grid.addColumn(Operacao::getData).setHeader(messages.getString("operacao.grid.data"));
        grid.addColumn(Operacao::getMovimentacao).setHeader(messages.getString("operacao.grid.movimentacao"));
        grid.addColumn(Operacao::getProduto).setHeader(messages.getString("operacao.grid.produto"));
        grid.addColumn(Operacao::getInstituicao).setHeader(messages.getString("operacao.grid.instituicao"));

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

        PaginationHelper.updatePageIndicator(pageIndicator, currentPageNumber, totalAmountOfPages, messageUtils);

        // Botões para alternar itens por página
        Button items25Button = filterFactory.createButton("global.paginator.25", ButtonVariant.LUMO_TERTIARY, e -> setItemsPerPage(25));
        Button items50Button = filterFactory.createButton("global.paginator.50", ButtonVariant.LUMO_TERTIARY, e -> setItemsPerPage(50));
        Button items100Button = filterFactory.createButton("global.paginator.100", ButtonVariant.LUMO_TERTIARY, e -> setItemsPerPage(100));

        // Botão Anterior
        Button previousButton = filterFactory.createButton("global.previous", ButtonVariant.LUMO_TERTIARY, e -> {
            if (currentPageNumber > 1) {
                currentPageNumber--;
                refreshGrid();
            }
        });

        // Botão Próximo
        Button nextButton = filterFactory.createButton("global.next", ButtonVariant.LUMO_TERTIARY, e -> {
            if (currentPageNumber < totalAmountOfPages) {
                currentPageNumber++;
                refreshGrid();
            }
        });

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
            totalAmountOfPages = PaginationHelper.calculateTotalPages(
                    operacaoService.countByFilters(
                            entradaSaidaFilter.getValue(),
                            startDateFilter.getValue(),
                            endDateFilter.getValue(),
                            movimentacaoFilter.getValue(),
                            produtoFilter.getValue(),
                            instituicaoFilter.getValue(),
                            duplicadoFilter.getValue(),
                            dimensionadoFilter.getValue()
                    ),
                    itemsPerPage
            );

            List<Operacao> items = operacaoService.findWithFilters(
                    entradaSaidaFilter.getValue(),
                    startDateFilter.getValue(),
                    endDateFilter.getValue(),
                    movimentacaoFilter.getValue(),
                    produtoFilter.getValue(),
                    instituicaoFilter.getValue(),
                    duplicadoFilter.getValue(),
                    dimensionadoFilter.getValue(),
                    PageRequest.of(currentPageNumber - 1, itemsPerPage),
                    currentLocale
            ).getContent();

            grid.setItems(items);

            if (pageIndicator != null) {
                PaginationHelper.updatePageIndicator(pageIndicator, currentPageNumber, totalAmountOfPages, messageUtils);
            } else {
                log.warn("pageIndicator está nulo. O indicador de página não será atualizado.");
            }
        } catch (Exception e) {
            String errorMessage = messageUtils.getString("operacao.grid.error.loading");
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
     * Atualiza o indicador de página.
     */
    private void updatePageIndicator() {
        pageIndicator.setText(String.format("Página %d de %d", currentPageNumber, totalAmountOfPages));
        log.info("Indicador de página atualizado: Página {} de {}.", currentPageNumber, totalAmountOfPages);
    }

    @Override
    public String getPageTitle() {
        return title;
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Long parameter) {
        title = messageUtils.getString("operacao.title") + " | " + messageUtils.getString("global.project.name");
        if (parameter != null) {
            title = title + parameter;
        }
        log.info("Título da página configurado como: {}", title);
    }
}
