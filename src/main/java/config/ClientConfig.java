package config;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ClientConfig{

    private String serverAddress;

    private int port;

    private String Token;

    private List<ConfigEntity> list;
}
