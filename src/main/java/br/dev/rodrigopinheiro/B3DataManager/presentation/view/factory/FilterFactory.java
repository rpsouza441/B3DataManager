package br.dev.rodrigopinheiro.B3DataManager.presentation.view.factory;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.textfield.TextField;

import java.util.List;
import java.util.ResourceBundle;

public class FilterFactory {

    private final ResourceBundle messages;

    public FilterFactory(ResourceBundle messages) {
        this.messages = messages;
    }

    /**
     * Cria um ComboBox configurado para valores de enum.
     *
     * @param labelKey       A chave da mensagem para o rótulo.
     * @param enumValues     Os valores do enum.
     * @param labelGenerator Função para gerar o rótulo dos itens.
     * @param <E>            Tipo do enum.
     * @return O ComboBox configurado.
     */
    public <E extends Enum<E>> ComboBox<E> createComboBoxFilterWithEnum(String labelKey, E[] enumValues, ItemLabelGenerator<E> labelGenerator) {
        ComboBox<E> comboBox = new ComboBox<>(messages.getString(labelKey));
        comboBox.setItems(enumValues);
        comboBox.setItemLabelGenerator(labelGenerator); // Usa o ItemLabelGenerator diretamente
        return comboBox;
    }

    /**
     * Cria um ComboBox configurado para itens simples, como Strings.
     *
     * @param labelKey A chave da mensagem para o rótulo do ComboBox.
     * @param items    A lista de itens para exibir no ComboBox.
     * @param <T>      O tipo dos itens no ComboBox.
     * @return O ComboBox configurado.
     */
    public <T> ComboBox<T> createComboBoxFilter(String labelKey, List<T> items) {
        ComboBox<T> comboBox = new ComboBox<>(messages.getString(labelKey));
        comboBox.setItems(items);
        return comboBox;
    }

    /**
     * Cria um TextField com um rótulo baseado na chave fornecida.
     *
     * @param labelKey A chave da mensagem para o rótulo.
     * @return O campo de texto configurado.
     */
    public TextField createTextFieldFilter(String labelKey) {
        TextField textField = new TextField();
        textField.setLabel(messages.getString(labelKey));
        return textField;
    }

    public DatePicker createDatePicker(String labelKey) {
        DatePicker datePicker = new DatePicker(messages.getString(labelKey));
        return datePicker;
    }

    public ComboBox<Boolean> createBooleanFilter(String labelKey) {
        ComboBox<Boolean> comboBox = new ComboBox<>(messages.getString(labelKey));
        comboBox.setItems(true, false);
        comboBox.setItemLabelGenerator(value -> value ? messages.getString("global.yes") : messages.getString("global.no"));
        return comboBox;
    }

    public Button createButton(String labelKey, ButtonVariant variant, ComponentEventListener<ClickEvent<Button>> listener) {
        Button button = new Button(messages.getString(labelKey), listener);
        button.addThemeVariants(variant);
        return button;
    }
}
