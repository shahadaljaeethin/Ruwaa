package org.example.ruwaa.DTOs;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import org.example.ruwaa.Model.Attachments;

import java.util.List;

@Data
@AllArgsConstructor
public class WorkPostDTO {

    @NotEmpty(message = "enter content")
    @Size(min=8,message = "minimum characters is 8")
    private String content;

    @NotNull
    private Boolean isPublic;

    private Attachments attachments;

    @NotEmpty(message = "enter category")
    private String category;

}
