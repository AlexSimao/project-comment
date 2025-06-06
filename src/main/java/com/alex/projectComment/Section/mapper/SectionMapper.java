package com.alex.projectComment.Section.mapper;

import com.alex.projectComment.Section.dtos.SectionDTO;
import com.alex.projectComment.Section.entities.Section;
import com.alex.projectComment.User.mappers.UserMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface SectionMapper {
  SectionDTO toDTO(Section section);

  Section toEntity(SectionDTO sectionDTO);
}
