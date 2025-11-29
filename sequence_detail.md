# Luồng Hoạt Động Hệ Thống Backend: Quản Lý Đơn Hàng

Tài liệu này mô tả chi tiết luồng xử lý đơn hàng trong hệ thống backend, từ khi khách hàng đặt hàng đến khi đơn hàng hoàn thành hoặc bị hủy.

## 1. Giai đoạn: Đặt hàng (Order Placement)

**Actor:** Khách hàng (Customer)
**API:** `POST /orders`
**Module chính:** `vn.techbox.techbox_store.order`

### Luồng xử lý:
1.  **Tiếp nhận yêu cầu (`OrderController`)**:
    *   Nhận payload `CreateOrderRequest` từ client.
    *   Lấy ID người dùng từ `UserPrincipal`.

2.  **Validate dữ liệu (`OrderValidationService`)**:
    *   Kiểm tra tính hợp lệ của request (địa chỉ, sản phẩm, số lượng).

3.  **Lưu thông tin giao hàng (`OrderShippingInfoRepository`)**:
    *   Tạo và lưu bản ghi `OrderShippingInfo`.

4.  **Khởi tạo thanh toán (`PaymentServiceFactory`)**:
    *   Dựa vào `paymentMethod` (COD hoặc VNPAY), factory trả về service tương ứng.
    *   Khởi tạo entity `Payment` (Trạng thái: `PENDING`).

5.  **Xử lý sản phẩm & Tính toán (`OrderCalculationService`)**:
    *   Duyệt danh sách sản phẩm đặt mua.
    *   Lấy thông tin từ `ProductVariationRepository`.
    *   Tính toán tổng tiền, áp dụng giảm giá (nếu có).

6.  **Lưu đơn hàng (`OrderRepository`)**:
    *   Lưu `Order` với trạng thái `PENDING`.

### Rẽ nhánh xử lý giữ chỗ (Reservation):

*   **Case 1: Thanh toán VNPAY (`PaymentMethod.VNPAY`)**
    *   **Module:** `vn.techbox.techbox_store.inventory`, `vn.techbox.techbox_store.voucher`
    *   **Hành động:**
        *   Gọi `InventoryReservationService.reserveInventory`: Tạo bản ghi giữ chỗ tồn kho (`InventoryReservation`) với thời hạn **15 phút**.
        *   Gọi `VoucherReservationService.reserveVoucherByCode` (nếu có voucher): Giữ chỗ voucher với thời hạn **15 phút**.
    *   **Mục đích:** Đảm bảo hàng hóa/voucher không bị người khác mua mất trong lúc khách đang thanh toán.

*   **Case 2: Thanh toán khi nhận hàng (`PaymentMethod.COD`)**
    *   **Hành động:** Không thực hiện giữ chỗ (Reservation) tại bước này.
    *   **Lý do:** Đơn hàng COD cần Admin xác nhận trước khi cam kết tồn kho.

### Kết quả giai đoạn 1:
*   Đơn hàng được tạo (Status: `PENDING`).
*   Trả về `OrderResponse`. Nếu là VNPAY, kèm theo `paymentUrl`.

---

## 2. Giai đoạn: Thanh toán (Payment - Chỉ áp dụng VNPAY)

**Actor:** Khách hàng thực hiện thanh toán trên cổng VNPAY -> VNPAY gọi Callback (IPN) về hệ thống.
**API:** `GET/POST /payments/vnpay/ipn`
**Module chính:** `vn.techbox.techbox_store.payment`

### Luồng xử lý (`VnPayCallbackService`):
1.  **Xác thực chữ ký (Signature)**: Kiểm tra tính toàn vẹn dữ liệu từ VNPAY.

2.  **Rẽ nhánh kết quả thanh toán:**

    *   **Case A: Thanh toán Thành công (ResponseCode = "00")**
        *   **Cập nhật tồn kho (`InventoryReservationService`)**:
            *   Gọi `setReservationsExpiryNull`: Chuyển các bản ghi giữ chỗ tạm thời (15p) thành giữ chỗ vĩnh viễn (cho đến khi đơn được xử lý tiếp).
        *   **Cập nhật Voucher (`VoucherReservationService`)**:
            *   Gọi `confirmReservations`: Xác nhận sử dụng voucher, tạo bản ghi `UserVoucher`, trừ số lượng voucher thực tế.
        *   **Cập nhật trạng thái**:
            *   Payment Status -> `PAID`.
            *   Order Status -> `CONFIRMED`.

    *   **Case B: Thanh toán Thất bại / Hủy**
        *   **Giải phóng tồn kho (`InventoryReservationService`)**:
            *   Gọi `releaseReservations`: Xóa/Hủy các bản ghi giữ chỗ, hoàn lại số lượng "đang giữ" cho sản phẩm.
        *   **Giải phóng Voucher (`VoucherReservationService`)**:
            *   Gọi `releaseReservations`: Hoàn lại voucher.
        *   **Cập nhật trạng thái**:
            *   Payment Status -> `FAILED`.
            *   Order Status vẫn là `PENDING` (hoặc có thể chuyển sang trạng thái chờ xử lý lại tùy logic, hiện tại code giữ PENDING nhưng payment FAILED).

