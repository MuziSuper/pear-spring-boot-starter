package cn.muzisheng.pear.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Response<T> implements Serializable {
    @Getter
    @Setter
    private int status=200;
    @Getter
    private Result<T> body;
    private MultiValueMap<String, String> headers;

    public ResponseEntity<Result<T>> value() {
        return new ResponseEntity<>(this.body,this.headers, this.status);
    }
    public void putHeader(String key, String value){
        if(this.headers ==null){
            this.headers = new HttpHeaders();
        }
        this.headers.add(key, value);
    }
    public void putHeaders(Map<String, String> headers){
        if(this.headers ==null){
            this.headers = new HttpHeaders();
        }
        for (String key : headers.keySet()) {
            this.headers.add(key, headers.get(key));
        }
    }
    public void removeHeader(String key){
        if(this.headers !=null){
            this.headers.remove(key);
        }
    }
    public void clearHeader(){
        this.headers =null;
    }

    public List<String> getHeader(String key){
        return this.headers.get(key);
    }

    public HashMap<String, Object> getHeaders() {
        HashMap<String, Object> repHeaders=new HashMap<>();
        for (String key : this.headers.keySet()) {
            repHeaders.put(key, this.headers.get(key));
        }
        return repHeaders;
    }
    public void setData(T data) {
        if(this.body==null){
            this.body=new Result<>();
        }
        this.body.setData(data);
    }
    public void setError(String error) {
        if(this.body==null){
            this.body=new Result<>();
        }
        this.body.setError(error);
    }
    public void setBody(String error, T data) {
        if(this.body==null){
            this.body=new Result<>();
        }
        this.body.setData(data);
        this.body.setError(error);
    }
}
