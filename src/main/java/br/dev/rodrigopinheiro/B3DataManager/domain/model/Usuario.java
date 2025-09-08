package br.dev.rodrigopinheiro.B3DataManager.domain.model;

import br.dev.rodrigopinheiro.B3DataManager.domain.enums.Roles;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.PortfolioEntity;

import java.util.Set;

public class Usuario {

    private Long id;
    private String username;
    private String password;
    private String email;
    private Set<Roles> roles;
    private Set<Instituicao> instituicoes;
    private PortfolioEntity portfolio;
    private Boolean deletado = false;

    public Usuario() {
    }

    public Usuario(Long id, String username, String password, String email, Set<Roles> roles, Set<Instituicao> instituicoes, PortfolioEntity portfolio, Boolean deletado) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.roles = roles;
        this.instituicoes = instituicoes;
        this.portfolio = portfolio;
        this.deletado = deletado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Roles> getRoles() {
        return roles;
    }

    public void setRoles(Set<Roles> roles) {
        this.roles = roles;
    }

    public Set<Instituicao> getInstituicoes() {
        return instituicoes;
    }

    public void setInstituicoes(Set<Instituicao> instituicoes) {
        this.instituicoes = instituicoes;
    }

    public PortfolioEntity getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(PortfolioEntity portfolio) {
        this.portfolio = portfolio;
    }

    public Boolean getDeletado() {
        return deletado;
    }

    public void setDeletado(Boolean deletado) {
        this.deletado = deletado;
    }
    
}
