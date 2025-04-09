package tech.formation.springSecurityTraining.securite;



import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class ConfigurationSecuriteApplication {


    private final JwtFilter jwtFilter;
    private final BCryptPasswordEncoder passwordEncoder;




    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception
    {
        return
            httpSecurity.csrf(AbstractHttpConfigurer::disable)
                        .authorizeHttpRequests(authorize ->
                                authorize.requestMatchers("/inscription").permitAll()
                                         .requestMatchers("/activation").permitAll()
                                         .requestMatchers("/connexion").permitAll()
                                         .requestMatchers("/envoyer-code").permitAll()
                                         .requestMatchers("/changer-mot-de-passe").permitAll()
                                         .anyRequest().authenticated()

                        )
                    .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                    .build();
    }





    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception
    {
        return authenticationConfiguration.getAuthenticationManager();
    }



    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService)
    {
        DaoAuthenticationProvider daoAuthenticationConfigurer = new DaoAuthenticationProvider();
        daoAuthenticationConfigurer.setUserDetailsService(userDetailsService);
        daoAuthenticationConfigurer.setPasswordEncoder(this.passwordEncoder);
        return daoAuthenticationConfigurer;
    }
}
