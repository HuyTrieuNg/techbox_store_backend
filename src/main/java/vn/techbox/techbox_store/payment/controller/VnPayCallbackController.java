package vn.techbox.techbox_store.payment.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.techbox.techbox_store.payment.service.VnPayCallbackService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payments/vnpay")
@RequiredArgsConstructor
@Slf4j
public class VnPayCallbackController {

    private final VnPayCallbackService vnPayCallbackService;

    @RequestMapping(value = "/ipn", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<Map<String, String>> ipn(HttpServletRequest request) {
        Map<String, String> params = extractParams(request);
        var result = vnPayCallbackService.handleIpn(params);
        return ResponseEntity.ok(result);
    }

    private Map<String, String> extractParams(HttpServletRequest request) {
        Map<String, String[]> map = request.getParameterMap();
        Map<String, String> params = new HashMap<>();
        for (Map.Entry<String, String[]> e : map.entrySet()) {
            String name = e.getKey();
            String value = (e.getValue() != null && e.getValue().length > 0) ? e.getValue()[0] : null;
            if (value != null) {
                params.put(name, URLDecoder.decode(value, StandardCharsets.UTF_8));
            }
        }
        return params;
    }
}
