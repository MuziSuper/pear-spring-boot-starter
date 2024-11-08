package cn.muzisheng.pear.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;
@Setter
@Getter
public class Response<T> extends ResponseEntity<Result<T>> {
    private int status;
    private Map<String, String> headers;
    private String error;
    private T data;
    public Response() {
        super(HttpStatusCode.valueOf(200));
    }
    public Response<T> value() {
        MultiValueMap<String, String> reqHeaders = new HttpHeaders();
        Result<T> result= new Result<>(this.error, this.data);
        for (String key:this.headers.keySet()){
            reqHeaders.add(key,this.headers.get(key));
        }
        return new Response<>(result,reqHeaders, this.status);
    }
    public void putHeader(String key, String value){
        if(this.headers==null){
            this.headers=new HashMap<>();
        }
        this.headers.put(key,value);
    }
    public void removeHeader(String key){
        if(this.headers!=null){
            this.headers.remove(key);
        }
    }
    public void clearHeader(){
        this.headers=null;
    }

    public String getHeader(String key){
        return this.headers.get(key);
    }

    public Map<String, String> getAllHeader() {
        return this.headers;
    }

    public Response(Result<T> result, MultiValueMap<String, String> headers, int rawStatus) {
        super(result, headers, rawStatus);
    }

}
