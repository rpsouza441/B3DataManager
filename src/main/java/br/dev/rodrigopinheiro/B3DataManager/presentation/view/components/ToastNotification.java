package br.dev.rodrigopinheiro.B3DataManager.presentation.view.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;

public class ToastNotification {

    public static void showError(String message) {
        createNotification(message, NotificationVariant.LUMO_ERROR);
    }

    public static void showWarning(String message) {
        createNotification(message, NotificationVariant.LUMO_CONTRAST);
    }

    public static void showInfo(String message) {
        createNotification(message, NotificationVariant.LUMO_SUCCESS);
    }

    private static void createNotification(String message, NotificationVariant variant) {
        Notification notification = new Notification();
        notification.addThemeVariants(variant);
        notification.setPosition(Notification.Position.TOP_CENTER);

        Div text = new Div();
        text.setText(message);
        text.getStyle().set("text-align", "center");
        text.getStyle().set("width", "100%"); // Centraliza horizontalmente

        Button closeButton = new Button(VaadinIcon.CLOSE.create(), e -> notification.close());
        closeButton.addClassName("close-button");

        Div layout = new Div(text, closeButton);
        layout.getStyle().set("display", "flex");
        layout.getStyle().set("align-items", "center");
        layout.getStyle().set("justify-content", "center");
        layout.getStyle().set("width", "100%");
        layout.getStyle().set("gap", "8px");

        notification.add(layout);
        notification.setDuration(5000);
        notification.open();
    }
}
