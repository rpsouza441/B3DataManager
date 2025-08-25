package br.dev.rodrigopinheiro.B3DataManager.presentation.view.fii;

import br.dev.rodrigopinheiro.B3DataManager.application.security.SecurityService;
import br.dev.rodrigopinheiro.B3DataManager.application.service.ErrorService;
import br.dev.rodrigopinheiro.B3DataManager.application.service.RendaVariavelService;
import br.dev.rodrigopinheiro.B3DataManager.domain.entity.AtivoFinanceiro;
import br.dev.rodrigopinheiro.B3DataManager.domain.entity.RendaVariavel;
import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivoFinanceiroVariavel;
import br.dev.rodrigopinheiro.B3DataManager.domain.exception.ServiceException;
import br.dev.rodrigopinheiro.B3DataManager.presentation.dto.AtivoFiiDTO;
import br.dev.rodrigopinheiro.B3DataManager.presentation.view.components.PaginationHelper;
import br.dev.rodrigopinheiro.B3DataManager.presentation.view.components.ToastNotification;
import br.dev.rodrigopinheiro.B3DataManager.presentation.view.factory.MessageUtils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
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

/**
 * Tela para exibição do grid de ativos FII com filtros e paginação.
 * Implementa Specification para aplicar os filtros na consulta.
 */
@Slf4j
@Route("fii")
@Menu(order = 1, icon = LineAwesomeIconUrl.WAREHOUSE_SOLID, title = "FII")
@PermitAll
@Uses(Icon.class)
public class GridwithFiltersFiiView extends Div implements HasDynamicTitle, HasUrlParameter<Long>, Specification<AtivoFinanceiro> {

    // Dependências de serviço e mensagens
    private final RendaVariavelService rendaVariavelService;
    private final ErrorService errorService;
    private final ResourceBundle messages;
    private final FiltersFiiView filters;
    private final MessageUtils messageUtils;
    Locale currentLocale;
    private Grid<AtivoFiiDTO> grid;
    private String title = "";
    // Atributos para paginação manual
    private int totalAmountOfPages;
    private int itemsPerPage = 25;
    private int currentPageNumber = 1;
    private Div pageIndicator;
    private ListDataProvider<AtivoFiiDTO> dataProvider;


    /**
     * Construtor da tela FII.
     *
     * @param rendaVariavelService Serviço para operações com renda variável
     * @param errorService         Serviço para tratamento de erros
     */
    public GridwithFiltersFiiView(RendaVariavelService rendaVariavelService, ErrorService errorService) {
        this.rendaVariavelService = rendaVariavelService;
        this.errorService = errorService;
        currentLocale = getUI().map(ui -> ui.getSession().getLocale()).orElse(Locale.getDefault());
        messages = ResourceBundle.getBundle("messages", currentLocale);
        this.messageUtils = new MessageUtils(messages);


        setSizeFull();
        addClassNames("gridwith-filters-view");

        // Inicializa os filtros e associa a ação de refresh do grid
        filters = new FiltersFiiView(this::refreshGrid);

        // Atualiza a estrutura do layout para garantir que o grid cresça corretamente
        VerticalLayout layout = new VerticalLayout(createMobileFilters(), filters, createGrid(), createPaginator());
        layout.setSizeFull();
        layout.setFlexGrow(1, layout.getComponentAt(2)); // Faz o grid ocupar o espaço restante
        layout.setPadding(false);
        layout.setSpacing(false);
        add(layout);


        log.info("GridwithFiltersFiiView inicializado com locale: {}", currentLocale);
    }

