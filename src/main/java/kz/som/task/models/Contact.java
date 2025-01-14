package kz.som.task.models;

import lombok.Builder;
import lombok.Data;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "contacts")
public class Contact {
    @Id
    private String id;
    private String name;
    private String phoneNumber;
    private String email;
    private String address;
}
