package apap.ti._5.Insurance_2306226864_be.restdto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponseDTO<T> {
    private Boolean success;
    private Integer status;
    private String message;
    private LocalDateTime timestamp;
    private T data;

    public static <T> BaseResponseDTO<T> success(String message, T data) {
        return new BaseResponseDTO<>(true, 200, message, LocalDateTime.now(), data);
    }

    public static <T> BaseResponseDTO<T> created(String message, T data) {
        return new BaseResponseDTO<>(true, 201, message, LocalDateTime.now(), data);
    }

    public static <T> BaseResponseDTO<T> error(Integer status, String message) {
        return new BaseResponseDTO<>(false, status, message, LocalDateTime.now(), null);
    }

    public static <T> BaseResponseDTO<T> error(String message) {
        return new BaseResponseDTO<>(false, 500, message, LocalDateTime.now(), null);
    }
}