package config;

import lombok.*;

import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ConfigEntity {

    private String name;

    private String port;

    private String remotePort;

    private Map<String,Object> map;

}
