package top.mortise.utils.webutils;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import io.swagger.annotations.ApiOperation;
import org.springframework.lang.Nullable;
import springfox.documentation.RequestHandler;

import java.util.List;
import java.util.Map;
import java.util.Set;


public class Swagger_WhiteList_Filter implements Predicate<RequestHandler>{
    Map<String,Set<String>> controllerAndMethods;
    public Swagger_WhiteList_Filter(Map<String,Set<String>> controllerAndMethods){
        this.controllerAndMethods=controllerAndMethods;
    }
    @Override
    public boolean apply(@Nullable RequestHandler requestHandler) {
        if(requestHandler.findAnnotation(ApiOperation.class).isPresent()){
            String controller = requestHandler.getHandlerMethod().getBeanType().getName();
            if(controllerAndMethods.containsKey(controller)){
                if(controllerAndMethods.get(controller).contains(requestHandler.getName())){
                    return true;
                }
            }
        }
       /*     if(requestHandler.findAnnotation(ResponseBody.class).isPresent()|| requestHandler.findControllerAnnotation(ResponseBody.class).isPresent()){
                return true;
            }*/
        return false;
    }



}