---

## 3. Giai đoạn: Xác nhận đơn hàng (Order Confirmation - Chủ yếu cho COD)

**Actor:** Admin/Staff
**API:** `PUT /orders/{orderId}/status`
**Module chính:** `vn.techbox.techbox_store.order`, `vn.techbox.techbox_store.inventory`

### Luồng xử lý (`OrderServiceImpl.updateOrderStatus`):
Khi Admin chuyển trạng thái đơn hàng sang `CONFIRMED`:

*   **Case COD (PaymentMethod.COD)**:
    *   **Hành động**:
        *   Gọi `InventoryReservationService.reserveInventoryPermanent`: Tạo bản ghi giữ chỗ tồn kho **vĩnh viễn** (không có expiry).
        *   Gọi `VoucherReservationService.reserveVoucherByCode`: Giữ chỗ voucher.
    *   **Ý nghĩa**: Admin đã xác nhận đơn là hợp lệ, hệ thống chính thức giữ hàng cho đơn này.

*   **Case VNPAY**:
    *   Thường đã được chuyển sang `CONFIRMED` tự động sau khi thanh toán thành công. Nếu Admin thao tác thủ công, hệ thống đảm bảo expiry của reservation được set về null.

---

## 4. Giai đoạn: Xử lý đơn hàng (Order Processing)

**Actor:** Admin/Staff (Kho)
**API:** `PUT /orders/{orderId}/status` (Chuyển sang `PROCESSING`)
**Module chính:** `vn.techbox.techbox_store.inventory`

### Luồng xử lý:
Khi trạng thái đơn hàng chuyển sang `PROCESSING` (Đang đóng gói/xuất kho):

1.  **Trừ kho thực tế (`InventoryReservationService.confirmReservations`)**:
    *   Chuyển trạng thái Reservation từ `RESERVED` sang `CONFIRMED` (hoặc tương đương).
    *   **Trừ số lượng tồn kho thực tế (`stockQuantity`)** của sản phẩm.
    *   Giảm số lượng `reservedQuantity`.

2.  **Tạo phiếu xuất kho (`StockExportService`)**:
    *   Tự động tạo bản ghi `StockExport` lưu lịch sử xuất kho cho đơn hàng này.

3.  **Xử lý Voucher (Nếu chưa xử lý)**:
    *   Gọi `VoucherReservationService.confirmReservations` để chốt sử dụng voucher (nếu bước thanh toán VNPAY chưa làm).

---

## 5. Giai đoạn: Giao hàng & Hoàn tất

**Actor:** Admin/Staff/Shipper
**API:** `PUT /orders/{orderId}/status`

### Luồng xử lý:
1.  **Giao hàng (`DELIVERING`)**: Cập nhật trạng thái để theo dõi.
2.  **Hoàn tất (`DELIVERED`)**:
    *   Cập nhật `ActualDeliveryDate` trong `OrderShippingInfo`.
    *   **Nếu là COD**:
        *   Tự động cập nhật Payment Status -> `PAID`.
        *   Cập nhật `PaymentCompletedAt`.

---

## 6. Giai đoạn: Hủy đơn hàng (Cancellation)

### Case 1: Khách hàng tự hủy (`OrderController.cancelOrder`)
*   **Điều kiện**: Đơn hàng đang ở trạng thái `PENDING`.
*   **Xử lý**:
    *   Nếu là VNPAY (đã giữ chỗ): Gọi `InventoryReservationService.releaseReservations` và `VoucherReservationService.releaseReservations` để trả lại hàng/voucher.
    *   Cập nhật Order Status -> `CANCELLED`.

### Case 2: Hệ thống hủy tự động (Auto-Cancel)
*   **Trigger**: Scheduled Task (`InventoryReservationService.cleanUpExpiredReservations`).
*   **Điều kiện**:
    *   Đơn hàng VNPAY quá hạn thanh toán (15 phút).
    *   Reservation hết hạn (`expiresAt` < `now`).
*   **Xử lý**:
    *   Chuyển trạng thái Reservation sang `EXPIRED`.
    *   Hoàn lại `reservedQuantity` cho sản phẩm.
    *   Kiểm tra nếu đơn hàng VNPAY chưa thanh toán (`PaymentStatus != PAID`) và không còn reservation nào active -> Cập nhật Order Status -> `CANCELLED`.

---

## Tóm tắt trạng thái đơn hàng (Order Status Flow)

1.  **PENDING**: Đơn mới tạo. (VNPAY: Đã giữ chỗ 15p; COD: Chưa giữ chỗ).
2.  **CONFIRMED**:
    *   VNPAY: Thanh toán thành công (Giữ chỗ vĩnh viễn).
    *   COD: Admin xác nhận (Bắt đầu giữ chỗ vĩnh viễn).
3.  **PROCESSING**: Kho đang xử lý. **(Trừ kho thực tế, tạo phiếu xuất)**.
4.  **DELIVERING**: Đang giao hàng.
5.  **DELIVERED**: Giao thành công. (COD: Đánh dấu đã trả tiền).
6.  **CANCELLED**: Đơn bị hủy, hoàn trả tài nguyên giữ chỗ.
