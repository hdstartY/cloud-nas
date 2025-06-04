package org.hdstart.cloud.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlogFile {

    private Integer memberId;

    private Integer isPublic;

    private String textContent;

    private List<MultipartFile> images;
}
