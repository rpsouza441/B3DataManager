package br.dev.rodrigopinheiro.B3DataManager.domain.enums;

/**
 * Enum que representa o status de um DARF
 */
public enum StatusDarf {
    PENDENTE("Pendente"),
    PAGO("Pago"),
    VENCIDO("Vencido");

    private final String descricao;

    StatusDarf(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}