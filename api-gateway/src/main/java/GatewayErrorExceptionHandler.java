import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.webflux.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
@Order(-2)
public class GatewayErrorExceptionHandler implements ErrorWebExceptionHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        // Tránh xử lý nếu response đã được commit trước đó
        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        // 1. Xác định mã trạng thái HTTP dựa trên loại lỗi ngoại lệ
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String errorMessage = "Lỗi hệ thống Gateway nội bộ";

        // Lỗi 404 (Không tìm thấy route/path) hoặc các lỗi định sẵn từ WebFlux
        if (ex instanceof ResponseStatusException) {
            status = (HttpStatus) ((ResponseStatusException) ex).getStatusCode();
            errorMessage = ((ResponseStatusException) ex).getReason();
        }
        // Lỗi 503 khi Service đích bị sập (Không kết nối được, Timeout, v.v.)
        else if (ex.getClass().getName().contains("ConnectException") ||
                ex.getClass().getName().contains("TimeoutException")) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
            errorMessage = "Cổng Gateway không thể kết nối tới dịch vụ đích";
        }

        // 2. Thiết lập Header phản hồi dạng JSON và mã HTTP tương ứng
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // 3. Xây dựng cấu trúc dữ liệu lỗi ApiResponseError theo yêu cầu
        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        errorAttributes.put("status", status.value());
        errorAttributes.put("error", status.getReasonPhrase());
        errorAttributes.put("message", errorMessage);

        // 4. Chuyển đổi Map thành dạng chuỗi byte JSON và ghi vào luồng phản hồi
        return response.writeWith(Mono.fromSupplier(() -> {
            DataBufferFactory bufferFactory = response.bufferFactory();
            try {
                byte[] jsonBytes = objectMapper.writeValueAsBytes(errorAttributes);
                return bufferFactory.wrap(jsonBytes);
            } catch (JsonProcessingException e) {
                return bufferFactory.wrap("{\"error\":\"Internal Server Error\"}".getBytes());
            }
        }));
    }
}
