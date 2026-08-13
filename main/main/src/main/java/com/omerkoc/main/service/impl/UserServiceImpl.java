package com.omerkoc.main.service.impl;

import com.omerkoc.main.dto.LoginResponse;
import com.omerkoc.main.dto.UserDto;
import com.omerkoc.main.dto.UserLoginRequest;
import com.omerkoc.main.dto.UserRegisterRequest;
import com.omerkoc.main.dto.UserLogoutRequest;
import com.omerkoc.main.exceptions.InvalidCredentialsException;
import com.omerkoc.main.exceptions.KeycloakAccessException;
import com.omerkoc.main.exceptions.UserAlreadyExistsException;
import com.omerkoc.main.model.User;
import com.omerkoc.main.repository.UserRepository;
import com.omerkoc.main.service.IUserService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service // Spring Framework'e bu sınıfın bir servis bileşeni olduğunu ve IoC container
         // tarafından yönetileceğini belirtir.
@RequiredArgsConstructor // final olan tüm alanlar (fields) için otomatik olarak bir constructor
                         // oluşturur (Constructor Injection).
public class UserServiceImpl implements IUserService {

    // Yerel veritabanı işlemleri için kullanılan JPA Repository bağımlılığı
    private final UserRepository userRepository;

    // Keycloak REST API'lerine HTTP çağrıları yapmak için kullanılan Spring
    // RestTemplate nesnesi
    private final RestTemplate restTemplate = new RestTemplate();

    // application.properties veya application.yml dosyasından okunan Keycloak
    // sunucu parametreleri
    @Value("${keycloak.server-url}")
    private String serverUrl; // Keycloak sunucusunun adresi (örn: http://localhost:8080)

    @Value("${keycloak.realm}")
    private String realm; // İşlem yapılacak Keycloak Realm alanı

    @Value("${keycloak.client-id}")
    private String clientId; // Keycloak üzerindeki uygulamanın Client ID'si

    @Value("${keycloak.client-secret}")
    private String clientSecret; // Keycloak Client Secret key

    @Value("${keycloak.admin-username}")
    private String adminUsername; // Keycloak admin yetkisine sahip kullanıcı adı

    @Value("${keycloak.admin-password}")
    private String adminPassword; // Keycloak admin şifresi

    /**
     * Yeni bir kullanıcının sisteme kaydolmasını sağlar.
     * Süreç hem yerel veritabanında hem de Keycloak üzerinde eş zamanlı yürütülür.
     *
     * @param request Kaydolacak kullanıcının bilgileri
     * @return Kaydedilen kullanıcının DTO gösterimi
     */

