package br.dev.rodrigopinheiro.B3DataManager.presentation.view.fii;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.LumoUtility;
import lombok.extern.slf4j.Slf4j;

/**
 * Componente de filtros para a tela de FII.
 * Possui campos para nome, data inicial, data final, preço médio mínimo e preço médio máximo.
 * Também contém botões para resetar os filtros e executar a busca.
 */
@Slf4j
public class FiltersFiiView extends Div {

    // Campos de filtro
    public final TextField nome = new TextField("Nome");
    public final DatePicker startDate = new DatePicker("Data Inicial");
    public final DatePicker endDate = new DatePicker("Data Final");
    public final TextField precoMedioMin = new TextField("Preço Médio Mínimo");
    public final TextField precoMedioMax = new TextField("Preço Médio Máximo");

    /**
     * Construtor que recebe uma ação (Runnable) a ser executada quando os filtros forem aplicados ou resetados.
     *
     * @param onSearch A ação a ser executada na busca
     */
    public FiltersFiiView(Runnable onSearch) {
        setWidthFull();
        addClassName("filter-layout");
        addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM,
                LumoUtility.BoxSizing.BORDER);

        // Botão para reset dos filtros com log da ação
        Button resetBtn = new Button("Reset");
        resetBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        resetBtn.addClickListener(e -> {
            log.debug("Resetando filtros");
            nome.clear();
            startDate.clear();
            endDate.clear();
            precoMedioMin.clear();
            precoMedioMax.clear();
            onSearch.run();
        });

        // Botão para execução da busca com log dos filtros aplicados
        Button searchBtn = new Button("Search");
        searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        searchBtn.addClickListener(e -> {
            log.debug("Executando busca com filtros: Nome={}, DataInicial={}, DataFinal={}, PreçoMínimo={}, PreçoMáximo={}",
                    nome.getValue(), startDate.getValue(), endDate.getValue(), precoMedioMin.getValue(), precoMedioMax.getValue());
            onSearch.run();
        });
        // Layout dos botões de ação
        Div actions = new Div(resetBtn, searchBtn);
        actions.addClassName(LumoUtility.Gap.SMALL);
        actions.addClassName("actions");

        // Adiciona os componentes de filtro e os botões ao layout principal
        add(nome, startDate, endDate, precoMedioMin, precoMedioMax, actions);
    }
}
