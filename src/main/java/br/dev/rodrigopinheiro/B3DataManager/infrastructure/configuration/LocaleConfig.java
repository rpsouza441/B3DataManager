package br.dev.rodrigopinheiro.B3DataManager.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Locale;

/**
 * Configuração para gerenciamento de Locale (internacionalização e localização).
 */
@Configuration
public class LocaleConfig implements WebMvcConfigurer {

    /**
     * Define o resolvedor de Locale com o idioma padrão.
     *
     * @return um resolvedor de Locale.
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.getDefault()); // Define o idioma padrão
        return slr;
    }

    /**
     * Define o interceptor para troca de Locale baseado em um parâmetro de requisição (ex.: ?lang=pt).
     *
     * @return um interceptor para mudanças de Locale.
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang"); // Parâmetro usado para alterar o idioma
        return lci;
    }

    /**
     * Adiciona o interceptor ao registro de interceptores do Spring.
     *
     * @param registry Registro de interceptores.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    /**
     * Configura o MessageSource para carregar mensagens com UTF-8.
     *
     * @return Configuração do MessageSource.
     */
    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages"); // Localização dos arquivos messages.properties
        messageSource.setDefaultEncoding("UTF-8"); // Configurar UTF-8 como padrão
        return messageSource;
    }
}
