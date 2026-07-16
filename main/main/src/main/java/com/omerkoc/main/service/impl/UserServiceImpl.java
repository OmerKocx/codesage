package com.omerkoc.main.service.impl;

import com.omerkoc.main.dto.LoginResponse;
import com.omerkoc.main.dto.UserDto;
import com.omerkoc.main.dto.UserLoginRequest;
import com.omerkoc.main.dto.UserRegisterRequest;
import com.omerkoc.main.exceptions.BadRequestException;
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

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.admin-username}")
    private String adminUsername;

    @Value("${keycloak.admin-password}")
    private String adminPassword;

    @Override
    public UserDto register(UserRegisterRequest request) {
        // 1. Check if user already exists locally
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email is already in use");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username is already in use");
        }

        // 2. Get Keycloak Admin Access Token
        String adminToken = getAdminToken();

        // 3. Register user in Keycloak
        String createUserUrl = serverUrl + "/admin/realms/" + realm + "/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);

        Map<String, Object> credential = new HashMap<>();
        credential.put("type", "password");
        credential.put("value", request.getPassword());
        credential.put("temporary", false);

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
            ResponseEntity<Void> response = restTemplate.postForEntity(createUserUrl, entity, Void.class);
            if (response.getStatusCode() == HttpStatus.CREATED) {
                List<String> locations = response.getHeaders().get(HttpHeaders.LOCATION);
                if (locations != null && !locations.isEmpty()) {
                    String location = locations.get(0);
                    keycloakUserId = location.substring(location.lastIndexOf("/") + 1);
                }
            }
        } catch (org.springframework.web.client.HttpClientErrorException.Conflict e) {
            // User already exists in Keycloak (Self-healing branch)
            keycloakUserId = getKeycloakUserIdByUsername(adminToken, request.getUsername());
        } catch (Exception e) {
            throw new BadRequestException("Failed to register user in Keycloak: " + e.getMessage());
        }

        if (keycloakUserId == null) {
            throw new BadRequestException("Failed to retrieve user ID from Keycloak");
        }

        // 4. Save user in local database
        User localUser = new User();
        localUser.setId(UUID.fromString(keycloakUserId));
        localUser.setUsername(request.getUsername());
        localUser.setEmail(request.getEmail());
        localUser.setFirstName(request.getFirstName());
        localUser.setLastName(request.getLastName());

        User savedUser = userRepository.save(localUser);

        return UserDto.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .build();
    }

    @SuppressWarnings("unchecked")
    private String getKeycloakUserIdByUsername(String adminToken, String username) {
        String url = serverUrl + "/admin/realms/" + realm + "/users?username=" + username;

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
                Map<String, Object> user = (Map<String, Object>) response.getBody().get(0);
                return (String) user.get("id");
            }
        } catch (Exception e) {
            throw new BadRequestException("Failed to retrieve existing user from Keycloak: " + e.getMessage());
        }
        throw new BadRequestException("User exists in Keycloak but could not be retrieved");
    }

    @SuppressWarnings("unchecked")
    @Override
    public LoginResponse login(UserLoginRequest request) {
        String tokenUrl = serverUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "password");
        map.add("client_id", clientId);
        map.add("username", request.getUsername());
        map.add("password", request.getPassword());

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<Map> response;
        try {
            response = restTemplate.postForEntity(tokenUrl, entity, Map.class);
        } catch (Exception e) {
            throw new BadRequestException("Invalid username or password");
        }

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new BadRequestException("Invalid username or password");
        }

        Map<String, Object> body = response.getBody();
        return LoginResponse.builder()
                .accessToken((String) body.get("access_token"))
                .refreshToken((String) body.get("refresh_token"))
                .expiresIn((Integer) body.get("expires_in"))
                .refreshExpiresIn((Integer) body.get("refresh_expires_in"))
                .tokenType((String) body.get("token_type"))
                .build();
    }

    @SuppressWarnings("unchecked")
    private String getAdminToken() {
        String adminTokenUrl = serverUrl + "/realms/master/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "password");
        map.add("client_id", "admin-cli");
        map.add("username", adminUsername);
        map.add("password", adminPassword);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<Map> response;
        try {
            response = restTemplate.postForEntity(adminTokenUrl, entity, Map.class);
        } catch (Exception e) {
            throw new BadRequestException("Failed to authenticate with Keycloak as Admin: " + e.getMessage());
        }

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new BadRequestException("Failed to retrieve admin token from Keycloak");
        }

        return (String) response.getBody().get("access_token");
    }
}
