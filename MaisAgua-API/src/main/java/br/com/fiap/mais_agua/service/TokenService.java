package br.com.fiap.mais_agua.service;

import br.com.fiap.mais_agua.model.Token;
import br.com.fiap.mais_agua.model.Usuario;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {
    private Instant expiresAt = LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.ofHours(-3));

    private Algorithm algorithm = Algorithm.HMAC256("secret");
    public Token createToken(Usuario user
    ){
        var jwt = JWT.create()
                .withSubject(user.getIdUsuario().toString())
                .withClaim("email", user.getEmail())
                .withExpiresAt(expiresAt)
                .sign(algorithm);

        return new Token(jwt, user.getEmail());
    }

    public Usuario getUserFromToken(String token){
        var verifiedToken = JWT.require(algorithm).build().verify(token);

        return Usuario.builder()
                .idUsuario(Integer.valueOf(verifiedToken.getSubject()))
                .email(verifiedToken.getClaim("email").toString()).build();
    }
}
