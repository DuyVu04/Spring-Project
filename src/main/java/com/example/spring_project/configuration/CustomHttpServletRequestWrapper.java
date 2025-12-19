package com.example.spring_project.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.*;

public class CustomHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private final Map<String, String> customHeaders;

    /**
     * @param request The request to wrap
     * @param authHeader Chuỗi "Bearer <token>"
     */
    public CustomHttpServletRequestWrapper(HttpServletRequest request, String authHeader) {
        super(request);
        this.customHeaders = new HashMap<>();
        // Lưu header Authorization vào map tùy chỉnh
        this.customHeaders.put("Authorization", authHeader);
    }

    // --- CÁC PHƯƠNG THỨC GHI ĐÈ ---

    @Override
    public String getHeader(String name) {
        String headerValue = customHeaders.get(name);
        // Nếu header có trong map tùy chỉnh, trả về giá trị tùy chỉnh
        if (headerValue != null) return headerValue;
        // Nếu không, trả về giá trị gốc
        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        // Sử dụng Set để tránh trùng lặp khi thêm tên header mới
        Set<String> names = new HashSet<>(customHeaders.keySet());

        // Thêm tất cả tên header gốc
        Enumeration<String> originalHeaders = super.getHeaderNames();
        while (originalHeaders.hasMoreElements()) {
            names.add(originalHeaders.nextElement());
        }
        return Collections.enumeration(names);
    }

    //  QUAN TRỌNG: Cần ghi đè getHeaders(String name) để Spring Security đọc đúng danh sách giá trị.
    @Override
    public Enumeration<String> getHeaders(String name) {
        if (customHeaders.containsKey(name)) {
            // Nếu là header tùy chỉnh, trả về Enumeration chỉ chứa giá trị mới
            return Collections.enumeration(Collections.singletonList(customHeaders.get(name)));
        }
        // Nếu không, trả về giá trị gốc
        return super.getHeaders(name);
    }
}