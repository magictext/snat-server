package config;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ServerConfig {

	private int port;

	private int maxClient;

	private String token;

}
