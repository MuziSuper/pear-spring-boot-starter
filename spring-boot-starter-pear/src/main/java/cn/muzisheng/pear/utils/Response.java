package cn.muzisheng.pear.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
@Setter
@Getter
public class Response<T> extends ResponseEntity<Result<T>> implements Serializable {
    private int status=200;
    private Map<String, String> repHeaders;
    private String error;
    private T data;
    public Response() {
        super(HttpStatusCode.valueOf(200));
    }
    public Response<T> value() {
        MultiValueMap<String, String> reqHeaders = new HttpHeaders();
        Result<T> result= new Result<>(this.error, this.data);
        if(this.repHeaders !=null) {
            for (String key : this.repHeaders.keySet()) {
                reqHeaders.add(key, this.repHeaders.get(key));
            }
        }
        return new Response<>(result,reqHeaders, this.status);
    }
    public void putHeader(String key, String value){
        if(this.repHeaders ==null){
            this.repHeaders =new HashMap<>();
        }
        this.repHeaders.put(key,value);
    }
    public void removeHeader(String key){
        if(this.repHeaders !=null){
            this.repHeaders.remove(key);
        }
    }
    public void clearHeader(){
        this.repHeaders =null;
    }

    public String getHeader(String key){
        return this.repHeaders.get(key);
    }

    public Map<String, String> getAllHeader() {
        return this.repHeaders;
    }

    public Response(Result<T> result, MultiValueMap<String, String> repHeaders, int rawStatus) {
        super(result, repHeaders, rawStatus);
    }

}