    /**
     * Cria layout para exibição dos filtros em dispositivos mobile.
     *
     * @return HorizontalLayout com os filtros mobile
     */
    private HorizontalLayout createMobileFilters() {
        HorizontalLayout mobileFilters = new HorizontalLayout();
        mobileFilters.setWidthFull();
        mobileFilters.addClassNames(LumoUtility.Padding.MEDIUM, LumoUtility.BoxSizing.BORDER,
                LumoUtility.AlignItems.CENTER);
        mobileFilters.addClassName("mobile-filters");

        Icon mobileIcon = new Icon("lumo", "plus");
        Span filtersHeading = new Span(messages.getString("fii.mobileFilters")); // Label obtido do messages
        mobileFilters.add(mobileIcon, filtersHeading);
        mobileFilters.setFlexGrow(1, filtersHeading);

        // Alterna a visibilidade dos filtros ao clicar no layout
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
     * Cria e configura o grid para exibição dos ativos FII.
     *
     * @return Componente com o grid e o placeholder para dados vazios
     */
    private Component createGrid() {
        // Inicializa o grid sem lazy loading, pois utilizaremos paginação manual
        grid = new Grid<>(AtivoFiiDTO.class, false);

        // Configuração das colunas do grid com labels do messages
        grid.addColumn(AtivoFiiDTO::getNome)
                .setHeader(messages.getString("ativo.grid.nome"))
                .setAutoWidth(true);
        grid.addColumn(AtivoFiiDTO::getQuantidade)
                .setHeader(messages.getString("ativo.grid.quantidade"))
                .setAutoWidth(true);
        grid.addColumn(dto -> dto.getPrecoMedio().setScale(2, RoundingMode.HALF_UP))
                .setHeader(messages.getString("ativo.grid.preco.medio"))
                .setAutoWidth(true);
        grid.addColumn(dto -> {
                    if (dto.getPrecoAtual() == null) {
                        return messages.getString("ativo.grid.carregando");
                    } else if (dto.getPrecoAtual().compareTo(BigDecimal.valueOf(-1)) == 0) {
                        return messages.getString("ativo.grid.falha");
                    } else {
                        return dto.getPrecoAtual().setScale(2, RoundingMode.HALF_UP).toString();
                    }
                }).setHeader(messages.getString("ativo.grid.preco.atual"))
                .setAutoWidth(true);
        grid.addColumn(dto -> {
                    if (dto.getVariacao() == null) {
                        return messages.getString("ativo.grid.carregando");
                    } else if (dto.getVariacao().compareTo(BigDecimal.valueOf(-1)) == 0) {
                        return messages.getString("ativo.grid.falha");
                    } else {
                        return dto.getVariacao().setScale(2, RoundingMode.HALF_UP) + " %";
                    }
                }).setHeader(messages.getString("ativo.grid.variacao"))
                .setAutoWidth(true);
        grid.addColumn(dto -> dto.getTotal().setScale(2, RoundingMode.HALF_UP))
                .setHeader(messages.getString("ativo.grid.total"))
                .setAutoWidth(true);
        grid.addColumn(dto -> dto.getPorcentagem().setScale(2, RoundingMode.HALF_UP) + " %")
                .setHeader(messages.getString("ativo.grid.porcentagem"))
                .setAutoWidth(true);

        // Criação do placeholder para quando não houver dados
        Div emptyPlaceholder = new Div();
        emptyPlaceholder.setText(messages.getString("ativo.grid.empty"));
        emptyPlaceholder.addClassName("empty-placeholder");

        // Wrapper que contém o grid e o placeholder
        Div wrapper = new Div();
        wrapper.addClassName("grid-wrapper");
        wrapper.setSizeFull();
        wrapper.add(grid, emptyPlaceholder);

        // Inicialmente, o placeholder é invisível
        emptyPlaceholder.setVisible(false);

        // Carrega os dados do grid pela primeira vez
        refreshGrid();

        return wrapper;
    }

    /**
     * Cria o componente de paginação com botões para navegação e seleção de itens por página.
     *
     * @return HorizontalLayout contendo os botões de paginação
     */
    private Component createPaginator() {
        // Inicializa o indicador de página, se necessário
        if (pageIndicator == null) {
            pageIndicator = new Div();
            pageIndicator.addClassName("page-indicator");
        }
        // Atualiza o indicador de página utilizando um helper (assume-se a existência de PaginationHelper)
        PaginationHelper.updatePageIndicator(pageIndicator, currentPageNumber, totalAmountOfPages, messageUtils);

        // Botões para alterar o número de itens exibidos por página
        Button items25Button = new Button(messages.getString("fii.pagination.items25"));
        items25Button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        items25Button.addClickListener(e -> setItemsPerPage(25));

        Button items50Button = new Button(messages.getString("fii.pagination.items50"));
        items50Button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        items50Button.addClickListener(e -> setItemsPerPage(50));

        Button items100Button = new Button(messages.getString("fii.pagination.items100"));
        items100Button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        items100Button.addClickListener(e -> setItemsPerPage(100));

        // Botão para página anterior
        Button previousButton = new Button(messages.getString("fii.pagination.previous"));
        previousButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        previousButton.addClickListener(e -> {
            if (currentPageNumber > 1) {
                currentPageNumber--;
                refreshGrid();
            }
        });
        previousButton.addClassName("paginator-button");
        previousButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Botão para próxima página
        Button nextButton = new Button(messages.getString("fii.pagination.next"));
        nextButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        nextButton.addClickListener(e -> {
            if (currentPageNumber < totalAmountOfPages) {
                currentPageNumber++;
                refreshGrid();
            }
        });
        nextButton.addClassName("paginator-button");
        nextButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Layout dos botões de paginação
        HorizontalLayout paginatorLayout = new HorizontalLayout(
                previousButton,
                pageIndicator,
                nextButton,
                items25Button,
                items50Button,
                items100Button
        );
        paginatorLayout.setWidthFull();
        paginatorLayout.setJustifyContentMode(com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode.CENTER);
        paginatorLayout.setSpacing(true);

        return paginatorLayout;
    }

    /**
     * Métod acionado pelos filtros e pelos controles de paginação para atualizar os dados do grid.
     * Utiliza paginação manual para carregar os ativos FII.
     */
    private void refreshGrid() {
        log.info("Atualizando grid com {} itens por página na página {}", itemsPerPage, currentPageNumber);
        try {
            Long usuarioId = SecurityService.getAuthenticatedUserId();

            // 1. Contagem total usando os filtros e forçando o tipo FII
            totalAmountOfPages = PaginationHelper.calculateTotalPages(
                    rendaVariavelService.countByFilters(
                            TipoAtivoFinanceiroVariavel.FII.name(),
                            filters.nome.getValue(),
                            filters.startDate.getValue(),
                            filters.endDate.getValue(),
                            filters.precoMedioMin.getValue().isEmpty() ? null : new BigDecimal(filters.precoMedioMin.getValue()),
                            filters.precoMedioMax.getValue().isEmpty() ? null : new BigDecimal(filters.precoMedioMax.getValue()),
                            usuarioId
                    ),
                    itemsPerPage
            );

            // 2. Busca paginada dos FIIs com os filtros (e com o tipo FII fixo)
            List<RendaVariavel> ativos = rendaVariavelService.findWithFilters(
                    TipoAtivoFinanceiroVariavel.FII.name(),
                    filters.nome.getValue(),
                    filters.startDate.getValue(),
                    filters.endDate.getValue(),
                    filters.precoMedioMin.getValue().isEmpty() ? null : new BigDecimal(filters.precoMedioMin.getValue()),
                    filters.precoMedioMax.getValue().isEmpty() ? null : new BigDecimal(filters.precoMedioMax.getValue()),
                    PageRequest.of(currentPageNumber - 1, itemsPerPage),
                    usuarioId,
                    currentLocale
            ).getContent();

            // 3. Calcula o total investido em FIIs (todos os registros do usuário do tipo FII)
            BigDecimal totalInvestidoEmFiis = rendaVariavelService.calcularTotalInvestidoEmFiis(usuarioId);

            // 4. Converte os registros (RendaVariavel) para DTO (AtivoFiiDTO) passando o total investido para o cálculo da porcentagem
            List<AtivoFiiDTO> ativosDTO = ativos.stream()
                    .map(rv -> AtivoFiiDTO.from(rv, totalInvestidoEmFiis))
                    .toList();


            // 5. Atualiza o grid com os dados convertidos
            grid.setItems(ativosDTO);

            // 6. Atualiza o indicador de página
            if (pageIndicator != null) {
                PaginationHelper.updatePageIndicator(pageIndicator, currentPageNumber, totalAmountOfPages, messageUtils);
            } else {
                log.warn("pageIndicator está nulo. O indicador de página não será atualizado.");
            }

            // 7. Atualiza os preços via API e, ao retornar, atualiza o grid
            atualizarPrecosAtuaisAsync(ativosDTO);

        } catch (ServiceException ex) {
            log.error("Erro ao obter ativos FII: {}", ex.getMessage(), ex);
            ToastNotification.showError(errorService.getErrorMessage("error.service.generic", ex.getMessage()));
        } catch (Exception ex) {
            log.error("Erro inesperado ao carregar o grid de ativos FII", ex);
            ToastNotification.showError(errorService.getErrorMessage("error.inesperado"));
        }
    }

    /**
     * Atualiza os preços de mercado chamando a API e reflete as mudanças no Grid.
     */
    private void atualizarPrecosAtuaisAsync(List<AtivoFiiDTO> ativosDTO) {
        rendaVariavelService.atualizarPrecosAtuaisAsync(ativosDTO)
                .thenAccept(updatedAtivos -> {
                    getUI().ifPresent(ui -> ui.access(() -> {
                        log.info("Preços atualizados via API, recarregando Grid...");

                        //  Evita que erros na API removam todos os dados
                        if (!updatedAtivos.isEmpty()) {
                            grid.setItems(updatedAtivos);
                        } else {
                            log.warn("Nenhum dado atualizado da API, mantendo dados anteriores.");
                        }
                    }));
                })
                .exceptionally(ex -> {
                    log.error("Erro ao atualizar preços via API: {}", ex.getMessage(), ex);
                    return null;
                });
    }


    /**
     * Altera o número de itens exibidos por página e recarrega o grid.
     *
     * @param items Número de itens por página.
     */
    private void setItemsPerPage(int items) {
        this.itemsPerPage = items;
        this.currentPageNumber = 1; // Volta para a primeira página
        refreshGrid();
        log.info("Número de itens por página alterado para: {}", items);
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

    /**
     * Define o título da página com base nos valores localizados.
     *
     * @param event     Evento de navegação
     * @param parameter Parâmetro opcional para compor o título
     */
    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Long parameter) {
        title = messages.getString("fii.title") + " | " + messages.getString("global.project.name");
        if (parameter != null) {
            title = title + parameter;
        }
    }

    /**
     * Retorna o título da página.
     *
     * @return Título da página
     */
    @Override
    public String getPageTitle() {
        return title;
    }
}
