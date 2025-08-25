package br.dev.rodrigopinheiro.B3DataManager.domain.service;

import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoAtivoFinanceiroFixa;
import org.springframework.stereotype.Service;

@Service
public class TipoAtivoFixaMapperImpl implements TipoAtivoFixaMapper {

    @Override
    public TipoAtivoFinanceiroFixa mapear(String produto) {
        if (produto == null || produto.trim().isEmpty()) {
            return TipoAtivoFinanceiroFixa.DESCONHECIDO;
        }
        // Converte a string para caixa alta para facilitar a comparação
        String produtoUpper = produto.trim().toUpperCase();

        if (produtoUpper.startsWith("TESOURO")) {
            // Considera "TESOURO" como Título Público
            return TipoAtivoFinanceiroFixa.TITULO_PUBLICO;
        }
        if (produtoUpper.startsWith("CDB")) {
            return TipoAtivoFinanceiroFixa.CDB;
        }
        if (produtoUpper.startsWith("LCI")) {
            return TipoAtivoFinanceiroFixa.LCI;
        }
        if (produtoUpper.startsWith("LCA")) {
            return TipoAtivoFinanceiroFixa.LCA;
        }
        if (produtoUpper.startsWith("DEB")) {
            return TipoAtivoFinanceiroFixa.DEBENTURE;
        }
        return TipoAtivoFinanceiroFixa.DESCONHECIDO;
    }
}
