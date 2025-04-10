package tech.formation.springSecurityTraining.securite;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.stereotype.Service;
import tech.formation.springSecurityTraining.entite.Jwt;
import tech.formation.springSecurityTraining.entite.Utilisateur;
import tech.formation.springSecurityTraining.repository.JwtRepository;
import tech.formation.springSecurityTraining.service.UtilisateurService;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;

@Slf4j
@AllArgsConstructor
@Service
public class JwtService {

    private static final String CLE_DE_CHIFFREMENT ="253600e0f8dbe835b112c2b6b54dd48d65ffb156edd4c3a057cd8fda1a149c10";
    public static final String BEARER = "Bearer";
    private UtilisateurService utilisateurService;
    private final JwtRepository jwtRepository;


    public Jwt generate(String username)
    {
        Utilisateur utilisateur = (Utilisateur) this.utilisateurService.loadUserByUsername(username);
        Map<String, Object> jwtGenere = new HashMap<>(this.generateJwt(utilisateur));
        final Jwt jwt = Jwt.builder()
                            .desactive(false)
                            .expire(false)
                            .utilisateur(utilisateur)
                            .value(String.valueOf(jwtGenere.get(BEARER)))
                            .expirationTime((Date)jwtGenere.get("expirationTime"))
                            .creationTime((Date)jwtGenere.get("creationTime"))
                            .build();
        return this.jwtRepository.save(jwt);
    }




    private @NotNull @Unmodifiable Map<String, Object> generateJwt(@NotNull Utilisateur utilisateur)
    {
        final Date currentTime = Date.from(Instant.now());
        final Date expirationTime = Date.from(Instant.now().plus(30, ChronoUnit.MINUTES));

        final Map<String, Object> claims =  Map.of(
                Claims.EXPIRATION, expirationTime,
                Claims.SUBJECT, utilisateur.getEmail(),
                "email", utilisateur.getEmail(),
                "role", String.valueOf(utilisateur.getRole().getLibelle())
        );
       final String bearer = Jwts.builder()
            .issuedAt(currentTime)
            .expiration(expirationTime)
            .subject(utilisateur.getEmail())
            .claims(claims)
            .signWith(getKey())
            .compact();
       log.info("date de creation {}  et date d'expiration {}", currentTime, expirationTime);
       return Map.of(BEARER, bearer, "expirationTime", expirationTime, "creationTime", currentTime);

    }


    @NotNull
    private SecretKey getKey()
    {
        final byte[] decodedBytes = Base64.getDecoder().decode(CLE_DE_CHIFFREMENT);
        return Keys.hmacShaKeyFor(decodedBytes);
    }

    public String lireUsername(String token) {
        return this.getClaim(token, Claims::getSubject);
    }

    public boolean isTokenExpired(String token)
    {
        Date expirationDate = this.getClaim(token, Claims::getExpiration);
        boolean result = expirationDate.before(Date.from(Instant.now()));
        log.info("Token valide {}",  !result);
        return expirationDate.before(Date.from(Instant.now()));

    }




    private <T> T getClaim(String token, @NotNull Function<Claims, T> function)
    {
        Claims claims = getAllClaims(token);
        return function.apply(claims);
    }


    private Claims getAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            throw new RuntimeException("Token invalide: " + e.getMessage());
        }
    }

    public Jwt getTokenByValue(String token) {
        return this.jwtRepository.findByValue(token).orElseThrow(()-> new RuntimeException("Token inconnu"));
    }


    public Jwt deconnexion(String token)
    {
        final Jwt jwt =  this.jwtRepository.findByValue(token).orElseThrow(()->new RuntimeException("Invalid Token"));
        jwt.setDesactive(true);
      //  jwt.setExpire(true);
        return this.jwtRepository.save(jwt);
    }


    public boolean isTokenDesactive(String token)
    {
        Jwt jwt = this.getTokenByValue(token);
        return jwt.isDesactive();
    }


    public void desactiverToken(String token) {
        Jwt jwtDansBDD = this.getTokenByValue(token);
        jwtDansBDD.setDesactive(true);
        jwtDansBDD.setExpire(true);
        this.jwtRepository.save(jwtDansBDD);
    }
}
