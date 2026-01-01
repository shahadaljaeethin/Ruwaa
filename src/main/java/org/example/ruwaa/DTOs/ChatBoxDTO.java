package org.example.ruwaa.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatBoxDTO
{
    private String sender;
    private String text;
    private LocalDateTime date;
}
