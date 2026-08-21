package ru.alimovdev.notification_service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // @EnableWebSecurity включает поддержку Spring Security.
public class SecurityConfig {

    private final Config config;

    @Autowired
    public SecurityConfig(Config config) {
        this.config = config;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        /* HttpSecurity - DSL (предметно-ориентированный язык) для настройки безопасности. Последовательно вызываются
        его методы (authorizeHttpRequests(), httpBasic(), csrf(), formLogin() ...etc), чтобы описать правила защиты
        эндпоинтов. Все эти вызовы не применяются мгновенно, а накапливают конфигурацию во внутреннем строителе.   */
        http.authorizeHttpRequests(auth -> auth
                        // Разрешение аутентификации в healthchec для отображения статуса в терминале
                        .requestMatchers("/actuator/health").permitAll()
                        // Разрешение доступа к публичным эндпоинтам (если есть)
                        .requestMatchers("/api/notifications/send-email").authenticated()
                        // Все остальные запросы требуют аутентификации
                        .anyRequest().authenticated()
                // httpBasic - это метод в конфигураторе HttpSecurity, который включает HTTP Basic аутентификацию.
                // Это механизм, при котором клиент передаёт имя пользователя и пароль в HTTP-заголовке
        ).httpBasic(Customizer.withDefaults());
        /*   Customizer.withDefaults() — статический метод, возвращает пустой (no-op) Customizer. Он не вносит
        изменений в конфигурацию, а просто сообщает Spring Security: "Надо включить HTTP Basic аутентификацию,
        используя все стандартные настройки"    */

        // build() завершает настройку и создаёт объект SecurityFilterChain. Этот объект содержит собранную цепочку
        // фильтров и все заданные правила. Без вызова build() настройки остаются в памяти и не передаются в Spring.
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
    /* UserDetailsService - бин, который хранит учётные данные пользователей в памяти (in-memory).
     Этот бин используется Spring Security для аутентификации (проверки логина и пароля)
     при входящих HTTP-запросах с заголовком Authorization: Basic.
     @return UserDetailsService — сервис, предоставляющий информацию о пользователе (логин, пароль, роли)  */

        // UserDetails — основная модель пользователя в Spring Security.Используется встроенный Builder (упрощает конструирование)
        UserDetails user = User.builder()
                // Имя пользователя (логин) — будет запрашиваться при Basic Auth
                .username(config.auth_username) // как пример - "admin" (оставил значением по умолчанию)
                // Пароль — сохраняется в закодированном виде (BCrypt, чтобы не хранить в открытом виде).
                // PasswordEncoder.encode() хеширует пароль "password"
                .password(passwordEncoder().encode(config.auth_password)) // как например - 111 (оставил значением по умолчанию)
                // Роль пользователя — в Spring Security роли автоматически преобразуются в authorities с префиксом "ROLE_".
                .roles("USER") // Добавляется роль USER.
                // И создается готовый объект UserDetails
                .build();

        /*   InMemoryUserDetailsManager — реализация UserDetailsService, которая хранит пользователей в оперативной
        памяти (в Map - удобно для тестов и демо, в продакшене обычно используют БД). Созданный user передается в
        менеджер, чтобы он мог его найти по имени   */
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
    /*   Создаётся бин PasswordEncoder, который используется Spring Security для
    хеширования паролей (при сохранении) и проверки пароля (при аутентификации).
    Его реализация - BCryptPasswordEncoder — один из самых надёжных и распространённых
    алгоритмов хеширования паролей в Java. Экземпляр BCryptPasswordEncoder, который будет
    внедряться в механизмы аутентификации Spring Security.  */
        return new BCryptPasswordEncoder();
    }

}

/**
BasicAuthenticationFilter — это специальный фильтр, встроенный в Spring Security. Он:
Извлекает логин и пароль из заголовка Authorization.
Проверяет их через AuthenticationManager.
Если логин/пароль не совпадают с теми, что заданы в UserDetailsService, фильтр отклоняет запрос
и возвращает клиенту статус 401 Unauthorized, даже не вызывая контроллер.
Только если аутентификация прошла успешно, Spring Security создаёт аутентифицированный контекст
(SecurityContext), помещает его в SecurityContextHolder и передаёт запрос дальше по цепочке — в ваш контроллер.
Что происходит в контроллере:
К моменту, когда запрос достигает метода sendEmail, он уже прошёл аутентификацию. В коде контроллера вы ничего
не проверяете — вся логика авторизации выполнена фильтрами.
Однако вы можете получить информацию о пользователе, если она вам нужна:

@PostMapping("/send-email")
public String sendEmail(@Valid @RequestBody EmailRequest request,
                        Authentication authentication) {
    // authentication.getName() — логин пользователя
    // authentication.getAuthorities() — его роли
    // Вы можете использовать эти данные, например, для логирования
    System.out.println("Запрос от пользователя: " + authentication.getName());

    emailService.sendEmail(...);
    return "Email sent";
}
Либо через SecurityContextHolder:

Authentication auth = SecurityContextHolder.getContext().getAuthentication();
String username = auth.getName();
Но для вашего приложения это не обязательно.
Почему контроллер "не знает" о клиенте
В обычных условиях контроллеру не нужно знать, кто отправил запрос — это задача уровня безопасности.
Spring Security абстрагирует этот механизм и позволяет контроллеру фокусироваться на бизнес-логике.
Если запрос неавторизован, он даже не дойдёт до контроллера — клиент получит 401 Unauthorized от фильтра.
Если авторизован — контроллер отработает как обычно.
Авторизация: определяет Spring Security до контроллера.
Контроллер: получает запрос только после успешной аутентификации.
Если вы хотите знать, кто отправил запрос, можно получить объект Authentication в методе контроллера.
 */