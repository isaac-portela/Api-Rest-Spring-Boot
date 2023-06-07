package med.voll.api.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import med.voll.api.domain.usuario.UsuarioRepository;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // funcao chamada uma vez para cada requisicao
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var tokenJWT = recuperarToken(request);

        if(tokenJWT != null){
            var subject = tokenService.getSubject(tokenJWT); //pega o login do usuario que esta no token
            var usuario = usuarioRepository.findByLogin(subject); //pega o usuario do banco de dados

            // cria um objeto dto de autenticacao que representa o usuario
            var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
            //força a autenticacao do usuario
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        //continua o fluxo da requisicao
        filterChain.doFilter(request, response);
    }

    public String recuperarToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization"); //retorna o valor do header do procolo Authorization, no caso o token
        if (authorizationHeader != null) {
            return authorizationHeader.replace("Bearer ", "").trim();
        }

        return null;
    }
}
