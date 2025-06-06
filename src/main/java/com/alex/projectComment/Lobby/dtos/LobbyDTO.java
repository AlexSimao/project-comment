package com.alex.projectComment.Lobby.dtos;

import com.alex.projectComment.Lobby.entities.Domain;
import com.alex.projectComment.Lobby.entities.Tag;
import com.alex.projectComment.User.dtos.UserDTO;
import com.alex.projectComment.enums.StatusEnum;
import com.alex.projectComment.enums.VisibilityEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class LobbyDTO {
  private Long id;
  private String name;
  private LocalDateTime creationDate;
  private List<Tag> tags;
  private List<Domain> domains;

  private VisibilityEnum visibility;
  private StatusEnum status;

  private UserDTO creator;
  private UserDTO userPrime;

  private List<UserDTO> usersAdmin;

}
