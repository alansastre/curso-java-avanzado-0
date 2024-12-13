

## Spring Security

Hasta Spring 5.7 se usaba WebSecurityConfigurerAdapter en favor de configuración basada en crear beans @Bean


Clases de Spring Security para Spring Web:

* SecurityFilterChain
* SecurityContextHolder
* HttpSecurity
* OncePerRequestFilter
* UsernamePasswordAuthenticationFilter
* AuthenticationManager
* UserDetailsService

Clases de Spring Security para Spring WebFlux:

* SecurityWebFilterChain
* ReactiveSecurityContextHolder
* ServerHttpSecurity
* WebFilter
* AuthenticationWebFilter
* ReactiveAuthenticationManager
* ReactiveUserDetailsService

Opciones:

* Microservicios:
  * OAuth 2 Resource server
  * Keycloak / Okta para delegar la generación y validación de tokens y base de datos de usuarios o crear nuestro propio OAuth 2 Authentication Server utilizando el starter de Spring para ello.
    * Configurar application.properties o application.yml el endpoint al realm y propiedades para mapear JWT o crear un converter

* Monolítico:
  * Crear, firmar y validar tokens JWT utilizando la librería jjwt
  * AuthenticationController:
    * register: registra usuario y codifica password PasswordEncoder
    * login: comprueba credenciales y genera token JWT con datos de usuario y firmado con clave secreta y lo devuelve
  * filtro JWT: 
  * Entry point para customizar el error en caso de no estar autenticado
  * SecurityUtils para obtener el usuario de SecurityContextHolder
  * Seguridad por a nivel de método: @Secured, @PreAuthorized, @PostAuthorized


STATUS HTTP PARA SEGURIDAD:

* 401 Unauthorized si la petición no está autenticada, es decir, no tiene token JWT y no se puede identificar usuario
* 403 Forbidden si la petición sí está autenticada, pero el rol no es el adecuado para acceder al recurso solicitado

