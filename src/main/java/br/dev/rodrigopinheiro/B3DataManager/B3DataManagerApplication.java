package br.dev.rodrigopinheiro.B3DataManager;

import br.dev.rodrigopinheiro.B3DataManager.application.service.UsuarioService;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.Theme;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@Push
@SpringBootApplication
@NpmPackage(value = "@fontsource/roboto-condensed", version = "4.5.0")
@Theme(value = "my-app")
@EnableBatchProcessing
@EnableScheduling
public class B3DataManagerApplication implements AppShellConfigurator {


    public static void main(String[] args) {
        SpringApplication.run(B3DataManagerApplication.class, args);
    }

    @Bean
    public CommandLineRunner initUsuarios(UsuarioService usuarioService) {

        return args -> usuarioService.criarUsuariosPadrao();
    }

}
