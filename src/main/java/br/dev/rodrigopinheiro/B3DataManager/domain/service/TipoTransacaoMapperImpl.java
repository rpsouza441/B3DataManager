package br.dev.rodrigopinheiro.B3DataManager.domain.service;

import br.dev.rodrigopinheiro.B3DataManager.domain.enums.TipoTransacao;
import org.springframework.stereotype.Service;

@Service
public class TipoTransacaoMapperImpl implements TipoTransacaoMapper {

    @Override
    public TipoTransacao mapear(String sinal, String descricao) {
        if (descricao == null || descricao.trim().isEmpty() || sinal == null) {
            return TipoTransacao.OUTRA;
        }

        String mov = descricao.toUpperCase().trim();
        String tipo = sinal.toUpperCase().trim();

        if (mov.contains("TRANSFERÊNCIA") && !mov.contains("COMPRA / VENDA")) {
            return TipoTransacao.TRANSFERENCIA;
        }

        if (mov.contains("COMPRA / VENDA")) {
            if (tipo.equals("ENTRADA")) {
                return TipoTransacao.VENDA;
            } else if (tipo.equals("SAIDA")) {
                return TipoTransacao.ENTRADA;
            }
        }

        if (tipo.equals("ENTRADA")) {
            if (mov.contains("RENDIMENTO")) {
                return TipoTransacao.LUCRO_RENDIMENTO;
            }
            if (mov.contains("DIVIDENDO")) {
                return TipoTransacao.LUCRO_DIVIDENDO;
            }
            if (mov.contains("JUROS SOBRE CAPITAL PRÓPRIO") || mov.contains("PAGAMENTO DE JUROS")) {
                return TipoTransacao.LUCRO_JUROS;
            }
            if (mov.contains("RESGATE") || mov.contains("AMORTIZAÇÃO") || mov.contains("BONIFICAÇÃO")) {
                return TipoTransacao.LUCRO_OUTRA;
            }
        }

        if (mov.contains("TAXA") || mov.contains("COBRANÇA")) {
            return TipoTransacao.TAXA;
        }

        return TipoTransacao.OUTRA;
    }
}
