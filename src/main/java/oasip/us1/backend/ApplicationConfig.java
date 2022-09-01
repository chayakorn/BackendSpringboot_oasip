package oasip.us1.backend;

import oasip.us1.backend.utils.ListMapper;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    @Bean
    public ModelMapper modelMapper(){
        return  new ModelMapper();
    }
    @Bean
    public ListMapper listMapper(){return ListMapper.getInstance();}
}
