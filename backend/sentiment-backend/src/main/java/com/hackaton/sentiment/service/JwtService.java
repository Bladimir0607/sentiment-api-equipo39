package com.hackaton.sentiment.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Servicio para la generación, validación y procesamiento de tokens JWT (JSON Web Tokens).
 *
 * Esta clase implementa toda la lógica relacionada con la creación y verificación
 * de tokens JWT utilizados para la autenticación en la aplicación. Utiliza la librería
 * jjwt (Java JWT) para manejar las operaciones criptográficas.
 *
 * Los tokens generados incluyen claims estándar como subject (username), fecha de
 * emisión y fecha de expiración, y están firmados utilizando el algoritmo HMAC SHA-256.

 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Extrae el nombre de usuario (subject) de un token JWT.
     *
     * @param token Token JWT del cual extraer el nombre de usuario
     * @return Nombre de usuario contenido en el token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae un claim específico de un token JWT usando una función de extracción.
     *
     * @param <T> Tipo de dato del claim a extraer
     * @param token Token JWT del cual extraer el claim
     * @param claimsResolver Función que especifica cómo extraer el claim deseado
     * @return El claim extraído del token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Genera un token JWT para un usuario sin claims adicionales.
     *
     * @param userDetails Detalles del usuario para quien se generará el token
     * @return Token JWT generado
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Genera un token JWT con claims adicionales personalizados.
     *
     * @param extraClaims Claims adicionales a incluir en el token
     * @param userDetails Detalles del usuario para quien se generará el token
     * @return Token JWT generado con los claims especificados
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Valida si un token JWT es válido para un usuario específico.
     *
     * Verifica que el nombre de usuario en el token coincida con el usuario proporcionado
     * y que el token no haya expirado.
     *
     * @param token Token JWT a validar
     * @param userDetails Detalles del usuario contra el cual validar el token
     * @return true si el token es válido para el usuario, false en caso contrario
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Verifica si un token JWT ha expirado.
     *
     * Private a public
     *
     * @param token Token JWT a verificar
     * @return true si el token ha expirado, false si aún es válido
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrae la fecha de expiración de un token JWT.
     *
     * @param token Token JWT del cual extraer la fecha de expiración
     * @return Fecha de expiración del token
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrae todos los claims de un token JWT.
     *
     * @param token Token JWT del cual extraer los claims
     * @return Objeto Claims con todos los claims del token
     * @throws io.jsonwebtoken.JwtException si el token es inválido o la firma no coincide
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Obtiene la clave de firma para tokens JWT.
     *
     * Decodifica la clave secreta desde Base64 y crea una clave HMAC SHA
     * compatible con la librería jjwt.
     *
     * @return Clave de firma para tokens JWT
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);  //
    }

    /**
     * Valida la integridad y vigencia de un token JWT.
     *
     * Verifica que el token esté correctamente formado, tenga una firma válida
     * y no haya expirado.
     *
     * @param token Token JWT a validar
     * @return true si el token es válido, false si hay cualquier error
     */
    public boolean validateToken(String token) {
        try {
            // Extraer claims (esto valida la firma)
            Claims claims = extractAllClaims(token);

            // Verificar expiración
            return !isTokenExpired(token);

        } catch (Exception e) {
            // Si hay cualquier error (firma inválida, token malformado, etc.)
            return false;
        }
    }

    /**
     * Método de depuración que se ejecuta después de la construcción del bean.
     *
     * Imprime información sobre la carga de la clave secreta JWT para fines
     * de diagnóstico durante el desarrollo.
     */
    @PostConstruct
    public void debugJwt() {
        System.out.println("JWT SECRET LOADED: " + (secretKey != null));
        System.out.println("JWT SECRET VALUE: " + secretKey);
    }

}