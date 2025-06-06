package com.alex.projectComment.User.auth;

import com.alex.projectComment.User.dtos.RoleDTO;
import com.alex.projectComment.User.entities.Role;
import com.alex.projectComment.User.entities.User;
import com.alex.projectComment.User.repositories.RoleRepository;
import com.alex.projectComment.User.repositories.UserRepository;
import com.alex.projectComment.User.services.RoleService;
import com.alex.projectComment.enums.StatusEnum;
import com.alex.projectComment.infra.exceptions.AlreadyInUseException;
import com.alex.projectComment.infra.exceptions.EntityNotFoundException;
import com.alex.projectComment.infra.security.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final TokenService tokenService;
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final RoleService roleService;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public AuthLoginResponseDTO login(UserLoginRequestDTO userLoginRequestDTO) {
    if (userLoginRequestDTO.username().isBlank()) {
      throw new IllegalArgumentException("O nome de Usuário não pode ser vazio.");
    }

    if (userLoginRequestDTO.password() == null || userLoginRequestDTO.password().isBlank()) {
      throw new IllegalArgumentException("A senha não pode ser vazia.");
    }

    User user = userRepository.findByUsernameAndStatusIgnoreCase(userLoginRequestDTO.username(), StatusEnum.ACTIVE)
        .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado/cadastrado."));

    if (!passwordEncoder.matches(userLoginRequestDTO.password(), user.getPassword())) {
      throw new IllegalArgumentException("Senha inválida.");
    }

    String token = tokenService.generateKeyToken(user);
    return new AuthLoginResponseDTO(user.getUsername(), token, "Bem Vindo de Volta");
  }

  @Transactional
  public AuthLoginResponseDTO register(UserRegisterRequestDTO userRegisterRequestDTO) {
    if (userRegisterRequestDTO.email().isBlank()) {
      throw new IllegalArgumentException("O Email não pode ser vazio.");
    }

    if (userRegisterRequestDTO.username().isBlank()) {
      throw new IllegalArgumentException("O nome de Usuário não pode ser vazio.");
    }

    if (userRegisterRequestDTO.password() == null || userRegisterRequestDTO.password().isBlank()) {
      throw new IllegalArgumentException("A senha não pode ser vazia.");
    }

    if (userRepository.existsByEmailAndStatusIgnoreCase(userRegisterRequestDTO.email(), StatusEnum.ACTIVE)) {
      throw new AlreadyInUseException("Este Email já esta em uso.");
    }

    if (userRepository.existsByUsernameAndStatusIgnoreCase(userRegisterRequestDTO.username(), StatusEnum.ACTIVE)) {
      throw new AlreadyInUseException("Este nome de Usuário já esta em uso.");
    }

    if (userRegisterRequestDTO.password().isBlank() || userRegisterRequestDTO.password().length() < 6) {
      throw new IllegalArgumentException("A senha deve ter pelo menos 6 caracteres.");
    }

    User newUser = new User();
    newUser.setFirstName(userRegisterRequestDTO.firstName());
    newUser.setLastName(userRegisterRequestDTO.lastName());
    newUser.setEmail(userRegisterRequestDTO.email());
    newUser.setUsername(userRegisterRequestDTO.username());
    newUser.setPassword(passwordEncoder.encode(userRegisterRequestDTO.password()));
    newUser.setStatus(StatusEnum.ACTIVE);

    if (newUser.getRoles() == null) { // Se não for enviado roles no request, define como padrão USER
      if (!roleRepository.existsByNameLikeIgnoreCase("USER")) {
        roleService.createRole(new RoleDTO(null, "USER"));
      }

      RoleDTO role = roleService.findByName("USER");

      Set<Role> roles = new HashSet<>();
      roles.add(role.toEntity());
      newUser.setRoles(roles);
    }

    newUser = userRepository.save(newUser);

    String token = tokenService.generateKeyToken(newUser);
    return new AuthLoginResponseDTO(newUser.getUsername(), token, "Novo Usuário registrado com Sucesso");
  }

}
