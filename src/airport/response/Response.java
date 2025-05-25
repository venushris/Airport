package airport.response;


public class Response<T> {
    private final StatusCode status;
    private final String message;
    private final T data;

    private Response(StatusCode status, String message, T data) {
        this.status  = status;
        this.message = message;
        this.data    = data;
    }

    
    public static <T> Response<T> of(StatusCode status, String message, T data) {
        return new Response<>(status, message, data);
    }

   
    public static <T> Response<T> of(StatusCode status, String message) {
        return new Response<>(status, message, null);
    }

    public StatusCode getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
    public boolean isSuccess() {
        int code = status.getCode();
        return code >= 200 && code < 300;
    }
}
