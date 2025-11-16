package com.java.school.online_video_training.exception;

import java.time.Instant;
import java.util.UUID;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private String errorCode;
    private int status;
    private T data;
    private Meta meta;
    private String path;
    private Instant timestamp;
    private String traceId;

    public ApiResponse() {
        this.timestamp = Instant.now();
    }

    public ApiResponse(boolean success, String message, String errorCode, int status, T data, Meta meta, String path, Instant timestamp, String traceId) {
        this.success = success;
        this.message = message;
        this.errorCode = errorCode;
        this.status = status;
        this.data = data;
        this.meta = meta;
        this.path = path;
        this.timestamp = timestamp != null ? timestamp : Instant.now();
        this.traceId = traceId != null ? traceId : UUID.randomUUID().toString();
    }

    public static <T> ApiResponse<T> success(T data, String message, Meta meta, String path) {
        return new ApiResponse<>(true, message, null, HttpStatus.OK.value(), data, meta, path, Instant.now(), UUID.randomUUID().toString());
    }

    public static <T> ApiResponse<T> error(String message, String errorCode, HttpStatus status, String path, String traceId) {
        return new ApiResponse<>(false, message, errorCode, status.value(), null, null, path, Instant.now(), traceId);
    }

    // Getters and setters omitted for brevity

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public Meta getMeta() { return meta; }
    public void setMeta(Meta meta) { this.meta = meta; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Meta {
        private Integer page;
        private Integer size;
        private Integer totalPages;
        private Long totalElements;
        private Object extra;

        public Meta() {}
        public Meta(Integer page, Integer size, Integer totalPages, Long totalElements, Object extra) {
            this.page = page;
            this.size = size;
            this.totalPages = totalPages;
            this.totalElements = totalElements;
            this.extra = extra;
        }
        // Getters and setters omitted for brevity
        public Integer getPage() { return page; }
        public void setPage(Integer page) { this.page = page; }
        public Integer getSize() { return size; }
        public void setSize(Integer size) { this.size = size; }
        public Integer getTotalPages() { return totalPages; }
        public void setTotalPages(Integer totalPages) { this.totalPages = totalPages; }
        public Long getTotalElements() { return totalElements; }
        public void setTotalElements(Long totalElements) { this.totalElements = totalElements; }
        public Object getExtra() { return extra; }
        public void setExtra(Object extra) { this.extra = extra; }
    }
}

