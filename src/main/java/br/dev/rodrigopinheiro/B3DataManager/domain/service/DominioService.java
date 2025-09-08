package br.dev.rodrigopinheiro.B3DataManager.domain.service;

import br.dev.rodrigopinheiro.B3DataManager.domain.model.Operacao;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.persistence.entity.AtivoFinanceiroEntity;
import br.dev.rodrigopinheiro.B3DataManager.infrastructure.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class DominioService {

    private final UsuarioRepository usuarioRepository;

    public DominioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public AtivoFinanceiroEntity criarAtivo(Operacao operacao) {
        AtivoFinanceiroEntity ativo = new AtivoFinanceiroEntity();

        //TODO Criar logica de conversão.
//        ativo.setNome(operacao.getProduto());
//        ativo.setQuantidade(operacao.getQuantidade());
//        ativo.setPrecoMedio(operacao.getPrecoUnitario());
//        ativo.calcularTotal();
//
//        // Define o usuário associado à operação
//        ativo.setUsuario(usuarioRepository.findById(operacao.getUsuarioId())
//                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com ID: " + operacao.getUsuarioId())));

        return ativo;
    }
}
