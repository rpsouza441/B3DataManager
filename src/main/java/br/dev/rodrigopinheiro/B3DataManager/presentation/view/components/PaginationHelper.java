package br.dev.rodrigopinheiro.B3DataManager.presentation.view.components;

import com.vaadin.flow.component.html.Div;
import br.dev.rodrigopinheiro.B3DataManager.presentation.view.factory.MessageUtils;

public class PaginationHelper {

    public static int calculateTotalPages(long totalItems, int itemsPerPage) {
        return (int) Math.ceil((double) totalItems / itemsPerPage);
    }

    public static void updatePageIndicator(Div pageIndicator, int currentPage, int totalPages, MessageUtils messageUtils) {
        String pageLabel = messageUtils.getString("operacao.grid.paginator.page");
        String ofLabel = messageUtils.getString("operacao.grid.paginator.of");
        pageIndicator.setText(String.format("%s %d %s %d", pageLabel, currentPage, ofLabel, totalPages));
    }
}
