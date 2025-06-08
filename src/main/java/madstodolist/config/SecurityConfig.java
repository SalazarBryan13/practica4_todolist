package madstodolist.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import madstodolist.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import madstodolist.dto.UsuarioData;
import madstodolist.authentication.ManagerUserSession;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ManagerUserSession managerUserSession;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        logger.debug("Configurando seguridad HTTP");
        http
            .csrf().disable()
            .authorizeRequests()
                .antMatchers("/", "/registro", "/login", "/about", "/css/**", "/js/**", "/h2-console/**").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("eMail")
                .passwordParameter("password")
                .successHandler((request, response, authentication) -> {
                    String email = authentication.getName();
                    UsuarioData usuario = usuarioService.findByEmail(email);
                    if (usuario != null) {
                        managerUserSession.logearUsuario(usuario.getId(), usuario.getNombre());
                        if (usuario.isAdmin()) {
                            response.sendRedirect("/registrados");
                        } else {
                            response.sendRedirect("/usuarios/" + usuario.getId() + "/tareas");
                        }
                    } else {
                        response.sendRedirect("/login?error=true");
                    }
                })
                .failureUrl("/login?error=true")
                .permitAll()
                .and()
            .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            .and()
            .exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> {
                    logger.error("Error de autenticación: {}", authException.getMessage());
                    response.sendRedirect("/login?error=true");
                });
            
        // Configuración de headers para H2 console
        http.headers().frameOptions().disable();
        
        logger.debug("Configuración de seguridad HTTP completada");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        logger.debug("Configurando autenticación global");
        try {
            auth.userDetailsService(usuarioService)
                .passwordEncoder(passwordEncoder);
            logger.debug("Configuración de autenticación global completada");
        } catch (Exception e) {
            logger.error("Error configurando autenticación global", e);
            throw e;
        }
    }
} 