    @Override
    public UserDto register(UserRegisterRequest request) {
        // 1. E-posta ve Kullanıcı adı yerel veritabanında benzersiz olmalıdır
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email is already in use");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username is already in use");
        }

        // 2. Keycloak üzerinde işlem yapabilmek için admin yetkisine sahip bir Access
        // Token alınır
        String adminToken = getAdminToken();

        // 3. Keycloak üzerinde yeni bir kullanıcı oluşturmak üzere REST isteği
        // hazırlanır
        String createUserUrl = serverUrl + "/admin/realms/" + realm + "/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken); // İstek yetkilendirme başlığına Admin Token eklenir

        // Kullanıcı şifre bilgisinin oluşturulması
        Map<String, Object> credential = new HashMap<>();
        credential.put("type", "password");
        credential.put("value", request.getPassword());
        credential.put("temporary", false); // Kullanıcı ilk girişte şifre değiştirmek zorunda olmasın

        // Keycloak API'sinin beklediği JSON şablonuna uygun kullanıcı verilerinin
        // haritalanması
        Map<String, Object> keycloakUser = new HashMap<>();
        keycloakUser.put("username", request.getUsername());
        keycloakUser.put("email", request.getEmail());
        keycloakUser.put("firstName", request.getFirstName());
        keycloakUser.put("lastName", request.getLastName());
        keycloakUser.put("enabled", true);
        keycloakUser.put("credentials", Collections.singletonList(credential));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(keycloakUser, headers);

        String keycloakUserId = null;
        try {
            // POST isteği ile Keycloak'a kullanıcı kaydı gönderilir
            ResponseEntity<Void> response = restTemplate.postForEntity(createUserUrl, entity, Void.class);

            // Keycloak başarılı kayıt sonrası 201 Created döner ve Location header'ında
            // yeni kullanıcının id'sini verir
            if (response.getStatusCode() == HttpStatus.CREATED) {
                List<String> locations = response.getHeaders().get(HttpHeaders.LOCATION);
                if (locations != null && !locations.isEmpty()) {
                    String location = locations.get(0);
                    // Location URL'sinin sonundaki UUID (kullanıcı id'si) ayıklanır
                    keycloakUserId = location.substring(location.lastIndexOf("/") + 1);
                }
            }
        } catch (org.springframework.web.client.HttpClientErrorException.Conflict e) {
            // Kullanıcı Keycloak tarafında zaten mevcutsa (Örn: Veritabanı tutarsızlıkları
            // sonrasındaki kendini iyileştirme adımı),
            // Mevcut kullanıcının Keycloak ID'si aranıp bulunur.
            keycloakUserId = getKeycloakUserIdByUsername(adminToken, request.getUsername());
        } catch (Exception e) {
            throw new KeycloakAccessException("Failed to register user in Keycloak: " + e.getMessage());
        }

        if (keycloakUserId == null) {
            throw new KeycloakAccessException("Failed to retrieve user ID from Keycloak");
        }

        // 4. Keycloak'ta başarıyla oluşturulan kullanıcının ID'si referans alınarak
        // yerel veritabanına kaydedilir
        User localUser = new User();
        localUser.setId(UUID.fromString(keycloakUserId)); // UUID olarak Keycloak ID'si set edilir
        localUser.setUsername(request.getUsername());
        localUser.setEmail(request.getEmail());
        localUser.setFirstName(request.getFirstName());
        localUser.setLastName(request.getLastName());

        User savedUser = userRepository.save(localUser);

        // Kullanıcıya şifre gibi kritik bilgileri içermeyen DTO döndürülür
        return UserDto.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .build();
    }

    /**
     * Kullanıcı adı ile Keycloak sunucusundaki kullanıcı ID'sini sorgular.
     *
     * @param adminToken Yetkilendirilmiş admin token'ı
     * @param username   Sorgulanacak kullanıcı adı
     * @return Keycloak üzerindeki UUID formatındaki kullanıcı ID'si
     */
    @SuppressWarnings("unchecked")
    private String getKeycloakUserIdByUsername(String adminToken, String username) {
        String url = serverUrl + "/admin/realms/" + realm + "/users?username=" + username;
        // url: http://localhost:8080/admin/realms/codesage/users?username=glaymet

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<List> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    List.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null
                    && !response.getBody().isEmpty()) {
                // Keycloak liste döndüğü için eşleşen ilk kullanıcının ID'si alınır
                Map<String, Object> user = (Map<String, Object>) response.getBody().get(0);
                return (String) user.get("id");
            }
        } catch (Exception e) {
            throw new KeycloakAccessException("Failed to retrieve existing user from Keycloak: " + e.getMessage());
        }
        throw new KeycloakAccessException("User exists in Keycloak but could not be retrieved");
    }

    /**
     * Kullanıcının giriş yapmasını (Login) ve Keycloak'tan Access/Refresh Token
     * almasını sağlar.
     *
     * @param request Kullanıcının giriş bilgileri (username, password)
     * @return Keycloak'tan dönen token ve geçerlilik süreleri bilgisi
     */

    @SuppressWarnings("unchecked")
    @Override
    public LoginResponse login(UserLoginRequest request) {
        // Keycloak OpenID Connect Token uç noktası (endpoint)
        String tokenUrl = serverUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        // Giriş isteği "application/x-www-form-urlencoded" tipinde form parametreleri
        // ile yapılmalıdır
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // OAuth2 password grant type akışına göre parametrelerin doldurulması
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "password");
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("username", request.getUsername());
        map.add("password", request.getPassword());

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<Map> response;
        try {
            response = restTemplate.postForEntity(tokenUrl, entity, Map.class);
        } catch (Exception e) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        // Başarılı giriş durumunda Keycloak'tan gelen token verileri çözümlenir ve
        // Response nesnesine dönüştürülür
        Map<String, Object> body = response.getBody();
        return LoginResponse.builder()
                .accessToken((String) body.get("access_token"))
                .refreshToken((String) body.get("refresh_token"))
                .expiresIn((Integer) body.get("expires_in"))
                .refreshExpiresIn((Integer) body.get("refresh_expires_in"))
                .tokenType((String) body.get("token_type"))
                .build();
    }

    /**
     * Keycloak yönetimsel (admin) işlemlerini gerçekleştirebilmek amacıyla Master
     * Realm üzerinden
     * "admin-cli" client'ını kullanarak bir admin access token'ı alır.
     *
     * @return Admin erişim token string değeri
     */
    @SuppressWarnings("unchecked")
    private String getAdminToken() {
        // Keycloak Master Realm OpenID Connect Token uç noktası
        String adminTokenUrl = serverUrl + "/realms/master/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Master Realm'de kimlik doğrulama için gerekli parametreler
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "password");
        map.add("client_id", "admin-cli"); // Keycloak admin CLI istemcisi
        map.add("username", adminUsername);
        map.add("password", adminPassword);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<Map> response;
        try {
            response = restTemplate.postForEntity(adminTokenUrl, entity, Map.class);
        } catch (Exception e) {
            throw new KeycloakAccessException("Failed to authenticate with Keycloak as Admin: " + e.getMessage());
        }

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new KeycloakAccessException("Failed to retrieve admin token from Keycloak");
        }

        // Alınan access token değeri döndürülür
        return (String) response.getBody().get("access_token");
    }

    /**
     * Kullanıcının sistemden çıkış yapmasını sağlar.
     * Keycloak üzerindeki aktif oturumu (session) sonlandırmak amacıyla refresh
     * token'ı revoke eder.
     *
     * @param request Çıkış yapmak isteyen kullanıcının refresh token bilgisini
     *                içeren nesne
     */
    @Override
    public void logout(UserLogoutRequest request) {
        String logoutUrl = serverUrl + "/realms/" + realm + "/protocol/openid-connect/logout";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", clientId);
        map.add("refresh_token", request.getRefreshToken());

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        try {
            ResponseEntity<Void> response = restTemplate.postForEntity(logoutUrl, entity, Void.class);
            if (response.getStatusCode() != HttpStatus.NO_CONTENT && response.getStatusCode() != HttpStatus.OK) {
                throw new KeycloakAccessException(
                        "Failed to logout from Keycloak. Server responded with status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new KeycloakAccessException("Failed to perform logout in Keycloak: " + e.getMessage());
        }
    }
}
