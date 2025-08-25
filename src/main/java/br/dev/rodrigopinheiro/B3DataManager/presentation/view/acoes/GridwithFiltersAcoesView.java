package br.dev.rodrigopinheiro.B3DataManager.presentation.view.acoes;

import br.dev.rodrigopinheiro.B3DataManager.application.security.SecurityService;
import br.dev.rodrigopinheiro.B3DataManager.application.service.ErrorService;
import br.dev.rodrigopinheiro.B3DataManager.application.service.RendaVariavelService;
import br.dev.rodrigopinheiro.B3DataManager.domain.entity.AtivoFinanceiro;
import br.dev.rodrigopinheiro.B3DataManager.domain.entity.RendaVariavel;
import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivoFinanceiroVariavel;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.ServiceException;
import br.dev.rodrigopinheiro.B3DataManager.presentation.dto.AtivoAcaoDTO;
import br.dev.rodrigopinheiro.B3DataManager.presentation.view.components.PaginationHelper;
import br.dev.rodrigopinheiro.B3DataManager.presentation.view.components.ToastNotification;
import br.dev.rodrigopinheiro.B3DataManager.presentation.view.factory.MessageUtils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@Slf4j
@Route("acoes")
@Menu(order = 2, icon = LineAwesomeIconUrl.CHART_LINE_SOLID, title = "Ações")
@PermitAll
@Uses(Icon.class)
public class GridwithFiltersAcoesView extends Div
        implements HasDynamicTitle, HasUrlParameter<Long>, Specification<AtivoFinanceiro> {

    private final RendaVariavelService rendaVariavelService;
    private final ErrorService errorService;
    private final Locale locale;
    private final ResourceBundle messages;
    private final MessageUtils messageUtils;

    private final FiltersAcoesView filters;
    private final Grid<AtivoAcaoDTO> grid;
    private Div pageIndicator = new Div();
    private ListDataProvider<AtivoAcaoDTO> dataProvider;
    // paginação manual
    private int itemsPerPage = 50;
    private int currentPageNumber = 1;
    private int totalAmountOfPages;
    private String title = "";

    public GridwithFiltersAcoesView(
            RendaVariavelService rendaVariavelService,
            ErrorService errorService) {
        this.rendaVariavelService = rendaVariavelService;
        this.errorService = errorService;
        this.locale = UI.getCurrent().getSession().getLocale();
        this.messages = ResourceBundle.getBundle("messages", locale);
        this.messageUtils = new MessageUtils(messages);

        setSizeFull();
        addClassNames("gridwith-filters-view");

        // filtros
        filters = new FiltersAcoesView(this::refreshGrid);

        // grid
        grid = new Grid<>(AtivoAcaoDTO.class, false);
        configureGridColumns();

        // layout
        VerticalLayout layout = new VerticalLayout(
                createMobileFilters(),
                filters,
                grid,
                createPaginator()
        );
        layout.setSizeFull();
        layout.setFlexGrow(1, grid);
        layout.setPadding(false);
        layout.setSpacing(false);
        add(layout);

        log.info("GridwithFiltersAcoesView inicializado com locale: {}", locale);

        // primeira carga
        refreshGrid();
    }

    private HorizontalLayout createMobileFilters() {
        Icon icon = new Icon("lumo", "plus");
        Span lbl = new Span(messages.getString("global.mobileFilters"));
        HorizontalLayout hl = new HorizontalLayout(icon, lbl);
        hl.setWidthFull();
        hl.addClassNames(
                LumoUtility.Padding.MEDIUM,
                LumoUtility.BoxSizing.BORDER,
                LumoUtility.AlignItems.CENTER,
                "mobile-filters"
        );
        hl.setFlexGrow(1, lbl);
        hl.addClickListener(e -> {
            if (filters.getClassNames().contains("visible")) {
                filters.removeClassName("visible");
                icon.getElement().setAttribute("icon", "lumo:plus");
            } else {
                filters.addClassName("visible");
                icon.getElement().setAttribute("icon", "lumo:minus");
            }
        });
        return hl;
    }

    private void configureGridColumns() {
        grid.addColumn(AtivoAcaoDTO::getNome)
                .setHeader(messages.getString("ativo.grid.nome")).setAutoWidth(true);
        grid.addColumn(AtivoAcaoDTO::getQuantidade)
                .setHeader(messages.getString("ativo.grid.quantidade")).setAutoWidth(true);
        grid.addColumn(dto -> dto.getPrecoMedio().setScale(2, RoundingMode.HALF_UP))
                .setHeader(messages.getString("ativo.grid.preco.medio")).setAutoWidth(true);
        grid.addColumn(dto -> {
            if (dto.getPrecoAtual() == null) {
                return messages.getString("ativo.grid.carregando");
            } else if (dto.getPrecoAtual().compareTo(BigDecimal.valueOf(-1)) == 0) {
                return messages.getString("ativo.grid.falha");
            } else {
                return dto.getPrecoAtual().setScale(2, RoundingMode.HALF_UP).toString();
            }
        }).setHeader(messages.getString("ativo.grid.preco.atual")).setAutoWidth(true);
        grid.addColumn(dto -> {
            if (dto.getVariacao() == null) {
                return messages.getString("ativo.grid.carregando");
            } else if (dto.getVariacao().compareTo(BigDecimal.valueOf(-1)) == 0) {
                return messages.getString("ativo.grid.falha");
            } else {
                return dto.getVariacao().setScale(2, RoundingMode.HALF_UP) + " %";
            }
        }).setHeader(messages.getString("ativo.grid.variacao")).setAutoWidth(true);
        grid.addColumn(dto -> dto.getTotal().setScale(2, RoundingMode.HALF_UP))
                .setHeader(messages.getString("ativo.grid.total")).setAutoWidth(true);
        grid.addColumn(dto -> dto.getPorcentagem().setScale(2, RoundingMode.HALF_UP) + " %")
                .setHeader(messages.getString("ativo.grid.porcentagem")).setAutoWidth(true);
    }

    private Component createPaginator() {
        // Inicializa o indicador de página, se necessário
        if (pageIndicator == null) {
            pageIndicator = new Div();
            pageIndicator.addClassName("page-indicator");
        }
        // Atualiza o indicador de página utilizando um helper (assume-se a existência de PaginationHelper)
        PaginationHelper.updatePageIndicator(pageIndicator, currentPageNumber, totalAmountOfPages, messageUtils);

        Button prev = new Button(messages.getString("global.previous"), e -> {
            if (currentPageNumber > 1) {
                currentPageNumber--;
                refreshGrid();
            }
        });
        prev.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button next = new Button(messages.getString("global.next"), e -> {
            if (currentPageNumber < totalAmountOfPages) {
                currentPageNumber++;
                refreshGrid();
            }
        });
        next.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btn25 = new Button(messages.getString("global.paginator.25"), e -> setItemsPerPage(25));
        btn25.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        Button btn50 = new Button(messages.getString("global.paginator.50"), e -> setItemsPerPage(50));
        btn50.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        Button btn100 = new Button(messages.getString("global.paginator.100"), e -> setItemsPerPage(100));
        btn100.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout hl = new HorizontalLayout(prev, pageIndicator, next, btn25, btn50, btn100);
        hl.setWidthFull();
        hl.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        hl.setSpacing(true);
        hl.addClassName("paginator-layout");
        return hl;
    }

    private void setItemsPerPage(int items) {
        this.itemsPerPage = items;
        this.currentPageNumber = 1;
        refreshGrid();
        log.info("Número de itens por página alterado para: {}", items);
    }

    private void refreshGrid() {
        log.info("Atualizando grid de Ações com {} itens por página na página {}", itemsPerPage, currentPageNumber);
        try {
            Long usuarioId = SecurityService.getAuthenticatedUserId();

            // 1) Define os tipos de variável para Ações
            List<String> tiposAcoes = List.of(
                    TipoAtivoFinanceiroVariavel.ACAO_ON.name(),
                    TipoAtivoFinanceiroVariavel.ACAO_PN.name(),
                    TipoAtivoFinanceiroVariavel.ACAO_UNIT.name(),
                    TipoAtivoFinanceiroVariavel.ETF.name()
            );

            // 2) Conta total de registros com filtros
            long totalRegistros = rendaVariavelService.countByFiltersIn(
                    tiposAcoes,
                    filters.nome.getValue(),
                    filters.startDate.getValue(),
                    filters.endDate.getValue(),
                    filters.precoMedioMin.getValue().isEmpty() ? null : new BigDecimal(filters.precoMedioMin.getValue()),
                    filters.precoMedioMax.getValue().isEmpty() ? null : new BigDecimal(filters.precoMedioMax.getValue()),
                    usuarioId
            );
            totalAmountOfPages = PaginationHelper.calculateTotalPages(totalRegistros, itemsPerPage);

            // 3) Busca página de RendaVariavel já com filtros aplicados
            List<RendaVariavel> rendasPaginadas = rendaVariavelService.findWithFiltersIn(
                    tiposAcoes,
                    filters.nome.getValue(),
                    filters.startDate.getValue(),
                    filters.endDate.getValue(),
                    filters.precoMedioMin.getValue().isEmpty() ? null : new BigDecimal(filters.precoMedioMin.getValue()),
                    filters.precoMedioMax.getValue().isEmpty() ? null : new BigDecimal(filters.precoMedioMax.getValue()),
                    PageRequest.of(currentPageNumber - 1, itemsPerPage),
                    usuarioId,
                    locale
            ).getContent();

            // 4) Calcula total de quantidade no portfólio para porcentagem
            BigDecimal totalQuantidadePortfolio = rendaVariavelService.calcularTotalInvestidoEmAcoes(usuarioId);

            // 5) Converte para DTO
            List<AtivoAcaoDTO> ativosDTO = rendasPaginadas.stream()
                    .map(rv -> AtivoAcaoDTO.from(rv, totalQuantidadePortfolio))
                    .toList();

            // 6) Atualiza o grid
            grid.setItems(ativosDTO);

            // 7) Atualiza indicador de página
            if (pageIndicator != null) {
                PaginationHelper.updatePageIndicator(pageIndicator, currentPageNumber, totalAmountOfPages, messageUtils);
            }

            // 8) Chama API assíncrona para preço e variação
            atualizarPrecosAtuaisAsyncAcoes(ativosDTO);

        } catch (ServiceException ex) {
            log.error("Erro ao obter ativos Ações: {}", ex.getMessage(), ex);
            ToastNotification.showError(errorService.getErrorMessage("error.service.generic", ex.getMessage()));
        } catch (Exception ex) {
            log.error("Erro inesperado ao carregar o grid de Ações", ex);
            ToastNotification.showError(errorService.getErrorMessage("error.inesperado"));
        }
    }

    /**
     * Atualiza os preços de mercado chamando a API e reflete as mudanças no Grid de Ações.
     */
    private void atualizarPrecosAtuaisAsyncAcoes(List<AtivoAcaoDTO> ativosDTO) {
        rendaVariavelService
                .atualizarPrecosAtuaisAsyncAcoes(ativosDTO)
                .thenAccept(updatedAtivos -> {
                    getUI().ifPresent(ui -> ui.access(() -> {
                        log.info("Preços de ações atualizados via API, recarregando Grid...");

                        // Evita que uma lista vazia da API limpe tudo
                        if (!updatedAtivos.isEmpty()) {
                            grid.setItems(updatedAtivos);
                        } else {
                            log.warn("Nenhum dado atualizado da API, mantendo dados anteriores.");
                        }
                    }));
                })
                .exceptionally(ex -> {
                    log.error("Erro ao atualizar preços de ações via API: {}", ex.getMessage(), ex);
                    return null;
                });
    }


    /**
     * Implementação do métod toPredicate para aplicação dos filtros na consulta.
     *
     * @return Predicate composto com os filtros aplicados
     */
    @Override
    public Predicate toPredicate(Root<AtivoFinanceiro> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        // Filtro por nome (converte para minúsculo para comparação case-insensitive)
        if (!filters.nome.isEmpty()) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nome")), "%" + filters.nome.getValue().toLowerCase() + "%"));
        }

        // Filtros de data inicial e final
        if (filters.startDate.getValue() != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("data"), filters.startDate.getValue()));
        }
        if (filters.endDate.getValue() != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("data"), filters.endDate.getValue()));
        }

        // Filtros de preço médio com tratamento para NumberFormatException
        if (!filters.precoMedioMin.isEmpty()) {
            try {
                Double minPrecoMedio = Double.valueOf(filters.precoMedioMin.getValue());
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("precoMedio"), minPrecoMedio));
            } catch (NumberFormatException e) {
                log.error("Erro ao converter preço médio mínimo: {}", filters.precoMedioMin.getValue(), e);
            }
        }
        if (!filters.precoMedioMax.isEmpty()) {
            try {
                Double maxPrecoMedio = Double.valueOf(filters.precoMedioMax.getValue());
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("precoMedio"), maxPrecoMedio));
            } catch (NumberFormatException e) {
                log.error("Erro ao converter preço médio máximo: {}", filters.precoMedioMax.getValue(), e);
            }
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Long parameter) {
        title = messages.getString("acoes.title")
                + " | " + messages.getString("global.project.name");
        if (parameter != null) {
            title += " " + parameter;
        }
    }

    @Override
    public String getPageTitle() {
        return title;
    }
}
