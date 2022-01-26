package com.pacheco.app.ecommerce.infrastructure.email;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailObject {
    private String to;
    private String title;
    private String content;
    private String contentType;
}
