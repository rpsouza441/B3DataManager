package br.dev.rodrigopinheiro.B3DataManager.domain.enums;

public enum Roles {
    ADMIN,
    USER;

    public String getRoleName() {
        return "ROLE_" + this.name();
    }
}
